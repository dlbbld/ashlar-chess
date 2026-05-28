package io.github.dlbbld.ashlarchess.squares;

import java.util.Set;

import io.github.dlbbld.ashlarchess.board.StaticPosition;
import io.github.dlbbld.ashlarchess.board.enums.Side;
import io.github.dlbbld.ashlarchess.board.enums.Square;

class QueenAttackedSquares extends AbstractAttackedSquares {

  public static Set<Square> calculateQueenAttackedSquares(StaticPosition staticPosition, Square fromSquare,
      Side havingMove) {

    checkPiece(staticPosition, havingMove, fromSquare, QUEEN);

    return QueenRangeSquares.calculateQueenRangeSquares(staticPosition, fromSquare, havingMove, true);
  }

}
