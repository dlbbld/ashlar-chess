package com.dlb.chess.report;

import com.dlb.chess.common.Nulls;
import com.dlb.chess.common.model.HalfMove;
import com.google.common.collect.ImmutableList;

/**
 * One legal move that would, if played from its parent position, create a threefold-repetition position the side
 * could claim before playing the move.
 *
 * <p>
 * {@code priorOccurrences} are the played half-moves at which the position appeared before {@code claimAheadMove} and
 * EXCLUDES {@code claimAheadMove} itself. The total occurrence count is
 * {@code priorOccurrences.size() + 1 + (includesInitialPosition ? 1 : 0)}: the +1 covers the claim-ahead move as the
 * (n+1)th occurrence, and the +1 for {@code includesInitialPosition} covers the implicit initial-position occurrence
 * not present in {@code priorOccurrences}. The compact constructor enforces this invariant.
 *
 * <p>
 * {@code hasBeenPlayed} is true when the same half-move appears in the played history - meaning the side actually
 * made the claim-ahead-able move on the board. The reporter marks such entries with an asterisk.
 */
record ClaimAheadEntry(HalfMove claimAheadMove, boolean hasBeenPlayed, ImmutableList<HalfMove> priorOccurrences,
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
