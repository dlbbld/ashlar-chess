package com.dlb.chess.report;

import com.dlb.chess.common.Nulls;
import com.google.common.collect.ImmutableList;

/**
 * All positions that reached the threefold-repetition threshold in the played history.
 *
 * <p>
 * Groups are ordered by the half-move count of each group's first occurrence (matching
 * {@code HalfMoveListListComparator.COMPARATOR} on the legacy {@code List<List<HalfMove>>} output).
 */
record ThreefoldExistingReport(ImmutableList<RepetitionGroup> groups) {

  public ThreefoldExistingReport {
    groups = Nulls.copyOfList(groups);
  }
}
