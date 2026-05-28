package io.github.dlbbld.ashlarchess.moves;

import java.util.Set;

import io.github.dlbbld.ashlarchess.board.StaticPosition;
import io.github.dlbbld.ashlarchess.board.enums.Piece;
import io.github.dlbbld.ashlarchess.board.enums.Side;
import io.github.dlbbld.ashlarchess.board.enums.Square;
import io.github.dlbbld.ashlarchess.model.LegalMove;
import io.github.dlbbld.ashlarchess.squares.KnightPotentialToSquares;

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
