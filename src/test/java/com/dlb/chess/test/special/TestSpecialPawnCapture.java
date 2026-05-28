package com.dlb.chess.test.special;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.dlb.chess.board.Board;
import com.dlb.chess.common.constants.EnumConstants;
import com.dlb.chess.san.SanValidationException;

class TestSpecialPawnCapture implements EnumConstants {

  @SuppressWarnings("static-method")
  @Test
  void testWhite() throws Exception {
    // two pawns can capture the same piece
    // caused a bug
    final Board board = new Board("rrrrrrrr/PPPPPPPP/8/8/8/8/8/2k1K3 w - - 0 100");

    board.moveStrict("bxa8=Q");
    board.unmove();
    board.moveStrict("bxc8=Q");
    board.unmove();

    board.moveStrict("cxd8=Q");
    board.unmove();

    checkException(board, "exd8=Q");
  }

  @SuppressWarnings("static-method")
  @Test
  void testBlack() throws Exception {
    // two pawns can capture the same piece
    // caused a bug
    final Board board = new Board("3k1K2/8/8/8/8/8/pppppppp/QQQQQQQQ b - - 0 100");

    board.moveStrict("bxa1=Q");
    board.unmove();
    board.moveStrict("bxc1=Q");
    board.unmove();

    board.moveStrict("fxe1=Q");
    board.unmove();

    checkException(board, "dxe1=Q");

  }

  private static void checkException(Board board, String san) {
      boolean isCorrectException = false;
    try {
      board.moveStrict(san);
    } catch (@SuppressWarnings("unused") final SanValidationException sve) {
      isCorrectException = true;
    }
    assertTrue(isCorrectException);
  }
}
