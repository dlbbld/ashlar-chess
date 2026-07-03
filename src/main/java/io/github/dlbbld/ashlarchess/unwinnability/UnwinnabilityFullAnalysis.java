// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.unwinnability;

import java.util.List;

import io.github.dlbbld.ashlarchess.board.UciMove;
import io.github.dlbbld.ashlarchess.internal.Nulls;

/**
 * Result of the complete unwinnability analysis: the public verdict plus, for a {@code WINNABLE} verdict, the
 * witnessing cooperative-mate line.
 *
 * <p>
 * A {@code WINNABLE} verdict is always established by the Figure 5 search exhibiting a concrete helpmate;
 * {@link #mateLine()} carries the witnessing UCI line. The line is empty exactly in the zero-move case - the
 * submitted position is already a checkmate delivered by the intended winner. A non-winnable verdict never carries a
 * line.
 */
public record UnwinnabilityFullAnalysis(UnwinnabilityFullVerdict verdict, List<UciMove> mateLine) {

  public UnwinnabilityFullAnalysis {
    mateLine = Nulls.copyOfList(mateLine);
    if (verdict != UnwinnabilityFullVerdict.WINNABLE && !mateLine.isEmpty()) {
      throw new IllegalArgumentException("Only a WINNABLE verdict can carry a mate line");
    }
  }
}
