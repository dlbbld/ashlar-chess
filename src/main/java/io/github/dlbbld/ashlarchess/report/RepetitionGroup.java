// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.report;

import com.google.common.collect.ImmutableList;

import io.github.dlbbld.ashlarchess.common.Nulls;
import io.github.dlbbld.ashlarchess.common.model.DynamicPosition;

/**
 * One position that reached the threefold-repetition threshold in the played history.
 *
 * <p>
 * {@code occurrences} are the played half-moves at which the position appeared, in chronological order.
 * {@code includesInitialPosition} is true when the position equals the game's initial position - in that case the
 * initial position itself is a hidden prior occurrence not present in {@code occurrences}, and the report formatter
 * prepends a "[Initial position]" marker. The compact constructor enforces
 * {@code totalRepetitionCount == occurrences.size() + (includesInitialPosition ? 1 : 0)} so an inconsistent group
 * cannot exist.
 */
record RepetitionGroup(DynamicPosition repeatedPosition, ImmutableList<MoveRecord> occurrences,
    boolean includesInitialPosition, int totalRepetitionCount) {

  public RepetitionGroup {
    occurrences = Nulls.copyOfList(occurrences);
    final int expectedTotal = occurrences.size() + (includesInitialPosition ? 1 : 0);
    if (totalRepetitionCount != expectedTotal) {
      throw new IllegalArgumentException(
          "totalRepetitionCount " + totalRepetitionCount + " disagrees with occurrences.size()=" + occurrences.size()
              + " and includesInitialPosition=" + includesInitialPosition);
    }
  }
}
