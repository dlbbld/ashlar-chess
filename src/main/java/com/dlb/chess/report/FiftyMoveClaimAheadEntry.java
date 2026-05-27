package com.dlb.chess.report;

import com.dlb.chess.common.model.HalfMove;

/**
 * One legal move that would, if played from its parent position, complete the 50 non-progress halfmoves under FIDE
 * 9.3 — a per-move claim-ahead opportunity. The corresponding {@link com.dlb.chess.board.Board} predicate is
 * {@code canClaimFiftyMoveRuleFor(MoveSpecification)}.
 *
 * <p>
 * {@code claimAheadMove} is the {@link HalfMove} that <em>would</em> be played from the parent position. The parent
 * position is whichever position was current during the report-builder's replay walk at the time this entry was
 * generated.
 *
 * <p>
 * {@code hasBeenPlayed} is true when the same half-move appears in the played history — meaning the side actually
 * made the claim-ahead-able move on the board. The reporter marks such entries with an asterisk (consistent with the
 * threefold claim-ahead convention).
 *
 * <p>
 * The 50-move analogue has no {@code priorOccurrences} or {@code includesInitialPosition} fields — the 50-move rule
 * is about a halfmove-clock run, not about position repetition, so position-occurrence bookkeeping does not apply.
 */
record FiftyMoveClaimAheadEntry(HalfMove claimAheadMove, boolean hasBeenPlayed) {
}
