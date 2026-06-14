// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.test.bitboard;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Set;

import org.junit.jupiter.api.Test;

import io.github.dlbbld.ashlarchess.bitboard.BitboardPosition;
import io.github.dlbbld.ashlarchess.bitboard.BitboardPositionUtility;
import io.github.dlbbld.ashlarchess.bitboard.QueenAttacks;
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
    final StaticPosition staticPosition = StaticPositionUtility.createChangedPosition(StaticPosition.EMPTY_POSITION, Square.D4,
        io.github.dlbbld.ashlarchess.board.enums.Piece.WHITE_QUEEN);
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
