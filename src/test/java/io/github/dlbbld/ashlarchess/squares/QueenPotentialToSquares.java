// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.squares;

import java.util.Set;

import io.github.dlbbld.ashlarchess.board.StaticPosition;
import io.github.dlbbld.ashlarchess.board.enums.Side;
import io.github.dlbbld.ashlarchess.board.enums.Square;

public class QueenPotentialToSquares extends AbstractPotentialToSquares {

  public static Set<Square> calculateQueenPotentialToSquares(StaticPosition staticPosition, Square fromSquare,
      Side havingMove) {

    checkPiece(staticPosition, havingMove, fromSquare, QUEEN);

    return QueenRangeSquares.calculateQueenRangeSquares(staticPosition, fromSquare, havingMove, false);
  }

}
