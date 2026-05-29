// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.report;

import com.google.common.collect.ImmutableList;

import io.github.dlbbld.ashlarchess.common.Nulls;

/**
 * All threefold-repetition claim-ahead opportunities discovered during the played history's replay, including those the
 * side actually played (asterisked in the report) and those that remained hypothetical.
 *
 * <p>
 * Entries are ordered by {@code (claimAheadMove.halfMoveCount(), legal-move-iteration-order at that ply)}, matching the
 * legacy outer sort (the comparator on the first element of each ply's claim group) and the inner order
 * (Board.getLegalMoves() iteration).
 */
record ThreefoldClaimAheadReport(ImmutableList<ClaimAheadEntry> entries) {

  public ThreefoldClaimAheadReport {
    entries = Nulls.copyOfList(entries);
  }
}
