// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.test.san;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import io.github.dlbbld.ashlarchess.board.Board;
import io.github.dlbbld.ashlarchess.san.SanValidationException;
import io.github.dlbbld.ashlarchess.san.SanValidationProblem;
import io.github.dlbbld.ashlarchess.san.StrictSanParser;

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