package com.dlb.chess.test.board;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.dlb.chess.board.Board;
import com.dlb.chess.bitboard.BitboardPositionUtility;

class TestStaticPosition {

  @SuppressWarnings("static-method")
  @Test
  void testStartPosition() {

    final Board board = new Board(false);

    final var expected = """
        rnbqkbnr
        pppppppp
        ........
        ........
        ........
        ........
        PPPPPPPP
        RNBQKBNR
        """;

    assertEquals(expected, BitboardPositionUtility.toStaticPosition(board.getBitboardPosition()).toString());
  }

  @SuppressWarnings("static-method")
  @Test
  void testFewMoves() {

    final Board board = new Board(false);

    board.movesStrict("e4", "e5", "Bc4", "Bc5", "Nf3", "Nc6");

    final var expected = """
        r.bqk.nr
        pppp.ppp
        ..n.....
        ..b.p...
        ..B.P...
        .....N..
        PPPP.PPP
        RNBQK..R
        """;

    assertEquals(expected, BitboardPositionUtility.toStaticPosition(board.getBitboardPosition()).toString());
  }

  @SuppressWarnings("static-method")
  @Test
  void testPosition() {

    final Board board = new Board("8/p4p1k/1pp2p1p/3p3P/1K1P1rP1/2P1n1R1/2P1r3/6R1 w - - 0 39", false);

    final var expected = """
        ........
        p....p.k
        .pp..p.p
        ...p...P
        .K.P.rP.
        ..P.n.R.
        ..P.r...
        ......R.
        """;

    assertEquals(expected, BitboardPositionUtility.toStaticPosition(board.getBitboardPosition()).toString());
  }
}
