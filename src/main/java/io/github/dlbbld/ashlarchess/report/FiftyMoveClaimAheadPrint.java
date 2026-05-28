package io.github.dlbbld.ashlarchess.report;

import java.util.ArrayList;
import java.util.List;

import io.github.dlbbld.ashlarchess.board.HalfMoveUtility;

abstract class FiftyMoveClaimAheadPrint {

  /**
   * Placeholder where a specific candidate's SAN would otherwise go. The 50-move claim-ahead section emits one entry
   * per missed-opportunity boundary, not per alternative legal move, so the candidate position is rendered abstractly:
   * the line states that an ahead-claim was possible at this ply, not which specific move would have triggered it.
   */
  private static final String CLAIM_AHEAD_POSSIBLE_PLACEHOLDER = "[ahead claim possible]";

  /**
   * Renders the missed-opportunity 50-move claim-ahead report as one line per boundary entry. Format:
   *
   * <pre>
   *   &lt;sequence-start-marker&gt; - &lt;move-number&gt;.[..] [ahead claim possible] (100)
   * </pre>
   *
   * <p>
   * The trailing {@code (100)} is the post-candidate halfmove clock - always 100 by predicate construction, rendered
   * for parallelism with the sequence-report line shape.
   */
  static List<List<String>> render(FiftyMoveClaimAheadReport report) {
    final List<List<String>> resultListList = new ArrayList<>();
    for (final FiftyMoveClaimAheadEntry entry : report.entries()) {
      final List<String> tokens = new ArrayList<>();
      tokens.add(SequenceStartFormat.format(entry.sequenceStart()));
      tokens.add("-");
      tokens.add(formatBoundary(entry));
      resultListList.add(tokens);
    }
    return resultListList;
  }

  private static String formatBoundary(FiftyMoveClaimAheadEntry entry) {
    return HalfMoveUtility.calculateMoveNumberAndSanWithSpace(entry.fullMoveNumber(), entry.sideHavingMove(),
        CLAIM_AHEAD_POSSIBLE_PLACEHOLDER) + " (100)";
  }
}
