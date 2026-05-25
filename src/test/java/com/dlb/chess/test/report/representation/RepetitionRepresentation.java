package com.dlb.chess.test.report.representation;

import java.util.ArrayList;
import java.util.List;

import com.dlb.chess.board.HalfMoveUtility;
import com.dlb.chess.common.Nulls;
import com.dlb.chess.common.model.HalfMove;
import com.dlb.chess.common.utility.RepetitionUtility;
import com.dlb.chess.report.Report;

public class RepetitionRepresentation {

  private static String calculateRepresentationRepetitionList(List<HalfMove> halfMoveList) {
    final StringBuilder result = new StringBuilder();
    // the moves must be sorted ascending, as this is expected in the result
    // so the last entry has the repetition count of the sequence in the repetition count for the half-move
    final HalfMove lastHalfMove = Nulls.getLast(halfMoveList);

    final var countRepetition = RepetitionUtility.getCountRepetition(lastHalfMove);

    result.append("repPos=").append(countRepetition).append(": ");
    for (var i = 0; i < halfMoveList.size(); i++) {
      final HalfMove halfMove = Nulls.get(halfMoveList, i);
      result.append(HalfMoveUtility.calculateMoveNumberAndSanWithSpace(halfMove));
      if (i <= halfMoveList.size() - 2) {
        result.append(" ");
      }
    }
    return Nulls.toString(result);
  }

  public static String calculateRepresentationRepetitionReport(Report report) {
    return calculateRepresentationRepetitionListList(report.repetitionListList());
  }

  public static String calculateRepresentationRepetitionListList(List<List<HalfMove>> repetitionList) {
    final List<String> resultList = new ArrayList<>();
    final var listLength = repetitionList.size();
    for (var i = 0; i < listLength; i++) {
      final List<HalfMove> repeatingPosition = Nulls.get(repetitionList, i);

      resultList.add(calculateRepresentationRepetitionList(repeatingPosition));
    }
    return Nulls.join("; ", resultList);
  }

}
