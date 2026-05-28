package io.github.dlbbld.ashlarchess.test.pgn.parser;

import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.nio.file.Path;

import org.eclipse.jdt.annotation.NonNull;
import org.junit.jupiter.api.Test;

import io.github.dlbbld.ashlarchess.board.Board;
import io.github.dlbbld.ashlarchess.board.CommonTestUtility;
import io.github.dlbbld.ashlarchess.common.Nulls;
import io.github.dlbbld.ashlarchess.fen.constants.FenConstants;
import io.github.dlbbld.ashlarchess.model.PgnHalfMove;
import io.github.dlbbld.ashlarchess.pgn.PgnGame;
import io.github.dlbbld.ashlarchess.test.pgntest.constants.PgnTestConstants;

class TestStrictPgnParserFromCustomPosition {
  private static final Path PGN_TEST_FOLDER_PATH = Nulls
      .pathResolve(PgnTestConstants.STRICT_PGN_PARSER_TEST_ROOT_FOLDER_PATH, "fromCustomPosition");

  @SuppressWarnings("static-method")
  @Test
  void test() {
    checkGame("kosteniuk_alexandra_vs_krush_irina_2016.09.10_from_move_10_white.pgn", "e4", "c5", "Nf3", "e6", "d4",
        "cxd4", "Nxd4", "a6", "Bd3", "Nf6", "O-O", "Qc7", "Qe2", "d6", "a4", "b6", "f4", "Nbd7", "b4", "d5", "e5",
        "Ne4", "Ba3", "a5", "Nb5", "Qc6", "bxa5", "Bc5+", "Bxc5", "Ndxc5", "Na7", "Qc7", "axb6", "Qxb6", "Nxc8", "Rxc8",
        "Bb5+", "Nd7+", "Kh1", "Qa7", "Bd3", "Ndc5", "Qe3", "Rc7", "Na3", "Qa5", "Nb5", "Rc8", "Nd4", "O-O", "Bb5",
        "Ra8", "Rf3", "Qb4", "Bc6", "Ra6", "f5", "Rc8", "Bb5", "Raa8", "c3", "Qb2", "Raf1", "Qd2", "fxe6", "fxe6",
        "Nc6", "Rc7", "Qd4", "h6", "Qb4", "Qg5", "g3", "Qd2", "Nd4", "Qg5", "Nc6", "Qd2", "Nd4", "Qg5", "Bc6", "Rac8",
        "c4", "Qd2", "Qxd2", "Nxd2", "cxd5", "Nxf3", "Rxf3", "exd5", "Bxd5+", "Kh7", "Rf4", "Rd7", "Bc4", "g5", "e6",
        "gxf4", "exd7", "Nxd7", "Bd3+", "Kh8", "Bf5", "Rc1+", "Kg2", "Nc5", "gxf4", "Nxa4", "Kg3", "Nc5", "Ne6", "Nd7",
        "Nd4", "Nf6", "Nf3", "Rc3", "h3", "Kg7", "Bb1", "Rc4", "Ne5", "Rb4", "Bg6", "Nd5", "Bf5", "Nxf4", "Ng4", "Ne2+",
        "Kh4", "Nd4", "Be4", "Ne6", "Bg2", "h5");

    checkGame("krush_irina_vs_kosteniuk_alexandra_2019.02.12_from_move_13_black.pgn", "d4", "Nf6", "c4", "e6", "Nc3",
        "Bb4", "Qc2", "c5", "dxc5", "O-O", "a3", "Bxc5", "Nf3", "b6", "Bf4", "Bb7", "Rd1", "Nc6", "e3", "Re8", "Be2",
        "Rc8", "O-O", "h6", "Bd6", "Bxd6", "Rxd6", "Ne7", "e4", "Ng6", "Rfd1", "Qc7", "e5", "Ng4", "Rxd7", "Qb8",
        "R7d4", "N4xe5", "Ne4", "Rcd8", "Nd6", "Nxf3+", "gxf3", "e5", "R4d2", "Re6", "Nxb7", "Rxd2", "Qxd2", "Qxb7",
        "Qd7", "Qxd7", "Rxd7", "Nf4", "Kf1", "Kf8", "Rxa7", "Rd6", "Ke1", "Ng2+", "Kf1", "Nf4", "Ke1", "Ng2+", "Kf1",
        "Nf4");

    checkGame("krush_irina_vs_kosteniuk_alexandra_2019.05.16_from_move_43_white.pgn", "c4", "e5", "Nc3", "Nf6", "g3",
        "d5", "cxd5", "Nxd5", "Bg2", "Nb6", "Nf3", "Nc6", "d3", "Be7", "a3", "Be6", "O-O", "O-O", "Be3", "a5", "Rc1",
        "Nd5", "Nxd5", "Bxd5", "Nd2", "Bxg2", "Kxg2", "a4", "Qc2", "f5", "Qc4+", "Kh8", "f3", "Bf6", "Qb5", "Qc8",
        "Nc4", "Re8", "Rc2", "Re6", "Rb1", "f4", "Bf2", "Nd4", "Bxd4", "exd4", "gxf4", "c6", "Qh5", "Qc7", "f5", "Re7",
        "f4", "Qxf4", "Rf1", "Qg5+", "Qxg5", "Bxg5", "Nd6", "Be3", "Ne4", "Ra5", "Rc5", "Rxc5", "Nxc5", "Re5", "Nxb7",
        "Bg5", "Nd6", "Rxe2+", "Rf2", "Rxf2+", "Kxf2", "Bf4", "Nc4", "Bxh2", "Nb6", "Kg8", "Nxa4", "Kf7", "Kf3", "h5",
        "Nc5", "Ke7", "Ne6", "Be5", "Ke4", "Kd6", "b4", "h4", "Ng5", "Bf6", "Nh3", "Kc7", "a4", "Be7", "Kxd4", "Bxb4",
        "Ke4", "Kb6", "Ng5", "Be7", "Ne6", "Bf6", "Kf4", "Ka5", "Nc5", "Kb4", "Ne4", "Bd8", "d4", "Kc4", "Kg4", "Kxd4",
        "Nd6", "c5", "a5", "Bxa5", "Kxh4", "c4", "Kg5", "c3", "Nb5+", "Kc4", "Na3+", "Kb3", "Nb5", "Bd8+");

    checkGame("krush_irina_vs_kosteniuk_alexandra_2019.05.18_from_move_37_black.pgn", "d4", "Nf6", "c4", "e6", "Nc3",
        "Bb4", "e3", "b6", "Ne2", "Bb7", "a3", "Be7", "d5", "O-O", "g3", "d6", "Bg2", "e5", "b4", "a5", "Rb1", "Nbd7",
        "O-O", "Re8", "e4", "Bf8", "f4", "exf4", "gxf4", "axb4", "axb4", "b5", "cxb5", "Nb6", "Nd4", "Qd7", "Nc6",
        "Qg4", "Qd3", "g6", "Bb2", "Bg7", "Kh1", "Qh4", "Ne2", "Ra2", "Bd4", "Ng4", "Bg1", "Bxc6", "bxc6", "Rea8",
        "Qg3", "Qxg3", "Nxg3", "Nc4", "Rfd1", "Nce3", "Bxe3", "Nxe3", "Rg1", "Bd4", "Bf3", "Rf2", "Be2", "Ra2", "Rge1",
        "Bc3", "Rec1", "Bd4", "Re1", "Rxf4", "b5", "Bb6", "Ra1", "Rc2", "Rec1", "Rd2", "Ra6", "Ng4", "Bxg4", "Rxg4",
        "Rxb6", "Rh4", "Rb8+", "Kg7", "Nf1", "Rb2", "b6", "g5", "bxc7", "g4", "Rxb2");
  }

  private static void checkGame(String pgn, String... hardCodedCompleteSanList) {
    final PgnGame pgnGame = PgnCacheForStrictPgnParserTestCases.getPgn(PGN_TEST_FOLDER_PATH, pgn);

    assertNotEquals(FenConstants.FEN_INITIAL, pgnGame.startFen());

    final Board boardFromFen = new Board(pgnGame.startFen());

    for (final PgnHalfMove halfMove : pgnGame.halfMoveList()) {
      boardFromFen.moveStrict(halfMove.san());
    }

    final Board boardFromFirstMove = new Board();
    for (final String san : hardCodedCompleteSanList) {
      @SuppressWarnings("null") @NonNull final String nonNullSan = san;
      boardFromFirstMove.moveStrict(nonNullSan);
    }

    CommonTestUtility.checkBoardsAgainstEachOtherExcludeHistory(boardFromFirstMove, boardFromFen);
  }
}
