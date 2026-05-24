package com.dlb.chess.test.fen.roundtrip;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.dlb.chess.board.Board;
import com.dlb.chess.board.CommonTestUtility;
import com.dlb.chess.common.model.MoveSpecification;
import com.dlb.chess.fen.FenParserAdvanced;
import com.dlb.chess.fen.constants.FenConstants;
import com.dlb.chess.fen.model.Fen;

class TestFenRoundtripMoves extends AbstractTestFenRoundtrip {

  @SuppressWarnings("static-method")
  @Test
  void testInitial() {
    final Fen fen = FenParserAdvanced.parseFenAdvanced(FenConstants.FEN_INITIAL_STR);
    final Board boardFromFenInitial = new Board(fen);
    final Board board = new Board();

    CommonTestUtility.checkBoardsAgainstEachOtherExcludeHistory(board, boardFromFenInitial);
  }

  @SuppressWarnings("static-method")
  @Test
  void testFenRoundtripGame1() {
    final Board boardMakeMoves = new Board();

    boardMakeMoves.movesStrict("d4", "d5", "Nc3", "Nf6", "Bf4", "a6", "e3", "b5", "Bd3", "e6", "Nf3", "c5", "O-O",
        "Bb7", "Ne5", "Nbd7", "a3", "Nxe5", "Bxe5", "Bd6", "f4", "O-O", "Qf3", "c4", "Be2", "Ne4", "Nxe4", "dxe4",
        "Qg4", "g6", "h4", "f5", "Qg3", "Bxe5", "fxe5", "Bd5", "c3", "a5", "Qh3", "h5", "Qg3", "Kh7", "Qg5", "Qxg5",
        "hxg5", "Rfb8", "Rfc1", "Ra7", "g3", "Kg7", "Kf2", "Kf7", "Ke1", "Ke7", "Kd2", "Rb6", "Ra2", "Rab7", "Raa1",
        "Rb8", "Rc2", "R6b7", "Rac1", "Kd7", "Ra1", "Rb6", "Rac1", "Rc8", "Ra1", "Rc7", "Bf1", "Kc8", "Be2", "Kb8",
        "Rac1", "Kb7", "Ra1", "Rc8", "Rac1", "Rbc6", "Ra1", "R6c7", "Rac1", "Kb6", "Ra1", "h4", "gxh4", "Rh8", "Rh1",
        "Rch7", "h5", "gxh5", "Rcc1", "h4", "Rcg1", "Rg7", "Ke1", "b4", "axb4", "axb4", "Kf2", "b3", "g6", "Rh6", "Rg5",
        "Rhxg6", "Rxg6", "Rxg6", "Rxh4", "Rg8", "Rh2", "Ra8", "Kg3", "Ra1", "Rg2", "Rb1", "Bh5", "Rf1", "Be2", "Rb1",
        "Bh5", "Rh1", "Be2", "Rc1", "Bh5", "Ka5", "Rf2", "Ka4", "Be8+", "Ka5", "Bh5", "Re1", "Re2", "Rg1+", "Rg2",
        "Rh1", "Rh2", "Rc1", "Rf2", "Rc2", "Bd1", "Rc1", "Rd2", "Kb6", "Kf4", "Kc7", "Kg3", "Kd8", "Kf4", "Ke7", "Kg3",
        "Kf8", "Kf4", "Ke7", "Kg3", "Bc6", "Kf2", "Kf8", "Be2", "Bd5", "Bd1", "Kg7", "Kg3", "Bc6", "Kf4", "Kf8", "Kg3",
        "Kf7", "Kf4", "Ke7", "Kg3", "Bd5", "Kf4", "Ra1", "Kg3", "Kd7", "Kf4", "Kc6", "Kg3", "Kb5", "Bh5", "Kb6", "Bd1",
        "Ra7", "Rh2", "Ra1", "Rd2", "Rc1", "Kf2", "Kc7", "Kg3", "Bc6", "Kf2", "Rb1", "Be2", "Bd5", "Bd1", "Kb6", "Kg3",
        "Bc6", "Kf2", "Bb5", "Kg3", "Kc6", "Bh5", "Kd5", "Rg2", "Ba4", "Kf4", "Rf1+", "Kg5", "Bd7", "Be2", "Rb1", "Bh5",
        "Re1", "Re2", "Rc1", "Kf6", "Rc2", "Ke7", "Kc6", "Kd8", "Rc1", "Rg2", "Rh1", "Be2", "Rh8+", "Ke7", "Rh7+",
        "Kf6", "Rh6+", "Kg7", "Rh3", "Bxc4", "Rxe3", "Bxb3", "f4", "Ba4+", "Kc7", "Bxd7", "Rg3+", "Rxg3", "fxg3", "d5",
        "g2", "dxe6", "g1=Q+", "Kf7", "Qf2+");

    final List<MoveSpecification> moveList = boardMakeMoves.getPerformedMoveSpecificationList();

    checFenRoundtrip(FenConstants.FEN_INITIAL_STR, moveList);
  }

  @SuppressWarnings("static-method")
  @Test
  void testFenRoundtripGame2() {
    final Board boardMakeMoves = new Board();
    boardMakeMoves.movesStrict("e4", "e5", "Nf3", "Nc6", "Bc4", "Bc5", "c3", "d6", "d4", "exd4", "cxd4", "Bxd4", "Nxd4",
        "Nxd4", "O-O", "Nf6", "Re1", "Nxe4", "Nc3", "O-O");

    final List<MoveSpecification> moveList = boardMakeMoves.getPerformedMoveSpecificationList();

    checFenRoundtrip(FenConstants.FEN_INITIAL_STR, moveList);
  }

}
