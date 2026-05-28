package com.dlb.chess.test.san.pawn;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.dlb.chess.board.Board;
import com.dlb.chess.san.SanValidationException;
import com.dlb.chess.san.SanValidationProblem;
import com.dlb.chess.san.StrictSanParser;

class TestSanValidatePawnPromotion {

  // --- White: promotion rank is 8 ---

  @SuppressWarnings("static-method")
  @Test
  void testWhiteNonCapturingPromotionValid() {
    // white pawn on d7, promotion to rank 8 with piece specified - valid
    final Board board = new Board("5k2/3P4/8/8/8/8/8/4K3 w - - 0 100");
    checkValid("d8=Q+", board);
    checkValid("d8=R+", board);
    checkValid("d8=B", board);
    checkValid("d8=N", board);
  }

  @SuppressWarnings("static-method")
  @Test
  void testWhiteNonCapturingMissingPromotionPiece() {
    // white pawn on d7, d8 without =Q - missing promotion piece
    final Board board = new Board("5k2/3P4/8/8/8/8/8/4K3 w - - 0 100");
    checkException("d8", board, SanValidationProblem.FORMAT_PAWN_FORWARD_PROMOTION_NO_PROMOTION_SYMBOL);
  }

  @SuppressWarnings("static-method")
  @Test
  void testWhiteNonCapturingPromotionWrongRank() {
    // white pawn on d3, d4=Q - rank 4 is not promotion rank
    final Board board = new Board("4k3/8/8/8/8/3P4/8/4K3 w - - 0 100");
    checkException("d4=Q", board, SanValidationProblem.FORMAT_PAWN_FORWARD_NON_PROMOTION_OVERLENGTH);
  }

  @SuppressWarnings("static-method")
  @Test
  void testWhiteNonCapturingPromotionWrongRankVariousRanks() {
    // white pawn on d2
    final Board board = new Board("4k3/8/8/8/8/8/3P4/4K3 w - - 0 100");
    checkException("d3=Q", board, SanValidationProblem.FORMAT_PAWN_FORWARD_NON_PROMOTION_OVERLENGTH);
    checkException("d4=Q", board, SanValidationProblem.FORMAT_PAWN_FORWARD_NON_PROMOTION_OVERLENGTH);
  }

  @SuppressWarnings("static-method")
  @Test
  void testWhiteCapturingPromotionValid() {
    // white pawn on d7, black rook on e8 - dxe8=Q is valid
    final Board board = new Board("4rk2/3P4/8/8/8/8/8/4K3 w - - 0 100");
    checkValid("dxe8=Q+", board);
    checkValid("dxe8=R+", board);
    checkValid("dxe8=B", board);
    checkValid("dxe8=N", board);
  }

  @SuppressWarnings("static-method")
  @Test
  void testWhiteCapturingMissingPromotionPiece() {
    // white pawn on d7, black rook on e8 - dxe8 without =Q
    final Board board = new Board("4rk2/3P4/8/8/8/8/8/4K3 w - - 0 100");
    checkException("dxe8", board, SanValidationProblem.FORMAT_PAWN_CAPTURE_PROMOTION_NO_PROMOTION_SYMBOL);
  }

  @SuppressWarnings("static-method")
  @Test
  void testWhiteCapturingPromotionWrongRank() {
    // white pawn on d4, black pawn on e5 - dxe5=Q, rank 5 is not promotion rank
    final Board board = new Board("4k3/8/8/4p3/3P4/8/8/4K3 w - - 0 100");
    checkException("dxe5=Q", board, SanValidationProblem.FORMAT_PAWN_CAPTURE_NON_PROMOTION_OVERLENGTH);
  }

  // --- Black: promotion rank is 1 ---

  @SuppressWarnings("static-method")
  @Test
  void testBlackNonCapturingPromotionValid() {
    // black pawn on d2, promotion to rank 1 with piece specified - valid
    final Board board = new Board("4k3/8/8/8/8/8/3p4/5K2 b - - 0 100");
    checkValid("d1=Q+", board);
    checkValid("d1=R+", board);
    checkValid("d1=B", board);
    checkValid("d1=N", board);
  }

  @SuppressWarnings("static-method")
  @Test
  void testBlackNonCapturingMissingPromotionPiece() {
    // black pawn on d2, d1 without =Q - missing promotion piece
    final Board board = new Board("4k3/8/8/8/8/8/3p4/5K2 b - - 0 100");
    checkException("d1", board, SanValidationProblem.FORMAT_PAWN_FORWARD_PROMOTION_NO_PROMOTION_SYMBOL);
  }

  @SuppressWarnings("static-method")
  @Test
  void testBlackNonCapturingPromotionWrongRank() {
    // black pawn on d6, d5=Q - rank 5 is not promotion rank
    final Board board = new Board("4k3/8/3p4/8/8/8/8/4K3 b - - 0 100");
    checkException("d5=Q", board, SanValidationProblem.FORMAT_PAWN_FORWARD_NON_PROMOTION_OVERLENGTH);
  }

  @SuppressWarnings("static-method")
  @Test
  void testBlackNonCapturingPromotionWrongRankVariousRanks() {
    // black pawn on d7
    final Board board = new Board("4k3/3p4/8/8/8/8/8/4K3 b - - 0 100");
    checkException("d6=Q", board, SanValidationProblem.FORMAT_PAWN_FORWARD_NON_PROMOTION_OVERLENGTH);
    checkException("d5=Q", board, SanValidationProblem.FORMAT_PAWN_FORWARD_NON_PROMOTION_OVERLENGTH);
  }

  @SuppressWarnings("static-method")
  @Test
  void testBlackCapturingPromotionValid() {
    // black pawn on d2, white rook on e1 - dxe1=Q is valid
    final Board board = new Board("4k3/8/8/8/8/8/3p4/4RK2 b - - 0 100");
    checkValid("dxe1=Q+", board);
    checkValid("dxe1=R+", board);
    checkValid("dxe1=B", board);
    checkValid("dxe1=N", board);
  }

  @SuppressWarnings("static-method")
  @Test
  void testBlackCapturingMissingPromotionPiece() {
    // black pawn on d2, white rook on e1 - dxe1 without =Q
    final Board board = new Board("4k3/8/8/8/8/8/3p4/4RK2 b - - 0 100");
    checkException("dxe1", board, SanValidationProblem.FORMAT_PAWN_CAPTURE_PROMOTION_NO_PROMOTION_SYMBOL);
  }

  @SuppressWarnings("static-method")
  @Test
  void testBlackCapturingPromotionWrongRank() {
    // black pawn on e5, white pawn on d4 - exd4=Q, rank 4 is not promotion rank
    final Board board = new Board("4k3/8/8/4p3/3P4/8/8/4K3 b - - 0 100");
    checkException("exd4=Q", board, SanValidationProblem.FORMAT_PAWN_CAPTURE_NON_PROMOTION_OVERLENGTH);
  }

  private static void checkValid(String san, Board board) {
    boolean isException = false;
    try {
      StrictSanParser.parseText(san, board);
    } catch (@SuppressWarnings("unused") final SanValidationException e) {
      isException = true;
    }
    assertFalse(isException);
  }

  private static void checkException(String san, Board board, SanValidationProblem svp) {
    boolean isException;
    try {
      StrictSanParser.parseText(san, board);
      isException = false;
    } catch (final SanValidationException e) {
      isException = true;
      assertEquals(svp, e.getSanValidationProblem());
    }
    assertTrue(isException);
  }

}
