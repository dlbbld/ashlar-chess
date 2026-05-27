package com.dlb.chess.report;

import com.dlb.chess.common.model.HalfMove;

/**
 * Sequence start anchored to the first non-zeroing legal move played after a clock-resetting ply. Rendered by the print
 * layer as {@code <move-number>.[..] <SAN> (1)} where the trailing {@code (1)} is the post-move halfmove clock — by
 * construction this move's {@code halfMoveClock} is exactly {@code 1}, since the prior ply zeroed the clock.
 *
 * <p>
 * Used for sequences that begin mid-game; the previous ply was a pawn move or capture (clock zeroed) and this is the
 * first non-zeroing ply that opens the fresh no-progress run.
 */
record AfterResetStart(HalfMove firstNonZeroingMove) implements SequenceStart {

  public AfterResetStart {
    if (firstNonZeroingMove.halfMoveClock() != 1) {
      throw new IllegalArgumentException(
          "AfterResetStart.firstNonZeroingMove must have halfMoveClock == 1 by construction; was "
              + firstNonZeroingMove.halfMoveClock());
    }
  }
}
