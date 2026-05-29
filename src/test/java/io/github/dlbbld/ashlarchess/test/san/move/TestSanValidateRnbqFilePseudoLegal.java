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

class TestSanValidateRnbqFilePseudoLegal {

  // --- Not reachable, single piece on file ---

  @SuppressWarnings("static-method")
  @Test
  void testWhiteNotReachableSingle() {
    // Single white bishop on c1, blocked by own knights on b2 and d2. Bcb3 not reachable.
    final Board board = new Board("k7/8/8/8/PPPPPPPP/8/1N1N4/2B1K3 w - - 0 1");
    checkException("Bcb3", board, SanValidationProblem.NOT_REACHABLE_RNBQ_FILE_SINGLE);
  }

  @SuppressWarnings("static-method")
  @Test
  void testBlackNotReachableSingle() {
    // Single black bishop on c8, blocked by own knights on b7 and d7. Bcb6 not reachable.
    final Board board = new Board("2b1k3/1n1n4/8/pppppppp/8/8/8/4K3 b - - 0 1");
    checkException("Bcb6", board, SanValidationProblem.NOT_REACHABLE_RNBQ_FILE_SINGLE);
  }

  // --- Not reachable, multiple pieces on file ---

  @SuppressWarnings("static-method")
  @Test
  void testWhiteNotReachableMultiple() {
    // Two white rooks on a1 and a3, both blocked. Raa6 not reachable by either.
    // a4 pawn blocks a3 rook upward, a2 pawn blocks a1 rook upward.
    final Board board = new Board("7k/8/8/8/P7/R7/P7/R3K3 w - - 0 1");
    checkException("Raa6", board, SanValidationProblem.NOT_REACHABLE_RNBQ_FILE_MULTIPLE);
  }

  @SuppressWarnings("static-method")
  @Test
  void testBlackNotReachableMultiple() {
    // Two black rooks on a8 and a5, both blocked. Raa3 not reachable by either.
    // a7 pawn blocks a8 rook downward, a4 white pawn blocks a5 rook downward.
    final Board board = new Board("r3k3/p7/8/r7/P7/8/8/4K3 b - - 0 1");
    checkException("Raa3", board, SanValidationProblem.NOT_REACHABLE_RNBQ_FILE_MULTIPLE);
  }

  // --- King in check, single pseudo-legal move on file ---

  @SuppressWarnings("static-method")
  @Test
  void testWhiteKingInCheckSingle() {
    // White bishop on e4 pinned along e-file (king e1, rook e8). Bed5 would expose king.
    final Board board = new Board("4r2k/8/8/8/4B3/8/8/4K3 w - - 0 1");
    checkException("Bed5", board, SanValidationProblem.KING_EXPOSED_TO_CHECK_RNBQ_FILE_SINGLE);
  }

  @SuppressWarnings("static-method")
  @Test
  void testBlackKingInCheckSingle() {
    // Black bishop on e5 pinned along e-file (king e8, rook e1). Bed4 would expose king.
    final Board board = new Board("4k3/8/8/4b3/8/8/8/4R2K b - - 0 1");
    checkException("Bed4", board, SanValidationProblem.KING_EXPOSED_TO_CHECK_RNBQ_FILE_SINGLE);
  }

  // --- King in check, multiple pseudo-legal moves on file ---

  @SuppressWarnings("static-method")
  @Test
  void testWhiteKingInCheckMultiple() {
    // Two white bishops on d2 and d4, pinned on separate diagonals from c3.
    // d2 pinned along c3-d2-e1 (bishop e1), d4 pinned along c3-d4-e5 (bishop e5).
    // Both can reach e3 but each would expose king on its diagonal.
    // Black pawn on h7 breaks the otherwise mutual same-color-bishops-only material configuration
    // so the position is not in DEAD_POSITION_INSUFFICIENT_MATERIAL. h7 does not interact with the test.
    final Board board = new Board("k7/7p/8/4b3/3B4/2K5/3B4/4b3 w - - 0 1");
    checkException("Bde3", board, SanValidationProblem.KING_EXPOSED_TO_CHECK_RNBQ_FILE_MULTIPLE);
  }

  @SuppressWarnings("static-method")
  @Test
  void testBlackKingInCheckMultiple() {
    // Two black bishops on d7 and d5, pinned on separate diagonals from c6.
    // d7 pinned along c6-d7-e8 (bishop e8), d5 pinned along c6-d5-e4 (bishop e4).
    // Both can reach e6 but each would expose king on its diagonal.
    // White pawn on h2 breaks the otherwise mutual same-color-bishops-only material configuration
    // so the position is not in DEAD_POSITION_INSUFFICIENT_MATERIAL. h2 does not interact with the test.
    final Board board = new Board("4B3/3b4/2k5/3b4/4B3/8/7P/K7 b - - 0 1");
    checkException("Bde6", board, SanValidationProblem.KING_EXPOSED_TO_CHECK_RNBQ_FILE_MULTIPLE);
  }

  // --- King left in check ---

  @SuppressWarnings("static-method")
  @Test
  void testWhiteKingLeftInCheckSingle() {
    // White king e1 in check from black rook e8. White bishop on c4 can reach Bcd5 but doesn't resolve check.
    final Board board = new Board("4r2k/8/8/8/2B5/8/8/4K3 w - - 0 1");
    checkException("Bcd5", board, SanValidationProblem.KING_LEFT_IN_CHECK_RNBQ_FILE_SINGLE);
  }

  @SuppressWarnings("static-method")
  @Test
  void testBlackKingLeftInCheckSingle() {
    // Black king e8 in check from white rook e1. Black bishop on c5 can reach Bcd4 but doesn't resolve check.
    final Board board = new Board("4k3/8/8/2b5/8/8/8/4R2K b - - 0 1");
    checkException("Bcd4", board, SanValidationProblem.KING_LEFT_IN_CHECK_RNBQ_FILE_SINGLE);
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
