package com.dlb.chess.test.san.destinationsquare;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.dlb.chess.board.Board;
import com.dlb.chess.san.SanValidationException;
import com.dlb.chess.san.SanValidationProblem;
import com.dlb.chess.san.StrictSanParser;

class TestSanValidateCapturingOpponentKing {

  @SuppressWarnings("static-method")
  @Test
  void testWhite() {
    // Position: black king on a8, white pieces on rank 1-2, none attacking a8
    // White: K on f1, Q on d1, B on c1, N on b1, R on h2
    final Board board = new Board("k7/8/8/8/8/8/7R/1NBQ1K2 w - - 0 1");

    // rook
    checkException("Rxa8", board);

    // knight
    checkException("Nxa8", board);

    // bishop
    checkException("Bxa8", board);

    // queen
    checkException("Qxa8", board);

    // king
    checkException("Kxa8", board);
  }

  @SuppressWarnings("static-method")
  @Test
  void testBlack() {
    // Position: white king on a1, black pieces on rank 7-8, none attacking a1
    // Black: k on f8, q on d8, b on c8, n on b8, r on h7
    final Board board = new Board("1nbq1k2/7r/8/8/8/8/8/K7 b - - 0 1");

    // rook
    checkException("Rxa1", board);

    // knight
    checkException("Nxa1", board);

    // bishop
    checkException("Bxa1", board);

    // queen
    checkException("Qxa1", board);

    // king
    checkException("Kxa1", board);
  }

  private static void checkException(String san, Board board) {
    boolean isException;
    try {
      StrictSanParser.parseText(san, board);
      isException = false;
    } catch (final SanValidationException e) {
      isException = true;
      assertEquals(SanValidationProblem.DESTINATION_RNBQK_OPPONENT_KING_CAPTURING, e.getSanValidationProblem());
    }
    assertTrue(isException);
  }

}
