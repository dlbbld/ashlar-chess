// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.report;

import org.eclipse.jdt.annotation.Nullable;

import io.github.dlbbld.ashlarchess.board.enums.Side;
import io.github.dlbbld.ashlarchess.common.model.HalfMove;

/**
 * One no-progress halfmove sequence that reached the 50-move-rule threshold (halfmove clock {@code >= 100}).
 *
 * <p>
 * {@code start} identifies how the sequence began: an initial-FEN-anchored start (when the starting FEN already had a
 * non-zero halfmove clock that the sequence inherits) or an after-reset start (when the sequence began mid-game with
 * the first non-zeroing move after a reset). See {@link SequenceStart}.
 *
 * <p>
 * {@code fiftyMoveThresholdPly} and {@code seventyFiveMoveThresholdPly} are the played plies at which the clock first
 * reached 100 and 150 - i.e. 50 and 75 moves by each player - or {@code null} when no played ply crossed that threshold
 * (it was inherited from the starting FEN, or, for 75, never reached). They anchor the "claims open" (50/50) and
 * "automatic draw" (75/75) points in the printed report.
 *
 * <p>
 * {@code endPly} is the last non-zeroing ply of the sequence, or {@code null} for the corner case where the starting
 * FEN's clock alone already met the threshold and no played halfmove extended the sequence (typically because the only
 * legal move from the FEN-start is a capture, which resets the clock; rendered as start marker alone, no end marker).
 *
 * <p>
 * {@code startingSide} is the side that made the first ply of the run (clock 1): the first non-zeroing move's side for
 * an after-reset start, or - for an initial-FEN start - derived from the FEN's side to move and clock parity. It lets
 * the print layer split any anchor's clock into per-player (White/Black) move counts.
 *
 * <p>
 * {@code finalClock()} derives the final halfmove-clock value from {@code endPly}'s clock when present, or from the
 * start's value otherwise. By the threshold guarantee at construction time in the builder, {@code finalClock() >= 100}
 * for every sequence in {@link FiftyMoveSequenceReport}.
 */
record FiftyMoveSequence(SequenceStart start, @Nullable HalfMove fiftyMoveThresholdPly,
    @Nullable HalfMove seventyFiveMoveThresholdPly, @Nullable HalfMove endPly, Side startingSide) {

  /**
   * The sequence's final halfmove-clock value. Equal to {@code endPly.halfMoveClock()} when {@code endPly != null};
   * otherwise the start's intrinsic clock value ({@code initialClockValue} for an initial-FEN start, or {@code 1} for
   * an after-reset start - though a one-ply after-reset sequence can never reach the threshold and therefore never
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
