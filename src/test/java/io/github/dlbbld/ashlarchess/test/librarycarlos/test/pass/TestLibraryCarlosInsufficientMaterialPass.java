// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.test.librarycarlos.test.pass;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.github.bhlangonijr.chesslib.Board;

class TestLibraryCarlosInsufficientMaterialPass {

  @SuppressWarnings("static-method")
  @Test
  void testExceptedTrueAndTrue() throws Exception {

    // KBvKB with bishop on same color field is a draw
    final String bishopOnSameColorSquares = "8/8/8/4k3/5b2/3K4/8/2B5 w - - 0 1";
    assertTrue(isInsufficientMaterial(bishopOnSameColorSquares));

    // KBBvK with bishop on same color field is a draw
    final String whiteTwoBishopsOnSameColorSquares = "B3k3/8/8/8/8/8/8/4KB2 w - - 0 1";
    assertTrue(isInsufficientMaterial(whiteTwoBishopsOnSameColorSquares));

    // KBBvKBBB with bishop on same color field is a draw
    assertTrue(isInsufficientMaterial("B1b1k3/3b4/4b3/8/8/8/8/4KB2 w - - 0 1"));
  }

  @SuppressWarnings("static-method")
  @Test
  void testExceptedFalseAndFalse() throws Exception {

    // KBvKB with bishop on different color field is not a draw
    final String bishopOnDifferentColorSquares = "8/8/8/4k3/5b2/3K4/2B5/8 w - - 0 1";
    assertFalse(isInsufficientMaterial(bishopOnDifferentColorSquares));

  }

  private static boolean isInsufficientMaterial(String fen) {
    final Board board = new Board();
    board.loadFromFen(fen);
    return board.isInsufficientMaterial();
  }
}
