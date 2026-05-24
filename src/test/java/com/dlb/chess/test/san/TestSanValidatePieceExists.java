package com.dlb.chess.test.san;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.dlb.chess.board.Board;
import com.dlb.chess.san.SanValidationException;
import com.dlb.chess.san.SanValidationProblem;
import com.dlb.chess.san.StrictSanParser;

class TestSanValidatePieceExists {

  @SuppressWarnings("static-method")
  @Test
  void testWhiteNeither() {

    {
      final Board board = new Board("k7/1q6/8/8/8/8/6Q1/7K w - - 0 100");

      checkExceptionNeither("Ra1", board);
      checkExceptionNeither("Nb1", board);
      checkExceptionNeither("Bc1", board);
    }

    {
      final Board board = new Board("k7/1r6/8/8/8/8/6R1/7K w - - 0 100");

      checkExceptionNeither("Qd1", board);
    }
  }

  @SuppressWarnings("static-method")
  @Test
  void testBlackNeither() {

    {
      final Board board = new Board("k7/1q6/8/8/8/8/6Q1/7K b - - 0 100");

      checkExceptionNeither("Rh8", board);
      checkExceptionNeither("Ng8", board);
      checkExceptionNeither("Bf8", board);
    }

    {
      final Board board = new Board("k7/1r6/8/8/8/8/6R1/7K b - - 0 100");

      checkExceptionNeither("Qd8", board);
    }
  }

  private static void checkExceptionNeither(String san, Board board) {
    checkException(san, board, SanValidationProblem.EXISTS_RNBQ_NEITHER);
  }

  @SuppressWarnings("static-method")
  @Test
  void testWhiteFile() {

    {
      final Board board = new Board("k7/rq6/n7/b7/7B/7N/6QR/7K w - - 0 100");

      checkExceptionFile("Raa1", board);
      checkExceptionFile("Nab1", board);

      checkExceptionFile("Bac1", board);
      checkExceptionFile("Qad1", board);
    }
  }

  @SuppressWarnings("static-method")
  @Test
  void testBlackFile() {

    {
      final Board board = new Board("k7/rq6/n7/b7/7B/7N/6QR/7K b - - 0 100");

      checkExceptionFile("Rhd8", board);

      checkExceptionFile("Nhg8", board);
      checkExceptionFile("Bhf8", board);
      checkExceptionFile("Qhf8", board);
    }

  }

  private static void checkExceptionFile(String san, Board board) {
    checkException(san, board, SanValidationProblem.EXISTS_RNBQ_FILE);
  }

  @SuppressWarnings("static-method")
  @Test
  void testWhiteRank() {

    {
      final Board board = new Board("k7/rq6/n7/b7/7B/7N/6QR/7K w - - 0 100");

      checkExceptionRank("R1a1", board);

      checkExceptionRank("N2b1", board);
      checkExceptionRank("B3c1", board);
      checkExceptionRank("Q4d1", board);
    }
  }

  @SuppressWarnings("static-method")
  @Test
  void testBlackRank() {

    {
      final Board board = new Board("k7/rq6/n7/b7/7B/7N/6QR/7K b - - 0 100");

      checkExceptionRank("R8d8", board);
      checkExceptionRank("N7g8", board);

      checkExceptionRank("B6f8", board);
      checkExceptionRank("Q8f8", board);
    }

  }

  private static void checkExceptionRank(String san, Board board) {
    checkException(san, board, SanValidationProblem.EXISTS_RNBQ_RANK);
  }

  @SuppressWarnings("static-method")
  @Test
  void testWhiteSquare() {

    {
      final Board board = new Board("k7/rq6/n7/b7/7B/7N/6QR/7K w - - 0 100");

      checkExceptionSquare("Rb1a1", board);
      checkExceptionSquare("Na3b1", board);
      checkExceptionSquare("Ba3c1", board);
      checkExceptionSquare("Qc4b3", board);
    }
  }

  @SuppressWarnings("static-method")
  @Test
  void testBlackSquare() {

    {
      final Board board = new Board("k7/rq6/n7/b7/7B/7N/6QR/7K b - - 0 100");

      checkExceptionSquare("Rf8e8", board);
      checkExceptionSquare("Nh6g8", board);
      checkExceptionSquare("Bf8h6", board);
      checkExceptionSquare("Qd8e7", board);
    }

  }

  private static void checkExceptionSquare(String san, Board board) {
    checkException(san, board, SanValidationProblem.EXISTS_RNBQ_SQUARE);
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