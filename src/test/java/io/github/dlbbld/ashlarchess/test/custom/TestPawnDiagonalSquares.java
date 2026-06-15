// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.test.custom;

import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.A1;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.A2;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.A3;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.A6;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.A7;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.A8;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.B1;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.B2;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.B3;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.B6;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.B7;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.B8;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.BLACK;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.C1;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.C3;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.C6;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.C8;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.G1;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.G3;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.G6;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.G8;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.H1;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.H2;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.H7;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.H8;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.WHITE;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Set;
import java.util.TreeSet;

import org.eclipse.jdt.annotation.NonNull;
import org.junit.jupiter.api.Test;

import io.github.dlbbld.ashlarchess.board.enums.Side;
import io.github.dlbbld.ashlarchess.board.enums.Square;
import io.github.dlbbld.ashlarchess.squares.PawnDiagonalSquares;

class TestPawnDiagonalSquares {

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
