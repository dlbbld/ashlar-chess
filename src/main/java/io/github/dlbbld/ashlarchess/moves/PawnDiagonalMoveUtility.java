// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.moves;

import java.util.Set;

import io.github.dlbbld.ashlarchess.board.enums.Side;
import io.github.dlbbld.ashlarchess.board.enums.Square;
import io.github.dlbbld.ashlarchess.squares.PawnDiagonalSquares;

public abstract class PawnDiagonalMoveUtility {

  public static boolean calculateIsPawnDiagonalMove(Side side, Square fromSquare, Square toSquare) {
    final Set<Square> diagonalToSquareSet = PawnDiagonalSquares.getPawnDiagonalSquares(side, fromSquare);
    return diagonalToSquareSet.contains(toSquare);
  }
}
