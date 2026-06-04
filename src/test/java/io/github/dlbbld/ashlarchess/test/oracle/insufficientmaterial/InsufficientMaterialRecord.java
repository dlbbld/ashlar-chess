// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.test.oracle.insufficientmaterial;

/**
 * One position's insufficient-material oracle. {@code fen} identifies the position; the three booleans are the
 * reference engine's verdicts for the combined predicate and each side (absolute white/black). Position-only - no game
 * history is needed, since insufficient material is a function of the position alone.
 */
public record InsufficientMaterialRecord(String pgn, String fen, boolean isInsufficientMaterial,
    boolean hasInsufficientMaterialWhite, boolean hasInsufficientMaterialBlack) {
}
