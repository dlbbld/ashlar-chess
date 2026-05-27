package com.dlb.chess.report;

import java.util.ArrayList;
import java.util.List;

import com.dlb.chess.board.HalfMoveUtility;
import com.dlb.chess.common.model.HalfMove;

abstract class FiftyMoveClaimAheadPrint {

  /**
   * Renders the missed-opportunity 50-move claim-ahead report as one line per entry. Format:
   *
   * <pre>
   *   &lt;sequence-start-marker&gt; - &lt;move-number&gt;.[..] &lt;SAN&gt; (100)
   * </pre>
   *
   * <p>
   * The resulting-clock token is always {@code (100)} by construction (the predicate only accepts candidates at clock
   * 99, so the post-move clock is always 100) and is rendered for parallelism with the sequence-report line shape, not
   * to disambiguate the value. No asterisk: under the missed-opportunity filter the candidate is by construction
   * different from the actually-played move at the boundary ply.
   */
  static List<List<String>> render(FiftyMoveClaimAheadReport report) {
    final List<List<String>> resultListList = new ArrayList<>();
    for (final FiftyMoveClaimAheadEntry entry : report.entries()) {
      final List<String> tokens = new ArrayList<>();
      tokens.add(SequenceStartFormat.format(entry.sequenceStart()));
      tokens.add("-");
      tokens.add(formatClaimAhead(entry.claimAheadMove()));
      resultListList.add(tokens);
    }
    return resultListList;
  }

  private static String formatClaimAhead(HalfMove claimAheadMove) {
    return HalfMoveUtility.calculateMoveNumberAndSanWithSpace(claimAheadMove) + " ("
        + claimAheadMove.halfMoveClock() + ")";
  }
}
