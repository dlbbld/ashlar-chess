package com.dlb.chess.test.san;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;

import com.dlb.chess.board.Board;
import com.dlb.chess.board.enums.PromotionPieceType;
import com.dlb.chess.common.constants.EnumConstants;
import com.dlb.chess.common.model.MoveSpecification;
import com.dlb.chess.san.StrictSanParser;

class TestPerformMoveForSan implements EnumConstants {

  @SuppressWarnings("static-method")
  @Test
  void testLastChar() {

    var isException = false;

    try {
      final Board board = new Board();

      // we require checks in SAN to be real checks
      // we do not allow fake checks white
      board.moveStrict("e4");
      // we do not allow fake checks black
      board.moveStrict("e5");
      board.moveStrict("Bc4");
      board.moveStrict("Bc5");
      // real check white
      board.moveStrict("Bxf7+");
      board.moveStrict("Ke7");
      board.moveStrict("a3");
      // real check black
      board.moveStrict("Bxf2+");
      board.moveStrict("Ke2");

      // we require checkmate in SAN to be real checkmate
      // we do not allow fake checkmate white
      board.moveStrict("a6");
      board.moveStrict("d3");
      // we do not allow fake checkmate black
      board.moveStrict("Bc5");
      board.moveStrict("Bc4");
      board.moveStrict("Ke8");
      board.moveStrict("Ke1");
      board.moveStrict("a5");
      board.moveStrict("Qf3");
      board.moveStrict("a4");
      // real checkmate white
      board.moveStrict("Qf7#");

      // real checkmate black
      {
        final Board boardCheckmateBlack = new Board();

        boardCheckmateBlack.moveStrict("f3");
        boardCheckmateBlack.moveStrict("e5");
        boardCheckmateBlack.moveStrict("g4");
        // real checkmate black
        boardCheckmateBlack.moveStrict("Qh4#");
      }
    } catch (@SuppressWarnings("unused") final Exception e) {
      isException = true;
    }

    assertFalse(isException);
  }

  @SuppressWarnings("static-method")
  @Test
  void testCalculateMoveSpecificationForSan() {
    var isException = false;

    try {
      final Board board = new Board();

      checkMoveSpecificationTest(board, "e4", new MoveSpecification(E2, E4));
      checkMoveSpecificationTest(board, "d5", new MoveSpecification(D7, D5));
      checkMoveSpecificationTest(board, "exd5", new MoveSpecification(E4, D5));
      checkMoveSpecificationTest(board, "e5", new MoveSpecification(E7, E5));
      checkMoveSpecificationTest(board, "dxe6", new MoveSpecification(D5, E6));
      checkMoveSpecificationTest(board, "h6", new MoveSpecification(H7, H6));
      checkMoveSpecificationTest(board, "exf7+", new MoveSpecification(E6, F7));
      checkMoveSpecificationTest(board, "Ke7", new MoveSpecification(E8, E7));
      checkMoveSpecificationTest(board, "fxg8=Q", new MoveSpecification(F7, G8, PromotionPieceType.QUEEN));
      checkMoveSpecificationTest(board, "h5", new MoveSpecification(H6, H5));
      checkMoveSpecificationTest(board, "Qxh8", new MoveSpecification(G8, H8));
      checkMoveSpecificationTest(board, "h4", new MoveSpecification(H5, H4));
      checkMoveSpecificationTest(board, "g4", new MoveSpecification(G2, G4));
      checkMoveSpecificationTest(board, "hxg3", new MoveSpecification(H4, G3));
      checkMoveSpecificationTest(board, "a4", new MoveSpecification(A2, A4));
      checkMoveSpecificationTest(board, "gxh2", new MoveSpecification(G3, H2));
      checkMoveSpecificationTest(board, "a5", new MoveSpecification(A4, A5));
      checkMoveSpecificationTest(board, "hxg1=Q", new MoveSpecification(H2, G1, PromotionPieceType.QUEEN));
      checkMoveSpecificationTest(board, "a6", new MoveSpecification(A5, A6));
      checkMoveSpecificationTest(board, "Qxh1", new MoveSpecification(G1, H1));
    } catch (@SuppressWarnings("unused") final Exception e) {
      isException = true;
    }

    assertFalse(isException);
  }

  @SuppressWarnings("static-method")
  @Test
  void testFileRankSpecificationValid() {

    var isException = false;

    try {

      // rook
      {
        final Board board = new Board();

        board.movesStrict("a4", "a5", "h4", "h5", "Ra3", "Ra6");
        // valid file specification white
        board.movesStrict("Rhh3");
        // valid file specification black
        board.movesStrict("Rhh6");
        // valid file specification white
        board.movesStrict("Rhe3");
        // valid file specification black
        board.movesStrict("Rhe6");
        // valid file specification white
        board.movesStrict("Rad3");
        // valid file specification black
        board.movesStrict("Rad6");
        board.movesStrict("Rd4");
        board.movesStrict("Rd5");
        // valid file specification white
        board.movesStrict("Rde4");
        // valid file specification black
        board.movesStrict("Red6");
        board.movesStrict("Re5");
        board.movesStrict("Rd4");
        // valid rank specification white
        board.movesStrict("R3e4");
        board.movesStrict("Rd3");
        board.movesStrict("Re6");
        // valid rank specification white
        board.movesStrict("R6d5");
      }

      // knight
      // "Nc3", "Nc6", "Nh3", "Nh6", "Nf4", "Nf5", "Nfd5", "Nfd4", "Nb4", "Nb5", "Nd3", "Nd6", "Nc5", "Nc4", "N5e4",
      // "N4e5", "h4", "a5", "h5", "a4", "h6", "a3", "hxg7", "axb2", "gxf8=N", "bxc1=N", "Ng5", "Ng4", "Nfe6", "Nb3",
      // "Nc5", "Nba5", "Nc5e4", "Nc4", "Nc5", "Nc4e5"
      {
        final Board board = new Board();

        board.movesStrict("Nc3", "Nc6", "Nh3", "Nh6", "Nf4", "Nf5");
        // valid file specification white
        board.movesStrict("Nfd5");
        // valid file specification black
        board.movesStrict("Nfd4");
        board.movesStrict("Nb4", "Nb5", "Nd3", "Nd6", "Nc5", "Nc4");
        // valid rank specification white
        board.movesStrict("N5e4");
        // valid rank specification black
        board.movesStrict("N4e5");
        board.movesStrict("h4", "a5", "h5", "a4", "h6", "a3", "hxg7", "axb2", "gxf8=N", "bxc1=N", "Ng5", "Ng4", "Nfe6",
            "Nb3");
        board.movesStrict("Nc5", "Nba5");
        // valid square specification white
        board.movesStrict("Nc5e4");
        board.movesStrict("Nc4");
        board.movesStrict("Nc5");
        // valid square specification black
        board.movesStrict("Nc4e5");
      }

      // bishop
      // "d4", "e5", "d5", "e4", "f3", "c6", "dxc6", "exf3", "cxb7", "fxg2", "bxa8=B", "gxh1=B", "e3", "d6", "Bd5",
      // "Bf3",
      // "Bb5+", "Ke7", "Bd2", "Bh3",
      // "Bbc6", "Bfg4", "Bc4", "Bg2", "B4d5", "B2h3", "e4", "a5", "e5", "a4", "e6", "a3",
      // "exf7", "axb2", "fxg8=B", "Qc8", "Na3", "b1=B", "Bc4", "Bxc2", "Bgd5", "Be4", "Be6", "Bf1", "Bc6d5", "Bfe2",
      // "Nb1", "Be4f3
      {
        final Board board = new Board();

        board.movesStrict("d4", "e5", "d5", "e4", "f3", "c6", "dxc6", "exf3", "cxb7", "fxg2", "bxa8=B", "gxh1=B", "e3",
            "d6", "Bd5", "Bf3");
        board.movesStrict("Bb5+", "Ke7", "Bd2", "Bh3");
        // valid file specification white
        board.movesStrict("Bbc6");
        // valid file specification black
        board.movesStrict("Bfg4");
        board.movesStrict("Bc4", "Bg2");
        // valid rank specification white
        board.movesStrict("B4d5");
        // valid rank specification black
        board.movesStrict("B2h3");
        board.movesStrict("e4", "a5", "e5", "a4", "e6", "a3");
        board.movesStrict("exf7", "axb2", "fxg8=B", "Qc8", "Na3", "b1=B", "Bc4", "Bxc2", "Bgd5", "Be4", "Be6", "Bf1");
        // valid square specification white
        board.movesStrict("Bc6d5");
        board.movesStrict("Bfe2");
        board.movesStrict("Nb1");
        // valid square specification black
        board.movesStrict("Be4f3");

      }

      // queen
      // "c4", "f5", "c5", "f4", "c6", "f3", "cxb7", "fxg2", "bxa8=Q", "gxh1=Q", "Qb3", "e6", "Qad5", "Qg5", "Qdc4",
      // "Qe4", "Qcc3", "Qgf4", "Qcb4", "Qfe5", "Q3c4", "Q5f4", "d4", "g5", "d5", "g4", "d6", "g3", "dxc7", "gxh2",
      // "cxb8=Q", "hxg1=Q", "Q8b5", "Qgg5", "Qb4c5", "Qef5", "Qc5b4", "Qf5g4"
      {
        final Board board = new Board();

        board.movesStrict("c4", "f5", "c5", "f4", "c6", "f3", "cxb7", "fxg2", "bxa8=Q", "gxh1=Q", "Qb3", "e6");
        // valid file specification white
        board.movesStrict("Qad5");
        board.movesStrict("Qg5");
        board.movesStrict("Qdc4");
        board.movesStrict("Qe4");
        // valid file specification black
        board.movesStrict("Qcc3");
        board.movesStrict("Qgf4", "Qcb4", "Qfe5");
        // valid rank specification white
        board.movesStrict("Q3c4");
        // valid rank specification black
        board.movesStrict("Q5f4");
        board.movesStrict("d4", "g5", "d5", "g4", "d6", "g3", "dxc7", "gxh2");
        board.movesStrict("cxb8=Q", "hxg1=Q", "Q8b5", "Qgg5");
        // valid square specification white
        board.movesStrict("Qb4c5");
        board.movesStrict("Qef5");
        board.movesStrict("Qc5b4");
        // valid square specification black
        board.movesStrict("Qf5g4");

      }
    } catch (@SuppressWarnings("unused") final Exception e) {
      isException = true;
    }

    assertFalse(isException);
  }

  private static void checkMoveSpecificationTest(Board board, String san, MoveSpecification expectedMove) {
    final MoveSpecification parsedMoveAsIs = StrictSanParser.parseText(san, board).moveSpecification();
    assertEquals(expectedMove, parsedMoveAsIs);

    board.move(expectedMove);

  }

}