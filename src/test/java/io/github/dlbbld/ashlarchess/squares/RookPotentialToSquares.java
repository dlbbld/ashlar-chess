package io.github.dlbbld.ashlarchess.squares;

import java.util.Set;

import io.github.dlbbld.ashlarchess.board.StaticPosition;
import io.github.dlbbld.ashlarchess.board.enums.Side;
import io.github.dlbbld.ashlarchess.board.enums.Square;

public class RookPotentialToSquares extends AbstractPotentialToSquares {

  public static Set<Square> calculateRookPotentialToSquares(StaticPosition staticPosition, Square fromSquare,
      Side havingMove) {

    checkPiece(staticPosition, havingMove, fromSquare, ROOK);

    return RookRangeSquares.calculateRookRangeSquares(staticPosition, fromSquare, havingMove, false);
  }

}
