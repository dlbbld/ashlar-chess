// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.squares;

import java.util.Set;

import io.github.dlbbld.ashlarchess.board.StaticPosition;
import io.github.dlbbld.ashlarchess.board.enums.Side;
import io.github.dlbbld.ashlarchess.board.enums.Square;

class RookRangeSquares extends AbstractRangeSquares {

  public static Set<Square> calculateRookRangeSquares(StaticPosition staticPosition, Square fromSquare, Side havingMove,
      boolean isAllowOwnPiece) {

    final RookRange emptyBoardRange = RookEmptyBoardSquares.getRookSquares(fromSquare);
    return calculateOrthogonalRangeSquare(staticPosition, havingMove, fromSquare, ROOK, emptyBoardRange,
        isAllowOwnPiece);
  }

}
