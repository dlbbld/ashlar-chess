package io.github.dlbbld.ashlarchess.test.bitboard;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import io.github.dlbbld.ashlarchess.bitboard.BitboardPosition;
import io.github.dlbbld.ashlarchess.bitboard.StaticPositionBridge;
import io.github.dlbbld.ashlarchess.board.StaticPosition;
import io.github.dlbbld.ashlarchess.board.enums.Side;
import io.github.dlbbld.ashlarchess.common.utility.StaticPositionUtility;
import io.github.dlbbld.ashlarchess.test.model.PgnFen;
import io.github.dlbbld.ashlarchess.test.model.PgnTestCaseList;
import io.github.dlbbld.ashlarchess.test.pgn.setup.PgnTestCaseCatalog;
import io.github.dlbbld.ashlarchess.test.pgntest.enums.PgnTest;

/**
 * Differential test for {@link BitboardPosition#isInCheck(Side)}: per fixture and per side, the bitboard check
 * detection must agree with {@code StaticPositionUtility.calculateIsCheck} - which is exactly what the production
 * {@code Board.isCheck()} reads. Corpus positions always have both kings, so the reference's "no king" precondition is
 * satisfied throughout the corpus walk.
 */
class TestBitboardPositionIsInCheck {

  @SuppressWarnings("static-method")
  @Test
  void corpusAgreesPerSide() {
    for (final PgnTest pgnTest : PgnTest.values()) {
      final PgnTestCaseList testCaseList = PgnTestCaseCatalog.getTestList(pgnTest);
      for (final PgnFen testCase : testCaseList.list()) {
        final StaticPosition staticPosition = StaticPositionBridge
            .toStaticPosition(testCase.finalPosition().getBitboardPosition());
        final BitboardPosition bitboardPosition = StaticPositionBridge.fromStaticPosition(staticPosition);

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
