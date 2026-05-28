package io.github.dlbbld.ashlarchess.squares;

import java.util.Set;

import io.github.dlbbld.ashlarchess.board.StaticPosition;
import io.github.dlbbld.ashlarchess.board.enums.Side;
import io.github.dlbbld.ashlarchess.board.enums.Square;

class RookAttackedSquares extends AbstractAttackedSquares {

  public static Set<Square> calculateRookAttackedSquares(StaticPosition staticPosition, Square fromSquare,
      Side havingMove) {

    checkPiece(staticPosition, havingMove, fromSquare, ROOK);

    return RookRangeSquares.calculateRookRangeSquares(staticPosition, fromSquare, havingMove, true);
  }

}
