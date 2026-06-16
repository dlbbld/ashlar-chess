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
    if (sq.hasLeftDiagonalSquare(Side.WHITE)) {
      result.add(sq.getLeftDiagonalSquare(Side.WHITE));
    }
    if (sq.hasRightDiagonalSquare(Side.WHITE)) {
      result.add(sq.getRightDiagonalSquare(Side.WHITE));
    }
    if (sq.hasLeftDiagonalSquare(Side.BLACK)) {
      result.add(sq.getLeftDiagonalSquare(Side.BLACK));
    }
    if (sq.hasRightDiagonalSquare(Side.BLACK)) {
      result.add(sq.getRightDiagonalSquare(Side.BLACK));
    }
    return result;
  }

  public static Set<Square> calculateOrthogonalSquares(Square sq) {
    final Set<Square> result = new TreeSet<>();
    if (sq.hasAheadSquare(Side.WHITE)) {
      result.add(sq.getAheadSquare(Side.WHITE));
    }
    if (sq.hasRightSquare(Side.WHITE)) {
      result.add(sq.getRightSquare(Side.WHITE));
    }
    if (sq.hasBehindSquare(Side.WHITE)) {
      result.add(sq.getBehindSquare(Side.WHITE));
    }
    if (sq.hasLeftSquare(Side.WHITE)) {
      result.add(sq.getLeftSquare(Side.WHITE));
    }
    return result;
  }

}
