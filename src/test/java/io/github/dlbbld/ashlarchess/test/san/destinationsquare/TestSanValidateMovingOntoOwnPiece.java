// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.test.san.destinationsquare;

import org.junit.jupiter.api.Test;

import io.github.dlbbld.ashlarchess.board.Board;
import io.github.dlbbld.ashlarchess.test.san.TestSanValidateSupport;

class TestSanValidateMovingOntoOwnPiece {

  @SuppressWarnings("static-method")
  @Test
  void testUnderstanding() {
    final Board board = Board.fromFenStrict("8/3k4/3r4/R7/4K3/8/8/R7 b - - 0 1");

    TestSanValidateSupport.checkExceptionRnbqkMovingOntoOwnPiece("Rd6", board);
    board.movesStrict("Rc6");
    // rook
    TestSanValidateSupport.checkExceptionRnbqkMovingOntoOwnPiece("Ra1", board);
  }

  @SuppressWarnings("static-method")
  @Test
  void testWhite() {

    final Board board = new Board();

    // rook
    TestSanValidateSupport.checkExceptionRnbqkCapturingOwnPiece("Rxa2", board);

    // knight
    TestSanValidateSupport.checkExceptionRnbqkCapturingOwnPiece("Nxd2", board);

    // bishop
    TestSanValidateSupport.checkExceptionRnbqkCapturingOwnPiece("Bxa2", board);

    // queen
    TestSanValidateSupport.checkExceptionRnbqkCapturingOwnPiece("Qxd2", board);

    // king
    TestSanValidateSupport.checkExceptionRnbqkCapturingOwnPiece("Kxf1", board);

    board.movesStrict("Nc3");
    board.movesStrict("a6");

    // pawn
    TestSanValidateSupport.checkExceptionPawnCaptureOwnPiece("bxc3", board);
  }

  @SuppressWarnings("static-method")
  @Test
  void testBlack() {

    final Board board = new Board();
    board.movesStrict("e4");

    // rook
    TestSanValidateSupport.checkExceptionRnbqkCapturingOwnPiece("Rxa7", board);

    // knight
    TestSanValidateSupport.checkExceptionRnbqkCapturingOwnPiece("Nxd7", board);

    // bishop
    TestSanValidateSupport.checkExceptionRnbqkCapturingOwnPiece("Bxa7", board);

    // queen
    TestSanValidateSupport.checkExceptionRnbqkCapturingOwnPiece("Qxd7", board);

    // king
    TestSanValidateSupport.checkExceptionRnbqkCapturingOwnPiece("Kxf7", board);

    board.movesStrict("Nc6");
    board.movesStrict("d4");

    // pawn
    TestSanValidateSupport.checkExceptionPawnCaptureOwnPiece("bxc6", board);
  }
}
