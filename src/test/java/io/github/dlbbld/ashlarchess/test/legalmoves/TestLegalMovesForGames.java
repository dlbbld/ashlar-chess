package io.github.dlbbld.ashlarchess.test.legalmoves;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;

import org.junit.jupiter.api.Test;

import io.github.dlbbld.ashlarchess.board.Board;
import io.github.dlbbld.ashlarchess.common.utility.BasicUtility;
import io.github.dlbbld.ashlarchess.model.PgnHalfMove;
import io.github.dlbbld.ashlarchess.pgn.PgnGame;
import io.github.dlbbld.ashlarchess.test.pgn.parser.PgnCacheForStrictPgnParserTestCases;
import io.github.dlbbld.ashlarchess.test.pgntest.enums.PgnTest;

class TestLegalMovesForGames {

  private static final String INITIAL_LEGAL_MOVES = "Na3, Nc3, Nf3, Nh3, a3, a4, b3, b4, c3, c4, d3, d4, e3, e4, f3, f4, g3, g4, h3, h4";

  @SuppressWarnings("static-method")
  @Test
  void testGame1() {
    // 2_0_1_spassky_fischer_1972_seventeenth
    final Board board = new Board();
    checkInitial(board);

    checkLegalMoves(board, "e4", "Na6, Nc6, Nf6, Nh6, a5, a6, b5, b6, c5, c6, d5, d6, e5, e6, f5, f6, g5, g6, h5, h6");
    checkLegalMoves(board, "d6",
        "Ba6, Bb5+, Bc4, Bd3, Be2, Ke2, Na3, Nc3, Ne2, Nf3, Nh3, Qe2, Qf3, Qg4, Qh5, a3, a4, b3, b4, c3, c4, d3, d4, e5, f3, f4, g3, g4, h3, h4");
    checkLegalMoves(board, "d4",
        "Bd7, Be6, Bf5, Bg4, Bh3, Kd7, Na6, Nc6, Nd7, Nf6, Nh6, Qd7, a5, a6, b5, b6, c5, c6, d5, e5, e6, f5, f6, g5, g6, h5, h6");
    checkLegalMoves(board, "g6",
        "Ba6, Bb5+, Bc4, Bd2, Bd3, Be2, Be3, Bf4, Bg5, Bh6, Kd2, Ke2, Na3, Nc3, Nd2, Ne2, Nf3, Nh3, Qd2, Qd3, Qe2, Qf3, Qg4, Qh5, a3, a4, b3, b4, c3, c4, d5, e5, f3, f4, g3, g4, h3, h4");
    checkLegalMoves(board, "Nc3",
        "Bd7, Be6, Bf5, Bg4, Bg7, Bh3, Bh6, Kd7, Na6, Nc6, Nd7, Nf6, Nh6, Qd7, a5, a6, b5, b6, c5, c6, d5, e5, e6, f5, f6, g5, h5, h6");
    checkLegalMoves(board, "Nf6",
        "Ba6, Bb5+, Bc4, Bd2, Bd3, Be2, Be3, Bf4, Bg5, Bh6, Kd2, Ke2, Na4, Nb1, Nb5, Nce2, Nd5, Nf3, Nge2, Nh3, Qd2, Qd3, Qe2, Qf3, Qg4, Qh5, Rb1, a3, a4, b3, b4, d5, e5, f3, f4, g3, g4, h3, h4");
    checkLegalMoves(board, "f4",
        "Bd7, Be6, Bf5, Bg4, Bg7, Bh3, Bh6, Kd7, Na6, Nbd7, Nc6, Nd5, Nfd7, Ng4, Ng8, Nh5, Nxe4, Qd7, Rg8, a5, a6, b5, b6, c5, c6, d5, e5, e6, g5, h5, h6");
    checkLegalMoves(board, "Bg7",
        "Ba6, Bb5+, Bc4, Bd2, Bd3, Be2, Be3, Kd2, Ke2, Kf2, Na4, Nb1, Nb5, Nce2, Nd5, Nf3, Nge2, Nh3, Qd2, Qd3, Qe2, Qf3, Qg4, Qh5, Rb1, a3, a4, b3, b4, d5, e5, f5, g3, g4, h3, h4");
    checkLegalMoves(board, "Nf3",
        "Bd7, Be6, Bf5, Bf8, Bg4, Bh3, Bh6, Kd7, Kf8, Na6, Nbd7, Nc6, Nd5, Nfd7, Ng4, Ng8, Nh5, Nxe4, O-O, Qd7, Rf8, Rg8, a5, a6, b5, b6, c5, c6, d5, e5, e6, g5, h5, h6");
    checkLegalMoves(board, "c5",
        "Ba6, Bb5+, Bc4, Bd2, Bd3, Be2, Be3, Kd2, Ke2, Kf2, Na4, Nb1, Nb5, Nd2, Nd5, Ne2, Ne5, Ng1, Ng5, Nh4, Qd2, Qd3, Qe2, Rb1, Rg1, a3, a4, b3, b4, d5, dxc5, e5, f5, g3, g4, h3, h4");
    checkLegalMoves(board, "dxc5",
        "Bd7, Be6, Bf5, Bf8, Bg4, Bh3, Bh6, Kd7, Kf8, Na6, Nbd7, Nc6, Nd5, Nfd7, Ng4, Ng8, Nh5, Nxe4, O-O, Qa5, Qb6, Qc7, Qd7, Rf8, Rg8, a5, a6, b5, b6, d5, dxc5, e5, e6, g5, h5, h6");
    checkLegalMoves(board, "Qa5",
        "Ba6, Bb5+, Bc4, Bd2, Bd3, Be2, Be3, Kd2, Ke2, Kf2, Nd2, Nd4, Ne5, Ng1, Ng5, Nh4, Qd2, Qd3, Qd4, Qd5, Qe2, Qxd6, Rb1, Rg1, a3, a4, b3, b4, c6, cxd6, e5, f5, g3, g4, h3, h4");
    checkLegalMoves(board, "Bd3",
        "Bd7, Be6, Bf5, Bf8, Bg4, Bh3, Bh6, Kd7, Kd8, Kf8, Na6, Nbd7, Nc6, Nd5, Nfd7, Ng4, Ng8, Nh5, Nxe4, O-O, Qa3, Qa4, Qa6, Qb4, Qb5, Qb6, Qc7, Qd8, Qxa2, Qxc3+, Qxc5, Rf8, Rg8, a6, b5, b6, d5, dxc5, e5, e6, g5, h5, h6");
    checkLegalMoves(board, "Qxc5",
        "Ba6, Bb5+, Bc4, Bd2, Be2, Be3, Bf1, Kd2, Ke2, Kf1, Na4, Nb1, Nb5, Nd2, Nd4, Nd5, Ne2, Ne5, Ng1, Ng5, Nh4, Qd2, Qe2, Rb1, Rf1, Rg1, a3, a4, b3, b4, e5, f5, g3, g4, h3, h4");
    checkLegalMoves(board, "Qe2",
        "Bd7, Be6, Bf5, Bf8, Bg4, Bh3, Bh6, Kd7, Kd8, Kf8, Na6, Nbd7, Nc6, Nd5, Nfd7, Ng4, Ng8, Nh5, Nxe4, O-O, Qa3, Qa5, Qb4, Qb5, Qb6, Qc4, Qc6, Qc7, Qd4, Qd5, Qe3, Qe5, Qf2+, Qf5, Qg1+, Qg5, Qh5, Qxc3+, Rf8, Rg8, a5, a6, b5, b6, d5, e5, e6, g5, h5, h6");
    checkLegalMoves(board, "O-O",
        "Ba6, Bb5, Bc4, Bd2, Be3, Kd1, Kd2, Kf1, Na4, Nb1, Nb5, Nd1, Nd2, Nd4, Nd5, Ne5, Ng1, Ng5, Nh4, Qd1, Qd2, Qe3, Qf1, Qf2, Rb1, Rf1, Rg1, a3, a4, b3, b4, e5, f5, g3, g4, h3, h4");
    checkLegalMoves(board, "Be3",
        "Bd7, Be6, Bf5, Bg4, Bh3, Bh6, Bh8, Kh8, Na6, Nbd7, Nc6, Nd5, Ne8, Nfd7, Ng4, Nh5, Nxe4, Qa3, Qa5, Qb4, Qb5, Qb6, Qc4, Qc6, Qc7, Qd4, Qd5, Qe5, Qf5, Qg5, Qh5, Qxc3+, Qxe3, Rd8, Re8, a5, a6, b5, b6, d5, e5, e6, g5, h5, h6");
    checkLegalMoves(board, "Qa5",
        "Ba6, Bb5, Bb6, Bc1, Bc4, Bc5, Bd2, Bd4, Bf2, Bg1, Bxa7, Kd1, Kd2, Kf1, Kf2, Nd2, Nd4, Ne5, Ng1, Ng5, Nh4, O-O, O-O-O, Qd1, Qd2, Qf1, Qf2, Rb1, Rc1, Rd1, Rf1, Rg1, a3, a4, b3, b4, e5, f5, g3, g4, h3, h4");
    checkLegalMoves(board, "O-O",
        "Bd7, Be6, Bf5, Bg4, Bh3, Bh6, Bh8, Kh8, Na6, Nbd7, Nc6, Nd5, Ne8, Nfd7, Ng4, Nh5, Nxe4, Qa3, Qa4, Qa6, Qb4, Qb5, Qb6, Qc5, Qc7, Qd5, Qd8, Qe5, Qf5, Qg5, Qh5, Qxa2, Qxc3, Rd8, Re8, a6, b5, b6, d5, e5, e6, g5, h5, h6");
    checkLegalMoves(board, "Bg4",
        "Ba6, Bb5, Bb6, Bc1, Bc4, Bc5, Bd2, Bd4, Bf2, Bxa7, Kf2, Kh1, Na4, Nb1, Nb5, Nd1, Nd2, Nd4, Nd5, Ne1, Ne5, Ng5, Nh4, Qd1, Qd2, Qe1, Qf2, Rab1, Rac1, Rad1, Rae1, Rf2, Rfb1, Rfc1, Rfd1, Rfe1, a3, a4, b3, b4, e5, f5, g3, h3, h4");
    checkLegalMoves(board, "Rad1",
        "Bc8, Bd7, Be6, Bf5, Bh3, Bh5, Bh6, Bh8, Bxf3, Kh8, Na6, Nbd7, Nc6, Nd5, Ne8, Nfd7, Nh5, Nxe4, Qa3, Qa4, Qa6, Qb4, Qb5, Qb6, Qc5, Qc7, Qd5, Qd8, Qe5, Qf5, Qg5, Qh5, Qxa2, Qxc3, Rc8, Rd8, Re8, a6, b5, b6, d5, e5, e6, g5, h5, h6");
    checkLegalMoves(board, "Nc6",
        "Ba6, Bb5, Bb6, Bc1, Bc4, Bc5, Bd2, Bd4, Bf2, Bxa7, Kf2, Kh1, Na4, Nb1, Nb5, Nd2, Nd4, Nd5, Ne1, Ne5, Ng5, Nh4, Qd2, Qe1, Qf2, Ra1, Rb1, Rc1, Rd2, Rde1, Rf2, Rfe1, a3, a4, b3, b4, e5, f5, g3, h3, h4");
    checkLegalMoves(board, "Bc4",
        "Bc8, Bd7, Be6, Bf5, Bh3, Bh5, Bh6, Bh8, Bxf3, Kh8, Nb4, Nb8, Nd4, Nd5, Nd7, Nd8, Ne5, Ne8, Nh5, Nxe4, Qa3, Qa4, Qa6, Qb4, Qb5, Qb6, Qc5, Qc7, Qd5, Qd8, Qe5, Qf5, Qg5, Qh5, Qxa2, Qxc3, Rab8, Rac8, Rad8, Rae8, Rfb8, Rfc8, Rfd8, Rfe8, a6, b5, b6, d5, e5, e6, g5, h5, h6");
    checkLegalMoves(board, "Nh5",
        "Ba6, Bb3, Bb5, Bb6, Bc1, Bc5, Bd2, Bd3, Bd4, Bd5, Be6, Bf2, Bxa7, Bxf7+, Kf2, Kh1, Na4, Nb1, Nb5, Nd2, Nd4, Nd5, Ne1, Ne5, Ng5, Nh4, Qd2, Qd3, Qe1, Qf2, Ra1, Rb1, Rc1, Rd2, Rd3, Rd4, Rd5, Rde1, Rf2, Rfe1, Rxd6, a3, a4, b3, b4, e5, f5, g3, h3, h4");
    checkLegalMoves(board, "Bb3",
        "Bc8, Bd4, Bd7, Be5, Be6, Bf5, Bf6, Bh3, Bh6, Bh8, Bxc3, Bxf3, Kh8, Nb4, Nb8, Nd4, Nd8, Ne5, Nf6, Ng3, Nxf4, Qa3, Qa4, Qa6, Qb4, Qb5, Qb6, Qc5, Qc7, Qd5, Qd8, Qe5, Qf5, Qg5, Qxa2, Qxc3, Rab8, Rac8, Rad8, Rae8, Rfb8, Rfc8, Rfd8, Rfe8, a6, b5, b6, d5, e5, e6, g5, h6");
    checkLegalMoves(board, "Bxc3",
        "Ba4, Bb6, Bc1, Bc4, Bc5, Bd2, Bd4, Bd5, Be6, Bf2, Bxa7, Bxf7+, Kf2, Kh1, Nd2, Nd4, Ne1, Ne5, Ng5, Nh4, Qa6, Qb5, Qc4, Qd2, Qd3, Qe1, Qf2, Ra1, Rb1, Rc1, Rd2, Rd3, Rd4, Rd5, Rde1, Rf2, Rfe1, Rxd6, a3, a4, bxc3, e5, f5, g3, h3, h4");
    checkLegalMoves(board, "bxc3",
        "Bc8, Bd7, Be6, Bf5, Bh3, Bxf3, Kg7, Kh8, Nb4, Nb8, Nd4, Nd8, Ne5, Nf6, Ng3, Ng7, Nxf4, Qa3, Qa4, Qa6, Qb4, Qb5, Qb6, Qc5, Qc7, Qd5, Qd8, Qe5, Qf5, Qg5, Qxa2, Qxc3, Rab8, Rac8, Rad8, Rae8, Rfb8, Rfc8, Rfd8, Rfe8, a6, b5, b6, d5, e5, e6, g5, h6");
    checkLegalMoves(board, "Qxc3",
        "Ba4, Bb6, Bc1, Bc4, Bc5, Bd2, Bd4, Bd5, Be6, Bf2, Bxa7, Bxf7+, Kf2, Kh1, Nd2, Nd4, Ne1, Ne5, Ng5, Nh4, Qa6, Qb5, Qc4, Qd2, Qd3, Qe1, Qf2, Ra1, Rb1, Rc1, Rd2, Rd3, Rd4, Rd5, Rde1, Rf2, Rfe1, Rxd6, a3, a4, e5, f5, g3, h3, h4");
    checkLegalMoves(board, "f5",
        "Bh3, Bxf3, Bxf5, Kg7, Kh8, Na5, Nb4, Nb8, Nd4, Nd8, Ne5, Nf4, Nf6, Ng3, Ng7, Qa1, Qa5, Qb2, Qb4, Qc4, Qc5, Qd2, Qd3, Qd4, Qe1, Qe5, Qf6, Qg7, Qh8, Qxb3, Qxc2, Qxe3+, Rab8, Rac8, Rad8, Rae8, Rfb8, Rfc8, Rfd8, Rfe8, a5, a6, b5, b6, d5, e5, e6, g5, gxf5, h6");
    checkLegalMoves(board, "Nf6",
        "Ba4, Bb6, Bc1, Bc4, Bc5, Bd2, Bd4, Bd5, Be6, Bf2, Bf4, Bg5, Bh6, Bxa7, Bxf7+, Kf2, Kh1, Nd2, Nd4, Ne1, Ne5, Ng5, Nh4, Qa6, Qb5, Qc4, Qd2, Qd3, Qe1, Qf2, Ra1, Rb1, Rc1, Rd2, Rd3, Rd4, Rd5, Rde1, Rf2, Rfe1, Rxd6, a3, a4, e5, fxg6, g3, h3, h4");
    checkLegalMoves(board, "h3",
        "Bh5, Bxf3, Bxf5, Bxh3, Kg7, Kh8, Na5, Nb4, Nb8, Nd4, Nd5, Nd7, Nd8, Ne5, Ne8, Nh5, Nxe4, Qa1, Qa5, Qb2, Qb4, Qc4, Qc5, Qd2, Qd3, Qd4, Qe1, Qe5, Qxb3, Qxc2, Qxe3+, Rab8, Rac8, Rad8, Rae8, Rfb8, Rfc8, Rfd8, Rfe8, a5, a6, b5, b6, d5, e5, e6, g5, gxf5, h5, h6");
    checkLegalMoves(board, "Bxf3",
        "Ba4, Bb6, Bc1, Bc4, Bc5, Bd2, Bd4, Bd5, Be6, Bf2, Bf4, Bg5, Bh6, Bxa7, Bxf7+, Kf2, Kh1, Kh2, Qa6, Qb5, Qc4, Qd2, Qd3, Qe1, Qf2, Qxf3, Ra1, Rb1, Rc1, Rd2, Rd3, Rd4, Rd5, Rde1, Rf2, Rfe1, Rxd6, Rxf3, a3, a4, e5, fxg6, g3, g4, gxf3, h4");
    checkLegalMoves(board, "Qxf3",
        "Kg7, Kh8, Na5, Nb4, Nb8, Nd4, Nd5, Nd7, Nd8, Ne5, Ne8, Ng4, Nh5, Nxe4, Qa1, Qa5, Qb2, Qb4, Qc4, Qc5, Qd2, Qd3, Qd4, Qe1, Qe5, Qxb3, Qxc2, Qxe3+, Rab8, Rac8, Rad8, Rae8, Rfb8, Rfc8, Rfd8, Rfe8, a5, a6, b5, b6, d5, e5, e6, g5, gxf5, h5, h6");
    checkLegalMoves(board, "Na5",
        "Ba4, Bb6, Bc1, Bc4, Bc5, Bd2, Bd4, Bd5, Be6, Bf2, Bf4, Bg5, Bh6, Bxa7, Bxf7+, Kf2, Kh1, Kh2, Qe2, Qf2, Qf4, Qg3, Qg4, Qh5, Ra1, Rb1, Rc1, Rd2, Rd3, Rd4, Rd5, Rde1, Rf2, Rfe1, Rxd6, a3, a4, e5, fxg6, g3, g4, h4");
    checkLegalMoves(board, "Rd3",
        "Kg7, Kh8, Nc4, Nc6, Nd5, Nd7, Ne8, Ng4, Nh5, Nxb3, Nxe4, Qa1, Qb2, Qb4, Qc4, Qc5, Qc6, Qc7, Qc8, Qd2, Qd4, Qe1, Qe5, Qxb3, Qxc2, Qxd3, Rab8, Rac8, Rad8, Rae8, Rfb8, Rfc8, Rfd8, Rfe8, a6, b5, b6, d5, e5, e6, g5, gxf5, h5, h6");
    checkLegalMoves(board, "Qc7",
        "Ba4, Bb6, Bc1, Bc4, Bc5, Bd2, Bd4, Bd5, Be6, Bf2, Bf4, Bg5, Bh6, Bxa7, Bxf7+, Kf2, Kh1, Kh2, Qd1, Qe2, Qf2, Qf4, Qg3, Qg4, Qh5, Ra1, Rb1, Rc1, Rc3, Rd2, Rd4, Rd5, Rdd1, Re1, Rf2, Rfd1, Rxd6, a3, a4, c3, c4, e5, fxg6, g3, g4, h4");
    checkLegalMoves(board, "Bh6",
        "Kh8, Nc4, Nc6, Nd5, Nd7, Ne8, Ng4, Nh5, Nxb3, Nxe4, Qb6+, Qb8, Qc3, Qc4, Qc5+, Qc6, Qc8, Qd7, Qd8, Qxc2, Rab8, Rac8, Rad8, Rae8, Rfb8, Rfc8, Rfd8, Rfe8, a6, b5, b6, d5, e5, e6, g5, gxf5");
    checkLegalMoves(board, "Nxb3",
        "Bc1, Bd2, Be3, Bf4, Bg5, Bg7, Bxf8, Kf2, Kh1, Kh2, Qd1, Qe2, Qe3, Qf2, Qf4, Qg3, Qg4, Qh5, Ra1, Rb1, Rc1, Rc3, Rd2, Rd4, Rd5, Rdd1, Re1, Re3, Rf2, Rfd1, Rxb3, Rxd6, a3, a4, axb3, c3, c4, cxb3, e5, fxg6, g3, g4, h4");
    checkLegalMoves(board, "cxb3",
        "Kh8, Nd5, Nd7, Ne8, Ng4, Nh5, Nxe4, Qa5, Qb6+, Qb8, Qc1, Qc2, Qc3, Qc4, Qc5+, Qc6, Qc8, Qd7, Qd8, Rab8, Rac8, Rad8, Rae8, Rfb8, Rfc8, Rfd8, Rfe8, a5, a6, b5, b6, d5, e5, e6, g5, gxf5");
    checkLegalMoves(board, "Qc5+", "Be3, Kh1, Kh2, Qe3, Qf2, Rd4, Re3, Rf2");
    checkLegalMoves(board, "Kh1",
        "Kh8, Nd5, Nd7, Ne8, Ng4, Nh5, Nxe4, Qa3, Qa5, Qb4, Qb5, Qb6, Qc1, Qc2, Qc3, Qc4, Qc6, Qc7, Qc8, Qd4, Qd5, Qe3, Qe5, Qf2, Qg1+, Qxf5, Rab8, Rac8, Rad8, Rae8, Rfb8, Rfc8, Rfd8, Rfe8, a5, a6, b5, b6, d5, e5, e6, g5, gxf5");
    checkLegalMoves(board, "Qe5",
        "Bc1, Bd2, Be3, Bf4, Bg5, Bg7, Bxf8, Kg1, Qd1, Qe2, Qe3, Qf2, Qf4, Qg3, Qg4, Qh5, Ra1, Rb1, Rc1, Rc3, Rd2, Rd4, Rd5, Rdd1, Re1, Re3, Rf2, Rfd1, Rg1, Rxd6, a3, a4, b4, fxg6, g3, g4, h4");
    checkLegalMoves(board, "Bxf8",
        "Kh8, Kxf8, Nd5, Nd7, Ne8, Ng4, Nh5, Nxe4, Qa1, Qa5, Qb2, Qb5, Qc3, Qc5, Qd4, Qd5, Qe6, Qf4, Qg3, Qh2+, Qxe4, Qxf5, Rb8, Rc8, Rd8, Re8, Rxf8, a5, a6, b5, b6, d5, e6, g5, gxf5, h5, h6");
    checkLegalMoves(board, "Rxf8",
        "Kg1, Qd1, Qe2, Qe3, Qf2, Qf4, Qg3, Qg4, Qh5, Ra1, Rb1, Rc1, Rc3, Rd2, Rd4, Rd5, Rdd1, Re1, Re3, Rf2, Rfd1, Rg1, Rxd6, a3, a4, b4, fxg6, g3, g4, h4");
    checkLegalMoves(board, "Re3",
        "Kg7, Kh8, Nd5, Nd7, Ne8, Ng4, Nh5, Nxe4, Qa1, Qa5, Qb2, Qb5, Qc3, Qc5, Qd4, Qd5, Qe6, Qf4, Qg3, Qh2+, Qxe4, Qxf5, Ra8, Rb8, Rc8, Rd8, Re8, a5, a6, b5, b6, d5, e6, g5, gxf5, h5, h6");
    checkLegalMoves(board, "Rc8",
        "Kg1, Qd1, Qe2, Qf2, Qf4, Qg3, Qg4, Qh5, Ra1, Rb1, Rc1, Rc3, Rd1, Rd3, Re2, Ree1, Rf2, Rfe1, Rg1, a3, a4, b4, fxg6, g3, g4, h4");
    checkLegalMoves(board, "fxg6",
        "Kf8, Kg7, Kh8, Nd5, Nd7, Ne8, Ng4, Nh5, Nxe4, Qa1, Qa5, Qb2, Qb5, Qc3, Qc5, Qd4, Qd5, Qe6, Qf4, Qf5, Qg3, Qg5, Qh2+, Qh5, Qxe4, Ra8, Rb8, Rc1, Rc2, Rc3, Rc4, Rc5, Rc6, Rc7, Rd8, Re8, Rf8, a5, a6, b5, b6, d5, e6, fxg6, h5, h6, hxg6");
    checkLegalMoves(board, "hxg6",
        "Kg1, Qd1, Qe2, Qf2, Qf4, Qf5, Qg3, Qg4, Qh5, Qxf6, Ra1, Rb1, Rc1, Rc3, Rd1, Rd3, Re2, Ree1, Rf2, Rfe1, Rg1, a3, a4, b4, g3, g4, h4");
    checkLegalMoves(board, "Qf4",
        "Kf8, Kg7, Kh7, Kh8, Nd5, Nd7, Ne8, Ng4, Nh5, Nh7, Nxe4, Qa1, Qa5, Qb2, Qb5, Qc3, Qc5, Qd4, Qd5, Qe6, Qf5, Qg5, Qh5, Qxe4, Qxf4, Ra8, Rb8, Rc1, Rc2, Rc3, Rc4, Rc5, Rc6, Rc7, Rd8, Re8, Rf8, a5, a6, b5, b6, d5, e6, g5");
    checkLegalMoves(board, "Qxf4",
        "Kg1, Ra1, Rb1, Rc1, Rc3, Rd1, Rd3, Re2, Ree1, Ref3, Rf2, Rfe1, Rff3, Rg1, Rg3, Rxf4, a3, a4, b4, e5, g3, g4, h4");
    checkLegalMoves(board, "Rxf4",
        "Kf8, Kg7, Kh7, Kh8, Nd5, Nd7, Ne8, Ng4, Nh5, Nh7, Nxe4, Ra8, Rb8, Rc1+, Rc2, Rc3, Rc4, Rc5, Rc6, Rc7, Rd8, Re8, Rf8, a5, a6, b5, b6, d5, e5, e6, g5");
    checkLegalMoves(board, "Nd7",
        "Kg1, Kh2, Rc3, Rd3, Re1, Re2, Ref3, Rf1, Rf2, Rf5, Rf6, Rff3, Rg3, Rg4, Rh4, Rxf7, a3, a4, b4, e5, g3, g4, h4");
    checkLegalMoves(board, "Rf2",
        "Kf8, Kg7, Kh7, Kh8, Nb6, Nb8, Nc5, Ne5, Nf6, Nf8, Ra8, Rb8, Rc1+, Rc2, Rc3, Rc4, Rc5, Rc6, Rc7, Rd8, Re8, Rf8, a5, a6, b5, b6, d5, e5, e6, f5, f6, g5");
    checkLegalMoves(board, "Ne5",
        "Kg1, Kh2, Rb2, Rc2, Rc3, Rd2, Rd3, Re1, Ree2, Ref3, Rf1, Rf4, Rf5, Rf6, Rfe2, Rff3, Rg3, Rxf7, a3, a4, b4, g3, g4, h4");
    checkLegalMoves(board, "Kh2",
        "Kf8, Kg7, Kh7, Kh8, Nc4, Nc6, Nd3, Nd7, Nf3+, Ng4+, Ra8, Rb8, Rc1, Rc2, Rc3, Rc4, Rc5, Rc6, Rc7, Rd8, Re8, Rf8, a5, a6, b5, b6, d5, e6, f5, f6, g5");
    checkLegalMoves(board, "Rc1",
        "Kg3, Rb2, Rc2, Rc3, Rd2, Rd3, Re1, Ree2, Ref3, Rf1, Rf4, Rf5, Rf6, Rfe2, Rff3, Rg3, Rxf7, a3, a4, b4, g3, g4, h4");
    checkLegalMoves(board, "Ree2",
        "Kf8, Kg7, Kh7, Kh8, Nc4, Nc6, Nd3, Nd7, Nf3+, Ng4+, Ra1, Rb1, Rc2, Rc3, Rc4, Rc5, Rc6, Rc7, Rc8, Rd1, Re1, Rf1, Rg1, Rh1+, a5, a6, b5, b6, d5, e6, f5, f6, g5");
    checkLegalMoves(board, "Nc6",
        "Kg3, Rb2, Rc2, Rd2, Re1, Re3, Rf1, Rf3, Rf4, Rf5, Rf6, Rxf7, a3, a4, b4, e5, g3, g4, h4");
    checkLegalMoves(board, "Rc2",
        "Kf8, Kg7, Kh7, Kh8, Na5, Nb4, Nb8, Nd4, Nd8, Ne5, Ra1, Rb1, Rd1, Re1, Rf1, Rg1, Rh1+, Rxc2, a5, a6, b5, b6, d5, e5, e6, f5, f6, g5");
    checkLegalMoves(board, "Re1",
        "Kg3, Rb2, Rc1, Rc3, Rc4, Rc5, Rcd2, Rce2, Rf1, Rf3, Rf4, Rf5, Rf6, Rfd2, Rfe2, Rxc6, Rxf7, a3, a4, b4, e5, g3, g4, h4");
    checkLegalMoves(board, "Rfe2",
        "Kf8, Kg7, Kh7, Kh8, Na5, Nb4, Nb8, Nd4, Nd8, Ne5, Ra1, Rb1, Rc1, Rd1, Rf1, Rg1, Rh1+, Rxe2, a5, a6, b5, b6, d5, e5, e6, f5, f6, g5");
    checkLegalMoves(board, "Ra1",
        "Kg3, Rb2, Rc1, Rc3, Rc4, Rc5, Rcd2, Re1, Re3, Red2, Rf2, Rxc6, a3, a4, b4, e5, g3, g4, h4");
    checkLegalMoves(board, "Kg3",
        "Kf8, Kg7, Kh7, Kh8, Na5, Nb4, Nb8, Nd4, Nd8, Ne5, Rb1, Rc1, Rd1, Re1, Rf1, Rg1, Rh1, Rxa2, a5, a6, b5, b6, d5, e5, e6, f5, f6, g5");
    checkLegalMoves(board, "Kg7",
        "Kf2, Kf3, Kf4, Kg4, Kh2, Kh4, Rb2, Rc1, Rc3, Rc4, Rc5, Rcd2, Re1, Re3, Red2, Rf2, Rxc6, a3, a4, b4, e5, h4");
    checkLegalMoves(board, "Rcd2",
        "Kf6, Kf8, Kg8, Kh6, Kh7, Kh8, Na5, Nb4, Nb8, Nd4, Nd8, Ne5, Rb1, Rc1, Rd1, Re1, Rf1, Rg1, Rh1, Rxa2, a5, a6, b5, b6, d5, e5, e6, f5, f6, g5");
    checkLegalMoves(board, "Rf1",
        "Kg4, Kh2, Kh4, Rb2, Rc2, Rd1, Rd3, Rd4, Rd5, Re1, Re3, Rf2, Rxd6, a3, a4, b4, e5, h4");
    checkLegalMoves(board, "Rf2",
        "Kf8, Kg8, Kh6, Kh7, Kh8, Na5, Nb4, Nb8, Nd4, Nd8, Ne5, Ra1, Rb1, Rc1, Rd1, Re1, Rg1, Rh1, Rxf2, a5, a6, b5, b6, d5, e5, e6, f5, f6, g5");
    checkLegalMoves(board, "Re1",
        "Kf3, Kf4, Kg4, Kh2, Kh4, Rb2, Rc2, Rd1, Rd3, Rd4, Rd5, Rde2, Rf1, Rf3, Rf4, Rf5, Rf6, Rfe2, Rxd6, Rxf7+, a3, a4, b4, e5, h4");
    checkLegalMoves(board, "Rfe2",
        "Kf6, Kf8, Kg8, Kh6, Kh7, Kh8, Na5, Nb4, Nb8, Nd4, Nd8, Ne5, Ra1, Rb1, Rc1, Rd1, Rf1, Rg1, Rh1, Rxe2, a5, a6, b5, b6, d5, e5, e6, f5, f6, g5");
    checkLegalMoves(board, "Rf1",
        "Kg4, Kh2, Kh4, Rb2, Rc2, Rd1, Rd3, Rd4, Rd5, Re1, Re3, Rf2, Rxd6, a3, a4, b4, e5, h4");
    checkLegalMoves(board, "Re3",
        "Kf6, Kf8, Kg8, Kh6, Kh7, Kh8, Na5, Nb4, Nb8, Nd4, Nd8, Ne5, Ra1, Rb1, Rc1, Rd1, Re1, Rf2, Rf3+, Rf4, Rf5, Rf6, Rg1, Rh1, a5, a6, b5, b6, d5, e5, e6, f5, f6, g5");
    checkLegalMoves(board, "a6",
        "Kg4, Kh2, Kh4, Rb2, Rc2, Rc3, Rd1, Rd4, Rd5, Rdd3, Rde2, Re1, Red3, Ree2, Rf2, Rf3, Rxd6, a3, a4, b4, e5, h4");
    checkLegalMoves(board, "Rc3",
        "Kf6, Kf8, Kg8, Kh6, Kh7, Kh8, Na5, Na7, Nb4, Nb8, Nd4, Nd8, Ne5, Ra1, Rb1, Rc1, Rd1, Re1, Rf2, Rf3+, Rf4, Rf5, Rf6, Rg1, Rh1, a5, b5, b6, d5, e5, e6, f5, f6, g5");
    checkLegalMoves(board, "Re1",
        "Kf2, Kf3, Kf4, Kg4, Kh2, Kh4, Rb2, Rc1, Rc4, Rc5, Rcc2, Rcd3, Rd1, Rd4, Rd5, Rdc2, Rdd3, Re2, Re3, Rf2, Rf3, Rxc6, Rxd6, a3, a4, b4, e5, h4");
    checkLegalMoves(board, "Rc4",
        "Kf6, Kf8, Kg8, Kh6, Kh7, Kh8, Na5, Na7, Nb4, Nb8, Nd4, Nd8, Ne5, Ra1, Rb1, Rc1, Rd1, Re2, Re3+, Rf1, Rg1, Rh1, Rxe4, a5, b5, b6, d5, e5, e6, f5, f6, g5");
    checkLegalMoves(board, "Rf1",
        "Kg4, Kh2, Kh4, Ra4, Rb2, Rb4, Rc1, Rc3, Rc5, Rcc2, Rcd4, Rd1, Rd3, Rd5, Rdc2, Rdd4, Re2, Rf2, Rxc6, Rxd6, a3, a4, b4, e5, h4");
    checkLegalMoves(board, "Rdc2",
        "Kf6, Kf8, Kg8, Kh6, Kh7, Kh8, Na5, Na7, Nb4, Nb8, Nd4, Nd8, Ne5, Ra1, Rb1, Rc1, Rd1, Re1, Rf2, Rf3+, Rf4, Rf5, Rf6, Rg1, Rh1, a5, b5, b6, d5, e5, e6, f5, f6, g5");
    checkLegalMoves(board, "Ra1",
        "Kf2, Kf3, Kf4, Kg4, Kh2, Kh4, R2c3, R4c3, Ra4, Rb2, Rb4, Rc1, Rc5, Rd2, Rd4, Re2, Rf2, Rxc6, a3, a4, b4, e5, h4");
    checkLegalMoves(board, "Rf2",
        "Kf8, Kg8, Kh6, Kh7, Kh8, Na5, Na7, Nb4, Nb8, Nd4, Nd8, Ne5, Rb1, Rc1, Rd1, Re1, Rf1, Rg1, Rh1, Rxa2, a5, b5, b6, d5, e5, e6, f5, f6, g5");
    checkLegalMoves(board, "Re1",
        "Kf3, Kf4, Kg4, Kh2, Kh4, Ra4, Rb2, Rb4, Rc1, Rc3, Rc5, Rcc2, Rd2, Rd4, Re2, Rf1, Rf3, Rf4, Rf5, Rf6, Rfc2, Rxc6, Rxf7+, a3, a4, b4, e5, h4");
    checkLegalMoves(board, "Rfc2",
        "Kf6, Kf8, Kg8, Kh6, Kh7, Kh8, Na5, Na7, Nb4, Nb8, Nd4, Nd8, Ne5, Ra1, Rb1, Rc1, Rd1, Re2, Re3+, Rf1, Rg1, Rh1, Rxe4, a5, b5, b6, d5, e5, e6, f5, f6, g5");
    checkLegalMoves(board, "g5",
        "Kf2, Kf3, Kg4, Kh2, R2c3, R4c3, Ra4, Rb2, Rb4, Rc1, Rc5, Rd2, Rd4, Re2, Rf2, Rxc6, a3, a4, b4, e5, h4");
    checkLegalMoves(board, "Rc1",
        "Kf6, Kf8, Kg6, Kg8, Kh6, Kh7, Kh8, Na5, Na7, Nb4, Nb8, Nd4, Nd8, Ne5, Rd1, Re2, Re3+, Rf1, Rg1, Rh1, Rxc1, Rxe4, a5, b5, b6, d5, e5, e6, f5, f6, g4");
    checkLegalMoves(board, "Re2",
        "Kf3, Kg4, Kh2, R1c2, R1c3, R4c2, R4c3, Ra1, Ra4, Rb1, Rb4, Rc5, Rd1, Rd4, Re1, Rf1, Rg1, Rh1, Rxc6, a3, a4, b4, e5, h4");
    checkLegalMoves(board, "R1c2",
        "Kf6, Kf8, Kg6, Kg8, Kh6, Kh7, Kh8, Na5, Na7, Nb4, Nb8, Nd4, Nd8, Ne5, Rd2, Re1, Re3+, Rf2, Rxc2, Rxe4, Rxg2+, a5, b5, b6, d5, e5, e6, f5, f6, g4");
    checkLegalMoves(board, "Re1",
        "Kf2, Kf3, Kg4, Kh2, R2c3, R4c3, Ra4, Rb2, Rb4, Rc1, Rc5, Rd2, Rd4, Re2, Rf2, Rxc6, a3, a4, b4, e5, h4");
    checkLegalMoves(board, "Rc1",
        "Kf6, Kf8, Kg6, Kg8, Kh6, Kh7, Kh8, Na5, Na7, Nb4, Nb8, Nd4, Nd8, Ne5, Rd1, Re2, Re3+, Rf1, Rg1, Rh1, Rxc1, Rxe4, a5, b5, b6, d5, e5, e6, f5, f6, g4");
    checkLegalMoves(board, "Re2",
        "Kf3, Kg4, Kh2, R1c2, R1c3, R4c2, R4c3, Ra1, Ra4, Rb1, Rb4, Rc5, Rd1, Rd4, Re1, Rf1, Rg1, Rh1, Rxc6, a3, a4, b4, e5, h4");
    checkLegalMoves(board, "R1c2",
        "Kf6, Kf8, Kg6, Kg8, Kh6, Kh7, Kh8, Na5, Na7, Nb4, Nb8, Nd4, Nd8, Ne5, Rd2, Re1, Re3+, Rf2, Rxc2, Rxe4, Rxg2+, a5, b5, b6, d5, e5, e6, f5, f6, g4");
  }

  @SuppressWarnings("static-method")
  @Test
  void testGame2() {
    // 2_4_1_alekhine_lasker_1914
    final Board board = new Board();
    checkInitial(board);

    checkLegalMoves(board, "e4", "Na6, Nc6, Nf6, Nh6, a5, a6, b5, b6, c5, c6, d5, d6, e5, e6, f5, f6, g5, g6, h5, h6");
    checkLegalMoves(board, "e5",
        "Ba6, Bb5, Bc4, Bd3, Be2, Ke2, Na3, Nc3, Ne2, Nf3, Nh3, Qe2, Qf3, Qg4, Qh5, a3, a4, b3, b4, c3, c4, d3, d4, f3, f4, g3, g4, h3, h4");
    checkLegalMoves(board, "Nf3",
        "Ba3, Bb4, Bc5, Bd6, Be7, Ke7, Na6, Nc6, Ne7, Nf6, Nh6, Qe7, Qf6, Qg5, Qh4, a5, a6, b5, b6, c5, c6, d5, d6, f5, f6, g5, g6, h5, h6");
    checkLegalMoves(board, "Nc6",
        "Ba6, Bb5, Bc4, Bd3, Be2, Ke2, Na3, Nc3, Nd4, Ng1, Ng5, Nh4, Nxe5, Qe2, Rg1, a3, a4, b3, b4, c3, c4, d3, d4, g3, g4, h3, h4");
    checkLegalMoves(board, "d4",
        "Ba3, Bb4+, Bc5, Bd6, Be7, Ke7, Na5, Nb4, Nb8, Nce7, Nf6, Nge7, Nh6, Nxd4, Qe7, Qf6, Qg5, Qh4, Rb8, a5, a6, b5, b6, d5, d6, exd4, f5, f6, g5, g6, h5, h6");
    checkLegalMoves(board, "exd4",
        "Ba6, Bb5, Bc4, Bd2, Bd3, Be2, Be3, Bf4, Bg5, Bh6, Kd2, Ke2, Na3, Nbd2, Nc3, Ne5, Nfd2, Ng1, Ng5, Nh4, Nxd4, Qd2, Qd3, Qe2, Qxd4, Rg1, a3, a4, b3, b4, c3, c4, e5, g3, g4, h3, h4");
    checkLegalMoves(board, "Nxd4",
        "Ba3, Bb4+, Bc5, Bd6, Be7, Ke7, Na5, Nb4, Nb8, Nce7, Ne5, Nf6, Nge7, Nh6, Nxd4, Qe7, Qf6, Qg5, Qh4, Rb8, a5, a6, b5, b6, d5, d6, f5, f6, g5, g6, h5, h6");
    checkLegalMoves(board, "Nf6",
        "Ba6, Bb5, Bc4, Bd2, Bd3, Be2, Be3, Bf4, Bg5, Bh6, Kd2, Ke2, Na3, Nb3, Nb5, Nc3, Nd2, Ne2, Ne6, Nf3, Nf5, Nxc6, Qd2, Qd3, Qe2, Qf3, Qg4, Qh5, Rg1, a3, a4, b3, b4, c3, c4, e5, f3, f4, g3, g4, h3, h4");
    checkLegalMoves(board, "Nc3",
        "Ba3, Bb4, Bc5, Bd6, Be7, Ke7, Na5, Nb4, Nb8, Nd5, Ne5, Ne7, Ng4, Ng8, Nh5, Nxd4, Nxe4, Qe7, Rb8, Rg8, a5, a6, b5, b6, d5, d6, g5, g6, h5, h6");
    checkLegalMoves(board, "Bb4",
        "Ba6, Bb5, Bc4, Bd2, Bd3, Be2, Be3, Bf4, Bg5, Bh6, Kd2, Ke2, Nb3, Nb5, Ne2, Ne6, Nf3, Nf5, Nxc6, Qd2, Qd3, Qe2, Qf3, Qg4, Qh5, Rb1, Rg1, a3, a4, b3, e5, f3, f4, g3, g4, h3, h4");
    checkLegalMoves(board, "Nxc6",
        "Ba3, Ba5, Bc5, Bd6, Be7, Bf8, Bxc3+, Kf8, Nd5, Ng4, Ng8, Nh5, Nxe4, O-O, Qe7, Rb8, Rf8, Rg8, a5, a6, b5, b6, bxc6, d5, d6, dxc6, g5, g6, h5, h6");
    checkLegalMoves(board, "bxc6",
        "Ba6, Bb5, Bc4, Bd2, Bd3, Be2, Be3, Bf4, Bg5, Bh6, Kd2, Ke2, Qd2, Qd3, Qd4, Qd5, Qd6, Qe2, Qf3, Qg4, Qh5, Qxd7+, Rb1, Rg1, a3, a4, b3, e5, f3, f4, g3, g4, h3, h4");
    checkLegalMoves(board, "Bd3",
        "Ba3, Ba5, Ba6, Bb7, Bc5, Bd6, Be7, Bf8, Bxc3+, Ke7, Kf8, Nd5, Ng4, Ng8, Nh5, Nxe4, O-O, Qe7, Rb8, Rf8, Rg8, a5, a6, c5, d5, d6, g5, g6, h5, h6");
    checkLegalMoves(board, "O-O",
        "Ba6, Bb5, Bc4, Bd2, Be2, Be3, Bf1, Bf4, Bg5, Bh6, Kd2, Ke2, Kf1, O-O, Qd2, Qe2, Qf3, Qg4, Qh5, Rb1, Rf1, Rg1, a3, a4, b3, e5, f3, f4, g3, g4, h3, h4");
    checkLegalMoves(board, "O-O",
        "Ba3, Ba5, Ba6, Bb7, Bc5, Bd6, Be7, Bxc3, Kh8, Nd5, Ne8, Ng4, Nh5, Nxe4, Qe7, Qe8, Rb8, Re8, a5, a6, c5, d5, d6, g5, g6, h5, h6");
    checkLegalMoves(board, "d5",
        "Ba6, Bb5, Bc4, Bd2, Be2, Be3, Bf4, Bg5, Bh6, Kh1, Na4, Nb1, Nb5, Ne2, Nxd5, Qd2, Qe1, Qe2, Qf3, Qg4, Qh5, Rb1, Re1, a3, a4, b3, e5, exd5, f3, f4, g3, g4, h3, h4");
    checkLegalMoves(board, "exd5",
        "Ba3, Ba5, Ba6, Bb7, Bc5, Bd6, Bd7, Be6, Be7, Bf5, Bg4, Bh3, Bxc3, Kh8, Nd7, Ne4, Ne8, Ng4, Nh5, Nxd5, Qd6, Qd7, Qe7, Qe8, Qxd5, Rb8, Re8, a5, a6, c5, cxd5, g5, g6, h5, h6");
    checkLegalMoves(board, "cxd5",
        "Ba6, Bb5, Bc4, Bd2, Be2, Be3, Be4, Bf4, Bf5, Bg5, Bg6, Bh6, Bxh7+, Kh1, Na4, Nb1, Nb5, Ne2, Ne4, Nxd5, Qd2, Qe1, Qe2, Qf3, Qg4, Qh5, Rb1, Re1, a3, a4, b3, f3, f4, g3, g4, h3, h4");
    checkLegalMoves(board, "Bg5",
        "Ba3, Ba5, Ba6, Bb7, Bc5, Bd6, Bd7, Be6, Be7, Bf5, Bg4, Bh3, Bxc3, Kh8, Nd7, Ne4, Ne8, Ng4, Nh5, Qd6, Qd7, Qe7, Qe8, Rb8, Re8, a5, a6, c5, c6, d4, g6, h5, h6");
    checkLegalMoves(board, "Be6",
        "Ba6, Bb5, Bc1, Bc4, Bd2, Be2, Be3, Be4, Bf4, Bf5, Bg6, Bh4, Bh6, Bxf6, Bxh7+, Kh1, Na4, Nb1, Nb5, Ne2, Ne4, Nxd5, Qb1, Qc1, Qd2, Qe1, Qe2, Qf3, Qg4, Qh5, Rb1, Rc1, Re1, a3, a4, b3, f3, f4, g3, g4, h3, h4");
    checkLegalMoves(board, "Qf3",
        "Ba3, Ba5, Bc5, Bc8, Bd6, Bd7, Be7, Bf5, Bg4, Bh3, Bxc3, Kh8, Nd7, Ne4, Ne8, Ng4, Nh5, Qb8, Qc8, Qd6, Qd7, Qe7, Qe8, Rb8, Rc8, Re8, a5, a6, c5, c6, d4, g6, h5, h6");
    checkLegalMoves(board, "Be7",
        "Ba6, Bb5, Bc1, Bc4, Bd2, Be2, Be3, Be4, Bf4, Bf5, Bg6, Bh4, Bh6, Bxf6, Bxh7+, Kh1, Na4, Nb1, Nb5, Nd1, Ne2, Ne4, Nxd5, Qd1, Qe2, Qe3, Qe4, Qf4, Qf5, Qg3, Qg4, Qh3, Qh5, Qxd5, Qxf6, Rab1, Rac1, Rad1, Rae1, Rfb1, Rfc1, Rfd1, Rfe1, a3, a4, b3, b4, g3, g4, h3, h4");
    checkLegalMoves(board, "Rfe1",
        "Ba3, Bb4, Bc5, Bc8, Bd6, Bd7, Bf5, Bg4, Bh3, Kh8, Nd7, Ne4, Ne8, Ng4, Nh5, Qb8, Qc8, Qd6, Qd7, Qe8, Rb8, Rc8, Re8, a5, a6, c5, c6, d4, g6, h5, h6");
    checkLegalMoves(board, "h6",
        "Ba6, Bb5, Bc1, Bc4, Bd2, Be2, Be3, Be4, Bf1, Bf4, Bf5, Bg6, Bh4, Bh7+, Bxf6, Bxh6, Kf1, Kh1, Na4, Nb1, Nb5, Nd1, Ne2, Ne4, Nxd5, Qd1, Qe2, Qe3, Qe4, Qf4, Qf5, Qg3, Qg4, Qh3, Qh5, Qxd5, Qxf6, Rab1, Rac1, Rad1, Re2, Re3, Re4, Re5, Reb1, Rec1, Red1, Rf1, Rxe6, a3, a4, b3, b4, g3, g4, h3, h4");
    checkLegalMoves(board, "Bxh6",
        "Ba3, Bb4, Bc5, Bc8, Bd6, Bd7, Bf5, Bg4, Bh3, Kh8, Nd7, Ne4, Ne8, Ng4, Nh5, Nh7, Qb8, Qc8, Qd6, Qd7, Qe8, Rb8, Rc8, Re8, a5, a6, c5, c6, d4, g5, g6, gxh6");
    checkLegalMoves(board, "gxh6",
        "Ba6, Bb5, Bc4, Be2, Be4, Bf1, Bf5, Bg6, Bh7+, Kf1, Kh1, Na4, Nb1, Nb5, Nd1, Ne2, Ne4, Nxd5, Qd1, Qe2, Qe3, Qe4, Qf4, Qf5, Qg3+, Qg4+, Qh3, Qh5, Qxd5, Qxf6, Rab1, Rac1, Rad1, Re2, Re3, Re4, Re5, Reb1, Rec1, Red1, Rf1, Rxe6, a3, a4, b3, b4, g3, g4, h3, h4");
    checkLegalMoves(board, "Rxe6",
        "Ba3, Bb4, Bc5, Bd6, Kg7, Kh8, Nd7, Ne4, Ne8, Ng4, Nh5, Nh7, Qb8, Qc8, Qd6, Qd7, Qe8, Rb8, Rc8, Re8, a5, a6, c5, c6, d4, fxe6, h5");
    checkLegalMoves(board, "fxe6",
        "Ba6, Bb5, Bc4, Be2, Be4, Bf1, Bf5, Bg6, Bh7+, Kf1, Kh1, Na4, Nb1, Nb5, Nd1, Ne2, Ne4, Nxd5, Qd1, Qe2, Qe3, Qe4, Qf4, Qf5, Qg3+, Qg4+, Qh3, Qh5, Qxd5, Qxf6, Rb1, Rc1, Rd1, Re1, Rf1, a3, a4, b3, b4, g3, g4, h3, h4");
    checkLegalMoves(board, "Qg3+", "Kf7, Kh8, Ng4");
    checkLegalMoves(board, "Kh8",
        "Ba6, Bb5, Bc4, Be2, Be4, Bf1, Bf5, Bg6, Bh7, Kf1, Kh1, Na4, Nb1, Nb5, Nd1, Ne2, Ne4, Nxd5, Qd6, Qe3, Qe5, Qf3, Qf4, Qg4, Qg5, Qg6, Qg7+, Qg8+, Qh3, Qh4, Qxc7, Rb1, Rc1, Rd1, Re1, Rf1, a3, a4, b3, b4, f3, f4, h3, h4");
    checkLegalMoves(board, "Qg6",
        "Ba3, Bb4, Bc5, Bd6, Nd7, Ne4, Ne8, Ng4, Ng8, Nh5, Nh7, Qb8, Qc8, Qd6, Qd7, Qe8, Rb8, Rc8, Re8, Rf7, Rg8, a5, a6, c5, c6, d4, e5, h5");

  }

  @SuppressWarnings("static-method")
  @Test
  void testGame3() {
    // 1_filipowicz_smederevac_1966
    final Board board = new Board();
    checkInitial(board);

    checkLegalMoves(board, "e4", "Na6, Nc6, Nf6, Nh6, a5, a6, b5, b6, c5, c6, d5, d6, e5, e6, f5, f6, g5, g6, h5, h6");
    checkLegalMoves(board, "e6",
        "Ba6, Bb5, Bc4, Bd3, Be2, Ke2, Na3, Nc3, Ne2, Nf3, Nh3, Qe2, Qf3, Qg4, Qh5, a3, a4, b3, b4, c3, c4, d3, d4, e5, f3, f4, g3, g4, h3, h4");
    checkLegalMoves(board, "d3",
        "Ba3, Bb4+, Bc5, Bd6, Be7, Ke7, Na6, Nc6, Ne7, Nf6, Nh6, Qe7, Qf6, Qg5, Qh4, a5, a6, b5, b6, c5, c6, d5, d6, e5, f5, f6, g5, g6, h5, h6");
    checkLegalMoves(board, "Ne7",
        "Bd2, Be2, Be3, Bf4, Bg5, Bh6, Kd2, Ke2, Na3, Nc3, Nd2, Ne2, Nf3, Nh3, Qd2, Qe2, Qf3, Qg4, Qh5, a3, a4, b3, b4, c3, c4, d4, e5, f3, f4, g3, g4, h3, h4");
    checkLegalMoves(board, "g3",
        "Na6, Nbc6, Nd5, Nec6, Nf5, Ng6, Ng8, Rg8, a5, a6, b5, b6, c5, c6, d5, d6, e5, f5, f6, g5, g6, h5, h6");
    checkLegalMoves(board, "c5",
        "Bd2, Be2, Be3, Bf4, Bg2, Bg5, Bh3, Bh6, Kd2, Ke2, Na3, Nc3, Nd2, Ne2, Nf3, Nh3, Qd2, Qe2, Qf3, Qg4, Qh5, a3, a4, b3, b4, c3, c4, d4, e5, f3, f4, g4, h3, h4");
    checkLegalMoves(board, "Bg2",
        "Na6, Nbc6, Nd5, Nec6, Nf5, Ng6, Ng8, Qa5+, Qb6, Qc7, Rg8, a5, a6, b5, b6, c4, d5, d6, e5, f5, f6, g5, g6, h5, h6");
    checkLegalMoves(board, "Nbc6",
        "Bd2, Be3, Bf1, Bf3, Bf4, Bg5, Bh3, Bh6, Kd2, Ke2, Kf1, Na3, Nc3, Nd2, Ne2, Nf3, Nh3, Qd2, Qe2, Qf3, Qg4, Qh5, a3, a4, b3, b4, c3, c4, d4, e5, f3, f4, g4, h3, h4");
    checkLegalMoves(board, "Be3",
        "Na5, Nb4, Nb8, Nd4, Nd5, Ne5, Nf5, Ng6, Ng8, Qa5+, Qb6, Qc7, Rb8, Rg8, a5, a6, b5, b6, c4, d5, d6, e5, f5, f6, g5, g6, h5, h6");
    checkLegalMoves(board, "b6",
        "Bc1, Bd2, Bd4, Bf1, Bf3, Bf4, Bg5, Bh3, Bh6, Bxc5, Kd2, Ke2, Kf1, Na3, Nc3, Nd2, Ne2, Nf3, Nh3, Qc1, Qd2, Qe2, Qf3, Qg4, Qh5, a3, a4, b3, b4, c3, c4, d4, e5, f3, f4, g4, h3, h4");
    checkLegalMoves(board, "Ne2",
        "Ba6, Bb7, Na5, Nb4, Nb8, Nd4, Nd5, Ne5, Nf5, Ng6, Ng8, Qc7, Rb8, Rg8, a5, a6, b5, c4, d5, d6, e5, f5, f6, g5, g6, h5, h6");
    checkLegalMoves(board, "d5",
        "Bc1, Bd2, Bd4, Bf1, Bf3, Bf4, Bg5, Bh3, Bh6, Bxc5, Kd2, Kf1, Na3, Nbc3, Nc1, Nd2, Nd4, Nec3, Nf4, Ng1, O-O, Qc1, Qd2, Rf1, Rg1, a3, a4, b3, b4, c3, c4, d4, e5, exd5, f3, f4, g4, h3, h4");
    checkLegalMoves(board, "O-O",
        "Ba6, Bb7, Bd7, Kd7, Na5, Nb4, Nb8, Nd4, Ne5, Nf5, Ng6, Ng8, Qc7, Qd6, Qd7, Rb8, Rg8, a5, a6, b5, c4, d4, dxe4, e5, f5, f6, g5, g6, h5, h6");
    checkLegalMoves(board, "d4",
        "Bc1, Bd2, Bf3, Bf4, Bg5, Bh1, Bh3, Bh6, Bxd4, Kh1, Na3, Nbc3, Nc1, Nd2, Nec3, Nf4, Nxd4, Qc1, Qd2, Qe1, Re1, a3, a4, b3, b4, c3, c4, e5, f3, f4, g4, h3, h4");
    checkLegalMoves(board, "Bc1",
        "Ba6, Bb7, Bd7, Kd7, Na5, Nb4, Nb8, Nd5, Ne5, Nf5, Ng6, Ng8, Qc7, Qd5, Qd6, Qd7, Rb8, Rg8, a5, a6, b5, c4, e5, f5, f6, g5, g6, h5, h6");
    checkLegalMoves(board, "g6",
        "Bd2, Be3, Bf3, Bf4, Bg5, Bh1, Bh3, Bh6, Kh1, Na3, Nbc3, Nd2, Nec3, Nf4, Nxd4, Qd2, Qe1, Re1, a3, a4, b3, b4, c3, c4, e5, f3, f4, g4, h3, h4");
    checkLegalMoves(board, "Nd2",
        "Ba6, Bb7, Bd7, Bg7, Bh6, Kd7, Na5, Nb4, Nb8, Nd5, Ne5, Nf5, Ng8, Qc7, Qd5, Qd6, Qd7, Rb8, Rg8, a5, a6, b5, c4, e5, f5, f6, g5, h5, h6");
    checkLegalMoves(board, "Bg7",
        "Bf3, Bh1, Bh3, Kh1, Nb1, Nb3, Nc3, Nc4, Nf3, Nf4, Nxd4, Qe1, Rb1, Re1, a3, a4, b3, b4, c3, c4, e5, f3, f4, g4, h3, h4");
    checkLegalMoves(board, "f4",
        "Ba6, Bb7, Bd7, Be5, Bf6, Bf8, Bh6, Kd7, Kf8, Na5, Nb4, Nb8, Nd5, Ne5, Nf5, Ng8, O-O, Qc7, Qd5, Qd6, Qd7, Rb8, Rf8, Rg8, a5, a6, b5, c4, e5, f5, f6, g5, h5, h6");
    checkLegalMoves(board, "f5",
        "Bf3, Bh1, Bh3, Kf2, Kh1, Nb1, Nb3, Nc3, Nc4, Nf3, Nxd4, Qe1, Rb1, Re1, Rf2, Rf3, a3, a4, b3, b4, c3, c4, e5, exf5, g4, h3, h4");
    checkLegalMoves(board, "a3",
        "Ba6, Bb7, Bd7, Be5, Bf6, Bf8, Bh6, Kd7, Kf7, Kf8, Na5, Nb4, Nb8, Nd5, Ne5, Ng8, O-O, Qc7, Qd5, Qd6, Qd7, Rb8, Rf8, Rg8, a5, a6, b5, c4, e5, fxe4, g5, h5, h6");
    checkLegalMoves(board, "O-O",
        "Bf3, Bh1, Bh3, Kf2, Kh1, Nb1, Nb3, Nc3, Nc4, Nf3, Nxd4, Qe1, Ra2, Rb1, Re1, Rf2, Rf3, a4, b3, b4, c3, c4, e5, exf5, g4, h3, h4");
    checkLegalMoves(board, "e5",
        "Ba6, Bb7, Bd7, Bf6, Bh6, Bh8, Bxe5, Kf7, Kh8, Na5, Nb4, Nb8, Nd5, Nxe5, Qc7, Qd5, Qd6, Qd7, Qe8, Rb8, Re8, Rf6, Rf7, a5, a6, b5, c4, g5, h5, h6");
    checkLegalMoves(board, "a5",
        "Bd5, Be4, Bf3, Bh1, Bh3, Bxc6, Kf2, Kh1, Nb1, Nb3, Nc3, Nc4, Ne4, Nf3, Nxd4, Qe1, Ra2, Rb1, Re1, Rf2, Rf3, a4, b3, b4, c3, c4, g4, h3, h4");
    checkLegalMoves(board, "a4",
        "Ba6, Bb7, Bd7, Bf6, Bh6, Bh8, Bxe5, Kf7, Kh8, Na7, Nb4, Nb8, Nd5, Nxe5, Qc7, Qd5, Qd6, Qd7, Qe8, Ra6, Ra7, Rb8, Re8, Rf6, Rf7, b5, c4, g5, h5, h6");
    checkLegalMoves(board, "Ba6",
        "Bd5, Be4, Bf3, Bh1, Bh3, Bxc6, Kf2, Kh1, Nb1, Nb3, Nc3, Nc4, Ne4, Nf3, Nxd4, Qe1, Ra2, Ra3, Rb1, Re1, Rf2, Rf3, b3, b4, c3, c4, g4, h3, h4");
    checkLegalMoves(board, "b3",
        "Bb5, Bb7, Bc4, Bc8, Bf6, Bh6, Bh8, Bxd3, Bxe5, Kf7, Kh8, Na7, Nb4, Nb8, Nc8, Nd5, Nxe5, Qb8, Qc7, Qc8, Qd5, Qd6, Qd7, Qe8, Ra7, Rb8, Rc8, Re8, Rf6, Rf7, b5, c4, g5, h5, h6");
    checkLegalMoves(board, "Rb8",
        "Ba3, Bb2, Bd5, Be4, Bf3, Bh1, Bh3, Bxc6, Kf2, Kh1, Nb1, Nc3, Nc4, Ne4, Nf3, Nxd4, Qe1, Ra2, Ra3, Rb1, Re1, Rf2, Rf3, b4, c3, c4, g4, h3, h4");
    checkLegalMoves(board, "Nc4",
        "Bb5, Bb7, Bc8, Bf6, Bh6, Bh8, Bxc4, Bxe5, Kf7, Kh8, Na7, Nb4, Nc8, Nd5, Nxe5, Qc7, Qc8, Qd5, Qd6, Qd7, Qe8, Ra8, Rb7, Rc8, Re8, Rf6, Rf7, b5, g5, h5, h6");
    checkLegalMoves(board, "Qc7",
        "Ba3, Bb2, Bd2, Bd5, Be3, Be4, Bf3, Bh1, Bh3, Bxc6, Kf2, Kh1, Na3, Nb2, Nc3, Nd2, Nd6, Ne3, Nxa5, Nxb6, Nxd4, Qd2, Qe1, Ra2, Ra3, Rb1, Re1, Rf2, Rf3, b4, c3, g4, h3, h4");
    checkLegalMoves(board, "Kh1",
        "Bb5, Bb7, Bc8, Bf6, Bh6, Bh8, Bxc4, Bxe5, Kf7, Kh8, Na7, Nb4, Nc8, Nd5, Nd8, Nxe5, Qa7, Qb7, Qc8, Qd6, Qd7, Qd8, Qxe5, Ra8, Rb7, Rbc8, Rbd8, Rbe8, Rf6, Rf7, Rfc8, Rfd8, Rfe8, b5, g5, h5, h6");
    checkLegalMoves(board, "Nd5",
        "Ba3, Bb2, Bd2, Be3, Be4, Bf3, Bh3, Bxd5, Kg1, Na3, Nb2, Nc3, Nd2, Nd6, Ne3, Ng1, Nxa5, Nxb6, Nxd4, Qd2, Qe1, Ra2, Ra3, Rb1, Re1, Rf2, Rf3, Rg1, b4, c3, g4, h3, h4");
    checkLegalMoves(board, "Bd2",
        "Bb5, Bb7, Bc8, Bf6, Bh6, Bh8, Bxc4, Bxe5, Kf7, Kh8, Na7, Nc3, Ncb4, Nce7, Nd8, Ndb4, Nde7, Ne3, Nf6, Nxe5, Nxf4, Qa7, Qb7, Qc8, Qd6, Qd7, Qd8, Qe7, Qf7, Qxe5, Ra8, Rb7, Rbc8, Rbd8, Rbe8, Rf6, Rf7, Rfc8, Rfd8, Rfe8, b5, g5, h5, h6");
    checkLegalMoves(board, "Rfd8",
        "Bb4, Bc1, Bc3, Be1, Be3, Be4, Bf3, Bh3, Bxa5, Bxd5, Kg1, Na3, Nb2, Nc1, Nc3, Nd6, Ne3, Ng1, Nxa5, Nxb6, Nxd4, Qb1, Qc1, Qe1, Ra2, Ra3, Rb1, Rc1, Re1, Rf2, Rf3, Rg1, b4, c3, g4, h3, h4");
    checkLegalMoves(board, "Ng1",
        "Bb5, Bb7, Bc8, Bf6, Bf8, Bh6, Bh8, Bxc4, Bxe5, Kf7, Kf8, Kh8, Na7, Nc3, Ncb4, Nce7, Ndb4, Nde7, Ne3, Nf6, Nxe5, Nxf4, Qa7, Qb7, Qc8, Qd6, Qd7, Qe7, Qf7, Qxe5, Ra8, Rb7, Rbc8, Rd6, Rd7, Rdc8, Re8, Rf8, b5, g5, h5, h6");
    checkLegalMoves(board, "Bf8",
        "Bb4, Bc1, Bc3, Be1, Be3, Be4, Bf3, Bh3, Bxa5, Bxd5, Na3, Nb2, Nd6, Ne2, Ne3, Nf3, Nh3, Nxa5, Nxb6, Qb1, Qc1, Qe1, Qe2, Qf3, Qg4, Qh5, Ra2, Ra3, Rb1, Rc1, Re1, Rf2, Rf3, b4, c3, g4, h3, h4");
    checkLegalMoves(board, "Nf3",
        "Bb5, Bb7, Bc8, Bd6, Be7, Bg7, Bh6, Bxc4, Kf7, Kg7, Kh8, Na7, Nc3, Ncb4, Nce7, Ndb4, Nde7, Ne3, Nf6, Nxe5, Nxf4, Qa7, Qb7, Qc8, Qd6, Qd7, Qe7, Qf7, Qg7, Qxe5, Ra8, Rb7, Rbc8, Rd6, Rd7, Rdc8, Re8, b5, g5, h5, h6");
    checkLegalMoves(board, "Be7",
        "Bb4, Bc1, Bc3, Be1, Be3, Bh3, Bxa5, Kg1, Na3, Nb2, Nd6, Ne1, Ne3, Ng1, Ng5, Nh4, Nxa5, Nxb6, Nxd4, Qb1, Qc1, Qe1, Qe2, Ra2, Ra3, Rb1, Rc1, Re1, Rf2, Rg1, b4, c3, g4, h3, h4");
    checkLegalMoves(board, "h4",
        "Bb5, Bb7, Bc8, Bd6, Bf6, Bf8, Bg5, Bxc4, Bxh4, Kf7, Kf8, Kg7, Kh8, Na7, Nc3, Ncb4, Ndb4, Ne3, Nf6, Nxe5, Nxf4, Qa7, Qb7, Qc8, Qd6, Qd7, Qxe5, Ra8, Rb7, Rbc8, Rd6, Rd7, Rdc8, Re8, Rf8, b5, g5, h5, h6");
    checkLegalMoves(board, "h5",
        "Bb4, Bc1, Bc3, Be1, Be3, Bh3, Bxa5, Kg1, Kh2, Na3, Nb2, Nd6, Ne1, Ne3, Ng1, Ng5, Nh2, Nxa5, Nxb6, Nxd4, Qb1, Qc1, Qe1, Qe2, Ra2, Ra3, Rb1, Rc1, Re1, Rf2, Rg1, b4, c3, g4");
    checkLegalMoves(board, "Qe2",
        "Bb5, Bb7, Bc8, Bd6, Bf6, Bf8, Bg5, Bxc4, Bxh4, Kf7, Kf8, Kg7, Kh7, Kh8, Na7, Nc3, Ncb4, Ndb4, Ne3, Nf6, Nxe5, Nxf4, Qa7, Qb7, Qc8, Qd6, Qd7, Qxe5, Ra8, Rb7, Rbc8, Rd6, Rd7, Rdc8, Re8, Rf8, b5, g5");
    checkLegalMoves(board, "Ncb4",
        "Bc1, Bc3, Be1, Be3, Bh3, Bxb4, Kg1, Kh2, Na3, Nb2, Nd6, Ne1, Ne3, Ng1, Ng5, Nh2, Nxa5, Nxb6, Nxd4, Qd1, Qe1, Qe3, Qe4, Qf2, Ra2, Ra3, Rab1, Rac1, Rad1, Rae1, Rf2, Rfb1, Rfc1, Rfd1, Rfe1, Rg1, c3, g4");
    checkLegalMoves(board, "Rfc1",
        "Bb5, Bb7, Bc8, Bd6, Bf6, Bf8, Bg5, Bxc4, Bxh4, Kf7, Kf8, Kg7, Kh7, Kh8, Na2, Nc3, Nc6, Ne3, Nf6, Nxc2, Nxd3, Nxf4, Qa7, Qb7, Qc6, Qc8, Qd6, Qd7, Qxe5, Ra8, Rb7, Rbc8, Rd6, Rd7, Rdc8, Re8, Rf8, b5, g5");
    checkLegalMoves(board, "Bb7",
        "Bc3, Be1, Be3, Bf1, Bh3, Bxb4, Kg1, Kh2, Na3, Nb2, Nd6, Ne1, Ne3, Ng1, Ng5, Nh2, Nxa5, Nxb6, Nxd4, Qd1, Qe1, Qe3, Qe4, Qf1, Qf2, Ra2, Ra3, Rab1, Rcb1, Rd1, Re1, Rf1, Rg1, c3, g4");
    checkLegalMoves(board, "Kh2",
        "Ba6, Ba8, Bc6, Bc8, Bd6, Bf6, Bf8, Bg5, Bxh4, Kf7, Kf8, Kg7, Kh7, Kh8, Na2, Na6, Nc3, Nc6, Ne3, Nf6, Nxc2, Nxd3, Nxf4, Qc6, Qc8, Qd6, Qd7, Qxe5, Ra8, Rbc8, Rd6, Rd7, Rdc8, Re8, Rf8, b5, g5");
    checkLegalMoves(board, "Bc6",
        "Bc3, Be1, Be3, Bf1, Bh1, Bh3, Bxb4, Kg1, Kh1, Kh3, Na3, Nb2, Nd6, Ne1, Ne3, Ng1, Ng5, Nxa5, Nxb6, Nxd4, Qd1, Qe1, Qe3, Qe4, Qf1, Qf2, Ra2, Ra3, Rab1, Rcb1, Rd1, Re1, Rf1, Rg1, Rh1, c3, g4");
    checkLegalMoves(board, "Na3",
        "Ba8, Bb5, Bb7, Bd6, Bd7, Be8, Bf6, Bf8, Bg5, Bxa4, Bxh4, Kf7, Kf8, Kg7, Kh7, Kh8, Na2, Na6, Nc3, Ne3, Nf6, Nxc2, Nxd3, Nxf4, Qa7, Qb7, Qc8, Qd6, Qd7, Qxe5, Ra8, Rb7, Rbc8, Rd6, Rd7, Rdc8, Re8, Rf8, b5, c4, g5");
    checkLegalMoves(board, "Ra8",
        "Bc3, Be1, Be3, Bf1, Bh1, Bh3, Bxb4, Kg1, Kh1, Kh3, Nb1, Nb5, Nc4, Ne1, Ng1, Ng5, Nxd4, Qd1, Qe1, Qe3, Qe4, Qf1, Qf2, Ra2, Rab1, Rcb1, Rd1, Re1, Rf1, Rg1, Rh1, c3, c4, g4");
    checkLegalMoves(board, "Qe1",
        "Bb5, Bb7, Bd6, Bd7, Be8, Bf6, Bf8, Bg5, Bxa4, Bxh4, Kf7, Kf8, Kg7, Kh7, Kh8, Na2, Na6, Nc3, Ne3, Nf6, Nxc2, Nxd3, Nxf4, Qa7, Qb7, Qb8, Qc8, Qd6, Qd7, Qxe5, Ra6, Ra7, Rab8, Rac8, Rd6, Rd7, Rdb8, Rdc8, Re8, Rf8, b5, c4, g5");
    checkLegalMoves(board, "Rdb8",
        "Bc3, Be3, Bf1, Bh1, Bh3, Bxb4, Kg1, Kh1, Kh3, Nb1, Nb5, Nc4, Ng1, Ng5, Nxd4, Qd1, Qe2, Qe3, Qe4, Qf1, Qf2, Qg1, Qh1, Ra2, Rab1, Rcb1, Rd1, c3, c4, g4");
    checkLegalMoves(board, "Qg1",
        "Bb5, Bb7, Bd6, Bd7, Bd8, Be8, Bf6, Bf8, Bg5, Bxa4, Bxh4, Kf7, Kf8, Kg7, Kh7, Kh8, Na2, Na6, Nc3, Ne3, Nf6, Nxc2, Nxd3, Nxf4, Qa7, Qb7, Qc8, Qd6, Qd7, Qd8, Qxe5, Ra6, Ra7, Rb7, Rc8, Rd8, Re8, Rf8, b5, c4, g5");
    checkLegalMoves(board, "Qb7",
        "Bc3, Be1, Be3, Bf1, Bh1, Bh3, Bxb4, Kh1, Kh3, Nb1, Nb5, Nc4, Ne1, Ng5, Nxd4, Qd1, Qe1, Qe3, Qf1, Qf2, Qh1, Qxd4, Ra2, Rab1, Rcb1, Rd1, Re1, Rf1, c3, c4, g4");
    checkLegalMoves(board, "Qf1",
        "Bb5, Bd6, Bd7, Bd8, Be8, Bf6, Bf8, Bg5, Bxa4, Bxh4, Kf7, Kf8, Kg7, Kh7, Kh8, Na2, Na6, Nc3, Nc7, Ne3, Nf6, Nxc2, Nxd3, Nxf4, Qa6, Qa7, Qc7, Qc8, Qd7, Ra6, Ra7, Rc8, Rd8, Re8, Rf8, b5, c4, g5");
    checkLegalMoves(board, "Kg7",
        "Bc3, Be1, Be3, Bh1, Bh3, Bxb4, Kg1, Kh1, Kh3, Nb1, Nb5, Nc4, Ne1, Ng1, Ng5, Nxd4, Qd1, Qe1, Qe2, Qf2, Qg1, Qh1, Ra2, Rab1, Rcb1, Rd1, Re1, c3, c4, g4");
    checkLegalMoves(board, "Qh1",
        "Bb5, Bd6, Bd7, Bd8, Be8, Bf6, Bf8, Bg5, Bxa4, Bxh4, Kf7, Kf8, Kg8, Kh6, Kh7, Kh8, Na2, Na6, Nc3, Nc7, Ne3, Nf6, Nxc2, Nxd3, Nxf4, Qa6, Qa7, Qc7, Qc8, Qd7, Ra6, Ra7, Rc8, Rd8, Re8, Rf8, Rg8, Rh8, b5, c4, g5");
    checkLegalMoves(board, "Qd7",
        "Bc3, Be1, Be3, Bf1, Bh3, Bxb4, Kg1, Kh3, Nb1, Nb5, Nc4, Ne1, Ng1, Ng5, Nxd4, Qd1, Qe1, Qf1, Qg1, Ra2, Rab1, Rcb1, Rd1, Re1, Rf1, Rg1, c3, c4, g4");
    checkLegalMoves(board, "Ne1",
        "Bb5, Bb7, Bd6, Bd8, Bf6, Bf8, Bg5, Bxa4, Bxh4, Kf7, Kf8, Kg8, Kh6, Kh7, Kh8, Na2, Na6, Nc3, Nc7, Ne3, Nf6, Nxc2, Nxd3, Nxf4, Qa7, Qb7, Qc7, Qc8, Qd6, Qd8, Qe8, Ra6, Ra7, Rb7, Rc8, Rd8, Re8, Rf8, Rg8, Rh8, b5, c4, g5");
    checkLegalMoves(board, "Ra7",
        "Bc3, Be3, Be4, Bf1, Bf3, Bh3, Bxb4, Bxd5, Kg1, Kh3, Nb1, Nb5, Nc4, Nf3, Qf1, Qg1, Ra2, Rab1, Rcb1, Rd1, c3, c4, g4");
    checkLegalMoves(board, "Nf3",
        "Ba8, Bb5, Bb7, Bd6, Bd8, Bf6, Bf8, Bg5, Bxa4, Bxh4, Kf7, Kf8, Kg8, Kh6, Kh7, Kh8, Na2, Na6, Nc3, Nc7, Ne3, Nf6, Nxc2, Nxd3, Nxf4, Qb7, Qc7, Qc8, Qd6, Qd8, Qe8, Ra6, Raa8, Rab7, Rba8, Rbb7, Rc7, Rc8, Rd8, Re8, Rf8, Rg8, Rh8, b5, c4, g5");
    checkLegalMoves(board, "Rba8",
        "Bc3, Be1, Be3, Bf1, Bh3, Bxb4, Kg1, Kh3, Nb1, Nb5, Nc4, Ne1, Ng1, Ng5, Nxd4, Qd1, Qe1, Qf1, Qg1, Ra2, Rab1, Rcb1, Rd1, Re1, Rf1, Rg1, c3, c4, g4");
    checkLegalMoves(board, "Ne1",
        "Bb5, Bb7, Bd6, Bd8, Bf6, Bf8, Bg5, Bxa4, Bxh4, Kf7, Kf8, Kg8, Kh6, Kh7, Kh8, Na2, Na6, Nc3, Nc7, Ne3, Nf6, Nxc2, Nxd3, Nxf4, Qb7, Qc7, Qc8, Qd6, Qd8, Qe8, Ra6, Rb7, Rb8, Rc7, Rc8, Rd8, Re8, Rf8, Rg8, Rh8, b5, c4, g5");
    checkLegalMoves(board, "Bd8",
        "Bc3, Be3, Be4, Bf1, Bf3, Bh3, Bxb4, Bxd5, Kg1, Kh3, Nb1, Nb5, Nc4, Nf3, Qf1, Qg1, Ra2, Rab1, Rcb1, Rd1, c3, c4, g4");
    checkLegalMoves(board, "Nf3",
        "Bb5, Bb7, Bc7, Be7, Bf6, Bg5, Bxa4, Bxh4, Kf7, Kf8, Kg8, Kh6, Kh7, Kh8, Na2, Na6, Nc3, Nc7, Ne3, Ne7, Nf6, Nxc2, Nxd3, Nxf4, Qb7, Qc7, Qc8, Qd6, Qe7, Qe8, Qf7, Ra6, Rb7, Rb8, Rc7, Rc8, b5, c4, g5");
    checkLegalMoves(board, "Rb8",
        "Bc3, Be1, Be3, Bf1, Bh3, Bxb4, Kg1, Kh3, Nb1, Nb5, Nc4, Ne1, Ng1, Ng5, Nxd4, Qd1, Qe1, Qf1, Qg1, Ra2, Rab1, Rcb1, Rd1, Re1, Rf1, Rg1, c3, c4, g4");
    checkLegalMoves(board, "Ne1",
        "Ba8, Bb5, Bb7, Bc7, Be7, Bf6, Bg5, Bxa4, Bxh4, Kf7, Kf8, Kg8, Kh6, Kh7, Kh8, Na2, Na6, Nc3, Nc7, Ne3, Ne7, Nf6, Nxc2, Nxd3, Nxf4, Qb7, Qc7, Qc8, Qd6, Qe7, Qe8, Qf7, Ra6, Raa8, Rab7, Rba8, Rbb7, Rc7, Rc8, b5, c4, g5");
    checkLegalMoves(board, "Bc7",
        "Bc3, Be3, Be4, Bf1, Bf3, Bh3, Bxb4, Bxd5, Kg1, Kh3, Nb1, Nb5, Nc4, Nf3, Qf1, Qg1, Ra2, Rab1, Rcb1, Rd1, c3, c4, g4");
    checkLegalMoves(board, "Nf3",
        "Ba8, Bb5, Bb7, Bd6, Bd8, Bxa4, Bxe5, Kf7, Kf8, Kg8, Kh6, Kh7, Kh8, Na2, Na6, Nc3, Ne3, Ne7, Nf6, Nxc2, Nxd3, Nxf4, Qc8, Qd6, Qd8, Qe7, Qe8, Qf7, Ra6, Raa8, Rab7, Rba8, Rbb7, Rc8, Rd8, Re8, Rf8, Rg8, Rh8, b5, c4, g5");
    checkLegalMoves(board, "Rh8",
        "Bc3, Be1, Be3, Bf1, Bh3, Bxb4, Kg1, Kh3, Nb1, Nb5, Nc4, Ne1, Ng1, Ng5, Nxd4, Qd1, Qe1, Qf1, Qg1, Ra2, Rab1, Rcb1, Rd1, Re1, Rf1, Rg1, c3, c4, g4");
    checkLegalMoves(board, "Ng5",
        "Ba8, Bb5, Bb7, Bb8, Bd6, Bd8, Bxa4, Bxe5, Kf8, Kg8, Kh6, Na2, Na6, Nc3, Ne3, Ne7, Nf6, Nxc2, Nxd3, Nxf4, Qc8, Qd6, Qd8, Qe7, Qe8, Qf7, Ra6, Raa8, Rb7, Rb8, Rc8, Rd8, Re8, Rf8, Rg8, Rh6, Rh7, Rha8, b5, c4");
    checkLegalMoves(board, "Bd8",
        "Bc3, Be1, Be3, Be4, Bf1, Bf3, Bh3, Bxb4, Bxd5, Kg1, Kh3, Nb1, Nb5, Nc4, Ne4, Nf3, Nf7, Nh3, Nh7, Nxe6+, Qd1, Qe1, Qf1, Qg1, Ra2, Rab1, Rcb1, Rd1, Re1, Rf1, Rg1, c3, c4, g4");
    checkLegalMoves(board, "Nf3",
        "Ba8, Bb5, Bb7, Bc7, Be7, Bf6, Bg5, Bxa4, Bxh4, Kf7, Kf8, Kg8, Kh6, Kh7, Na2, Na6, Nc3, Nc7, Ne3, Ne7, Nf6, Nxc2, Nxd3, Nxf4, Qb7, Qc7, Qc8, Qd6, Qe7, Qe8, Qf7, Ra6, Ra8, Rb7, Rc7, Re8, Rf8, Rg8, Rh6, Rh7, b5, c4, g5");
    checkLegalMoves(board, "Be7",
        "Bc3, Be1, Be3, Bf1, Bh3, Bxb4, Kg1, Kh3, Nb1, Nb5, Nc4, Ne1, Ng1, Ng5, Nxd4, Qd1, Qe1, Qf1, Qg1, Ra2, Rab1, Rcb1, Rd1, Re1, Rf1, Rg1, c3, c4, g4");
    checkLegalMoves(board, "Qg1",
        "Ba8, Bb5, Bb7, Bd6, Bd8, Bf6, Bf8, Bg5, Bxa4, Bxh4, Kf7, Kf8, Kg8, Kh6, Kh7, Na2, Na6, Nc3, Nc7, Ne3, Nf6, Nxc2, Nxd3, Nxf4, Qb7, Qc7, Qc8, Qd6, Qd8, Qe8, Ra6, Raa8, Rb7, Rb8, Rc7, Rc8, Rd8, Re8, Rf8, Rg8, Rh6, Rh7, Rha8, b5, c4, g5");
    checkLegalMoves(board, "Bb7",
        "Bc3, Be1, Be3, Bf1, Bh1, Bh3, Bxb4, Kh1, Kh3, Nb1, Nb5, Nc4, Ne1, Ng5, Nxd4, Qd1, Qe1, Qe3, Qf1, Qf2, Qh1, Qxd4, Ra2, Rab1, Rcb1, Rd1, Re1, Rf1, c3, c4, g4");
    checkLegalMoves(board, "Nb5",
        "Ba6, Ba8, Bc6, Bc8, Bd6, Bd8, Bf6, Bf8, Bg5, Bxh4, Kf7, Kf8, Kg8, Kh6, Kh7, Na2, Na6, Nc3, Nc6, Nc7, Ne3, Nf6, Nxc2, Nxd3, Nxf4, Qc6, Qc7, Qc8, Qd6, Qd8, Qe8, Qxb5, Ra6, Raa8, Rb8, Rc8, Rd8, Re8, Rf8, Rg8, Rh6, Rh7, Rha8, c4, g5");
    checkLegalMoves(board, "Raa8",
        "Bc3, Be1, Be3, Bf1, Bh1, Bh3, Bxb4, Kh1, Kh3, Na3, Na7, Nbxd4, Nc3, Nc7, Nd6, Ne1, Nfxd4, Ng5, Qd1, Qe1, Qe3, Qf1, Qf2, Qh1, Qxd4, Ra2, Ra3, Rab1, Rcb1, Rd1, Re1, Rf1, c3, c4, g4");
    checkLegalMoves(board, "Na3",
        "Ba6, Bc6, Bc8, Bd6, Bd8, Bf6, Bf8, Bg5, Bxh4, Kf7, Kf8, Kg8, Kh6, Kh7, Na2, Na6, Nc3, Nc6, Nc7, Ne3, Nf6, Nxc2, Nxd3, Nxf4, Qb5, Qc6, Qc7, Qc8, Qd6, Qd8, Qe8, Qxa4, Ra6, Ra7, Rab8, Rac8, Rad8, Rae8, Raf8, Rag8, Rh6, Rh7, Rhb8, Rhc8, Rhd8, Rhe8, Rhf8, Rhg8, b5, c4, g5");
    checkLegalMoves(board, "Ba6",
        "Bc3, Be1, Be3, Bf1, Bh1, Bh3, Bxb4, Kh1, Kh3, Nb1, Nb5, Nc4, Ne1, Ng5, Nxd4, Qd1, Qe1, Qe3, Qf1, Qf2, Qh1, Qxd4, Ra2, Rab1, Rcb1, Rd1, Re1, Rf1, c3, c4, g4");
    checkLegalMoves(board, "Qf1",
        "Bb5, Bb7, Bc4, Bc8, Bd6, Bd8, Bf6, Bf8, Bg5, Bxd3, Bxh4, Kf7, Kf8, Kg8, Kh6, Kh7, Na2, Nc3, Nc6, Nc7, Ne3, Nf6, Nxc2, Nxd3, Nxf4, Qa7, Qb5, Qb7, Qc6, Qc7, Qc8, Qd6, Qd8, Qe8, Qxa4, Ra7, Rab8, Rac8, Rad8, Rae8, Raf8, Rag8, Rh6, Rh7, Rhb8, Rhc8, Rhd8, Rhe8, Rhf8, Rhg8, b5, c4, g5");
    checkLegalMoves(board, "Rab8",
        "Bc3, Be1, Be3, Bh1, Bh3, Bxb4, Kg1, Kh1, Kh3, Nb1, Nb5, Nc4, Ne1, Ng1, Ng5, Nxd4, Qd1, Qe1, Qe2, Qf2, Qg1, Qh1, Ra2, Rab1, Rcb1, Rd1, Re1, c3, c4, g4");
    checkLegalMoves(board, "Nc4",
        "Bb5, Bb7, Bc8, Bd6, Bd8, Bf6, Bf8, Bg5, Bxc4, Bxh4, Kf7, Kf8, Kg8, Kh6, Kh7, Na2, Nc3, Nc6, Nc7, Ne3, Nf6, Nxc2, Nxd3, Nxf4, Qa7, Qb5, Qb7, Qc6, Qc7, Qc8, Qd6, Qd8, Qe8, Qxa4, Ra8, Rb7, Rbc8, Rbd8, Rbe8, Rbf8, Rbg8, Rh6, Rh7, Rhc8, Rhd8, Rhe8, Rhf8, Rhg8, b5, g5");
    checkLegalMoves(board, "Bd8",
        "Bc3, Be1, Be3, Bh1, Bh3, Bxb4, Kg1, Kh1, Kh3, Na3, Nb2, Nd6, Ne1, Ne3, Ng1, Ng5, Nxa5, Nxb6, Nxd4, Qd1, Qe1, Qe2, Qf2, Qg1, Qh1, Ra2, Ra3, Rab1, Rcb1, Rd1, Re1, c3, g4");
    checkLegalMoves(board, "Qd1",
        "Bb5, Bb7, Bc7, Bc8, Be7, Bf6, Bg5, Bxc4, Bxh4, Kf7, Kf8, Kg8, Kh6, Kh7, Na2, Nc3, Nc6, Nc7, Ne3, Ne7, Nf6, Nxc2, Nxd3, Nxf4, Qa7, Qb5, Qb7, Qc6, Qc7, Qc8, Qd6, Qe7, Qe8, Qf7, Qxa4, Ra8, Rb7, Rc8, Re8, Rf8, Rg8, Rh6, Rh7, b5, g5");
    checkLegalMoves(board, "Ne7",
        "Bc3, Be1, Be3, Bf1, Bh1, Bh3, Bxb4, Kg1, Kh1, Kh3, Na3, Nb2, Nd6, Ne1, Ne3, Ng1, Ng5, Nxa5, Nxb6, Nxd4, Qe1, Qe2, Qf1, Qg1, Qh1, Ra2, Ra3, Rab1, Rcb1, c3, g4");
    checkLegalMoves(board, "Nd6",
        "Bb5, Bb7, Bc4, Bc7, Bc8, Bxd3, Kf8, Kg8, Kh6, Kh7, Na2, Nbc6, Nbd5, Nc8, Nec6, Ned5, Ng8, Nxc2, Nxd3, Qa7, Qb5, Qb7, Qc6, Qc7, Qc8, Qe8, Qxa4, Qxd6, Ra8, Rb7, Rc8, Re8, Rf8, Rg8, Rh6, Rh7, b5, c4, g5");
    checkLegalMoves(board, "Bc7",
        "Bc3, Be1, Be3, Bf1, Bh1, Bh3, Bxb4, Kg1, Kh1, Kh3, Nb5, Nb7, Nc4, Nc8, Ne1, Ne4, Ne8+, Nf7, Ng1, Ng5, Nxd4, Nxf5+, Qe1, Qe2, Qf1, Qg1, Qh1, Ra2, Ra3, Rab1, Rcb1, c3, c4, g4");
    checkLegalMoves(board, "Qe2",
        "Bb5, Bb7, Bc4, Bc8, Bd8, Bxd3, Bxd6, Kf8, Kg8, Kh6, Kh7, Na2, Nbc6, Nbd5, Nc8, Nec6, Ned5, Ng8, Nxc2, Nxd3, Qb5, Qc6, Qc8, Qd8, Qe8, Qxa4, Qxd6, Ra8, Rb7, Rbc8, Rbd8, Rbe8, Rbf8, Rbg8, Rh6, Rh7, Rhc8, Rhd8, Rhe8, Rhf8, Rhg8, b5, c4, g5");
    checkLegalMoves(board, "Ng8",
        "Bc3, Be1, Be3, Bf1, Bh1, Bh3, Bxb4, Kg1, Kh1, Kh3, Nb5, Nb7, Nc4, Nc8, Ne1, Ne4, Ne8+, Nf7, Ng1, Ng5, Nxd4, Nxf5+, Qd1, Qe1, Qe3, Qe4, Qf1, Qf2, Ra2, Ra3, Rab1, Rcb1, Rd1, Re1, Rf1, Rg1, Rh1, c3, c4, g4");
    checkLegalMoves(board, "Ng5",
        "Bb5, Bb7, Bc4, Bc8, Bd8, Bxd3, Bxd6, Kf8, Kh6, Na2, Nc6, Nd5, Ne7, Nf6, Nh6, Nxc2, Nxd3, Qb5, Qc6, Qc8, Qd8, Qe7, Qe8, Qf7, Qxa4, Qxd6, Ra8, Rb7, Rc8, Rd8, Re8, Rf8, Rh6, Rh7, b5, c4");
    checkLegalMoves(board, "Nh6",
        "Ba8, Bb7, Bc3, Bc6, Bd5, Be1, Be3, Be4, Bf1, Bf3, Bh1, Bh3, Bxb4, Kg1, Kh1, Kh3, Nb5, Nb7, Nc4, Nc8, Nde4, Ndf7, Ne8+, Nf3, Nge4, Ngf7, Nh3, Nh7, Nxe6+, Nxf5+, Qd1, Qe1, Qe3, Qe4, Qf1, Qf2, Qf3, Qg4, Qxh5, Ra2, Ra3, Rab1, Rcb1, Rd1, Re1, Rf1, Rg1, Rh1, c3, c4, g4");
    checkLegalMoves(board, "Bf3",
        "Bb5, Bb7, Bc4, Bc8, Bd8, Bxd3, Bxd6, Kf8, Kg8, Na2, Nc6, Nd5, Nf7, Ng4+, Ng8, Nxc2, Nxd3, Qb5, Qc6, Qc8, Qd8, Qe7, Qe8, Qf7, Qxa4, Qxd6, Ra8, Rb7, Rbc8, Rbd8, Rbe8, Rbf8, Rbg8, Rh7, Rhc8, Rhd8, Rhe8, Rhf8, Rhg8, b5, c4");
    checkLegalMoves(board, "Bd8",
        "Ba8, Bb7, Bc3, Bc6, Bd5, Be1, Be3, Be4, Bg2, Bg4, Bh1, Bxb4, Bxh5, Kg1, Kg2, Kh1, Kh3, Nb5, Nb7, Nc4, Nc8, Nde4, Ndf7, Ne8+, Nge4, Ngf7, Nh3, Nh7, Nxe6+, Nxf5+, Qd1, Qe1, Qe3, Qe4, Qf1, Qf2, Qg2, Ra2, Ra3, Rab1, Rcb1, Rd1, Re1, Rf1, Rg1, Rh1, c3, c4, g4");
    checkLegalMoves(board, "Nh3",
        "Bb5, Bb7, Bc4, Bc7, Bc8, Be7, Bf6, Bg5, Bxd3, Bxh4, Kf8, Kg8, Kh7, Na2, Nc6, Nd5, Nf7, Ng4+, Ng8, Nxc2, Nxd3, Qa7, Qb5, Qb7, Qc6, Qc7, Qc8, Qe7, Qe8, Qf7, Qxa4, Qxd6, Ra8, Rb7, Rc8, Re8, Rf8, Rg8, Rh7, b5, c4, g5");
    checkLegalMoves(board, "Ng4+", "Bxg4, Kg1, Kg2, Kh1");
    checkLegalMoves(board, "Kg1",
        "Bb5, Bb7, Bc4, Bc7, Bc8, Be7, Bf6, Bg5, Bxd3, Bxh4, Kf8, Kg8, Kh6, Kh7, Na2, Nc6, Nd5, Ne3, Nf2, Nf6, Nh2, Nh6, Nxc2, Nxd3, Nxe5, Qa7, Qb5, Qb7, Qc6, Qc7, Qc8, Qe7, Qe8, Qf7, Qxa4, Qxd6, Ra8, Rb7, Rc8, Re8, Rf8, Rg8, Rh6, Rh7, b5, c4, g5");
    checkLegalMoves(board, "Be7",
        "Ba8, Bb7, Bc3, Bc6, Bd5, Be1, Be3, Be4, Bg2, Bh1, Bxb4, Bxg4, Kf1, Kg2, Kh1, Nb5, Nb7, Nc4, Nc8, Ne4, Ne8+, Nf2, Nf7, Ng5, Nxf5+, Qd1, Qe1, Qe3, Qe4, Qf1, Qf2, Qg2, Qh2, Ra2, Ra3, Rab1, Rcb1, Rd1, Re1, Rf1, c3, c4");
    checkLegalMoves(board, "Nc4",
        "Bb5, Bb7, Bc8, Bd6, Bd8, Bf6, Bf8, Bg5, Bxc4, Bxh4, Kf7, Kf8, Kg8, Kh6, Kh7, Na2, Nc6, Nd5, Ne3, Nf2, Nf6, Nh2, Nh6, Nxc2, Nxd3, Nxe5, Qa7, Qb5, Qb7, Qc6, Qc7, Qc8, Qd5, Qd6, Qd8, Qe8, Qxa4, Ra8, Rb7, Rbc8, Rbd8, Rbe8, Rbf8, Rbg8, Rh6, Rh7, Rhc8, Rhd8, Rhe8, Rhf8, Rhg8, b5, g5");
    checkLegalMoves(board, "Nd5",
        "Bb4, Bc3, Be1, Be3, Be4, Bg2, Bh1, Bxa5, Bxd5, Bxg4, Kf1, Kg2, Kh1, Na3, Nb2, Nd6, Ne3, Nf2, Ng5, Nxa5, Nxb6, Qd1, Qe1, Qe3, Qe4, Qf1, Qf2, Qg2, Qh2, Ra2, Ra3, Rab1, Rcb1, Rd1, Re1, Rf1, b4, c3");
    checkLegalMoves(board, "Nf2",
        "Bb5, Bb7, Bc8, Bd6, Bd8, Bf6, Bf8, Bg5, Bxc4, Bxh4, Kf7, Kf8, Kg8, Kh6, Kh7, Nb4, Nc3, Nc7, Nde3, Ndf6, Nge3, Ngf6, Nh2, Nh6, Nxe5, Nxf2, Nxf4, Qa7, Qb5, Qb7, Qc6, Qc7, Qc8, Qd6, Qd8, Qe8, Qxa4, Ra8, Rb7, Rbc8, Rbd8, Rbe8, Rbf8, Rbg8, Rh6, Rh7, Rhc8, Rhd8, Rhe8, Rhf8, Rhg8, b5, g5");
    checkLegalMoves(board, "Bb7",
        "Bb4, Bc3, Be1, Be3, Be4, Bg2, Bh1, Bxa5, Bxd5, Bxg4, Kf1, Kg2, Kh1, Na3, Nb2, Nd1, Nd6, Ne3, Ne4, Nh1, Nh3, Nxa5, Nxb6, Nxg4, Qd1, Qe1, Qe3, Qe4, Qf1, Ra2, Ra3, Rab1, Rcb1, Rd1, Re1, Rf1, b4, c3");
    checkLegalMoves(board, "Nh3",
        "Ba6, Ba8, Bc6, Bc8, Bd6, Bd8, Bf6, Bf8, Bg5, Bxh4, Kf7, Kf8, Kg8, Kh6, Kh7, Nb4, Nc3, Nc7, Nde3, Ndf6, Nf2, Nge3, Ngf6, Nh2, Nh6, Nxe5, Nxf4, Qb5, Qc6, Qc7, Qc8, Qd6, Qd8, Qe8, Qxa4, Ra8, Rbc8, Rbd8, Rbe8, Rbf8, Rbg8, Rh6, Rh7, Rhc8, Rhd8, Rhe8, Rhf8, Rhg8, b5, g5");
    checkLegalMoves(board, "Bc6",
        "Bb4, Bc3, Be1, Be3, Be4, Bg2, Bh1, Bxa5, Bxd5, Bxg4, Kf1, Kg2, Kh1, Na3, Nb2, Nd6, Ne3, Nf2, Ng5, Nxa5, Nxb6, Qd1, Qe1, Qe3, Qe4, Qf1, Qf2, Qg2, Qh2, Ra2, Ra3, Rab1, Rcb1, Rd1, Re1, Rf1, b4, c3");
    checkLegalMoves(board, "Qg2",
        "Ba8, Bb5, Bb7, Bd6, Bd8, Bf6, Bf8, Bg5, Bxa4, Bxh4, Kf7, Kf8, Kg8, Kh6, Kh7, Nb4, Nc3, Nc7, Nde3, Ndf6, Nf2, Nge3, Ngf6, Nh2, Nh6, Nxe5, Nxf4, Qa7, Qb7, Qc7, Qc8, Qd6, Qd8, Qe8, Ra8, Rb7, Rbc8, Rbd8, Rbe8, Rbf8, Rbg8, Rh6, Rh7, Rhc8, Rhd8, Rhe8, Rhf8, Rhg8, b5, g5");
    checkLegalMoves(board, "Rhc8",
        "Bb4, Bc3, Bd1, Be1, Be2, Be3, Be4, Bxa5, Bxd5, Bxg4, Kf1, Kh1, Na3, Nb2, Nd6, Ne3, Nf2, Ng5, Nxa5, Nxb6, Qe2, Qf1, Qf2, Qh1, Qh2, Ra2, Ra3, Rab1, Rcb1, Rd1, Re1, Rf1, b4, c3");
    checkLegalMoves(board, "Re1",
        "Ba8, Bb5, Bb7, Bd6, Bd8, Bf6, Bf8, Bg5, Bxa4, Bxh4, Kf7, Kf8, Kg8, Kh6, Kh7, Kh8, Nb4, Nc3, Nc7, Nde3, Ndf6, Nf2, Nge3, Ngf6, Nh2, Nh6, Nxe5, Nxf4, Qa7, Qb7, Qc7, Qd6, Qd8, Qe8, Ra8, Rb7, Rc7, Rd8, Re8, Rf8, Rg8, Rh8, b5, g5");
    checkLegalMoves(board, "Rc7",
        "Bb4, Bc1, Bc3, Bd1, Be2, Be3, Be4, Bxa5, Bxd5, Bxg4, Kf1, Kh1, Na3, Nb2, Nd6, Ne3, Nf2, Ng5, Nxa5, Nxb6, Qe2, Qf1, Qf2, Qh1, Qh2, Ra2, Ra3, Rab1, Rac1, Rad1, Re2, Re3, Re4, Reb1, Rec1, Red1, Rf1, b4, c3");
    checkLegalMoves(board, "Re2",
        "Ba8, Bb5, Bb7, Bd6, Bd8, Bf6, Bf8, Bg5, Bxa4, Bxh4, Kf7, Kf8, Kg8, Kh6, Kh7, Kh8, Nb4, Nc3, Nde3, Ndf6, Nf2, Nge3, Ngf6, Nh2, Nh6, Nxe5, Nxf4, Qc8, Qd6, Qd8, Qe8, Ra7, Ra8, Rbb7, Rbc8, Rcb7, Rcc8, Rd8, Re8, Rf8, Rg8, Rh8, b5, g5");
    checkLegalMoves(board, "Ra7",
        "Bb4, Bc1, Bc3, Be1, Be3, Be4, Bxa5, Bxd5, Bxg4, Kf1, Kh1, Na3, Nb2, Nd6, Ne3, Nf2, Ng5, Nxa5, Nxb6, Qf1, Qf2, Qh1, Qh2, Ra2, Ra3, Rae1, Rb1, Rc1, Rd1, Re3, Re4, Ree1, Rf1, Rf2, b4, c3");
    checkLegalMoves(board, "Ree1",
        "Ba8, Bb5, Bb7, Bd6, Bd8, Bf6, Bf8, Bg5, Bxa4, Bxh4, Kf7, Kf8, Kg8, Kh6, Kh7, Kh8, Nb4, Nc3, Nc7, Nde3, Ndf6, Nf2, Nge3, Ngf6, Nh2, Nh6, Nxe5, Nxf4, Qb7, Qc7, Qc8, Qd6, Qd8, Qe8, Ra6, Raa8, Rab7, Rba8, Rbb7, Rc7, Rc8, Rd8, Re8, Rf8, Rg8, Rh8, b5, g5");
    checkLegalMoves(board, "Ra6",
        "Bb4, Bc1, Bc3, Bd1, Be2, Be3, Be4, Bxa5, Bxd5, Bxg4, Kf1, Kh1, Na3, Nb2, Nd6, Ne3, Nf2, Ng5, Nxa5, Nxb6, Qe2, Qf1, Qf2, Qh1, Qh2, Ra2, Ra3, Rab1, Rac1, Rad1, Re2, Re3, Re4, Reb1, Rec1, Red1, Rf1, b4, c3");
    checkLegalMoves(board, "Re2",
        "Ba8, Bb5, Bb7, Bd6, Bd8, Bf6, Bf8, Bg5, Bxa4, Bxh4, Kf7, Kf8, Kg8, Kh6, Kh7, Kh8, Nb4, Nc3, Nc7, Nde3, Ndf6, Nf2, Nge3, Ngf6, Nh2, Nh6, Nxe5, Nxf4, Qa7, Qb7, Qc7, Qc8, Qd6, Qd8, Qe8, Ra7, Raa8, Rb7, Rba8, Rc8, Rd8, Re8, Rf8, Rg8, Rh8, b5, g5");
    checkLegalMoves(board, "Rba8",
        "Bb4, Bc1, Bc3, Be1, Be3, Be4, Bxa5, Bxd5, Bxg4, Kf1, Kh1, Na3, Nb2, Nd6, Ne3, Nf2, Ng5, Nxa5, Nxb6, Qf1, Qf2, Qh1, Qh2, Ra2, Ra3, Rae1, Rb1, Rc1, Rd1, Re3, Re4, Ree1, Rf1, Rf2, b4, c3");
    checkLegalMoves(board, "Ree1",
        "Bb5, Bb7, Bd6, Bd8, Bf6, Bf8, Bg5, Bxa4, Bxh4, Kf7, Kf8, Kg8, Kh6, Kh7, Kh8, Nb4, Nc3, Nc7, Nde3, Ndf6, Nf2, Nge3, Ngf6, Nh2, Nh6, Nxe5, Nxf4, Qa7, Qb7, Qc7, Qc8, Qd6, Qd8, Qe8, R6a7, R8a7, Rb8, Rc8, Rd8, Re8, Rf8, Rg8, Rh8, b5, g5");
    checkLegalMoves(board, "R8a7",
        "Bb4, Bc1, Bc3, Bd1, Be2, Be3, Be4, Bxa5, Bxd5, Bxg4, Kf1, Kh1, Na3, Nb2, Nd6, Ne3, Nf2, Ng5, Nxa5, Nxb6, Qe2, Qf1, Qf2, Qh1, Qh2, Ra2, Ra3, Rab1, Rac1, Rad1, Re2, Re3, Re4, Reb1, Rec1, Red1, Rf1, b4, c3");
    checkLegalMoves(board, "Na3",
        "Ba8, Bb5, Bb7, Bd6, Bd8, Bf6, Bf8, Bg5, Bxa4, Bxh4, Kf7, Kf8, Kg8, Kh6, Kh7, Kh8, Nb4, Nc3, Nc7, Nde3, Ndf6, Nf2, Nge3, Ngf6, Nh2, Nh6, Nxe5, Nxf4, Qb7, Qc7, Qc8, Qd6, Qd8, Qe8, Ra8, Rb7, Rc7, b5, c4, g5");
    checkLegalMoves(board, "Ra8",
        "Bb4, Bc1, Bc3, Bd1, Be2, Be3, Be4, Bxa5, Bxd5, Bxg4, Kf1, Kh1, Nb1, Nb5, Nc4, Nf2, Ng5, Qe2, Qf1, Qf2, Qh1, Qh2, Ra2, Rab1, Rac1, Rad1, Re2, Re3, Re4, Reb1, Rec1, Red1, Rf1, b4, c3, c4");
    checkLegalMoves(board, "Nc4",
        "Bb5, Bb7, Bd6, Bd8, Bf6, Bf8, Bg5, Bxa4, Bxh4, Kf7, Kf8, Kg8, Kh6, Kh7, Kh8, Nb4, Nc3, Nc7, Nde3, Ndf6, Nf2, Nge3, Ngf6, Nh2, Nh6, Nxe5, Nxf4, Qa7, Qb7, Qc7, Qc8, Qd6, Qd8, Qe8, R6a7, R8a7, Rb8, Rc8, Rd8, Re8, Rf8, Rg8, Rh8, b5, g5");
    checkLegalMoves(board, "Nh6",
        "Bb4, Bc1, Bc3, Bd1, Be2, Be3, Be4, Bg4, Bxa5, Bxd5, Bxh5, Kf1, Kf2, Kh1, Kh2, Na3, Nb2, Nd6, Ne3, Nf2, Ng5, Nxa5, Nxb6, Qe2, Qf1, Qf2, Qh1, Qh2, Ra2, Ra3, Rab1, Rac1, Rad1, Re2, Re3, Re4, Reb1, Rec1, Red1, Rf1, b4, c3, g4");
    checkLegalMoves(board, "Na3",
        "Bb5, Bb7, Bd6, Bd8, Bf6, Bf8, Bg5, Bxa4, Bxh4, Kf7, Kf8, Kg8, Kh7, Kh8, Nb4, Nc3, Nc7, Ne3, Nf6, Nf7, Ng4, Ng8, Nxf4, Qa7, Qb7, Qc7, Qc8, Qd6, Qd8, Qe8, R6a7, R8a7, Rb8, Rc8, Rd8, Re8, Rf8, Rg8, Rh8, b5, c4, g5");
    checkLegalMoves(board, "Nf7",
        "Bb4, Bc1, Bc3, Bd1, Be2, Be3, Be4, Bg4, Bxa5, Bxd5, Bxh5, Kf1, Kf2, Kh1, Kh2, Nb1, Nb5, Nc4, Nf2, Ng5, Qe2, Qf1, Qf2, Qh1, Qh2, Ra2, Rab1, Rac1, Rad1, Re2, Re3, Re4, Reb1, Rec1, Red1, Rf1, b4, c3, c4, g4");
    checkLegalMoves(board, "Nf2",
        "Bb5, Bb7, Bd6, Bd8, Bf6, Bf8, Bg5, Bxa4, Bxh4, Kf8, Kg8, Kh6, Kh7, Kh8, Nb4, Nc3, Nc7, Nd6, Nd8, Ne3, Nf6, Ng5, Nh6, Nh8, Nxe5, Nxf4, Qa7, Qb7, Qc7, Qc8, Qd6, Qd8, Qe8, R6a7, R8a7, Rb8, Rc8, Rd8, Re8, Rf8, Rg8, Rh8, b5, c4, g5");
    checkLegalMoves(board, "Rd8",
        "Bb4, Bc1, Bc3, Bd1, Be2, Be3, Be4, Bg4, Bxa5, Bxd5, Bxh5, Kf1, Kh1, Kh2, Nb1, Nb5, Nc4, Nd1, Ne4, Ng4, Nh1, Nh3, Qf1, Qh1, Qh2, Qh3, Ra2, Rab1, Rac1, Rad1, Re2, Re3, Re4, Reb1, Rec1, Red1, Rf1, b4, c3, c4, g4");
    checkLegalMoves(board, "Nc4",
        "Ba8, Bb5, Bb7, Bd6, Bf6, Bf8, Bg5, Bxa4, Bxh4, Kf8, Kg8, Kh6, Kh7, Kh8, Nb4, Nc3, Nc7, Nd6, Ne3, Nf6, Ng5, Nh6, Nh8, Nxe5, Nxf4, Qa7, Qb7, Qc7, Qc8, Qd6, Qe8, Ra7, Raa8, Rb8, Rc8, Rda8, Re8, Rf8, Rg8, Rh8, b5, g5");
    checkLegalMoves(board, "Rb8",
        "Bb4, Bc1, Bc3, Bd1, Be2, Be3, Be4, Bg4, Bxa5, Bxd5, Bxh5, Kf1, Kh1, Kh2, Na3, Nb2, Nd1, Nd6, Ne3, Ne4, Ng4, Nh1, Nh3, Nxa5, Nxb6, Qf1, Qh1, Qh2, Qh3, Ra2, Ra3, Rab1, Rac1, Rad1, Re2, Re3, Re4, Reb1, Rec1, Red1, Rf1, b4, c3, g4");
    checkLegalMoves(board, "Nh3",
        "Ba8, Bb5, Bb7, Bd6, Bd8, Bf6, Bf8, Bg5, Bxa4, Bxh4, Kf8, Kg8, Kh6, Kh7, Kh8, Nb4, Nc3, Nc7, Nd6, Nd8, Ne3, Nf6, Ng5, Nh6, Nh8, Nxe5, Nxf4, Qa7, Qb7, Qc7, Qc8, Qd6, Qd8, Qe8, Ra7, Raa8, Rb7, Rba8, Rc8, Rd8, Re8, Rf8, Rg8, Rh8, b5, g5");
    checkLegalMoves(board, "Bd8",
        "Bb4, Bc1, Bc3, Bd1, Be2, Be3, Be4, Bg4, Bxa5, Bxd5, Bxh5, Kf1, Kf2, Kh1, Kh2, Na3, Nb2, Nd6, Ne3, Nf2, Ng5, Nxa5, Nxb6, Qe2, Qf1, Qf2, Qh1, Qh2, Ra2, Ra3, Rab1, Rac1, Rad1, Re2, Re3, Re4, Reb1, Rec1, Red1, Rf1, b4, c3, g4");
    checkLegalMoves(board, "Na3",
        "Ba8, Bb5, Bb7, Bc7, Be7, Bf6, Bg5, Bxa4, Bxh4, Kf8, Kg8, Kh6, Kh7, Kh8, Nb4, Nc3, Nc7, Nd6, Ne3, Ne7, Nf6, Ng5, Nh6, Nh8, Nxe5, Nxf4, Qa7, Qb7, Qc7, Qc8, Qd6, Qe7, Qe8, Ra7, Raa8, Rb7, Rba8, Rc8, b5, c4, g5");
    checkLegalMoves(board, "Ra7",
        "Bb4, Bc1, Bc3, Bd1, Be2, Be3, Be4, Bg4, Bxa5, Bxd5, Bxh5, Kf1, Kf2, Kh1, Kh2, Nb1, Nb5, Nc4, Nf2, Ng5, Qe2, Qf1, Qf2, Qh1, Qh2, Ra2, Rab1, Rac1, Rad1, Re2, Re3, Re4, Reb1, Rec1, Red1, Rf1, b4, c3, c4, g4");
    checkLegalMoves(board, "Qh1",
        "Ba8, Bb5, Bb7, Bc7, Be7, Bf6, Bg5, Bxa4, Bxh4, Kf8, Kg8, Kh6, Kh7, Kh8, Nb4, Nc3, Nc7, Nd6, Ne3, Ne7, Nf6, Ng5, Nh6, Nh8, Nxe5, Nxf4, Qb7, Qc7, Qc8, Qd6, Qe7, Qe8, Ra6, Raa8, Rab7, Rba8, Rbb7, Rc7, Rc8, b5, c4, g5");
    checkLegalMoves(board, "Bc7",
        "Bb4, Bc1, Bc3, Bd1, Be2, Be3, Be4, Bg2, Bg4, Bxa5, Bxd5, Bxh5, Kf1, Kf2, Kg2, Kh2, Nb1, Nb5, Nc4, Nf2, Ng5, Qg2, Qh2, Ra2, Rab1, Rac1, Rad1, Re2, Re3, Re4, Reb1, Rec1, Red1, Rf1, b4, c3, c4, g4");
    checkLegalMoves(board, "Qg2",
        "Ba8, Bb5, Bb7, Bd6, Bd8, Bxa4, Bxe5, Kf8, Kg8, Kh6, Kh7, Kh8, Nb4, Nc3, Nd6, Nd8, Ne3, Ne7, Nf6, Ng5, Nh6, Nh8, Nxe5, Nxf4, Qc8, Qd6, Qd8, Qe7, Qe8, Ra6, Raa8, Rab7, Rba8, Rbb7, Rc8, Rd8, Re8, Rf8, Rg8, Rh8, b5, c4, g5");
    checkLegalMoves(board, "Rd8",
        "Bb4, Bc1, Bc3, Bd1, Be2, Be3, Be4, Bg4, Bxa5, Bxd5, Bxh5, Kf1, Kf2, Kh1, Kh2, Nb1, Nb5, Nc4, Nf2, Ng5, Qe2, Qf1, Qf2, Qh1, Qh2, Ra2, Rab1, Rac1, Rad1, Re2, Re3, Re4, Reb1, Rec1, Red1, Rf1, b4, c3, c4, g4");
    checkLegalMoves(board, "Qh1",
        "Ba8, Bb5, Bb7, Bb8, Bd6, Bxa4, Bxe5, Kf8, Kg8, Kh6, Kh7, Kh8, Nb4, Nc3, Nd6, Ne3, Ne7, Nf6, Ng5, Nh6, Nh8, Nxe5, Nxf4, Qc8, Qd6, Qe7, Qe8, Ra6, Raa8, Rb7, Rb8, Rc8, Rda8, Re8, Rf8, Rg8, Rh8, b5, c4, g5");
    checkLegalMoves(board, "Nh6",
        "Bb4, Bc1, Bc3, Bd1, Be2, Be3, Be4, Bg2, Bg4, Bxa5, Bxd5, Bxh5, Kf1, Kf2, Kg2, Kh2, Nb1, Nb5, Nc4, Nf2, Ng5, Qg2, Qh2, Ra2, Rab1, Rac1, Rad1, Re2, Re3, Re4, Reb1, Rec1, Red1, Rf1, b4, c3, c4, g4");
    checkLegalMoves(board, "Ng5",
        "Ba8, Bb5, Bb7, Bb8, Bd6, Bxa4, Bxe5, Kf8, Kg8, Kh8, Nb4, Nc3, Ne3, Ne7, Nf6, Nf7, Ng4, Ng8, Nxf4, Qc8, Qd6, Qe7, Qe8, Qf7, Ra6, Raa8, Rb7, Rb8, Rc8, Rda8, Re8, Rf8, Rg8, Rh8, b5, c4");
    checkLegalMoves(board, "Nf7",
        "Bb4, Bc1, Bc3, Bd1, Be2, Be3, Be4, Bg2, Bg4, Bxa5, Bxd5, Bxh5, Kf1, Kf2, Kg2, Kh2, Nb1, Nb5, Nc4, Ne4, Nh3, Nh7, Nxe6+, Nxf7, Qg2, Qh2, Qh3, Ra2, Rab1, Rac1, Rad1, Re2, Re3, Re4, Reb1, Rec1, Red1, Rf1, b4, c3, c4, g4");
    checkLegalMoves(board, "Nh3",
        "Ba8, Bb5, Bb7, Bb8, Bd6, Bxa4, Bxe5, Kf8, Kg8, Kh6, Kh7, Kh8, Nb4, Nc3, Nd6, Ne3, Ne7, Nf6, Ng5, Nh6, Nh8, Nxe5, Nxf4, Qc8, Qd6, Qe7, Qe8, Ra6, Raa8, Rb7, Rb8, Rc8, Rda8, Re8, Rf8, Rg8, Rh8, b5, c4, g5");
    checkLegalMoves(board, "Qe8",
        "Bb4, Bc1, Bc3, Bd1, Be2, Be3, Be4, Bg2, Bg4, Bxa5, Bxd5, Bxh5, Kf1, Kf2, Kg2, Kh2, Nb1, Nb5, Nc4, Nf2, Ng5, Qg2, Qh2, Ra2, Rab1, Rac1, Rad1, Re2, Re3, Re4, Reb1, Rec1, Red1, Rf1, b4, c3, c4, g4");
    checkLegalMoves(board, "Kh2",
        "Ba8, Bb5, Bb7, Bb8, Bd6, Bd7, Bxa4, Bxe5, Kf8, Kg8, Kh6, Kh7, Kh8, Nb4, Nc3, Nd6, Ne3, Ne7, Nf6, Ng5, Nh6, Nh8, Nxe5, Nxf4, Qd7, Qe7, Qf8, Qg8, Qh8, Ra6, Raa8, Rb7, Rb8, Rc8, Rd6, Rd7, Rda8, b5, c4, g5");
    checkLegalMoves(board, "Rd7",
        "Bb4, Bc1, Bc3, Bd1, Be2, Be3, Be4, Bg2, Bg4, Bxa5, Bxd5, Bxh5, Kg1, Kg2, Nb1, Nb5, Nc4, Nf2, Ng1, Ng5, Qf1, Qg1, Qg2, Ra2, Rab1, Rac1, Rad1, Re2, Re3, Re4, Reb1, Rec1, Red1, Rf1, Rg1, b4, c3, c4, g4");

  }

  @SuppressWarnings("static-method")
  @Test
  void testGame4() {
    // 2_2_karpov_kasparov_1991
    final Board board = new Board();
    checkInitial(board);

    checkLegalMoves(board, "d4", "Na6, Nc6, Nf6, Nh6, a5, a6, b5, b6, c5, c6, d5, d6, e5, e6, f5, f6, g5, g6, h5, h6");
    checkLegalMoves(board, "Nf6",
        "Bd2, Be3, Bf4, Bg5, Bh6, Kd2, Na3, Nc3, Nd2, Nf3, Nh3, Qd2, Qd3, a3, a4, b3, b4, c3, c4, d5, e3, e4, f3, f4, g3, g4, h3, h4");
    checkLegalMoves(board, "c4",
        "Na6, Nc6, Nd5, Ne4, Ng4, Ng8, Nh5, Rg8, a5, a6, b5, b6, c5, c6, d5, d6, e5, e6, g5, g6, h5, h6");
    checkLegalMoves(board, "g6",
        "Bd2, Be3, Bf4, Bg5, Bh6, Kd2, Na3, Nc3, Nd2, Nf3, Nh3, Qa4, Qb3, Qc2, Qd2, Qd3, a3, a4, b3, b4, c5, d5, e3, e4, f3, f4, g3, g4, h3, h4");
    checkLegalMoves(board, "Nc3",
        "Bg7, Bh6, Na6, Nc6, Nd5, Ne4, Ng4, Ng8, Nh5, Rg8, a5, a6, b5, b6, c5, c6, d5, d6, e5, e6, g5, h5, h6");
    checkLegalMoves(board, "Bg7",
        "Bd2, Be3, Bf4, Bg5, Bh6, Kd2, Na4, Nb1, Nb5, Nd5, Ne4, Nf3, Nh3, Qa4, Qb3, Qc2, Qd2, Qd3, Rb1, a3, a4, b3, b4, c5, d5, e3, e4, f3, f4, g3, g4, h3, h4");
    checkLegalMoves(board, "e4",
        "Bf8, Bh6, Kf8, Na6, Nc6, Nd5, Ng4, Ng8, Nh5, Nxe4, O-O, Rf8, Rg8, a5, a6, b5, b6, c5, c6, d5, d6, e5, e6, g5, h5, h6");
    checkLegalMoves(board, "d6",
        "Bd2, Bd3, Be2, Be3, Bf4, Bg5, Bh6, Kd2, Ke2, Na4, Nb1, Nb5, Nce2, Nd5, Nf3, Nge2, Nh3, Qa4+, Qb3, Qc2, Qd2, Qd3, Qe2, Qf3, Qg4, Qh5, Rb1, a3, a4, b3, b4, c5, d5, e5, f3, f4, g3, g4, h3, h4");
    checkLegalMoves(board, "Nf3",
        "Bd7, Be6, Bf5, Bf8, Bg4, Bh3, Bh6, Kd7, Kf8, Na6, Nbd7, Nc6, Nd5, Nfd7, Ng4, Ng8, Nh5, Nxe4, O-O, Qd7, Rf8, Rg8, a5, a6, b5, b6, c5, c6, d5, e5, e6, g5, h5, h6");
    checkLegalMoves(board, "O-O",
        "Bd2, Bd3, Be2, Be3, Bf4, Bg5, Bh6, Kd2, Ke2, Na4, Nb1, Nb5, Nd2, Nd5, Ne2, Ne5, Ng1, Ng5, Nh4, Qa4, Qb3, Qc2, Qd2, Qd3, Qe2, Rb1, Rg1, a3, a4, b3, b4, c5, d5, e5, g3, g4, h3, h4");
    checkLegalMoves(board, "Be2",
        "Bd7, Be6, Bf5, Bg4, Bh3, Bh6, Bh8, Kh8, Na6, Nbd7, Nc6, Nd5, Ne8, Nfd7, Ng4, Nh5, Nxe4, Qd7, Qe8, Re8, a5, a6, b5, b6, c5, c6, d5, e5, e6, g5, h5, h6");
    checkLegalMoves(board, "e5",
        "Bd2, Bd3, Be3, Bf1, Bf4, Bg5, Bh6, Kd2, Kf1, Na4, Nb1, Nb5, Nd2, Nd5, Ng1, Ng5, Nh4, Nxe5, O-O, Qa4, Qb3, Qc2, Qd2, Qd3, Rb1, Rf1, Rg1, a3, a4, b3, b4, c5, d5, dxe5, g3, g4, h3, h4");
    checkLegalMoves(board, "O-O",
        "Bd7, Be6, Bf5, Bg4, Bh3, Bh6, Bh8, Kh8, Na6, Nbd7, Nc6, Nd5, Ne8, Nfd7, Ng4, Nh5, Nxe4, Qd7, Qe7, Qe8, Re8, a5, a6, b5, b6, c5, c6, d5, exd4, g5, h5, h6");
    checkLegalMoves(board, "Nc6",
        "Bd2, Bd3, Be3, Bf4, Bg5, Bh6, Kh1, Na4, Nb1, Nb5, Nd2, Nd5, Ne1, Ng5, Nh4, Nxe5, Qa4, Qb3, Qc2, Qd2, Qd3, Qe1, Rb1, Re1, a3, a4, b3, b4, c5, d5, dxe5, g3, g4, h3, h4");
    checkLegalMoves(board, "d5",
        "Bd7, Be6, Bf5, Bg4, Bh3, Bh6, Bh8, Kh8, Na5, Nb4, Nb8, Nd4, Nd7, Ne7, Ne8, Ng4, Nh5, Nxd5, Nxe4, Qd7, Qe7, Qe8, Rb8, Re8, a5, a6, b5, b6, g5, h5, h6");
    checkLegalMoves(board, "Ne7",
        "Bd2, Bd3, Be3, Bf4, Bg5, Bh6, Kh1, Na4, Nb1, Nb5, Nd2, Nd4, Ne1, Ng5, Nh4, Nxe5, Qa4, Qb3, Qc2, Qd2, Qd3, Qd4, Qe1, Rb1, Re1, a3, a4, b3, b4, c5, g3, g4, h3, h4");
    checkLegalMoves(board, "Nd2",
        "Bd7, Be6, Bf5, Bg4, Bh3, Bh6, Bh8, Kh8, Nc6, Nd7, Ne8, Nexd5, Nf5, Nfxd5, Ng4, Nh5, Nxe4, Qd7, Qe8, Rb8, Re8, a5, a6, b5, b6, c5, c6, g5, h5, h6");
    checkLegalMoves(board, "a5",
        "Bd3, Bf3, Bg4, Bh5, Kh1, Na4, Nb3, Nb5, Ncb1, Ndb1, Nf3, Qa4, Qb3, Qc2, Qe1, Rb1, Re1, a3, a4, b3, b4, c5, f3, f4, g3, g4, h3, h4");
    checkLegalMoves(board, "Rb1",
        "Bd7, Be6, Bf5, Bg4, Bh3, Bh6, Bh8, Kh8, Nc6, Nd7, Ne8, Nexd5, Nf5, Nfxd5, Ng4, Nh5, Nxe4, Qd7, Qe8, Ra6, Ra7, Rb8, Re8, a4, b5, b6, c5, c6, g5, h5, h6");
    checkLegalMoves(board, "Nd7",
        "Bd3, Bf3, Bg4, Bh5, Kh1, Na4, Nb3, Nb5, Nf3, Qa4, Qb3, Qc2, Qe1, Ra1, Re1, a3, a4, b3, b4, c5, f3, f4, g3, g4, h3, h4");
    checkLegalMoves(board, "a3",
        "Bf6, Bh6, Bh8, Kh8, Nb6, Nb8, Nc5, Nc6, Nf5, Nf6, Nxd5, Qe8, Ra6, Ra7, Rb8, Re8, a4, b5, b6, c5, c6, f5, f6, g5, h5, h6");
    checkLegalMoves(board, "f5",
        "Bd3, Bf3, Bg4, Bh5, Kh1, Na2, Na4, Nb3, Nb5, Nf3, Qa4, Qb3, Qc2, Qe1, Ra1, Re1, a4, b3, b4, c5, exf5, f3, f4, g3, g4, h3, h4");
    checkLegalMoves(board, "b4",
        "Bf6, Bh6, Bh8, Kf7, Kh8, Nb6, Nb8, Nc5, Nc6, Nf6, Nxd5, Qe8, Ra6, Ra7, Rb8, Re8, Rf6, Rf7, a4, axb4, b5, b6, c5, c6, f4, fxe4, g5, h5, h6");
    checkLegalMoves(board, "Kh8",
        "Bb2, Bd3, Bf3, Bg4, Bh5, Kh1, Na2, Na4, Nb3, Nb5, Nf3, Qa4, Qb3, Qc2, Qe1, Ra1, Rb2, Rb3, Re1, a4, b5, bxa5, c5, exf5, f3, f4, g3, g4, h3, h4");
    checkLegalMoves(board, "f3",
        "Bf6, Bh6, Kg8, Nb6, Nb8, Nc5, Nc6, Nf6, Ng8, Nxd5, Qe8, Ra6, Ra7, Rb8, Re8, Rf6, Rf7, Rg8, a4, axb4, b5, b6, c5, c6, f4, fxe4, g5, h5, h6");
    checkLegalMoves(board, "Ng8",
        "Bb2, Bd3, Kf2, Kh1, Na2, Na4, Nb3, Nb5, Qa4, Qb3, Qc2, Qe1, Ra1, Rb2, Rb3, Re1, Rf2, a4, b5, bxa5, c5, exf5, f4, g3, g4, h3, h4");
    checkLegalMoves(board, "Qc2",
        "Bf6, Bh6, Nb6, Nb8, Nc5, Ndf6, Ne7, Ngf6, Nh6, Qe7, Qe8, Qf6, Qg5, Qh4, Ra6, Ra7, Rb8, Re8, Rf6, Rf7, a4, axb4, b5, b6, c5, c6, f4, fxe4, g5, h5, h6");
    checkLegalMoves(board, "Ngf6",
        "Bb2, Bd1, Bd3, Kf2, Kh1, Na2, Na4, Nb3, Nb5, Nd1, Qa2, Qa4, Qb2, Qb3, Qd1, Qd3, Ra1, Rb2, Rb3, Rd1, Re1, Rf2, a4, b5, bxa5, c5, exf5, f4, g3, g4, h3, h4");
    checkLegalMoves(board, "Nb5",
        "Bh6, Kg8, Nb6, Nb8, Nc5, Ne8, Ng4, Ng8, Nh5, Nxd5, Nxe4, Qe7, Qe8, Ra6, Ra7, Rb8, Re8, Rf7, Rg8, a4, axb4, b6, c5, c6, f4, fxe4, g5, h5, h6");
    checkLegalMoves(board, "axb4",
        "Bb2, Bd1, Bd3, Kf2, Kh1, Na7, Nb3, Nc3, Nd4, Nxc7, Nxd6, Qa2, Qa4, Qb2, Qb3, Qc3, Qd1, Qd3, Ra1, Rb2, Rb3, Rd1, Re1, Rf2, Rxb4, a4, axb4, c5, exf5, f4, g3, g4, h3, h4");
    checkLegalMoves(board, "axb4",
        "Bh6, Kg8, Nb6, Nb8, Nc5, Ne8, Ng4, Ng8, Nh5, Nxd5, Nxe4, Qe7, Qe8, Ra1, Ra2, Ra3, Ra4, Ra5, Ra6, Ra7, Rb8, Re8, Rf7, Rg8, b6, c5, c6, f4, fxe4, g5, h5, h6");
    checkLegalMoves(board, "Nh5",
        "Ba3, Bb2, Bd1, Bd3, Kf2, Kh1, Na3, Na7, Nb3, Nc3, Nd4, Nxc7, Nxd6, Qa2, Qa4, Qb2, Qb3, Qc3, Qd1, Qd3, Ra1, Rb2, Rb3, Rd1, Re1, Rf2, c5, exf5, f4, g3, g4, h3, h4");
    checkLegalMoves(board, "g3",
        "Bf6, Bh6, Kg8, Nb6, Nb8, Nc5, Ndf6, Nf4, Nhf6, Nxg3, Qe7, Qe8, Qf6, Qg5, Qh4, Ra1, Ra2, Ra3, Ra4, Ra5, Ra6, Ra7, Rb8, Re8, Rf6, Rf7, Rg8, b6, c5, c6, f4, fxe4, g5, h6");
    checkLegalMoves(board, "Ndf6",
        "Ba3, Bb2, Bd1, Bd3, Kf2, Kg2, Kh1, Na3, Na7, Nb3, Nc3, Nd4, Nxc7, Nxd6, Qa2, Qa4, Qb2, Qb3, Qc3, Qd1, Qd3, Ra1, Rb2, Rb3, Rd1, Re1, Rf2, c5, exf5, f4, g4, h3, h4");
    checkLegalMoves(board, "c5",
        "Bd7, Be6, Bh6, Kg8, Nd7, Ne8, Nf4, Ng4, Ng8, Nxd5, Nxe4, Nxg3, Qd7, Qe7, Qe8, Ra1, Ra2, Ra3, Ra4, Ra5, Ra6, Ra7, Rb8, Re8, Rf7, Rg8, b6, c6, dxc5, f4, fxe4, g5, h6");
    checkLegalMoves(board, "Bd7",
        "Ba3, Bb2, Bc4, Bd1, Bd3, Kf2, Kg2, Kh1, Na3, Na7, Nb3, Nc3, Nc4, Nd4, Nxc7, Nxd6, Qa2, Qa4, Qb2, Qb3, Qc3, Qc4, Qd1, Qd3, Ra1, Rb2, Rb3, Rd1, Re1, Rf2, c6, cxd6, exf5, f4, g4, h3, h4");
    checkLegalMoves(board, "Rb3",
        "Bc6, Bc8, Be6, Be8, Bh6, Bxb5, Kg8, Ne8, Nf4, Ng4, Ng8, Nxd5, Nxe4, Nxg3, Qb8, Qc8, Qe7, Qe8, Ra1, Ra2, Ra3, Ra4, Ra5, Ra6, Ra7, Rb8, Rc8, Re8, Rf7, Rg8, b6, c6, dxc5, f4, fxe4, g5, h6");
    checkLegalMoves(board, "Nxg3",
        "Ba3, Bb2, Bc4, Bd1, Bd3, Kf2, Kg2, Na3, Na7, Nb1, Nc3, Nc4, Nd4, Nxc7, Nxd6, Qa2, Qb1, Qb2, Qc3, Qc4, Qd1, Qd3, Ra3, Rb1, Rb2, Rc3, Rd1, Rd3, Re1, Re3, Rf2, c6, cxd6, exf5, f4, h3, h4, hxg3");
    checkLegalMoves(board, "hxg3",
        "Bc6, Bc8, Be6, Be8, Bh6, Bxb5, Kg8, Ne8, Ng4, Ng8, Nh5, Nxd5, Nxe4, Qb8, Qc8, Qe7, Qe8, Ra1, Ra2, Ra3, Ra4, Ra5, Ra6, Ra7, Rb8, Rc8, Re8, Rf7, Rg8, b6, c6, dxc5, f4, fxe4, g5, h5, h6");
    checkLegalMoves(board, "Nh5",
        "Ba3, Bb2, Bc4, Bd1, Bd3, Kf2, Kg2, Kh1, Kh2, Na3, Na7, Nb1, Nc3, Nc4, Nd4, Nxc7, Nxd6, Qa2, Qb1, Qb2, Qc3, Qc4, Qd1, Qd3, Ra3, Rb1, Rb2, Rc3, Rd1, Rd3, Re1, Re3, Rf2, c6, cxd6, exf5, f4, g4");
    checkLegalMoves(board, "f4",
        "Bc6, Bc8, Be6, Be8, Bf6, Bh6, Bxb5, Kg8, Nf6, Nxf4, Nxg3, Qb8, Qc8, Qe7, Qe8, Qf6, Qg5, Qh4, Ra1, Ra2, Ra3, Ra4, Ra5, Ra6, Ra7, Rb8, Rc8, Re8, Rf6, Rf7, Rg8, b6, c6, dxc5, exf4, fxe4, g5, h6");
    checkLegalMoves(board, "exf4",
        "Ba3, Bb2, Bc4, Bd1, Bd3, Bf3, Bg4, Bxh5, Kf2, Kg2, Kh1, Kh2, Na3, Na7, Nb1, Nc3, Nc4, Nd4, Nf3, Nxc7, Nxd6, Qa2, Qb1, Qb2, Qc3, Qc4, Qd1, Qd3, Ra3, Rb1, Rb2, Rbf3, Rc3, Rd1, Rd3, Re1, Re3, Rf2, Rff3, Rxf4, c6, cxd6, e5, exf5, g4, gxf4");
    checkLegalMoves(board, "c6",
        "Ba1, Bb2, Bc3, Bc8, Bd4+, Be5, Be6, Be8, Bf6, Bh6, Bxc6, Kg8, Nf6, Nxg3, Qb8, Qc8, Qe7, Qe8, Qf6, Qg5, Qh4, Ra1, Ra2, Ra3, Ra4, Ra5, Ra6, Ra7, Rb8, Rc8, Re8, Rf6, Rf7, Rg8, b6, bxc6, f3, fxe4, fxg3, g5, h6");
    checkLegalMoves(board, "bxc6",
        "Ba3, Bb2, Bc4, Bd1, Bd3, Bf3, Bg4, Bxh5, Kf2, Kg2, Kh1, Kh2, Na3, Na7, Nb1, Nc3, Nc4, Nd4, Nf3, Nxc7, Nxd6, Qa2, Qb1, Qb2, Qc3, Qc4, Qc5, Qd1, Qd3, Qxc6, Ra3, Rb1, Rb2, Rbf3, Rc3, Rd1, Rd3, Re1, Re3, Rf2, Rff3, Rxf4, dxc6, e5, exf5, g4, gxf4");
    checkLegalMoves(board, "dxc6",
        "Ba1, Bb2, Bc3, Bc8, Bd4+, Be5, Be6, Be8, Bf6, Bh6, Bxc6, Kg8, Nf6, Nxg3, Qb8, Qc8, Qe7, Qe8, Qf6, Qg5, Qh4, Ra1, Ra2, Ra3, Ra4, Ra5, Ra6, Ra7, Rb8, Rc8, Re8, Rf6, Rf7, Rg8, d5, f3, fxe4, fxg3, g5, h6");
    checkLegalMoves(board, "Nxg3",
        "Ba3, Bb2, Bc4, Bd1, Bd3, Bf3, Bg4, Bh5, Kf2, Kg2, Kh2, Na3, Na7, Nb1, Nc3, Nc4, Nd4, Nf3, Nxc7, Nxd6, Qa2, Qb1, Qb2, Qc3, Qc4, Qc5, Qd1, Qd3, Ra3, Rb1, Rb2, Rbf3, Rc3, Rd1, Rd3, Re1, Re3, Rf2, Rff3, Rxf4, Rxg3, cxd7, e5, exf5");
    checkLegalMoves(board, "Rxg3",
        "Ba1, Bb2, Bc3, Bc8, Bd4+, Be5, Be6, Be8, Bf6, Bh6, Bxc6, Kg8, Qb8, Qc8, Qe7, Qe8, Qf6, Qg5, Qh4, Ra1, Ra2, Ra3, Ra4, Ra5, Ra6, Ra7, Rb8, Rc8, Re8, Rf6, Rf7, Rg8, d5, f3, fxe4, fxg3, g5, h5, h6");
    checkLegalMoves(board, "fxg3",
        "Ba3, Bb2, Bc4, Bd1, Bd3, Bf3, Bg4, Bh5, Kg2, Kh1, Na3, Na7, Nb1, Nb3, Nc3, Nc4, Nd4, Nf3, Nxc7, Nxd6, Qa2, Qa4, Qb1, Qb2, Qb3, Qc3, Qc4, Qc5, Qd1, Qd3, Rd1, Re1, Rf2, Rf3, Rf4, Rxf5, cxd7, e5, exf5");
    checkLegalMoves(board, "cxd7",
        "Ba1, Bb2, Bc3, Bd4+, Be5, Bf6, Bh6, Kg8, Qb8, Qc8, Qe7, Qe8, Qf6, Qg5, Qh4, Qxd7, Ra1, Ra2, Ra3, Ra4, Ra5, Ra6, Ra7, Rb8, Rc8, Re8, Rf6, Rf7, Rg8, c5, c6, d5, f4, fxe4, g2, g5, h5, h6");
    checkLegalMoves(board, "g2",
        "Ba3, Bb2, Bc4, Bd1, Bd3, Bf3, Bg4, Bh5, Kf2, Kh2, Kxg2, Na3, Na7, Nb1, Nb3, Nc3, Nc4, Nd4, Nf3, Nxc7, Nxd6, Qa2, Qa4, Qb1, Qb2, Qb3, Qc3, Qc4, Qc5, Qc6, Qd1, Qd3, Qxc7, Rd1, Re1, Rf2, Rf3, Rf4, Rxf5, e5, exf5");
    checkLegalMoves(board, "Rf3",
        "Ba1, Bb2, Bc3, Bd4+, Be5, Bf6, Bh6, Kg8, Qb8, Qc8, Qe7, Qe8, Qf6, Qg5, Qh4, Qxd7, Ra1, Ra2, Ra3, Ra4, Ra5, Ra6, Ra7, Rb8, Rc8, Re8, Rf6, Rf7, Rg8, c5, c6, d5, f4, fxe4, g5, h5, h6");
    checkLegalMoves(board, "Qxd7",
        "Ba3, Bb2, Bc4, Bd1, Bd3, Bf1, Kf2, Kh2, Kxg2, Na3, Na7, Nb1, Nb3, Nc3, Nc4, Nd4, Nf1, Nxc7, Nxd6, Qa2, Qa4, Qb1, Qb2, Qb3, Qc3, Qc4, Qc5, Qc6, Qd1, Qd3, Qxc7, Ra3, Rb3, Rc3, Rd3, Re3, Rf1, Rf2, Rf4, Rg3, Rh3, Rxf5, e5, exf5");
    checkLegalMoves(board, "Bb2",
        "Bc3, Bd4+, Be5, Bf6, Bxb2, Kg8, Qc6, Qc8, Qd8, Qe6, Qe7, Qe8, Qf7, Qxb5, Ra1+, Ra2, Ra3, Ra4, Ra5, Ra6, Ra7, Rab8, Rac8, Rad8, Rae8, Rf6, Rf7, Rfb8, Rfc8, Rfd8, Rfe8, Rg8, c5, c6, d5, f4, fxe4, g5, h5, h6");
    checkLegalMoves(board, "fxe4",
        "Ba1, Ba3, Bc1, Bc3, Bc4, Bd1, Bd3, Bd4, Be5, Bf1, Bf6, Bxg7+, Kf2, Kh2, Kxg2, Na3, Na7, Nb1, Nb3, Nc3, Nc4, Nd4, Nf1, Nxc7, Nxd6, Nxe4, Qa4, Qb1, Qb3, Qc1, Qc3, Qc4, Qc5, Qc6, Qd1, Qd3, Qxc7, Qxe4, Ra3, Rb3, Rc3, Rd3, Re3, Rf1, Rf2, Rf4, Rf5, Rf6, Rf7, Rg3, Rh3, Rxf8+");
    checkLegalMoves(board, "Rxf8+", "Rxf8");
    checkLegalMoves(board, "Rxf8",
        "Ba1, Ba3, Bc1, Bc3, Bc4, Bd1, Bd3, Bd4, Be5, Bf1, Bf3, Bf6, Bg4, Bh5, Bxg7+, Kh2, Kxg2, Na3, Na7, Nb1, Nb3, Nc3, Nc4, Nd4, Nf1, Nf3, Nxc7, Nxd6, Nxe4, Qa4, Qb1, Qb3, Qc1, Qc3, Qc4, Qc5, Qc6, Qd1, Qd3, Qxc7, Qxe4");
    checkLegalMoves(board, "Bxg7+", "Kg8, Kxg7, Qxg7");
    checkLegalMoves(board, "Qxg7",
        "Bc4, Bd1, Bd3, Bf1, Bf3, Bg4, Bh5, Kh2, Kxg2, Na3, Na7, Nb1, Nb3, Nc3, Nc4, Nd4, Nf1, Nf3, Nxc7, Nxd6, Nxe4, Qa2, Qa4, Qb1, Qb2, Qb3, Qc1, Qc3, Qc4, Qc5, Qc6, Qd1, Qd3, Qxc7, Qxe4");
    checkLegalMoves(board, "Qxe4",
        "Kg8, Qa1+, Qb2, Qc3, Qd4+, Qd7, Qe5, Qe7, Qf6, Qf7, Qg8, Qh6, Ra8, Rb8, Rc8, Rd8, Re8, Rf1+, Rf2, Rf3, Rf4, Rf5, Rf6, Rf7, Rg8, c5, c6, d5, g5, h5, h6");
    checkLegalMoves(board, "Qf6",
        "Bc4, Bd1, Bd3, Bf1, Bf3, Bg4, Bh5, Kh2, Kxg2, Na3, Na7, Nb1, Nb3, Nc3, Nc4, Nd4, Nf1, Nf3, Nxc7, Nxd6, Qa8, Qb1, Qb7, Qc2, Qc4, Qc6, Qd3, Qd4, Qd5, Qe3, Qe5, Qe6, Qe7, Qe8, Qf3, Qf4, Qf5, Qg4, Qh4, Qxg2, Qxg6");
    checkLegalMoves(board, "Nf3",
        "Kg7, Kg8, Qa1+, Qb2, Qc3, Qd4+, Qd8, Qe5, Qe6, Qe7, Qf4, Qf5, Qf7, Qg5, Qg7, Qh4, Qxf3, Ra8, Rb8, Rc8, Rd8, Re8, Rf7, Rg8, c5, c6, d5, g5, h5, h6");
    checkLegalMoves(board, "Qf4",
        "Bc4, Bd1, Bd3, Bf1, Kf2, Kxg2, Na3, Na7, Nbd4, Nc3, Nd2, Ne1, Ne5, Nfd4, Ng5, Nh2, Nh4, Nxc7, Nxd6, Qa8, Qb1, Qb7, Qc2, Qc4, Qc6, Qd3, Qd4+, Qd5, Qe3, Qe5+, Qe6, Qe7, Qe8, Qf5, Qxf4, Qxg6");
    checkLegalMoves(board, "Qe7",
        "Kg8, Qc1+, Qc4, Qd2, Qd4+, Qe3+, Qe4, Qe5, Qf5, Qf6, Qf7, Qg3, Qg4, Qg5, Qh2+, Qh4, Qh6, Qxb4, Qxf3, Ra8, Rb8, Rc8, Rd8, Re8, Rf5, Rf6, Rf7, Rg8, c5, c6, d5, g5, h5, h6");
    checkLegalMoves(board, "Rf7",
        "Bc4, Bd1, Bd3, Bf1, Kf2, Kxg2, Na3, Na7, Nbd4, Nc3, Nd2, Ne1, Ne5, Nfd4, Ng5, Nh2, Nh4, Nxc7, Nxd6, Qd7, Qd8+, Qe3, Qe4, Qe5+, Qe6, Qe8+, Qf6+, Qf8+, Qg5, Qh4, Qxc7, Qxd6, Qxf7");
    checkLegalMoves(board, "Qe6",
        "Kg7, Kg8, Qc1+, Qc4, Qd2, Qd4+, Qe3+, Qe4, Qe5, Qf5, Qf6, Qg3, Qg4, Qg5, Qh2+, Qh4, Qh6, Qxb4, Qxf3, Rd7, Re7, Rf5, Rf6, Rf8, Rg7, c5, c6, d5, g5, h5, h6");
    checkLegalMoves(board, "Rf6",
        "Bc4, Bd1, Bd3, Bf1, Kf2, Kxg2, Na3, Na7, Nbd4, Nc3, Nd2, Ne1, Ne5, Nfd4, Ng5, Nh2, Nh4, Nxc7, Nxd6, Qa2, Qb3, Qc4, Qc8+, Qd5, Qd7, Qe3, Qe4, Qe5, Qe7, Qe8+, Qf5, Qf7, Qg4, Qg8+, Qh3, Qxd6, Qxf6+");
    checkLegalMoves(board, "Qe8+", "Kg7, Rf8");
    checkLegalMoves(board, "Rf8",
        "Bc4, Bd1, Bd3, Bf1, Kf2, Kxg2, Na3, Na7, Nbd4, Nc3, Nd2, Ne1, Ne5, Nfd4, Ng5, Nh2, Nh4, Nxc7, Nxd6, Qa8, Qb8, Qc6, Qc8, Qd7, Qd8, Qe3, Qe4, Qe5+, Qe6, Qe7, Qf7, Qxf8+, Qxg6");
    checkLegalMoves(board, "Qe7",
        "Kg8, Qc1+, Qc4, Qd2, Qd4+, Qe3+, Qe4, Qe5, Qf5, Qf6, Qf7, Qg3, Qg4, Qg5, Qh2+, Qh4, Qh6, Qxb4, Qxf3, Ra8, Rb8, Rc8, Rd8, Re8, Rf5, Rf6, Rf7, Rg8, c5, c6, d5, g5, h5, h6");
    checkLegalMoves(board, "Rf7",
        "Bc4, Bd1, Bd3, Bf1, Kf2, Kxg2, Na3, Na7, Nbd4, Nc3, Nd2, Ne1, Ne5, Nfd4, Ng5, Nh2, Nh4, Nxc7, Nxd6, Qd7, Qd8+, Qe3, Qe4, Qe5+, Qe6, Qe8+, Qf6+, Qf8+, Qg5, Qh4, Qxc7, Qxd6, Qxf7");
    checkLegalMoves(board, "Qe6",
        "Kg7, Kg8, Qc1+, Qc4, Qd2, Qd4+, Qe3+, Qe4, Qe5, Qf5, Qf6, Qg3, Qg4, Qg5, Qh2+, Qh4, Qh6, Qxb4, Qxf3, Rd7, Re7, Rf5, Rf6, Rf8, Rg7, c5, c6, d5, g5, h5, h6");
    checkLegalMoves(board, "Rf6",
        "Bc4, Bd1, Bd3, Bf1, Kf2, Kxg2, Na3, Na7, Nbd4, Nc3, Nd2, Ne1, Ne5, Nfd4, Ng5, Nh2, Nh4, Nxc7, Nxd6, Qa2, Qb3, Qc4, Qc8+, Qd5, Qd7, Qe3, Qe4, Qe5, Qe7, Qe8+, Qf5, Qf7, Qg4, Qg8+, Qh3, Qxd6, Qxf6+");
    checkLegalMoves(board, "Qb3",
        "Kg7, Qc1+, Qc4, Qd2, Qd4+, Qe3+, Qe4, Qe5, Qf5, Qg3, Qg4, Qg5, Qh2+, Qh4, Qh6, Qxb4, Qxf3, Re6, Rf5, Rf7, Rf8, c5, c6, d5, g5, h5, h6");
    checkLegalMoves(board, "g5",
        "Bc4, Bd1, Bd3, Bf1, Kf2, Kxg2, Na3, Na7, Nbd4, Nc3, Nd2, Ne1, Ne5, Nfd4, Nh2, Nh4, Nxc7, Nxd6, Nxg5, Qa2, Qa3, Qa4, Qb1, Qb2, Qc2, Qc3, Qc4, Qd1, Qd3, Qd5, Qe3, Qe6, Qf7, Qg8+");
    checkLegalMoves(board, "Nxc7",
        "Kg7, Qc1+, Qc4, Qd2, Qd4+, Qe3+, Qe4, Qe5, Qf5, Qg3, Qg4, Qh2+, Qh4, Qxb4, Qxf3, Re6, Rf5, Rf7, Rf8, Rg6, Rh6, d5, g4, h5, h6");
    checkLegalMoves(board, "g4",
        "Ba6, Bb5, Bc4, Bd1, Bd3, Bf1, Kf2, Kxg2, Na6, Na8, Nb5, Nd2, Nd4, Nd5, Ne1, Ne5, Ne6, Ne8, Ng5, Nh2, Nh4, Qa2, Qa3, Qa4, Qb1, Qb2, Qc2, Qc3, Qc4, Qd1, Qd3, Qd5, Qe3, Qe6, Qf7, Qg8+, b5");
    checkLegalMoves(board, "Nd5",
        "Kg7, Kg8, Qc1+, Qc4, Qd2, Qd4+, Qe3+, Qe4, Qe5, Qf5, Qg3, Qg5, Qh2+, Qh6, Qxb4, Qxf3, Re6, Rf5, Rf7, Rf8, Rg6, Rh6, g3, gxf3, h5, h6");
    checkLegalMoves(board, "Qc1+", "Bd1, Bf1, Kf2, Kh2, Kxg2, Ne1, Qd1");
    checkLegalMoves(board, "Qd1",
        "Kg7, Kg8, Qa1, Qa3, Qb1, Qb2, Qc2, Qc3, Qc4, Qc5+, Qc6, Qc7, Qc8, Qd2, Qe3+, Qf4, Qg5, Qh6, Qxd1+, Re6, Rf4, Rf5, Rf7, Rf8, Rg6, Rh6, Rxf3, g3, gxf3, h5, h6");
    checkLegalMoves(board, "Qxd1+", "Bf1, Bxd1, Kf2, Kh2, Kxg2, Ne1");
    checkLegalMoves(board, "Bxd1", "Kg7, Kg8, Re6, Rf4, Rf5, Rf7, Rf8, Rg6, Rh6, Rxf3, g3, gxf3, h5, h6");
    checkLegalMoves(board, "Rf5",
        "Ba4, Bb3, Bc2, Be2, Kf2, Kh2, Kxg2, Nb6, Nc3, Nc7, Nd2, Nd4, Ne1, Ne3, Ne5, Ne7, Nf4, Nf6, Ng5, Nh2, Nh4, b5");
    checkLegalMoves(board, "Ne3",
        "Kg7, Kg8, Ra5, Rb5, Rc5, Rd5, Re5, Rf4, Rf6, Rf7, Rf8, Rg5, Rh5, Rxf3, d5, g3, gxf3, h5, h6");
    checkLegalMoves(board, "Rf4",
        "Ba4, Bb3, Bc2, Be2, Kf2, Kh2, Kxg2, Nc2, Nc4, Nd2, Nd4, Nd5, Ne1, Ne5, Nf1, Nf5, Ng5, Nh2, Nh4, Nxg2, Nxg4, b5");
    checkLegalMoves(board, "Ne1", "Kg7, Kg8, Rc4, Rd4, Re4, Rf1+, Rf2, Rf3, Rf5, Rf6, Rf7, Rf8, Rxb4, d5, g3, h5, h6");
    checkLegalMoves(board, "Rxb4",
        "Ba4, Bb3, Bc2, Be2, Bf3, Bxg4, Kf2, Kh2, Kxg2, N1c2, N1xg2, N3c2, N3xg2, Nc4, Nd3, Nd5, Nf1, Nf3, Nf5, Nxg4");
    checkLegalMoves(board, "Bxg4",
        "Kg7, Kg8, Ra4, Rb1, Rb2, Rb3, Rb5, Rb6, Rb7, Rb8, Rc4, Rd4, Re4, Rf4, Rxg4, d5, h5, h6");
    checkLegalMoves(board, "h5",
        "Bc8, Bd1, Bd7, Be2, Be6, Bf3, Bf5, Bh3, Bxh5, Kf2, Kh2, Kxg2, N1c2, N1xg2, N3c2, N3xg2, Nc4, Nd1, Nd3, Nd5, Nf1, Nf3, Nf5");
    checkLegalMoves(board, "Bf3",
        "Kg7, Kg8, Kh7, Ra4, Rb1, Rb2, Rb3, Rb5, Rb6, Rb7, Rb8, Rc4, Rd4, Re4, Rf4, Rg4, Rh4, d5, h4");
    checkLegalMoves(board, "d5",
        "Bd1, Be2, Be4, Bg4, Bxd5, Bxg2, Bxh5, Kf2, Kh2, Kxg2, N1c2, N1xg2, N3c2, N3xg2, Nc4, Nd1, Nd3, Nf1, Nf5, Ng4, Nxd5");
    checkLegalMoves(board, "N3xg2",
        "Kg7, Kg8, Kh7, Ra4, Rb1, Rb2, Rb3, Rb5, Rb6, Rb7, Rb8, Rc4, Rd4, Re4, Rf4, Rg4, Rh4, d4, h4");
    checkLegalMoves(board, "h4", "Bd1, Be2, Be4, Bg4, Bh5, Bxd5, Kf1, Kf2, Kh1, Kh2, Nc2, Nd3, Ne3, Nf4, Nxh4");
    checkLegalMoves(board, "Nd3",
        "Kg7, Kg8, Kh7, Ra4, Rb1+, Rb2, Rb3, Rb5, Rb6, Rb7, Rb8, Rc4, Rd4, Re4, Rf4, Rg4, d4, h3");
    checkLegalMoves(board, "Ra4",
        "Bd1, Be2, Be4, Bg4, Bh5, Bxd5, Kf1, Kf2, Kh1, Kh2, Nb2, Nb4, Nc1, Nc5, Nde1, Ndf4, Ne3, Ne5, Nf2, Nge1, Ngf4, Nxh4");
    checkLegalMoves(board, "Ngf4",
        "Kg7, Kg8, Kh7, Ra1+, Ra2, Ra3, Ra5, Ra6, Ra7, Ra8, Rb4, Rc4, Rd4, Re4, Rxf4, d4, h3");
    checkLegalMoves(board, "Kg7",
        "Bd1, Be2, Be4, Bg2, Bg4, Bh1, Bh5, Bxd5, Kf1, Kf2, Kg2, Kh1, Kh2, Nb2, Nb4, Nc1, Nc5, Ne1, Ne2, Ne5, Ne6+, Nf2, Ng2, Ng6, Nh3, Nh5+, Nxd5");
    checkLegalMoves(board, "Kg2",
        "Kf6, Kf7, Kf8, Kg8, Kh6, Kh7, Kh8, Ra1, Ra2+, Ra3, Ra5, Ra6, Ra7, Ra8, Rb4, Rc4, Rd4, Re4, Rxf4, d4, h3+");
    checkLegalMoves(board, "Kf6",
        "Bd1, Be2, Be4, Bg4, Bh5, Bxd5, Kf1, Kf2, Kg1, Kh1, Kh2, Kh3, Nb2, Nb4, Nc1, Nc5, Ne1, Ne2, Ne5, Ne6, Nf2, Ng6, Nh3, Nh5+, Nxd5+");
    checkLegalMoves(board, "Bxd5",
        "Ke7, Kf5, Kg5, Kg7, Ra1, Ra2+, Ra3, Ra5, Ra6, Ra7, Ra8, Rb4, Rc4, Rd4, Re4, Rxf4, h3+");
    checkLegalMoves(board, "Ra5",
        "Ba2, Ba8, Bb3, Bb7, Bc4, Bc6, Be4, Be6, Bf3, Bf7, Bg8, Kf1, Kf2, Kf3, Kg1, Kh1, Kh2, Kh3, Nb2, Nb4, Nc1, Nc5, Ne1, Ne2, Ne5, Ne6, Nf2, Ng6, Nh3, Nh5+");
    checkLegalMoves(board, "Bc6",
        "Ke7, Kf5, Kf7, Kg5, Kg7, Ra1, Ra2+, Ra3, Ra4, Ra6, Ra7, Ra8, Rb5, Rc5, Rd5, Re5, Rf5, Rg5+, Rh5, h3+");
    checkLegalMoves(board, "Ra6",
        "Ba4, Ba8, Bb5, Bb7, Bd5, Bd7, Be4, Be8, Bf3, Kf1, Kf2, Kf3, Kg1, Kh1, Kh2, Kh3, Nb2, Nb4, Nc1, Nc5, Nd5+, Ne1, Ne2, Ne5, Ne6, Nf2, Ng6, Nh3, Nh5+");
    checkLegalMoves(board, "Bb7",
        "Ke7, Kf5, Kf7, Kg5, Kg7, Ra1, Ra2+, Ra3, Ra4, Ra5, Ra7, Ra8, Rb6, Rc6, Rd6, Re6, h3+");
    checkLegalMoves(board, "Ra3",
        "Ba6, Ba8, Bc6, Bc8, Bd5, Be4, Bf3, Kf1, Kf2, Kf3, Kg1, Kh1, Kh2, Kh3, Nb2, Nb4, Nc1, Nc5, Nd5+, Ne1, Ne2, Ne5, Ne6, Nf2, Ng6, Nh3, Nh5+");
    checkLegalMoves(board, "Be4", "Ke7, Kf7, Kg5, Kg7, Ra1, Ra2+, Ra4, Ra5, Ra6, Ra7, Ra8, Rb3, Rc3, Rxd3, h3+");
    checkLegalMoves(board, "Ra4",
        "Ba8, Bb7, Bc6, Bd5, Bf3, Bf5, Bg6, Bh7, Kf1, Kf2, Kf3, Kg1, Kh1, Kh2, Kh3, Nb2, Nb4, Nc1, Nc5, Nd5+, Ne1, Ne2, Ne5, Ne6, Nf2, Ng6, Nh3, Nh5+");
    checkLegalMoves(board, "Bd5",
        "Ke7, Kf5, Kg5, Kg7, Ra1, Ra2+, Ra3, Ra5, Ra6, Ra7, Ra8, Rb4, Rc4, Rd4, Re4, Rxf4, h3+");
    checkLegalMoves(board, "Ra5",
        "Ba2, Ba8, Bb3, Bb7, Bc4, Bc6, Be4, Be6, Bf3, Bf7, Bg8, Kf1, Kf2, Kf3, Kg1, Kh1, Kh2, Kh3, Nb2, Nb4, Nc1, Nc5, Ne1, Ne2, Ne5, Ne6, Nf2, Ng6, Nh3, Nh5+");
    checkLegalMoves(board, "Bc6",
        "Ke7, Kf5, Kf7, Kg5, Kg7, Ra1, Ra2+, Ra3, Ra4, Ra6, Ra7, Ra8, Rb5, Rc5, Rd5, Re5, Rf5, Rg5+, Rh5, h3+");
    checkLegalMoves(board, "Ra6",
        "Ba4, Ba8, Bb5, Bb7, Bd5, Bd7, Be4, Be8, Bf3, Kf1, Kf2, Kf3, Kg1, Kh1, Kh2, Kh3, Nb2, Nb4, Nc1, Nc5, Nd5+, Ne1, Ne2, Ne5, Ne6, Nf2, Ng6, Nh3, Nh5+");
    checkLegalMoves(board, "Bf3",
        "Ke7, Kf5, Kf7, Kg5, Kg7, Ra1, Ra2+, Ra3, Ra4, Ra5, Ra7, Ra8, Rb6, Rc6, Rd6, Re6, h3+");
    checkLegalMoves(board, "Kg5",
        "Ba8, Bb7, Bc6, Bd1, Bd5, Be2, Be4, Bg4, Bh5, Kf1, Kf2, Kg1, Kh1, Kh2, Kh3, Nb2, Nb4, Nc1, Nc5, Nd5, Ne1, Ne2, Ne5, Ne6+, Nf2, Ng6, Nh3+, Nh5");

    checkLegalMoves(board, "Bb7",
        "Kf5, Kf6, Kg4, Kh6, Ra1, Ra2+, Ra3, Ra4, Ra5, Ra7, Ra8, Rb6, Rc6, Rd6, Re6, Rf6, Rg6, Rh6, h3+");

    checkLegalMoves(board, "Ra1",
        "Ba6, Ba8, Bc6, Bc8, Bd5, Be4, Bf3, Kf2, Kf3, Kh2, Kh3, Nb2, Nb4, Nc1, Nc5, Nd5, Ne1, Ne2, Ne5, Ne6+, Nf2, Ng6, Nh3+, Nh5");
    checkLegalMoves(board, "Bc8",
        "Kf6, Kh6, Ra2+, Ra3, Ra4, Ra5, Ra6, Ra7, Ra8, Rb1, Rc1, Rd1, Re1, Rf1, Rg1+, Rh1, h3+");
    checkLegalMoves(board, "Ra4",
        "Ba6, Bb7, Bd7, Be6, Bf5, Bg4, Bh3, Kf1, Kf2, Kf3, Kg1, Kh1, Kh2, Kh3, Nb2, Nb4, Nc1, Nc5, Nd5, Ne1, Ne2, Ne5, Ne6+, Nf2, Ng6, Nh3+, Nh5");
    checkLegalMoves(board, "Kf3", "Kf6, Kh6, Ra1, Ra2, Ra3, Ra5, Ra6, Ra7, Ra8, Rb4, Rc4, Rd4, Re4, Rxf4+, h3");
    checkLegalMoves(board, "Rc4",
        "Ba6, Bb7, Bd7, Be6, Bf5, Bg4, Bh3, Ke2, Ke3, Kf2, Kg2, Nb2, Nb4, Nc1, Nc5, Nd5, Ne1, Ne2, Ne5, Ne6+, Nf2, Ng2, Ng6, Nh3+, Nh5");
    checkLegalMoves(board, "Bd7", "Kf6, Kh6, Ra4, Rb4, Rc1, Rc2, Rc3, Rc5, Rc6, Rc7, Rc8, Rd4, Re4, Rxf4+, h3");
    checkLegalMoves(board, "Kf6",
        "Ba4, Bb5, Bc6, Bc8, Be6, Be8, Bf5, Bg4, Bh3, Ke2, Ke3, Kf2, Kg2, Kg4, Nb2, Nb4, Nc1, Nc5, Nd5+, Ne1, Ne2, Ne5, Ne6, Nf2, Ng2, Ng6, Nh3, Nh5+");
    checkLegalMoves(board, "Kg4", "Ke7, Kf7, Kg7, Ra4, Rb4, Rc1, Rc2, Rc3, Rc5, Rc6, Rc7, Rc8, Rd4, Re4, Rxf4+, h3");
    checkLegalMoves(board, "Rd4",
        "Ba4, Bb5, Bc6, Bc8, Be6, Be8, Bf5, Kf3, Kh3, Kh5, Kxh4, Nb2, Nb4, Nc1, Nc5, Ne1, Ne5, Nf2");
    checkLegalMoves(board, "Bc6", "Ke7, Kf7, Kg7, Ra4, Rb4, Rc4, Rd5, Rd6, Rd7, Rd8, Re4, Rxd3, Rxf4+, h3");
    checkLegalMoves(board, "Rd8",
        "Ba4, Ba8, Bb5, Bb7, Bd5, Bd7, Be4, Be8, Bf3, Bg2, Bh1, Kf3, Kh3, Kh5, Kxh4, Nb2, Nb4, Nc1, Nc5, Nd5+, Ne1, Ne2, Ne5, Ne6, Nf2, Ng2, Ng6, Nh3, Nh5+");
    checkLegalMoves(board, "Kxh4", "Ke7, Kf5, Kf7, Kg7, Ra8, Rb8, Rc8, Rd4, Rd5, Rd6, Rd7, Re8, Rf8, Rg8, Rh8+, Rxd3");
    checkLegalMoves(board, "Rg8",
        "Ba4, Ba8, Bb5, Bb7, Bd5, Bd7, Be4, Be8, Bf3, Bg2, Bh1, Kh3, Kh5, Nb2, Nb4, Nc1, Nc5, Nd5+, Ne1, Ne2, Ne5, Ne6, Nf2, Ng2, Ng6, Nh3, Nh5+");
    checkLegalMoves(board, "Be4",
        "Ke7, Kf7, Kg7, Ra8, Rb8, Rc8, Rd8, Re8, Rf8, Rg1, Rg2, Rg3, Rg4+, Rg5, Rg6, Rg7, Rh8+");
    checkLegalMoves(board, "Rg1",
        "Ba8, Bb7, Bc6, Bd5, Bf3, Bf5, Bg2, Bg6, Bh1, Bh7, Kh3, Kh5, Nb2, Nb4, Nc1, Nc5, Nd5+, Ne1, Ne2, Ne5, Ne6, Nf2, Ng2, Ng6, Nh3, Nh5+");
    checkLegalMoves(board, "Nh5+", "Ke6, Ke7, Kf7");
    checkLegalMoves(board, "Ke6",
        "Ba8, Bb7, Bc6, Bd5+, Bf3, Bf5+, Bg2, Bg6, Bh1, Bh7, Kh3, Nb2, Nb4, Nc1, Nc5+, Ndf4+, Ne1, Ne5, Nf2, Nf6, Ng3, Ng7+, Nhf4+");
    checkLegalMoves(board, "Ng3", "Kd6, Kd7, Ke7, Kf6, Kf7, Ra1, Rb1, Rc1, Rd1, Re1, Rf1, Rg2, Rh1+, Rxg3");
    checkLegalMoves(board, "Kf6",
        "Ba8, Bb7, Bc6, Bd5, Bf3, Bf5, Bg2, Bg6, Bh1, Bh7, Kg4, Kh3, Kh5, Nb2, Nb4, Nc1, Nc5, Ne1, Ne2, Ne5, Nf1, Nf2, Nf4, Nf5, Nh1, Nh5+");
    checkLegalMoves(board, "Kg4", "Ke6, Ke7, Kf7, Kg7, Ra1, Rb1, Rc1, Rd1, Re1, Rf1, Rg2, Rh1, Rxg3+");
    checkLegalMoves(board, "Ra1",
        "Ba8, Bb7, Bc6, Bd5, Bf3, Bf5, Bg2, Bg6, Bh1, Bh7, Kf3, Kf4, Kh3, Kh4, Kh5, Nb2, Nb4, Nc1, Nc5, Ne1, Ne2, Ne5, Nf1, Nf2, Nf4, Nf5, Nh1, Nh5+");
    checkLegalMoves(board, "Bd5",
        "Ke7, Kg6, Kg7, Ra2, Ra3, Ra4+, Ra5, Ra6, Ra7, Ra8, Rb1, Rc1, Rd1, Re1, Rf1, Rg1, Rh1");
    checkLegalMoves(board, "Ra5",
        "Ba2, Ba8, Bb3, Bb7, Bc4, Bc6, Be4, Be6, Bf3, Bf7, Bg2, Bg8, Bh1, Kf3, Kf4, Kh3, Kh4, Kh5, Nb2, Nb4, Nc1, Nc5, Ne1, Ne2, Ne4+, Ne5, Nf1, Nf2, Nf4, Nf5, Nh1, Nh5+");
    checkLegalMoves(board, "Bf3",
        "Ke6, Ke7, Kf7, Kg6, Kg7, Ra1, Ra2, Ra3, Ra4+, Ra6, Ra7, Ra8, Rb5, Rc5, Rd5, Re5, Rf5, Rg5+, Rh5");
    checkLegalMoves(board, "Ra1",
        "Ba8, Bb7, Bc6, Bd1, Bd5, Be2, Be4, Bg2, Bh1, Kf4, Kh3, Kh4, Kh5, Nb2, Nb4, Nc1, Nc5, Ne1, Ne2, Ne4+, Ne5, Nf1, Nf2, Nf4, Nf5, Nh1, Nh5+");
    checkLegalMoves(board, "Kf4",
        "Ke6, Ke7, Kf7, Kg6, Kg7, Ra2, Ra3, Ra4+, Ra5, Ra6, Ra7, Ra8, Rb1, Rc1, Rd1, Re1, Rf1, Rg1, Rh1");
    checkLegalMoves(board, "Ke6",
        "Ba8, Bb7, Bc6, Bd1, Bd5+, Be2, Be4, Bg2, Bg4+, Bh1, Bh5, Ke3, Ke4, Kg4, Kg5, Nb2, Nb4, Nc1, Nc5+, Ne1, Ne2, Ne4, Ne5, Nf1, Nf2, Nf5, Nh1, Nh5");
    checkLegalMoves(board, "Nc5+", "Kd6, Ke7, Kf6, Kf7");
    checkLegalMoves(board, "Kd6",
        "Ba8, Bb7, Bc6, Bd1, Bd5, Be2, Be4, Bg2, Bg4, Bh1, Bh5, Ke3, Ke4, Kf5, Kg4, Kg5, Na4, Na6, Nb3, Nb7+, Nce4+, Nd3, Nd7, Ne2, Ne6, Nf1, Nf5+, Nge4+, Nh1, Nh5");
    checkLegalMoves(board, "Nge4+", "Kc6, Kc7, Kd5, Ke7");
    checkLegalMoves(board, "Ke7",
        "Bd1, Be2, Bg2, Bg4, Bh1, Bh5, Ke3, Ke5, Kf5, Kg3, Kg4, Kg5, Na4, Na6, Nb3, Nb7, Nc3, Nd2, Nd3, Nd6, Nd7, Ne6, Nf2, Nf6, Ng3, Ng5");
    checkLegalMoves(board, "Ke5",
        "Kd8, Ke8, Kf7, Kf8, Ra2, Ra3, Ra4, Ra5, Ra6, Ra7, Ra8, Rb1, Rc1, Rd1, Re1, Rf1, Rg1, Rh1");
    checkLegalMoves(board, "Rf1",
        "Bd1, Be2, Bg2, Bg4, Bh1, Bh5, Kd4, Kd5, Kf4, Kf5, Na4, Na6, Nb3, Nb7, Nc3, Nd2, Nd3, Nd6, Nd7, Ne6, Nf2, Nf6, Ng3, Ng5");
    checkLegalMoves(board, "Bg4",
        "Kd8, Ke8, Kf7, Kf8, Ra1, Rb1, Rc1, Rd1, Re1, Rf2, Rf3, Rf4, Rf5+, Rf6, Rf7, Rf8, Rg1, Rh1");
    checkLegalMoves(board, "Rg1",
        "Bc8, Bd1, Bd7, Be2, Be6, Bf3, Bf5, Bh3, Bh5, Kd4, Kd5, Kf4, Kf5, Na4, Na6, Nb3, Nb7, Nc3, Nd2, Nd3, Nd6, Nd7, Ne6, Nf2, Nf6, Ng3, Ng5");
    checkLegalMoves(board, "Be6",
        "Kd8, Ke8, Kf8, Ra1, Rb1, Rc1, Rd1, Re1, Rf1, Rg2, Rg3, Rg4, Rg5+, Rg6, Rg7, Rg8, Rh1");
    checkLegalMoves(board, "Re1",
        "Ba2, Bb3, Bc4, Bc8, Bd5, Bd7, Bf5, Bf7, Bg4, Bg8, Bh3, Kd4, Kd5, Kf4, Kf5, Na4, Na6, Nb3, Nb7, Nd3, Nd7");
    checkLegalMoves(board, "Bc8", "Kd8, Ke8, Kf7, Kf8, Ra1, Rb1, Rc1, Rd1, Re2, Re3, Rf1, Rg1, Rh1, Rxe4+");
    checkLegalMoves(board, "Rc1",
        "Ba6, Bb7, Bd7, Be6, Bf5, Bg4, Bh3, Kd4, Kd5, Kf4, Kf5, Na4, Na6, Nb3, Nb7, Nc3, Nd2, Nd3, Nd6, Nd7, Ne6, Nf2, Nf6, Ng3, Ng5");
    checkLegalMoves(board, "Kd4", "Kd8, Ke8, Kf7, Kf8, Ra1, Rb1, Rc2, Rc3, Rc4+, Rd1+, Re1, Rf1, Rg1, Rh1, Rxc5");
    checkLegalMoves(board, "Rd1+", "Kc3, Kc4, Ke3, Ke5, Nd2, Nd3");
    checkLegalMoves(board, "Nd3", "Kd8, Ke8, Kf7, Kf8, Ra1, Rb1, Rc1, Rd2, Re1, Rf1, Rg1, Rh1, Rxd3+");
    checkLegalMoves(board, "Kf7",
        "Ba6, Bb7, Bd7, Be6+, Bf5, Bg4, Bh3, Kc3, Kc4, Kc5, Kd5, Ke3, Ke5, Nc3, Nc5, Nd2, Nd6+, Nf2, Nf6, Ng3, Ng5+");
    checkLegalMoves(board, "Ke3", "Ke7, Ke8, Kf8, Kg6, Kg7, Kg8, Ra1, Rb1, Rc1, Rd2, Re1+, Rf1, Rg1, Rh1, Rxd3+");
    checkLegalMoves(board, "Ra1",
        "Ba6, Bb7, Bd7, Be6+, Bf5, Bg4, Bh3, Kd2, Kd4, Ke2, Kf2, Kf3, Kf4, Nb2, Nb4, Nc1, Nc3, Nd2, Nd6+, Ndc5, Ndf2, Ne1, Ne5+, Nec5, Nef2, Nf4, Nf6, Ng3, Ng5+");
    checkLegalMoves(board, "Kf4",
        "Ke7, Ke8, Kf8, Kg6, Kg7, Kg8, Ra2, Ra3, Ra4, Ra5, Ra6, Ra7, Ra8, Rb1, Rc1, Rd1, Re1, Rf1+, Rg1, Rh1");
    checkLegalMoves(board, "Ke7",
        "Ba6, Bb7, Bd7, Be6, Bf5, Bg4, Bh3, Ke3, Ke5, Kf3, Kf5, Kg3, Kg4, Kg5, Nb2, Nb4, Nc1, Nc3, Nd2, Nd6, Ndc5, Ndf2, Ne1, Ne5, Nec5, Nef2, Nf6, Ng3, Ng5");
    checkLegalMoves(board, "Nb4",
        "Kd8, Ke8, Kf7, Kf8, Ra2, Ra3, Ra4, Ra5, Ra6, Ra7, Ra8, Rb1, Rc1, Rd1, Re1, Rf1+, Rg1, Rh1");
    checkLegalMoves(board, "Rc1",
        "Ba6, Bb7, Bd7, Be6, Bf5, Bg4, Bh3, Ke3, Ke5, Kf3, Kf5, Kg3, Kg4, Kg5, Na2, Na6, Nc2, Nc3, Nc5, Nc6+, Nd2, Nd3, Nd5+, Nd6, Nf2, Nf6, Ng3, Ng5");
    checkLegalMoves(board, "Nd5+", "Kd8, Ke8, Kf7, Kf8");
    checkLegalMoves(board, "Kf7",
        "Ba6, Bb7, Bd7, Be6+, Bf5, Bg4, Bh3, Ke3, Ke5, Kf3, Kf5, Kg3, Kg4, Kg5, Nb4, Nb6, Nc5, Nc7, Nd2, Nd6+, Ndc3, Ndf6, Ne3, Ne7, Nec3, Nef6, Nf2, Ng3, Ng5+");
    checkLegalMoves(board, "Bd7",
        "Kf8, Kg6, Kg7, Kg8, Ra1, Rb1, Rc2, Rc3, Rc4, Rc5, Rc6, Rc7, Rc8, Rd1, Re1, Rf1+, Rg1, Rh1");
    checkLegalMoves(board, "Rf1+", "Ke3, Ke5, Kg3, Kg4, Kg5, Nf2");
    checkLegalMoves(board, "Ke5", "Kf8, Kg6, Kg7, Kg8, Ra1, Rb1, Rc1, Rd1, Re1, Rf2, Rf3, Rf4, Rf5+, Rf6, Rg1, Rh1");
    checkLegalMoves(board, "Ra1",
        "Ba4, Bb5, Bc6, Bc8, Be6+, Be8+, Bf5, Bg4, Bh3, Kd4, Kd6, Kf4, Kf5, Nb4, Nb6, Nc5, Nc7, Nd2, Nd6+, Ndc3, Ndf6, Ne3, Ne7, Nec3, Nef6, Nf2, Nf4, Ng3, Ng5+");
    checkLegalMoves(board, "Ng5+", "Kf8, Kg6, Kg7, Kg8");
    checkLegalMoves(board, "Kg6",
        "Ba4, Bb5, Bc6, Bc8, Be6, Be8+, Bf5+, Bg4, Bh3, Kd4, Kd6, Ke4, Ke6, Kf4, Nb4, Nb6, Nc3, Nc7, Ne3, Ne4, Ne6, Ne7+, Nf3, Nf4+, Nf6, Nf7, Nh3, Nh7");
    checkLegalMoves(board, "Nf3",
        "Kf7, Kg7, Kh5, Kh6, Kh7, Ra2, Ra3, Ra4, Ra5, Ra6, Ra7, Ra8, Rb1, Rc1, Rd1, Re1+, Rf1, Rg1, Rh1");
    checkLegalMoves(board, "Kg7",
        "Ba4, Bb5, Bc6, Bc8, Be6, Be8, Bf5, Bg4, Bh3, Kd4, Kd6, Ke4, Ke6, Kf4, Kf5, Nb4, Nb6, Nc3, Nc7, Nd2, Nd4, Ne1, Ne3, Ne7, Nf4, Nf6, Ng1, Ng5, Nh2, Nh4");
    checkLegalMoves(board, "Bg4",
        "Kf7, Kf8, Kg6, Kg8, Kh6, Kh7, Kh8, Ra2, Ra3, Ra4, Ra5, Ra6, Ra7, Ra8, Rb1, Rc1, Rd1, Re1+, Rf1, Rg1, Rh1");
    checkLegalMoves(board, "Kg6",
        "Bc8, Bd7, Be6, Bf5+, Bh3, Bh5+, Kd4, Kd6, Ke4, Ke6, Kf4, Nb4, Nb6, Nc3, Nc7, Nd2, Nd4, Ne1, Ne3, Ne7+, Nf4+, Nf6, Ng1, Ng5, Nh2, Nh4+");
    checkLegalMoves(board, "Nf4+", "Kf7, Kg7, Kh6, Kh7");
    checkLegalMoves(board, "Kg7",
        "Bc8, Bd7, Be6, Bf5, Bh3, Bh5, Kd4, Kd5, Kd6, Ke4, Ke6, Kf5, Nd2, Nd3, Nd4, Nd5, Ne1, Ne2, Ne6+, Ng1, Ng2, Ng5, Ng6, Nh2, Nh3, Nh4, Nh5+");
    checkLegalMoves(board, "Nd4",
        "Kf7, Kf8, Kg8, Kh6, Kh7, Kh8, Ra2, Ra3, Ra4, Ra5+, Ra6, Ra7, Ra8, Rb1, Rc1, Rd1, Re1+, Rf1, Rg1, Rh1");
    checkLegalMoves(board, "Re1+", "Be2, Kd5, Kd6, Kf5, Nde2, Nfe2");
    checkLegalMoves(board, "Kf5",
        "Kf7, Kf8, Kg8, Kh6, Kh7, Kh8, Ra1, Rb1, Rc1, Rd1, Re2, Re3, Re4, Re5+, Re6, Re7, Re8, Rf1, Rg1, Rh1");
    checkLegalMoves(board, "Rc1",
        "Bd1, Be2, Bf3, Bh3, Bh5, Ke4, Ke5, Ke6, Kg5, Nb3, Nb5, Nc2, Nc6, Nd3, Nd5, Nde2, Nde6+, Nf3, Nfe2, Nfe6+, Ng2, Ng6, Nh3, Nh5+");
    checkLegalMoves(board, "Be2",
        "Kf7, Kf8, Kg8, Kh6, Kh7, Kh8, Ra1, Rb1, Rc2, Rc3, Rc4, Rc5+, Rc6, Rc7, Rc8, Rd1, Re1, Rf1, Rg1, Rh1");
    checkLegalMoves(board, "Re1",
        "Ba6, Bb5, Bc4, Bd1, Bd3, Bf1, Bf3, Bg4, Bh5, Ke4, Ke5, Ke6, Kg4, Kg5, Nb3, Nb5, Nc2, Nc6, Nd3, Nd5, Nde6+, Nf3, Nfe6+, Ng2, Ng6, Nh3, Nh5+");
    checkLegalMoves(board, "Bh5",
        "Kf8, Kg8, Kh6, Kh7, Kh8, Ra1, Rb1, Rc1, Rd1, Re2, Re3, Re4, Re5+, Re6, Re7, Re8, Rf1, Rg1, Rh1");
    checkLegalMoves(board, "Ra1",
        "Bd1, Be2, Be8, Bf3, Bf7, Bg4, Bg6, Ke4, Ke5, Ke6, Kg4, Kg5, Nb3, Nb5, Nc2, Nc6, Nd3, Nd5, Nde2, Nde6+, Nf3, Nfe2, Nfe6+, Ng2, Ng6, Nh3");
    checkLegalMoves(board, "Nfe6+", "Kg8, Kh6, Kh7, Kh8");
    checkLegalMoves(board, "Kh6",
        "Bd1, Be2, Be8, Bf3, Bf7, Bg4, Bg6, Ke4, Ke5, Kf4, Kf6, Kg4, Nb3, Nb5, Nc2, Nc5, Nc6, Nc7, Nd8, Ne2, Nf3, Nf4, Nf8, Ng5, Ng7");
    checkLegalMoves(board, "Be8", "Kh7, Ra2, Ra3, Ra4, Ra5+, Ra6, Ra7, Ra8, Rb1, Rc1, Rd1, Re1, Rf1+, Rg1, Rh1");
    checkLegalMoves(board, "Ra8",
        "Ba4, Bb5, Bc6, Bd7, Bf7, Bg6, Bh5, Ke4, Ke5, Kf4, Kf6, Kg4, Nb3, Nb5, Nc2, Nc5, Nc6, Nc7, Nd8, Ne2, Nf3, Nf4, Nf8, Ng5, Ng7");
    checkLegalMoves(board, "Bc6", "Kh5, Kh7, Ra1, Ra2, Ra3, Ra4, Ra5+, Ra6, Ra7, Rb8, Rc8, Rd8, Re8, Rf8+, Rg8, Rh8");
    checkLegalMoves(board, "Ra1",
        "Ba4, Ba8, Bb5, Bb7, Bd5, Bd7, Be4, Be8, Bf3, Bg2, Bh1, Ke4, Ke5, Kf4, Kf6, Kg4, Nb3, Nb5, Nc2, Nc5, Nc7, Nd8, Ne2, Nf3, Nf4, Nf8, Ng5, Ng7");
    checkLegalMoves(board, "Kf6", "Kh5, Kh7, Ra2, Ra3, Ra4, Ra5, Ra6, Ra7, Ra8, Rb1, Rc1, Rd1, Re1, Rf1+, Rg1, Rh1");
    checkLegalMoves(board, "Kh7",
        "Ba4, Ba8, Bb5, Bb7, Bd5, Bd7, Be4+, Be8, Bf3, Bg2, Bh1, Ke5, Ke7, Kf5, Kf7, Kg5, Nb3, Nb5, Nc2, Nc5, Nc7, Nd8, Ne2, Nf3, Nf4, Nf5, Nf8+, Ng5+, Ng7");
    checkLegalMoves(board, "Ng5+", "Kg8, Kh6, Kh8");
    checkLegalMoves(board, "Kh8",
        "Ba4, Ba8, Bb5, Bb7, Bd5, Bd7, Be4, Be8, Bf3, Bg2, Bh1, Ke5, Ke6, Ke7, Kf5, Kf7, Kg6, Nb3, Nb5, Nc2, Nde6, Ndf3, Ne2, Ne4, Nf5, Nf7+, Nge6, Ngf3, Nh3, Nh7");
    checkLegalMoves(board, "Nde6", "Kg8, Ra2, Ra3, Ra4, Ra5, Ra6, Ra7, Ra8, Rb1, Rc1, Rd1, Re1, Rf1+, Rg1, Rh1");
    checkLegalMoves(board, "Ra6",
        "Ba4, Ba8, Bb5, Bb7, Bd5, Bd7, Be4, Be8, Bf3, Bg2, Bh1, Ke5, Ke7, Kf5, Kf7, Kg6, Nc5, Nc7, Nd4, Nd8, Ne4, Nf3, Nf4, Nf7+, Nf8, Ng7, Nh3, Nh7");
    checkLegalMoves(board, "Be8", "Kg8, Ra1, Ra2, Ra3, Ra4, Ra5, Ra7, Ra8, Rb6, Rc6, Rd6, Rxe6+");
    checkLegalMoves(board, "Ra8",
        "Ba4, Bb5, Bc6, Bd7, Bf7, Bg6, Bh5, Ke5, Ke7, Kf5, Kf7, Kg6, Nc5, Nc7, Nd4, Nd8, Ne4, Nf3, Nf4, Nf7+, Nf8, Ng7, Nh3, Nh7");
    checkLegalMoves(board, "Bh5", "Kg8, Ra1, Ra2, Ra3, Ra4, Ra5, Ra6, Ra7, Rb8, Rc8, Rd8, Re8, Rf8+, Rg8");
    checkLegalMoves(board, "Ra1",
        "Bd1, Be2, Be8, Bf3, Bf7, Bg4, Bg6, Ke5, Ke7, Kf5, Kf7, Kg6, Nc5, Nc7, Nd4, Nd8, Ne4, Nf3, Nf4, Nf7+, Nf8, Ng7, Nh3, Nh7");
    checkLegalMoves(board, "Bg6", "Kg8, Ra2, Ra3, Ra4, Ra5, Ra6, Ra7, Ra8, Rb1, Rc1, Rd1, Re1, Rf1+, Rg1, Rh1");
    checkLegalMoves(board, "Rf1+", "Bf5, Ke5, Ke7, Nf3, Nf4");
    checkLegalMoves(board, "Ke7", "Kg8, Ra1, Rb1, Rc1, Rd1, Re1, Rf2, Rf3, Rf4, Rf5, Rf6, Rf7+, Rf8, Rg1, Rh1");
    checkLegalMoves(board, "Ra1",
        "Bb1, Bc2, Bd3, Be4, Be8, Bf5, Bf7, Bh5, Bh7, Kd6, Kd7, Kd8, Ke8, Kf6, Kf7, Kf8, Nc5, Nc7, Nd4, Nd8, Ne4, Nf3, Nf4, Nf7+, Nf8, Ng7, Nh3, Nh7");
    checkLegalMoves(board, "Nf7+", "Kg8");
    checkLegalMoves(board, "Kg8",
        "Bb1, Bc2, Bd3, Be4, Bf5, Bh5, Bh7+, Kd6, Kd7, Kd8, Ke8, Kf6, Nc5, Nc7, Nd4, Nd6, Ne5, Ned8, Neg5, Nf4, Nf8, Nfd8, Nfg5, Ng7, Nh6+, Nh8");
    checkLegalMoves(board, "Nh6+", "Kh8");
    checkLegalMoves(board, "Kh8",
        "Bb1, Bc2, Bd3, Be4, Be8, Bf5, Bf7, Bh5, Bh7, Kd6, Kd7, Kd8, Ke8, Kf6, Kf7, Kf8, Nc5, Nc7, Nd4, Nd8, Nf4, Nf5, Nf7+, Nf8, Ng4, Ng5, Ng7, Ng8");
    checkLegalMoves(board, "Nf5", "Kg8, Ra2, Ra3, Ra4, Ra5, Ra6, Ra7+, Ra8, Rb1, Rc1, Rd1, Re1, Rf1, Rg1, Rh1");
    checkLegalMoves(board, "Ra7+", "Kd6, Kd8, Ke8, Kf6, Kf8, Nc7");
    checkLegalMoves(board, "Kf6", "Kg8, Ra1, Ra2, Ra3, Ra4, Ra5, Ra6, Ra8, Rb7, Rc7, Rd7, Re7, Rf7+, Rg7, Rh7");
    checkLegalMoves(board, "Ra1",
        "Be8, Bf7, Bh5, Bh7, Ke5, Ke7, Kf7, Kg5, Nc5, Nc7, Nd6, Nd8, Ne3, Ne7, Ned4, Neg7, Nf4, Nf8, Nfd4, Nfg7, Ng3, Ng5, Nh4, Nh6");
    checkLegalMoves(board, "Ne3", "Kg8, Ra2, Ra3, Ra4, Ra5, Ra6, Ra7, Ra8, Rb1, Rc1, Rd1, Re1, Rf1+, Rg1, Rh1");
    checkLegalMoves(board, "Re1",
        "Bb1, Bc2, Bd3, Be4, Be8, Bf5, Bf7, Bh5, Bh7, Ke5, Ke7, Kf5, Kf7, Kg5, Nc2, Nc4, Nc5, Nc7, Nd1, Nd4, Nd5, Nd8, Nf1, Nf4, Nf5, Nf8, Ng2, Ng4, Ng5, Ng7");
    checkLegalMoves(board, "Nd5", "Kg8, Ra1, Rb1, Rc1, Rd1, Re2, Re3, Re4, Re5, Rf1+, Rg1, Rh1, Rxe6+");
    checkLegalMoves(board, "Rg1",
        "Bb1, Bc2, Bd3, Be4, Be8, Bf5, Bf7, Bh5, Bh7, Ke5, Ke7, Kf5, Kf7, Nb4, Nb6, Nc3, Nc5, Nd4, Nd8, Ndc7, Ndf4, Ne3, Ne7, Nec7, Nef4, Nf8, Ng5, Ng7");
    checkLegalMoves(board, "Bf5", "Kg8, Ra1, Rb1, Rc1, Rd1, Re1, Rf1, Rg2, Rg3, Rg4, Rg5, Rg6+, Rg7, Rg8, Rh1");
    checkLegalMoves(board, "Rf1",
        "Ke5, Ke7, Kf7, Kg5, Kg6, Nb4, Nb6, Nc3, Nc5, Nd4, Nd8, Ndc7, Ndf4, Ne3, Ne7, Nec7, Nef4, Nf8, Ng5, Ng7");
    checkLegalMoves(board, "Ndf4", "Kg8, Ra1, Rb1, Rc1, Rd1, Re1, Rf2, Rf3, Rg1, Rh1, Rxf4");
    checkLegalMoves(board, "Ra1",
        "Bb1, Bc2, Bd3, Be4, Bg4, Bg6, Bh3, Bh7, Ke5, Ke7, Kf7, Kg5, Kg6, Nc5, Nc7, Nd3, Nd4, Nd5, Nd8, Ne2, Nf8, Ng2, Ng5, Ng6+, Ng7, Nh3, Nh5");
    checkLegalMoves(board, "Ng6+", "Kg8, Kh7");
    checkLegalMoves(board, "Kg8",
        "Bb1, Bc2, Bd3, Be4, Bg4, Bh3, Ke5, Ke7, Kg5, Nc5, Nc7, Nd4, Nd8, Ne5, Ne7+, Nef4, Nef8, Ng5, Ng7, Ngf4, Ngf8, Nh4, Nh8");
    checkLegalMoves(board, "Ne7+", "Kh8");
    checkLegalMoves(board, "Kh8",
        "Bb1, Bc2, Bd3, Be4, Bg4, Bg6, Bh3, Bh7, Ke5, Kf7, Kg5, Kg6, Nc5, Nc6, Nc7, Nc8, Nd4, Nd5, Nd8, Nf4, Nf8, Ng5, Ng6+, Ng7, Ng8");
    checkLegalMoves(board, "Ng5", "Ra2, Ra3, Ra4, Ra5, Ra6+, Ra7, Ra8, Rb1, Rc1, Rd1, Re1, Rf1, Rg1, Rh1");
    checkLegalMoves(board, "Ra6+", "Be6, Ke5, Kf7, Nc6, Ne6");
    checkLegalMoves(board, "Kf7", "Ra1, Ra2, Ra3, Ra4, Ra5, Ra7, Ra8, Rb6, Rc6, Rd6, Re6, Rf6+, Rg6, Rh6");
    checkLegalMoves(board, "Rf6+", "Ke8, Kxf6");

  }

  private static void checkInitial(Board board) {
    assertEquals(parseSanSet(INITIAL_LEGAL_MOVES), new TreeSet<>(board.getLegalMovesSan()));
  }

  private static void checkLegalMoves(Board board, String san, String expected) {
    board.moveStrict(san);
    assertEquals(parseSanSet(expected), new TreeSet<>(board.getLegalMovesSan()));
  }

  // The fixtures are written in SAN-alphabetic order for readability, which is independent of the
  // move-generation order now defined by Board#getLegalMoves. This test verifies legal-move
  // membership only - the order contract is exercised by other tests. Wrap actual in a TreeSet so
  // both sides compare under the same ordering.
  @SuppressWarnings("null")
  private static Set<String> parseSanSet(String commaSeparated) {
    if (commaSeparated.isEmpty()) {
      return new TreeSet<>();
    }
    final Set<String> result = new TreeSet<>();
    Collections.addAll(result, commaSeparated.split(", "));
    return result;
  }

  public static void main(String[] args) {
    generateGame(PgnTest.WIKIPEDIA_FIFTY_MOVE, "2_2_karpov_kasparov_1991.pgn");
  }

  private static void generateGame(PgnTest pgnTest, String pgnName) {
    final PgnGame pgnGame = PgnCacheForStrictPgnParserTestCases.getPgn(pgnTest.getFolderPath(), pgnName);
    final Board board = new Board();
    for (final PgnHalfMove move : pgnGame.halfMoveList()) {
      board.moveStrict(move.san());
      final String san = board.getSan();
      final String legalMoveList = BasicUtility.calculateCommaSeparatedList(new ArrayList<>(board.getLegalMovesSan()));
      final String output = "checkLegalMoves(board, \"" + san + "\", \"" + legalMoveList + "\");";
      System.out.println(output);
    }
  }
}