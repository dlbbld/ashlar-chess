// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.test.performance;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.eclipse.jdt.annotation.Nullable;

import io.github.dlbbld.ashlarchess.board.Board;
import io.github.dlbbld.ashlarchess.common.Nulls;
import io.github.dlbbld.ashlarchess.fen.FenConstants;
import io.github.dlbbld.ashlarchess.pgn.PgnMove;
import io.github.dlbbld.ashlarchess.pgn.PgnGame;
import io.github.dlbbld.ashlarchess.report.Reporter;
import io.github.dlbbld.ashlarchess.test.model.PgnFen;
import io.github.dlbbld.ashlarchess.test.model.PgnTestCaseList;
import io.github.dlbbld.ashlarchess.test.pgn.parser.PgnCacheForStrictPgnParserTestCases;
import io.github.dlbbld.ashlarchess.test.pgn.setup.PgnTestCaseCatalog;
import io.github.dlbbld.ashlarchess.test.pgntest.enums.PgnTest;

/**
 * Scaling survey for the report layer: times {@link Reporter#report(Board)} on the longest game of corpora spanning a
 * range of lengths (normal ~100 plies up to the synthetic MAX_MOVES games of ~17,700) and prints us/ply. The report
 * builders replay the game and probe claims at boundaries; if the cost per ply grows with game length, the whole report
 * is O(n^2). Each report runs under a hard timeout so a pathological build is reported, not left to hang. The report
 * layer does not invoke the CHA / unwinnability search, so this is bounded work. Manually run diagnostic (a
 * {@code main}), like the other surveys in this package.
 */
@SuppressWarnings("null") // Manual survey; JDT cannot model unannotated JDK/JUnit/concurrency APIs cleanly.
public class ReportScalingSurvey {

  private static final int WARMUP_ROUNDS = 1;
  private static final int MEASURE_ROUNDS = 3;
  private static final long TIMEOUT_MS = 120_000L;

  private static final PgnTest[] GROUPS = { PgnTest.WCC2021, PgnTest.RANDOM_NO_REPETITION, PgnTest.MAX_MOVES };

  private static final ExecutorService WORKER = newWorker();

  private static long sink;

  public static void main(String[] args) {
    System.out.println("Reporter.report(board) scaling across game lengths.");
    System.out.printf("%-26s %12s %14s %14s%n", "corpus", "plies", "us/ply", "ms/report");

    final Map<String, Double> usPerPlyByGroup = new LinkedHashMap<>();
    for (final PgnTest pgnTest : GROUPS) {
      final Board board = longestInitialStartGameBoard(pgnTest);
      final int plies = board.getPerformedMoveCount();
      final String name = Nulls.name(pgnTest);

      final double[] result = measure(board, plies);
      if (result[0] < 0) {
        System.out.printf("%-26s %,12d   DID NOT RETURN within %ds (pathological)%n", name, plies, TIMEOUT_MS / 1000);
      } else {
        System.out.printf("%-26s %,12d %,14.3f %,14.1f%n", name, plies, result[0], result[1]);
        usPerPlyByGroup.put(name, result[0]);
      }
    }

    final Double shortUs = usPerPlyByGroup.get(PgnTest.WCC2021.name());
    final Double longUs = usPerPlyByGroup.get(PgnTest.MAX_MOVES.name());
    if (shortUs != null && longUs != null && shortUs.doubleValue() > 0) {
      System.out.printf(
          "%ncross-game ratio %s / %s us/ply = %.1f  (noisy: different games differ in claim density, not just length)%n",
          PgnTest.MAX_MOVES.name(), PgnTest.WCC2021.name(), longUs.doubleValue() / shortUs.doubleValue());
    }

    prefixScalingProbe();

    System.out.printf("%n(sink=%d)%n", sink);
    WORKER.shutdownNow();
  }

  /**
   * Definitive scaling test: one game (MAX_MOVES) reported at growing prefix lengths. Same game = constant content per
   * ply, so the only variable is length. Doubling the plies should ~double the time for an O(n) report build; a ~4x
   * jump per doubling is O(n^2).
   */
  private static void prefixScalingProbe() {
    System.out.println();
    System.out.println("---- same-game (MAX_MOVES) prefix scaling: doubling plies should ~2x the time if O(n) ----");
    final List<String> sans = longestSans(PgnTest.MAX_MOVES);
    reportTimedNanos(replayPrefix(sans, 500)); // warm up the JIT
    System.out.printf("%12s %14s %14s %10s%n", "plies", "ms/report", "us/ply", "vs prev");
    double previousMs = -1.0;
    for (final int length : new int[] { 1000, 2000, 4000, 8000, 16000 }) {
      if (length > sans.size()) {
        break;
      }
      final long nanos = reportTimedNanos(replayPrefix(sans, length));
      if (nanos < 0) {
        System.out.printf("%,12d   timeout%n", length);
        break;
      }
      final double ms = nanos / 1_000_000.0;
      final String ratio = previousMs < 0 ? "-" : String.format("%.2fx", ms / previousMs);
      System.out.printf("%,12d %,14.1f %,14.3f %10s%n", length, ms, nanos / (double) length / 1000.0, ratio);
      previousMs = ms;
    }
  }

  /** Returns {us/ply, ms/report}, or {-1,-1} if any report call exceeds the timeout. */
  private static double[] measure(Board board, int plies) {
    for (int w = 0; w < WARMUP_ROUNDS; w++) {
      if (reportTimedNanos(board) < 0) {
        return new double[] { -1, -1 };
      }
    }
    long total = 0L;
    for (int r = 0; r < MEASURE_ROUNDS; r++) {
      final long nanos = reportTimedNanos(board);
      if (nanos < 0) {
        return new double[] { -1, -1 };
      }
      total += nanos;
    }
    final double averageNanos = total / (double) MEASURE_ROUNDS;
    return new double[] { averageNanos / plies / 1000.0, averageNanos / 1_000_000.0 };
  }

  private static long reportTimedNanos(Board board) {
    final long start = System.nanoTime();
    final Future<Integer> future = submit(board);
    try {
      sink += get(future).intValue();
      return System.nanoTime() - start;
    } catch (@SuppressWarnings("unused") final TimeoutException e) {
      future.cancel(true);
      return -1L;
    } catch (final Exception e) {
      System.out.println("  report threw " + e.getClass().getSimpleName() + ": " + e.getMessage());
      return -1L;
    }
  }

  private static Future<Integer> submit(Board board) {
    return WORKER.submit(() -> Integer.valueOf(Reporter.report(board).size()));
  }

  private static Integer get(Future<Integer> future) throws Exception {
    return future.get(TIMEOUT_MS, TimeUnit.MILLISECONDS);
  }

  private static Board longestInitialStartGameBoard(PgnTest pgnTest) {
    return replayPrefix(longestSans(pgnTest), Integer.MAX_VALUE);
  }

  private static List<String> longestSans(PgnTest pgnTest) {
    @Nullable PgnGame longest = null;
    final PgnTestCaseList testCaseList = PgnTestCaseCatalog.getTestList(pgnTest);
    for (final PgnFen testCase : testCaseList.list()) {
      final PgnGame game = PgnCacheForStrictPgnParserTestCases.getPgn(pgnTest.getFolderPath(), testCase.pgnName());
      if (!game.startFen().equals(FenConstants.FEN_INITIAL)) {
        continue;
      }
      if (longest == null || game.moves().size() > longest.moves().size()) {
        longest = game;
      }
    }
    if (longest == null) {
      throw new IllegalStateException("no initial-start game in " + pgnTest);
    }
    final List<String> sans = new ArrayList<>();
    for (final PgnMove move : longest.moves()) {
      sans.add(move.san());
    }
    return sans;
  }

  // These corpora games start from the initial position (filtered in longestSans), so a default Board is the start.
  private static Board replayPrefix(List<String> sans, int length) {
    final Board board = new Board();
    final int count = Math.min(length, sans.size());
    for (int i = 0; i < count; i++) {
      board.moveStrict(Nulls.get(sans, i));
    }
    return board;
  }

  private static ExecutorService newWorker() {
    return Executors.newSingleThreadExecutor(r -> {
      final Thread t = new Thread(r, "report-scaling");
      t.setDaemon(true);
      return t;
    });
  }
}
