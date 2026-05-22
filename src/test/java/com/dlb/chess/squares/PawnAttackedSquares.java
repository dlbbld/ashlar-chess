package com.dlb.chess.squares;

import java.util.Set;

import com.dlb.chess.board.StaticPosition;
import com.dlb.chess.board.enums.Side;
import com.dlb.chess.board.enums.Square;

class PawnAttackedSquares extends AbstractAttackedSquares {

  public static Set<Square> calculatePawnAttackedSquares(StaticPosition staticPosition, Square fromSquare,
      Side havingMove) {

    checkPiece(staticPosition, havingMove, fromSquare, PAWN);

    return PawnDiagonalSquares.getPawnDiagonalSquares(havingMove, fromSquare);
  }
}
