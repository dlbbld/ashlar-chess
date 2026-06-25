// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.moves;

import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.KING;

import java.util.Set;

import io.github.dlbbld.ashlarchess.board.LegalMove;
import io.github.dlbbld.ashlarchess.board.StaticPosition;
import io.github.dlbbld.ashlarchess.board.enums.Piece;
import io.github.dlbbld.ashlarchess.board.enums.Side;
import io.github.dlbbld.ashlarchess.board.enums.Square;
import io.github.dlbbld.ashlarchess.squares.KingNonCastlingPotentialToSquares;

class KingNonCastlingLegalMoves extends KingLegalMoves {
  public static Set<LegalMove> calculateKingNonCastlingLegalMoves(StaticPosition staticPosition, Side sideToMove,
      Square fromSquare) {

    final Piece movingPiece = staticPosition.get(fromSquare);
    LegalMovesSupport.checkPiece(sideToMove, movingPiece, KING);

    final Set<Square> toSquareSet = KingNonCastlingPotentialToSquares
        .calculateKingNonCastlingPotentialToSquares(staticPosition, fromSquare, sideToMove);

    return LegalMovesSupport.calculateLegalMoveSet(staticPosition, sideToMove, fromSquare, toSquareSet);
  }

}
