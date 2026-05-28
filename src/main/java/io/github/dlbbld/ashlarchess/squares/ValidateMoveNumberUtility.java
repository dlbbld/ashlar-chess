package io.github.dlbbld.ashlarchess.squares;

import java.util.Set;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import io.github.dlbbld.ashlarchess.board.enums.Square;
import io.github.dlbbld.ashlarchess.common.exceptions.ProgrammingMistakeException;

abstract class ValidateMoveNumberUtility {

  public static <E extends OrthogonalRange> void validateOrthogonalMoveNumber(ImmutableMap<Square, E> mapOfListList,
      int numberOfExpectedMoves) {
    int numberOfActualMoves = 0;
    for (final E bishopRange : mapOfListList.values()) {
      numberOfActualMoves += calculateOrthogonalMoves(bishopRange);
    }
    if (numberOfExpectedMoves != numberOfActualMoves) {
      throw new ProgrammingMistakeException("Move generation has a bug");
    }
  }

  private static int calculateOrthogonalMoves(OrthogonalRange moves) {
    int total = 0;
    total += moves.squareListNorth().size();
    total += moves.squareListEast().size();
    total += moves.squareListSouth().size();
    total += moves.squareListWest().size();
    return total;
  }

  public static <E extends DiagonalRange> void validateDiagonalMovesNumber(ImmutableMap<Square, E> mapOfListList,
      int numberOfExpectedMoves) {
    int numberOfActualMoves = 0;
    for (final E bishopRange : mapOfListList.values()) {
      numberOfActualMoves += calculateDiagonalMovesNumber(bishopRange);
    }
    if (numberOfExpectedMoves != numberOfActualMoves) {
      throw new ProgrammingMistakeException("Move generation has a bug");
    }
  }

  private static int calculateDiagonalMovesNumber(DiagonalRange moves) {
    int total = 0;
    total += moves.squareListNorthEast().size();
    total += moves.squareListSouthEast().size();
    total += moves.squareListSouthWest().size();
    total += moves.squareListNorthWest().size();
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
