// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.model;

import io.github.dlbbld.ashlarchess.board.enums.PromotionPieceType;
import io.github.dlbbld.ashlarchess.board.enums.Square;

public record UciMove(Square fromSquare, Square toSquare, String text, boolean isPromotion,
    PromotionPieceType promotionPieceType) {

}
