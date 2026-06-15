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
        if (Square.hasLeftDiagonalSquare(side, square)) {
          final Square calculatedSquare = Square.getLeftDiagonalSquare(side, square);
          final Square revertedSquare = Square.getLeftDiagonalSquare(side.getOppositeSide(), calculatedSquare);
          assertEquals(square, revertedSquare);
        }
        if (Square.hasRightDiagonalSquare(side, square)) {
          final Square calculatedSquare = Square.getRightDiagonalSquare(side, square);
          final Square revertedSquare = Square.getRightDiagonalSquare(side.getOppositeSide(), calculatedSquare);
          assertEquals(square, revertedSquare);
        }
        if (Square.hasAheadSquare(side, square)) {
          final Square calculatedSquare = Square.getAheadSquare(side, square);
          final Square revertedSquare = Square.getBehindSquare(side, calculatedSquare);
          assertEquals(square, revertedSquare);
        }
        if (Square.hasBehindSquare(side, square)) {
          final Square calculatedSquare = Square.getBehindSquare(side, square);
          final Square revertedSquare = Square.getAheadSquare(side, calculatedSquare);
          assertEquals(square, revertedSquare);
        }
        if (Square.hasLeftSquare(side, square)) {
          final Square calculatedSquare = Square.getLeftSquare(side, square);
          final Square revertedSquare = Square.getRightSquare(side, calculatedSquare);
          assertEquals(square, revertedSquare);
        }
        if (Square.hasRightSquare(side, square)) {
          final Square calculatedSquare = Square.getRightSquare(side, square);
          final Square revertedSquare = Square.getLeftSquare(side, calculatedSquare);
          assertEquals(square, revertedSquare);
        }
      }
    }
  }

}
