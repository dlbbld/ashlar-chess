// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.report;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.github.dlbbld.ashlarchess.board.DynamicPosition;
import io.github.dlbbld.ashlarchess.internal.Nulls;

final class ThreefoldClaimAheadPrint {

  private ThreefoldClaimAheadPrint() {
  }

  static List<List<String>> render(ThreefoldClaimAheadReport report,
      Map<DynamicPosition, String> positionIdentifierMap) {

    final List<List<String>> lineGroups = new ArrayList<>();
    for (final ClaimAheadEntry entry : report.entries()) {
      final List<String> lines = new ArrayList<>();
      if (entry.includesInitialPosition()) {
        lines.add("[Initial position]");
      }

      // The joined sequence is [priorOccurrences..., claimAheadMove]. claimAheadMove sits at lastIndex.
      final List<MoveRecord> priorOccurrences = entry.priorOccurrences();
      final int lastIndex = priorOccurrences.size();
      for (int i = 0; i <= lastIndex; i++) {
        final MoveRecord move = i < lastIndex ? Nulls.get(priorOccurrences, i) : entry.claimAheadMove();
        final boolean isAddAsterisk = i < lastIndex || entry.hasBeenPlayed();
        final boolean isAddPositionInformation = i == lastIndex;
        final String moveInformation = PositionIdentifierUtility.calculateMoveInformation(move,
            entry.totalRepetitionCount(), isAddAsterisk, isAddPositionInformation, positionIdentifierMap);
        lines.add(moveInformation);
      }
      lineGroups.add(lines);
    }
    return lineGroups;
  }
}
