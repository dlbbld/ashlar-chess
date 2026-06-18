// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.squares;

import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.QUEEN;

import java.util.Set;
import java.util.TreeSet;

import io.github.dlbbld.ashlarchess.board.StaticPosition;
import io.github.dlbbld.ashlarchess.board.enums.Side;
import io.github.dlbbld.ashlarchess.board.enums.Square;

class QueenRangeSquares {

  public static Set<Square> calculateQueenRangeSquares(StaticPosition staticPosition, Square fromSquare,
      Side sideToMove, boolean isAllowOwnPiece) {

    final QueenRange emptyBoardRange = QueenEmptyBoardSquares.getQueenSquares(fromSquare);

    final Set<Square> result = new TreeSet<>(RangeSquaresSupport.calculateOrthogonalRangeSquare(staticPosition,
        sideToMove, fromSquare, QUEEN, emptyBoardRange, isAllowOwnPiece));
    result.addAll(RangeSquaresSupport.calculateDiagonalRangeSquare(staticPosition, sideToMove, fromSquare, QUEEN,
        emptyBoardRange, isAllowOwnPiece));

    return result;
  }

}
