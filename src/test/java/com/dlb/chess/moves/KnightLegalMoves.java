package com.dlb.chess.moves;

import java.util.Set;

import com.dlb.chess.board.StaticPosition;
import com.dlb.chess.board.enums.Piece;
import com.dlb.chess.board.enums.Side;
import com.dlb.chess.board.enums.Square;
import com.dlb.chess.model.LegalMove;
import com.dlb.chess.squares.KnightPotentialToSquares;

class KnightLegalMoves extends AbstractLegalMoves {
  public static Set<LegalMove> calculateKnightLegalMoves(StaticPosition staticPosition, Side havingMove,
      Square fromSquare) {

    final Piece movingPiece = staticPosition.get(fromSquare);
    checkPiece(havingMove, movingPiece, KNIGHT);

    final Set<Square> toSquareSet = KnightPotentialToSquares.calculateKnightPotentialToSquares(staticPosition,
        fromSquare, havingMove);

    return calculateLegalMoveSet(staticPosition, havingMove, fromSquare, toSquareSet);
  }

}
