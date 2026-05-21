package com.dlb.chess.squares;

import java.util.Set;

import com.dlb.chess.board.StaticPosition;
import com.dlb.chess.board.enums.Side;
import com.dlb.chess.board.enums.Square;

public class KnightPotentialToSquares extends AbstractPotentialToSquares {

  public static Set<Square> calculateKnightPotentialToSquares(StaticPosition staticPosition, Square fromSquare,
      Side havingMove) {

    final Set<Square> emptyBoardSquareSet = KnightEmptyBoardSquares.getKnightSquares(fromSquare);

    return calculateNonRangeNonPawnPotentialToSquares(staticPosition, fromSquare, KNIGHT, emptyBoardSquareSet,
        havingMove);
  }

}
