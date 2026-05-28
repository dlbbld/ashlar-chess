package com.dlb.chess.test.san.pawn;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.dlb.chess.board.Board;
import com.dlb.chess.san.SanValidationException;
import com.dlb.chess.san.SanValidationProblem;
import com.dlb.chess.san.StrictSanParser;

class TestSanValidatePawnCapturingDiagonal {

  @SuppressWarnings("static-method")
  @Test
  void testMissingValidation() {
    final Board board = new Board("8/P3k3/8/3p4/3P4/7P/8/4K3 w - - 0 100");
    checkValid("a8=Q", board);
  }

  // --- White capturing ---

  @SuppressWarnings("static-method")
  @Test
  void testWhiteValidDiagonal() {
    // white pawn on d4, black pawn on c5 and e5
    final Board board = new Board("4k3/8/8/2p1p3/3P4/8/8/4K3 w - - 0 100");
    checkValid("dxc5", board);
    checkValid("dxe5", board);
  }

  @SuppressWarnings("static-method")
  @Test
  void testWhiteInvalidDiagonal() {
    // white pawn on d4 - dxf5 and dxa5 are not adjacent file captures
    final Board board = new Board("4k3/8/8/p4p2/3P4/8/8/4K3 w - - 0 100");
    checkException("dxf5", board, SanValidationProblem.MOVEMENT_PAWN_CAPTURE_NON_ADJACENT_FILE);
    checkException("dxa5", board, SanValidationProblem.MOVEMENT_PAWN_CAPTURE_NON_ADJACENT_FILE);
  }

  @SuppressWarnings("static-method")
  @Test
  void testWhiteInvalidDiagonalSameFile() {
    // white pawn on d4 - dxd5 is same file, not diagonal
    // (format: this is actually parsed as non-capturing, but let's check dxb5)
    final Board board = new Board("4k3/8/8/1p6/3P4/8/8/4K3 w - - 0 100");
    checkException("dxb5", board, SanValidationProblem.MOVEMENT_PAWN_CAPTURE_NON_ADJACENT_FILE);
  }

  @SuppressWarnings("static-method")
  @Test
  void testWhiteEdgeFileA() {
    // white pawn on a4 - can only capture on b-file
    final Board board = new Board("4k3/8/8/1p6/P7/8/8/4K3 w - - 0 100");
    checkValid("axb5", board);
    checkException("axc5", board, SanValidationProblem.MOVEMENT_PAWN_CAPTURE_NON_ADJACENT_FILE);
  }

  @SuppressWarnings("static-method")
  @Test
  void testWhiteEdgeFileH() {
    // white pawn on h4 - can only capture on g-file
    final Board board = new Board("4k3/8/8/6p1/7P/8/8/4K3 w - - 0 100");
    checkValid("hxg5", board);
    checkException("hxf5", board, SanValidationProblem.MOVEMENT_PAWN_CAPTURE_NON_ADJACENT_FILE);
  }

  // --- White forward capture (same file) ---

  @SuppressWarnings("static-method")
  @Test
  void testWhiteForwardCapture() {
    // white pawn on b3 - bxb4 is forward, not diagonal
    final Board board = new Board("4k3/8/8/8/1p6/1P6/8/4K3 w - - 0 100");
    checkException("bxb4", board, SanValidationProblem.MOVEMENT_PAWN_CAPTURE_NON_ADJACENT_FILE);
  }

  @SuppressWarnings("static-method")
  @Test
  void testWhiteForwardCaptureVariousFiles() {
    // white pawns on a2, d4, h3
    final Board board = new Board("4k3/8/8/3p4/3P4/7P/P7/4K3 w - - 0 100");
    checkException("axa3", board, SanValidationProblem.MOVEMENT_PAWN_CAPTURE_NON_ADJACENT_FILE);
    checkException("dxd5", board, SanValidationProblem.MOVEMENT_PAWN_CAPTURE_NON_ADJACENT_FILE);
    checkException("hxh4", board, SanValidationProblem.MOVEMENT_PAWN_CAPTURE_NON_ADJACENT_FILE);
  }

  // --- Black forward capture (same file) ---

  @SuppressWarnings("static-method")
  @Test
  void testBlackForwardCapture() {
    // black pawn on e5 - exe4 is forward, not diagonal
    final Board board = new Board("4k3/8/8/4p3/4P3/8/8/4K3 b - - 0 100");
    checkException("exe4", board, SanValidationProblem.MOVEMENT_PAWN_CAPTURE_NON_ADJACENT_FILE);
  }

  @SuppressWarnings("static-method")
  @Test
  void testBlackForwardCaptureVariousFiles() {
    // black pawns on a7, d5, h6
    final Board board = new Board("4k3/p7/7p/3p4/8/8/8/4K3 b - - 0 100");
    checkException("axa6", board, SanValidationProblem.MOVEMENT_PAWN_CAPTURE_NON_ADJACENT_FILE);
    checkException("dxd4", board, SanValidationProblem.MOVEMENT_PAWN_CAPTURE_NON_ADJACENT_FILE);
    checkException("hxh5", board, SanValidationProblem.MOVEMENT_PAWN_CAPTURE_NON_ADJACENT_FILE);
  }

  // --- Black capturing ---

  @SuppressWarnings("static-method")
  @Test
  void testBlackValidDiagonal() {
    // black pawn on e5, white pawns on d4 and f4
    final Board board = new Board("4k3/8/8/4p3/3P1P2/8/8/4K3 b - - 0 100");
    checkValid("exd4", board);
    checkValid("exf4", board);
  }

  @SuppressWarnings("static-method")
  @Test
  void testBlackInvalidDiagonal() {
    // black pawn on e5 - exb4 and exh4 are not adjacent
    final Board board = new Board("4k3/8/8/4p3/1P5P/8/8/4K3 b - - 0 100");
    checkException("exb4", board, SanValidationProblem.MOVEMENT_PAWN_CAPTURE_NON_ADJACENT_FILE);
    checkException("exh4", board, SanValidationProblem.MOVEMENT_PAWN_CAPTURE_NON_ADJACENT_FILE);
  }

  @SuppressWarnings("static-method")
  @Test
  void testBlackEdgeFileA() {
    // black pawn on a5 - can only capture on b-file
    final Board board = new Board("4k3/8/8/p7/1P6/8/8/4K3 b - - 0 100");
    checkValid("axb4", board);
    checkException("axc4", board, SanValidationProblem.MOVEMENT_PAWN_CAPTURE_NON_ADJACENT_FILE);
  }

  @SuppressWarnings("static-method")
  @Test
  void testBlackEdgeFileH() {
    // black pawn on h5 - can only capture on g-file
    final Board board = new Board("4k3/8/8/7p/6P1/8/8/4K3 b - - 0 100");
    checkValid("hxg4", board);
    checkException("hxf4", board, SanValidationProblem.MOVEMENT_PAWN_CAPTURE_NON_ADJACENT_FILE);
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
