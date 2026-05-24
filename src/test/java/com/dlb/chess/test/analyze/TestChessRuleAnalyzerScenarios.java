package com.dlb.chess.test.analyze;

import org.junit.jupiter.api.Test;

import com.dlb.chess.board.Board;
import com.dlb.chess.board.enums.PromotionPieceType;
import com.dlb.chess.common.model.MoveSpecification;
import com.dlb.chess.enums.MoveCheck;

class TestChessRuleAnalyzerScenarios extends AbstractTestChessRuleAnalyzerScenarios {

  @Test
  @SuppressWarnings("static-method")
  void testWhiteMovingPiece() {
    final Board board = new Board();
    MoveSpecification move = new MoveSpecification(E3, E4);

    check(board, move, MoveCheck.MOVE_SPEC_FROM_SQUARE_EMPTY);

    move = new MoveSpecification(A3, H6);
    check(board, move, MoveCheck.MOVE_SPEC_FROM_SQUARE_EMPTY);

    move = new MoveSpecification(D4, D8);
    check(board, move, MoveCheck.MOVE_SPEC_FROM_SQUARE_EMPTY);

    move = new MoveSpecification(E7, E6);
    check(board, move, MoveCheck.MOVE_SPEC_FROM_SQUARE_OCCUPIED_BY_OPPONENT);

    move = new MoveSpecification(D8, D6);
    check(board, move, MoveCheck.MOVE_SPEC_FROM_SQUARE_OCCUPIED_BY_OPPONENT);

    move = new MoveSpecification(E8, E7);
    check(board, move, MoveCheck.MOVE_SPEC_FROM_SQUARE_OCCUPIED_BY_OPPONENT);

    move = new MoveSpecification(B2, B3);
    board.move(move);

    move = new MoveSpecification(B7, B6);
    board.move(move);

    // rook movement
    move = new MoveSpecification(A1, B2);
    check(board, move, MoveCheck.MOVEMENT_NOT_POSSIBLE);

    // rook movement
    move = new MoveSpecification(H1, G3);
    check(board, move, MoveCheck.MOVEMENT_NOT_POSSIBLE);

    // rook movement
    move = new MoveSpecification(A1, B8);
    check(board, move, MoveCheck.MOVEMENT_NOT_POSSIBLE);

    // knight movement
    move = new MoveSpecification(B1, B4);
    check(board, move, MoveCheck.MOVEMENT_NOT_POSSIBLE);

    // knight movement
    move = new MoveSpecification(B1, B2);
    check(board, move, MoveCheck.MOVEMENT_NOT_POSSIBLE);

    move = new MoveSpecification(C2, C4);
    board.move(move);

    move = new MoveSpecification(C7, C5);
    board.move(move);

    // knight movement
    move = new MoveSpecification(B1, C2);
    check(board, move, MoveCheck.MOVEMENT_NOT_POSSIBLE);

    move = new MoveSpecification(B1, C3);
    board.move(move);

    move = new MoveSpecification(B8, C6);
    board.move(move);

    // bishop movement
    move = new MoveSpecification(C1, C2);
    check(board, move, MoveCheck.MOVEMENT_NOT_POSSIBLE);

    // bishop movement
    move = new MoveSpecification(F1, C3);
    check(board, move, MoveCheck.MOVEMENT_NOT_POSSIBLE);

    // bishop movement
    move = new MoveSpecification(C1, C8);
    check(board, move, MoveCheck.MOVEMENT_NOT_POSSIBLE);

    // queen movement
    move = new MoveSpecification(D1, B2);
    check(board, move, MoveCheck.MOVEMENT_NOT_POSSIBLE);

    // queen movement
    move = new MoveSpecification(D1, H3);
    check(board, move, MoveCheck.MOVEMENT_NOT_POSSIBLE);

    // queen movement
    move = new MoveSpecification(D1, C8);
    check(board, move, MoveCheck.MOVEMENT_NOT_POSSIBLE);

    move = new MoveSpecification(E2, E4);
    board.move(move);

    move = new MoveSpecification(E7, E5);
    board.move(move);

    // king movement
    move = new MoveSpecification(E1, E3);
    check(board, move, MoveCheck.MOVEMENT_NOT_POSSIBLE);

    // king movement
    move = new MoveSpecification(E1, F3);
    check(board, move, MoveCheck.MOVEMENT_NOT_POSSIBLE);

    // king movement
    move = new MoveSpecification(E1, A8);
    check(board, move, MoveCheck.MOVEMENT_NOT_POSSIBLE);

    // pawn movement
    move = new MoveSpecification(A2, A5);
    check(board, move, MoveCheck.MOVEMENT_NOT_POSSIBLE);

    // pawn movement
    move = new MoveSpecification(A2, B4);
    check(board, move, MoveCheck.MOVEMENT_NOT_POSSIBLE);

    // pawn movement
    move = new MoveSpecification(A2, A8);
    check(board, move, MoveCheck.MOVEMENT_NOT_POSSIBLE);

    // pawn movement
    move = new MoveSpecification(A2, A3, PromotionPieceType.QUEEN);
    check(board, move, MoveCheck.MOVE_SPEC_PAWN_NON_PROMOTION_PROMOTION_PIECE);

    // pawn movement
    move = new MoveSpecification(A2, A8, PromotionPieceType.QUEEN);
    check(board, move, MoveCheck.MOVEMENT_NOT_POSSIBLE);

  }

  @Test
  @SuppressWarnings("static-method")
  void testWhiteCapturingNonPawn() {

    // capturing own pieces
    final Board board = new Board();

    // rook
    MoveSpecification move = new MoveSpecification(A1, A2);
    check(board, move, MoveCheck.MOVEMENT_TO_SQUARE_OCCUPIED_BY_OWN_PIECE);

    move = new MoveSpecification(A1, B1);
    check(board, move, MoveCheck.MOVEMENT_TO_SQUARE_OCCUPIED_BY_OWN_PIECE);

    // knight
    move = new MoveSpecification(B1, D2);
    check(board, move, MoveCheck.MOVEMENT_TO_SQUARE_OCCUPIED_BY_OWN_PIECE);

    move = new MoveSpecification(A2, A3);
    board.move(move);

    move = new MoveSpecification(E7, E5);
    board.move(move);

    move = new MoveSpecification(B1, A3);
    check(board, move, MoveCheck.MOVEMENT_TO_SQUARE_OCCUPIED_BY_OWN_PIECE);

    // bishop
    move = new MoveSpecification(C1, B2);
    check(board, move, MoveCheck.MOVEMENT_TO_SQUARE_OCCUPIED_BY_OWN_PIECE);

    move = new MoveSpecification(D2, D4);
    board.move(move);

    move = new MoveSpecification(D7, D5);
    board.move(move);

    move = new MoveSpecification(D1, D2);
    board.move(move);

    move = new MoveSpecification(D8, D7);
    board.move(move);

    move = new MoveSpecification(C1, D2);
    check(board, move, MoveCheck.MOVEMENT_TO_SQUARE_OCCUPIED_BY_OWN_PIECE);

    // queen
    move = new MoveSpecification(D2, C1);
    check(board, move, MoveCheck.MOVEMENT_TO_SQUARE_OCCUPIED_BY_OWN_PIECE);

    // queen tries to capture the own king
    move = new MoveSpecification(D2, E1);
    check(board, move, MoveCheck.MOVEMENT_TO_SQUARE_OCCUPIED_BY_OWN_PIECE);

    move = new MoveSpecification(D2, C2);
    check(board, move, MoveCheck.MOVEMENT_TO_SQUARE_OCCUPIED_BY_OWN_PIECE);

    // king
    move = new MoveSpecification(E1, D2);
    check(board, move, MoveCheck.MOVEMENT_TO_SQUARE_OCCUPIED_BY_OWN_PIECE);

    move = new MoveSpecification(E1, F1);
    check(board, move, MoveCheck.MOVEMENT_TO_SQUARE_OCCUPIED_BY_OWN_PIECE);

  }

  @Test
  @SuppressWarnings("static-method")
  void testWhiteCapturingPawn() {

    Board board = new Board("rnb1kbnr/pppq1ppp/8/3pp3/3P4/P7/1PPQPPPP/RNB1KBNR w KQkq - 2 4");

    // pawn
    MoveSpecification move = new MoveSpecification(A3, A4);
    board.move(move);

    move = new MoveSpecification(A7, A5);
    board.move(move);

    move = new MoveSpecification(B2, B4);
    board.move(move);

    move = new MoveSpecification(D7, D8);
    board.move(move);

    move = new MoveSpecification(B4, A5);
    board.move(move);

    move = new MoveSpecification(D8, D7);
    board.move(move);

    move = new MoveSpecification(A4, A5);
    check(board, move, MoveCheck.MOVEMENT_PAWN_FORWARD_ONE_SQUARE_TO_SQUARE_NOT_EMPTY_OWN_PIECE);

    move = new MoveSpecification(D4, E5);
    board.move(move);

    move = new MoveSpecification(D7, D8);
    board.move(move);

    move = new MoveSpecification(E2, E4);
    board.move(move);

    move = new MoveSpecification(D8, D7);
    board.move(move);

    move = new MoveSpecification(E4, E5);
    check(board, move, MoveCheck.MOVEMENT_PAWN_FORWARD_ONE_SQUARE_TO_SQUARE_NOT_EMPTY_OWN_PIECE);

    move = new MoveSpecification(H2, H4);
    board.move(move);

    move = new MoveSpecification(H7, H5);
    board.move(move);

    move = new MoveSpecification(H4, H5);
    check(board, move, MoveCheck.MOVEMENT_PAWN_FORWARD_ONE_SQUARE_TO_SQUARE_NOT_EMPTY_OPPONENT_PIECE);

    move = new MoveSpecification(A5, A6);
    board.move(move);

    move = new MoveSpecification(B7, A6);
    board.move(move);

    move = new MoveSpecification(A4, A5);
    board.move(move);

    move = new MoveSpecification(D7, D8);
    board.move(move);

    move = new MoveSpecification(A5, A6);
    check(board, move, MoveCheck.MOVEMENT_PAWN_FORWARD_ONE_SQUARE_TO_SQUARE_NOT_EMPTY_OPPONENT_PIECE);

    move = new MoveSpecification(F2, F3);
    board.move(move);

    move = new MoveSpecification(D8, D7);
    board.move(move);

    move = new MoveSpecification(F3, E4);
    check(board, move, MoveCheck.MOVEMENT_PAWN_DIAGONAL_OWN_PIECE);

    move = new MoveSpecification(C2, C3);
    board.move(move);

    move = new MoveSpecification(F8, B4);
    board.move(move);

    move = new MoveSpecification(C3, B4);
    board.move(move);

    move = new MoveSpecification(D7, D8);
    board.move(move);

    move = new MoveSpecification(B4, A5);
    check(board, move, MoveCheck.MOVEMENT_PAWN_DIAGONAL_OWN_PIECE);

    move = new MoveSpecification(B4, C5);
    check(board, move, MoveCheck.MOVEMENT_PAWN_EN_PASSANT_WRONG_RANK);

    move = new MoveSpecification(E4, F5);
    check(board, move, MoveCheck.MOVEMENT_PAWN_EN_PASSANT_WRONG_RANK);

    move = new MoveSpecification(F3, G4);
    check(board, move, MoveCheck.MOVEMENT_PAWN_EN_PASSANT_WRONG_RANK);

    move = new MoveSpecification(H4, G5);
    check(board, move, MoveCheck.MOVEMENT_PAWN_EN_PASSANT_WRONG_RANK);

    move = new MoveSpecification(F3, F4);
    board.move(move);

    move = new MoveSpecification(D8, D7);
    board.move(move);

    move = new MoveSpecification(F4, F5);
    board.move(move);

    move = new MoveSpecification(D7, D8);
    board.move(move);

    move = new MoveSpecification(F5, E6);
    check(board, move, MoveCheck.MOVEMENT_PAWN_EN_PASSANT_NO_IMMEDIATE_BEFORE_TWO_SQUARE_ADVANCE);

    move = new MoveSpecification(D2, H6);
    board.move(move);

    move = new MoveSpecification(D8, D7);
    board.move(move);

    move = new MoveSpecification(H6, H5);
    board.move(move);

    move = new MoveSpecification(D7, D8);
    board.move(move);

    move = new MoveSpecification(H5, H7);
    board.move(move);

    move = new MoveSpecification(D8, D7);
    board.move(move);

    move = new MoveSpecification(H4, H5);
    board.move(move);

    move = new MoveSpecification(D7, D8);
    board.move(move);

    move = new MoveSpecification(F5, E6);
    check(board, move, MoveCheck.MOVEMENT_PAWN_EN_PASSANT_NO_IMMEDIATE_BEFORE_TWO_SQUARE_ADVANCE);

    move = new MoveSpecification(H5, G6);
    check(board, move, MoveCheck.MOVEMENT_PAWN_EN_PASSANT_NO_IMMEDIATE_BEFORE_TWO_SQUARE_ADVANCE);

    move = new MoveSpecification(A5, B6);
    check(board, move, MoveCheck.MOVEMENT_PAWN_EN_PASSANT_NO_IMMEDIATE_BEFORE_TWO_SQUARE_ADVANCE);

    move = new MoveSpecification(E5, F6);
    check(board, move, MoveCheck.MOVEMENT_PAWN_EN_PASSANT_NO_IMMEDIATE_BEFORE_TWO_SQUARE_ADVANCE);

    move = new MoveSpecification(F5, G6);
    check(board, move, MoveCheck.MOVEMENT_PAWN_EN_PASSANT_NO_IMMEDIATE_BEFORE_TWO_SQUARE_ADVANCE);

    // pawn moves
    board = new Board();

    move = new MoveSpecification(E2, E4);
    board.move(move);

    move = new MoveSpecification(D7, D6);
    board.move(move);

    move = new MoveSpecification(E4, E5);
    board.move(move);

    move = new MoveSpecification(D6, D5);
    board.move(move);

    move = new MoveSpecification(E5, D6);
    check(board, move, MoveCheck.MOVEMENT_PAWN_EN_PASSANT_NO_IMMEDIATE_BEFORE_TWO_SQUARE_ADVANCE);

    move = new MoveSpecification(H2, H4);
    board.move(move);

    move = new MoveSpecification(G7, G6);
    board.move(move);

    move = new MoveSpecification(H4, H5);
    board.move(move);

    move = new MoveSpecification(G6, G5);
    board.move(move);

    move = new MoveSpecification(E1, E2);
    board.move(move);

    move = new MoveSpecification(D8, D7);
    board.move(move);

    move = new MoveSpecification(H5, G6);
    check(board, move, MoveCheck.MOVEMENT_PAWN_EN_PASSANT_NO_IMMEDIATE_BEFORE_TWO_SQUARE_ADVANCE);

    move = new MoveSpecification(A2, A4);
    board.move(move);

    move = new MoveSpecification(B7, B6);
    board.move(move);

    move = new MoveSpecification(A4, A5);
    board.move(move);

    move = new MoveSpecification(B6, B5);
    board.move(move);

    move = new MoveSpecification(A5, B6);
    check(board, move, MoveCheck.MOVEMENT_PAWN_EN_PASSANT_NO_IMMEDIATE_BEFORE_TWO_SQUARE_ADVANCE);

    move = new MoveSpecification(D1, E1);
    board.move(move);

    move = new MoveSpecification(F7, F6);
    board.move(move);

    move = new MoveSpecification(E1, D1);
    board.move(move);

    move = new MoveSpecification(F6, F5);
    board.move(move);

    move = new MoveSpecification(E5, F6);
    check(board, move, MoveCheck.MOVEMENT_PAWN_EN_PASSANT_NO_IMMEDIATE_BEFORE_TWO_SQUARE_ADVANCE);

    // pawn moves
    board = new Board();

    move = new MoveSpecification(B2, B4);
    board.move(move);

    move = new MoveSpecification(C7, C5);
    board.move(move);

    move = new MoveSpecification(B4, B5);
    board.move(move);

    move = new MoveSpecification(A7, A5);
    board.move(move);

    move = new MoveSpecification(B5, C6);
    check(board, move, MoveCheck.MOVEMENT_PAWN_EN_PASSANT_NO_IMMEDIATE_BEFORE_TWO_SQUARE_ADVANCE);

    move = new MoveSpecification(B5, A6);
    board.move(move);

    move = new MoveSpecification(D8, C7);
    board.move(move);

    move = new MoveSpecification(H2, H4);
    board.move(move);

    move = new MoveSpecification(C7, D8);
    board.move(move);

    move = new MoveSpecification(H4, H5);
    board.move(move);

    move = new MoveSpecification(G7, G5);
    board.move(move);

    move = new MoveSpecification(E2, E4);
    board.move(move);

    move = new MoveSpecification(E7, E5);
    board.move(move);

    move = new MoveSpecification(H5, G6);
    check(board, move, MoveCheck.MOVEMENT_PAWN_EN_PASSANT_NO_IMMEDIATE_BEFORE_TWO_SQUARE_ADVANCE);

  }

  @Test
  @SuppressWarnings("static-method")
  void testWhitePawnTwoSquareAdvanceMoves() {
    // pawn two advance moves

    final Board board = new Board();

    MoveSpecification move = new MoveSpecification(B1, C3);
    board.move(move);

    move = new MoveSpecification(B8, C6);
    board.move(move);

    move = new MoveSpecification(C2, C4);
    check(board, move, MoveCheck.MOVEMENT_PAWN_FORWARD_TWO_SQUARE_JUMP_OVER_SQUARE_ONLY_NOT_EMPTY);

    move = new MoveSpecification(E2, E4);
    board.move(move);

    move = new MoveSpecification(E7, E5);
    board.move(move);

    move = new MoveSpecification(F1, C4);
    board.move(move);

    move = new MoveSpecification(F8, C5);
    board.move(move);

    move = new MoveSpecification(C3, A4);
    board.move(move);

    move = new MoveSpecification(C6, A5);
    board.move(move);

    move = new MoveSpecification(C2, C4);
    check(board, move, MoveCheck.MOVEMENT_PAWN_FORWARD_TWO_SQUARE_TO_SQUARE_ONLY_NOT_EMPTY);

    move = new MoveSpecification(A4, C3);
    board.move(move);

    move = new MoveSpecification(A5, C6);
    board.move(move);

    move = new MoveSpecification(C2, C4);
    check(board, move, MoveCheck.MOVEMENT_PAWN_FORWARD_TWO_SQUARE_BOTH_SQUARE_NOT_EMPTY);
  }

  @Test
  @SuppressWarnings("static-method")
  void testWhiteKingMoves() {

    Board board = new Board();

    // king
    board = new Board();

    MoveSpecification move = new MoveSpecification(B1, A3);
    board.move(move);

    move = new MoveSpecification(B8, C6);
    board.move(move);

    move = new MoveSpecification(A3, B1);
    board.move(move);

    move = new MoveSpecification(C6, D4);
    board.move(move);

    move = new MoveSpecification(B1, A3);
    board.move(move);

    move = new MoveSpecification(G8, F6);
    board.move(move);

    move = new MoveSpecification(A3, B1);
    board.move(move);

    move = new MoveSpecification(F6, H5);
    board.move(move);

    move = new MoveSpecification(B1, A3);
    board.move(move);

    move = new MoveSpecification(H5, F4);
    board.move(move);

    move = new MoveSpecification(A3, B1);
    board.move(move);

    move = new MoveSpecification(D4, E2);
    board.move(move);

    move = new MoveSpecification(E1, E2);
    check(board, move, MoveCheck.KING_CAPTURES_GUARDED_PIECE);

    move = new MoveSpecification(D2, D3);
    board.move(move);

    move = new MoveSpecification(F4, H3);
    board.move(move);

    move = new MoveSpecification(B1, A3);
    board.move(move);

    move = new MoveSpecification(E7, E5);
    board.move(move);

    move = new MoveSpecification(A3, B1);
    board.move(move);

    move = new MoveSpecification(F8, C5);
    board.move(move);

    move = new MoveSpecification(B1, A3);
    board.move(move);

    move = new MoveSpecification(C5, F2);
    board.move(move);

    move = new MoveSpecification(E1, F2);
    check(board, move, MoveCheck.KING_CAPTURES_GUARDED_PIECE);

    move = new MoveSpecification(E1, D2);
    board.move(move);

    move = new MoveSpecification(D8, H4);
    board.move(move);

    move = new MoveSpecification(A3, B1);
    board.move(move);

    move = new MoveSpecification(H4, D4);
    board.move(move);

    move = new MoveSpecification(B1, A3);
    board.move(move);

    move = new MoveSpecification(D4, G4);
    board.move(move);

    move = new MoveSpecification(D2, E2);
    check(board, move, MoveCheck.KING_CAPTURES_GUARDED_PIECE);

    // kings walk on the middle of the board to move next to each other
    board = new Board();

    move = new MoveSpecification(E2, E4);
    board.move(move);

    move = new MoveSpecification(E7, E5);
    board.move(move);

    move = new MoveSpecification(E1, E2);
    board.move(move);

    move = new MoveSpecification(E8, E7);
    board.move(move);

    move = new MoveSpecification(E2, E3);
    board.move(move);

    move = new MoveSpecification(E7, E6);
    board.move(move);

    move = new MoveSpecification(G1, F3);
    board.move(move);

    move = new MoveSpecification(G8, H6);
    board.move(move);

    move = new MoveSpecification(F3, E5);
    board.move(move);

    move = new MoveSpecification(H6, G8);
    board.move(move);

    move = new MoveSpecification(E3, D4);
    board.move(move);

    move = new MoveSpecification(G8, H6);
    board.move(move);

    move = new MoveSpecification(D4, D5);
    check(board, move, MoveCheck.KING_MOVES_NEXT_TO_OPPONENT_KING);

    move = new MoveSpecification(E5, F3);
    board.move(move);

    move = new MoveSpecification(H6, G8);
    board.move(move);

    move = new MoveSpecification(D4, E5);
    check(board, move, MoveCheck.KING_MOVES_NEXT_TO_OPPONENT_KING);

    move = new MoveSpecification(D4, C4);
    board.move(move);

    move = new MoveSpecification(G8, E7);
    board.move(move);

    move = new MoveSpecification(C4, C5);
    board.move(move);

    move = new MoveSpecification(H7, H6);
    board.move(move);

    move = new MoveSpecification(C5, D6);
    check(board, move, MoveCheck.KING_MOVES_NEXT_TO_OPPONENT_KING);

  }

  @Test
  @SuppressWarnings("static-method")
  void testWhitePromotionPieceWronglySet() {

    Board board = new Board();
    // non pawn move - promotion piece set
    board = new Board();

    MoveSpecification move = new MoveSpecification(A2, A4);
    board.move(move);

    move = new MoveSpecification(A7, A5);
    board.move(move);

    move = new MoveSpecification(D2, D4);
    board.move(move);

    move = new MoveSpecification(D7, D5);
    board.move(move);

    move = new MoveSpecification(E2, E4);
    board.move(move);

    move = new MoveSpecification(E7, E5);
    board.move(move);

    // rook
    move = new MoveSpecification(A1, A2, PromotionPieceType.QUEEN);
    check(board, move, MoveCheck.MOVE_SPEC_NON_PAWN_PROMOTION_PIECE_SET);

    // knight
    move = new MoveSpecification(B1, C3, PromotionPieceType.QUEEN);
    check(board, move, MoveCheck.MOVE_SPEC_NON_PAWN_PROMOTION_PIECE_SET);

    // bishop light square
    move = new MoveSpecification(F1, C4, PromotionPieceType.QUEEN);
    check(board, move, MoveCheck.MOVE_SPEC_NON_PAWN_PROMOTION_PIECE_SET);

    // bishop dark square
    move = new MoveSpecification(C1, F4, PromotionPieceType.QUEEN);
    check(board, move, MoveCheck.MOVE_SPEC_NON_PAWN_PROMOTION_PIECE_SET);

    // queen
    move = new MoveSpecification(D1, D2, PromotionPieceType.QUEEN);
    check(board, move, MoveCheck.MOVE_SPEC_NON_PAWN_PROMOTION_PIECE_SET);

    // king
    move = new MoveSpecification(E1, E2, PromotionPieceType.QUEEN);
    check(board, move, MoveCheck.MOVE_SPEC_NON_PAWN_PROMOTION_PIECE_SET);

    // pawn move - promotion piece checks with promotion by capture
    board = new Board();

    move = new MoveSpecification(E2, E3, PromotionPieceType.QUEEN);
    check(board, move, MoveCheck.MOVE_SPEC_PAWN_NON_PROMOTION_PROMOTION_PIECE);

    move = new MoveSpecification(E2, E4, PromotionPieceType.QUEEN);
    check(board, move, MoveCheck.MOVE_SPEC_PAWN_NON_PROMOTION_PROMOTION_PIECE);

    move = new MoveSpecification(E2, E4);
    board.move(move);

    move = new MoveSpecification(D7, D5);
    board.move(move);

    move = new MoveSpecification(E4, D5, PromotionPieceType.KNIGHT);
    check(board, move, MoveCheck.MOVE_SPEC_PAWN_NON_PROMOTION_PROMOTION_PIECE);

    move = new MoveSpecification(E4, D5);
    board.move(move);

    move = new MoveSpecification(C7, C5);
    board.move(move);

    // en passant capture
    move = new MoveSpecification(D5, C6, PromotionPieceType.BISHOP);
    check(board, move, MoveCheck.MOVE_SPEC_PAWN_NON_PROMOTION_PROMOTION_PIECE);

    move = new MoveSpecification(D5, C6);
    board.move(move);

    move = new MoveSpecification(H7, H6);
    board.move(move);

    move = new MoveSpecification(C6, B7);
    board.move(move);

    move = new MoveSpecification(H6, H5);
    board.move(move);

    // promotion with capture
    move = new MoveSpecification(B7, A8);
    check(board, move, MoveCheck.MOVE_SPEC_PAWN_PROMOTION_NO_PROMOTION_PIECE);

    move = new MoveSpecification(B7, A8, PromotionPieceType.ROOK);
    board.move(move);

    // pawn move - promotion piece checks with promotion without capture
    board = new Board();

    move = new MoveSpecification(D2, D3, PromotionPieceType.QUEEN);
    check(board, move, MoveCheck.MOVE_SPEC_PAWN_NON_PROMOTION_PROMOTION_PIECE);

    move = new MoveSpecification(D2, D4, PromotionPieceType.ROOK);
    check(board, move, MoveCheck.MOVE_SPEC_PAWN_NON_PROMOTION_PROMOTION_PIECE);

    move = new MoveSpecification(D2, D4);
    board.move(move);

    move = new MoveSpecification(E7, E5);
    board.move(move);

    move = new MoveSpecification(D4, E5, PromotionPieceType.QUEEN);
    check(board, move, MoveCheck.MOVE_SPEC_PAWN_NON_PROMOTION_PROMOTION_PIECE);

    move = new MoveSpecification(D4, E5);
    board.move(move);

    move = new MoveSpecification(F7, F5);
    board.move(move);

    move = new MoveSpecification(E5, F6, PromotionPieceType.ROOK);
    check(board, move, MoveCheck.MOVE_SPEC_PAWN_NON_PROMOTION_PROMOTION_PIECE);

    move = new MoveSpecification(E5, F6);
    board.move(move);

    move = new MoveSpecification(G8, H6);
    board.move(move);

    move = new MoveSpecification(F6, G7);
    board.move(move);

    move = new MoveSpecification(A7, A6);
    board.move(move);

    // promotion without capture
    move = new MoveSpecification(G7, G8);
    check(board, move, MoveCheck.MOVE_SPEC_PAWN_PROMOTION_NO_PROMOTION_PIECE);

    move = new MoveSpecification(G7, G8, PromotionPieceType.QUEEN);
    board.move(move);

  }

  @Test
  @SuppressWarnings("static-method")
  void testBlackMovingPieces() {
    final Board board = new Board();

    board.moveStrict("e4");

    MoveSpecification move = new MoveSpecification(E6, E5);
    check(board, move, MoveCheck.MOVE_SPEC_FROM_SQUARE_EMPTY);

    move = new MoveSpecification(A6, H3);
    check(board, move, MoveCheck.MOVE_SPEC_FROM_SQUARE_EMPTY);

    move = new MoveSpecification(D4, D1);
    check(board, move, MoveCheck.MOVE_SPEC_FROM_SQUARE_EMPTY);

    move = new MoveSpecification(D2, D3);
    check(board, move, MoveCheck.MOVE_SPEC_FROM_SQUARE_OCCUPIED_BY_OPPONENT);

    move = new MoveSpecification(D1, D3);
    check(board, move, MoveCheck.MOVE_SPEC_FROM_SQUARE_OCCUPIED_BY_OPPONENT);

    move = new MoveSpecification(E1, E2);
    check(board, move, MoveCheck.MOVE_SPEC_FROM_SQUARE_OCCUPIED_BY_OPPONENT);

    move = new MoveSpecification(B7, B6);
    board.move(move);

    move = new MoveSpecification(B2, B3);
    board.move(move);

    // rook movement
    move = new MoveSpecification(A8, B7);
    check(board, move, MoveCheck.MOVEMENT_NOT_POSSIBLE);

    // rook movement
    move = new MoveSpecification(H8, G6);
    check(board, move, MoveCheck.MOVEMENT_NOT_POSSIBLE);

    // rook movement
    move = new MoveSpecification(A8, B1);
    check(board, move, MoveCheck.MOVEMENT_NOT_POSSIBLE);

    // knight movement
    move = new MoveSpecification(B8, B5);
    check(board, move, MoveCheck.MOVEMENT_NOT_POSSIBLE);

    // knight movement
    move = new MoveSpecification(B8, B7);
    check(board, move, MoveCheck.MOVEMENT_NOT_POSSIBLE);

    move = new MoveSpecification(C7, C5);
    board.move(move);

    move = new MoveSpecification(C2, C4);
    board.move(move);

    // knight movement
    move = new MoveSpecification(B8, C7);
    check(board, move, MoveCheck.MOVEMENT_NOT_POSSIBLE);

    move = new MoveSpecification(B8, C6);
    board.move(move);

    move = new MoveSpecification(B1, C3);
    board.move(move);

    // bishop movement
    move = new MoveSpecification(C8, C7);
    check(board, move, MoveCheck.MOVEMENT_NOT_POSSIBLE);

    // bishop movement
    move = new MoveSpecification(F8, C6);
    check(board, move, MoveCheck.MOVEMENT_NOT_POSSIBLE);

    // bishop movement
    move = new MoveSpecification(C8, C1);
    check(board, move, MoveCheck.MOVEMENT_NOT_POSSIBLE);

    // queen movement
    move = new MoveSpecification(D8, B7);
    check(board, move, MoveCheck.MOVEMENT_NOT_POSSIBLE);

    // queen movement
    move = new MoveSpecification(D8, H6);
    check(board, move, MoveCheck.MOVEMENT_NOT_POSSIBLE);

    // queen movement
    move = new MoveSpecification(D8, C1);
    check(board, move, MoveCheck.MOVEMENT_NOT_POSSIBLE);

    move = new MoveSpecification(E7, E5);
    board.move(move);

    move = new MoveSpecification(D2, D4);
    board.move(move);

    // king movement
    move = new MoveSpecification(E8, E6);
    check(board, move, MoveCheck.MOVEMENT_NOT_POSSIBLE);

    // king movement
    move = new MoveSpecification(E8, F6);
    check(board, move, MoveCheck.MOVEMENT_NOT_POSSIBLE);

    // king movement
    move = new MoveSpecification(E8, A1);
    check(board, move, MoveCheck.MOVEMENT_NOT_POSSIBLE);

    // pawn movement
    move = new MoveSpecification(A7, A4);
    check(board, move, MoveCheck.MOVEMENT_NOT_POSSIBLE);

    // pawn movement
    move = new MoveSpecification(A7, B5);
    check(board, move, MoveCheck.MOVEMENT_NOT_POSSIBLE);

    // pawn movement
    move = new MoveSpecification(A7, A1);
    check(board, move, MoveCheck.MOVEMENT_NOT_POSSIBLE);

    // pawn movement
    move = new MoveSpecification(A7, A6, PromotionPieceType.QUEEN);
    check(board, move, MoveCheck.MOVE_SPEC_PAWN_NON_PROMOTION_PROMOTION_PIECE);

    // pawn movement
    move = new MoveSpecification(A7, A1, PromotionPieceType.QUEEN);
    check(board, move, MoveCheck.MOVEMENT_NOT_POSSIBLE);

  }

  @Test
  @SuppressWarnings("static-method")
  void testBlackCapturingNonPawn() {

    // capturing own pieces
    final Board board = new Board();
    board.moveStrict("e4");

    // rook
    MoveSpecification move = new MoveSpecification(A8, A7);
    check(board, move, MoveCheck.MOVEMENT_TO_SQUARE_OCCUPIED_BY_OWN_PIECE);

    move = new MoveSpecification(A8, B8);
    check(board, move, MoveCheck.MOVEMENT_TO_SQUARE_OCCUPIED_BY_OWN_PIECE);

    // knight
    move = new MoveSpecification(B8, D7);
    check(board, move, MoveCheck.MOVEMENT_TO_SQUARE_OCCUPIED_BY_OWN_PIECE);

    move = new MoveSpecification(A7, A6);
    board.move(move);

    move = new MoveSpecification(A2, A3);
    board.move(move);

    move = new MoveSpecification(B8, A6);
    check(board, move, MoveCheck.MOVEMENT_TO_SQUARE_OCCUPIED_BY_OWN_PIECE);

    // bishop
    move = new MoveSpecification(C8, B7);
    check(board, move, MoveCheck.MOVEMENT_TO_SQUARE_OCCUPIED_BY_OWN_PIECE);

    move = new MoveSpecification(D7, D5);
    board.move(move);

    move = new MoveSpecification(D2, D4);
    board.move(move);

    move = new MoveSpecification(D8, D7);
    board.move(move);

    move = new MoveSpecification(D1, D2);
    board.move(move);

    move = new MoveSpecification(C8, D7);
    check(board, move, MoveCheck.MOVEMENT_TO_SQUARE_OCCUPIED_BY_OWN_PIECE);

    // queen
    move = new MoveSpecification(D7, C8);
    check(board, move, MoveCheck.MOVEMENT_TO_SQUARE_OCCUPIED_BY_OWN_PIECE);

    // queen tries to capture the own king
    move = new MoveSpecification(D7, E8);
    check(board, move, MoveCheck.MOVEMENT_TO_SQUARE_OCCUPIED_BY_OWN_PIECE);

    move = new MoveSpecification(D7, C7);
    check(board, move, MoveCheck.MOVEMENT_TO_SQUARE_OCCUPIED_BY_OWN_PIECE);

    // king
    move = new MoveSpecification(E8, D7);
    check(board, move, MoveCheck.MOVEMENT_TO_SQUARE_OCCUPIED_BY_OWN_PIECE);

    move = new MoveSpecification(E8, F8);
    check(board, move, MoveCheck.MOVEMENT_TO_SQUARE_OCCUPIED_BY_OWN_PIECE);

  }

  @Test
  @SuppressWarnings("static-method")
  void testBlackCapturingPawn() {
    // pawn

    Board board = new Board();

    MoveSpecification move = new MoveSpecification(A2, A3);
    board.move(move);

    move = new MoveSpecification(B7, B5);
    board.move(move);

    move = new MoveSpecification(A3, A4);
    board.move(move);

    move = new MoveSpecification(B5, A4);
    board.move(move);

    move = new MoveSpecification(A1, A2);
    board.move(move);

    move = new MoveSpecification(A7, A5);
    board.move(move);

    move = new MoveSpecification(A2, A1);
    board.move(move);

    move = new MoveSpecification(A5, A4);
    check(board, move, MoveCheck.MOVEMENT_PAWN_FORWARD_ONE_SQUARE_TO_SQUARE_NOT_EMPTY_OWN_PIECE);

    move = new MoveSpecification(G8, H6);
    board.move(move);

    move = new MoveSpecification(A1, A2);
    board.move(move);

    move = new MoveSpecification(H7, H6);
    check(board, move, MoveCheck.MOVEMENT_PAWN_FORWARD_ONE_SQUARE_TO_SQUARE_NOT_EMPTY_OWN_PIECE);

    board = new Board();

    move = new MoveSpecification(A2, A4);
    board.move(move);

    move = new MoveSpecification(A7, A5);
    board.move(move);

    move = new MoveSpecification(A1, A3);
    board.move(move);

    move = new MoveSpecification(A5, A4);
    check(board, move, MoveCheck.MOVEMENT_PAWN_FORWARD_ONE_SQUARE_TO_SQUARE_NOT_EMPTY_OPPONENT_PIECE);

    move = new MoveSpecification(A8, A6);
    board.move(move);

    move = new MoveSpecification(A3, G3);
    board.move(move);

    move = new MoveSpecification(A6, A7);
    board.move(move);

    move = new MoveSpecification(G3, G6);
    board.move(move);

    move = new MoveSpecification(G7, G6);
    check(board, move, MoveCheck.MOVEMENT_PAWN_FORWARD_ONE_SQUARE_TO_SQUARE_NOT_EMPTY_OPPONENT_PIECE);

    board = new Board();

    move = new MoveSpecification(E2, E4);
    board.move(move);

    move = new MoveSpecification(B8, C6);
    board.move(move);

    move = new MoveSpecification(D2, D4);
    board.move(move);

    move = new MoveSpecification(B7, C6);
    check(board, move, MoveCheck.MOVEMENT_PAWN_DIAGONAL_OWN_PIECE);

    move = new MoveSpecification(E7, E5);
    board.move(move);

    move = new MoveSpecification(G2, G4);
    board.move(move);

    move = new MoveSpecification(D7, D5);
    board.move(move);

    move = new MoveSpecification(G4, G5);
    board.move(move);

    move = new MoveSpecification(D8, D7);
    board.move(move);

    move = new MoveSpecification(G5, G6);
    board.move(move);

    move = new MoveSpecification(H7, G6);
    board.move(move);

    move = new MoveSpecification(E1, E2);
    board.move(move);

    move = new MoveSpecification(F7, G6);
    check(board, move, MoveCheck.MOVEMENT_PAWN_DIAGONAL_OWN_PIECE);

    board = new Board();

    move = new MoveSpecification(E2, E4);
    board.move(move);

    move = new MoveSpecification(G7, F6);
    check(board, move, MoveCheck.MOVEMENT_PAWN_EN_PASSANT_WRONG_RANK);

    move = new MoveSpecification(G7, H6);
    check(board, move, MoveCheck.MOVEMENT_PAWN_EN_PASSANT_WRONG_RANK);

    move = new MoveSpecification(F7, F5);
    board.move(move);

    move = new MoveSpecification(E4, E5);
    board.move(move);

    move = new MoveSpecification(F5, E4);
    check(board, move, MoveCheck.MOVEMENT_PAWN_EN_PASSANT_WRONG_RANK);

    move = new MoveSpecification(B7, B6);
    board.move(move);

    move = new MoveSpecification(G2, G4);
    board.move(move);

    move = new MoveSpecification(B6, C5);
    check(board, move, MoveCheck.MOVEMENT_PAWN_EN_PASSANT_WRONG_RANK);

    move = new MoveSpecification(B6, A5);
    check(board, move, MoveCheck.MOVEMENT_PAWN_EN_PASSANT_WRONG_RANK);

    board = new Board();

    move = new MoveSpecification(A2, A4);
    board.move(move);

    move = new MoveSpecification(B7, B5);
    board.move(move);

    move = new MoveSpecification(C2, C4);
    board.move(move);

    move = new MoveSpecification(B5, B4);
    board.move(move);

    move = new MoveSpecification(E2, E4);
    board.move(move);

    move = new MoveSpecification(B4, A3);
    check(board, move, MoveCheck.MOVEMENT_PAWN_EN_PASSANT_NO_IMMEDIATE_BEFORE_TWO_SQUARE_ADVANCE);

    move = new MoveSpecification(B4, C3);
    check(board, move, MoveCheck.MOVEMENT_PAWN_EN_PASSANT_NO_IMMEDIATE_BEFORE_TWO_SQUARE_ADVANCE);

    move = new MoveSpecification(C8, B7);
    board.move(move);

    move = new MoveSpecification(A1, A2);
    board.move(move);

    move = new MoveSpecification(H7, H5);
    board.move(move);

    move = new MoveSpecification(A2, A1);
    board.move(move);

    move = new MoveSpecification(H5, H4);
    board.move(move);

    move = new MoveSpecification(A1, A2);
    board.move(move);

    move = new MoveSpecification(H4, G3);
    check(board, move, MoveCheck.MOVEMENT_PAWN_EN_PASSANT_NO_IMMEDIATE_BEFORE_TWO_SQUARE_ADVANCE);

    move = new MoveSpecification(F7, F5);
    board.move(move);

    move = new MoveSpecification(A2, A1);
    board.move(move);

    move = new MoveSpecification(F5, F4);
    board.move(move);

    move = new MoveSpecification(G2, G4);
    board.move(move);

    move = new MoveSpecification(B8, C6);
    board.move(move);

    move = new MoveSpecification(A1, A2);
    board.move(move);

    move = new MoveSpecification(F4, G3);
    check(board, move, MoveCheck.MOVEMENT_PAWN_EN_PASSANT_NO_IMMEDIATE_BEFORE_TWO_SQUARE_ADVANCE);

  }

  @Test
  @SuppressWarnings("static-method")
  void testBlackPawnTwoSquareAdvanceMoves() {

    final Board board = new Board();

    MoveSpecification move = new MoveSpecification(E2, E4);
    board.move(move);

    move = new MoveSpecification(B8, C6);
    board.move(move);

    move = new MoveSpecification(D2, D4);
    board.move(move);

    move = new MoveSpecification(C7, C5);
    check(board, move, MoveCheck.MOVEMENT_PAWN_FORWARD_TWO_SQUARE_JUMP_OVER_SQUARE_ONLY_NOT_EMPTY);

    move = new MoveSpecification(C6, B8);
    board.move(move);

    move = new MoveSpecification(A2, A4);
    board.move(move);

    move = new MoveSpecification(B8, C6);
    board.move(move);

    move = new MoveSpecification(A4, A5);
    board.move(move);

    move = new MoveSpecification(C6, B8);
    board.move(move);

    move = new MoveSpecification(A5, A6);
    board.move(move);

    move = new MoveSpecification(A7, A5);
    check(board, move, MoveCheck.MOVEMENT_PAWN_FORWARD_TWO_SQUARE_JUMP_OVER_SQUARE_ONLY_NOT_EMPTY);

    move = new MoveSpecification(G8, F6);
    board.move(move);

    move = new MoveSpecification(A1, A2);
    board.move(move);

    move = new MoveSpecification(F6, H5);
    board.move(move);

    move = new MoveSpecification(A2, A1);
    board.move(move);

    move = new MoveSpecification(H7, H5);
    check(board, move, MoveCheck.MOVEMENT_PAWN_FORWARD_TWO_SQUARE_TO_SQUARE_ONLY_NOT_EMPTY);

    move = new MoveSpecification(H5, F6);
    board.move(move);

    move = new MoveSpecification(F2, F4);
    board.move(move);

    move = new MoveSpecification(H7, H6);
    board.move(move);

    move = new MoveSpecification(F4, F5);
    board.move(move);

    move = new MoveSpecification(F7, F5);
    check(board, move, MoveCheck.MOVEMENT_PAWN_FORWARD_TWO_SQUARE_BOTH_SQUARE_NOT_EMPTY);

  }

  @Test
  @SuppressWarnings("static-method")
  void testBlackKingMoves() {

    Board board = new Board();

    MoveSpecification move = new MoveSpecification(B1, C3);
    board.move(move);

    move = new MoveSpecification(D7, D5);
    board.move(move);

    move = new MoveSpecification(C3, D5);
    board.move(move);

    move = new MoveSpecification(E8, D7);
    board.move(move);

    move = new MoveSpecification(C2, C4);
    board.move(move);

    move = new MoveSpecification(D7, D6);
    board.move(move);

    move = new MoveSpecification(G1, F3);
    board.move(move);

    move = new MoveSpecification(D6, D5);
    check(board, move, MoveCheck.KING_CAPTURES_GUARDED_PIECE);

    // kings walk on the middle of the board to move next to each other
    board = new Board();

    move = new MoveSpecification(F2, F3);
    board.move(move);

    move = new MoveSpecification(F7, F6);
    board.move(move);

    move = new MoveSpecification(E1, F2);
    board.move(move);

    move = new MoveSpecification(E8, F7);
    board.move(move);

    move = new MoveSpecification(F2, E3);
    board.move(move);

    move = new MoveSpecification(F7, E6);
    board.move(move);

    move = new MoveSpecification(E3, E4);
    board.move(move);

    move = new MoveSpecification(E6, E5);
    check(board, move, MoveCheck.KING_MOVES_NEXT_TO_OPPONENT_KING);
  }

  @Test
  @SuppressWarnings("static-method")
  void testBlackPromotionPieceWronglySet() {

    Board board = new Board();

    MoveSpecification move = new MoveSpecification(A2, A4);
    board.move(move);

    move = new MoveSpecification(A7, A5);
    board.move(move);

    move = new MoveSpecification(D2, D4);
    board.move(move);

    move = new MoveSpecification(D7, D5);
    board.move(move);

    move = new MoveSpecification(E2, E4);
    board.move(move);

    move = new MoveSpecification(E7, E5);
    board.move(move);

    move = new MoveSpecification(D1, D2);
    board.move(move);

    // rook
    move = new MoveSpecification(A8, A7, PromotionPieceType.QUEEN);
    check(board, move, MoveCheck.MOVE_SPEC_NON_PAWN_PROMOTION_PIECE_SET);

    // knight
    move = new MoveSpecification(B8, C6, PromotionPieceType.QUEEN);
    check(board, move, MoveCheck.MOVE_SPEC_NON_PAWN_PROMOTION_PIECE_SET);

    // bishop light square
    move = new MoveSpecification(F8, C5, PromotionPieceType.QUEEN);
    check(board, move, MoveCheck.MOVE_SPEC_NON_PAWN_PROMOTION_PIECE_SET);

    // bishop dark square
    move = new MoveSpecification(C8, F5, PromotionPieceType.QUEEN);
    check(board, move, MoveCheck.MOVE_SPEC_NON_PAWN_PROMOTION_PIECE_SET);

    // queen
    move = new MoveSpecification(D8, D7, PromotionPieceType.QUEEN);
    check(board, move, MoveCheck.MOVE_SPEC_NON_PAWN_PROMOTION_PIECE_SET);

    // king
    move = new MoveSpecification(E8, E7, PromotionPieceType.QUEEN);
    check(board, move, MoveCheck.MOVE_SPEC_NON_PAWN_PROMOTION_PIECE_SET);

    // pawn move - promotion piece checks with promotion by capture
    board = new Board();

    move = new MoveSpecification(E2, E4);
    board.move(move);

    move = new MoveSpecification(E7, E6, PromotionPieceType.QUEEN);
    check(board, move, MoveCheck.MOVE_SPEC_PAWN_NON_PROMOTION_PROMOTION_PIECE);

    move = new MoveSpecification(E7, E5, PromotionPieceType.QUEEN);
    check(board, move, MoveCheck.MOVE_SPEC_PAWN_NON_PROMOTION_PROMOTION_PIECE);

    move = new MoveSpecification(E7, E5);
    board.move(move);

    move = new MoveSpecification(D2, D4);
    board.move(move);

    move = new MoveSpecification(E5, D4, PromotionPieceType.KNIGHT);
    check(board, move, MoveCheck.MOVE_SPEC_PAWN_NON_PROMOTION_PROMOTION_PIECE);

    // en passant capture
    board = new Board();

    move = new MoveSpecification(B1, C3);
    board.move(move);

    move = new MoveSpecification(D7, D5);
    board.move(move);

    move = new MoveSpecification(C3, B1);
    board.move(move);

    move = new MoveSpecification(D5, D4);
    board.move(move);

    move = new MoveSpecification(E2, E4);
    board.move(move);

    move = new MoveSpecification(D4, E3, PromotionPieceType.BISHOP);
    check(board, move, MoveCheck.MOVE_SPEC_PAWN_NON_PROMOTION_PROMOTION_PIECE);

    // promotion with capture
    board = new Board();

    move = new MoveSpecification(B1, C3);
    board.move(move);

    move = new MoveSpecification(B7, B5);
    board.move(move);

    move = new MoveSpecification(C3, B1);
    board.move(move);

    move = new MoveSpecification(B5, B4);
    board.move(move);

    move = new MoveSpecification(B1, C3);
    board.move(move);

    move = new MoveSpecification(B4, B3);
    board.move(move);

    move = new MoveSpecification(C3, B1);
    board.move(move);

    move = new MoveSpecification(B3, A2);
    board.move(move);

    move = new MoveSpecification(G1, F3);
    board.move(move);

    move = new MoveSpecification(A2, B1);
    check(board, move, MoveCheck.MOVE_SPEC_PAWN_PROMOTION_NO_PROMOTION_PIECE);

    // pawn move - promotion piece checks with promotion without capture
    board = new Board();

    move = new MoveSpecification(G1, F3);
    board.move(move);

    move = new MoveSpecification(D7, D6, PromotionPieceType.QUEEN);
    check(board, move, MoveCheck.MOVE_SPEC_PAWN_NON_PROMOTION_PROMOTION_PIECE);

    move = new MoveSpecification(D7, D5, PromotionPieceType.ROOK);
    check(board, move, MoveCheck.MOVE_SPEC_PAWN_NON_PROMOTION_PROMOTION_PIECE);

  }

  @Test
  @SuppressWarnings("static-method")
  void testLongRangePieces() {
    // white
    // rook
    {
      final var fen = "r2qk2r/1bppbppp/p1n2n2/1p2p2P/4P2R/2N2N2/PPPP1PP1/R1BQKB2 w Qkq - 3 8";
      final MoveSpecification move = new MoveSpecification(H4, A4);
      check(fen, move, MoveCheck.MOVEMENT_LONG_RANGE_PIECE_JUMPS_OVER_PIECE);
    }
    // bishop
    {
      final var fen = "r1bqkb1r/p1pp1ppp/2n2n2/1p2p3/4P3/2N2N1P/PPPP1PP1/R1BQKB1R w KQkq - 0 5";
      final MoveSpecification move = new MoveSpecification(F1, A6);
      check(fen, move, MoveCheck.MOVEMENT_LONG_RANGE_PIECE_JUMPS_OVER_PIECE);
    }
    // queen
    {
      final var fen = "r2q1rk1/1bppbppp/p1n2n2/1p2p2P/4P2R/P1N2N2/1PPP1PP1/R1BQKB2 w Q - 1 9";
      final MoveSpecification move = new MoveSpecification(D1, G4);
      check(fen, move, MoveCheck.MOVEMENT_LONG_RANGE_PIECE_JUMPS_OVER_PIECE);
    }

    // black
    // rook
    {
      final var fen = "r1b1kbn1/ppp1ppp1/2n4r/q6p/3P4/2N2N1P/PPPB1PP1/R2QKB1R b KQq - 4 7";
      final MoveSpecification move = new MoveSpecification(H6, B6);
      check(fen, move, MoveCheck.MOVEMENT_LONG_RANGE_PIECE_JUMPS_OVER_PIECE);
    }
    // bishop
    {
      final var fen = "r1b1kb2/ppp1ppp1/2nr1n2/q6p/3P2PP/2N2N2/PPPB1P2/R2QKB1R b KQq - 0 9";
      final MoveSpecification move = new MoveSpecification(C8, H3);
      check(fen, move, MoveCheck.MOVEMENT_LONG_RANGE_PIECE_JUMPS_OVER_PIECE);
    }
    // queen
    {
      final var fen = "r3kb2/pppbppp1/2nr1n2/q6p/3P2PP/2N2N2/PPPB1P2/1R1QKB1R b Kq - 2 10";
      final MoveSpecification move = new MoveSpecification(A5, A1);
      check(fen, move, MoveCheck.MOVEMENT_LONG_RANGE_PIECE_JUMPS_OVER_PIECE);
    }
  }

  @Test
  @SuppressWarnings("static-method")
  void testAllPiecesButKingKingLeftInCheck() {
    // white
    // rook
    {
      final var fen = "rnbqk1nr/pppp1ppp/8/4p3/4P3/2N2N2/PPPP1bPP/R1BQKB1R w KQkq - 0 4";
      final MoveSpecification move = new MoveSpecification(H1, G1);
      check(fen, move, MoveCheck.ALL_BUT_KING_KING_LEFT_IN_CHECK);
    }
    // knight
    {
      final var fen = "rnbqk1nr/pppp1ppp/8/4p3/4P3/2N2N2/PPPP1bPP/R1BQKB1R w KQkq - 0 4";
      final MoveSpecification move = new MoveSpecification(C3, D5);
      check(fen, move, MoveCheck.ALL_BUT_KING_KING_LEFT_IN_CHECK);
    }
    // bishop
    {
      final var fen = "rnbqk1nr/pppp1ppp/8/4p3/4P3/2N2N2/PPPP1bPP/R1BQKB1R w KQkq - 0 4";
      final MoveSpecification move = new MoveSpecification(F1, C4);
      check(fen, move, MoveCheck.ALL_BUT_KING_KING_LEFT_IN_CHECK);
    }
    // queen
    {
      final var fen = "rnbqk1nr/pppp1ppp/8/4p3/4P3/2N2N2/PPPP1bPP/R1BQKB1R w KQkq - 0 4";
      final MoveSpecification move = new MoveSpecification(D1, E2);
      check(fen, move, MoveCheck.ALL_BUT_KING_KING_LEFT_IN_CHECK);
    }
    // pawn
    {
      final var fen = "rnbqk1nr/pppp1ppp/8/4p3/4P3/2N2N2/PPPP1bPP/R1BQKB1R w KQkq - 0 4";
      final MoveSpecification move = new MoveSpecification(D2, D4);
      check(fen, move, MoveCheck.ALL_BUT_KING_KING_LEFT_IN_CHECK);
    }

    // black
    // rook
    {
      final var fen = "rnbqkbnr/pppp1Bp1/8/4p2p/4P3/8/PPPP1PPP/RNBQK1NR b KQkq - 0 3";
      final MoveSpecification move = new MoveSpecification(H8, H6);
      check(fen, move, MoveCheck.ALL_BUT_KING_KING_LEFT_IN_CHECK);
    }
    // knight
    {
      final var fen = "rnbqkbnr/pppp1Bp1/8/4p2p/4P3/8/PPPP1PPP/RNBQK1NR b KQkq - 0 3";
      final MoveSpecification move = new MoveSpecification(B8, C6);
      check(fen, move, MoveCheck.ALL_BUT_KING_KING_LEFT_IN_CHECK);
    }
    // bishop
    {
      final var fen = "rnbqkbnr/pppp1Bp1/8/4p2p/4P3/8/PPPP1PPP/RNBQK1NR b KQkq - 0 3";
      final MoveSpecification move = new MoveSpecification(F8, C5);
      check(fen, move, MoveCheck.ALL_BUT_KING_KING_LEFT_IN_CHECK);
    }
    // queen
    {
      final var fen = "rnbqkbnr/pppp1Bp1/8/4p2p/4P3/8/PPPP1PPP/RNBQK1NR b KQkq - 0 3";
      final MoveSpecification move = new MoveSpecification(D8, H4);
      check(fen, move, MoveCheck.ALL_BUT_KING_KING_LEFT_IN_CHECK);
    }
    // pawn
    {
      final var fen = "rnbqkbnr/pppp1Bp1/8/4p2p/4P3/8/PPPP1PPP/RNBQK1NR b KQkq - 0 3";
      final MoveSpecification move = new MoveSpecification(G7, G5);
      check(fen, move, MoveCheck.ALL_BUT_KING_KING_LEFT_IN_CHECK);
    }
  }

  @Test
  @SuppressWarnings("static-method")
  void testKingInCheckRemainsInCheckLegalMoves() {
    // white
    // rook checking
    {
      final var fen = "rnbqkbn1/ppppppp1/8/7p/4P3/r3K3/PPPPBPPP/RNBQ2NR w q - 6 5";
      final MoveSpecification move = new MoveSpecification(E3, F3);
      check(fen, move, MoveCheck.KING_MOVES_TO_ATTACKED_EMPTY_SQUARE);
    }
    // knight checking
    {
      final var fen = "r1bqkbnr/pppppppp/8/8/8/P2nPP2/1PPP2PP/RNBQKBNR w KQkq - 1 4";
      final MoveSpecification move = new MoveSpecification(E1, F2);
      check(fen, move, MoveCheck.KING_MOVES_TO_ATTACKED_EMPTY_SQUARE);
    }
    // bishop checking
    {
      final var fen = "r1bqk1nr/pppp1ppp/2n5/4p3/1b1P4/5P1N/PPP1P1PP/RNBQKB1R w KQkq - 1 4";
      final MoveSpecification move = new MoveSpecification(E1, D2);
      check(fen, move, MoveCheck.KING_MOVES_TO_ATTACKED_EMPTY_SQUARE);
    }
    // queen checking
    {
      final var fen = "rnb1kbnr/pppp1ppp/8/4p3/3P1P1q/2NQ4/PPP1P1PP/2BRKBNR w Kkq - 15 10";
      final MoveSpecification move = new MoveSpecification(E1, F2);
      check(fen, move, MoveCheck.KING_MOVES_TO_ATTACKED_EMPTY_SQUARE);
    }
    // pawn checking
    {
      final var fen = "rn1qkbnr/ppp1pppp/8/8/3pP1b1/4K2P/PPPP1PP1/RNBQ1BNR w kq - 0 5";
      final MoveSpecification move = new MoveSpecification(E3, F3);
      check(fen, move, MoveCheck.KING_MOVES_TO_ATTACKED_EMPTY_SQUARE);
    }

    // black
    // rook checking
    {
      final var fen = "r1bq3r/pppp1ppp/2nk1n2/2b1p3/2B1P3/3R1N2/PPPP1PPP/RNBQ2K1 b - - 11 7";
      final MoveSpecification move = new MoveSpecification(D6, E6);
      check(fen, move, MoveCheck.KING_MOVES_TO_ATTACKED_EMPTY_SQUARE);
    }
    // knight checking
    {
      final var fen = "rnbq1bnr/ppppkppp/8/4pN2/8/8/PPPPPPPP/RNBQKB1R b KQ - 3 3";
      final MoveSpecification move = new MoveSpecification(E7, D6);
      check(fen, move, MoveCheck.KING_MOVES_TO_ATTACKED_EMPTY_SQUARE);
    }
    // bishop checking
    {
      final var fen = "rnbq2nr/ppppppkp/6pb/8/4P3/1P6/PBPP1PPP/RN1QKBNR b KQ - 6 5";
      final MoveSpecification move = new MoveSpecification(G7, F6);
      check(fen, move, MoveCheck.KING_MOVES_TO_ATTACKED_EMPTY_SQUARE);
    }
    // queen checking
    {
      final var fen = "rnbq1bnr/pppkpppp/3p4/8/6Q1/4P3/PPPP1PPP/RNB1KBNR b KQ - 3 3";
      final MoveSpecification move = new MoveSpecification(D7, E6);
      check(fen, move, MoveCheck.KING_MOVES_TO_ATTACKED_EMPTY_SQUARE);
    }
    // pawn checking
    {
      final var fen = "rnbq1bnr/ppppp1pp/5pk1/7P/8/4PQ2/PPPP1PP1/RNB1KBNR b KQ - 0 4";
      final MoveSpecification move = new MoveSpecification(G6, F5);
      check(fen, move, MoveCheck.KING_MOVES_TO_ATTACKED_EMPTY_SQUARE);
    }
  }

  @Test
  @SuppressWarnings("static-method")
  void testKingInCheckRemainsInCheckNoLegalMoves() {
    // white
    // rook checking
    {
      final var fen = "rnbqkbn1/pp1ppp2/8/2p3pp/4P3/r3K3/PPPPBPPP/RNBQ2NR w q - 2 9";
      final MoveSpecification move = new MoveSpecification(E3, F3);
      check(fen, move, MoveCheck.KING_MOVES_TO_ATTACKED_EMPTY_SQUARE);
    }
    // knight checking
    {
      final var fen = "r1bqkbnr/pppppppp/8/8/8/P2n1PP1/1PPPP2P/RNBQKBNR w KQkq - 1 4";
      final MoveSpecification move = new MoveSpecification(E1, F2);
      check(fen, move, MoveCheck.KING_MOVES_TO_ATTACKED_EMPTY_SQUARE);
    }
    // bishop checking
    {
      final var fen = "rnbqk1nr/pppp1ppp/8/4p3/1b1P4/7N/PPP1PPPP/RNBQKB1R w KQkq - 2 3";
      final MoveSpecification move = new MoveSpecification(E1, D2);
      check(fen, move, MoveCheck.KING_MOVES_TO_ATTACKED_EMPTY_SQUARE);
    }
    // queen checking
    {
      final var fen = "rnb1kbnr/pppp1ppp/8/4p3/3P1P1q/2NQ4/PPPBP1PP/3RKBNR w Kkq - 9 7";
      final MoveSpecification move = new MoveSpecification(E1, F2);
      check(fen, move, MoveCheck.KING_MOVES_TO_ATTACKED_EMPTY_SQUARE);
    }
    // pawn checking
    {
      final var fen = "r3kbnr/p1pp3p/bpn1p3/6p1/4Pp2/4K1P1/PPPPBPqP/RNBQ2NR w kq - 0 12";
      final MoveSpecification move = new MoveSpecification(E3, D3);
      check(fen, move, MoveCheck.KING_MOVES_TO_ATTACKED_EMPTY_SQUARE);
    }

    // black
    // rook checking
    {
      final var fen = "1nbq2nr/rpppbppp/pk6/3Q4/P6P/RR6/1PP1PPP1/1NB1KBN1 b - - 3 10";
      final MoveSpecification move = new MoveSpecification(B6, A5);
      check(fen, move, MoveCheck.KING_MOVES_TO_ATTACKED_EMPTY_SQUARE);
    }
    // knight checking
    {
      final var fen = "rnbqkbnr/1pppp1pp/p2N1p2/8/8/8/PPPPPPPP/R1BQKBNR b KQkq - 1 3";
      final MoveSpecification move = new MoveSpecification(E8, F7);
      check(fen, move, MoveCheck.KING_MOVES_TO_ATTACKED_EMPTY_SQUARE);
    }
    // bishop checking
    {
      final var fen = "rnbqkbnr/ppp1p1pp/3p1p2/5Q1B/8/4P3/PPPP1PPP/RNB1K1NR b KQ - 9 7";
      final MoveSpecification move = new MoveSpecification(E8, F7);
      check(fen, move, MoveCheck.KING_MOVES_TO_ATTACKED_EMPTY_SQUARE);
    }
    // queen checking
    {
      final var fen = "r1bqkbnr/ppppp1pp/5p2/4n2Q/8/4P3/PPPP1PPP/RNB1KBNR b KQkq - 5 4";
      final MoveSpecification move = new MoveSpecification(E8, F7);
      check(fen, move, MoveCheck.KING_MOVES_TO_ATTACKED_EMPTY_SQUARE);
    }
    // pawn checking
    {
      final var fen = "r1b2qnr/ppppppkp/Q6P/6p1/3b2n1/8/PPP1PPP1/RNB1KBNR b KQ - 0 12";
      final MoveSpecification move = new MoveSpecification(G7, G6);
      check(fen, move, MoveCheck.KING_MOVES_TO_ATTACKED_EMPTY_SQUARE);
    }
  }

  @Test
  @SuppressWarnings("static-method")
  void testKingMovesIntoCheck() {
    // white
    // rook
    {
      final var fen = "rnbqkbn1/ppppppp1/8/7p/3P4/r7/PPPKPPPP/RNBQ1BNR w q - 6 5";
      final MoveSpecification move = new MoveSpecification(D2, D3);
      check(fen, move, MoveCheck.KING_MOVES_TO_ATTACKED_EMPTY_SQUARE);
    }
    // knight
    {
      final var fen = "rnbqkb1r/pppppppp/8/8/4P1n1/8/PPPPKPPP/RNBQ1BNR w kq - 3 3";
      final MoveSpecification move = new MoveSpecification(E2, E3);
      check(fen, move, MoveCheck.KING_MOVES_TO_ATTACKED_EMPTY_SQUARE);
    }
    // bishop
    {
      final var fen = "rnbqk1nr/pppppp1p/6pb/8/8/3P2P1/PPP1PP1P/RNBQKBNR w KQkq - 1 3";
      final MoveSpecification move = new MoveSpecification(E1, D2);
      check(fen, move, MoveCheck.KING_MOVES_TO_ATTACKED_EMPTY_SQUARE);
    }
    // queen
    {
      final var fen = "rnb1kbnr/pppp1ppp/8/4p1q1/4P3/8/PPPPKPPP/RNBQ1BNR w kq - 2 3";
      final MoveSpecification move = new MoveSpecification(E2, E3);
      check(fen, move, MoveCheck.KING_MOVES_TO_ATTACKED_EMPTY_SQUARE);
    }
    // pawn
    {
      final var fen = "rnbqkbnr/pppp1pp1/7p/4p3/4P3/4K3/PPPP1PPP/RNBQ1BNR w kq - 0 4";
      final MoveSpecification move = new MoveSpecification(E3, D4);
      check(fen, move, MoveCheck.KING_MOVES_TO_ATTACKED_EMPTY_SQUARE);
    }

    // black
    // rook
    {
      final var fen = "rnbq1bnr/ppppkppp/1R2p3/8/7P/8/PPPPPPP1/RNBQKBN1 b Q - 7 5";
      final MoveSpecification move = new MoveSpecification(E7, D6);
      check(fen, move, MoveCheck.KING_MOVES_TO_ATTACKED_EMPTY_SQUARE);
    }
    // knight
    {
      final var fen = "rnbqkbnr/pppp1ppp/4p3/3N4/8/8/PPPPPPPP/R1BQKBNR b KQkq - 1 2";
      final MoveSpecification move = new MoveSpecification(E8, E7);
      check(fen, move, MoveCheck.KING_MOVES_TO_ATTACKED_EMPTY_SQUARE);
    }
    // bishop
    {
      final var fen = "rnbqkbnr/ppppp1pp/5p2/8/2B5/4P3/PPPP1PPP/RNBQK1NR b KQkq - 1 2";
      final MoveSpecification move = new MoveSpecification(E8, F7);
      check(fen, move, MoveCheck.KING_MOVES_TO_ATTACKED_EMPTY_SQUARE);
    }
    // queen
    {
      final var fen = "rnbqkbnr/p1pp1ppp/1p3Q2/4p3/4P3/8/PPPP1PPP/RNB1KBNR b KQkq - 1 3";
      final MoveSpecification move = new MoveSpecification(E8, E7);
      check(fen, move, MoveCheck.KING_MOVES_TO_ATTACKED_EMPTY_SQUARE);
    }
    // pawn
    {
      final var fen = "rnbq1bnr/pppp1ppp/5k2/4p3/6P1/5N2/PPPPPP1P/RNBQKB1R b KQ - 5 4";
      final MoveSpecification move = new MoveSpecification(F6, F5);
      check(fen, move, MoveCheck.KING_MOVES_TO_ATTACKED_EMPTY_SQUARE);
    }

  }

  @Test
  @SuppressWarnings("static-method")
  void testKingCapturesGuardedPiece() {
    // white
    // rook
    {
      final var fen = "rnb1kbn1/p1qpppp1/8/7p/3P4/2r5/P2KPPPP/RNBQ1BNR w q - 6 25";
      final MoveSpecification move = new MoveSpecification(D2, C3);
      check(fen, move, MoveCheck.KING_CAPTURES_GUARDED_PIECE);
    }

    // knight failure
    {
      final var fen = "2k5/3r4/8/8/3K1N2/3n4/8/8 w - - 0 1";
      final MoveSpecification move = new MoveSpecification(D4, D3);
      check(fen, move, MoveCheck.KING_CAPTURES_GUARDED_PIECE);
    }

    // knight
    {
      final var fen = "rnb1kb2/p1qpppp1/8/7p/3P4/5Kn1/P3PPPP/RNBQ1BNR w q - 6 25";
      final MoveSpecification move = new MoveSpecification(F3, G3);
      check(fen, move, MoveCheck.KING_CAPTURES_GUARDED_PIECE);
    }
    // bishop
    {
      final var fen = "rn2kb2/p1qpppp1/8/2n4p/3P4/3bK3/P3PPPP/RNBQ1BNR w q - 6 25";
      final MoveSpecification move = new MoveSpecification(E3, D3);
      check(fen, move, MoveCheck.KING_CAPTURES_GUARDED_PIECE);
    }
    // queen
    // not possible
    // pawn
    {
      final var fen = "rn1qkb2/p2p2p1/5p2/2n1p2p/3PK3/8/P3PPPP/RNBQ1BNR w q - 6 25";
      final MoveSpecification move = new MoveSpecification(E4, E5);
      check(fen, move, MoveCheck.KING_CAPTURES_GUARDED_PIECE);
    }

    // black
    // rook
    {
      final var fen = "r1bq2nr/pppp2pp/2n2k2/1Bb1p1R1/P3P3/5N2/3P1PPP/1NBQK2R b K - 4 25";
      final MoveSpecification move = new MoveSpecification(F6, G5);
      check(fen, move, MoveCheck.KING_CAPTURES_GUARDED_PIECE);
    }
    // knight
    {
      final var fen = "r1bq2nr/pppp2pp/2n3k1/1Bb1p2N/P3P3/8/3P1PPP/RNBQK2R b KQ - 4 25";
      final MoveSpecification move = new MoveSpecification(G6, H5);
      check(fen, move, MoveCheck.KING_CAPTURES_GUARDED_PIECE);
    }
    // bishop
    {
      final var fen = "r1bq2nr/pppp2pp/2n3k1/1Bb1p1B1/P3P3/7N/3P1PPP/RN1QK2R b KQ - 4 25";
      final MoveSpecification move = new MoveSpecification(G6, G5);
      check(fen, move, MoveCheck.KING_CAPTURES_GUARDED_PIECE);
    }
    // queen
    // not possible
    // pawn
    {
      final var fen = "r1bq2nr/pppp2pp/2n5/1Bb1pk2/P4P2/4P2N/3P2PP/RNBQK2R b KQ - 4 25";
      final MoveSpecification move = new MoveSpecification(F5, F4);
      check(fen, move, MoveCheck.KING_CAPTURES_GUARDED_PIECE);
    }
  }

}