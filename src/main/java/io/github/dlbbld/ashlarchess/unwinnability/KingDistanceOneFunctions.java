// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.unwinnability;

import java.util.Set;
import java.util.TreeSet;

import io.github.dlbbld.ashlarchess.board.enums.Side;
import io.github.dlbbld.ashlarchess.board.enums.Square;

class KingDistanceOneFunctions {

  public static Set<Square> calculateDiagonalSquares(Square sq) {
    final Set<Square> result = new TreeSet<>();
    if (Square.hasLeftDiagonalSquare(Side.WHITE, sq)) {
      result.add(Square.getLeftDiagonalSquare(Side.WHITE, sq));
    }
    if (Square.hasRightDiagonalSquare(Side.WHITE, sq)) {
      result.add(Square.getRightDiagonalSquare(Side.WHITE, sq));
    }
    if (Square.hasLeftDiagonalSquare(Side.BLACK, sq)) {
      result.add(Square.getLeftDiagonalSquare(Side.BLACK, sq));
    }
    if (Square.hasRightDiagonalSquare(Side.BLACK, sq)) {
      result.add(Square.getRightDiagonalSquare(Side.BLACK, sq));
    }
    return result;
  }

  public static Set<Square> calculateOrthogonalSquares(Square sq) {
    final Set<Square> result = new TreeSet<>();
    if (Square.hasAheadSquare(Side.WHITE, sq)) {
      result.add(Square.getAheadSquare(Side.WHITE, sq));
    }
    if (Square.hasRightSquare(Side.WHITE, sq)) {
      result.add(Square.getRightSquare(Side.WHITE, sq));
    }
    if (Square.hasBehindSquare(Side.WHITE, sq)) {
      result.add(Square.getBehindSquare(Side.WHITE, sq));
    }
    if (Square.hasLeftSquare(Side.WHITE, sq)) {
      result.add(Square.getLeftSquare(Side.WHITE, sq));
    }
    return result;
  }

}
