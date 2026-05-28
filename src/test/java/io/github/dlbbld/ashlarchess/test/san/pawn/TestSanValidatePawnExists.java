// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.test.san.pawn;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import io.github.dlbbld.ashlarchess.board.Board;
import io.github.dlbbld.ashlarchess.san.SanValidationException;
import io.github.dlbbld.ashlarchess.san.SanValidationProblem;
import io.github.dlbbld.ashlarchess.san.StrictSanParser;

class TestSanValidatePawnExists {

  @SuppressWarnings("static-method")
  @Test
  void testWhite() {

    {
      final Board board = new Board("4k3/5p2/8/8/8/8/3P4/4K3 w - - 0 100");

      checkException("c1", board, SanValidationProblem.FORMAT_PAWN_FORWARD_PROMOTION_NO_PROMOTION_SYMBOL);
      checkException("c2", board, SanValidationProblem.MOVEMENT_PAWN_FORWARD_BACKWARDS);
      checkException("c3", board);
      checkException("c4", board);
      checkException("c5", board);
      checkException("c6", board);
      checkException("c7", board);
      checkException("c8", board, SanValidationProblem.FORMAT_PAWN_FORWARD_PROMOTION_NO_PROMOTION_SYMBOL);

      checkException("fxg1", board, SanValidationProblem.FORMAT_PAWN_CAPTURE_PROMOTION_NO_PROMOTION_SYMBOL);
      checkException("fxg2", board, SanValidationProblem.MOVEMENT_PAWN_FORWARD_BACKWARDS);
      checkException("fxg3", board);
      checkException("fxg4", board);
      checkException("fxg5", board);
      checkException("fxg6", board);
      checkException("fxg7", board);
      checkException("fxg8", board, SanValidationProblem.FORMAT_PAWN_CAPTURE_PROMOTION_NO_PROMOTION_SYMBOL);
    }

  }

  @SuppressWarnings("static-method")
  @Test
  void testBlack() {

    {
      final Board board = new Board("4k3/5p2/8/8/8/8/3P4/4K3 b - - 0 100");

      checkException("h8", board, SanValidationProblem.FORMAT_PAWN_FORWARD_PROMOTION_NO_PROMOTION_SYMBOL);
      checkException("h7", board, SanValidationProblem.MOVEMENT_PAWN_FORWARD_BACKWARDS);
      checkException("h6", board);
      checkException("h5", board);
      checkException("h4", board);
      checkException("h3", board);
      checkException("h2", board);
      checkException("h1", board, SanValidationProblem.FORMAT_PAWN_FORWARD_PROMOTION_NO_PROMOTION_SYMBOL);

      checkException("bxa8", board, SanValidationProblem.FORMAT_PAWN_CAPTURE_PROMOTION_NO_PROMOTION_SYMBOL);
      checkException("bxa7", board, SanValidationProblem.MOVEMENT_PAWN_FORWARD_BACKWARDS);
      checkException("bxa6", board);
      checkException("bxa5", board);
      checkException("bxa4", board);
      checkException("bxa3", board);
      checkException("bxa2", board);
      checkException("bxa1", board, SanValidationProblem.FORMAT_PAWN_CAPTURE_PROMOTION_NO_PROMOTION_SYMBOL);

    }

  }

  private static void checkException(String san, Board board) {
    checkException(san, board, SanValidationProblem.EXISTS_PAWN);
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