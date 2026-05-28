// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.test.san.move;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import io.github.dlbbld.ashlarchess.board.Board;
import io.github.dlbbld.ashlarchess.san.SanValidationException;
import io.github.dlbbld.ashlarchess.san.SanValidationProblem;
import io.github.dlbbld.ashlarchess.san.StrictSanParser;

class TestSanValidateRnbqSquarePseudoLegal {

  // --- Not reachable (square fully identifies the piece, always single) ---

  @SuppressWarnings("static-method")
  @Test
  void testWhiteNotReachable() {
    // White bishop on c1 blocked by own knights. Bc1e3 not reachable.
    final Board board = new Board("k7/8/8/8/PPPPPPPP/8/1N1N4/2B1K3 w - - 0 1");
    checkException("Bc1e3", board, SanValidationProblem.NOT_REACHABLE_RNBQ_SQUARE);
  }

  @SuppressWarnings("static-method")
  @Test
  void testBlackNotReachable() {
    // Black bishop on c8 blocked by own knights. Bc8e6 not reachable.
    final Board board = new Board("2b1k3/1n1n4/8/pppppppp/8/8/8/4K3 b - - 0 1");
    checkException("Bc8e6", board, SanValidationProblem.NOT_REACHABLE_RNBQ_SQUARE);
  }

  // --- King in check (square fully identifies the piece, always single) ---

  @SuppressWarnings("static-method")
  @Test
  void testWhiteKingInCheck() {
    // White bishop on e4 pinned along e-file (king e1, rook e8). Be4d5 would expose king.
    final Board board = new Board("4r2k/8/8/8/4B3/8/8/4K3 w - - 0 1");
    checkException("Be4d5", board, SanValidationProblem.KING_EXPOSED_TO_CHECK_RNBQ_SQUARE);
  }

  @SuppressWarnings("static-method")
  @Test
  void testBlackKingInCheck() {
    // Black bishop on e5 pinned along e-file (king e8, rook e1). Be5d4 would expose king.
    final Board board = new Board("4k3/8/8/4b3/8/8/8/4R2K b - - 0 1");
    checkException("Be5d4", board, SanValidationProblem.KING_EXPOSED_TO_CHECK_RNBQ_SQUARE);
  }

  // --- King left in check ---

  @SuppressWarnings("static-method")
  @Test
  void testWhiteKingLeftInCheck() {
    // White king e1 in check from black rook e8. White bishop on c4 can reach Bc4d5 but doesn't resolve check.
    final Board board = new Board("4r2k/8/8/8/2B5/8/8/4K3 w - - 0 1");
    checkException("Bc4d5", board, SanValidationProblem.KING_LEFT_IN_CHECK_RNBQ_SQUARE);
  }

  @SuppressWarnings("static-method")
  @Test
  void testBlackKingLeftInCheck() {
    // Black king e8 in check from white rook e1. Black bishop on c5 can reach Bc5d4 but doesn't resolve check.
    final Board board = new Board("4k3/8/8/2b5/8/8/8/4R2K b - - 0 1");
    checkException("Bc5d4", board, SanValidationProblem.KING_LEFT_IN_CHECK_RNBQ_SQUARE);
  }

  private static void checkException(String san, Board board, SanValidationProblem expectedProblem) {
    boolean isException;
    try {
      StrictSanParser.parseText(san, board);
      isException = false;
    } catch (final SanValidationException e) {
      isException = true;
      assertEquals(expectedProblem, e.getSanValidationProblem());
    }
    assertTrue(isException);
  }

}
