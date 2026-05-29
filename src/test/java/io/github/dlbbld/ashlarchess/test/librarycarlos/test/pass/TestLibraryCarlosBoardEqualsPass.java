// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.test.librarycarlos.test.pass;

import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;

import com.github.bhlangonijr.chesslib.Board;
import com.github.bhlangonijr.chesslib.Side;
import com.github.bhlangonijr.chesslib.move.Move;

class TestLibraryCarlosBoardEqualsPass {

  @SuppressWarnings("static-method")
  @Test
  void test() throws Exception {
    final Board board1 = new Board();
    board1.doMove(new Move("b1c3", Side.WHITE));
    board1.doMove(new Move("b8c6", Side.BLACK));
    board1.doMove(new Move("c3b1", Side.WHITE));
    board1.doMove(new Move("c6b8", Side.BLACK));
    board1.doMove(new Move("b1c3", Side.WHITE));
    board1.doMove(new Move("b8c6", Side.BLACK));
    board1.doMove(new Move("c3b1", Side.WHITE));

    final Board board2 = new Board();
    board2.loadFromFen(board1.getFen());

    // expected
    assertFalse(board1.strictEquals(board2));
  }

}
