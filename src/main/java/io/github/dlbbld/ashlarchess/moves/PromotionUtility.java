// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.moves;

import java.util.ArrayList;
import java.util.List;

import io.github.dlbbld.ashlarchess.board.enums.Piece;
import io.github.dlbbld.ashlarchess.board.enums.PromotionPieceTypeUtility;
import io.github.dlbbld.ashlarchess.board.enums.Side;
import io.github.dlbbld.ashlarchess.board.model.UpdateSquare;
import io.github.dlbbld.ashlarchess.common.model.MoveSpecification;

public final class PromotionUtility {

  private PromotionUtility() {
  }

  public static List<UpdateSquare> performPromotionMovements(Side havingMove, MoveSpecification moveSpecification) {

    final List<UpdateSquare> result = new ArrayList<>();

    result.add(new UpdateSquare(moveSpecification.fromSquare()));
    final Piece promotionPiece = PromotionPieceTypeUtility.calculate(havingMove,
        moveSpecification.promotionPieceType());
    result.add(new UpdateSquare(moveSpecification.toSquare(), promotionPiece));

    return result;
  }

}
