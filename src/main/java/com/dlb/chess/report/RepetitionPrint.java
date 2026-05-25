package com.dlb.chess.report;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.dlb.chess.board.HalfMoveUtility;
import com.dlb.chess.common.Nulls;
import com.dlb.chess.common.model.HalfMove;
import com.dlb.chess.common.utility.BasicUtility;

class RepetitionPrint {

  public static String calculateOutputRepetitionChronlogically(List<List<HalfMove>> repetitionList) {

    final List<RepetitionMove> modelList = calculateOutputRepetitionChronlogicallyModelList(repetitionList);

    final List<String> result = new ArrayList<>();
    for (final RepetitionMove repetitionMove : modelList) {
      result.add(calculatePrint(repetitionMove));
    }

    return BasicUtility.calculateSpaceSeparatedList(result);
  }

  private static String calculatePrint(RepetitionMove repetitionMove) {

    final StringBuilder result = new StringBuilder();

    result.append(HalfMoveUtility.calculateMoveNumberAndSanWithSpace(repetitionMove.halfMove()));

    result.append(" (");

    final String positionNumber = PositionIdentifierUtility.calculateIdentifier(repetitionMove.repetionSeriesId());
    result.append(positionNumber);
    result.append(" - ");
    result.append(repetitionMove.halfMove().countRepetition());
    result.append("/");
    result.append(repetitionMove.totalRepetitionCount());
    result.append(")");

    return Nulls.toString(result);
  }

  private static List<RepetitionMove> calculateOutputRepetitionChronlogicallyModelList(
      List<List<HalfMove>> repetitionListList) {

    final List<RepetitionMove> resultList = new ArrayList<>();
    var repetionSeriesId = 0;
    for (final List<HalfMove> repetionList : repetitionListList) {
      repetionSeriesId++;
      final var totalRepetitionCount = repetionList.size();
      for (final HalfMove halfMove : repetionList) {
        resultList.add(new RepetitionMove(repetionSeriesId, totalRepetitionCount, halfMove));
      }
    }

    Collections.sort(resultList);
    return resultList;
  }

}
