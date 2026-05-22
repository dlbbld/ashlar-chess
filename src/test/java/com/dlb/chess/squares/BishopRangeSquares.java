package com.dlb.chess.squares;

import java.util.Set;

import com.dlb.chess.board.StaticPosition;
import com.dlb.chess.board.enums.Side;
import com.dlb.chess.board.enums.Square;

class BishopRangeSquares extends AbstractRangeSquares {

  public static Set<Square> calculateBishopRangeSquares(StaticPosition staticPosition, Square fromSquare,
      Side havingMove, boolean isAllowOwnPiece) {

    final BishopRange bishopRange = BishopEmptyBoardSquares.getBishopSquares(fromSquare);
    return calculateDiagonalRangeSquare(staticPosition, havingMove, fromSquare, BISHOP, bishopRange, isAllowOwnPiece);

  }

}
