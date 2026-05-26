package com.dlb.chess.test.bitboard;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Set;

import org.junit.jupiter.api.Test;

import com.dlb.chess.bitboard.BishopAttacks;
import com.dlb.chess.bitboard.BitboardPosition;
import com.dlb.chess.bitboard.BitboardPositionUtility;
import com.dlb.chess.bitboard.StaticPositionBridge;
import com.dlb.chess.board.StaticPosition;
import com.dlb.chess.board.enums.Side;
import com.dlb.chess.board.enums.Square;
import com.dlb.chess.common.Nulls;
import com.dlb.chess.squares.SlidingAttacksTestOracle;
import com.dlb.chess.test.model.PgnFen;
import com.dlb.chess.test.model.PgnTestCaseList;
import com.dlb.chess.test.pgn.setup.PgnTestCaseCatalog;
import com.dlb.chess.test.pgntest.enums.PgnTest;

/**
 * Differential test for {@link BishopAttacks}: for every bishop on every fixture in the corpus, the bitboard ray-loop
 * result must agree with {@code BishopAttackedSquares} (reached via {@link SlidingAttacksTestOracle}). Unlike the
 * non-sliding attacks, this is genuinely position-dependent — the corpus walk exercises a wide range of blocker
 * patterns.
 */
class TestBishopAttacks {

  @SuppressWarnings("static-method")
  @Test
  void corpusEveryBishopAgrees() {
    for (final PgnTest pgnTest : PgnTest.values()) {
      final PgnTestCaseList testCaseList = PgnTestCaseCatalog.getTestList(pgnTest);
      for (final PgnFen testCase : testCaseList.list()) {
        final StaticPosition staticPosition = StaticPositionBridge.toStaticPosition(testCase.finalPosition().getBitboardPosition());
        final BitboardPosition bitboardPosition = StaticPositionBridge.fromStaticPosition(staticPosition);
        final long occupied = bitboardPosition.occupied();
        assertSideAgrees(bitboardPosition.whiteBishops(), Side.WHITE, staticPosition, occupied, testCase);
        assertSideAgrees(bitboardPosition.blackBishops(), Side.BLACK, staticPosition, occupied, testCase);
      }
    }
  }

  private static void assertSideAgrees(long bishops, Side side, StaticPosition staticPosition, long occupied,
      PgnFen testCase) {
    long remaining = bishops;
    while (remaining != 0L) {
      final int squareOrdinal = Long.numberOfTrailingZeros(remaining);
      final Square fromSquare = Nulls.get(Square.REAL, squareOrdinal);
      final Set<Square> bitboardAttacks = BitboardPositionUtility
          .toSquareSet(BishopAttacks.attacks(squareOrdinal, occupied));
      final Set<Square> referenceAttacks = SlidingAttacksTestOracle.bishopAttacks(staticPosition, fromSquare, side);
      assertEquals(referenceAttacks, bitboardAttacks,
          side + " bishop attacks from " + fromSquare.getName() + " in fixture " + testCase.pgnName());
      remaining &= remaining - 1L;
    }
  }

  @SuppressWarnings("static-method")
  @Test
  void emptyBoardFromCenterMatchesReference() {
    // Bishop on D4 on an otherwise empty board — verifies the empty-board diagonal pattern via the production
    // reference rather than a hand-written expected set (no tautological self-test).
    final StaticPosition staticPosition = StaticPosition.EMPTY_POSITION
        .createChangedPosition(Square.D4, com.dlb.chess.board.enums.Piece.WHITE_BISHOP);
    final BitboardPosition bitboardPosition = StaticPositionBridge.fromStaticPosition(staticPosition);
    final Set<Square> bitboardAttacks = BitboardPositionUtility
        .toSquareSet(BishopAttacks.attacks(Square.D4.ordinal(), bitboardPosition.occupied()));
    final Set<Square> referenceAttacks = SlidingAttacksTestOracle.bishopAttacks(staticPosition, Square.D4, Side.WHITE);
    assertEquals(referenceAttacks, bitboardAttacks);
  }

  @SuppressWarnings("static-method")
  @Test
  void outOfRangeSquareOrdinalThrows() {
    assertThrows(IllegalArgumentException.class, () -> BishopAttacks.attacks(-1, 0L));
    assertThrows(IllegalArgumentException.class, () -> BishopAttacks.attacks(64, 0L));
  }
}
