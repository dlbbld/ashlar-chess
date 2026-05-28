// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.test.board;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import io.github.dlbbld.ashlarchess.bitboard.StaticPositionBridge;
import io.github.dlbbld.ashlarchess.board.Board;

class TestStaticPosition {

  @SuppressWarnings("static-method")
  @Test
  void testStartPosition() {

    final Board board = new Board();

    final String expected = """
        rnbqkbnr
        pppppppp
        ........
        ........
        ........
        ........
        PPPPPPPP
        RNBQKBNR
        """;

    assertEquals(expected, StaticPositionBridge.toStaticPosition(board.getBitboardPosition()).toString());
  }

  @SuppressWarnings("static-method")
  @Test
  void testFewMoves() {

    final Board board = new Board();

    board.movesStrict("e4", "e5", "Bc4", "Bc5", "Nf3", "Nc6");

    final String expected = """
        r.bqk.nr
        pppp.ppp
        ..n.....
        ..b.p...
        ..B.P...
        .....N..
        PPPP.PPP
        RNBQK..R
        """;

    assertEquals(expected, StaticPositionBridge.toStaticPosition(board.getBitboardPosition()).toString());
  }

  @SuppressWarnings("static-method")
  @Test
  void testPosition() {

    final Board board = new Board("8/p4p1k/1pp2p1p/3p3P/1K1P1rP1/2P1n1R1/2P1r3/6R1 w - - 0 39");

    final String expected = """
        ........
        p....p.k
        .pp..p.p
        ...p...P
        .K.P.rP.
        ..P.n.R.
        ..P.r...
        ......R.
        """;

    assertEquals(expected, StaticPositionBridge.toStaticPosition(board.getBitboardPosition()).toString());
  }
}
