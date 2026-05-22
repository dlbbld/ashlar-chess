package com.dlb.chess.squares;

import java.util.Set;

import com.dlb.chess.board.StaticPosition;
import com.dlb.chess.board.enums.Side;
import com.dlb.chess.board.enums.Square;

class RookRangeSquares extends AbstractRangeSquares {

  public static Set<Square> calculateRookRangeSquares(StaticPosition staticPosition, Square fromSquare, Side havingMove,
      boolean isAllowOwnPiece) {

    final RookRange emptyBoardRange = RookEmptyBoardSquares.getRookSquares(fromSquare);
    return calculateOrthogonalRangeSquare(staticPosition, havingMove, fromSquare, ROOK, emptyBoardRange,
        isAllowOwnPiece);
  }

}
