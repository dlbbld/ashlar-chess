package io.github.dlbbld.ashlarchess.test.san.destinationsquare;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import io.github.dlbbld.ashlarchess.board.Board;
import io.github.dlbbld.ashlarchess.san.SanValidationException;
import io.github.dlbbld.ashlarchess.san.SanValidationProblem;
import io.github.dlbbld.ashlarchess.san.StrictSanParser;

class TestSanValidateCapturingNoPiece {

  @SuppressWarnings("static-method")
  @Test
  void testWhite() {

    final Board board = new Board();

    // pawn
    checkExceptionPawn("axb3", board);

    // rook
    board.movesStrict("a3");
    board.movesStrict("a6");

    // knight
    checkExceptionRnbqk("Nxc3", board);

    // bishop
    board.movesStrict("b3");
    board.movesStrict("b6");
    checkExceptionRnbqk("Bxb2", board);

    // queen
    board.movesStrict("Bb2");
    board.movesStrict("Bb7");
    checkExceptionRnbqk("Qxc1", board);

    // king
    board.movesStrict("Qc1");
    board.movesStrict("Qc8");
    checkExceptionRnbqk("Kxd1", board);
  }

  @SuppressWarnings("static-method")
  @Test
  void testBlack() {

    final Board board = new Board();

    // pawn
    board.movesStrict("a3");
    checkExceptionPawn("axb6", board);

    // rook

    board.movesStrict("a6");
    board.movesStrict("a4");
    checkExceptionRnbqk("Rxa7", board);

    // knight
    checkExceptionRnbqk("Nxc6", board);

    // bishop
    board.movesStrict("b6");
    board.movesStrict("b3");
    checkExceptionRnbqk("Bxb7", board);

    // queen
    board.movesStrict("Bb7");
    board.movesStrict("Bb2");
    checkExceptionRnbqk("Qxc8", board);

    // king
    board.movesStrict("Qc8");
    board.movesStrict("Qc1");
    checkExceptionRnbqk("Kxd8", board);
  }

  private static void checkExceptionPawn(String san, Board board) {
    checkException(san, board, SanValidationProblem.DESTINATION_PAWN_CAPTURE_EMPTY_NOT_EN_PASSANT);
  }

  private static void checkExceptionRnbqk(String san, Board board) {
    checkException(san, board, SanValidationProblem.DESTINATION_RNBQK_EMPTY_CAPTURE_SYMBOL);
  }

  private static void checkException(String san, Board board, SanValidationProblem expected) {
    boolean isException;
    try {
      StrictSanParser.parseText(san, board);
      isException = false;
    } catch (final SanValidationException e) {
      isException = true;
      assertEquals(expected, e.getSanValidationProblem());
    }
    assertTrue(isException);
  }

}