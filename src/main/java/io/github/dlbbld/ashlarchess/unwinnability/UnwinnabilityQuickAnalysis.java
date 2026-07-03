// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.unwinnability;

/**
 * Public result of the quick unwinnability analysis: the three-valued verdict only ({@code UNWINNABLE},
 * {@code WINNABLE}, {@code POSSIBLY_WINNABLE} - see {@link UnwinnabilityQuickVerdict}). No mate line is exposed - the
 * quick analysis never advertises a winning sequence, even for a {@code WINNABLE} verdict.
 */
public record UnwinnabilityQuickAnalysis(UnwinnabilityQuickVerdict verdict) {
}
