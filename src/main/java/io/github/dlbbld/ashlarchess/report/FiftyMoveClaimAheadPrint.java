// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.report;

import java.util.ArrayList;
import java.util.List;

import io.github.dlbbld.ashlarchess.board.MoveNumberFormat;
import io.github.dlbbld.ashlarchess.common.constants.ChessConstants;

abstract class FiftyMoveClaimAheadPrint {

  /**
   * Placeholder where a specific candidate's SAN would otherwise go. The 50-move claim-ahead section emits one entry
   * per missed-opportunity boundary, not per alternative legal move, so the candidate position is rendered abstractly:
   * the line states that an ahead-claim was possible at this move, not which specific move would have triggered it.
   */
  private static final String CLAIM_AHEAD_POSSIBLE_PLACEHOLDER = "[ahead claim possible]";

  /**
   * Renders the missed-opportunity 50-move claim-ahead report as one line per boundary entry. Format:
   *
   * <pre>
   *   &lt;sequence-start&gt; (W/B) - &lt;move-number&gt;.[..] [ahead claim possible] (50/50)
   * </pre>
   *
   * <p>
   * The trailing {@code (50/50)} is the would-be move count after the candidate claim - always 50 moves by each player
   * by predicate construction (the candidate brings the clock to 100), shown in the same per-player vocabulary as the
   * sequence report.
   */
  static List<List<String>> render(FiftyMoveClaimAheadReport report) {
    final List<List<String>> resultListList = new ArrayList<>();
    for (final FiftyMoveClaimAheadEntry entry : report.entries()) {
      final List<String> tokens = new ArrayList<>();
      tokens.add(SequenceStartFormat.startAnchor(entry.sequenceStart(), entry.startingSide()));
      tokens.add("-");
      tokens.add(formatBoundary(entry));
      resultListList.add(tokens);
    }
    return resultListList;
  }

  private static String formatBoundary(FiftyMoveClaimAheadEntry entry) {
    final int wouldBeClock = ChessConstants.FIFTY_MOVE_RULE_HALF_MOVE_CLOCK_THRESHOLD;
    return MoveNumberFormat.calculateMoveNumberAndSanWithSpace(entry.fullMoveNumber(), entry.sideHavingMove(),
        CLAIM_AHEAD_POSSIBLE_PLACEHOLDER) + " " + SequenceStartFormat.counts(wouldBeClock, entry.startingSide());
  }
}
