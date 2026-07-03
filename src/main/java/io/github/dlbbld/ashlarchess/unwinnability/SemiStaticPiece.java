// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.unwinnability;

import io.github.dlbbld.ashlarchess.board.enums.PieceType;
import io.github.dlbbld.ashlarchess.board.enums.Side;

/**
 * A piece of the semi-static analysis's position model ({@code fun22-spec.pdf} section 1): type, side, and the
 * square it currently occupies as an index {@code 0..63} ({@code a1 = 0, ..., h8 = 63}).
 */
record SemiStaticPiece(PieceType pieceType, Side side, int square) {
}
