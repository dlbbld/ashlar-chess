package com.dlb.chess.common.enums;

/**
 * The cause of an automatic game termination — the {@code termination} field of {@link Outcome}.
 *
 * <p>
 * Companion to {@link com.dlb.chess.common.utility.BasicChessUtility#calculateOutcome}, which surfaces one of these
 * five causes when the board is in an automatic termination state, or {@code null} when the game is ongoing.
 *
 * <p>
 * Analyzer-driven dead positions (FIDE 5.2.2 via {@link com.dlb.chess.board.Board#isDeadPositionQuick()} /
 * {@link com.dlb.chess.board.Board#isDeadPositionFull()}) are <em>not</em> represented here — invoking the analyzer
 * from {@code calculateOutcome} would silently make every status query expensive. Callers that want the analyzer-driven
 * verdict query it directly.
 *
 * <p>
 * Claimable draws (FIDE 9.2 3-fold repetition, FIDE 9.3 50-move rule) are not represented either — they end the game
 * only if a player claims them, and the claim is the caller's decision. They remain queryable on the board via the
 * dedicated {@code canClaim*} predicates.
 *
 * <p>
 * The single-side insufficient-material conditions (one side lacks mating material but the other does not) are a
 * diagnostic state of the board, not a termination, and are not represented here. Callers that need that information
 * query {@link com.dlb.chess.board.Board#isInsufficientMaterial(com.dlb.chess.board.enums.Side)} directly.
 */
public enum Termination {

  CHECKMATE,
  INSUFFICIENT_MATERIAL,
  STALEMATE,
  SEVENTY_FIVE_MOVES,
  FIVEFOLD_REPETITION,
}
