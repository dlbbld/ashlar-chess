// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.report;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.annotation.Nullable;

import com.google.common.collect.ImmutableList;

import io.github.dlbbld.ashlarchess.board.Board;
import io.github.dlbbld.ashlarchess.board.enums.Side;
import io.github.dlbbld.ashlarchess.common.Nulls;
import io.github.dlbbld.ashlarchess.common.constants.ChessConstants;

abstract class FiftyMoveSequenceReportBuilder {

  /**
   * Walks the played history once, opening a new {@link SequenceStart} whenever the clock transitions to 1 (first
   * non-zeroing move after a reset, or first non-zeroing move from a fresh FEN), closing the open sequence whenever a
   * clock-resetting move is encountered, and emitting only those sequences that reach the 50-move-rule threshold
   * ({@code halfMoveClock >= 100}). While a sequence is open it captures the played moves that hit clock 100 and 150
   * (the 50/50 and 75/75 threshold anchors) and the run's starting side, so the print layer can render per-player move
   * counts.
   *
   * <p>
   * Special initial-FEN handling: if the starting FEN's halfmove clock is non-zero, a sequence is open from the
   * beginning with the initial-FEN-anchored {@link SequenceStart} shape. If the FEN's clock alone already meets the
   * threshold and the first played move resets it (or no moves are played at all), the sequence is emitted with
   * {@code endMove == null} - the print layer renders only the start marker.
   */
  static FiftyMoveSequenceReport build(Board board) {
    final int fiftyThreshold = ChessConstants.FIFTY_MOVE_RULE_HALF_MOVE_CLOCK_THRESHOLD;
    final int seventyFiveThreshold = ChessConstants.SEVENTY_FIVE_MOVE_RULE_HALF_MOVE_CLOCK_THRESHOLD;
    final int initialFenClock = board.getInitialFen().halfMoveClock();
    final Side initialFenSideToMove = board.getInitialFen().havingMove();
    final ImmutableList<MoveRecord> moveList = MoveRecords.played(board);

    final List<FiftyMoveSequence> sequences = new ArrayList<>();

    SequenceStart currentStart = initialSequenceStart(initialFenClock);
    @Nullable MoveRecord currentEndMove = null;
    @Nullable MoveRecord currentFiftyMove = null;
    @Nullable MoveRecord currentSeventyFiveMove = null;

    for (final MoveRecord move : moveList) {
      final int clock = move.halfMoveClock();
      if (clock == 0) {
        // Clock-resetting move: closes any open sequence (without including this move). The
        // resetting move itself does not start a new sequence; the NEXT non-zeroing move will.
        if (currentStart != null) {
          maybeEmit(sequences, currentStart, currentFiftyMove, currentSeventyFiveMove, currentEndMove, fiftyThreshold,
              initialFenClock, initialFenSideToMove);
        }
        currentStart = null;
        currentEndMove = null;
        currentFiftyMove = null;
        currentSeventyFiveMove = null;
      } else if (currentStart == null) {
        // First non-zeroing move after a reset (or after a fresh FEN with clock 0). By the
        // chess-engine invariant, this move's halfMoveClock is exactly 1.
        currentStart = SequenceStart.afterReset(move);
        currentEndMove = null;
        currentFiftyMove = null;
        currentSeventyFiveMove = null;
      } else {
        // Sequence continues; extend the end marker and capture threshold crossings.
        currentEndMove = move;
        if (clock == fiftyThreshold) {
          currentFiftyMove = move;
        } else if (clock == seventyFiveThreshold) {
          currentSeventyFiveMove = move;
        }
      }
    }

    // End of played history: close whatever is open.
    if (currentStart != null) {
      maybeEmit(sequences, currentStart, currentFiftyMove, currentSeventyFiveMove, currentEndMove, fiftyThreshold,
          initialFenClock, initialFenSideToMove);
    }

    return new FiftyMoveSequenceReport(Nulls.copyOfList(sequences));
  }

  private static @Nullable SequenceStart initialSequenceStart(int initialFenClock) {
    return initialFenClock > 0 ? SequenceStart.initialFen(initialFenClock) : null;
  }

  private static void maybeEmit(List<FiftyMoveSequence> sequences, SequenceStart start, @Nullable MoveRecord fiftyMove,
      @Nullable MoveRecord seventyFiveMove, @Nullable MoveRecord endMove, int threshold, int initialFenClock,
      Side initialFenSideToMove) {
    final Side startingSide = SequenceStartFormat.startingSide(start, initialFenClock, initialFenSideToMove);
    final FiftyMoveSequence sequence = new FiftyMoveSequence(start, fiftyMove, seventyFiveMove, endMove, startingSide);
    if (sequence.finalClock() >= threshold) {
      sequences.add(sequence);
    }
  }
}
