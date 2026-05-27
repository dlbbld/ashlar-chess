package com.dlb.chess.report;

import org.eclipse.jdt.annotation.Nullable;

import com.dlb.chess.common.model.HalfMove;

/**
 * One no-progress halfmove sequence that reached the 50-move-rule threshold (halfmove clock {@code >= 100}).
 *
 * <p>
 * {@code start} identifies how the sequence began: an initial-FEN-anchored start (when the starting FEN already had a
 * non-zero halfmove clock that the sequence inherits) or an after-reset start (when the sequence began mid-game with
 * the first non-zeroing move after a reset). See {@link SequenceStart}.
 *
 * <p>
 * {@code endPly} is the last non-zeroing ply of the sequence, or {@code null} for the corner case where the starting
 * FEN's clock alone already met the threshold and no played halfmove extended the sequence (typically because the only
 * legal move from the FEN-start is a capture, which resets the clock; rendered as start marker alone, no end marker).
 *
 * <p>
 * {@code finalClock()} derives the final halfmove-clock value from {@code endPly}'s clock when present, or from the
 * start's value otherwise. By the threshold guarantee at construction time in the builder, {@code finalClock() >= 100}
 * for every sequence in {@link FiftyMoveSequenceReport}.
 */
record FiftyMoveSequence(SequenceStart start, @Nullable HalfMove endPly) {

  /**
   * The sequence's final halfmove-clock value. Equal to {@code endPly.halfMoveClock()} when {@code endPly != null};
   * otherwise the start's intrinsic clock value ({@code initialClockValue} for an initial-FEN start, or {@code 1} for
   * an after-reset start — though a one-ply after-reset sequence can never reach the threshold and therefore never
   * appears in the report).
   */
  int finalClock() {
    if (endPly != null) {
      return endPly.halfMoveClock();
    }
    if (start.isInitialFen()) {
      return start.initialClockValue();
    }
    return start.firstNonZeroingMoveOrThrow().halfMoveClock();
  }
}
