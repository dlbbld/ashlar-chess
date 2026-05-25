package com.dlb.chess.report;

import java.util.ArrayList;
import java.util.List;

import com.dlb.chess.board.HalfMoveUtility;
import com.dlb.chess.common.Nulls;
import com.dlb.chess.common.model.ClaimAhead;
import com.dlb.chess.common.utility.BasicUtility;

class ThreefoldClaimAheadPrint {

  public static List<String> calculateClaimAheadList(List<List<ClaimAhead>> claimAheadListList) {

    final List<String> result = new ArrayList<>();

    for (final List<ClaimAhead> claimAheadList : claimAheadListList) {
      final StringBuilder line = new StringBuilder();

      // lists contains at least one ahead claim per construction
      final ClaimAhead claimAheadFirst = Nulls.getFirst(claimAheadList);
      final String fullMoveNumber = HalfMoveUtility.calculateFullMoveNumberInitialWithSpace(
          claimAheadFirst.halfMove().fullMoveNumber(), claimAheadFirst.halfMove().havingMove());
      line.append(fullMoveNumber);

      final List<String> claimAheadSanList = new ArrayList<>();
      for (final ClaimAhead claimAhead : claimAheadList) {
        claimAheadSanList.add(claimAhead.halfMove().san());
      }

      final String sanList = BasicUtility.calculateCommaSeparatedList(claimAheadSanList);
      line.append(sanList);

      result.add(Nulls.toString(line));
    }

    return result;
  }
}
