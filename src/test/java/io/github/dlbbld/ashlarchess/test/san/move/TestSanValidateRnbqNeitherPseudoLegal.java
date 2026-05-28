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

class TestSanValidateRnbqNeitherPseudoLegal {

  // --- Not reachable, single piece ---

  @SuppressWarnings("static-method")
  @Test
  void testWhiteNotReachableSingle() {
    // Single white bishop on c1, blocked by own knights on b2 and d2. Cannot reach e3.
    final Board board = new Board("k7/8/8/8/PPPPPPPP/8/1N1N4/2B1K3 w - - 0 1");
    checkException("Be3", board, SanValidationProblem.NOT_REACHABLE_RNBQ_NEITHER_SINGLE);
  }

  @SuppressWarnings("static-method")
  @Test
  void testBlackNotReachableSingle() {
    // Single black bishop on c8, blocked by own knights on b7 and d7. Cannot reach e6.
    final Board board = new Board("2b1k3/1n1n4/8/pppppppp/8/8/8/4K3 b - - 0 1");
    checkException("Be6", board, SanValidationProblem.NOT_REACHABLE_RNBQ_NEITHER_SINGLE);
  }

  // --- Not reachable, multiple pieces ---

  @SuppressWarnings("static-method")
  @Test
  void testWhiteNotReachableMultiple() {
    // Two white knights on b1 and g1, blocked by pawns on 4th rank. Neither can reach e5.
    final Board board = new Board("k7/8/8/8/PPPPPPPP/8/8/1N2K1N1 w - - 0 1");
    checkException("Ne5", board, SanValidationProblem.NOT_REACHABLE_RNBQ_NEITHER_MULTIPLE);
  }

  @SuppressWarnings("static-method")
  @Test
  void testBlackNotReachableMultiple() {
    // Two black knights on b8 and g8, blocked by pawns on 5th rank. Neither can reach e4.
    final Board board = new Board("1n2k1n1/8/8/pppppppp/8/8/8/4K3 b - - 0 1");
    checkException("Ne4", board, SanValidationProblem.NOT_REACHABLE_RNBQ_NEITHER_MULTIPLE);
  }

  // --- King in check, single pseudo-legal move ---

  @SuppressWarnings("static-method")
  @Test
  void testWhiteKingInCheckSingle() {
    // White bishop on e4 pinned along e-file (king e1, rook e8). Bishop can reach d5 but would expose king.
    final Board board = new Board("4r2k/8/8/8/4B3/8/8/4K3 w - - 0 1");
    checkException("Bd5", board, SanValidationProblem.KING_EXPOSED_TO_CHECK_RNBQ_NEITHER_SINGLE);
  }

  @SuppressWarnings("static-method")
  @Test
  void testBlackKingInCheckSingle() {
    // Black bishop on e5 pinned along e-file (king e8, rook e1). Bishop can reach d4 but would expose king.
    final Board board = new Board("4k3/8/8/4b3/8/8/8/4R2K b - - 0 1");
    checkException("Bd4", board, SanValidationProblem.KING_EXPOSED_TO_CHECK_RNBQ_NEITHER_SINGLE);
  }

  // --- King in check, multiple pseudo-legal moves ---

  @SuppressWarnings("static-method")
  @Test
  void testWhiteKingInCheckMultiple() {
    // White knights on d2 and f2, pinned on separate diagonals from e1 (bishops b4 and g3).
    // Both can reach e4 but each would expose king on its diagonal.
    final Board board = new Board("k7/8/8/8/1b6/6b1/3N1N2/4K3 w - - 0 1");
    checkException("Ne4", board, SanValidationProblem.KING_EXPOSED_TO_CHECK_RNBQ_NEITHER_MULTIPLE);
  }

  @SuppressWarnings("static-method")
  @Test
  void testBlackKingInCheckMultiple() {
    // Black knights on d7 and f7, pinned on separate diagonals from e8 (bishops b5 and g6).
    // Both can reach e5 but each would expose king on its diagonal.
    final Board board = new Board("4k3/3n1n2/6B1/1B6/8/8/8/K7 b - - 0 1");
    checkException("Ne5", board, SanValidationProblem.KING_EXPOSED_TO_CHECK_RNBQ_NEITHER_MULTIPLE);
  }

  // --- King left in check, single pseudo-legal move ---

  @SuppressWarnings("static-method")
  @Test
  void testWhiteKingLeftInCheckSingle() {
    // White king e1 in check from black rook e8. Single white bishop on c4 can reach d5
    // but doesn't resolve check.
    final Board board = new Board("4r2k/8/8/8/2B5/8/8/4K3 w - - 0 1");
    checkException("Bd5", board, SanValidationProblem.KING_LEFT_IN_CHECK_RNBQ_NEITHER_SINGLE);
  }

  @SuppressWarnings("static-method")
  @Test
  void testBlackKingLeftInCheckSingle() {
    // Black king e8 in check from white rook e1. Single black bishop on c5 can reach d4
    // but doesn't resolve check.
    final Board board = new Board("4k3/8/8/2b5/8/8/8/4R2K b - - 0 1");
    checkException("Bd4", board, SanValidationProblem.KING_LEFT_IN_CHECK_RNBQ_NEITHER_SINGLE);
  }

  // --- King left in check, multiple pseudo-legal moves ---

  @SuppressWarnings("static-method")
  @Test
  void testWhiteKingLeftInCheckMultiple() {
    // White king a1 in check from black rook a8. Two white rooks on d3 and d6, both can reach d5
    // but neither resolves check on a-file.
    final Board board = new Board("r6k/8/3R4/8/8/3R4/8/K7 w - - 0 1");
    checkException("Rd5", board, SanValidationProblem.KING_LEFT_IN_CHECK_RNBQ_NEITHER_MULTIPLE);
  }

  @SuppressWarnings("static-method")
  @Test
  void testBlackKingLeftInCheckMultiple() {
    // Black king a8 in check from white rook a1. Two black rooks on d3 and d6, both can reach d5
    // but neither resolves check on a-file.
    final Board board = new Board("k7/8/3r4/8/8/3r4/8/R6K b - - 0 1");
    checkException("Rd5", board, SanValidationProblem.KING_LEFT_IN_CHECK_RNBQ_NEITHER_MULTIPLE);
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
