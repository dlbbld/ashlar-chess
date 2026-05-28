// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.test.basic;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import io.github.dlbbld.ashlarchess.board.enums.Square;
import io.github.dlbbld.ashlarchess.board.enums.SquareType;
import io.github.dlbbld.ashlarchess.common.constants.EnumConstants;

class TestBasicSquare implements EnumConstants {

  @SuppressWarnings("static-method")
  @Test
  void testCount() throws Exception {
    int totalSquares = 0;
    for (@SuppressWarnings("unused") final Square square : Square.REAL) {
      totalSquares++;
    }
    assertEquals(64, totalSquares);
  }

  @SuppressWarnings("static-method")
  @Test
  void testCalculateSquare() throws Exception {
    assertEquals(Square.A1, Square.calculate(1, 1));
    assertEquals(Square.A8, Square.calculate(1, 8));
    assertEquals(Square.H8, Square.calculate(8, 8));
    assertEquals(Square.H1, Square.calculate(8, 1));

    assertEquals(Square.A2, Square.calculate(1, 2));
    assertEquals(Square.B1, Square.calculate(2, 1));
    assertEquals(Square.B2, Square.calculate(2, 2));
    assertEquals(Square.D4, Square.calculate(4, 4));
    assertEquals(Square.B8, Square.calculate(2, 8));
    assertEquals(Square.F6, Square.calculate(6, 6));
  }

  @SuppressWarnings("static-method")
  @Test
  void testSquareType() throws Exception {
    for (final Square square : Square.REAL) {
      if ((square.getFile().getNumber() + square.getRank().getNumber()) % 2 == 0) {
        assertEquals(SquareType.DARK_SQUARE, square.getSquareType());
      } else {
        assertEquals(SquareType.LIGHT_SQUARE, square.getSquareType());
      }
    }
  }
}
