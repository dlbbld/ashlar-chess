package com.dlb.chess.squares;

import java.util.Set;

import com.dlb.chess.board.StaticPosition;
import com.dlb.chess.board.enums.Side;
import com.dlb.chess.board.enums.Square;

class QueenAttackedSquares extends AbstractAttackedSquares {

  public static Set<Square> calculateQueenAttackedSquares(StaticPosition staticPosition, Square fromSquare,
      Side havingMove) {

    checkPiece(staticPosition, havingMove, fromSquare, QUEEN);

    return QueenRangeSquares.calculateQueenRangeSquares(staticPosition, fromSquare, havingMove, true);
  }

}
