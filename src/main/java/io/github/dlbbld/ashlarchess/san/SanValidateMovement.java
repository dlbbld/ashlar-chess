// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.san;

import io.github.dlbbld.ashlarchess.board.enums.PieceType;
import io.github.dlbbld.ashlarchess.board.enums.Side;
import io.github.dlbbld.ashlarchess.san.internal.SanConversion;
import io.github.dlbbld.ashlarchess.san.internal.SanFormat;
import io.github.dlbbld.ashlarchess.san.internal.SanParse;

final class SanValidateMovement {

  private SanValidateMovement() {
  }

  public static void validateMovement(SanParse sanParse, Side sideToMove) {
    final SanConversion sanConversion = sanParse.sanConversion();
    final SanFormat sanFormat = sanParse.sanFormat();

    if (sanConversion.movingPieceType() == PieceType.PAWN) {
      SanValidateMovementPawn.validatePawnMovement(sideToMove, sanFormat, sanConversion);
      return;
    }

    if (sanConversion.movingPieceType() == PieceType.KING) {
      SanValidateMovementKing.validateKingMovement(sanParse);
      return;
    }

    SanValidateMovementRnbq.validateRnbqMovement(sanParse);
  }

}
