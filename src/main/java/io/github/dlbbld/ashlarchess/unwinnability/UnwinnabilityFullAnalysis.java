// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.unwinnability;

import java.util.List;

import io.github.dlbbld.ashlarchess.model.UciMove;

/**
 * Result of the complete unwinnability analysis: the verdict and, for {@code WINNABLE_HELPMATE}, a witnessing mate line
 * in UCI moves that checkmates the loser.
 *
 * <p>
 * The mate line is present only for {@code WINNABLE_HELPMATE} (the helpmate search found a concrete cooperative mate).
 * It is empty for {@code WINNABLE_BY_THEOREM} (winnability certified by the basic-helpmate-existence theorem without
 * searching for a sequence), and for {@code UNWINNABLE} and {@code UNDETERMINED}.
 */
public record UnwinnabilityFullAnalysis(UnwinnabilityFullVerdict verdict, List<UciMove> mateLine) {

}
