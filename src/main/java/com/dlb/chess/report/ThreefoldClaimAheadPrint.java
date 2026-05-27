package com.dlb.chess.report;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.dlb.chess.common.Nulls;
import com.dlb.chess.common.model.DynamicPosition;
import com.dlb.chess.common.model.HalfMove;
import com.google.common.collect.ImmutableList;

abstract class ThreefoldClaimAheadPrint {

  static List<List<String>> render(ThreefoldClaimAheadReport report,
      Map<DynamicPosition, String> positionIdentifierMap) {

    final List<List<String>> resultListList = new ArrayList<>();
    for (final ClaimAheadEntry entry : report.entries()) {
      final List<String> resultList = new ArrayList<>();
      if (entry.includesInitialPosition()) {
        resultList.add("[Initial position]");
      }

      // The joined sequence is [priorOccurrences..., claimAheadMove]. claimAheadMove sits at lastIndex.
      final ImmutableList<HalfMove> priorOccurrences = entry.priorOccurrences();
      final int lastIndex = priorOccurrences.size();
      for (int i = 0; i <= lastIndex; i++) {
        final HalfMove halfMove = i < lastIndex ? Nulls.get(priorOccurrences, i) : entry.claimAheadMove();
        final boolean isAddAsterisk = i < lastIndex || entry.hasBeenPlayed();
        final boolean isAddPositionInformation = i == lastIndex;
        final String halfMoveInformation = PositionIdentifierUtility.calculateHalfMoveInformation(halfMove,
            entry.totalRepetitionCount(), isAddAsterisk, isAddPositionInformation, positionIdentifierMap);
        resultList.add(halfMoveInformation);
      }
      resultListList.add(resultList);
    }
    return resultListList;
  }
}
