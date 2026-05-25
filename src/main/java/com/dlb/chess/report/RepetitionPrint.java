package com.dlb.chess.report;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.dlb.chess.board.HalfMoveUtility;
import com.dlb.chess.common.Nulls;
import com.dlb.chess.common.model.HalfMove;
import com.dlb.chess.common.utility.BasicUtility;

class RepetitionPrint {

  public static String calculateOutputRepetitionChronologically(List<List<HalfMove>> repetitionList) {

    final List<RepetitionMove> modelList = calculateOutputRepetitionChronologicallyModelList(repetitionList);

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

    // FIXME that is wrong
    final String positionNumber = PositionIdentifierUtility.calculateIdentifier(1);
    result.append(positionNumber);
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
