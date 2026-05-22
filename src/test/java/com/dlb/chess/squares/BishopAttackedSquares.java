package com.dlb.chess.squares;

import java.util.Set;

import com.dlb.chess.board.StaticPosition;
import com.dlb.chess.board.enums.Side;
import com.dlb.chess.board.enums.Square;

class BishopAttackedSquares extends AbstractAttackedSquares {

  public static Set<Square> calculateBishopAttackedSquares(StaticPosition staticPosition, Square fromSquare,
      Side havingMove) {

    checkPiece(staticPosition, havingMove, fromSquare, BISHOP);

    return BishopRangeSquares.calculateBishopRangeSquares(staticPosition, fromSquare, havingMove, true);

  }

}
