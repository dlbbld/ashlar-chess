package com.dlb.chess.report;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.dlb.chess.common.Nulls;
import com.dlb.chess.common.model.DynamicPosition;
import com.dlb.chess.common.model.HalfMove;
import com.google.common.collect.ImmutableList;

abstract class RepetitionPrint {

  static List<List<String>> render(ThreefoldExistingReport report,
      Map<DynamicPosition, String> positionIdentifierMap) {

    final List<List<String>> resultListList = new ArrayList<>();
    for (final RepetitionGroup group : report.groups()) {
      final List<String> resultList = new ArrayList<>();
      if (group.includesInitialPosition()) {
        resultList.add("[Initial position]");
      }
      final ImmutableList<HalfMove> occurrences = group.occurrences();
      for (int i = 0; i < occurrences.size(); i++) {
        final HalfMove halfMove = Nulls.get(occurrences, i);
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
