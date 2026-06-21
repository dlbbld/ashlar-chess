// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.squares;

import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.BISHOP;

import java.util.Set;

import io.github.dlbbld.ashlarchess.board.StaticPosition;
import io.github.dlbbld.ashlarchess.board.enums.Side;
import io.github.dlbbld.ashlarchess.board.enums.Square;

public class BishopPotentialToSquares {

  public static Set<Square> calculateBishopPotentialToSquares(StaticPosition staticPosition, Square fromSquare,
      Side sideToMove) {

    ToSquaresSupport.checkPiece(staticPosition, sideToMove, fromSquare, BISHOP);

    return BishopRangeSquares.calculateBishopRangeSquares(staticPosition, fromSquare, sideToMove, false);

  }

}
