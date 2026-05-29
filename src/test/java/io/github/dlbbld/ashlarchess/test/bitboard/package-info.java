// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

/**
 * Differential tests asserting that {@link io.github.dlbbld.ashlarchess.bitboard.BitboardPosition} agrees bit-exact
 * with {@link io.github.dlbbld.ashlarchess.board.StaticPosition} across the full PGN/FEN corpus, primitive by
 * primitive.
 *
 * <p>
 * Tests under this package iterate the corpus via
 * {@link io.github.dlbbld.ashlarchess.test.pgn.setup.PgnTestCaseCatalog#getTestList} and run both representations
 * through the primitive under test, asserting agreement. Pure-unit-shaped bitboard tests (no corpus walk) live
 * alongside their subject under {@code io.github.dlbbld.ashlarchess.bitboard.*} instead.
 */
@NonNullByDefault
package io.github.dlbbld.ashlarchess.test.bitboard;

import org.eclipse.jdt.annotation.NonNullByDefault;
