// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.report;

import java.util.List;

import io.github.dlbbld.ashlarchess.common.Nulls;

/**
 * All threefold-repetition claim-ahead opportunities discovered during the played history's replay, including those the
 * side actually played (asterisked in the report) and those that remained hypothetical.
 *
 * <p>
 * Entries are ordered by {@code (claimAheadMove.performedMoveCount(), legal-move-iteration-order at that move)},
 * matching the stable outer sort (the comparator on the first element of each move's claim group) and the inner order
 * (Board.getLegalMoves() iteration).
 */
record ThreefoldClaimAheadReport(List<ClaimAheadEntry> entries) {

  public ThreefoldClaimAheadReport {
    entries = Nulls.copyOfList(entries);
  }
}
