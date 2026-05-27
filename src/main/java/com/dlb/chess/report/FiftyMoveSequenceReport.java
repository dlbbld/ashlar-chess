package com.dlb.chess.report;

import com.dlb.chess.common.Nulls;
import com.google.common.collect.ImmutableList;

/**
 * All no-progress runs in the game that reached the 50-move-rule threshold (halfmove clock {@code >= 100}).
 *
 * <p>
 * Companion to {@link ThreefoldExistingReport}: where the threefold report lists repeated positions, this report lists
 * the points in time where the no-progress halfmove counter crossed the FIDE 9.3 threshold. Sequences are ordered as
 * {@link NoProgressMoveUtility} produces them (chronologically by start ply, with the initial-FEN-already-at-threshold
 * sequence — if any — listed first because its start ply is treated as before-game).
 */
record FiftyMoveSequenceReport(ImmutableList<FiftyMoveSequence> sequences) {

  public FiftyMoveSequenceReport {
    sequences = Nulls.copyOfList(sequences);
  }
}
