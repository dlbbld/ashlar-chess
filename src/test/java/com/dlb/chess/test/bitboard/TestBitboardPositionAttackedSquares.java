package com.dlb.chess.test.bitboard;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Set;

import org.junit.jupiter.api.Test;

import com.dlb.chess.bitboard.BitboardPosition;
import com.dlb.chess.bitboard.BitboardPositionUtility;
import com.dlb.chess.board.StaticPosition;
import com.dlb.chess.board.enums.Side;
import com.dlb.chess.board.enums.Square;
import com.dlb.chess.squares.AbstractAttackedSquares;
import com.dlb.chess.test.model.PgnTestCase;
import com.dlb.chess.test.model.PgnTestCaseList;
import com.dlb.chess.test.pgn.setup.PgnTestCaseCatalog;
import com.dlb.chess.test.pgntest.enums.PgnTest;
import com.dlb.chess.bitboard.StaticPositionBridge;

/**
 * Differential test for {@link BitboardPosition#attackedSquares(Side)}: for every fixture and every side, the
 * bitboard's union of all piece attacks must agree, square-for-square, with
 * {@link AbstractAttackedSquares#calculateAttackedSquares}. This is the first composed bitboard primitive — exercises
 * the whole Phase 2 + Phase 3 stack together.
 */
class TestBitboardPositionAttackedSquares {

  @SuppressWarnings("static-method")
  @Test
  void corpusAgreesPerSide() {
    for (final PgnTest pgnTest : PgnTest.values()) {
      final PgnTestCaseList testCaseList = PgnTestCaseCatalog.getTestList(pgnTest);
      for (final PgnTestCase testCase : testCaseList.list()) {
        final StaticPosition staticPosition = StaticPositionBridge.toStaticPosition(testCase.finalPosition().getBitboardPosition());
        final BitboardPosition bitboardPosition = StaticPositionBridge.fromStaticPosition(staticPosition);
        assertSideAgrees(staticPosition, bitboardPosition, Side.WHITE, testCase);
        assertSideAgrees(staticPosition, bitboardPosition, Side.BLACK, testCase);
      }
    }
  }

  private static void assertSideAgrees(StaticPosition staticPosition, BitboardPosition bitboardPosition, Side side,
      PgnTestCase testCase) {
    final Set<Square> bitboardAttacks = BitboardPositionUtility.toSquareSet(bitboardPosition.attackedSquares(side));
    final Set<Square> referenceAttacks = AbstractAttackedSquares.calculateAttackedSquares(staticPosition, side);
    assertEquals(referenceAttacks, bitboardAttacks,
        side + " attackedSquares in fixture " + testCase.pgnName());
  }

  @SuppressWarnings("static-method")
  @Test
  void initialPositionMatchesReference() {
    final Set<Square> bbWhite = BitboardPositionUtility
        .toSquareSet(BitboardPosition.INITIAL_POSITION.attackedSquares(Side.WHITE));
    final Set<Square> refWhite = AbstractAttackedSquares.calculateAttackedSquares(StaticPosition.INITIAL_POSITION,
        Side.WHITE);
    assertEquals(refWhite, bbWhite, "white attackedSquares on initial position");

    final Set<Square> bbBlack = BitboardPositionUtility
        .toSquareSet(BitboardPosition.INITIAL_POSITION.attackedSquares(Side.BLACK));
    final Set<Square> refBlack = AbstractAttackedSquares.calculateAttackedSquares(StaticPosition.INITIAL_POSITION,
        Side.BLACK);
    assertEquals(refBlack, bbBlack, "black attackedSquares on initial position");
  }

  @SuppressWarnings("static-method")
  @Test
  void emptyPositionHasNoAttacks() {
    assertEquals(0L, BitboardPosition.EMPTY_POSITION.attackedSquares(Side.WHITE));
    assertEquals(0L, BitboardPosition.EMPTY_POSITION.attackedSquares(Side.BLACK));
  }

  @SuppressWarnings("static-method")
  @Test
  void noneSideThrows() {
    assertThrows(IllegalArgumentException.class, () -> BitboardPosition.INITIAL_POSITION.attackedSquares(Side.NONE));
  }
}
