package com.dlb.chess.common.model;

import java.util.Objects;

import com.dlb.chess.board.enums.Side;
import com.dlb.chess.common.enums.Termination;

/**
 * The current-position outcome of a game: how it ended and who, if anyone, won. Produced by
 * {@link com.dlb.chess.common.utility.BasicChessUtility#calculateOutcome}; that method returns {@code null} when the
 * game is ongoing.
 *
 * <p>
 * Invariants enforced by the compact constructor:
 * <ul>
 * <li>{@code termination} is non-null.
 * <li>{@code winner} is non-null.
 * <li>{@code winner} is {@link Side#NONE} iff {@code termination} is <em>not</em> {@link Termination#CHECKMATE}. For
 *     checkmate, {@code winner} is {@link Side#WHITE} or {@link Side#BLACK} — the side that delivered mate (i.e.
 *     <em>not</em> the side to move).
 * </ul>
 *
 * <p>
 * Although the project uses {@code @NonNullByDefault}, this is a public record reachable from outside the module — the
 * null checks defend against callers using the record from contexts where the JDT annotations are not enforced.
 *
 * <p>
 * Shape parity with python-chess {@code chess.Outcome(termination, winner)} (with {@link Side#NONE} substituting for
 * Python's {@code None}). Termination is information, not enforcement; the library does not block moves at these
 * states, and callers poll {@code calculateOutcome} to decide whether to adjudicate.
 */
public record Outcome(Termination termination, Side winner) {

  public Outcome {
    Objects.requireNonNull(termination, "termination must not be null");
    Objects.requireNonNull(winner, "winner must not be null (use Side.NONE for drawing outcomes)");
    if (termination == Termination.CHECKMATE) {
      if (winner == Side.NONE) {
        throw new IllegalArgumentException("CHECKMATE outcome requires winner to be WHITE or BLACK");
      }
    } else if (winner != Side.NONE) {
      throw new IllegalArgumentException(
          "drawing outcome " + termination + " requires winner == Side.NONE; got " + winner);
    }
  }
}
