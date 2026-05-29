// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.test.librarycarlos.test.notpass;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.github.bhlangonijr.chesslib.Board;

class TestLibraryCarlosFenValidationNotPass {

  @SuppressWarnings("static-method")
  @Test
  void testExpectedFailureButSuccess() throws Exception {

    // invalid side field - accepted - not ok
    assertTrue(testIsValid("r1bqk1nr/pppp1ppp/2n5/2b1p3/2B1P3/5N2/PPPP1PPP/RNBQK2R x KQkq - 0 4"));

    // castling field contains invalid letter - accepted - not ok
    assertTrue(testIsValid("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w xQkq - 0 1"));

    // position field contains non complete rank
    assertTrue(testIsValid("rnbqkbnr/pppppppp/7/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1"));

    // en passant field contains non possible value for position
    assertTrue(testIsValid("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq e3 0 1"));

    // position field contains non possible value (opponent king in check)
    assertTrue(testIsValid("4k3/8/8/8/8/8/4R3/4K3 w - - 0 1"));

    // castling field contains non possible value for position
    assertTrue(testIsValid("4k3/8/8/8/8/8/8/4KR2 w K - 0 1"));

    // halfmove counter contains non possible value (too big in regard to fullMoveNumber)
    assertTrue(testIsValid("4k3/8/8/8/8/8/8/4KR2 w K - 500 50"));

  }

  private static boolean testIsValid(String fen) {
    final Board board = new Board();
    try {
      board.loadFromFen(fen);
      return true;
    } catch (@SuppressWarnings("unused") final Exception e) {
      return false;
    }
  }
}
