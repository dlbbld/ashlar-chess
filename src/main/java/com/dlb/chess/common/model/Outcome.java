package com.dlb.chess.common.model;

import org.eclipse.jdt.annotation.Nullable;

import com.dlb.chess.board.enums.Side;
import com.dlb.chess.common.enums.Termination;

/**
 * The current-position outcome of a game: how it ended and who, if anyone, won. Produced by
 * {@link com.dlb.chess.common.utility.BasicChessUtility#calculateOutcome}; that method returns {@code null} when the
 * game is ongoing.
 *
 * <p>
 * Invariant enforced by the compact constructor: {@code winner} is non-null if and only if {@code termination} is
 * {@link Termination#CHECKMATE}, in which case it is the side that delivered mate (i.e. <em>not</em> the side to
 * move). Every drawing termination ({@code STALEMATE}, {@code INSUFFICIENT_MATERIAL}, {@code SEVENTY_FIVE_MOVES},
 * {@code FIVEFOLD_REPETITION}) carries {@code winner == null}.
 *
 * <p>
 * Shape parity with python-chess {@code chess.Outcome(termination, winner)} — termination is information, not
 * enforcement; the library does not block moves at these states, and callers poll {@code calculateOutcome} to decide
 * whether to adjudicate.
 */
public record Outcome(Termination termination, @Nullable Side winner) {

  public Outcome {
    if (termination == Termination.CHECKMATE) {
      if (winner == null) {
        throw new IllegalArgumentException("CHECKMATE outcome requires a non-null winner");
      }
    } else if (winner != null) {
      throw new IllegalArgumentException("drawing outcome " + termination + " requires winner == null");
    }
  }
}
