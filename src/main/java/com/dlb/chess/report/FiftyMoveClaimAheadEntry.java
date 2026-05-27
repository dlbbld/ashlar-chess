package com.dlb.chess.report;

import com.dlb.chess.common.model.HalfMove;

/**
 * One legal move that would, if played from its parent position, complete the 50 non-progress halfmoves under FIDE
 * 9.3 — a per-move claim-ahead opportunity. The corresponding {@link com.dlb.chess.board.Board} predicate is
 * {@code canClaimFiftyMoveRuleFor(MoveSpecification)}.
 *
 * <p>
 * {@code sequenceStart} carries the start of the no-progress sequence the parent position belongs to
 * ({@link InitialFenStart} or {@link AfterResetStart}), so the print layer can render the line in the form
 * {@code <sequenceStart-marker> - <claimAheadMove> (<post-clock>[*])}, attributing each per-move claim to the run it
 * advances.
 *
 * <p>
 * {@code claimAheadMove} is the {@link HalfMove} that <em>would</em> be played; its {@code halfMoveClock} is the
 * resulting clock value (always {@code >= 100} for any entry that exists in the report, since the per-move predicate
 * only accepts moves at parent-clock {@code >= 99}).
 *
 * <p>
 * {@code hasBeenPlayed} is true when the same half-move appears in the played history. At most one entry per ply can
 * have this flag (only one move can actually be played at any ply); the reporter marks such entries with an asterisk
 * (consistent with the threefold claim-ahead convention).
 */
record FiftyMoveClaimAheadEntry(SequenceStart sequenceStart, HalfMove claimAheadMove, boolean hasBeenPlayed) {
}
