// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.squares;

import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.KNIGHT;

import java.util.Set;

import io.github.dlbbld.ashlarchess.board.StaticPosition;
import io.github.dlbbld.ashlarchess.board.enums.Side;
import io.github.dlbbld.ashlarchess.board.enums.Square;

public class KnightPotentialToSquares {

  public static Set<Square> calculateKnightPotentialToSquares(StaticPosition staticPosition, Square fromSquare,
      Side havingMove) {

    final Set<Square> emptyBoardSquareSet = KnightEmptyBoardSquares.getKnightSquares(fromSquare);

    return PotentialToSquaresSupport.calculateNonRangeNonPawnPotentialToSquares(staticPosition, fromSquare, KNIGHT,
        emptyBoardSquareSet, havingMove);
  }

}
