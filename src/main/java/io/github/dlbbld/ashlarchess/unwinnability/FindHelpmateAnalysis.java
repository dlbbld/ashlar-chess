// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.unwinnability;

import java.util.List;

import io.github.dlbbld.ashlarchess.common.Nulls;
import io.github.dlbbld.ashlarchess.board.UciMove;

record FindHelpmateAnalysis(FindHelpmateResult findHelpmateResult, int localNodesCount, List<UciMove> mateLine) {
  FindHelpmateAnalysis {
    mateLine = Nulls.copyOfList(mateLine);
  }
}
