// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.san;

import io.github.dlbbld.ashlarchess.board.enums.File;
import io.github.dlbbld.ashlarchess.board.enums.Rank;
import io.github.dlbbld.ashlarchess.board.enums.Side;
import io.github.dlbbld.ashlarchess.common.constants.EnumConstants;
import io.github.dlbbld.ashlarchess.messages.Message;

abstract class SanValidateMovementPawn extends AbstractSan implements EnumConstants {

  public static void validatePawnMovement(Side havingMove, SanFormat sanFormat, SanConversion sanConversion) {

    switch (sanFormat) {
      case KING_CASTLING_KING_SIDE:
      case KING_CASTLING_QUEEN_SIDE:
      case KING_NON_CASTLING_CAPTURING:
      case KING_NON_CASTLING_NON_CAPTURING:
        throw new IllegalArgumentException();
      case PAWN_NON_CAPTURING_NON_PROMOTION:
      case PAWN_NON_CAPTURING_PROMOTION: {
        validatePawnDestinationRank(havingMove, sanConversion.toSquare().getRank());
        break;
      }
      case PAWN_CAPTURING_NON_PROMOTION:
      case PAWN_CAPTURING_PROMOTION: {
        validatePawnDestinationRank(havingMove, sanConversion.toSquare().getRank());
        validatePawnCapturingDiagonal(havingMove, sanConversion.fromFile(), sanConversion.toSquare().getFile());
        break;
      }
      case RNBQ_CAPTURING_NEITHER:
      case RNBQ_NON_CAPTURING_NEITHER:
      case RNBQ_CAPTURING_FILE:
      case RNBQ_NON_CAPTURING_FILE:
      case RNBQ_CAPTURING_RANK:
      case RNBQ_NON_CAPTURING_RANK:
      case RNBQ_CAPTURING_SQUARE:
      case RNBQ_NON_CAPTURING_SQUARE:
      default:
        throw new IllegalArgumentException();
    }
  }

  private static void validatePawnDestinationRank(Side havingMove, Rank destinationRank) {
    final boolean isInvalid = !Rank.calculateIsValidRank(havingMove, destinationRank);
    if (isInvalid) {
      throw new SanValidationException(SanValidationProblem.MOVEMENT_PAWN_FORWARD_BACKWARDS,
          Message.getString("validation.san.movement.pawn.forward.backwards"));
    }
  }

  private static void validatePawnCapturingDiagonal(Side havingMove, File fromFile, File toFile) {
    final boolean isAdjacentLeft = File.calculateHasLeftFile(havingMove, fromFile)
        && File.calculateLeftFile(havingMove, fromFile) == toFile;
    final boolean isAdjacentRight = File.calculateHasRightFile(havingMove, fromFile)
        && File.calculateRightFile(havingMove, fromFile) == toFile;

    if (!isAdjacentLeft && !isAdjacentRight) {
      throw new SanValidationException(SanValidationProblem.MOVEMENT_PAWN_CAPTURE_NON_ADJACENT_FILE,
          Message.getString("validation.san.movement.pawn.capture.nonAdjacentFile"));
    }
  }

}
