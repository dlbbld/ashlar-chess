package com.dlb.chess.test.bitboard;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Set;

import org.junit.jupiter.api.Test;

import com.dlb.chess.bitboard.BitboardPosition;
import com.dlb.chess.bitboard.BitboardPositionUtility;
import com.dlb.chess.bitboard.KnightAttacks;
import com.dlb.chess.board.StaticPosition;
import com.dlb.chess.board.enums.Square;
import com.dlb.chess.common.Nulls;
import com.dlb.chess.squares.KnightEmptyBoardSquares;
import com.dlb.chess.test.model.PgnTestCase;
import com.dlb.chess.test.model.PgnTestCaseList;
import com.dlb.chess.test.pgn.setup.PgnTestCaseCatalog;
import com.dlb.chess.test.pgntest.enums.PgnTest;

/**
 * Differential test for {@link KnightAttacks}: the precomputed bitboard table must agree, for every from-square, with
 * the existing {@link KnightEmptyBoardSquares}-backed reference. The geometric pattern is position-independent, so the
 * direct per-square test is exhaustive; the corpus walk additionally exercises the harness shape that every later
 * step will reuse.
 */
class TestKnightAttacks {

  @SuppressWarnings("static-method")
  @Test
  void directAgainstReference() {
    for (final Square fromSquare : Square.REAL) {
      final Set<Square> bitboardAttacks = BitboardPositionUtility.toSquareSet(KnightAttacks.attacks(fromSquare));
      final Set<Square> referenceAttacks = KnightEmptyBoardSquares.getKnightSquares(fromSquare);
      assertEquals(referenceAttacks, bitboardAttacks, "knight attacks from " + fromSquare.getName());
    }
  }

  @SuppressWarnings("static-method")
  @Test
  void corpusEveryKnightAgrees() {
    for (final PgnTest pgnTest : PgnTest.values()) {
      final PgnTestCaseList testCaseList = PgnTestCaseCatalog.getTestList(pgnTest);
      for (final PgnTestCase testCase : testCaseList.list()) {
        final StaticPosition staticPosition = BitboardPositionUtility.toStaticPosition(testCase.finalPosition().getBitboardPosition());
        final BitboardPosition bitboardPosition = BitboardPositionUtility.fromStaticPosition(staticPosition);
        long knights = bitboardPosition.whiteKnights() | bitboardPosition.blackKnights();
        while (knights != 0L) {
          final Square fromSquare = Nulls.get(Square.REAL, Long.numberOfTrailingZeros(knights));
          final Set<Square> bitboardAttacks = BitboardPositionUtility.toSquareSet(KnightAttacks.attacks(fromSquare));
          final Set<Square> referenceAttacks = KnightEmptyBoardSquares.getKnightSquares(fromSquare);
          assertEquals(referenceAttacks, bitboardAttacks,
              "knight attacks from " + fromSquare.getName() + " in fixture " + testCase.pgnName());
          knights &= knights - 1L;
        }
      }
    }
  }

  @SuppressWarnings("static-method")
  @Test
  void noneSquareThrows() {
    assertThrows(IllegalArgumentException.class, () -> KnightAttacks.attacks(Square.NONE));
  }
}
