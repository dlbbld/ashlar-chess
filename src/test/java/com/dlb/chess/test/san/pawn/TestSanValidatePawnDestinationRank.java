package com.dlb.chess.test.san.pawn;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.dlb.chess.board.Board;
import com.dlb.chess.san.SanValidationException;
import com.dlb.chess.san.SanValidationProblem;
import com.dlb.chess.san.StrictSanParser;

class TestSanValidatePawnDestinationRank {

  @SuppressWarnings("static-method")
  @Test
  void testWhite() {

    {
      final Board board = new Board("4k3/5p2/8/8/8/8/3P4/4K3 w - - 0 100");

      checkException("d2", board);
      checkException("d1=Q", board);

      checkException("dxc2", board);
      checkException("dxc1=Q", board);

    }

  }

  @SuppressWarnings("static-method")
  @Test
  void testBlack() {

    {
      final Board board = new Board("4k3/5p2/8/8/8/8/3P4/4K3 b - - 0 100");

      checkException("f7", board);
      checkException("f8=Q", board);

      checkException("fxg7", board);
      checkException("fxg8=Q", board);

    }

  }

  private static void checkException(String san, Board board) {
    checkException(san, board, SanValidationProblem.MOVEMENT_PAWN_FORWARD_BACKWARDS);
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