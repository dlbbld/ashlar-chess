package com.dlb.chess.report;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.dlb.chess.common.Nulls;
import com.dlb.chess.common.model.DynamicPosition;
import com.dlb.chess.common.model.HalfMove;
import com.google.common.collect.ImmutableList;

class ThreefoldClaimAheadPrint {

  public static List<List<String>> calculateClaimAheadListListPrint(ImmutableList<HalfMove> halfMoveListPlayed,
      List<List<HalfMove>> claimAheadListList, Map<DynamicPosition, String> positionIdentifierMap) {

    final List<List<String>> resultListList = new ArrayList<>();

    for (final List<HalfMove> claimAheadList : claimAheadListList) {

      for (final HalfMove claimAhead : claimAheadList) {
        final List<String> resultList = new ArrayList<>();
        final List<HalfMove> repetitionLine = calculateRepetitionLine(halfMoveListPlayed, claimAhead);

        final var totalRepetitionCount = repetitionLine.size();
        final var hasBeenPlayed = halfMoveListPlayed.contains(claimAhead);
        for (var i = 0; i <= repetitionLine.size() - 1; i++) {
          final HalfMove repetitionLineHalfMove = Nulls.get(repetitionLine, i);
          final var isAddPositionInformation = i == repetitionLine.size() - 1;

          final String halfMoveInformation = PositionIdentifierUtility.calculateHalfMoveInformation(
              repetitionLineHalfMove, totalRepetitionCount, hasBeenPlayed, isAddPositionInformation,
              positionIdentifierMap);
          resultList.add(halfMoveInformation);
        }
        resultListList.add(resultList);
      }
    }
    return resultListList;
  }

  private static List<HalfMove> calculateRepetitionLine(ImmutableList<HalfMove> halfMoveListPlayed,
      HalfMove claimAhead) {
    final List<HalfMove> resultList = new ArrayList<>();
    for (final HalfMove halfMovePlayed : halfMoveListPlayed) {
      if (halfMovePlayed.halfMoveCount() >= claimAhead.halfMoveCount()) {
        break;
      }
      if (halfMovePlayed.dynamicPosition().equals(claimAhead.dynamicPosition())) {
        resultList.add(halfMovePlayed);
      }
    }
    resultList.add(claimAhead);
    return resultList;
  }
}
