package com.dlb.chess.report;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.dlb.chess.common.Nulls;
import com.dlb.chess.common.model.DynamicPosition;
import com.dlb.chess.common.model.HalfMove;

class RepetitionPrint {

  public static List<List<String>> calculateRepetitionPrint(DynamicPosition initialDynamicPosition,
      List<List<HalfMove>> repetitionListList, Map<DynamicPosition, String> positionIdentifierMap) {

    final List<List<String>> resultListList = new ArrayList<>();

    for (final List<HalfMove> repetitionList : repetitionListList) {
      final List<String> resultList = new ArrayList<>();

      final var isInitialRepetionRepeats = initialDynamicPosition
          .equals(Nulls.getFirst(repetitionList).dynamicPosition());

      var totalRepetitionCount = repetitionList.size();
      if (isInitialRepetionRepeats) {
        totalRepetitionCount = repetitionList.size() + 1;
      } else {
        totalRepetitionCount = repetitionList.size();
      }

      if (isInitialRepetionRepeats) {
        resultList.add("[Initial position]");
      }

      for (var i = 0; i <= repetitionList.size() - 1; i++) {
        final HalfMove repetitionHalfMove = Nulls.get(repetitionList, i);
        final var isAddPositionInformation = i == repetitionList.size() - 1;
        final String halfMoveInformation = PositionIdentifierUtility.calculateHalfMoveInformation(repetitionHalfMove,
            totalRepetitionCount, false, isAddPositionInformation, positionIdentifierMap);
        resultList.add(halfMoveInformation);
      }
      resultListList.add(resultList);
    }

    return resultListList;
  }

}
