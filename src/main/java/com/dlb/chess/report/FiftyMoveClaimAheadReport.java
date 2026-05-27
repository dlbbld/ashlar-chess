package com.dlb.chess.report;

import com.dlb.chess.common.Nulls;
import com.google.common.collect.ImmutableList;

/**
 * All 50-move-rule claim-ahead opportunities discovered during the played history's replay, including those the side
 * actually played (asterisked in the report) and those that remained hypothetical.
 *
 * <p>
 * Companion to {@link ThreefoldClaimAheadReport}: the same per-move-claim concept applied to FIDE 9.3 instead of
 * FIDE 9.2. Entries are ordered by {@code (claimAheadMove.halfMoveCount(), legal-move-iteration-order at that ply)}.
 */
record FiftyMoveClaimAheadReport(ImmutableList<FiftyMoveClaimAheadEntry> entries) {

  public FiftyMoveClaimAheadReport {
    entries = Nulls.copyOfList(entries);
  }
}
