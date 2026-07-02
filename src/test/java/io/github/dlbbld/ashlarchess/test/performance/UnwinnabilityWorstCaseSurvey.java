// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.test.performance;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import io.github.dlbbld.ashlarchess.board.Board;
import io.github.dlbbld.ashlarchess.board.enums.Side;
import io.github.dlbbld.ashlarchess.internal.Nulls;
import io.github.dlbbld.ashlarchess.test.model.PgnFen;
import io.github.dlbbld.ashlarchess.test.model.PgnTestCaseList;
import io.github.dlbbld.ashlarchess.test.pgn.setup.PgnTestCaseCatalog;
import io.github.dlbbld.ashlarchess.test.pgntest.enums.PgnTest;

/**
 * Worst-case stress for the flagship CHA feature: runs {@code unwinnableFull(WHITE)}, {@code unwinnableFull(BLACK)} and
 * {@code deadPositionFull()} over a broad endgame sample - the whole restricted corpus's final positions PLUS every CHA
 * category (the deliberately-hard unwinnability fixtures) - each under a hard timeout on a worker thread. A call that
 * does not return within the timeout is a release blocker (non-terminating / pathological) and ends the run with the
 * offending FEN. Otherwise it reports the slowest calls so the worst-case tail is characterised, not just assumed.
 *
 * <p>
 * The bounded {@code unwinnableQuick} / {@code deadPositionQuick} are not the concern here; this targets the unbounded
 * full CHA search. Manually run diagnostic (a {@code main}), like the other surveys in this package.
 */
@SuppressWarnings("null") // Manual survey; JDT cannot model unannotated JDK/JUnit/concurrency APIs cleanly.
public class UnwinnabilityWorstCaseSurvey {

  private static final int MAX_POSITIONS = 800;
  private static final long TIMEOUT_MS = 90_000L;
  private static final int TOP_SLOWEST = 20;

  private static final List<PgnTest> CHA_GROUPS = Nulls.listOf(PgnTest.CHA_LICHESS_QUICK_DEPTH_THREE,
      PgnTest.CHA_LICHESS_QUICK_DEPTH_FOUR, PgnTest.CHA_LICHESS_QUICK_DEPTH_ABOVE_FOUR,
      PgnTest.CHA_LICHESS_QUICK_DEPTH_ABOVE_FOUR_WINNABLE_FOR_FLAGGING_WITH_HELPMATE, PgnTest.CHA_CHASOLVER_EXCEPTIONS,
      PgnTest.CHA_PAWN_WALL_YES, PgnTest.CHA_PAWN_WALL_NO, PgnTest.CHA_SHALLOW_TERMINATION,
      PgnTest.CHA_HELPMATE_BEYOND_FIVEFOLD, PgnTest.CHA_HELPMATE_BEYOND_SEVENTY_FIVE, PgnTest.CHA_BASIC_MATE_DRAW,
      PgnTest.CHA_BASIC_MATE_HELPMATE_04, PgnTest.CHA_BASIC_MATE_HELPMATE_10,
      PgnTest.CHA_BASIC_MATE_HELPMATE_AROUND_MAX, PgnTest.CHA_BASIC_HELPMATE_EXISTENCE_THEOREM);

  private static final ExecutorService WORKER = newWorker();

  private static long sink;

  @FunctionalInterface
  private interface FullAnalyzerCall {
    String run();
  }

  private record Timing(String label, long milliseconds, String fen) {
  }

  public static void main(String[] args) {
    final List<Board> positions = collectPositions();
    log("sweeping " + positions.size() + " endgame positions x 3 full analyzers, per-call timeout " + TIMEOUT_MS / 1000
        + "s");

    final List<Timing> timings = new ArrayList<>();
    for (final Board board : positions) {
      final String fen = board.getFen();
      record(timings, fen, "unwinnableFull(WHITE)", () -> Nulls.format("%s", board.unwinnableFull(Side.WHITE)));
      record(timings, fen, "unwinnableFull(BLACK)", () -> Nulls.format("%s", board.unwinnableFull(Side.BLACK)));
      record(timings, fen, "deadPositionFull", () -> Nulls.format("%s", board.deadPositionFull()));
    }

    log("DONE: every call returned within " + TIMEOUT_MS / 1000 + "s (no non-termination).");
    log("slowest " + TOP_SLOWEST + " calls:");
    timings.stream().sorted(Comparator.comparingLong(Timing::milliseconds).reversed()).limit(TOP_SLOWEST)
        .forEach(t -> log(String.format("  %,7d ms  %-22s %s", t.milliseconds(), t.label(), t.fen())));
    log("(sink=" + sink + ")");
    WORKER.shutdownNow();
  }

  private static void record(List<Timing> timings, String fen, String label, FullAnalyzerCall call) {
    final long start = System.nanoTime();
    final Future<String> future = submit(call);
    try {
      sink += get(future).length();
      timings.add(new Timing(label, (System.nanoTime() - start) / 1_000_000L, fen));
    } catch (@SuppressWarnings("unused") final TimeoutException e) {
      future.cancel(true);
      log("!!! " + label + " DID NOT RETURN within " + TIMEOUT_MS / 1000 + "s -- non-terminating / pathological");
      log("!!! position: " + fen);
      WORKER.shutdownNow();
      System.out.flush();
      System.exit(2);
    } catch (final Exception e) {
      log("  " + label + " threw " + e.getClass().getSimpleName() + ": " + e.getMessage() + " on " + fen);
    }
  }

  private static List<Board> collectPositions() {
    final List<PgnTestCaseList> sources = new ArrayList<>(PgnTestCaseCatalog.getRestrictedTestCaseLists());

    for (final PgnTest pgnTest : CHA_GROUPS) {
      sources.add(PgnTestCaseCatalog.getTestList(pgnTest));
    }

    final Set<String> seenFens = new HashSet<>();
    final List<Board> positions = new ArrayList<>();
    for (final PgnTestCaseList testCaseList : sources) {
      for (final PgnFen testCase : testCaseList.list()) {
        if (positions.size() >= MAX_POSITIONS) {
          return positions;
        }
        if (seenFens.add(testCase.finalFen())) {
          positions.add(testCase.finalPosition());
        }
      }
    }
    return positions;
  }

  private static Future<String> submit(FullAnalyzerCall call) {
    return WORKER.submit(call::run);
  }

  private static String get(Future<String> future) throws Exception {
    return future.get(TIMEOUT_MS, TimeUnit.MILLISECONDS);
  }

  private static ExecutorService newWorker() {
    return Executors.newSingleThreadExecutor(r -> {
      final Thread t = new Thread(r, "cha-worst-case");
      t.setDaemon(true);
      return t;
    });
  }

  private static void log(String message) {
    System.out.println(message);
    System.out.flush();
  }
}
