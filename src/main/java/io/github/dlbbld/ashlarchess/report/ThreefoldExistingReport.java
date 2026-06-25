// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.report;

import java.util.List;

import io.github.dlbbld.ashlarchess.internal.Nulls;

/**
 * All positions that reached the threefold-repetition threshold in the played history.
 *
 * <p>
 * Groups are ordered by the move count of each group's first occurrence (matching the inlined first-occurrence sort in
 * {@link RepetitionGrouping#calculateRepetitionGroups} on the {@code List<List<MoveRecord>>} output).
 */
record ThreefoldExistingReport(List<RepetitionGroup> groups) {

  public ThreefoldExistingReport {
    groups = Nulls.copyOfList(groups);
  }
}
