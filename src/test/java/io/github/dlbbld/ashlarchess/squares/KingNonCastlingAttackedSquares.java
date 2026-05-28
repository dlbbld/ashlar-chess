package io.github.dlbbld.ashlarchess.squares;

import java.util.Set;

import io.github.dlbbld.ashlarchess.board.StaticPosition;
import io.github.dlbbld.ashlarchess.board.enums.Side;
import io.github.dlbbld.ashlarchess.board.enums.Square;
import io.github.dlbbld.ashlarchess.squares.KingNonCastlingEmptyBoardSquares;

class KingNonCastlingAttackedSquares extends AbstractAttackedSquares {

  public static Set<Square> calculateKingNonCastlingAttackedSquares(StaticPosition staticPosition, Square fromSquare,
      Side havingMove) {

    checkPiece(staticPosition, havingMove, fromSquare, KING);

    return KingNonCastlingEmptyBoardSquares.getKingSquares(fromSquare);
  }

}
