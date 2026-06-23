// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.test.performance;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import io.github.dlbbld.ashlarchess.board.Board;
import io.github.dlbbld.ashlarchess.board.enums.Side;
import io.github.dlbbld.ashlarchess.internal.Nulls;
import io.github.dlbbld.ashlarchess.pgn.PgnMove;
import io.github.dlbbld.ashlarchess.pgn.PgnGame;
import io.github.dlbbld.ashlarchess.test.model.PgnFen;
import io.github.dlbbld.ashlarchess.test.model.PgnTestCaseList;
import io.github.dlbbld.ashlarchess.test.pgn.parser.PgnCacheForStrictPgnParserTestCases;
import io.github.dlbbld.ashlarchess.test.pgn.setup.PgnTestCaseCatalog;
import io.github.dlbbld.ashlarchess.test.pgntest.enums.PgnTest;

/**
 * Diagnostic: does {@link Board#unwinnableFull(Side)} / {@link Board#deadPositionFull()} actually TERMINATE on the
 * sample positions the burn-in hit, or can it hang? Replays the first games of each corpus to their final position and
 * calls each full analyzer with a hard per-call timeout, on a worker thread, streaming each result as it lands. A call
 * that does not return within the timeout is reported with its FEN and ends the probe - that would be a genuine
 * non-termination (release blocker), as opposed to merely slow-but-bounded.
 */
@SuppressWarnings("null") // Manual survey; JDT cannot model unannotated JDK/JUnit/concurrency APIs cleanly.
public class UnwinnableFullProbe {

  private static final int GAMES_PER_GROUP = 2;
  private static final long TIMEOUT_MS = 180_000L;

  private static final PgnTest[] GROUPS = { PgnTest.WCC2021, PgnTest.CHA_LICHESS_QUICK_DEPTH_ABOVE_FOUR,
      PgnTest.RANDOM_NO_REPETITION, PgnTest.MAX_MOVES };

  private static final ExecutorService WORKER = newWorker();

  @FunctionalInterface
  private interface FullAnalyzerCall {
    String run();
  }

  private static ExecutorService newWorker() {
    return Executors.newSingleThreadExecutor(r -> {
      final Thread t = new Thread(r, "full-analyzer");
      t.setDaemon(true);
      return t;
    });
  }

  private static Future<String> submit(FullAnalyzerCall call) {
    return WORKER.submit(call::run);
  }

  private static String get(Future<String> future) throws Exception {
    return future.get(TIMEOUT_MS, TimeUnit.MILLISECONDS);
  }

  public static void main(String[] args) {
    for (final PgnTest pgnTest : GROUPS) {
      final List<Board> finals = finalPositions(pgnTest, GAMES_PER_GROUP);
      for (int g = 0; g < finals.size(); g++) {
        final Board board = Nulls.get(finals, g);
        final String fen = board.getFen();
        log(Nulls.name(pgnTest) + " game#" + (g + 1) + "  plies=" + board.getPerformedMoveCount() + "  fen=" + fen);
        timeCall("unwinnableFull(WHITE)", fen, () -> Nulls.format("%s", board.unwinnableFull(Side.WHITE)));
        timeCall("unwinnableFull(BLACK)", fen, () -> Nulls.format("%s", board.unwinnableFull(Side.BLACK)));
        timeCall("deadPositionFull", fen, () -> Nulls.format("%s", board.deadPositionFull()));
      }
    }
    log("ALL CALLS RETURNED -- no hang; unwinnableFull/deadPositionFull are bounded on these positions.");
    WORKER.shutdownNow();
  }

  private static void timeCall(String label, String fen, FullAnalyzerCall call) {
    log("  -> " + label + " ...");
    final long start = System.nanoTime();
    final Future<String> future = submit(call);
    try {
      final String result = get(future);
      final double ms = (System.nanoTime() - start) / 1_000_000.0;
      log(Nulls.format("     %s = %s   (%.1f ms)", label, result, ms));
    } catch (@SuppressWarnings("unused") final TimeoutException e) {
      log("     !!! " + label + " DID NOT RETURN within " + (TIMEOUT_MS / 1000)
          + "s -- non-terminating / pathological");
      log("     !!! position: " + fen);
      future.cancel(true);
      WORKER.shutdownNow();
      System.out.flush();
      System.exit(2);
    } catch (final Exception e) {
      log("     " + label + " threw " + e.getClass().getSimpleName() + ": " + e.getMessage());
    }
  }

  private static List<Board> finalPositions(PgnTest pgnTest, int maxGames) {
    final List<Board> result = new ArrayList<>();
    final PgnTestCaseList testCaseList = PgnTestCaseCatalog.getTestList(pgnTest);
    for (final PgnFen testCase : testCaseList.list()) {
      if (result.size() >= maxGames) {
        break;
      }
      final PgnGame pgnGame = PgnCacheForStrictPgnParserTestCases.getPgn(pgnTest.getFolderPath(), testCase.pgnName());
      final Board board = new Board(pgnGame.startFen());
      for (final PgnMove move : pgnGame.moves()) {
        board.moveStrict(move.san());
      }
      result.add(board);
    }
    return result;
  }

  private static void log(String message) {
    System.out.println(message);
    System.out.flush();
  }
}
