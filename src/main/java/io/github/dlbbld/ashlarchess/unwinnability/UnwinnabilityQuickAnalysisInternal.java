// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.unwinnability;

import java.util.List;

import io.github.dlbbld.ashlarchess.model.UciMove;

/**
 * Internal result of the quick unwinnability search: the three-valued internal verdict together with the mate line for
 * a winnable result. The public {@link UnwinnabilityQuickAnalysis} exposes only the (two-valued) verdict; the mate line
 * stays internal.
 */
record UnwinnabilityQuickAnalysisInternal(UnwinnabilityQuickVerdictInternal verdict, List<UciMove> mateLine) {
}
