// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.report;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableList;

import io.github.dlbbld.ashlarchess.common.Nulls;
import io.github.dlbbld.ashlarchess.common.model.DynamicPosition;

abstract class RepetitionPrint {

  static List<List<String>> render(ThreefoldExistingReport report, Map<DynamicPosition, String> positionIdentifierMap) {

    final List<List<String>> resultListList = new ArrayList<>();
    for (final RepetitionGroup group : report.groups()) {
      final List<String> resultList = new ArrayList<>();
      if (group.includesInitialPosition()) {
        resultList.add("[Initial position]");
      }
      final ImmutableList<MoveRecord> occurrences = group.occurrences();
      for (int i = 0; i < occurrences.size(); i++) {
        final MoveRecord halfMove = Nulls.get(occurrences, i);
        final boolean isAddPositionInformation = i == occurrences.size() - 1;
        final String halfMoveInformation = PositionIdentifierUtility.calculateHalfMoveInformation(halfMove,
            group.totalRepetitionCount(), false, isAddPositionInformation, positionIdentifierMap);
        resultList.add(halfMoveInformation);
      }
      resultListList.add(resultList);
    }
    return resultListList;
  }
}
