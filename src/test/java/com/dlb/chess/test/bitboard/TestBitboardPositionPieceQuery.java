package com.dlb.chess.test.bitboard;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.dlb.chess.bitboard.BitboardPosition;
import com.dlb.chess.bitboard.BitboardPositionUtility;
import com.dlb.chess.board.StaticPosition;
import com.dlb.chess.board.enums.Piece;
import com.dlb.chess.board.enums.Square;
import com.dlb.chess.test.model.PgnTestCase;
import com.dlb.chess.test.model.PgnTestCaseList;
import com.dlb.chess.test.pgn.setup.PgnTestCaseCatalog;
import com.dlb.chess.test.pgntest.enums.PgnTest;
import com.dlb.chess.bitboard.StaticPositionBridge;

/**
 * Differential test for {@link BitboardPosition#get(Square)} and {@link BitboardPosition#isEmpty(Square)}: for every
 * fixture in the corpus and every one of the 64 real squares, the bitboard and the reference {@link StaticPosition}
 * must agree on which piece (if any) sits there.
 */
class TestBitboardPositionPieceQuery {

  @SuppressWarnings("static-method")
  @Test
  void initialPosition() {
    final BitboardPosition bitboardPosition = BitboardPosition.INITIAL_POSITION;
    final StaticPosition staticPosition = StaticPosition.INITIAL_POSITION;
    for (final Square square : Square.REAL) {
      assertEquals(staticPosition.get(square), bitboardPosition.get(square), "get(" + square.getName() + ")");
      assertEquals(staticPosition.isEmpty(square), bitboardPosition.isEmpty(square),
          "isEmpty(" + square.getName() + ")");
    }
  }

  @SuppressWarnings("static-method")
  @Test
  void emptyPosition() {
    final BitboardPosition bitboardPosition = BitboardPosition.EMPTY_POSITION;
    for (final Square square : Square.REAL) {
      assertEquals(Piece.NONE, bitboardPosition.get(square), "get(" + square.getName() + ")");
      assertTrue(bitboardPosition.isEmpty(square), "isEmpty(" + square.getName() + ")");
    }
  }

  @SuppressWarnings("static-method")
  @Test
  void noneSquareThrows() {
    final BitboardPosition bitboardPosition = BitboardPosition.INITIAL_POSITION;
    assertThrows(IllegalArgumentException.class, () -> bitboardPosition.get(Square.NONE));
    assertThrows(IllegalArgumentException.class, () -> bitboardPosition.isEmpty(Square.NONE));
  }

  @SuppressWarnings("static-method")
  @Test
  void fullCorpus() {
    for (final PgnTest pgnTest : PgnTest.values()) {
      final PgnTestCaseList testCaseList = PgnTestCaseCatalog.getTestList(pgnTest);
      for (final PgnTestCase testCase : testCaseList.list()) {
        final StaticPosition staticPosition = StaticPositionBridge.toStaticPosition(testCase.finalPosition().getBitboardPosition());
        final BitboardPosition bitboardPosition = StaticPositionBridge.fromStaticPosition(staticPosition);
        for (final Square square : Square.REAL) {
          assertEquals(staticPosition.get(square), bitboardPosition.get(square),
              "get(" + square.getName() + ") mismatch in fixture " + testCase.pgnName());
          assertEquals(staticPosition.isEmpty(square), bitboardPosition.isEmpty(square),
              "isEmpty(" + square.getName() + ") mismatch in fixture " + testCase.pgnName());
        }
      }
    }
  }
}
