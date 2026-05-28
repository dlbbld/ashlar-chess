package io.github.dlbbld.ashlarchess.unwinnability;

import java.util.Set;
import java.util.TreeSet;

import io.github.dlbbld.ashlarchess.board.enums.Side;
import io.github.dlbbld.ashlarchess.board.enums.Square;

class KingDistanceOneFunctions {

  public static Set<Square> calculateDiagonalSquares(Square sq) {
    final Set<Square> result = new TreeSet<>();
    if (Square.calculateHasLeftDiagonalSquare(Side.WHITE, sq)) {
      result.add(Square.calculateLeftDiagonalSquare(Side.WHITE, sq));
    }
    if (Square.calculateHasRightDiagonalSquare(Side.WHITE, sq)) {
      result.add(Square.calculateRightDiagonalSquare(Side.WHITE, sq));
    }
    if (Square.calculateHasLeftDiagonalSquare(Side.BLACK, sq)) {
      result.add(Square.calculateLeftDiagonalSquare(Side.BLACK, sq));
    }
    if (Square.calculateHasRightDiagonalSquare(Side.BLACK, sq)) {
      result.add(Square.calculateRightDiagonalSquare(Side.BLACK, sq));
    }
    return result;
  }

  public static Set<Square> calculateOrthogonalSquares(Square sq) {
    final Set<Square> result = new TreeSet<>();
    if (Square.calculateHasAheadSquare(Side.WHITE, sq)) {
      result.add(Square.calculateAheadSquare(Side.WHITE, sq));
    }
    if (Square.calculateHasRightSquare(Side.WHITE, sq)) {
      result.add(Square.calculateRightSquare(Side.WHITE, sq));
    }
    if (Square.calculateHasBehindSquare(Side.WHITE, sq)) {
      result.add(Square.calculateBehindSquare(Side.WHITE, sq));
    }
    if (Square.calculateHasLeftSquare(Side.WHITE, sq)) {
      result.add(Square.calculateLeftSquare(Side.WHITE, sq));
    }
    return result;
  }

}
