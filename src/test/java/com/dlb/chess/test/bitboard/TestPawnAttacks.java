package com.dlb.chess.test.bitboard;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Set;

import org.junit.jupiter.api.Test;

import com.dlb.chess.bitboard.BitboardPosition;
import com.dlb.chess.bitboard.BitboardPositionUtility;
import com.dlb.chess.bitboard.PawnAttacks;
import com.dlb.chess.bitboard.StaticPositionBridge;
import com.dlb.chess.board.StaticPosition;
import com.dlb.chess.board.enums.Side;
import com.dlb.chess.board.enums.Square;
import com.dlb.chess.common.Nulls;
import com.dlb.chess.squares.PawnDiagonalSquares;
import com.dlb.chess.test.model.PgnFen;
import com.dlb.chess.test.model.PgnTestCaseList;
import com.dlb.chess.test.pgn.setup.PgnTestCaseCatalog;
import com.dlb.chess.test.pgntest.enums.PgnTest;

/**
 * Differential test for {@link PawnAttacks}: per side, the precomputed bitboard table must agree with the existing
 * {@link PawnDiagonalSquares}-backed reference for every legal pawn from-square (ranks 2-7). The bitboard table is
 * geometric across all 64 squares — including ranks 1 and 8, where {@code PawnDiagonalSquares} returns an empty set
 * by its "pawns only legally exist on ranks 2-7" convention. That intentional divergence enables the reverse-attack
 * identity used by {@link com.dlb.chess.bitboard.BitboardPosition#attackersTo} for targets on the back ranks.
 */
class TestPawnAttacks {

  @SuppressWarnings("static-method")
  @Test
  void directAgainstReferenceWhite() {
    for (final Square fromSquare : Square.REAL) {
      if (!isLegalPawnFromSquare(fromSquare)) {
        continue;
      }
      final Set<Square> bitboardAttacks = BitboardPositionUtility
          .toSquareSet(PawnAttacks.attacks(fromSquare, Side.WHITE));
      final Set<Square> referenceAttacks = PawnDiagonalSquares.getPawnDiagonalSquares(Side.WHITE, fromSquare);
      assertEquals(referenceAttacks, bitboardAttacks, "white pawn attacks from " + fromSquare.getName());
    }
  }

  @SuppressWarnings("static-method")
  @Test
  void directAgainstReferenceBlack() {
    for (final Square fromSquare : Square.REAL) {
      if (!isLegalPawnFromSquare(fromSquare)) {
        continue;
      }
      final Set<Square> bitboardAttacks = BitboardPositionUtility
          .toSquareSet(PawnAttacks.attacks(fromSquare, Side.BLACK));
      final Set<Square> referenceAttacks = PawnDiagonalSquares.getPawnDiagonalSquares(Side.BLACK, fromSquare);
      assertEquals(referenceAttacks, bitboardAttacks, "black pawn attacks from " + fromSquare.getName());
    }
  }

  private static boolean isLegalPawnFromSquare(Square square) {
    final int rank0Indexed = square.ordinal() / 8;
    return rank0Indexed >= 1 && rank0Indexed <= 6;
  }

  @SuppressWarnings("static-method")
  @Test
  void corpusEveryPawnAgrees() {
    for (final PgnTest pgnTest : PgnTest.values()) {
      final PgnTestCaseList testCaseList = PgnTestCaseCatalog.getTestList(pgnTest);
      for (final PgnFen testCase : testCaseList.list()) {
        final StaticPosition staticPosition = StaticPositionBridge.toStaticPosition(testCase.finalPosition().getBitboardPosition());
        final BitboardPosition bitboardPosition = StaticPositionBridge.fromStaticPosition(staticPosition);
        assertSideAgrees(bitboardPosition.whitePawns(), Side.WHITE, testCase);
        assertSideAgrees(bitboardPosition.blackPawns(), Side.BLACK, testCase);
      }
    }
  }

  private static void assertSideAgrees(long pawns, Side side, PgnFen testCase) {
    long remaining = pawns;
    while (remaining != 0L) {
      final Square fromSquare = Nulls.get(Square.REAL, Long.numberOfTrailingZeros(remaining));
      final Set<Square> bitboardAttacks = BitboardPositionUtility.toSquareSet(PawnAttacks.attacks(fromSquare, side));
      final Set<Square> referenceAttacks = PawnDiagonalSquares.getPawnDiagonalSquares(side, fromSquare);
      assertEquals(referenceAttacks, bitboardAttacks,
          side + " pawn attacks from " + fromSquare.getName() + " in fixture " + testCase.pgnName());
      remaining &= remaining - 1L;
    }
  }

  @SuppressWarnings("static-method")
  @Test
  void noneSquareThrows() {
    assertThrows(IllegalArgumentException.class, () -> PawnAttacks.attacks(Square.NONE, Side.WHITE));
    assertThrows(IllegalArgumentException.class, () -> PawnAttacks.attacks(Square.NONE, Side.BLACK));
  }

  @SuppressWarnings("static-method")
  @Test
  void noneSideThrows() {
    assertThrows(IllegalArgumentException.class, () -> PawnAttacks.attacks(Square.A1, Side.NONE));
  }
}
