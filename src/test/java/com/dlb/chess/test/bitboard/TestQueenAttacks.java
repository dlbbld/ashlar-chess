package com.dlb.chess.test.bitboard;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Set;

import org.junit.jupiter.api.Test;

import com.dlb.chess.bitboard.BitboardPosition;
import com.dlb.chess.bitboard.BitboardPositionUtility;
import com.dlb.chess.bitboard.QueenAttacks;
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
 * Differential test for {@link QueenAttacks}: for every queen on every fixture in the corpus, the bitboard union of
 * bishop+rook attacks must agree with {@code QueenAttackedSquares} (reached via {@link SlidingAttacksTestOracle}).
 */
class TestQueenAttacks {

  @SuppressWarnings("static-method")
  @Test
  void corpusEveryQueenAgrees() {
    for (final PgnTest pgnTest : PgnTest.values()) {
      final PgnTestCaseList testCaseList = PgnTestCaseCatalog.getTestList(pgnTest);
      for (final PgnFen testCase : testCaseList.list()) {
        final StaticPosition staticPosition = StaticPositionBridge
            .toStaticPosition(testCase.finalPosition().getBitboardPosition());
        final BitboardPosition bitboardPosition = StaticPositionBridge.fromStaticPosition(staticPosition);
        final long occupied = bitboardPosition.occupied();
        assertSideAgrees(bitboardPosition.whiteQueens(), Side.WHITE, staticPosition, occupied, testCase);
        assertSideAgrees(bitboardPosition.blackQueens(), Side.BLACK, staticPosition, occupied, testCase);
      }
    }
  }

  private static void assertSideAgrees(long queens, Side side, StaticPosition staticPosition, long occupied,
      PgnFen testCase) {
    long remaining = queens;
    while (remaining != 0L) {
      final int squareOrdinal = Long.numberOfTrailingZeros(remaining);
      final Square fromSquare = Nulls.get(Square.REAL, squareOrdinal);
      final Set<Square> bitboardAttacks = BitboardPositionUtility
          .toSquareSet(QueenAttacks.attacks(squareOrdinal, occupied));
      final Set<Square> referenceAttacks = SlidingAttacksTestOracle.queenAttacks(staticPosition, fromSquare, side);
      assertEquals(referenceAttacks, bitboardAttacks,
          side + " queen attacks from " + fromSquare.getName() + " in fixture " + testCase.pgnName());
      remaining &= remaining - 1L;
    }
  }

  @SuppressWarnings("static-method")
  @Test
  void emptyBoardFromCenterMatchesReference() {
    final StaticPosition staticPosition = StaticPosition.EMPTY_POSITION.createChangedPosition(Square.D4,
        com.dlb.chess.board.enums.Piece.WHITE_QUEEN);
    final BitboardPosition bitboardPosition = StaticPositionBridge.fromStaticPosition(staticPosition);
    final Set<Square> bitboardAttacks = BitboardPositionUtility
        .toSquareSet(QueenAttacks.attacks(Square.D4.ordinal(), bitboardPosition.occupied()));
    final Set<Square> referenceAttacks = SlidingAttacksTestOracle.queenAttacks(staticPosition, Square.D4, Side.WHITE);
    assertEquals(referenceAttacks, bitboardAttacks);
  }

  @SuppressWarnings("static-method")
  @Test
  void outOfRangeSquareOrdinalThrows() {
    assertThrows(IllegalArgumentException.class, () -> QueenAttacks.attacks(-1, 0L));
    assertThrows(IllegalArgumentException.class, () -> QueenAttacks.attacks(64, 0L));
  }
}
