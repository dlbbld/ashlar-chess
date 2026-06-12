// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.report;

import com.google.common.collect.ImmutableList;

import io.github.dlbbld.ashlarchess.common.Nulls;

/**
 * All no-progress runs in the game that reached the 50-move-rule threshold (halfmove clock {@code >= 100}).
 *
 * <p>
 * Companion to {@link ThreefoldExistingReport}: where the threefold report lists repeated positions, this report lists
 * the points in time where the no-progress counter crossed the FIDE 9.3 threshold. Sequences are ordered
 * chronologically by start move, with the initial-FEN-already-at-threshold sequence - if any - listed first because its
 * start move is treated as before-game.
 */
record FiftyMoveSequenceReport(ImmutableList<FiftyMoveSequence> sequences) {

  public FiftyMoveSequenceReport {
    sequences = Nulls.copyOfList(sequences);
  }
}
