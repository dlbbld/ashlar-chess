package com.dlb.chess.report;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.dlb.chess.board.HalfMoveUtility;
import com.dlb.chess.common.Nulls;
import com.dlb.chess.common.exceptions.ProgrammingMistakeException;
import com.dlb.chess.common.model.DynamicPosition;
import com.dlb.chess.common.model.HalfMove;
import com.google.common.collect.ImmutableList;

class ThreefoldClaimAheadPrint {

  public static List<List<String>> calculateClaimAheadListListPrint(ImmutableList<HalfMove> halfMoveListPlayed,
      List<List<HalfMove>> claimAheadListList, Map<DynamicPosition, String> positionIdentifierMap) {

    final List<List<String>> resultListList = new ArrayList<>();

    for (final List<HalfMove> claimAheadList : claimAheadListList) {

      // lists contains at least one ahead claim per construction
      final HalfMove claimAheadFirst = Nulls.getFirst(claimAheadList);
      final String fullMoveNumber = HalfMoveUtility
          .calculateFullMoveNumberInitialWithSpace(claimAheadFirst.fullMoveNumber(), claimAheadFirst.havingMove());

      final List<String> resultList = new ArrayList<>();
      for (final HalfMove claimAhead : claimAheadList) {
        final StringBuilder line = new StringBuilder();
        line.append(fullMoveNumber);
        line.append(" ");
        line.append(claimAhead.san());
        if (!positionIdentifierMap.containsKey(claimAhead.dynamicPosition())) {
          throw new ProgrammingMistakeException(
              "position identifier map does not contain position for half move: " + claimAhead.dynamicPosition());
        }
        line.append(" (").append(positionIdentifierMap.get(claimAhead.dynamicPosition()));
        final var hasBeenPlayed = halfMoveListPlayed.contains(claimAhead);
        if (hasBeenPlayed) {
          line.append("*");
        }
        line.append(")");
        resultList.add(Nulls.toString(line));
      }

      resultListList.add(resultList);
    }

    return resultListList;
  }
}
