package com.dlb.chess.report;

import java.util.ArrayList;
import java.util.List;

import com.dlb.chess.board.Board;
import com.dlb.chess.common.Nulls;
import com.dlb.chess.common.constants.ChessConstants;
import com.google.common.collect.ImmutableList;

abstract class FiftyMoveSequenceReportBuilder {

  /**
   * Builds the 50-move-sequence report by wrapping the raw output of {@link NoProgressMoveUtility} into typed
   * {@link FiftyMoveSequence} records.
   *
   * <p>
   * Always uses the FIDE 9.3 threshold (100 halfmoves). The underlying utility supports lower thresholds for future
   * reporting flavors (e.g. half-progress sequences); this builder commits to the 50-move-rule threshold because the
   * report is named after it.
   *
   * <p>
   * Sets {@code includesInitialFen} for any sequence whose first marker is before the first played halfmove (the
   * initial FEN's halfmove clock contributed to the sequence's start), and additionally sets
   * {@code thresholdReachedDuringInitialFen} when the initial FEN's halfmove clock alone already met or exceeded the
   * threshold. The two flags together let the print layer distinguish "initial-FEN-continued" from
   * "initial-FEN-already-at-threshold" cases — the latter is surfaced even when no halfmoves are played from the
   * initial position (e.g. when the only legal move is a capture and there is no continuation).
   */
  static FiftyMoveSequenceReport build(Board board) {
    final int threshold = ChessConstants.FIFTY_MOVE_RULE_HALF_MOVE_CLOCK_THRESHOLD;
    final List<List<NoProgressHalfMove>> rawSequences = NoProgressMoveUtility.calculateNoProgressMoveRule(board,
        threshold);
    final int initialFenClock = board.getInitialFen().halfMoveClock();

    final List<FiftyMoveSequence> sequences = new ArrayList<>();
    for (final List<NoProgressHalfMove> rawSequence : rawSequences) {
      final NoProgressHalfMove first = Nulls.getFirst(rawSequence);
      final NoProgressHalfMove last = Nulls.getLast(rawSequence);
      // includesInitialFen: the sequence starts before the first played halfmove. The utility
      // emits such markers with a non-positive performedHalfMoveCount when the initial FEN's
      // halfmove clock contributed to the sequence's start.
      final boolean includesInitialFen = first.performedHalfMoveCount() <= 0;
      // thresholdReachedDuringInitialFen: the initial FEN's clock alone already met or exceeded
      // the threshold — the rule was met without any played halfmoves contributing. This is the
      // special case where the report must surface a sequence even if the played history is empty
      // or the only legal continuation zeroes the clock.
      final boolean thresholdReachedDuringInitialFen = includesInitialFen && initialFenClock >= threshold;
      final int finalSequenceLength = last.sequenceLength();
      sequences.add(new FiftyMoveSequence(ImmutableList.copyOf(rawSequence), includesInitialFen,
          thresholdReachedDuringInitialFen, finalSequenceLength));
    }
    return new FiftyMoveSequenceReport(ImmutableList.copyOf(sequences));
  }
}
