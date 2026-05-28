package io.github.dlbbld.ashlarchess.san;

import io.github.dlbbld.ashlarchess.board.enums.PieceType;
import io.github.dlbbld.ashlarchess.board.enums.Side;
import io.github.dlbbld.ashlarchess.common.constants.EnumConstants;

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
