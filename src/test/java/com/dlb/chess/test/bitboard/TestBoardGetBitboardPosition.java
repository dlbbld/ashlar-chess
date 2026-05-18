package com.dlb.chess.test.bitboard;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.dlb.chess.bitboard.BitboardPosition;
import com.dlb.chess.bitboard.BitboardPositionUtility;
import com.dlb.chess.board.Board;
import com.dlb.chess.test.model.PgnTestCase;
import com.dlb.chess.test.model.PgnTestCaseList;
import com.dlb.chess.test.pgn.setup.PgnTestCaseCatalog;
import com.dlb.chess.test.pgntest.enums.PgnTest;

/**
 * Step 1.1 of the switchover release: {@link Board#getBitboardPosition()} must, for every fixture, equal a fresh
 * {@code BitboardPositionUtility.fromStaticPosition(board.getStaticPosition())}. Pure consistency check on the new
 * Board accessor — no production behaviour has changed at this step.
 */
class TestBoardGetBitboardPosition {

  @SuppressWarnings("static-method")
  @Test
  void initialPositionMatchesBitboardConstant() {
    final Board board = new Board(false);
    assertEquals(BitboardPosition.INITIAL_POSITION, board.getBitboardPosition());
  }

  @SuppressWarnings("static-method")
  @Test
  void corpusFinalPositionsMatchFromStaticPosition() {
    for (final PgnTest pgnTest : PgnTest.values()) {
      final PgnTestCaseList testCaseList = PgnTestCaseCatalog.getTestList(pgnTest);
      for (final PgnTestCase testCase : testCaseList.list()) {
        final Board board = testCase.finalPosition();
        final BitboardPosition expected = BitboardPositionUtility.fromStaticPosition(board.getStaticPosition());
        assertEquals(expected, board.getBitboardPosition(),
            "Board.getBitboardPosition disagrees with fromStaticPosition in fixture " + testCase.pgnName());
      }
    }
  }
}
