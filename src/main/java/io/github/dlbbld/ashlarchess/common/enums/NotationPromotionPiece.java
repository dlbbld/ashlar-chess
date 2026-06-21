// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.common.enums;

import io.github.dlbbld.ashlarchess.board.enums.PromotionPieceType;
import io.github.dlbbld.ashlarchess.common.exceptions.ProgrammingMistakeException;

public enum NotationPromotionPiece {
  ROOK(PromotionPieceType.ROOK),
  KNIGHT(PromotionPieceType.KNIGHT),
  BISHOP(PromotionPieceType.BISHOP),
  QUEEN(PromotionPieceType.QUEEN);

  private final PromotionPieceType promotionPieceType;

  NotationPromotionPiece(PromotionPieceType promotionPieceType) {
    this.promotionPieceType = promotionPieceType;
  }

  public PromotionPieceType getPromotionPieceType() {
    return promotionPieceType;
  }

  public static boolean exists(char promotionPieceLetter) {
    for (final NotationPromotionPiece option : values()) {
      if (option.getPromotionPieceType().getPieceType().getLetter() == promotionPieceLetter) {
        return true;
      }
    }
    return false;
  }

  public static NotationPromotionPiece parse(char promotionPieceLetter) {
    if (!exists(promotionPieceLetter)) {
      throw new IllegalArgumentException("For this letter not ignoring case no corresponding enum exists");
    }
    for (final NotationPromotionPiece option : values()) {
      if (option.getPromotionPieceType().getPieceType().getLetter() == promotionPieceLetter) {
        return option;
      }
    }
    throw new ProgrammingMistakeException();
  }

}
