// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.common.utility;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.github.dlbbld.ashlarchess.common.HalfMoveListListComparator;
import io.github.dlbbld.ashlarchess.common.Nulls;
import io.github.dlbbld.ashlarchess.common.exceptions.ProgrammingMistakeException;
import io.github.dlbbld.ashlarchess.common.model.DynamicPosition;
import io.github.dlbbld.ashlarchess.common.model.HalfMove;
import io.github.dlbbld.ashlarchess.model.LegalMove;

public abstract class RepetitionUtility {

  public static int calculateCountRepetition(List<LegalMove> performedLegalMoveList,
      List<DynamicPosition> dynamicPositionList, DynamicPosition dynamicPosition) {

    if (performedLegalMoveList.isEmpty()) {
      throw new ProgrammingMistakeException("Not to be called for no moves played");
    }
    // double-check because we are iterating over both lists
    if (performedLegalMoveList.size() != dynamicPositionList.size() - 1) {
      throw new ProgrammingMistakeException("Something went wrong with the list size");
    }

    int countRepetition = 1;

    // we use the same index for moves and position on purpose
    for (int i = performedLegalMoveList.size() - 1; i >= 0; i--) {
      final LegalMove lastLegalMove = Nulls.get(performedLegalMoveList, i);
      if (BasicChessUtility.calculateIsResetHalfMoveClock(lastLegalMove)) {
        // if pawn move or capture the positions before cannot equal the current position
        // this is a property of the chess game with a basic mathematical proof
        // this is used often and increases performance
        return countRepetition;
      }
      final DynamicPosition previousDynamicPosition = Nulls.get(dynamicPositionList, i);
      if (dynamicPosition.equals(previousDynamicPosition)) {
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
      final int countRepetition = searchHalfMoveThreeFold.countRepetition();

      if (countRepetition == countRepetitionThreshold) {
        // if we found a half move which has the required count, we sample all half-moves with
        // the same dynamic position
        final List<HalfMove> halfMoveSameDynamicPositionList = new ArrayList<>();
        for (final HalfMove searchHalfMoveSameDynamicPosition : halfMoveList) {
          if (searchDynamicPositionThreeFold.equals(searchHalfMoveSameDynamicPosition.dynamicPosition())) {
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
      if (processedDynamicPosition.equals(position)) {
        return true;
      }
    }
    return false;
  }

}
