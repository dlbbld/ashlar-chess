// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.report;

import java.util.ArrayList;
import java.util.List;

import io.github.dlbbld.ashlarchess.common.Nulls;
import io.github.dlbbld.ashlarchess.common.model.DynamicPosition;

final class RepetitionGrouping {

  private RepetitionGrouping() {
  }

  static List<List<MoveRecord>> calculateRepetitionGroups(List<MoveRecord> moveRecords,
      int countRepetitionThreshold) {

    final List<List<MoveRecord>> list = new ArrayList<>();
    final List<DynamicPosition> processed = new ArrayList<>();
    for (final MoveRecord searchMoveRecordThreeFold : moveRecords) {
      // we iterate over the move list
      final DynamicPosition searchDynamicPositionThreeFold = searchMoveRecordThreeFold.dynamicPosition();
      if (calculateIsContained(processed, searchDynamicPositionThreeFold)) {
        continue;
      }
      final int countRepetition = searchMoveRecordThreeFold.countRepetition();

      if (countRepetition == countRepetitionThreshold) {
        // if we found a move record which has the required count, we sample all move records with
        // the same dynamic position
        final List<MoveRecord> moveRecordsSameDynamicPosition = new ArrayList<>();
        for (final MoveRecord searchMoveRecordSameDynamicPosition : moveRecords) {
          if (searchDynamicPositionThreeFold.equals(searchMoveRecordSameDynamicPosition.dynamicPosition())) {
            moveRecordsSameDynamicPosition.add(searchMoveRecordSameDynamicPosition);
          }
        }

        list.add(moveRecordsSameDynamicPosition);
        processed.add(searchDynamicPositionThreeFold);
      }
    }
    list.sort((firstGroup, secondGroup) -> Integer.compare(Nulls.getFirst(firstGroup).performedMoveCount(),
        Nulls.getFirst(secondGroup).performedMoveCount()));
    return list;
  }

  private static boolean calculateIsContained(List<DynamicPosition> processedDynamicPositions,
      DynamicPosition position) {
    for (final DynamicPosition processedDynamicPosition : processedDynamicPositions) {
      if (processedDynamicPosition.equals(position)) {
        return true;
      }
    }
    return false;
  }

}
