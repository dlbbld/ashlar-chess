package io.github.dlbbld.ashlarchess.test.bitboard;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Set;

import org.junit.jupiter.api.Test;

import io.github.dlbbld.ashlarchess.bitboard.BitboardPosition;
import io.github.dlbbld.ashlarchess.bitboard.BitboardPositionUtility;
import io.github.dlbbld.ashlarchess.bitboard.KnightAttacks;
import io.github.dlbbld.ashlarchess.bitboard.StaticPositionBridge;
import io.github.dlbbld.ashlarchess.board.StaticPosition;
import io.github.dlbbld.ashlarchess.board.enums.Square;
import io.github.dlbbld.ashlarchess.common.Nulls;
import io.github.dlbbld.ashlarchess.squares.KnightEmptyBoardSquares;
import io.github.dlbbld.ashlarchess.test.model.PgnFen;
import io.github.dlbbld.ashlarchess.test.model.PgnTestCaseList;
import io.github.dlbbld.ashlarchess.test.pgn.setup.PgnTestCaseCatalog;
import io.github.dlbbld.ashlarchess.test.pgntest.enums.PgnTest;

/**
 * Differential test for {@link KnightAttacks}: the precomputed bitboard table must agree, for every from-square, with
 * the existing {@link KnightEmptyBoardSquares}-backed reference. The geometric pattern is position-independent, so the
 * direct per-square test is exhaustive; the corpus walk additionally exercises the harness shape that every later step
 * will reuse.
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
      for (final PgnFen testCase : testCaseList.list()) {
        final StaticPosition staticPosition = StaticPositionBridge
            .toStaticPosition(testCase.finalPosition().getBitboardPosition());
        final BitboardPosition bitboardPosition = StaticPositionBridge.fromStaticPosition(staticPosition);
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
