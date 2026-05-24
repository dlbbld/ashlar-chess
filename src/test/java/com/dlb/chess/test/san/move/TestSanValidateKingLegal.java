package com.dlb.chess.test.san.move;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.dlb.chess.board.Board;
import com.dlb.chess.san.SanValidationException;
import com.dlb.chess.san.SanValidationProblem;
import com.dlb.chess.san.StrictSanParser;

class TestSanValidateKingLegal {

  // --- Not reachable, non-capturing ---

  // Each test FEN includes a corner rook so that the position is not in mutual insufficient
  // material (which would otherwise trigger the strict-pipeline GAME_ALREADY_ENDED check before
  // SAN validation runs). The rook is placed where it neither attacks the king-mobility paths
  // under test nor checks any king.

  @SuppressWarnings("static-method")
  @Test
  void testWhiteNotReachable() {
    // White king e1 trying to move three squares away to e4.
    final Board board = new Board("r6k/8/8/8/8/8/8/4K3 w - - 0 1");
    checkException("Ke4", board, SanValidationProblem.NOT_REACHABLE_KING_NON_CASTLING);
  }

  @SuppressWarnings("static-method")
  @Test
  void testBlackNotReachable() {
    // Black king e8 trying to move three squares away to e5.
    final Board board = new Board("4k3/8/8/8/8/8/8/R6K b - - 0 1");
    checkException("Ke5", board, SanValidationProblem.NOT_REACHABLE_KING_NON_CASTLING);
  }

  // --- Not reachable, capturing ---

  @SuppressWarnings("static-method")
  @Test
  void testWhiteCaptureNotReachable() {
    // White king e1 trying to capture a black knight three squares away on e4.
    final Board board = new Board("r6k/8/8/8/4n3/8/8/4K3 w - - 0 1");
    checkException("Kxe4", board, SanValidationProblem.NOT_REACHABLE_KING_NON_CASTLING);
  }

  @SuppressWarnings("static-method")
  @Test
  void testBlackCaptureNotReachable() {
    // Black king e8 trying to capture a white knight three squares away on e5.
    final Board board = new Board("4k3/8/8/4N3/8/8/8/R6K b - - 0 1");
    checkException("Kxe5", board, SanValidationProblem.NOT_REACHABLE_KING_NON_CASTLING);
  }

  private static void checkException(String san, Board board, SanValidationProblem expectedProblem) {
    boolean isException;
    try {
      StrictSanParser.parseText(san, board);
      isException = false;
    } catch (final SanValidationException e) {
      isException = true;
      assertEquals(expectedProblem, e.getSanValidationProblem());
    }
    assertTrue(isException);
  }

}
