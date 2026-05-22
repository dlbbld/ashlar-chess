package com.dlb.chess.moves;

import java.util.Set;

import com.dlb.chess.board.StaticPosition;
import com.dlb.chess.board.enums.Piece;
import com.dlb.chess.board.enums.Side;
import com.dlb.chess.board.enums.Square;
import com.dlb.chess.model.LegalMove;
import com.dlb.chess.squares.BishopPotentialToSquares;

class BishopLegalMoves extends AbstractLegalMoves {
  public static Set<LegalMove> calculateBishopLegalMoves(StaticPosition staticPosition, Side havingMove,
      Square fromSquare) {

    final Piece movingPiece = staticPosition.get(fromSquare);
    checkPiece(havingMove, movingPiece, BISHOP);

    final Set<Square> toSquareSet = BishopPotentialToSquares.calculateBishopPotentialToSquares(staticPosition,
        fromSquare, havingMove);

    return calculateLegalMoveSet(staticPosition, havingMove, fromSquare, toSquareSet);
  }

}
