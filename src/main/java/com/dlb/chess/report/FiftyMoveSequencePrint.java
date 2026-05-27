package com.dlb.chess.report;

import java.util.ArrayList;
import java.util.List;

import com.dlb.chess.board.enums.Side;
import com.dlb.chess.common.constants.BasicConstants;

abstract class FiftyMoveSequencePrint {

  /**
   * Renders the 50-move-sequence report as one line of tokens per sequence. Format per sequence:
   *
   * <pre>
   *   [Initial position] [50-move rule met by initial FEN] &lt;start&gt; ... &lt;threshold-marker&gt; ... &lt;end&gt; (length N)
   * </pre>
   *
   * <p>
   * The {@code [Initial position]} marker appears only when {@code includesInitialFen} is set; the
   * {@code [50-move rule met by initial FEN]} marker only when {@code thresholdReachedDuringInitialFen} is set.
   * Played halfmove entries are rendered in standard {@code N.SAN} (White) or {@code N...SAN} (Black) form;
   * synthetic before-game entries (used by the utility to mark the initial-FEN portion) are suppressed because the
   * markers convey the same information without the verbose {@code (NA)} entries.
   */
  static List<List<String>> render(FiftyMoveSequenceReport report) {
    final List<List<String>> resultListList = new ArrayList<>();
    for (final FiftyMoveSequence sequence : report.sequences()) {
      resultListList.add(renderSequence(sequence));
    }
    return resultListList;
  }

  private static List<String> renderSequence(FiftyMoveSequence sequence) {
    final List<String> tokens = new ArrayList<>();
    if (sequence.includesInitialFen()) {
      tokens.add("[Initial position]");
    }
    if (sequence.thresholdReachedDuringInitialFen()) {
      tokens.add("[50-move rule met by initial FEN]");
    }
    for (final NoProgressHalfMove entry : sequence.entries()) {
      if (BasicConstants.NA.equals(entry.san())) {
        continue;
      }
      tokens.add(formatPlayed(entry));
    }
    tokens.add("(length " + sequence.finalSequenceLength() + ")");
    return tokens;
  }

  private static String formatPlayed(NoProgressHalfMove entry) {
    if (entry.sideMoved() == Side.WHITE) {
      return entry.fullMoveNumber() + ". " + entry.san();
    }
    return entry.fullMoveNumber() + "... " + entry.san();
  }
}
