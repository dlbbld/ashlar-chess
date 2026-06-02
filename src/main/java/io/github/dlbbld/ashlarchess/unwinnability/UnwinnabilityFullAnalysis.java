// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.unwinnability;

import java.util.List;

import io.github.dlbbld.ashlarchess.model.UciMove;

/**
 * Result of the complete unwinnability analysis: the verdict and, when the verdict is {@code WINNABLE} and the
 * helpmate search produced one, a witnessing mate line in UCI moves.
 *
 * <p>
 * The mate line is empty for {@code UNWINNABLE} and {@code UNDETERMINED}, and also for a {@code WINNABLE} verdict that
 * was certified without an explicit sequence - currently the basic-checkmate-reachability theorem, which proves
 * winnability for elementary material without searching for a concrete mate. A non-empty mate line always checkmates
 * the loser; an empty mate line on a {@code WINNABLE} verdict means "winnable, line not computed", not "no line
 * exists".
 */
public record UnwinnabilityFullAnalysis(UnwinnabilityFullVerdict verdict, List<UciMove> mateLine) {

}
