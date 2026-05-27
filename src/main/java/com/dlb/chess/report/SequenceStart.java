package com.dlb.chess.report;

/**
 * Sealed discriminated union for the START of a 50-move-rule no-progress sequence. Either the sequence inherits an
 * existing halfmove-clock value from the starting FEN ({@link InitialFenStart}) or it began with the first non-zeroing
 * legal move played after a clock-resetting ply ({@link AfterResetStart}).
 *
 * <p>
 * Together with {@code endPly} on {@link FiftyMoveSequence} and {@code claimAheadMove} on
 * {@link FiftyMoveClaimAheadEntry}, this carries the chronological position of the sequence in the played history,
 * which is what the printed report shows as the sequence's start marker.
 */
sealed interface SequenceStart permits InitialFenStart, AfterResetStart {
}
