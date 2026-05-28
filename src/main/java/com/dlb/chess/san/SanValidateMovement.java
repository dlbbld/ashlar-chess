package com.dlb.chess.san;

import com.dlb.chess.board.enums.PieceType;
import com.dlb.chess.board.enums.Side;
import com.dlb.chess.common.constants.EnumConstants;

abstract class SanValidateMovement extends AbstractSan implements EnumConstants {

  public static void validateMovement(SanParse sanParse, Side havingMove) {
    final SanConversion sanConversion = sanParse.sanConversion();
    final SanFormat sanFormat = sanParse.sanFormat();

    if (sanConversion.movingPieceType() == PieceType.PAWN) {
      SanValidateMovementPawn.validatePawnMovement(havingMove, sanFormat, sanConversion);
      return;
    }

    if (sanConversion.movingPieceType() == PieceType.KING) {
      SanValidateMovementKing.validateKingMovement(sanParse);
      return;
    }

    SanValidateMovementRnbq.validateRnbqMovement(sanParse);
  }

}
