// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.report;

import com.google.common.collect.ImmutableList;

import io.github.dlbbld.ashlarchess.common.Nulls;

/**
 * One legal move that would, if played from its parent position, create a threefold-repetition position the side could
 * claim before playing the move.
 *
 * <p>
 * {@code priorOccurrences} are the played moves at which the position appeared before {@code claimAheadMove} and
 * EXCLUDES {@code claimAheadMove} itself. The total occurrence count is
 * {@code priorOccurrences.size() + 1 + (includesInitialPosition ? 1 : 0)}: the +1 covers the claim-ahead move as the
 * (n+1)th occurrence, and the +1 for {@code includesInitialPosition} covers the implicit initial-position occurrence
 * not present in {@code priorOccurrences}. The compact constructor enforces this invariant.
 *
 * <p>
 * {@code hasBeenPlayed} is true when the same move appears in the played history - meaning the side actually made
 * the claim-ahead-able move on the board. The reporter marks such entries with an asterisk.
 */
record ClaimAheadEntry(MoveRecord claimAheadMove, boolean hasBeenPlayed, ImmutableList<MoveRecord> priorOccurrences,
    boolean includesInitialPosition, int totalRepetitionCount) {

  public ClaimAheadEntry {
    priorOccurrences = Nulls.copyOfList(priorOccurrences);
    final int expectedTotal = priorOccurrences.size() + 1 + (includesInitialPosition ? 1 : 0);
    if (totalRepetitionCount != expectedTotal) {
      throw new IllegalArgumentException("totalRepetitionCount " + totalRepetitionCount
          + " disagrees with priorOccurrences.size()=" + priorOccurrences.size()
          + " (+1 for claimAheadMove, +1 if includesInitialPosition=" + includesInitialPosition + ")");
    }
  }
}
