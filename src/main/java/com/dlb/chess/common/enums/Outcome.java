package com.dlb.chess.common.enums;

import org.eclipse.jdt.annotation.Nullable;

import com.dlb.chess.board.enums.Side;

/**
 * The current-position outcome of a game: how it ended and who, if anyone, won. Produced by
 * {@link com.dlb.chess.common.utility.BasicChessUtility#calculateOutcome}; that method returns {@code null} when the
 * game is ongoing.
 *
 * <p>
 * {@code winner} is the side that won (non-null only for {@link Termination#CHECKMATE}, where it is the side that
 * delivered mate — i.e. <em>not</em> the side to move); for every drawing termination ({@code STALEMATE},
 * {@code INSUFFICIENT_MATERIAL}, {@code SEVENTY_FIVE_MOVES}, {@code FIVEFOLD_REPETITION}) it is {@code null}.
 *
 * <p>
 * Shape parity with python-chess {@code chess.Outcome(termination, winner)} — termination is information, not
 * enforcement; the library does not block moves at these states, and callers poll {@code calculateOutcome} to decide
 * whether to adjudicate.
 */
public record Outcome(Termination termination, @Nullable Side winner) {
}
