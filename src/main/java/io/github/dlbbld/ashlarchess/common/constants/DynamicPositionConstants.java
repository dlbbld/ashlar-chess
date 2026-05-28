package io.github.dlbbld.ashlarchess.common.constants;

import io.github.dlbbld.ashlarchess.bitboard.BitboardPosition;
import io.github.dlbbld.ashlarchess.board.enums.Square;
import io.github.dlbbld.ashlarchess.common.model.DynamicPosition;
import io.github.dlbbld.ashlarchess.fen.constants.FenConstants;

public class DynamicPositionConstants {

  public static final DynamicPosition INITIAL = new DynamicPosition(FenConstants.FEN_INITIAL.havingMove(),
      BitboardPosition.INITIAL_POSITION, Square.NONE, FenConstants.FEN_INITIAL.castlingRightWhite(),
      FenConstants.FEN_INITIAL.castlingRightBlack());
}
