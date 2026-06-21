// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.squares;

import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.PAWN;

import java.util.Set;

import io.github.dlbbld.ashlarchess.board.StaticPosition;
import io.github.dlbbld.ashlarchess.board.enums.Side;
import io.github.dlbbld.ashlarchess.board.enums.Square;

class PawnAttackedSquares {

  public static Set<Square> calculatePawnAttackedSquares(StaticPosition staticPosition, Square fromSquare,
      Side sideToMove) {

    ToSquaresSupport.checkPiece(staticPosition, sideToMove, fromSquare, PAWN);

    return PawnDiagonalSquares.getPawnDiagonalSquares(sideToMove, fromSquare);
  }
}
