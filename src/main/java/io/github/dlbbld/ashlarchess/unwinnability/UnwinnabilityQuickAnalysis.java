// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.unwinnability;

/**
 * Public result of the quick unwinnability analysis: the two-valued verdict only. No mate line is exposed - the quick
 * analysis never advertises a winning sequence (any internally computed line stays in
 * {@code UnwinnabilityQuickAnalysisInternal}).
 */
public record UnwinnabilityQuickAnalysis(UnwinnabilityQuickVerdict verdict) {
}
