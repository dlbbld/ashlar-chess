// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.squares;

import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.KING;

import java.util.Set;

import io.github.dlbbld.ashlarchess.board.StaticPosition;
import io.github.dlbbld.ashlarchess.board.enums.Side;
import io.github.dlbbld.ashlarchess.board.enums.Square;

public class KingNonCastlingPotentialToSquares {

  public static Set<Square> calculateKingNonCastlingPotentialToSquares(StaticPosition staticPosition, Square fromSquare,
      Side sideToMove) {

    final Set<Square> emptyBoardSquareSet = KingNonCastlingEmptyBoardSquares.getKingSquares(fromSquare);

    return PotentialToSquaresSupport.calculateNonRangeNonPawnPotentialToSquares(staticPosition, fromSquare, KING,
        emptyBoardSquareSet, sideToMove);
  }

}
