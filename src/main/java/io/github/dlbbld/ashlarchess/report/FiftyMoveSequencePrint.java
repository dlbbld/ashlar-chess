package io.github.dlbbld.ashlarchess.report;

import java.util.ArrayList;
import java.util.List;

import io.github.dlbbld.ashlarchess.board.HalfMoveUtility;
import io.github.dlbbld.ashlarchess.common.model.HalfMove;

abstract class FiftyMoveSequencePrint {

  /**
   * Renders the 50-move-sequence report as one line per sequence. Format:
   *
   * <pre>
   *   &lt;sequence-start-marker&gt; - &lt;move-number&gt;.[..] &lt;SAN&gt; (&lt;final-clock&gt;)
   * </pre>
   *
   * <p>
   * The end-of-sequence token is omitted entirely for the corner case where the starting FEN's clock alone met the
   * threshold and no halfmove extended the sequence - there the line is just the start marker, e.g.
   * {@code [Starting position] (100)}.
   */
  static List<List<String>> render(FiftyMoveSequenceReport report) {
    final List<List<String>> resultListList = new ArrayList<>();
    for (final FiftyMoveSequence sequence : report.sequences()) {
      final List<String> tokens = new ArrayList<>();
      tokens.add(SequenceStartFormat.format(sequence.start()));
      final HalfMove endPly = sequence.endPly();
      if (endPly != null) {
        tokens.add("-");
        tokens.add(formatEndPly(endPly));
      }
      resultListList.add(tokens);
    }
    return resultListList;
  }

  private static String formatEndPly(HalfMove endPly) {
    final String movePart = HalfMoveUtility.calculateMoveNumberAndSanWithSpace(endPly);
    return movePart + " (" + endPly.halfMoveClock() + ")";
  }
}
