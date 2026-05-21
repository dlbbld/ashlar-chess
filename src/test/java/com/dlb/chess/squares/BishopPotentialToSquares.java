package com.dlb.chess.squares;

import java.util.Set;

import com.dlb.chess.board.StaticPosition;
import com.dlb.chess.board.enums.Side;
import com.dlb.chess.board.enums.Square;

public class BishopPotentialToSquares extends AbstractPotentialToSquares {

  public static Set<Square> calculateBishopPotentialToSquares(StaticPosition staticPosition, Square fromSquare,
      Side havingMove) {

    checkPiece(staticPosition, havingMove, fromSquare, BISHOP);

    return BishopRangeSquares.calculateBishopRangeSquares(staticPosition, fromSquare, havingMove, false);

  }

}
