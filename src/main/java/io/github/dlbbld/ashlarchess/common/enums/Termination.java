package io.github.dlbbld.ashlarchess.common.enums;

/**
 * The cause of an automatic game termination - the {@code termination} field of
 * {@link io.github.dlbbld.ashlarchess.common.model.Outcome}.
 *
 * <p>
 * Companion to {@link io.github.dlbbld.ashlarchess.common.utility.BasicChessUtility#calculateOutcome}: the method surfaces one of
 * these six values, with {@link #NONE} for ongoing positions where no termination condition fires. (Returning a
 * non-null {@code Outcome} for every position lets callers branch on {@code termination} alone without a separate null
 * check.)
 *
 * <p>
 * Analyzer-driven dead positions (FIDE 5.2.2 via {@link io.github.dlbbld.ashlarchess.board.Board#isDeadPositionQuick()} /
 * {@link io.github.dlbbld.ashlarchess.board.Board#isDeadPositionFull()}) are <em>not</em> represented here - invoking the analyzer
 * from {@code calculateOutcome} would silently make every status query expensive. Callers that want the analyzer-driven
 * verdict query it directly.
 *
 * <p>
 * Claimable draws (FIDE 9.2 3-fold repetition, FIDE 9.3 50-move rule) are not represented either - they end the game
 * only if a player claims them, and the claim is the caller's decision. They remain queryable on the board via the
 * dedicated {@code canClaim*} predicates.
 *
 * <p>
 * The single-side insufficient-material conditions (one side lacks mating material but the other does not) are a
 * diagnostic state of the board, not a termination, and are not represented here. Callers that need that information
 * query {@link io.github.dlbbld.ashlarchess.board.Board#isInsufficientMaterial(com.dlb.chess.board.enums.Side)} directly.
 */
public enum Termination {

  /** No termination condition fires on this position - the game is ongoing. */
  NONE,
  CHECKMATE,
  INSUFFICIENT_MATERIAL,
  STALEMATE,
  SEVENTY_FIVE_MOVES,
  FIVEFOLD_REPETITION,
}
