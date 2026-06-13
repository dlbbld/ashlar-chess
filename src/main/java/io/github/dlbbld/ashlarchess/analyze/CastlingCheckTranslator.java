// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.analyze;

import io.github.dlbbld.ashlarchess.board.enums.CastlingRightLoss;
import io.github.dlbbld.ashlarchess.common.exceptions.ProgrammingMistakeException;
import io.github.dlbbld.ashlarchess.enums.CastlingCheck;
import io.github.dlbbld.ashlarchess.enums.MoveCheck;

/**
 * Translates a {@link CastlingCheck} result into the broader {@link MoveCheck} vocabulary. Used when a castling result
 * is surfaced through an {@code InvalidMoveException} or {@code SanValidationException}, both of which carry a
 * {@code MoveCheck}.
 *
 * <p>
 * The {@code castlingRightLoss} parameter is consulted only for {@code FINAL_NO_RIGHT}: the surfaced MoveCheck encodes
 * WHY the right was lost (KING_MOVED / ROOK_MOVED / ROOK_CAPTURED / CASTLED / UNKNOWN_FEN_IMPORT). For TEMPORARY_*
 * values the parameter is ignored.
 *
 * <p>
 * This is the {@link MoveCheck}-targeted sibling of {@code CastlingCheckMapper}, which maps the same input enums to the
 * SAN-facing {@code SanValidationProblem}. Both switches are exhaustive (no {@code default:}) so any new value added to
 * {@code CastlingCheck} or {@code CastlingRightLoss} causes a compile error here.
 */
public abstract class CastlingCheckTranslator {

  public static MoveCheck toMoveCheck(CastlingCheck castlingCheck, CastlingRightLoss castlingRightLoss) {
    return switch (castlingCheck) {
      case FINAL_NO_RIGHT -> mapFinalNoRight(castlingRightLoss);
      case TEMPORARY_SQUARES_NOT_EMPTY -> MoveCheck.KING_CASTLING_TEMPORARY_SQUARES_NOT_EMPTY;
      case TEMPORARY_KING_IN_CHECK -> MoveCheck.KING_CASTLING_TEMPORARY_KING_IN_CHECK;
      case TEMPORARY_KING_TRAVELS_THROUGH_CHECK -> MoveCheck.KING_CASTLING_TEMPORARY_KING_TRAVELS_THROUGH_CHECK;
      case TEMPORARY_KING_ENDS_IN_CHECK -> MoveCheck.KING_CASTLING_TEMPORARY_KING_ENDS_IN_CHECK;
      case SUCCESS -> throw new ProgrammingMistakeException("SUCCESS is not a castling-refusal reason");
    };
  }

  private static MoveCheck mapFinalNoRight(CastlingRightLoss castlingRightLoss) {
    return switch (castlingRightLoss) {
      case KING_MOVED -> MoveCheck.KING_CASTLING_FINAL_NO_RIGHT_KING_MOVED;
      case ROOK_MOVED -> MoveCheck.KING_CASTLING_FINAL_NO_RIGHT_ROOK_MOVED;
      case ROOK_CAPTURED -> MoveCheck.KING_CASTLING_FINAL_NO_RIGHT_ROOK_CAPTURED;
      case CASTLED -> MoveCheck.KING_CASTLING_FINAL_NO_RIGHT_CASTLED;
      case UNKNOWN_FEN_IMPORT -> MoveCheck.KING_CASTLING_FINAL_NO_RIGHT_UNKNOWN_FEN_IMPORT;
      case NOT_LOST -> throw new ProgrammingMistakeException(
          "NOT_LOST is not a valid provenance for a FINAL_NO_RIGHT failure");
    };
  }

}
