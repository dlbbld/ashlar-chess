package com.dlb.chess.test.bitboard;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Set;

import org.junit.jupiter.api.Test;

import com.dlb.chess.bitboard.BitboardPosition;
import com.dlb.chess.bitboard.BitboardPositionUtility;
import com.dlb.chess.bitboard.RookAttacks;
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
 * Differential test for {@link RookAttacks}: for every rook on every fixture in the corpus, the bitboard ray-loop
 * result must agree with {@code RookAttackedSquares} (reached via {@link SlidingAttacksTestOracle}).
 */
class TestRookAttacks {

  @SuppressWarnings("static-method")
  @Test
  void corpusEveryRookAgrees() {
    for (final PgnTest pgnTest : PgnTest.values()) {
      final PgnTestCaseList testCaseList = PgnTestCaseCatalog.getTestList(pgnTest);
      for (final PgnFen testCase : testCaseList.list()) {
        final StaticPosition staticPosition = StaticPositionBridge.toStaticPosition(testCase.finalPosition().getBitboardPosition());
        final BitboardPosition bitboardPosition = StaticPositionBridge.fromStaticPosition(staticPosition);
        final long occupied = bitboardPosition.occupied();
        assertSideAgrees(bitboardPosition.whiteRooks(), Side.WHITE, staticPosition, occupied, testCase);
        assertSideAgrees(bitboardPosition.blackRooks(), Side.BLACK, staticPosition, occupied, testCase);
      }
    }
  }

  private static void assertSideAgrees(long rooks, Side side, StaticPosition staticPosition, long occupied,
      PgnFen testCase) {
    long remaining = rooks;
    while (remaining != 0L) {
      final int squareOrdinal = Long.numberOfTrailingZeros(remaining);
      final Square fromSquare = Nulls.get(Square.REAL, squareOrdinal);
      final Set<Square> bitboardAttacks = BitboardPositionUtility
          .toSquareSet(RookAttacks.attacks(squareOrdinal, occupied));
      final Set<Square> referenceAttacks = SlidingAttacksTestOracle.rookAttacks(staticPosition, fromSquare, side);
      assertEquals(referenceAttacks, bitboardAttacks,
          side + " rook attacks from " + fromSquare.getName() + " in fixture " + testCase.pgnName());
      remaining &= remaining - 1L;
    }
  }

  @SuppressWarnings("static-method")
  @Test
  void emptyBoardFromCenterMatchesReference() {
    final StaticPosition staticPosition = StaticPosition.EMPTY_POSITION
        .createChangedPosition(Square.D4, com.dlb.chess.board.enums.Piece.WHITE_ROOK);
    final BitboardPosition bitboardPosition = StaticPositionBridge.fromStaticPosition(staticPosition);
    final Set<Square> bitboardAttacks = BitboardPositionUtility
        .toSquareSet(RookAttacks.attacks(Square.D4.ordinal(), bitboardPosition.occupied()));
    final Set<Square> referenceAttacks = SlidingAttacksTestOracle.rookAttacks(staticPosition, Square.D4, Side.WHITE);
    assertEquals(referenceAttacks, bitboardAttacks);
  }

  @SuppressWarnings("static-method")
  @Test
  void outOfRangeSquareOrdinalThrows() {
    assertThrows(IllegalArgumentException.class, () -> RookAttacks.attacks(-1, 0L));
    assertThrows(IllegalArgumentException.class, () -> RookAttacks.attacks(64, 0L));
  }
}
