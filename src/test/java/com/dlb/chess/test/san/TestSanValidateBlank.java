package com.dlb.chess.test.san;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.dlb.chess.board.Board;
import com.dlb.chess.san.SanValidationException;
import com.dlb.chess.san.SanValidationProblem;
import com.dlb.chess.san.StrictSanParser;

class TestSanValidateBlank {

  @SuppressWarnings("static-method")
  @Test
  void test() {

    final Board board = new Board();

    checkException("", board);
    checkException(" ", board);

    board.moveStrict("e4");

    checkException("", board);
    checkException(" ", board);

    board.moveStrict("e5");

    checkException("", board);
    checkException(" ", board);

  }

  private static void checkException(String san, Board board) {
    boolean isException;
    try {
      StrictSanParser.parseText(san, board);
      isException = false;
    } catch (final SanValidationException e) {
      isException = true;
      assertEquals(SanValidationProblem.FORMAT_BLANK, e.getSanValidationProblem());
    }
    assertTrue(isException);
  }

}