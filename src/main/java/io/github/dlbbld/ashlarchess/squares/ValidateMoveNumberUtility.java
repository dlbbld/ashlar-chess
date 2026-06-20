// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.squares;

import java.util.Set;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import io.github.dlbbld.ashlarchess.board.enums.Square;
import io.github.dlbbld.ashlarchess.common.exceptions.ProgrammingMistakeException;

final class ValidateMoveNumberUtility {

  private ValidateMoveNumberUtility() {
  }

  public static <E extends OrthogonalRange> void validateOrthogonalMoveNumber(ImmutableMap<Square, E> rangesBySquare,
      int numberOfExpectedMoves) {
    int numberOfActualMoves = 0;
    for (final E bishopRange : rangesBySquare.values()) {
      numberOfActualMoves += calculateOrthogonalMoves(bishopRange);
    }
    if (numberOfExpectedMoves != numberOfActualMoves) {
      throw new ProgrammingMistakeException("Move generation has a bug");
    }
  }

  private static int calculateOrthogonalMoves(OrthogonalRange moves) {
    int total = 0;
    total += moves.northSquares().size();
    total += moves.eastSquares().size();
    total += moves.southSquares().size();
    total += moves.westSquares().size();
    return total;
  }

  public static <E extends DiagonalRange> void validateDiagonalMovesNumber(ImmutableMap<Square, E> rangesBySquare,
      int numberOfExpectedMoves) {
    int numberOfActualMoves = 0;
    for (final E bishopRange : rangesBySquare.values()) {
      numberOfActualMoves += calculateDiagonalMovesNumber(bishopRange);
    }
    if (numberOfExpectedMoves != numberOfActualMoves) {
      throw new ProgrammingMistakeException("Move generation has a bug");
    }
  }

  private static int calculateDiagonalMovesNumber(DiagonalRange moves) {
    int total = 0;
    total += moves.northEastSquares().size();
    total += moves.southEastSquares().size();
    total += moves.southWestSquares().size();
    total += moves.northWestSquares().size();
    return total;
  }

  public static void validateMapOfSet(ImmutableMap<Square, ImmutableSet<Square>> mapOfSet, int numberOfExpectedMoves) {
    int numberOfActualMoves = 0;
    for (final Set<Square> set : mapOfSet.values()) {
      numberOfActualMoves += set.size();
    }
    if (numberOfExpectedMoves != numberOfActualMoves) {
      throw new ProgrammingMistakeException("Move generation has a bug");
    }
  }

}
