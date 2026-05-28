// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.san;

import io.github.dlbbld.ashlarchess.board.enums.CastlingRightLoss;
import io.github.dlbbld.ashlarchess.common.exceptions.ProgrammingMistakeException;
import io.github.dlbbld.ashlarchess.enums.CastlingCheck;

/**
 * Maps {@link CastlingCheck} + {@link CastlingRightLoss} to their counterparts in {@link SanValidationProblem}.
 *
 * <p>
 * The two input enums form orthogonal dimensions of the castling-failure space:
 * <ul>
 * <li>{@code CastlingCheck} = which chess-rule check failed;</li>
 * <li>{@code CastlingRightLoss} = why the castling right was lost (6 values + {@code NOT_LOST}).</li>
 * </ul>
 * {@code SanValidationProblem} flattens these dimensions into a single user-facing enum: the FINAL_NO_RIGHT case is
 * expanded across the 6 provenance values, while the 4 TEMPORARY cases stay flat (they have no provenance
 * sub-dimension). Consumers switching on {@code SanValidationProblem} get the whole story without drilling into
 * side-fields.
 *
 * <p>
 * Both inner switches are exhaustive (no {@code default:}) so any new value added to {@code CastlingCheck} or
 * {@code CastlingRightLoss} causes a compile error here.
 */
abstract class CastlingCheckMapper {

  public static SanValidationProblem map(CastlingCheck castlingCheck, CastlingRightLoss castlingRightLoss) {
    return switch (castlingCheck) {
      case FINAL_NO_RIGHT -> mapRightLoss(castlingRightLoss);
      case TEMPORARY_SQUARES_NOT_EMPTY -> SanValidationProblem.KING_CASTLING_TEMPORARY_SQUARES_NOT_EMPTY;
      case TEMPORARY_KING_IN_CHECK -> SanValidationProblem.KING_CASTLING_TEMPORARY_KING_IN_CHECK;
      case TEMPORARY_KING_TRAVELS_THROUGH_CHECK -> SanValidationProblem.KING_CASTLING_TEMPORARY_KING_TRAVELS_THROUGH_CHECK;
      case TEMPORARY_KING_ENDS_IN_CHECK -> SanValidationProblem.KING_CASTLING_TEMPORARY_KING_ENDS_IN_CHECK;
      case SUCCESS -> throw new ProgrammingMistakeException("SUCCESS is not a castling-refusal reason");
    };
  }

  private static SanValidationProblem mapRightLoss(CastlingRightLoss castlingRightLoss) {
    return switch (castlingRightLoss) {
      case KING_MOVED -> SanValidationProblem.KING_CASTLING_FINAL_NO_RIGHT_KING_MOVED;
      case ROOK_MOVED -> SanValidationProblem.KING_CASTLING_FINAL_NO_RIGHT_ROOK_MOVED;
      case ROOK_CAPTURED -> SanValidationProblem.KING_CASTLING_FINAL_NO_RIGHT_ROOK_CAPTURED;
      case CASTLED -> SanValidationProblem.KING_CASTLING_FINAL_NO_RIGHT_CASTLED;
      case UNKNOWN_FEN_IMPORT -> SanValidationProblem.KING_CASTLING_FINAL_NO_RIGHT_UNKNOWN_FEN_IMPORT;
      case NOT_LOST -> throw new ProgrammingMistakeException(
          "NOT_LOST is not a valid provenance for a FINAL_NO_RIGHT failure");
    };
  }

}
