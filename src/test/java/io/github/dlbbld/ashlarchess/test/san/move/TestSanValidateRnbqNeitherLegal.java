package io.github.dlbbld.ashlarchess.test.san.move;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import io.github.dlbbld.ashlarchess.board.Board;
import io.github.dlbbld.ashlarchess.san.SanValidationException;
import io.github.dlbbld.ashlarchess.san.SanValidationProblem;
import io.github.dlbbld.ashlarchess.san.StrictSanParser;

class TestSanValidateRnbqNeitherLegal {

  @SuppressWarnings("static-method")
  @Test
  void testPieceNeither1NoLegalMove() {

    final Board board = new Board();

    // white
    checkException(board, "Ra3", SanValidationProblem.NOT_REACHABLE_RNBQ_NEITHER_MULTIPLE);
    checkException(board, "Nb4", SanValidationProblem.NOT_REACHABLE_RNBQ_NEITHER_MULTIPLE);
    checkException(board, "Be3", SanValidationProblem.NOT_REACHABLE_RNBQ_NEITHER_MULTIPLE);
    checkException(board, "Qd5", SanValidationProblem.NOT_REACHABLE_RNBQ_NEITHER_SINGLE);
    checkException(board, "Rxa8", SanValidationProblem.NOT_REACHABLE_RNBQ_NEITHER_MULTIPLE);
    checkException(board, "Nxb8", SanValidationProblem.NOT_REACHABLE_RNBQ_NEITHER_MULTIPLE);
    checkException(board, "Bxc8", SanValidationProblem.NOT_REACHABLE_RNBQ_NEITHER_MULTIPLE);
    checkException(board, "Qxd8", SanValidationProblem.NOT_REACHABLE_RNBQ_NEITHER_SINGLE);

    // black
    board.movesStrict("e4");
    checkException(board, "Ra6", SanValidationProblem.NOT_REACHABLE_RNBQ_NEITHER_MULTIPLE);
    checkException(board, "Ne5", SanValidationProblem.NOT_REACHABLE_RNBQ_NEITHER_MULTIPLE);
    checkException(board, "Bg4", SanValidationProblem.NOT_REACHABLE_RNBQ_NEITHER_MULTIPLE);
    checkException(board, "Qd6", SanValidationProblem.NOT_REACHABLE_RNBQ_NEITHER_SINGLE);
    checkException(board, "Rxa2", SanValidationProblem.NOT_REACHABLE_RNBQ_NEITHER_MULTIPLE);
    checkException(board, "Nxb2", SanValidationProblem.NOT_REACHABLE_RNBQ_NEITHER_MULTIPLE);
    checkException(board, "Bxc2", SanValidationProblem.NOT_REACHABLE_RNBQ_NEITHER_MULTIPLE);
    checkException(board, "Qxd2", SanValidationProblem.NOT_REACHABLE_RNBQ_NEITHER_SINGLE);

  }

  @SuppressWarnings("static-method")
  @Test
  void testPieceNeither2MultipleLegalMoves() {

    final Board board = new Board();

    // white
    // white rook
    board.movesStrict("a4", "a5", "h4", "h5", "Ra3", "Ra6");
    checkException(board, "Rh3",
        SanValidationProblem.INSUFFICIENTLY_SPECIFIED_RNBQ_NEITHER_EITHER_FILE_OR_RANK_OR_SQUARE_REQUIRED);

    board.moveStrict("Rah3");
    // black rook
    checkException(board, "Rh6",
        SanValidationProblem.INSUFFICIENTLY_SPECIFIED_RNBQ_NEITHER_EITHER_FILE_OR_RANK_OR_SQUARE_REQUIRED);

    board.moveStrict("Rah6");

    // white knight
    board.movesStrict("Nc3", "Nc6", "Nf3", "Nf6", "Nd4", "Nd5");
    checkException(board, "Nb5",
        SanValidationProblem.INSUFFICIENTLY_SPECIFIED_RNBQ_NEITHER_EITHER_FILE_OR_RANK_OR_SQUARE_REQUIRED);

    board.moveStrict("Ncb5");
    // black knight
    checkException(board, "Nb4",
        SanValidationProblem.INSUFFICIENTLY_SPECIFIED_RNBQ_NEITHER_EITHER_FILE_OR_RANK_OR_SQUARE_REQUIRED);

    board.moveStrict("Ncb4");

    // white bishop
    board.movesStrict("f4", "c5", "f5", "c4", "f6", "c3", "fxg7", "cxb2", "gxh8=B", "e6", "d3", "Bd6", "Bd2", "Ke7",
        "Qa1", "bxa1=B", "Bxh6", "Bxd4");
    checkException(board, "Bg7",
        SanValidationProblem.INSUFFICIENTLY_SPECIFIED_RNBQ_NEITHER_EITHER_FILE_OR_RANK_OR_SQUARE_REQUIRED);

    board.moveStrict("B6g7");
    // black bishop
    checkException(board, "Be5",
        SanValidationProblem.INSUFFICIENTLY_SPECIFIED_RNBQ_NEITHER_EITHER_FILE_OR_RANK_OR_SQUARE_REQUIRED);

    board.moveStrict("B4e5");

    // white queen
    board.movesStrict("c4", "f5", "cxd5", "f4", "dxe6", "f3", "exd7", "fxg2", "dxc8=Q", "gxh1=Q", "d4", "Qe4", "dxe5",
        "Kf7", "exd6");
    checkException(board, "Qxh4+",
        SanValidationProblem.INSUFFICIENTLY_SPECIFIED_RNBQ_NEITHER_EITHER_FILE_OR_RANK_OR_SQUARE_REQUIRED);

    board.moveStrict("Qdxh4+");
    board.movesStrict("Kd1", "Na2", "d7", "Nb4", "d8=Q", "Nc2", "Na7", "Kg6", "Nb5", "Kh7", "Qdc7");
    // black queen
    checkException(board, "Qf4",
        SanValidationProblem.INSUFFICIENTLY_SPECIFIED_RNBQ_NEITHER_EITHER_FILE_OR_RANK_OR_SQUARE_REQUIRED);

    board.moveStrict("Qhf4");
    // white queen
    checkException(board, "Qd7",
        SanValidationProblem.INSUFFICIENTLY_SPECIFIED_RNBQ_NEITHER_EITHER_FILE_OR_RANK_OR_SQUARE_REQUIRED);

    board.moveStrict("Q8d7");
    board.movesStrict("Qfe3", "Qf7");
    // black queen
    checkException(board, "Qf4",
        SanValidationProblem.INSUFFICIENTLY_SPECIFIED_RNBQ_NEITHER_EITHER_FILE_OR_RANK_OR_SQUARE_REQUIRED);

    board.moveStrict("Q4f4");
  }

  private static void checkException(Board board, String san, SanValidationProblem expectedValidation) {
    boolean isException;
    try {
      StrictSanParser.parseText(san, board);
      isException = false;
    } catch (final SanValidationException e) {
      isException = true;
      assertEquals(expectedValidation, e.getSanValidationProblem());
    }
    assertTrue(isException);
  }
}
