// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.unwinnability;

import java.util.List;

import io.github.dlbbld.ashlarchess.board.UciMove;

/**
 * Outcome of one bounded {@link FindHelpmate} search (one iterative-deepening iteration of Figure 9).
 *
 * @param helpmateFound whether a checkmate by the intended winner was exhibited
 * @param interrupted   whether a search limit (node count or depth budget) was hit - Figure 9 step 5
 * @param nodesUsed     nodes expanded by this search, charged against the global budget
 * @param mateLine      the exhibited mate line when {@code helpmateFound}, otherwise empty
 */
record HelpmateSearchResult(boolean helpmateFound, boolean interrupted, int nodesUsed, List<UciMove> mateLine) {
}
