// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.report;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.github.dlbbld.ashlarchess.common.Nulls;
import io.github.dlbbld.ashlarchess.common.model.DynamicPosition;

abstract class ThreefoldExistingReportBuilder {

  /**
   * Builds the "threefolds and beyond" report from the played history. Groups are ordered by
   * {@link ReportLineOrder#REPETITION_GROUP_COMPARATOR}: lex on the displayed-occurrence sequence with a virtual
   * {@code -1} prefix when the repeated position is the initial position. In practice that puts initial-position groups
   * before non-initial groups, then orders the rest by the first played occurrence of each group.
   */
  static ThreefoldExistingReport build(DynamicPosition initialDynamicPosition, List<MoveRecord> moveRecordList,
      int threshold) {

    final List<List<MoveRecord>> rawGroups = RepetitionGrouping.calculateRepetitionListList(moveRecordList, threshold);
    final List<RepetitionGroup> groups = new ArrayList<>();
    for (final List<MoveRecord> rawGroup : rawGroups) {
      final DynamicPosition repeatedPosition = Nulls.getFirst(rawGroup).dynamicPosition();
      final boolean includesInitialPosition = initialDynamicPosition.equals(repeatedPosition);
      final int totalRepetitionCount = rawGroup.size() + (includesInitialPosition ? 1 : 0);
      groups.add(new RepetitionGroup(repeatedPosition, Nulls.copyOfList(rawGroup), includesInitialPosition,
          totalRepetitionCount));
    }
    Collections.sort(groups, ReportLineOrder.REPETITION_GROUP_COMPARATOR);
    return new ThreefoldExistingReport(Nulls.copyOfList(groups));
  }
}
