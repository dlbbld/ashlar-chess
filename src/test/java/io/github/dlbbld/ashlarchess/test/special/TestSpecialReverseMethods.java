// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.test.special;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import io.github.dlbbld.ashlarchess.board.enums.Side;
import io.github.dlbbld.ashlarchess.board.enums.Square;

class TestSpecialReverseMethods {

  @SuppressWarnings("static-method")
  @Test
  void testSquareDirections() {
    for (final Square square : Square.REAL) {
      for (final Side side : Side.REAL) {
        if (square.hasLeftDiagonalSquare(side)) {
          final Square calculatedSquare = square.getLeftDiagonalSquare(side);
          final Square revertedSquare = calculatedSquare.getLeftDiagonalSquare(side.getOppositeSide());
          assertEquals(square, revertedSquare);
        }
        if (square.hasRightDiagonalSquare(side)) {
          final Square calculatedSquare = square.getRightDiagonalSquare(side);
          final Square revertedSquare = calculatedSquare.getRightDiagonalSquare(side.getOppositeSide());
          assertEquals(square, revertedSquare);
        }
        if (square.hasAheadSquare(side)) {
          final Square calculatedSquare = square.getAheadSquare(side);
          final Square revertedSquare = calculatedSquare.getBehindSquare(side);
          assertEquals(square, revertedSquare);
        }
        if (square.hasBehindSquare(side)) {
          final Square calculatedSquare = square.getBehindSquare(side);
          final Square revertedSquare = calculatedSquare.getAheadSquare(side);
          assertEquals(square, revertedSquare);
        }
        if (square.hasLeftSquare(side)) {
          final Square calculatedSquare = square.getLeftSquare(side);
          final Square revertedSquare = calculatedSquare.getRightSquare(side);
          assertEquals(square, revertedSquare);
        }
        if (square.hasRightSquare(side)) {
          final Square calculatedSquare = square.getRightSquare(side);
          final Square revertedSquare = calculatedSquare.getLeftSquare(side);
          assertEquals(square, revertedSquare);
        }
      }
    }
  }

}
