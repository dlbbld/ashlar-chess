// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.san;

import io.github.dlbbld.ashlarchess.san.internal.SanFormat;
import io.github.dlbbld.ashlarchess.san.internal.SanConversion;
import io.github.dlbbld.ashlarchess.board.enums.File;
import io.github.dlbbld.ashlarchess.board.enums.Rank;
import io.github.dlbbld.ashlarchess.board.enums.internal.RankUtility;
import io.github.dlbbld.ashlarchess.board.enums.Side;
import io.github.dlbbld.ashlarchess.messages.Message;

final class SanValidateMovementPawn {

  private SanValidateMovementPawn() {
  }

  public static void validatePawnMovement(Side sideToMove, SanFormat sanFormat, SanConversion sanConversion) {

    switch (sanFormat) {
      case KING_CASTLING_KING_SIDE:
      case KING_CASTLING_QUEEN_SIDE:
      case KING_NON_CASTLING_CAPTURING:
      case KING_NON_CASTLING_NON_CAPTURING:
        throw new IllegalArgumentException();
      case PAWN_NON_CAPTURING_NON_PROMOTION:
      case PAWN_NON_CAPTURING_PROMOTION: {
        validatePawnDestinationRank(sideToMove, sanConversion.toSquare().getRank());
        break;
      }
      case PAWN_CAPTURING_NON_PROMOTION:
      case PAWN_CAPTURING_PROMOTION: {
        validatePawnDestinationRank(sideToMove, sanConversion.toSquare().getRank());
        validatePawnCapturingDiagonal(sideToMove, sanConversion.fromFile(), sanConversion.toSquare().getFile());
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

  private static void validatePawnDestinationRank(Side sideToMove, Rank destinationRank) {
    final boolean isInvalid = !RankUtility.isValidRank(sideToMove, destinationRank);
    if (isInvalid) {
      throw new SanValidationException(SanValidationProblem.MOVEMENT_PAWN_FORWARD_BACKWARDS,
          Message.getString("validation.san.movement.pawn.forward.backwards"));
    }
  }

  private static void validatePawnCapturingDiagonal(Side sideToMove, File fromFile, File toFile) {
    final boolean isAdjacentLeft = fromFile.hasLeftFile(sideToMove) && fromFile.getLeftFile(sideToMove) == toFile;
    final boolean isAdjacentRight = fromFile.hasRightFile(sideToMove) && fromFile.getRightFile(sideToMove) == toFile;

    if (!isAdjacentLeft && !isAdjacentRight) {
      throw new SanValidationException(SanValidationProblem.MOVEMENT_PAWN_CAPTURE_NON_ADJACENT_FILE,
          Message.getString("validation.san.movement.pawn.capture.nonAdjacentFile"));
    }
  }

}
