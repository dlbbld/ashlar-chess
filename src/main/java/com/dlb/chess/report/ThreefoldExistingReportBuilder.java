package com.dlb.chess.report;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.dlb.chess.common.Nulls;
import com.dlb.chess.common.model.DynamicPosition;
import com.dlb.chess.common.model.HalfMove;
import com.dlb.chess.common.utility.RepetitionUtility;

abstract class ThreefoldExistingReportBuilder {

  /**
   * Builds the "threefolds and beyond" report from the played history. Groups are ordered by
   * {@link ReportLineOrder#REPETITION_GROUP_COMPARATOR}: lex on the displayed-occurrence sequence with a virtual
   * {@code -1} prefix when the repeated position is the initial position. In practice that puts initial-position groups
   * before non-initial groups, then orders the rest by the first played occurrence of each group.
   */
  static ThreefoldExistingReport build(DynamicPosition initialDynamicPosition, List<HalfMove> halfMoveList,
      int threshold) {

    final List<List<HalfMove>> rawGroups = RepetitionUtility.calculateRepetitionListList(halfMoveList, threshold);
    final List<RepetitionGroup> groups = new ArrayList<>();
    for (final List<HalfMove> rawGroup : rawGroups) {
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
