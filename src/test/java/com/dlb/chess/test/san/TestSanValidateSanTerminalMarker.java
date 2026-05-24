package com.dlb.chess.test.san;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.dlb.chess.board.Board;
import com.dlb.chess.san.SanValidationException;
import com.dlb.chess.san.SanValidationProblem;

class TestSanValidateSanTerminalMarker {

  @SuppressWarnings("static-method")
  @Test
  void testWhite() {
    final Board board = new Board();

    checkException("e4#", SanValidationProblem.CHECKMATE_SYMBOL_BUT_NO_CHECK, board);
    checkException("e4+", SanValidationProblem.CHECK_SYMBOL_BUT_NO_CHECK, board);

    board.movesStrict("e4", "e5", "Bc4", "Bc5");
    checkException("Bxf7#", SanValidationProblem.CHECKMATE_SYMBOL_BUT_CHECK_ONLY, board);
    checkException("Bxf7", SanValidationProblem.NO_SYMBOL_BUT_CHECK, board);

    board.movesStrict("a3", "Nc6", "Qf3", "d6");
    checkException("Qxf7+", SanValidationProblem.CHECK_SYMBOL_BUT_CHECKMATE, board);
    checkException("Qxf7", SanValidationProblem.NO_SYMBOL_BUT_CHECKMATE, board);
  }

  @SuppressWarnings("static-method")
  @Test
  void testBlack() {
    final Board board = new Board();

    board.moveStrict("e4");

    checkException("e5#", SanValidationProblem.CHECKMATE_SYMBOL_BUT_NO_CHECK, board);
    checkException("e5+", SanValidationProblem.CHECK_SYMBOL_BUT_NO_CHECK, board);

    board.movesStrict("e5", "Bc4", "Bc5", "Nc3");
    checkException("Bxf2#", SanValidationProblem.CHECKMATE_SYMBOL_BUT_CHECK_ONLY, board);
    checkException("Bxf2", SanValidationProblem.NO_SYMBOL_BUT_CHECK, board);

    board.movesStrict("Qf6", "d3");
    checkException("Qxf2+", SanValidationProblem.CHECK_SYMBOL_BUT_CHECKMATE, board);
    checkException("Qxf2", SanValidationProblem.NO_SYMBOL_BUT_CHECKMATE, board);
  }

  private static void checkException(String san, SanValidationProblem problem, Board board) {
    boolean isException;
    try {
      board.moveStrict(san);
      isException = false;
    } catch (final SanValidationException e) {
      isException = true;
      assertEquals(problem, e.getSanValidationProblem());
    }
    assertTrue(isException);
  }

}