package io.github.dlbbld.ashlarchess.common.model;

import io.github.dlbbld.ashlarchess.board.enums.Side;
import io.github.dlbbld.ashlarchess.common.enums.Termination;

/**
 * The current-position outcome of a game: how it ended (or {@link Termination#NONE} if it has not) and who, if anyone,
 * won. Produced by {@link io.github.dlbbld.ashlarchess.common.utility.BasicChessUtility#calculateOutcome}; the {@link #ONGOING}
 * sentinel is returned for positions where no termination condition fires.
 *
 * <p>
 * Invariant enforced by the compact constructor: {@code winner} is {@link Side#NONE} unless {@code termination} is
 * {@link Termination#CHECKMATE}. For checkmate, {@code winner} is {@link Side#WHITE} or {@link Side#BLACK} - the side
 * that delivered mate (i.e. <em>not</em> the side to move). Non-nullness of the fields is enforced at compile time by
 * the package's {@code @NonNullByDefault}.
 *
 * <p>
 * Shape parity with python-chess {@code chess.Outcome(termination, winner)} (with {@link Side#NONE} substituting for
 * Python's {@code None}, and {@link Termination#NONE} substituting for "no termination yet"). Termination is
 * information, not enforcement; the library does not block moves at these states, and callers poll
 * {@code calculateOutcome} to decide whether to adjudicate.
 */
public record Outcome(Termination termination, Side winner) {

  /**
   * Singleton "no termination" outcome - returned by {@code calculateOutcome} for ongoing positions.
   */
  public static final Outcome ONGOING = new Outcome(Termination.NONE, Side.NONE);

  public Outcome {
    if (termination == Termination.CHECKMATE) {
      if (winner == Side.NONE) {
        throw new IllegalArgumentException("CHECKMATE outcome requires winner to be WHITE or BLACK");
      }
    } else if (winner != Side.NONE) {
      throw new IllegalArgumentException(
          "non-CHECKMATE outcome " + termination + " requires winner == Side.NONE; got " + winner);
    }
  }
}
