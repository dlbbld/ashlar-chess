// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.test.custom;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Set;
import java.util.TreeSet;

import org.eclipse.jdt.annotation.NonNull;
import org.junit.jupiter.api.Test;

import io.github.dlbbld.ashlarchess.board.enums.Side;
import io.github.dlbbld.ashlarchess.board.enums.Square;
import io.github.dlbbld.ashlarchess.common.constants.EnumConstants;
import io.github.dlbbld.ashlarchess.squares.PawnDiagonalSquares;

class TestPawnDiagonalSquares implements EnumConstants {

  @SuppressWarnings("static-method")
  @Test
  void testPawnDiagonalSquares() {
    check(WHITE, A1);
    check(WHITE, A2, B3);
    check(WHITE, A7, B8);
    check(WHITE, A8);

    check(WHITE, B1);
    check(WHITE, B2, A3, C3);
    check(WHITE, B7, A8, C8);
    check(WHITE, B8);

    check(WHITE, H1);
    check(WHITE, H2, G3);
    check(WHITE, H7, G8);
    check(WHITE, H8);

    check(WHITE, A8);
    check(BLACK, A7, B6);
    check(BLACK, A2, B1);
    check(BLACK, A1);

    check(WHITE, B8);
    check(BLACK, B7, A6, C6);
    check(BLACK, B2, A1, C1);
    check(WHITE, B1);

    check(WHITE, H8);
    check(BLACK, H7, G6);
    check(BLACK, H2, G1);
    check(BLACK, H1);
  }

  private static void check(Side havingMove, Square fromSquare, Square... expectedSquareArray) {
    final Set<Square> diagonalSquareSet = PawnDiagonalSquares.getPawnDiagonalSquares(havingMove, fromSquare);

    final Set<Square> expectedSquareSet = new TreeSet<>();
    for (final Square expectedSquare : expectedSquareArray) {
      @SuppressWarnings("null") @NonNull final Square expectedSquareNonNull = expectedSquare;
      expectedSquareSet.add(expectedSquareNonNull);
    }
    assertEquals(expectedSquareSet, diagonalSquareSet);
  }
}
