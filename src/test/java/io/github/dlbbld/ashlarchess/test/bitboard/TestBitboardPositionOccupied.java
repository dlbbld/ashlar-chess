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
import io.github.dlbbld.ashlarchess.bitboard.StaticPositionBridge;
import io.github.dlbbld.ashlarchess.board.StaticPosition;
import io.github.dlbbld.ashlarchess.board.enums.Side;
import io.github.dlbbld.ashlarchess.board.enums.Square;
import io.github.dlbbld.ashlarchess.test.model.PgnFen;
import io.github.dlbbld.ashlarchess.test.model.PgnTestCaseList;
import io.github.dlbbld.ashlarchess.test.pgn.setup.PgnTestCaseCatalog;
import io.github.dlbbld.ashlarchess.test.pgntest.enums.PgnTest;

/**
 * Differential test for {@link BitboardPosition#occupied()} and {@link BitboardPosition#occupied(Side)}: the bitboard
 * occupancy must agree, square-for-square, with what the reference {@link StaticPosition} reports via
 * {@link StaticPosition#isEmpty(Square)} and {@link StaticPosition#isOwnPiece(Square, Side)}.
 */
class TestBitboardPositionOccupied {

  @SuppressWarnings("static-method")
  @Test
  void corpusOccupiedTotalAgrees() {
    for (final PgnTest pgnTest : PgnTest.values()) {
      final PgnTestCaseList testCaseList = PgnTestCaseCatalog.getTestList(pgnTest);
      for (final PgnFen testCase : testCaseList.list()) {
        final StaticPosition staticPosition = StaticPositionBridge
            .toStaticPosition(testCase.finalPosition().getBitboardPosition());
        final BitboardPosition bitboardPosition = StaticPositionBridge.fromStaticPosition(staticPosition);
        final Set<Square> fromBitboard = BitboardPositionUtility.toSquareSet(bitboardPosition.occupied());
        final Set<Square> fromReference = referenceOccupied(staticPosition);
        assertEquals(fromReference, fromBitboard, "occupied() in fixture " + testCase.pgnName());
      }
    }
  }

  @SuppressWarnings("static-method")
  @Test
  void corpusOccupiedPerSideAgrees() {
    for (final PgnTest pgnTest : PgnTest.values()) {
      final PgnTestCaseList testCaseList = PgnTestCaseCatalog.getTestList(pgnTest);
      for (final PgnFen testCase : testCaseList.list()) {
        final StaticPosition staticPosition = StaticPositionBridge
            .toStaticPosition(testCase.finalPosition().getBitboardPosition());
        final BitboardPosition bitboardPosition = StaticPositionBridge.fromStaticPosition(staticPosition);
        assertSideAgrees(staticPosition, bitboardPosition, Side.WHITE, testCase);
        assertSideAgrees(staticPosition, bitboardPosition, Side.BLACK, testCase);
      }
    }
  }

  private static void assertSideAgrees(StaticPosition staticPosition, BitboardPosition bitboardPosition, Side side,
      PgnFen testCase) {
    final Set<Square> fromBitboard = BitboardPositionUtility.toSquareSet(bitboardPosition.occupied(side));
    final Set<Square> fromReference = referenceOccupiedBySide(staticPosition, side);
    assertEquals(fromReference, fromBitboard, "occupied(" + side + ") in fixture " + testCase.pgnName());
  }

  private static Set<Square> referenceOccupied(StaticPosition staticPosition) {
    final Set<Square> squares = new TreeSet<>();
    for (final Square square : Square.REAL) {
      if (!staticPosition.isEmpty(square)) {
        squares.add(square);
      }
    }
    return squares;
  }

  private static Set<Square> referenceOccupiedBySide(StaticPosition staticPosition, Side side) {
    final Set<Square> squares = new TreeSet<>();
    for (final Square square : Square.REAL) {
      if (staticPosition.isOwnPiece(square, side)) {
        squares.add(square);
      }
    }
    return squares;
  }

  @SuppressWarnings("static-method")
  @Test
  void initialAndEmptyConstantsAgreeOnTotal() {
    final Set<Square> initialOccupied = BitboardPositionUtility
        .toSquareSet(BitboardPosition.INITIAL_POSITION.occupied());
    assertEquals(referenceOccupied(StaticPosition.INITIAL_POSITION), initialOccupied);
    assertEquals(0L, BitboardPosition.EMPTY_POSITION.occupied());
  }

  @SuppressWarnings("static-method")
  @Test
  void noneSideThrows() {
    assertThrows(IllegalArgumentException.class, () -> BitboardPosition.INITIAL_POSITION.occupied(Side.NONE));
  }
}
