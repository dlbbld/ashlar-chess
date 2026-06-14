// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.test.bitboard;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Set;

import org.junit.jupiter.api.Test;

import io.github.dlbbld.ashlarchess.bitboard.BitboardPosition;
import io.github.dlbbld.ashlarchess.bitboard.BitboardPositionUtility;
import io.github.dlbbld.ashlarchess.bitboard.RookAttacks;
import io.github.dlbbld.ashlarchess.bitboard.StaticPositionBridge;
import io.github.dlbbld.ashlarchess.board.StaticPosition;
import io.github.dlbbld.ashlarchess.board.enums.Side;
import io.github.dlbbld.ashlarchess.board.enums.Square;
import io.github.dlbbld.ashlarchess.common.Nulls;
import io.github.dlbbld.ashlarchess.common.utility.StaticPositionUtility;
import io.github.dlbbld.ashlarchess.squares.SlidingAttacksTestOracle;
import io.github.dlbbld.ashlarchess.test.model.PgnFen;
import io.github.dlbbld.ashlarchess.test.model.PgnTestCaseList;
import io.github.dlbbld.ashlarchess.test.pgn.setup.PgnTestCaseCatalog;
import io.github.dlbbld.ashlarchess.test.pgntest.enums.PgnTest;

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
        final StaticPosition staticPosition = StaticPositionBridge
            .toStaticPosition(testCase.finalPosition().getBitboardPosition());
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
    final StaticPosition staticPosition = StaticPositionUtility.createChangedPosition(StaticPosition.EMPTY_POSITION, Square.D4,
        io.github.dlbbld.ashlarchess.board.enums.Piece.WHITE_ROOK);
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
