package com.dlb.chess.report;

import java.util.ArrayList;
import java.util.List;

import com.dlb.chess.board.HalfMoveUtility;
import com.dlb.chess.common.Nulls;
import com.dlb.chess.common.model.HalfMove;

class ThreefoldClaimAheadPrint {

  public static List<List<String>> calculateClaimAheadListListPrint(List<List<HalfMove>> claimAheadListList) {

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
        resultList.add(Nulls.toString(line));
      }

      resultListList.add(resultList);
    }

    return resultListList;
  }
}
