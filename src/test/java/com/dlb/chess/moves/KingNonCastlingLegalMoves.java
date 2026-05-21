package com.dlb.chess.moves;

import java.util.Set;

import com.dlb.chess.board.StaticPosition;
import com.dlb.chess.board.enums.Piece;
import com.dlb.chess.board.enums.Side;
import com.dlb.chess.board.enums.Square;
import com.dlb.chess.model.LegalMove;
import com.dlb.chess.squares.KingNonCastlingPotentialToSquares;

class KingNonCastlingLegalMoves extends KingLegalMoves {
  public static Set<LegalMove> calculateKingNonCastlingLegalMoves(StaticPosition staticPosition, Side havingMove,
      Square fromSquare) {

    final Piece movingPiece = staticPosition.get(fromSquare);
    checkPiece(havingMove, movingPiece, KING);

    final Set<Square> toSquareSet = KingNonCastlingPotentialToSquares
        .calculateKingNonCastlingPotentialToSquares(staticPosition, fromSquare, havingMove);

    return calculateLegalMoveSet(staticPosition, havingMove, fromSquare, toSquareSet);
  }

}
