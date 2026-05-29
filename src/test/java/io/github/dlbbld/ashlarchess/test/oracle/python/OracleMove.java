// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.test.oracle.python;

public record OracleMove(String san, String lan, String uci, String fenAfter, int halfmoveClock, int fullmoveNumber,
    boolean isCheck, boolean isCheckmate, boolean isStalemate, boolean isInsufficientMaterial,
    boolean hasInsufficientMaterialWhite, boolean hasInsufficientMaterialBlack, boolean isRepetition2,
    boolean isRepetition3, boolean isRepetition4, boolean isFivefoldRepetition, boolean isFiftyMoves,
    boolean isSeventyFiveMoves, boolean canClaimThreefold, boolean canClaimFifty) {
}
