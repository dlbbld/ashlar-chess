package com.dlb.chess.report;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.dlb.chess.board.HalfMoveUtility;
import com.dlb.chess.common.Nulls;
import com.dlb.chess.common.exceptions.ProgrammingMistakeException;
import com.dlb.chess.common.model.DynamicPosition;
import com.dlb.chess.common.model.HalfMove;
import com.dlb.chess.common.utility.BasicUtility;

class RepetitionPrint {

  public static String calculateOutputRepetitionChronologically(List<List<HalfMove>> repetitionList,
      Map<DynamicPosition, String> positionIdentifierMap) {

    final List<RepetitionMove> modelList = calculateOutputRepetitionChronologicallyModelList(repetitionList);

    final List<String> result = new ArrayList<>();
    for (final RepetitionMove repetitionMove : modelList) {
      result.add(calculatePrint(repetitionMove, positionIdentifierMap));
    }

    return BasicUtility.calculateSpaceSeparatedList(result);
  }

  private static String calculatePrint(RepetitionMove repetitionMove,
      Map<DynamicPosition, String> positionIdentifierMap) {

    final StringBuilder result = new StringBuilder();

    result.append(HalfMoveUtility.calculateMoveNumberAndSanWithSpace(repetitionMove.halfMove()));

    result.append(" (");

    if (!positionIdentifierMap.containsKey(repetitionMove.halfMove().dynamicPosition())) {
      throw new ProgrammingMistakeException(
          "position identifier map does not contain position for half move: " + repetitionMove.halfMove());
    }

    final String positionIdentifier = positionIdentifierMap.get(repetitionMove.halfMove().dynamicPosition());
    result.append(positionIdentifier);
    result.append(" - ");
    result.append(repetitionMove.halfMove().countRepetition());
    result.append("/");
    result.append(repetitionMove.totalRepetitionCount());
    result.append(")");

    return Nulls.toString(result);
  }

  private static List<RepetitionMove> calculateOutputRepetitionChronologicallyModelList(
      List<List<HalfMove>> repetitionListList) {

    final List<RepetitionMove> resultList = new ArrayList<>();
    for (final List<HalfMove> repetionList : repetitionListList) {
      final var totalRepetitionCount = repetionList.size();
      for (final HalfMove halfMove : repetionList) {
        resultList.add(new RepetitionMove(totalRepetitionCount, halfMove));
      }
    }

    Collections.sort(resultList);
    return resultList;
  }

}
