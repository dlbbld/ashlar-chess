package io.github.dlbbld.ashlarchess.moves;

import java.util.Set;

import io.github.dlbbld.ashlarchess.board.StaticPosition;
import io.github.dlbbld.ashlarchess.board.enums.Piece;
import io.github.dlbbld.ashlarchess.board.enums.Side;
import io.github.dlbbld.ashlarchess.board.enums.Square;
import io.github.dlbbld.ashlarchess.model.LegalMove;
import io.github.dlbbld.ashlarchess.squares.BishopPotentialToSquares;

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
