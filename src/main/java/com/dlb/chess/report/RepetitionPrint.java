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

    final String positionIdentifier = PositionIdentifierUtility.calculateIdentifier(repetitionMove.positionId());
    result.append(positionIdentifier);
    result.append(" - ");
    result.append(repetitionMove.halfMove().countRepetition());
    result.append("/");
    result.append(repetitionMove.fold());
    result.append(")");

    return Nulls.toString(result);
  }

  private static List<RepetitionMove> calculateOutputRepetitionChronlogicallyModelList(
      List<List<HalfMove>> repetitionList) {

    final List<RepetitionMove> resultList = new ArrayList<>();
    var positionId = 0;
    for (final List<HalfMove> list : repetitionList) {
      positionId++;
      final var fold = list.size();
      for (final HalfMove halfMove : list) {
        resultList.add(new RepetitionMove(positionId, fold, halfMove));
      }
    }

    Collections.sort(resultList);
    return resultList;
  }

  private static List<String> calculateMoveNumberAndSanList(List<HalfMove> halfMoveList) {
    final List<String> result = new ArrayList<>();
    for (final HalfMove halfMove : halfMoveList) {
      result.add(HalfMoveUtility.calculateMoveNumberAndSanWithSpace(halfMove));
    }
    return result;
  }

}
