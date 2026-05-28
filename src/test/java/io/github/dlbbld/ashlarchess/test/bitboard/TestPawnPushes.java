// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.test.bitboard;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Set;
import java.util.TreeSet;

import org.junit.jupiter.api.Test;

import io.github.dlbbld.ashlarchess.bitboard.BitboardPosition;
import io.github.dlbbld.ashlarchess.bitboard.BitboardPositionUtility;
import io.github.dlbbld.ashlarchess.bitboard.PawnMoves;
import io.github.dlbbld.ashlarchess.bitboard.StaticPositionBridge;
import io.github.dlbbld.ashlarchess.board.StaticPosition;
import io.github.dlbbld.ashlarchess.board.enums.Side;
import io.github.dlbbld.ashlarchess.board.enums.Square;
import io.github.dlbbld.ashlarchess.common.Nulls;
import io.github.dlbbld.ashlarchess.test.model.PgnFen;
import io.github.dlbbld.ashlarchess.test.model.PgnTestCaseList;
import io.github.dlbbld.ashlarchess.test.pgn.setup.PgnTestCaseCatalog;
import io.github.dlbbld.ashlarchess.test.pgntest.enums.PgnTest;

/**
 * Differential test for {@link PawnMoves#pushes}. The reference oracle is computed in test code directly from
 * {@link StaticPosition} using 1-indexed file/rank arithmetic and {@code Square.calculate} - structurally different
 * from the bitboard implementation's 0-indexed shift+mask, so the two implementations are independent enough that
 * agreement is a real signal.
 */
class TestPawnPushes {

  @SuppressWarnings("static-method")
  @Test
  void corpusPushesAgree() {
    for (final PgnTest pgnTest : PgnTest.values()) {
      final PgnTestCaseList testCaseList = PgnTestCaseCatalog.getTestList(pgnTest);
      for (final PgnFen testCase : testCaseList.list()) {
        final StaticPosition staticPosition = StaticPositionBridge
            .toStaticPosition(testCase.finalPosition().getBitboardPosition());
        final BitboardPosition bitboardPosition = StaticPositionBridge.fromStaticPosition(staticPosition);
        final long occupied = bitboardPosition.occupied();
        assertPushesAgree(staticPosition, bitboardPosition.whitePawns(), Side.WHITE, occupied, testCase);
        assertPushesAgree(staticPosition, bitboardPosition.blackPawns(), Side.BLACK, occupied, testCase);
      }
    }
  }

  private static void assertPushesAgree(StaticPosition staticPosition, long pawns, Side side, long occupied,
      PgnFen testCase) {
    long remaining = pawns;
    while (remaining != 0L) {
      final int squareOrdinal = Long.numberOfTrailingZeros(remaining);
      final Square fromSquare = Nulls.get(Square.REAL, squareOrdinal);
      final Set<Square> bitboardPushes = BitboardPositionUtility
          .toSquareSet(PawnMoves.pushes(squareOrdinal, occupied, side));
      final Set<Square> referencePushes = referencePushes(staticPosition, fromSquare, side);
      assertEquals(referencePushes, bitboardPushes,
          side + " pawn pushes from " + fromSquare.getName() + " in fixture " + testCase.pgnName());
      remaining &= remaining - 1L;
    }
  }

  private static Set<Square> referencePushes(StaticPosition staticPosition, Square from, Side side) {
    final Set<Square> result = new TreeSet<>();
    final int fromFile = from.getFile().getNumber();
    final int fromRank = from.getRank().getNumber();
    final int rankOffset = side == Side.WHITE ? 1 : -1;
    final int forwardRank = fromRank + rankOffset;
    if (forwardRank < 1 || forwardRank > 8) {
      return result;
    }
    final Square forward = Square.calculate(fromFile, forwardRank);
    if (!staticPosition.isEmpty(forward)) {
      return result;
    }
    result.add(forward);
    final boolean onStartingRank = side == Side.WHITE ? fromRank == 2 : fromRank == 7;
    if (onStartingRank) {
      final int doubleRank = fromRank + 2 * rankOffset;
      final Square doubleForward = Square.calculate(fromFile, doubleRank);
      if (staticPosition.isEmpty(doubleForward)) {
        result.add(doubleForward);
      }
    }
    return result;
  }

  @SuppressWarnings("static-method")
  @Test
  void initialPositionWhitePawnsAllHaveTwoPushes() {
    // From the initial position, every white pawn on rank 2 has single and double pushes available.
    final long occupied = BitboardPosition.INITIAL_POSITION.occupied();
    for (int file = 0; file < 8; file++) {
      final int squareOrdinal = 8 + file; // rank 2 (0-indexed rank 1)
      final long pushes = PawnMoves.pushes(squareOrdinal, occupied, Side.WHITE);
      assertEquals(2, Long.bitCount(pushes), "two pushes from file " + file + " on rank 2");
    }
  }

  @SuppressWarnings("static-method")
  @Test
  void outOfRangeAndNoneSideThrow() {
    assertThrows(IllegalArgumentException.class, () -> PawnMoves.pushes(-1, 0L, Side.WHITE));
    assertThrows(IllegalArgumentException.class, () -> PawnMoves.pushes(64, 0L, Side.WHITE));
    assertThrows(IllegalArgumentException.class, () -> PawnMoves.pushes(0, 0L, Side.NONE));
  }
}
