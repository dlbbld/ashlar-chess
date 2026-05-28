package com.dlb.chess.report;

import org.eclipse.jdt.annotation.Nullable;

import com.dlb.chess.common.model.HalfMove;

/**
 * The START of a 50-move-rule no-progress sequence. Either the sequence inherits an existing halfmove-clock value from
 * the starting FEN (build with {@link #initialFen(int)}), or it began with the first non-zeroing legal move played
 * after a clock-resetting ply (build with {@link #afterReset(HalfMove)}).
 *
 * <p>
 * The two shapes share one record with a boolean discriminator. The compact constructor enforces that exactly one of
 * the two payload fields is populated:
 * <ul>
 * <li>{@code isInitialFen == true}: {@code initialClockValue >= 0}, {@code firstNonZeroingMove == null}.</li>
 * <li>{@code isInitialFen == false}: {@code initialClockValue == 0} (unused), {@code firstNonZeroingMove != null} with
 * {@code halfMoveClock == 1} by chess-engine invariant.</li>
 * </ul>
 */
record SequenceStart(boolean isInitialFen, int initialClockValue, @Nullable HalfMove firstNonZeroingMove) {

  public SequenceStart {
    if (isInitialFen) {
      if (firstNonZeroingMove != null) {
        throw new IllegalArgumentException("isInitialFen=true requires firstNonZeroingMove == null");
      }
      if (initialClockValue < 0) {
        throw new IllegalArgumentException("initialClockValue must be non-negative; was " + initialClockValue);
      }
    } else {
      if (firstNonZeroingMove == null) {
        throw new IllegalArgumentException("isInitialFen=false requires firstNonZeroingMove != null");
      }
      if (firstNonZeroingMove.halfMoveClock() != 1) {
        throw new IllegalArgumentException("firstNonZeroingMove must have halfMoveClock == 1 by construction; was "
            + firstNonZeroingMove.halfMoveClock());
      }
      if (initialClockValue != 0) {
        throw new IllegalArgumentException(
            "isInitialFen=false requires initialClockValue == 0 (the field is unused in this case); got "
                + initialClockValue);
      }
    }
  }

  static SequenceStart initialFen(int initialClockValue) {
    return new SequenceStart(true, initialClockValue, null);
  }

  static SequenceStart afterReset(HalfMove firstNonZeroingMove) {
    return new SequenceStart(false, 0, firstNonZeroingMove);
  }

  /**
   * Returns {@code firstNonZeroingMove} guaranteed non-null. Throws if called on an initial-FEN start, where the field
   * is null by invariant. Consumers that have already branched on {@link #isInitialFen()} use this to avoid a manual
   * null-check that JDT cannot infer through the discriminator.
   */
  HalfMove firstNonZeroingMoveOrThrow() {
    if (firstNonZeroingMove == null) {
      throw new IllegalStateException("firstNonZeroingMove is null on an initial-FEN sequence start");
    }
    return firstNonZeroingMove;
  }
}
