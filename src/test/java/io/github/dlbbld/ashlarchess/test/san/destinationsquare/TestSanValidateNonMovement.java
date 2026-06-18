// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.test.san.destinationsquare;

import org.junit.jupiter.api.Test;

import io.github.dlbbld.ashlarchess.board.Board;
import io.github.dlbbld.ashlarchess.san.SanValidationProblem;
import io.github.dlbbld.ashlarchess.test.san.TestSanValidateSupport;

class TestSanValidateNonMovement {

  @SuppressWarnings("static-method")
  @Test
  void test() {

    // attention for kings this is disallowed by format definition

    final Board board = new Board();

    TestSanValidateSupport.checkExceptionNonMovement("Ra1a1", board);
    TestSanValidateSupport.checkExceptionNonMovement("Nb1b1", board);
    TestSanValidateSupport.checkExceptionNonMovement("Bc1c1", board);
    TestSanValidateSupport.checkExceptionNonMovement("Qd1d1", board);
    TestSanValidateSupport.checkExceptionFormat("Ke1e1", SanValidationProblem.FORMAT_KING_NON_CASTLING_NON_CAPTURE_OVERLENGTH, board);

    board.moveStrict("e4");

    TestSanValidateSupport.checkExceptionNonMovement("Ra8a8", board);
    TestSanValidateSupport.checkExceptionNonMovement("Nb8b8", board);
    TestSanValidateSupport.checkExceptionNonMovement("Bc8c8", board);
    TestSanValidateSupport.checkExceptionNonMovement("Qd8d8", board);
    TestSanValidateSupport.checkExceptionFormat("Ke8e8", SanValidationProblem.FORMAT_KING_NON_CASTLING_NON_CAPTURE_OVERLENGTH, board);

    board.moveStrict("e5");

    // rooks after moved
    board.moveStrict("a4");
    board.moveStrict("h5");
    board.moveStrict("Ra2");
    board.moveStrict("Rh7");
    TestSanValidateSupport.checkExceptionNonMovement("Ra2a2", board);
    board.moveStrict("Ra3");
    TestSanValidateSupport.checkExceptionNonMovement("Rh7h7", board);
    board.moveStrict("Rh6");

    // knights after moved
    board.moveStrict("Nc3");
    board.moveStrict("Nf6");
    TestSanValidateSupport.checkExceptionNonMovement("Nc3c3", board);
    board.moveStrict("Nd5");
    TestSanValidateSupport.checkExceptionNonMovement("Nf6f6", board);
    board.moveStrict("Nxe4");

    // bishops after moved
    board.moveStrict("Bc4");
    board.moveStrict("d6");
    TestSanValidateSupport.checkExceptionNonMovement("Bc4c4", board);
    board.moveStrict("Bf1");
    board.moveStrict("Bd7");
    board.moveStrict("Bc4");
    TestSanValidateSupport.checkExceptionNonMovement("Bd7d7", board);
    board.moveStrict("Bg4");

    // queens after moved
    board.moveStrict("Qxg4");
    board.moveStrict("Qd7");
    TestSanValidateSupport.checkExceptionNonMovement("Qg4g4", board);
    board.moveStrict("Qh3");
    TestSanValidateSupport.checkExceptionNonMovement("Qd7d7", board);
    board.moveStrict("Qc6");

    // kings after moved
    board.moveStrict("Kd1");
    board.moveStrict("Kd8");
    TestSanValidateSupport.checkExceptionFormat("Kd1d1", SanValidationProblem.FORMAT_KING_NON_CASTLING_NON_CAPTURE_OVERLENGTH, board);
    board.moveStrict("Ke2");
    TestSanValidateSupport.checkExceptionFormat("Kd8d8", SanValidationProblem.FORMAT_KING_NON_CASTLING_NON_CAPTURE_OVERLENGTH, board);
    board.moveStrict("Ke8");

  }

}