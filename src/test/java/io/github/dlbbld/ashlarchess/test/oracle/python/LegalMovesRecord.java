// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.test.oracle.python;

import java.util.List;

/**
 * One PGN's per-position legal-moves oracle. {@code perPly} has one entry per position visited starting from the
 * initial position: index 0 is the legal-move set at {@code startFen} (before any move), index N is the set after the
 * Nth played move. So the list length is {@code plyCount + 1}.
 */
public record LegalMovesRecord(String pgn, List<LegalMovesPly> perPly) {
}
