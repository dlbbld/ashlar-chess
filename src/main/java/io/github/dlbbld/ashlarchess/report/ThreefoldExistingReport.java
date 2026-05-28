// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.report;

import com.google.common.collect.ImmutableList;

import io.github.dlbbld.ashlarchess.common.Nulls;

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
