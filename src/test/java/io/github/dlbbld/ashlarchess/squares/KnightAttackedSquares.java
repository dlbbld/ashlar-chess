package io.github.dlbbld.ashlarchess.squares;

import java.util.Set;

import io.github.dlbbld.ashlarchess.board.StaticPosition;
import io.github.dlbbld.ashlarchess.board.enums.Side;
import io.github.dlbbld.ashlarchess.board.enums.Square;
import io.github.dlbbld.ashlarchess.squares.KnightEmptyBoardSquares;

class KnightAttackedSquares extends AbstractAttackedSquares {

  public static Set<Square> calculateKnightAttackedSquares(StaticPosition staticPosition, Square fromSquare,
      Side havingMove) {

    checkPiece(staticPosition, havingMove, fromSquare, KNIGHT);

    return KnightEmptyBoardSquares.getKnightSquares(fromSquare);
  }

}
