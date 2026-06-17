// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.squares;

import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.KNIGHT;

import java.util.Set;

import io.github.dlbbld.ashlarchess.board.StaticPosition;
import io.github.dlbbld.ashlarchess.board.enums.Side;
import io.github.dlbbld.ashlarchess.board.enums.Square;

class KnightAttackedSquares {

  public static Set<Square> calculateKnightAttackedSquares(StaticPosition staticPosition, Square fromSquare,
      Side havingMove) {

    ToSquaresSupport.checkPiece(staticPosition, havingMove, fromSquare, KNIGHT);

    return KnightEmptyBoardSquares.getKnightSquares(fromSquare);
  }

}
