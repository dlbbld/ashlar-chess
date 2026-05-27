package com.dlb.chess.report;

import java.util.ArrayList;
import java.util.List;

import com.dlb.chess.board.HalfMoveUtility;

abstract class FiftyMoveClaimAheadPrint {

  /**
   * Renders the 50-move claim-ahead report as one line per entry. Each line carries the candidate move in standard
   * move-number-and-SAN form, with an asterisk suffix when the move appears in the played history (the
   * {@code hasBeenPlayed} signal — same convention as the threefold claim-ahead print).
   */
  static List<List<String>> render(FiftyMoveClaimAheadReport report) {
    final List<List<String>> resultListList = new ArrayList<>();
    for (final FiftyMoveClaimAheadEntry entry : report.entries()) {
      final List<String> tokens = new ArrayList<>();
      final String base = HalfMoveUtility.calculateMoveNumberAndSanWithSpace(entry.claimAheadMove());
      tokens.add(entry.hasBeenPlayed() ? base + "*" : base);
      resultListList.add(tokens);
    }
    return resultListList;
  }
}
