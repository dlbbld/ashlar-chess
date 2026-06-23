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
 * The public {@code verdict} stays coarse. {@link #winnableProof()} says how a {@code WINNABLE} verdict was established
 * - {@link WinnableProof#THEOREM} for the basic-helpmate-existence theorem (no line) or {@link WinnableProof#HELPMATE}
 * for a concrete search ({@link #mateLine()} carries the witnessing UCI line) - and is {@link WinnableProof#NONE} for a
 * non-winnable verdict.
 */
public record UnwinnabilityFullAnalysis(UnwinnabilityFullVerdict verdict, WinnableProof winnableProof,
    List<UciMove> mateLine) {

  public UnwinnabilityFullAnalysis {
    mateLine = Nulls.copyOfList(mateLine);
    if ((verdict == UnwinnabilityFullVerdict.WINNABLE) == (winnableProof == WinnableProof.NONE)) {
      throw new IllegalArgumentException("winnableProof must be NONE exactly when the verdict is not WINNABLE");
    }
    if (winnableProof == WinnableProof.THEOREM && !mateLine.isEmpty()) {
      throw new IllegalArgumentException("A theorem-certified win does not carry a mate line");
    }
    if (winnableProof == WinnableProof.HELPMATE && mateLine.isEmpty()) {
      throw new IllegalArgumentException("A searched (helpmate) win must carry a mate line");
    }
    if (winnableProof == WinnableProof.NONE && !mateLine.isEmpty()) {
      throw new IllegalArgumentException("Only a WINNABLE analysis can carry a mate line");
    }
  }
}
