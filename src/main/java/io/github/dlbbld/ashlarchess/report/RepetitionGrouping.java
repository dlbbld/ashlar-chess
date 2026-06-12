// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.report;

import java.util.ArrayList;
import java.util.List;

import io.github.dlbbld.ashlarchess.common.Nulls;
import io.github.dlbbld.ashlarchess.common.model.DynamicPosition;

abstract class RepetitionGrouping {

  static List<List<MoveRecord>> calculateRepetitionListList(List<MoveRecord> moveRecordList,
      int countRepetitionThreshold) {

    final List<List<MoveRecord>> list = new ArrayList<>();
    final List<DynamicPosition> processed = new ArrayList<>();
    for (final MoveRecord searchMoveRecordThreeFold : moveRecordList) {
      // we iterate over the move list
      final DynamicPosition searchDynamicPositionThreeFold = searchMoveRecordThreeFold.dynamicPosition();
      if (calculateIsContained(processed, searchDynamicPositionThreeFold)) {
        continue;
      }
      final int countRepetition = searchMoveRecordThreeFold.countRepetition();

      if (countRepetition == countRepetitionThreshold) {
        // if we found a move record which has the required count, we sample all move records with
        // the same dynamic position
        final List<MoveRecord> moveRecordSameDynamicPositionList = new ArrayList<>();
        for (final MoveRecord searchMoveRecordSameDynamicPosition : moveRecordList) {
          if (searchDynamicPositionThreeFold.equals(searchMoveRecordSameDynamicPosition.dynamicPosition())) {
            moveRecordSameDynamicPositionList.add(searchMoveRecordSameDynamicPosition);
          }
        }

        list.add(moveRecordSameDynamicPositionList);
        processed.add(searchDynamicPositionThreeFold);
      }
    }
    list.sort((firstList, secondList) -> Integer.compare(Nulls.getFirst(firstList).performedMoveCount(),
        Nulls.getFirst(secondList).performedMoveCount()));
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
