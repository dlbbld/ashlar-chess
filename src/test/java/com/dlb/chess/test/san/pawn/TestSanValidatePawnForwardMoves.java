package com.dlb.chess.test.san.pawn;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.dlb.chess.board.Board;
import com.dlb.chess.san.SanValidationException;
import com.dlb.chess.san.SanValidationProblem;
import com.dlb.chess.san.StrictSanParser;

class TestSanValidatePawnForwardMoves {

  @SuppressWarnings("static-method")
  @Test
  void testWhite() {

    {
      final Board board = new Board();

      board.moveStrict("d4");
      board.moveStrict("d5");
      checkExceptionOpponent("d5", board);
    }

    {
      final Board board = new Board();

      board.moveStrict("d4");
      board.moveStrict("e5");
      board.moveStrict("dxe5");
      board.moveStrict("a6");
      board.moveStrict("e4");
      board.moveStrict("a5");
      checkExceptionOwn("e5", board);
    }
  }

  @SuppressWarnings("static-method")
  @Test
  void testBlack() {

    {
      final Board board = new Board();

      board.moveStrict("d4");
      board.moveStrict("d5");
      board.moveStrict("a3");
      checkExceptionOpponent("d4", board);
    }

    {
      final Board board = new Board();

      board.moveStrict("d4");
      board.moveStrict("e5");
      board.moveStrict("a3");
      board.moveStrict("exd4");
      board.moveStrict("a4");
      board.moveStrict("d5");
      board.moveStrict("a5");
      checkExceptionOwn("d4", board);
    }
  }

  private static void checkExceptionOpponent(String san, Board board) {
    checkException(san, board, SanValidationProblem.DESTINATION_PAWN_FORWARD_OPPONENT_PIECE_NOT_KING);
  }

  private static void checkExceptionOwn(String san, Board board) {
    checkException(san, board, SanValidationProblem.DESTINATION_PAWN_FORWARD_OWN_PIECE);
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