package com.dlb.chess.report;

import java.util.ArrayList;
import java.util.List;

import com.dlb.chess.board.HalfMoveUtility;
import com.dlb.chess.common.model.HalfMove;

abstract class FiftyMoveClaimAheadPrint {

  /**
   * Renders the 50-move claim-ahead report as one line per entry. Format:
   *
   * <pre>
   *   &lt;sequence-start-marker&gt; - &lt;move-number&gt;.[..] &lt;SAN&gt; (&lt;resulting-clock&gt;[*])
   * </pre>
   *
   * <p>
   * The sequence-start marker is {@code [Starting position] (N)} for {@link InitialFenStart} and
   * {@code <ply>.[..] <SAN> (1)} for {@link AfterResetStart}. The trailing {@code *} on the resulting-clock token
   * marks entries whose {@code claimAheadMove} was actually played (the threefold claim-ahead convention).
   */
  static List<List<String>> render(FiftyMoveClaimAheadReport report) {
    final List<List<String>> resultListList = new ArrayList<>();
    for (final FiftyMoveClaimAheadEntry entry : report.entries()) {
      final List<String> tokens = new ArrayList<>();
      tokens.add(SequenceStartFormat.format(entry.sequenceStart()));
      tokens.add("-");
      tokens.add(formatClaimAhead(entry));
      resultListList.add(tokens);
    }
    return resultListList;
  }

  private static String formatClaimAhead(FiftyMoveClaimAheadEntry entry) {
    final HalfMove claimAheadMove = entry.claimAheadMove();
    final String movePart = HalfMoveUtility.calculateMoveNumberAndSanWithSpace(claimAheadMove);
    final String asterisk = entry.hasBeenPlayed() ? "*" : "";
    return movePart + " (" + claimAheadMove.halfMoveClock() + asterisk + ")";
  }
}
