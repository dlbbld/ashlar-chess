// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.report;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import io.github.dlbbld.ashlarchess.common.Nulls;
import io.github.dlbbld.ashlarchess.common.model.DynamicPosition;

final class RepetitionGrouping {

  private RepetitionGrouping() {
  }

  static List<List<MoveRecord>> calculateRepetitionGroups(List<MoveRecord> moveRecords, int countRepetitionThreshold) {

    // Group every move record by its dynamic position in a single pass (O(n)); a position's k-th occurrence carries
    // repetition count k (identical positions only recur within one no-progress window), so a group reaches the
    // threshold exactly when one of its occurrences has count == threshold. The earlier nested scan over the whole
    // move list per record was O(n^2) and dominated the report on repetition-heavy games.
    final Map<DynamicPosition, List<MoveRecord>> occurrencesByPosition = new LinkedHashMap<>();
    for (final MoveRecord moveRecord : moveRecords) {
      if (!occurrencesByPosition.containsKey(moveRecord.dynamicPosition())) {
        final List<MoveRecord> occurrences = new ArrayList<>();
        occurrences.add(moveRecord);
        occurrencesByPosition.put(moveRecord.dynamicPosition(), occurrences);
      } else {
        final List<MoveRecord> occurrences = Nulls.get(occurrencesByPosition, moveRecord.dynamicPosition());
        occurrences.add(moveRecord);
      }
    }

    final List<List<MoveRecord>> list = new ArrayList<>();
    for (final List<MoveRecord> occurrences : occurrencesByPosition.values()) {
      if (reachesThreshold(occurrences, countRepetitionThreshold)) {
        list.add(occurrences);
      }
    }
    list.sort((firstGroup, secondGroup) -> Integer.compare(Nulls.getFirst(firstGroup).performedMoveCount(),
        Nulls.getFirst(secondGroup).performedMoveCount()));
    return list;
  }

  private static boolean reachesThreshold(List<MoveRecord> occurrences, int countRepetitionThreshold) {
    for (final MoveRecord occurrence : occurrences) {
      if (occurrence.countRepetition() == countRepetitionThreshold) {
        return true;
      }
    }
    return false;
  }

}
