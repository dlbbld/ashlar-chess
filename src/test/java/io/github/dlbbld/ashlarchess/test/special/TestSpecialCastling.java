// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.test.special;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import io.github.dlbbld.ashlarchess.board.Board;
import io.github.dlbbld.ashlarchess.common.constants.EnumConstants;
import io.github.dlbbld.ashlarchess.san.SanValidationException;

class TestSpecialCastling implements EnumConstants {

  @SuppressWarnings("static-method")
  @Test
  void testWhite() throws Exception {
    {
      // white has king-side castling rights but not queen side
      // try queen side castling
      final Board board = new Board("4k3/8/8/8/8/8/8/R3K2R w K - 0 100");

      board.moveStrict("O-O");
      board.unmove();

      checkException(board, "O-O-O");
    }
    {
      // white has queen-side castling rights but not king side
      // try king side castling
      final Board board = new Board("4k3/8/8/8/8/8/8/R3K2R w Q - 0 100");

      board.moveStrict("O-O-O");
      board.unmove();

      checkException(board, "O-O");
    }
  }

  @SuppressWarnings("static-method")
  @Test
  void testBlack() throws Exception {
    {
      // black has king-side castling rights but not queen side
      // try queen side castling
      final Board board = new Board("r3k2r/8/8/8/8/8/8/R3K2R b k - 0 100");

      board.moveStrict("O-O");
      board.unmove();

      checkException(board, "O-O-O");
    }
    {
      // black has queen-side castling rights but not king side
      // try king side castling
      final Board board = new Board("r3k2r/8/8/8/8/8/8/R3K2R b q - 0 100");

      board.moveStrict("O-O-O");
      board.unmove();

      checkException(board, "O-O");
    }
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
