package io.github.dlbbld.ashlarchess.test.san.destinationsquare;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import io.github.dlbbld.ashlarchess.board.Board;
import io.github.dlbbld.ashlarchess.san.SanValidationException;
import io.github.dlbbld.ashlarchess.san.SanValidationProblem;
import io.github.dlbbld.ashlarchess.san.StrictSanParser;

class TestSanValidateNonCapturingMovingOntoOpponentPiece {

  @SuppressWarnings("static-method")
  @Test
  void testWhite() {

    // rook
    {
      final Board board = new Board();

      board.moveStrict("a4");
      board.moveStrict("d5");
      board.moveStrict("Ra3");
      board.moveStrict("Bh3");
      checkException("Rh3", board);
    }
    // knight
    {
      final Board board = new Board();

      board.moveStrict("Nc3");
      board.moveStrict("Nf6");
      board.moveStrict("Ne4");
      board.moveStrict("a6");
      checkException("Nf6", board);
    }
    // bishop
    {
      final Board board = new Board();

      board.moveStrict("e4");
      board.moveStrict("e5");
      board.moveStrict("Bb5");
      board.moveStrict("Nc6");
      checkException("Bc6", board);
    }
    // queen
    {
      final Board board = new Board();

      board.moveStrict("e4");
      board.moveStrict("e5");
      board.moveStrict("Qf3");
      board.moveStrict("d5");
      checkException("Qf7", board);
    }
    // king
    {
      final Board board = new Board();

      board.moveStrict("e4");
      board.moveStrict("e5");
      board.moveStrict("d3");
      board.moveStrict("Bc5");
      board.moveStrict("f4");
      board.moveStrict("Bf2+");
      checkException("Kf2", board);
    }
    // pawn diagonal move must contain capture symbol by format specification
  }

  @SuppressWarnings("static-method")
  @Test
  void testBlack() {
    // rook
    {
      final Board board = new Board();

      board.moveStrict("e4");
      board.moveStrict("h5");
      board.moveStrict("Ba6");
      board.moveStrict("Rh6");
      board.moveStrict("a3");
      checkException("Ra6", board);
    }
    // knight
    {
      final Board board = new Board();

      board.moveStrict("Nc3");
      board.moveStrict("Nf6");
      board.moveStrict("Ne4");
      checkException("Ne4", board);
    }
    // bishop
    {
      final Board board = new Board();

      board.moveStrict("Nf3");
      board.moveStrict("d5");
      board.moveStrict("e4");
      board.moveStrict("Bg4");
      board.moveStrict("d4");
      checkException("Bf3", board);
    }
    // queen
    {
      final Board board = new Board();

      board.moveStrict("e4");
      board.moveStrict("e5");
      board.moveStrict("a3");
      board.moveStrict("Qf6");
      board.moveStrict("d4");
      checkException("Qf2", board);
    }
    // king
    {
      final Board board = new Board();

      board.moveStrict("e4");
      board.moveStrict("e5");
      board.moveStrict("Bc4");
      board.moveStrict("d6");
      board.moveStrict("Bxf7+");
      checkException("Kf7", board);
    }
    // pawn diagonal move must contain capture symbol by format specification
  }

  private static void checkException(String san, Board board) {
    boolean isException;
    try {
      StrictSanParser.parseText(san, board);
      isException = false;
    } catch (final SanValidationException e) {
      isException = true;
      assertEquals(SanValidationProblem.DESTINATION_RNBQK_OPPONENT_NON_KING_NO_CAPTURE_SYMBOL,
          e.getSanValidationProblem());
    }
    assertTrue(isException);
  }

}