// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.analyze;

import io.github.dlbbld.ashlarchess.common.exceptions.ProgrammingMistakeException;
import io.github.dlbbld.ashlarchess.enums.MoveCheck;
import io.github.dlbbld.ashlarchess.enums.MovementCheck;

/**
 * Translates a {@link MovementCheck} failure into the broader {@link MoveCheck} vocabulary, used when a movement
 * refusal is surfaced through {@code InvalidMoveException} for MoveCheck public-API stability.
 *
 * <p>
 * The switch is exhaustive (no {@code default:}) so any new value added to {@code MovementCheck} causes a compile error
 * here.
 */
public abstract class MovementCheckTranslator {

  public static MoveCheck toMoveCheck(MovementCheck movementCheck) {
    return switch (movementCheck) {
      case NOT_POSSIBLE -> MoveCheck.MOVEMENT_NOT_POSSIBLE;
      case TO_SQUARE_OCCUPIED_BY_OWN_PIECE -> MoveCheck.MOVEMENT_TO_SQUARE_OCCUPIED_BY_OWN_PIECE;
      case LONG_RANGE_PIECE_JUMPS_OVER_PIECE -> MoveCheck.MOVEMENT_LONG_RANGE_PIECE_JUMPS_OVER_PIECE;
      case PAWN_FORWARD_TWO_SQUARE_JUMP_OVER_SQUARE_ONLY_NOT_EMPTY -> MoveCheck.MOVEMENT_PAWN_FORWARD_TWO_SQUARE_JUMP_OVER_SQUARE_ONLY_NOT_EMPTY;
      case PAWN_FORWARD_TWO_SQUARE_TO_SQUARE_ONLY_NOT_EMPTY -> MoveCheck.MOVEMENT_PAWN_FORWARD_TWO_SQUARE_TO_SQUARE_ONLY_NOT_EMPTY;
      case PAWN_FORWARD_TWO_SQUARE_BOTH_SQUARE_NOT_EMPTY -> MoveCheck.MOVEMENT_PAWN_FORWARD_TWO_SQUARE_BOTH_SQUARE_NOT_EMPTY;
      case PAWN_FORWARD_ONE_SQUARE_TO_SQUARE_NOT_EMPTY_OWN_PIECE -> MoveCheck.MOVEMENT_PAWN_FORWARD_ONE_SQUARE_TO_SQUARE_NOT_EMPTY_OWN_PIECE;
      case PAWN_FORWARD_ONE_SQUARE_TO_SQUARE_NOT_EMPTY_OPPONENT_PIECE -> MoveCheck.MOVEMENT_PAWN_FORWARD_ONE_SQUARE_TO_SQUARE_NOT_EMPTY_OPPONENT_PIECE;
      case PAWN_DIAGONAL_OWN_PIECE -> MoveCheck.MOVEMENT_PAWN_DIAGONAL_OWN_PIECE;
      case PAWN_EN_PASSANT_WRONG_RANK -> MoveCheck.MOVEMENT_PAWN_EN_PASSANT_WRONG_RANK;
      case PAWN_EN_PASSANT_NO_IMMEDIATE_BEFORE_TWO_SQUARE_ADVANCE -> MoveCheck.MOVEMENT_PAWN_EN_PASSANT_NO_IMMEDIATE_BEFORE_TWO_SQUARE_ADVANCE;
      case KING_CAPTURES_GUARDED_PIECE -> MoveCheck.KING_CAPTURES_GUARDED_PIECE;
      case KING_MOVES_NEXT_TO_OPPONENT_KING -> MoveCheck.KING_MOVES_NEXT_TO_OPPONENT_KING;
      case KING_MOVES_TO_ATTACKED_EMPTY_SQUARE -> MoveCheck.KING_MOVES_TO_ATTACKED_EMPTY_SQUARE;
      case SUCCESS -> throw new ProgrammingMistakeException("SUCCESS is not a movement-refusal reason");
    };
  }

}
