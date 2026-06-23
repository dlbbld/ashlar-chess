// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.report;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.github.dlbbld.ashlarchess.board.DynamicPosition;
import io.github.dlbbld.ashlarchess.internal.Nulls;

final class RepetitionPrint {

  private RepetitionPrint() {
  }

  static List<List<String>> render(ThreefoldExistingReport report, Map<DynamicPosition, String> positionIdentifierMap) {

    final List<List<String>> lineGroups = new ArrayList<>();
    for (final RepetitionGroup group : report.groups()) {
      final List<String> lines = new ArrayList<>();
      if (group.includesInitialPosition()) {
        lines.add("[Initial position]");
      }
      final List<MoveRecord> occurrences = group.occurrences();
      for (int i = 0; i < occurrences.size(); i++) {
        final MoveRecord move = Nulls.get(occurrences, i);
        final boolean isAddPositionInformation = i == occurrences.size() - 1;
        final String moveInformation = PositionIdentifierUtility.calculateMoveInformation(move,
            group.totalRepetitionCount(), false, isAddPositionInformation, positionIdentifierMap);
        lines.add(moveInformation);
      }
      lineGroups.add(lines);
    }
    return lineGroups;
  }
}
