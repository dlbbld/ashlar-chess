package com.dlb.chess.common.model;

import com.dlb.chess.common.enums.Termination;

/**
 * Snapshot of all game-end-relevant facts at a single position, together with the precedence-projected {@link Outcome}.
 * The fact booleans are independent and condition-only: each is the raw truth of its rule on the current position, not
 * suppressed by any higher-precedence condition that may also hold. {@code outcome} is the official ruling produced by
 * applying the python-chess precedence stack (CHECKMATE -> INSUFFICIENT_MATERIAL -> STALEMATE -> SEVENTY_FIVE_MOVES ->
 * FIVEFOLD_REPETITION) to those raw facts; for ongoing positions {@code outcome} carries {@link Termination#NONE}.
 *
 * <p>
 * Facts are independent. {@code Outcome} is a projection. A position can have, simultaneously, {@code checkmate=true}
 * and {@code seventyFiveMove=true} (a non-pawn non-capture mate at clock 150); the {@code outcome.termination()} is
 * {@link Termination#CHECKMATE} (highest precedence) but both facts remain true here for callers that want the raw
 * board truth instead of the official ruling.
 *
 * <p>
 * {@code deadPosition} is queryable separately from {@code insufficientMaterial}: the latter is the cheap structural
 * "king+minor vs king" class of facts that auto-terminates as {@code INSUFFICIENT_MATERIAL}; the former is the broader
 * analyzer-driven Chess Unwinnability Analyzer verdict (a superset including blocked pawn walls etc.) that does not
 * auto-terminate in this library. Both surface here for completeness; only {@code insufficientMaterial} projects into
 * {@code outcome}.
 *
 * <p>
 * {@code outcome} is never {@code null}: ongoing positions carry {@link Outcome#ONGOING}. {@link #isGameEnd()}
 * distinguishes by checking {@code outcome.termination() != Termination.NONE}. Non-nullness of {@code outcome} is
 * enforced at compile time by the package's {@code @NonNullByDefault}.
 */
public record GameEndFacts(boolean checkmate, boolean stalemate, boolean insufficientMaterial, boolean deadPosition,
    boolean fivefoldRepetition, boolean seventyFiveMove, Outcome outcome) {

  /**
   * Convenience: {@code true} iff a termination condition fires on the current position (i.e.
   * {@code outcome.termination() != Termination.NONE}).
   */
  public boolean isGameEnd() {
    return outcome.termination() != Termination.NONE;
  }
}
