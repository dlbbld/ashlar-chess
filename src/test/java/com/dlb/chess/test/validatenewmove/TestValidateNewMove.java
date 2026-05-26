package com.dlb.chess.test.validatenewmove;

import org.junit.jupiter.api.Test;

import com.dlb.chess.board.Board;
import com.dlb.chess.board.enums.CastlingMove;
import com.dlb.chess.board.enums.PromotionPieceType;
import com.dlb.chess.common.model.MoveSpecification;
import com.dlb.chess.enums.MoveCheck;

/**
 * Surface-level smoke tests for {@link com.dlb.chess.board.ValidateNewMove#validateNewMove}: one representative
 * scenario per public {@link MoveCheck} failure value, exercising the full pipeline (Board → ValidateNewMove →
 * InvalidMoveException → MoveCheck).
 *
 * <p>
 * Comprehensive scenario coverage lives in {@code TestChessRuleAnalyzerScenarios} (analyzer-level tests).
 * Per-enum-value exhaustiveness checks live in {@code TestMovementCheck} and {@code TestKingSafetyCheck}. Tests here
 * verify that the surface translation/wiring works for every category, including the categories the analyzer does not
 * handle (MOVE_SPEC_*, KING_CASTLING_*).
 */
class TestValidateNewMove extends AbstractTestValidateNewMove {

  // --- MOVE_SPEC_* (spec coherence — surface only, not analyzer territory) ---

  @SuppressWarnings("static-method")
  @Test
  void testMoveSpecFromSquareEmpty() {
    check(new Board(), new MoveSpecification(E3, E4), MoveCheck.MOVE_SPEC_FROM_SQUARE_EMPTY);
  }

  @SuppressWarnings("static-method")
  @Test
  void testMoveSpecFromSquareOccupiedByOpponent() {
    check(new Board(), new MoveSpecification(E7, E5), MoveCheck.MOVE_SPEC_FROM_SQUARE_OCCUPIED_BY_OPPONENT);
  }

  @SuppressWarnings("static-method")
  @Test
  void testMoveSpecNonPawnPromotionPieceSet() {
    check(new Board(), new MoveSpecification(B1, C3, PromotionPieceType.QUEEN),
        MoveCheck.MOVE_SPEC_NON_PAWN_PROMOTION_PIECE_SET);
  }

  @SuppressWarnings("static-method")
  @Test
  void testMoveSpecPawnNonPromotionPromotionPiece() {
    check(new Board(), new MoveSpecification(E2, E4, PromotionPieceType.QUEEN),
        MoveCheck.MOVE_SPEC_PAWN_NON_PROMOTION_PROMOTION_PIECE);
  }

  @SuppressWarnings("static-method")
  @Test
  void testMoveSpecPawnPromotionNoPromotionPiece() {
    check("7k/4P3/8/8/8/8/8/4K3 w - - 0 1", new MoveSpecification(E7, E8),
        MoveCheck.MOVE_SPEC_PAWN_PROMOTION_NO_PROMOTION_PIECE);
  }

  // --- MOVEMENT_* (representative samples — comprehensive coverage in TestMovementCheck) ---

  @SuppressWarnings("static-method")
  @Test
  void testMovementNotPossible() {
    check(new Board(), new MoveSpecification(B1, B4), MoveCheck.MOVEMENT_NOT_POSSIBLE);
  }

  @SuppressWarnings("static-method")
  @Test
  void testMovementToSquareOccupiedByOwnPiece() {
    check(new Board(), new MoveSpecification(A1, A2), MoveCheck.MOVEMENT_TO_SQUARE_OCCUPIED_BY_OWN_PIECE);
  }

  @SuppressWarnings("static-method")
  @Test
  void testMovementLongRangePieceJumpsOverPiece() {
    check("7k/8/8/8/P7/8/8/R3K3 w - - 0 1", new MoveSpecification(A1, A8),
        MoveCheck.MOVEMENT_LONG_RANGE_PIECE_JUMPS_OVER_PIECE);
  }

  @SuppressWarnings("static-method")
  @Test
  void testMovementPawnForwardTwoSquare() {
    // jump-over occupied
    check("4k3/8/8/8/8/4n3/4P3/4K3 w - - 0 1", new MoveSpecification(E2, E4),
        MoveCheck.MOVEMENT_PAWN_FORWARD_TWO_SQUARE_JUMP_OVER_SQUARE_ONLY_NOT_EMPTY);
  }

  @SuppressWarnings("static-method")
  @Test
  void testMovementPawnForwardOneSquare() {
    // own piece blocks
    check("4k3/8/8/8/8/4N3/4P3/4K3 w - - 0 1", new MoveSpecification(E2, E3),
        MoveCheck.MOVEMENT_PAWN_FORWARD_ONE_SQUARE_TO_SQUARE_NOT_EMPTY_OWN_PIECE);
  }

  @SuppressWarnings("static-method")
  @Test
  void testMovementPawnDiagonalOwnPiece() {
    check("4k3/8/8/8/8/3N4/4P3/4K3 w - - 0 1", new MoveSpecification(E2, D3),
        MoveCheck.MOVEMENT_PAWN_DIAGONAL_OWN_PIECE);
  }

  @SuppressWarnings("static-method")
  @Test
  void testMovementPawnEnPassantWrongRank() {
    check("4k3/8/8/8/4P3/8/8/4K3 w - - 0 1", new MoveSpecification(E4, D5),
        MoveCheck.MOVEMENT_PAWN_EN_PASSANT_WRONG_RANK);
  }

  @SuppressWarnings("static-method")
  @Test
  void testMovementPawnEnPassantNoImmediateBeforeTwoSquareAdvance() {
    check("4k3/8/8/4P3/8/8/8/4K3 w - - 0 1", new MoveSpecification(E5, D6),
        MoveCheck.MOVEMENT_PAWN_EN_PASSANT_NO_IMMEDIATE_BEFORE_TWO_SQUARE_ADVANCE);
  }

  @SuppressWarnings("static-method")
  @Test
  void testKingCapturesGuardedPiece() {
    check("4k3/7p/8/b7/8/8/3b4/4K3 w - - 0 1", new MoveSpecification(E1, D2), MoveCheck.KING_CAPTURES_GUARDED_PIECE);
  }

  @SuppressWarnings("static-method")
  @Test
  void testKingMovesNextToOpponentKing() {
    check("r7/8/8/8/8/4k3/8/4K3 w - - 0 1", new MoveSpecification(E1, E2), MoveCheck.KING_MOVES_NEXT_TO_OPPONENT_KING);
  }

  // --- KING_MOVES_TO_ATTACKED_EMPTY_SQUARE (king-move to attacked empty destination) ---

  @SuppressWarnings("static-method")
  @Test
  void testKingMovesToAttackedEmptySquare() {
    check("4kr2/8/8/8/8/8/8/4K3 w - - 0 1", new MoveSpecification(E1, F1),
        MoveCheck.KING_MOVES_TO_ATTACKED_EMPTY_SQUARE);
  }

  // --- ALL_BUT_KING_KING_* (non-king-move king-safety) ---

  @SuppressWarnings("static-method")
  @Test
  void testAllButKingKingLeftInCheck() {
    check("4r2k/8/8/8/2N5/8/8/4K3 w - - 0 1", new MoveSpecification(C4, D6), MoveCheck.ALL_BUT_KING_KING_LEFT_IN_CHECK);
  }

  @SuppressWarnings("static-method")
  @Test
  void testAllButKingKingExposedToCheck() {
    check("4r2k/8/8/8/8/8/4N3/4K3 w - - 0 1", new MoveSpecification(E2, C3),
        MoveCheck.ALL_BUT_KING_KING_EXPOSED_TO_CHECK);
  }

  // --- KING_CASTLING_* (one representative per family — comprehensive coverage in TestValidateNewMoveCastling) ---

  @SuppressWarnings("static-method")
  @Test
  void testKingCastlingFinalNoRightUnknownFenImport() {
    check("rnbqk2r/pppp1ppp/5n2/2b1p3/2B1P3/5N2/PPPP1PPP/RNBQK2R w Qkq - 0 25",
        new MoveSpecification(CastlingMove.KING_SIDE), MoveCheck.KING_CASTLING_FINAL_NO_RIGHT_UNKNOWN_FEN_IMPORT);
  }

  @SuppressWarnings("static-method")
  @Test
  void testKingCastlingTemporarySquaresNotEmpty() {
    check("r1bqkbnr/pppppppp/2n5/8/8/5N2/PPPPPPPP/RNBQKB1R w KQkq - 0 25",
        new MoveSpecification(CastlingMove.KING_SIDE), MoveCheck.KING_CASTLING_TEMPORARY_SQUARES_NOT_EMPTY);
  }

  @SuppressWarnings("static-method")
  @Test
  void testKingCastlingTemporaryKingInCheck() {
    check("rnbqk2r/pppp1ppp/5n2/4p3/2B1P3/2N2N2/PPPP1bPP/R1BQK2R w KQkq - 0 5",
        new MoveSpecification(CastlingMove.KING_SIDE), MoveCheck.KING_CASTLING_TEMPORARY_KING_IN_CHECK);
  }

  @SuppressWarnings("static-method")
  @Test
  void testKingCastlingTemporaryKingTravelsThroughCheck() {
    check("rnb1kbnr/pppp2pp/5q2/8/2B5/7N/PPPP2PP/RNBQK2R w KQkq - 0 25", new MoveSpecification(CastlingMove.KING_SIDE),
        MoveCheck.KING_CASTLING_TEMPORARY_KING_TRAVELS_THROUGH_CHECK);
  }

  @SuppressWarnings("static-method")
  @Test
  void testKingCastlingTemporaryKingEndsInCheck() {
    check("rnbqk1nr/pppp1ppp/4p3/2b5/2B1P3/5P1N/PPPP2PP/RNBQK2R w KQkq - 0 25",
        new MoveSpecification(CastlingMove.KING_SIDE), MoveCheck.KING_CASTLING_TEMPORARY_KING_ENDS_IN_CHECK);
  }

}
