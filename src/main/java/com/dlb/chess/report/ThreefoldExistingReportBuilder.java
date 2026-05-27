package com.dlb.chess.report;

import java.util.ArrayList;
import java.util.List;

import com.dlb.chess.common.Nulls;
import com.dlb.chess.common.model.DynamicPosition;
import com.dlb.chess.common.model.HalfMove;
import com.dlb.chess.common.utility.RepetitionUtility;
import com.google.common.collect.ImmutableList;

abstract class ThreefoldExistingReportBuilder {

  static ThreefoldExistingReport build(DynamicPosition initialDynamicPosition, List<HalfMove> halfMoveList,
      int threshold) {

    final List<List<HalfMove>> rawGroups = RepetitionUtility.calculateRepetitionListList(halfMoveList, threshold);
    final List<RepetitionGroup> groups = new ArrayList<>();
    for (final List<HalfMove> rawGroup : rawGroups) {
      final DynamicPosition repeatedPosition = Nulls.getFirst(rawGroup).dynamicPosition();
      final boolean includesInitialPosition = initialDynamicPosition.equals(repeatedPosition);
      final int totalRepetitionCount = rawGroup.size() + (includesInitialPosition ? 1 : 0);
      groups.add(new RepetitionGroup(repeatedPosition, ImmutableList.copyOf(rawGroup), includesInitialPosition,
          totalRepetitionCount));
    }
    return new ThreefoldExistingReport(ImmutableList.copyOf(groups));
  }
}
