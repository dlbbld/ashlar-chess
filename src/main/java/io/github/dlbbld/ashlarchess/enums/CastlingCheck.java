package io.github.dlbbld.ashlarchess.enums;

import io.github.dlbbld.ashlarchess.board.enums.CastlingRightLoss;
import io.github.dlbbld.ashlarchess.common.exceptions.ProgrammingMistakeException;

// Outcomes of the castling-specific check performed in CastlingUtility.
// FINAL means the castling right is permanently lost; TEMPORARY means the
// board state blocks castling now but castling may become possible again.
// The cases are overlapping so they are checked in the order listed.
public enum CastlingCheck {
  SUCCESS,
  FINAL_NO_RIGHT,
  TEMPORARY_SQUARES_NOT_EMPTY,
  TEMPORARY_KING_IN_CHECK,
  TEMPORARY_KING_TRAVELS_THROUGH_CHECK,
  TEMPORARY_KING_ENDS_IN_CHECK;

  // Translator to the broader MoveCheck enum. Used when a CastlingCheck result is surfaced through
  // an InvalidMoveException or SanValidationException, both of which carry a MoveCheck.
  //
  // The {@code castlingRightLoss} parameter is consulted only for {@code FINAL_NO_RIGHT}: the
  // surfaced MoveCheck encodes WHY the right was lost (KING_MOVED / ROOK_MOVED / ROOK_CAPTURED /
  // CASTLED / UNKNOWN_FEN_IMPORT). For TEMPORARY_* values the parameter is ignored.
  public MoveCheck toMoveCheck(CastlingRightLoss castlingRightLoss) {
    return switch (this) {
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
