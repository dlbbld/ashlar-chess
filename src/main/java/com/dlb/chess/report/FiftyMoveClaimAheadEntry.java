package com.dlb.chess.report;

import com.dlb.chess.common.model.HalfMove;

/**
 * One missed 50-move claim-ahead opportunity: a non-zeroing legal move that, if played at the parent ply, would have
 * brought the halfmove clock to 100 and completed the FIDE 9.3 conditions for a draw claim — but was <em>not</em>
 * played, and the actually-played move at that ply broke the sequence (a pawn move or capture, or the game ended at
 * the boundary).
 *
 * <p>
 * Only emitted when the no-progress sequence containing the parent ply did not reach clock 100 in the actual played
 * history. Once a sequence reaches the threshold (in actual play), all its claim-ahead candidates are intentionally
 * omitted — they're informationally redundant with the "Fifty moves and beyond" sequence row that already surfaces the
 * achieved threshold.
 *
 * <p>
 * Unlike the threefold claim-ahead model there is no {@code hasBeenPlayed} flag: under the missed-opportunity filter,
 * the actually-played move at the boundary ply is by construction clock-resetting (otherwise the sequence would have
 * continued past 99), so the claim-ahead candidate — being non-zeroing by predicate — can never coincide with it.
 *
 * <p>
 * {@code claimAheadMove.halfMoveClock()} is always exactly 100 by construction; the print layer renders it inside the
 * trailing {@code (100)} token for parallelism with the sequence-report line shape.
 */
record FiftyMoveClaimAheadEntry(SequenceStart sequenceStart, HalfMove claimAheadMove) {

  public FiftyMoveClaimAheadEntry {
    if (claimAheadMove.halfMoveClock() != 100) {
      throw new IllegalArgumentException(
          "claimAheadMove.halfMoveClock must be 100 by construction; was " + claimAheadMove.halfMoveClock());
    }
  }
}
