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
import java.util.function.Supplier;

import org.eclipse.jdt.annotation.NonNull;

import io.github.dlbbld.ashlarchess.board.Board;
import io.github.dlbbld.ashlarchess.board.enums.Side;
import io.github.dlbbld.ashlarchess.common.model.MoveSpecification;
import io.github.dlbbld.ashlarchess.model.PgnMove;
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
public class UnwinnableFullProbe {

  private static final int GAMES_PER_GROUP = 2;
  private static final long TIMEOUT_MS = 180_000L;

  private static final PgnTest[] GROUPS = { PgnTest.WCC2021, PgnTest.CHA_LICHESS_QUICK_DEPTH_ABOVE_FOUR,
      PgnTest.RANDOM_NO_REPETITION, PgnTest.MAX_MOVES };

  private static final ExecutorService WORKER = Executors.newSingleThreadExecutor(r -> {
    final Thread t = new Thread(r, "full-analyzer");
    t.setDaemon(true);
    return t;
  });

  public static void main(String[] args) {
    for (final PgnTest pgnTest : GROUPS) {
      @SuppressWarnings("null") final @NonNull PgnTest pgnTestNotNull = pgnTest;
      final List<Board> finals = finalPositions(pgnTestNotNull, GAMES_PER_GROUP);
      for (int g = 0; g < finals.size(); g++) {
        final Board board = finals.get(g);
        final String fen = board.getFen();
        log(pgnTest.name() + " game#" + (g + 1) + "  plies=" + board.getPerformedMoveCount() + "  fen=" + fen);
        timeCall("unwinnableFull(WHITE)", fen, () -> board.unwinnableFull(Side.WHITE).toString());
        timeCall("unwinnableFull(BLACK)", fen, () -> board.unwinnableFull(Side.BLACK).toString());
        timeCall("deadPositionFull", fen, () -> board.deadPositionFull().toString());
      }
    }
    log("ALL CALLS RETURNED -- no hang; unwinnableFull/deadPositionFull are bounded on these positions.");
    WORKER.shutdownNow();
  }

  private static void timeCall(String label, String fen, Supplier<String> call) {
    log("  -> " + label + " ...");
    final long start = System.nanoTime();
    final Future<String> future = WORKER.submit(call::get);
    try {
      final String result = future.get(TIMEOUT_MS, TimeUnit.MILLISECONDS);
      final double ms = (System.nanoTime() - start) / 1_000_000.0;
      log(String.format("     %s = %s   (%.1f ms)", label, result, ms));
    } catch (final TimeoutException e) {
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
        final MoveSpecification spec = board.moveStrict(move.san());
        if (spec == null) {
          throw new IllegalStateException("null spec");
        }
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
