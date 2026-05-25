package com.dlb.chess.common.utility;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.jdt.annotation.Nullable;

import com.dlb.chess.common.HalfMoveListListComparator;
import com.dlb.chess.common.Nulls;
import com.dlb.chess.common.exceptions.ProgrammingMistakeException;
import com.dlb.chess.common.model.DynamicPosition;
import com.dlb.chess.common.model.HalfMove;
import com.dlb.chess.model.LegalMove;

public abstract class RepetitionUtility {

  /**
   * Two dynamic positions are equal for FIDE threefold-repetition purposes when the piece placement, side to move,
   * castling rights, and en-passant availability all match. Piece-placement equality goes through
   * {@link com.dlb.chess.bitboard.BitboardPosition#equals(Object)} on the carried bitboard, which compares the 12
   * per-piece bitboards directly. We do not override {@code equals} on {@link DynamicPosition} because elsewhere
   * positions are compared by piece-arrangement only; this method is the authoritative repetition-comparison.
   */
  private static boolean equals(DynamicPosition dynamicPosition, @Nullable Object obj) {
    if (dynamicPosition == obj) {
      return true;
    }
    if (obj == null || dynamicPosition.getClass() != obj.getClass()) {
      return false;
    }
    final var other = (DynamicPosition) obj;
    return dynamicPosition.castlingRightWhite().equals(other.castlingRightWhite())
        && dynamicPosition.castlingRightBlack().equals(other.castlingRightBlack())
        && dynamicPosition.enPassantCaptureTargetSquare() == other.enPassantCaptureTargetSquare()
        && dynamicPosition.havingMove() == other.havingMove()
        && dynamicPosition.bitboardPosition().equals(other.bitboardPosition());
  }

  public static int getCountRepetition(HalfMove halfMove) {
    return halfMove.countRepetition();
  }

  public static int calculateCountRepetition(List<LegalMove> performedLegalMoveList,
      List<DynamicPosition> dynamicPositionList, DynamicPosition dynamicPosition) {

    if (performedLegalMoveList.isEmpty()) {
      throw new ProgrammingMistakeException("Not to be called for no moves played");
    }
    // double-check because we are iterating over both lists
    if (performedLegalMoveList.size() != dynamicPositionList.size() - 1) {
      throw new ProgrammingMistakeException("Something went wrong with the list size");
    }

    var countRepetition = 1;

    // we use the same index for moves and position on purpose
    for (var i = performedLegalMoveList.size() - 1; i >= 0; i--) {
      final LegalMove lastLegalMove = Nulls.get(performedLegalMoveList, i);
      if (BasicChessUtility.calculateIsResetHalfMoveClock(lastLegalMove)) {
        // if pawn move or capture the positions before cannot equal the current position
        // this is a property of the chess game with a basic mathematical proof
        // this is used often and increases performance
        return countRepetition;
      }
      final DynamicPosition previousDynamicPosition = Nulls.get(dynamicPositionList, i);
      if (equals(dynamicPosition, previousDynamicPosition)) {
        countRepetition++;
      }
    }
    if (countRepetition < 1) {
      throw new ProgrammingMistakeException("The conditional repetition count cannot be below one");
    }
    return countRepetition;
  }

  public static List<List<HalfMove>> calculateRepetitionListList(List<HalfMove> halfMoveList,
      int countRepetitionThreshold) {

    final List<List<HalfMove>> list = new ArrayList<>();
    final List<DynamicPosition> processed = new ArrayList<>();
    for (final HalfMove searchHalfMoveThreeFold : halfMoveList) {
      // we iterate over the move list
      final DynamicPosition searchDynamicPositionThreeFold = searchHalfMoveThreeFold.dynamicPosition();
      if (calculateIsContained(processed, searchDynamicPositionThreeFold)) {
        continue;
      }
      final var countRepetition = getCountRepetition(searchHalfMoveThreeFold);

      if (countRepetition == countRepetitionThreshold) {
        // if we found a half move which has the required count, we sample all half-moves with
        // the same dynamic position
        final List<HalfMove> halfMoveSameDynamicPositionList = new ArrayList<>();
        for (final HalfMove searchHalfMoveSameDynamicPosition : halfMoveList) {
          if (equals(searchDynamicPositionThreeFold, searchHalfMoveSameDynamicPosition.dynamicPosition())) {
            halfMoveSameDynamicPositionList.add(searchHalfMoveSameDynamicPosition);
          }
        }

        list.add(halfMoveSameDynamicPositionList);
        processed.add(searchDynamicPositionThreeFold);
      }
    }
    Collections.sort(list, HalfMoveListListComparator.COMPARATOR);
    return list;
  }

  private static boolean calculateIsContained(List<DynamicPosition> processedDynamicPositionList,
      DynamicPosition position) {
    for (final DynamicPosition processedDynamicPosition : processedDynamicPositionList) {
      if (equals(processedDynamicPosition, position)) {
        return true;
      }
    }
    return false;
  }

}
