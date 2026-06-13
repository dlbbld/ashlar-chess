// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.san;

/**
 * Behaviour for the {@link SanFormat} value enum: the capture / non-capture classification that previously lived on the
 * enum itself.
 */
public abstract class SanFormatUtility {

  public static boolean isCapture(SanFormat sanFormat) {
    return switch (sanFormat) {
      case PAWN_CAPTURING_NON_PROMOTION, PAWN_CAPTURING_PROMOTION, RNBQ_CAPTURING_NEITHER, RNBQ_CAPTURING_FILE, RNBQ_CAPTURING_RANK, RNBQ_CAPTURING_SQUARE, KING_NON_CASTLING_CAPTURING -> true;
      case PAWN_NON_CAPTURING_NON_PROMOTION, PAWN_NON_CAPTURING_PROMOTION, RNBQ_NON_CAPTURING_NEITHER, RNBQ_NON_CAPTURING_FILE, RNBQ_NON_CAPTURING_RANK, RNBQ_NON_CAPTURING_SQUARE, KING_NON_CASTLING_NON_CAPTURING, KING_CASTLING_QUEEN_SIDE, KING_CASTLING_KING_SIDE -> false;
    };
  }

}
