package io.github.dlbbld.ashlarchess.squares;

import java.util.Set;

import io.github.dlbbld.ashlarchess.board.StaticPosition;
import io.github.dlbbld.ashlarchess.board.enums.Side;
import io.github.dlbbld.ashlarchess.board.enums.Square;
import io.github.dlbbld.ashlarchess.squares.KnightEmptyBoardSquares;

public class KnightPotentialToSquares extends AbstractPotentialToSquares {

  public static Set<Square> calculateKnightPotentialToSquares(StaticPosition staticPosition, Square fromSquare,
      Side havingMove) {

    final Set<Square> emptyBoardSquareSet = KnightEmptyBoardSquares.getKnightSquares(fromSquare);

    return calculateNonRangeNonPawnPotentialToSquares(staticPosition, fromSquare, KNIGHT, emptyBoardSquareSet,
        havingMove);
  }

}
