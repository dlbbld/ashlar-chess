package com.dlb.chess.test.bitboard;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Set;

import org.junit.jupiter.api.Test;

import com.dlb.chess.bitboard.BitboardPosition;
import com.dlb.chess.bitboard.BitboardPositionUtility;
import com.dlb.chess.bitboard.KingAttacks;
import com.dlb.chess.board.StaticPosition;
import com.dlb.chess.board.enums.Square;
import com.dlb.chess.common.Nulls;
import com.dlb.chess.squares.KingNonCastlingEmptyBoardSquares;
import com.dlb.chess.test.model.PgnTestCase;
import com.dlb.chess.test.model.PgnTestCaseList;
import com.dlb.chess.test.pgn.setup.PgnTestCaseCatalog;
import com.dlb.chess.test.pgntest.enums.PgnTest;

/**
 * Differential test for {@link KingAttacks}: the precomputed bitboard table must agree, for every from-square, with
 * the existing {@link KingNonCastlingEmptyBoardSquares}-backed reference. Geometric, position-independent. Castling
 * targets are intentionally out of scope at this layer and not tested here.
 */
class TestKingAttacks {

  @SuppressWarnings("static-method")
  @Test
  void directAgainstReference() {
    for (final Square fromSquare : Square.REAL) {
      final Set<Square> bitboardAttacks = BitboardPositionUtility.toSquareSet(KingAttacks.attacks(fromSquare));
      final Set<Square> referenceAttacks = KingNonCastlingEmptyBoardSquares.getKingSquares(fromSquare);
      assertEquals(referenceAttacks, bitboardAttacks, "king attacks from " + fromSquare.getName());
    }
  }

  @SuppressWarnings("static-method")
  @Test
  void corpusEveryKingAgrees() {
    for (final PgnTest pgnTest : PgnTest.values()) {
      final PgnTestCaseList testCaseList = PgnTestCaseCatalog.getTestList(pgnTest);
      for (final PgnTestCase testCase : testCaseList.list()) {
        final StaticPosition staticPosition = testCase.finalPosition().getStaticPosition();
        final BitboardPosition bitboardPosition = BitboardPositionUtility.fromStaticPosition(staticPosition);
        long kings = bitboardPosition.whiteKings() | bitboardPosition.blackKings();
        while (kings != 0L) {
          final Square fromSquare = Nulls.get(Square.REAL, Long.numberOfTrailingZeros(kings));
          final Set<Square> bitboardAttacks = BitboardPositionUtility.toSquareSet(KingAttacks.attacks(fromSquare));
          final Set<Square> referenceAttacks = KingNonCastlingEmptyBoardSquares.getKingSquares(fromSquare);
          assertEquals(referenceAttacks, bitboardAttacks,
              "king attacks from " + fromSquare.getName() + " in fixture " + testCase.pgnName());
          kings &= kings - 1L;
        }
      }
    }
  }

  @SuppressWarnings("static-method")
  @Test
  void noneSquareThrows() {
    assertThrows(IllegalArgumentException.class, () -> KingAttacks.attacks(Square.NONE));
  }
}
