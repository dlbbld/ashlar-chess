// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.moves;

import java.util.Set;

import io.github.dlbbld.ashlarchess.board.StaticPosition;
import io.github.dlbbld.ashlarchess.board.enums.Piece;
import io.github.dlbbld.ashlarchess.board.enums.Side;
import io.github.dlbbld.ashlarchess.board.enums.Square;
import io.github.dlbbld.ashlarchess.model.LegalMove;
import io.github.dlbbld.ashlarchess.squares.QueenPotentialToSquares;

class QueenLegalMoves extends AbstractLegalMoves {
  public static Set<LegalMove> calculateQueenLegalMoves(StaticPosition staticPosition, Side havingMove,
      Square fromSquare) {

    final Piece movingPiece = staticPosition.get(fromSquare);
    checkPiece(havingMove, movingPiece, QUEEN);

    final Set<Square> toSquareSet = QueenPotentialToSquares.calculateQueenPotentialToSquares(staticPosition, fromSquare,
        havingMove);

    return calculateLegalMoveSet(staticPosition, havingMove, fromSquare, toSquareSet);
  }
}
