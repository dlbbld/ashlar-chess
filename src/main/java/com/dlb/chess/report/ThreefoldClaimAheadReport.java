package com.dlb.chess.report;

import com.dlb.chess.common.Nulls;
import com.google.common.collect.ImmutableList;

/**
 * All threefold-repetition claim-ahead opportunities discovered during the played history's replay, including those
 * the side actually played (asterisked in the report) and those that remained hypothetical.
 *
 * <p>
 * Entries are ordered by {@code (claimAheadMove.halfMoveCount(), legal-move-iteration-order at that ply)}, matching
 * the legacy outer sort (the comparator on the first element of each ply's claim group) and the inner order
 * (Board.getLegalMoves() iteration).
 */
record ThreefoldClaimAheadReport(ImmutableList<ClaimAheadEntry> entries) {

  public ThreefoldClaimAheadReport {
    entries = Nulls.copyOfList(entries);
  }
}
