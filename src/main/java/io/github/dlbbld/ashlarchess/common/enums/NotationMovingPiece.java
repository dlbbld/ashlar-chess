// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.common.enums;

import io.github.dlbbld.ashlarchess.board.enums.PieceType;
import io.github.dlbbld.ashlarchess.common.exceptions.ProgrammingMistakeException;

public enum NotationMovingPiece {
  ROOK(PieceType.ROOK),
  KNIGHT(PieceType.KNIGHT),
  BISHOP(PieceType.BISHOP),
  QUEEN(PieceType.QUEEN),
  KING(PieceType.KING);

  private final PieceType pieceType;

  public PieceType getPieceType() {
    return pieceType;
  }

  NotationMovingPiece(PieceType pieceType) {
    this.pieceType = pieceType;
  }

  public static boolean exists(char movingPieceLetter) {
    for (final NotationMovingPiece movingPiece : values()) {
      if (movingPiece.getPieceType().getLetter() == movingPieceLetter) {
        return true;
      }
    }
    return false;
  }

  public static NotationMovingPiece calculate(char movingPieceLetter) {
    if (!exists(movingPieceLetter)) {
      throw new IllegalArgumentException("For this letter no corresponding moving piece exists");
    }
    for (final NotationMovingPiece movingPiece : values()) {
      if (movingPiece.getPieceType().getLetter() == movingPieceLetter) {
        return movingPiece;
      }
    }
    throw new ProgrammingMistakeException();
  }
}
