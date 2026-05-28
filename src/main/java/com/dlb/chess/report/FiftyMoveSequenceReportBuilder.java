package com.dlb.chess.report;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.annotation.Nullable;

import com.dlb.chess.board.Board;
import com.dlb.chess.common.Nulls;
import com.dlb.chess.common.constants.ChessConstants;
import com.dlb.chess.common.model.HalfMove;
import com.google.common.collect.ImmutableList;

abstract class FiftyMoveSequenceReportBuilder {

  /**
   * Walks the played history once, opening a new {@link SequenceStart} whenever the clock transitions to 1 (first
   * non-zeroing move after a reset, or first non-zeroing move from a fresh FEN), closing the open sequence whenever a
   * clock-resetting ply is encountered, and emitting only those sequences that reach the 50-move-rule threshold
   * ({@code halfMoveClock >= 100}).
   *
   * <p>
   * Special initial-FEN handling: if the starting FEN's halfmove clock is non-zero, a sequence is open from the
   * beginning with the initial-FEN-anchored {@link SequenceStart} shape. If the FEN's clock alone already meets the
   * threshold and the first played move resets it (or no halfmoves are played at all), the sequence is emitted with
   * {@code endPly == null} - the print layer renders only the start marker.
   */
  static FiftyMoveSequenceReport build(Board board) {
    final int threshold = ChessConstants.FIFTY_MOVE_RULE_HALF_MOVE_CLOCK_THRESHOLD;
    final int initialFenClock = board.getInitialFen().halfMoveClock();
    final ImmutableList<HalfMove> halfMoveList = board.getHalfMoveList();

    final List<FiftyMoveSequence> sequences = new ArrayList<>();

    SequenceStart currentStart = initialSequenceStart(initialFenClock);
    @Nullable HalfMove currentEndPly = null;

    for (final HalfMove ply : halfMoveList) {
      final int clock = ply.halfMoveClock();
      if (clock == 0) {
        // Clock-resetting move: closes any open sequence (without including this move). The
        // resetting move itself does not start a new sequence; the NEXT non-zeroing move will.
        if (currentStart != null) {
          maybeEmit(sequences, currentStart, currentEndPly, threshold);
        }
        currentStart = null;
        currentEndPly = null;
      } else if (currentStart == null) {
        // First non-zeroing move after a reset (or after a fresh FEN with clock 0). By the
        // chess-engine invariant, this move's halfMoveClock is exactly 1.
        currentStart = SequenceStart.afterReset(ply);
        currentEndPly = null;
      } else {
        // Sequence continues; extend the end marker.
        currentEndPly = ply;
      }
    }

    // End of played history: close whatever is open.
    if (currentStart != null) {
      maybeEmit(sequences, currentStart, currentEndPly, threshold);
    }

    return new FiftyMoveSequenceReport(Nulls.copyOfList(sequences));
  }

  private static @Nullable SequenceStart initialSequenceStart(int initialFenClock) {
    return initialFenClock > 0 ? SequenceStart.initialFen(initialFenClock) : null;
  }

  private static void maybeEmit(List<FiftyMoveSequence> sequences, SequenceStart start, @Nullable HalfMove endPly,
      int threshold) {
    final FiftyMoveSequence sequence = new FiftyMoveSequence(start, endPly);
    if (sequence.finalClock() >= threshold) {
      sequences.add(sequence);
    }
  }
}
