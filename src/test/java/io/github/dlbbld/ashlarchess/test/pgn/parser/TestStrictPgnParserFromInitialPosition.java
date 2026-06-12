// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.test.pgn.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Path;

import org.eclipse.jdt.annotation.NonNull;
import org.junit.jupiter.api.Test;

import io.github.dlbbld.ashlarchess.board.Board;
import io.github.dlbbld.ashlarchess.common.Nulls;
import io.github.dlbbld.ashlarchess.fen.constants.FenConstants;
import io.github.dlbbld.ashlarchess.model.PgnMove;
import io.github.dlbbld.ashlarchess.pgn.PgnGame;
import io.github.dlbbld.ashlarchess.test.pgntest.constants.PgnTestConstants;

class TestStrictPgnParserFromInitialPosition {
  private static final Path PGN_TEST_FOLDER_PATH = Nulls
      .pathResolve(PgnTestConstants.STRICT_PGN_PARSER_TEST_ROOT_FOLDER_PATH, "fromInitialPosition");

  @SuppressWarnings("static-method")
  @Test
  void test() {

    checkGame("anand_viswanathan_vs_carlsen_magnus_2019.06.03.pgn", "e4", "g6", "d4", "Nf6", "Nc3", "d5", "e5", "Ne4",
        "Nce2", "f6", "f3", "Ng5", "Bxg5", "fxg5", "Qd2", "c5", "Qxg5", "Nc6", "c3", "Qb6", "Qd2", "cxd4", "cxd4",
        "Bf5", "g4", "Bd7", "Rd1", "O-O-O", "Nc3", "e6", "Nge2", "Kb8", "f4", "Be7", "h4", "Na5", "b3", "Bb4", "Kf2",
        "Rhf8", "Rh3", "Bb5", "Qe3", "Bxe2", "Nxe2", "Rc8", "Rc1", "Rxc1", "Qxc1", "Nc6", "Qe3", "Nxd4", "Nxd4", "Bc5",
        "Kg2", "Bxd4", "Qd2", "a5", "Rf3", "Bc5", "Bd3", "Qd8", "g5", "Bb6", "Kg3", "Qe7", "Rf1", "Qc5", "Rc1", "Qd4",
        "Rd1", "Bc7", "Re1", "a4", "Qe3", "Qb4", "bxa4", "Bb6", "Qc1", "Qd4", "Qd2", "Ba5", "Qxa5", "Qxd3+", "Kg4",
        "Qf5+");

    checkGame("anand_viswanathan_vs_carlsen_magnus_2019.08.18.pgn", "e4", "c5", "Nf3", "Nc6", "Bb5", "g6", "Bxc6",
        "bxc6", "d4", "Bg7", "dxc5", "Qa5+", "Nbd2", "Qxc5", "O-O", "d6", "Re1", "f6", "a3", "Nh6", "b4", "Qh5", "c4",
        "O-O", "Qa4", "Bd7", "Nf1", "Nf7", "Ng3", "Qg4", "c5", "e5", "cxd6", "c5", "Qb3", "cxb4", "axb4", "Qe6", "Qxe6",
        "Bxe6", "Be3", "a6", "Rec1", "Rfd8", "Nd2", "Bf8", "Nc4", "Rac8", "Bc5", "Bxc4", "Rxc4", "Nxd6", "Rcc1", "Rc6",
        "f3", "Nb5", "Bxf8", "Rxc1+", "Rxc1", "Kxf8", "Nf1", "Nd4", "Rc4", "Ke7", "Ne3", "Kd6", "Rc5", "Nc6", "Rd5+",
        "Ke7", "b5", "axb5", "Rxb5", "Rb8", "Rxb8", "Nxb8", "Kf2", "Ke6", "g3", "Nd7", "Nc4", "Nc5", "Ke3", "Nd7",
        "Kf2", "Nc5", "Ke3", "Nd7", "Kf2");

    checkGame("carlsen_magnus_vs_anand_viswanathan_2019.06.04.pgn", "d4", "Nf6", "c4", "e6", "Nf3", "d5", "Nc3", "dxc4",
        "e4", "Bb4", "Bg5", "c5", "Bxc4", "cxd4", "Nxd4", "Bxc3+", "bxc3", "Qa5", "Nb5", "Nxe4", "Qd4", "O-O", "Qxe4",
        "a6", "O-O", "axb5", "Bd3", "f5", "Qe2", "Nc6", "Bd2", "Qc7", "Bxb5", "f4", "f3", "e5", "Rfe1", "Bd7", "a4",
        "Kh8", "Rad1", "Rae8", "Bc1", "e4", "fxe4", "Ne5", "Bxd7", "Nxd7", "Rf1", "Nc5", "Ba3", "Rxe4", "Qb5", "Re5",
        "Rde1", "Rc8", "Rxe5", "Qxe5", "Bxc5", "Qxc5+", "Qxc5", "Rxc5", "Rxf4", "h6", "Rf3", "Kg8", "Rd3", "Ra5", "Rd7",
        "Rxa4", "Rxb7", "Ra2", "h4", "Kh7", "Kh2", "Rd2", "Rc7", "Kg6", "Kg3", "Kh5", "Rc5+", "g5", "Kh3", "Rd3+", "g3",
        "Re3", "Kg2", "Rd3", "Rc4", "Re3", "hxg5", "hxg5", "Kh3", "Rd3", "Rc8", "g4+", "Kg2", "Rd2+", "Kf1", "Rc2",
        "c4", "Kg5", "c5", "Kf6", "c6", "Ke7", "c7", "Kd6", "Rd8+", "Kxc7", "Rd4", "Kc6", "Kg1", "Kc5", "Rxg4", "Kd5");

    checkGame("carlsen_magnus_vs_anand_viswanathan_2019.06.27.pgn", "d4", "Nf6", "c4", "e6", "Nf3", "d5", "Nc3", "dxc4",
        "e4", "Bb4", "Bg5", "c5", "Bxc4", "cxd4", "Nxd4", "Bxc3+", "bxc3", "Qa5", "Bb5+", "Bd7", "Bxf6", "gxf6",
        "Bxd7+", "Nxd7", "O-O", "Qxc3", "Qa4", "O-O", "Nxe6", "Nb6", "Qd4", "Rfc8", "Qxc3", "Rxc3", "Nf4", "Rc5",
        "Rfd1", "f5", "exf5", "Rac8", "f6", "Rf5", "g3", "Rxf6", "Rd3", "Rfc6", "Ra3", "Rc1+", "Rxc1", "Rxc1+", "Kg2",
        "Nc8", "Rd3", "Rc7", "Rd8+", "Kg7", "h4", "Ne7", "Nh5+", "Kh6", "Nf6", "Kg6", "Ne8", "Rc6", "Rd7", "Re6", "h5+",
        "Kh6", "Nd6", "f5", "Nxb7", "Ng8", "Nd6", "Nf6", "Nxf5+", "Kg5", "Nd4", "Nxd7", "Nxe6+", "Kxh5", "Kf3", "Kg6",
        "Kf4", "Kf6", "Ng5", "h6", "Ne4+", "Ke6", "Kg4", "Ke5", "f3", "Kd4", "Kf5", "Ke3", "f4", "h5", "Ng5", "Nc5",
        "Kg6", "Kf2", "f5", "Kxg3", "Ne6", "Nd7", "Kxh5", "Kf3", "Kg5", "Ke4", "Nc7", "a5", "Nb5", "Kd3", "Kg6", "a4",
        "Kf7", "Ne5+", "Kf6", "Nf3", "Ke7", "Kc2", "f6", "Ng5", "Nd4+", "Kb2", "Ne6", "Nh7", "f7", "Kxa2", "Ng5", "a3",
        "Nxh7", "Kb1", "f8=Q", "a2", "Qf5+", "Kb2", "Qe5+", "Kb1", "Qe4+", "Kb2", "Qe2+", "Kb1", "Nf6", "a1=Q", "Nd5",
        "Qg7+", "Kd6", "Qf8+", "Ke5", "Qe8+", "Kd4", "Qxe2", "Nc3+", "Ka1", "Nxe2");
  }

  private static void checkGame(String pgn, String... sanList) {
    final PgnGame pgnGame = PgnCacheForStrictPgnParserTestCases.getPgn(PGN_TEST_FOLDER_PATH, pgn);

    assertEquals(FenConstants.FEN_INITIAL, pgnGame.startFen());

    final Board actual = new Board();

    for (final PgnMove move : pgnGame.moveList()) {
      actual.moveStrict(move.san());
    }

    final Board expected = new Board();
    for (final String san : sanList) {
      @SuppressWarnings("null") @NonNull final String nonNullSan = san;
      expected.moveStrict(nonNullSan);
    }

    assertEquals(expected, actual);
  }

}
