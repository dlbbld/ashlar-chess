package com.dlb.chess.test.bitboard;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import com.dlb.chess.bitboard.BitboardPosition;
import com.dlb.chess.bitboard.BitboardPositionUtility;
import com.dlb.chess.board.StaticPosition;
import com.dlb.chess.board.enums.Side;
import com.dlb.chess.common.utility.StaticPositionUtility;
import com.dlb.chess.test.model.PgnTestCase;
import com.dlb.chess.test.model.PgnTestCaseList;
import com.dlb.chess.test.pgn.setup.PgnTestCaseCatalog;
import com.dlb.chess.test.pgntest.enums.PgnTest;

/**
 * Differential test for {@link BitboardPosition#isInCheck(Side)}: per fixture and per side, the bitboard check
 * detection must agree with {@code StaticPositionUtility.calculateIsCheck} — which is exactly what the production
 * {@code Board.isCheck()} reads. Corpus positions always have both kings, so the reference's "no king" precondition
 * is satisfied throughout the corpus walk.
 */
class TestBitboardPositionIsInCheck {

  @SuppressWarnings("static-method")
  @Test
  void corpusAgreesPerSide() {
    for (final PgnTest pgnTest : PgnTest.values()) {
      final PgnTestCaseList testCaseList = PgnTestCaseCatalog.getTestList(pgnTest);
      for (final PgnTestCase testCase : testCaseList.list()) {
        final StaticPosition staticPosition = testCase.finalPosition().getStaticPosition();
        final BitboardPosition bitboardPosition = BitboardPositionUtility.fromStaticPosition(staticPosition);

        final boolean bbWhite = bitboardPosition.isInCheck(Side.WHITE);
        final boolean refWhite = StaticPositionUtility.calculateIsCheck(staticPosition, Side.WHITE);
        assertEquals(refWhite, bbWhite, "white isInCheck in fixture " + testCase.pgnName());

        final boolean bbBlack = bitboardPosition.isInCheck(Side.BLACK);
        final boolean refBlack = StaticPositionUtility.calculateIsCheck(staticPosition, Side.BLACK);
        assertEquals(refBlack, bbBlack, "black isInCheck in fixture " + testCase.pgnName());
      }
    }
  }

  @SuppressWarnings("static-method")
  @Test
  void initialPositionNotInCheck() {
    assertFalse(BitboardPosition.INITIAL_POSITION.isInCheck(Side.WHITE));
    assertFalse(BitboardPosition.INITIAL_POSITION.isInCheck(Side.BLACK));
    // Same answer from the reference.
    assertFalse(StaticPositionUtility.calculateIsCheck(StaticPosition.INITIAL_POSITION, Side.WHITE));
    assertFalse(StaticPositionUtility.calculateIsCheck(StaticPosition.INITIAL_POSITION, Side.BLACK));
  }

  @SuppressWarnings("static-method")
  @Test
  void emptyPositionNotInCheck() {
    // No king of either side: bitboard returns false. The reference precondition (king must exist) does not hold for
    // EMPTY_POSITION, so the bitboard's defensive answer stands alone here.
    assertFalse(BitboardPosition.EMPTY_POSITION.isInCheck(Side.WHITE));
    assertFalse(BitboardPosition.EMPTY_POSITION.isInCheck(Side.BLACK));
  }

  @SuppressWarnings("static-method")
  @Test
  void noneSideThrows() {
    assertThrows(IllegalArgumentException.class, () -> BitboardPosition.INITIAL_POSITION.isInCheck(Side.NONE));
  }
}
