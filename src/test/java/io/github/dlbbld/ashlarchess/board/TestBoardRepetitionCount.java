// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.board;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.List;

import org.junit.jupiter.api.Test;

import io.github.dlbbld.ashlarchess.common.model.DynamicPosition;
import io.github.dlbbld.ashlarchess.model.PgnMove;
import io.github.dlbbld.ashlarchess.pgn.PgnGame;
import io.github.dlbbld.ashlarchess.test.model.PgnFen;
import io.github.dlbbld.ashlarchess.test.model.PgnTestCaseList;
import io.github.dlbbld.ashlarchess.test.pgn.parser.PgnCacheForStrictPgnParserTestCases;
import io.github.dlbbld.ashlarchess.test.pgn.setup.PgnTestCaseCatalog;

/**
 * Proves the incremental repetition-count index on {@link Board} against an independent slow oracle that scans the full
 * history. Central invariant: for the current position, the map-backed {@link Board#getRepetitionCount()} equals the
 * number of occurrences of the current {@link DynamicPosition} in the current history prefix. Production reads the map;
 * this test reads only the oracle. (The {@code board} package placement is required to reach the package-private
 * {@link Board#getDynamicPositions()} the oracle scans.)
 */
class TestBoardRepetitionCount {

  private static final List<String> KNIGHT_SHUFFLE_CYCLE = List.of("Nf3", "Nf6", "Ng1", "Ng8");

  /** Test-only oracle: occurrences of the current dynamic position across the whole history prefix; never the map. */
  private static int historyScanRepetitionCount(Board board) {
    final DynamicPosition current = board.getDynamicPosition();
    int count = 0;
    for (final DynamicPosition position : board.getDynamicPositions()) {
      if (position.equals(current)) {
        count++;
      }
    }
    return count;
  }

  @SuppressWarnings("static-method")
  @Test
  void initialBoardCountIsOneAndMatchesOracle() {
    final Board board = new Board();
    assertEquals(1, board.getRepetitionCount());
    assertEquals(historyScanRepetitionCount(board), board.getRepetitionCount());
  }

  @SuppressWarnings("static-method")
  @Test
  void mapMatchesOracleAfterEveryMoveInRepresentativeGames() {
    int pgnsExercised = 0;
    for (final PgnTestCaseList testCaseList : PgnTestCaseCatalog.getParserIntegrationSmokeTests()) {
      for (final PgnFen testCase : testCaseList.list()) {
        final PgnGame pgnGame = PgnCacheForStrictPgnParserTestCases.getPgn(testCaseList.pgnTest().getFolderPath(),
            testCase.pgnName());
        final Board board = new Board(pgnGame.startFen());
        assertCountMatchesOracle(board, testCase.pgnName(), 0);
        int moveIndex = 0;
        for (final PgnMove move : pgnGame.moves()) {
          moveIndex++;
          board.moveStrict(move.san());
          assertCountMatchesOracle(board, testCase.pgnName(), moveIndex);
        }
        pgnsExercised++;
      }
    }
    if (pgnsExercised == 0) {
      fail("No representative PGNs were exercised - corpus mis-configured");
    }
  }

  private static void assertCountMatchesOracle(Board board, String pgnName, int moveIndex) {
    final int oracle = historyScanRepetitionCount(board);
    assertEquals(oracle, board.getRepetitionCount(),
        () -> "repetition count vs oracle in " + pgnName + " after move " + moveIndex);
    assertEquals(oracle >= 3, board.isThreefoldRepetition(),
        () -> "isThreefoldRepetition vs oracle in " + pgnName + " after move " + moveIndex);
    assertEquals(oracle >= 5, board.isFivefoldRepetition(),
        () -> "isFivefoldRepetition vs oracle in " + pgnName + " after move " + moveIndex);
  }

  @SuppressWarnings("static-method")
  @Test
  void knightShuffleReachesThreefoldThenFivefold() {
    final Board board = new Board();

    playCycleAndCheckOracle(board); // 1 cycle: the initial position has now occurred twice
    assertEquals(2, board.getRepetitionCount());
    assertFalse(board.isThreefoldRepetition());

    playCycleAndCheckOracle(board); // 2 cycles: three occurrences -> threefold
    assertEquals(3, board.getRepetitionCount());
    assertTrue(board.isThreefoldRepetition());
    assertFalse(board.isFivefoldRepetition());

    playCycleAndCheckOracle(board); // 3 cycles
    assertEquals(4, board.getRepetitionCount());

    playCycleAndCheckOracle(board); // 4 cycles: five occurrences -> fivefold
    assertEquals(5, board.getRepetitionCount());
    assertTrue(board.isFivefoldRepetition());
  }

  @SuppressWarnings("static-method")
  @Test
  void unmoveKeepsCountAlignedWithOracleAndRoundTrips() {
    final Board board = new Board();
    final DynamicPosition initialDynamicPosition = board.getDynamicPosition();

    final int cycles = 4;
    for (int c = 0; c < cycles; c++) {
      playCycle(board);
    }
    assertEquals(16, board.getPerformedMoveCount());
    assertEquals(5, board.getRepetitionCount());

    // Unmove step by step: after each unmove the map-backed count must still equal the history-scan oracle.
    while (board.getPerformedMoveCount() > 0) {
      board.unmove();
      assertEquals(historyScanRepetitionCount(board), board.getRepetitionCount());
    }

    // Full round-trip: the board is back to the initial state, with no stale repetition state.
    assertEquals(0, board.getPerformedMoveCount());
    assertEquals(1, board.getRepetitionCount());
    assertEquals(initialDynamicPosition, board.getDynamicPosition());

    // No stale map entries (observable through behavior): replaying the same sequence yields the same counts; a map
    // that retained entries from the first pass would over-count here.
    for (int c = 0; c < cycles; c++) {
      playCycleAndCheckOracle(board);
    }
    assertEquals(5, board.getRepetitionCount());
  }

  private static void playCycle(Board board) {
    for (final String san : KNIGHT_SHUFFLE_CYCLE) {
      board.moveStrict(san);
    }
  }

  private static void playCycleAndCheckOracle(Board board) {
    for (final String san : KNIGHT_SHUFFLE_CYCLE) {
      board.moveStrict(san);
      assertEquals(historyScanRepetitionCount(board), board.getRepetitionCount());
    }
  }
}
