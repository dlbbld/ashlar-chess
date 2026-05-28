package io.github.dlbbld.ashlarchess.squares;

import java.util.Set;

import io.github.dlbbld.ashlarchess.board.StaticPosition;
import io.github.dlbbld.ashlarchess.board.enums.Side;
import io.github.dlbbld.ashlarchess.board.enums.Square;
import io.github.dlbbld.ashlarchess.squares.BishopEmptyBoardSquares;
import io.github.dlbbld.ashlarchess.squares.BishopRange;

class BishopRangeSquares extends AbstractRangeSquares {

  public static Set<Square> calculateBishopRangeSquares(StaticPosition staticPosition, Square fromSquare,
      Side havingMove, boolean isAllowOwnPiece) {

    final BishopRange bishopRange = BishopEmptyBoardSquares.getBishopSquares(fromSquare);
    return calculateDiagonalRangeSquare(staticPosition, havingMove, fromSquare, BISHOP, bishopRange, isAllowOwnPiece);

  }

}
