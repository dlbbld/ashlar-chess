package com.dlb.chess.common.constants;

import com.dlb.chess.bitboard.BitboardPosition;
import com.dlb.chess.board.enums.Square;
import com.dlb.chess.common.model.DynamicPosition;
import com.dlb.chess.fen.constants.FenConstants;

public class DynamicPositionConstants {

  public static final DynamicPosition INITIAL = new DynamicPosition(FenConstants.FEN_INITIAL.havingMove(),
      BitboardPosition.INITIAL_POSITION, Square.NONE, FenConstants.FEN_INITIAL.castlingRightWhite(),
      FenConstants.FEN_INITIAL.castlingRightBlack());
}
