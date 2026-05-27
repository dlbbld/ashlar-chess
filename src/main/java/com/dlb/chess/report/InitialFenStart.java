package com.dlb.chess.report;

/**
 * Sequence start anchored to the initial FEN's halfmove-clock value. Rendered by the print layer as
 * {@code [Starting position] (N)} where {@code N} is {@code initialClockValue}.
 *
 * <p>
 * Used when the no-progress sequence inherits a non-trivial clock from the starting FEN — either {@code (N > 0, < 100)}
 * meaning a sequence already underway when the game opened, or {@code (N >= 100)} meaning the 50-move rule was already
 * met by the FEN itself before any halfmove was played.
 */
record InitialFenStart(int initialClockValue) implements SequenceStart {

  public InitialFenStart {
    if (initialClockValue < 0) {
      throw new IllegalArgumentException("initialClockValue must be non-negative; was " + initialClockValue);
    }
  }
}
