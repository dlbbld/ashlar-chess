package com.dlb.chess.common.model;

import org.eclipse.jdt.annotation.Nullable;

import com.dlb.chess.common.enums.Termination;

/**
 * Snapshot of all game-end-relevant facts at a single position, together with the precedence-projected {@link Outcome}.
 * The fact booleans are independent and condition-only: each is the raw truth of its rule on the current position, not
 * suppressed by any higher-precedence condition that may also hold. {@code outcome} is the official ruling produced by
 * applying the python-chess precedence stack (CHECKMATE → INSUFFICIENT_MATERIAL → STALEMATE → SEVENTY_FIVE_MOVES →
 * FIVEFOLD_REPETITION) to those raw facts.
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
 * {@code outcome} is {@code null} iff no termination condition holds — equivalently, {@link #isGameEnd()} returns
 * {@code false}.
 */
public record GameEndFacts(boolean checkmate, boolean stalemate, boolean insufficientMaterial, boolean deadPosition,
    boolean fivefoldRepetition, boolean seventyFiveMove, @Nullable Outcome outcome) {

  /**
   * Convenience: {@code true} iff a termination condition fires on the current position (i.e. {@code outcome != null}).
   * Identical to {@code outcome() != null}; provided to mirror common API shape and so callers can write
   * {@code if (facts.isGameEnd()) ...} naturally.
   */
  public boolean isGameEnd() {
    return outcome != null;
  }
}
