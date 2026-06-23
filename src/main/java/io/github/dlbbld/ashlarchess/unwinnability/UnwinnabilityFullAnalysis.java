// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.unwinnability;

import java.util.List;

import io.github.dlbbld.ashlarchess.board.UciMove;
import io.github.dlbbld.ashlarchess.internal.Nulls;

/**
 * Result of the complete unwinnability analysis: the public verdict plus proof detail for proven wins.
 *
 * <p>
 * A {@code WINNABLE} verdict can come either from the basic-helpmate-existence theorem or from a concrete helpmate
 * search. The public verdict stays coarse; {@link #isWinnableByTheorem()} distinguishes theorem wins, and
 * {@link #mateLine()} carries a witnessing UCI line only for searched wins.
 */
public record UnwinnabilityFullAnalysis(UnwinnabilityFullVerdict verdict, boolean isWinnableByTheorem,
    List<UciMove> mateLine) {

  public UnwinnabilityFullAnalysis {
    mateLine = Nulls.copyOfList(mateLine);
    if (verdict != UnwinnabilityFullVerdict.WINNABLE && isWinnableByTheorem) {
      throw new IllegalArgumentException("Only a WINNABLE analysis can be theorem-certified");
    }
    if (verdict != UnwinnabilityFullVerdict.WINNABLE && !mateLine.isEmpty()) {
      throw new IllegalArgumentException("Only a WINNABLE analysis can carry a mate line");
    }
    if (isWinnableByTheorem && !mateLine.isEmpty()) {
      throw new IllegalArgumentException("A theorem-certified win does not carry a mate line");
    }
  }
}
