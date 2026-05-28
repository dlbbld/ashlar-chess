package io.github.dlbbld.ashlarchess.test.san;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import io.github.dlbbld.ashlarchess.board.Board;
import io.github.dlbbld.ashlarchess.san.SanValidationException;
import io.github.dlbbld.ashlarchess.san.SanValidationProblem;
import io.github.dlbbld.ashlarchess.san.StrictSanParser;

class TestSanValidateAgainstLegalMoves {

  @SuppressWarnings("static-method")
  @Test
  void testKingNonCastling() {
    final Board board = new Board();

    // black
    board.movesStrict("e4");
    checkException(board, "Ke2", SanValidationProblem.NOT_REACHABLE_KING_NON_CASTLING);
    checkException(board, "Kxa1", SanValidationProblem.NOT_REACHABLE_KING_NON_CASTLING);

  }

  @SuppressWarnings("static-method")
  @Test
  void testPawnNonPromotion() {
    final Board board = new Board("8/8/8/2Rpk3/2R5/4K3/8/8 b - - 0 100");

    checkException(board, "d4", SanValidationProblem.KING_EXPOSED_TO_CHECK_PAWN);
    checkException(board, "dxc4", SanValidationProblem.KING_EXPOSED_TO_CHECK_PAWN);

  }

  @SuppressWarnings("static-method")
  @Test
  void testPawnPromotion() {
    // black
    {
      final Board board = new Board("8/8/8/4K3/8/8/2R2p1k/6R1 b - - 0 100");
      checkException(board, "f1=N+", SanValidationProblem.KING_EXPOSED_TO_CHECK_PAWN);
    }
    {
      final Board board = new Board("8/8/8/4K3/8/8/2R2p1k/6R1 b - - 0 100");
      checkException(board, "fxg1=Q+", SanValidationProblem.KING_EXPOSED_TO_CHECK_PAWN);
    }

  }

  @SuppressWarnings("static-method")
  @Test
  void testPieceFile1NoLegalMove() {
    final Board board = new Board();

    // white
    // rook
    checkException(board, "Raa3", SanValidationProblem.NOT_REACHABLE_RNBQ_FILE_SINGLE);

    // knight
    checkException(board, "Nbc4", SanValidationProblem.NOT_REACHABLE_RNBQ_FILE_SINGLE);

    // bishop
    checkException(board, "Bcb3", SanValidationProblem.NOT_REACHABLE_RNBQ_FILE_SINGLE);

    // queen
    checkException(board, "Qdd3", SanValidationProblem.NOT_REACHABLE_RNBQ_FILE_SINGLE);

    // black
    board.movesStrict("e4");
    // rook
    checkException(board, "Raa6", SanValidationProblem.NOT_REACHABLE_RNBQ_FILE_SINGLE);

    // knight
    checkException(board, "Nbc5", SanValidationProblem.NOT_REACHABLE_RNBQ_FILE_SINGLE);

    // bishop
    checkException(board, "Bcb6", SanValidationProblem.NOT_REACHABLE_RNBQ_FILE_SINGLE);

    // queen
    checkException(board, "Qdd4", SanValidationProblem.NOT_REACHABLE_RNBQ_FILE_SINGLE);

  }

  @SuppressWarnings("static-method")
  @Test
  void testPieceFile2OnlyOneLegalMove() {
    final Board board = new Board();

    // white
    // rook
    board.movesStrict("a4");
    board.movesStrict("a5");
    checkException(board, "Raa2", SanValidationProblem.OVERSPECIFIED_RNBQ_FILE_ONLY_ONE_LEGAL_MOVE);

    // knight
    checkException(board, "Nbc3", SanValidationProblem.OVERSPECIFIED_RNBQ_FILE_ONLY_ONE_LEGAL_MOVE);

    // bishop
    board.movesStrict("b3");
    board.movesStrict("b6");
    checkException(board, "Bcb2", SanValidationProblem.OVERSPECIFIED_RNBQ_FILE_ONLY_ONE_LEGAL_MOVE);

    // queen
    board.movesStrict("d4");
    board.movesStrict("d5");
    checkException(board, "Qdd2", SanValidationProblem.OVERSPECIFIED_RNBQ_FILE_ONLY_ONE_LEGAL_MOVE);

    // black
    board.movesStrict("e4");
    // rook
    checkException(board, "Raa7", SanValidationProblem.OVERSPECIFIED_RNBQ_FILE_ONLY_ONE_LEGAL_MOVE);

    // knight
    checkException(board, "Nbc6", SanValidationProblem.OVERSPECIFIED_RNBQ_FILE_ONLY_ONE_LEGAL_MOVE);

    // bishop
    checkException(board, "Bcb7", SanValidationProblem.OVERSPECIFIED_RNBQ_FILE_ONLY_ONE_LEGAL_MOVE);

    // queen
    checkException(board, "Qdd7", SanValidationProblem.OVERSPECIFIED_RNBQ_FILE_ONLY_ONE_LEGAL_MOVE);

  }

  @SuppressWarnings("static-method")
  @Test
  void testPieceFile2OnlyOneLegalMoveMore() {

    final Board board = new Board();
    board.movesStrict("e4", "e5", "d3", "Bb4+", "Nc3", "Nf6");
    checkException(board, "Nge2", SanValidationProblem.OVERSPECIFIED_RNBQ_FILE_ONLY_ONE_LEGAL_MOVE);
  }

  @SuppressWarnings("static-method")
  @Test
  void testPieceFile3MustUseRank() {
    {
      final Board board = new Board();

      board.movesStrict("a4", "a5", "h4", "h5", "Ra3", "Ra6", "Rhh3", "Rhh6", "Rad3", "Rac6", "Rd4", "Rc5", "Rhd3",
          "Rhc6", "Rd5", "Rc4");
      // two white rooks

      checkException(board, "Rdd4", SanValidationProblem.INSUFFICIENTLY_SPECIFIED_RNBQ_FILE_RANK_REQUIRED);

      board.moveStrict("R5d4");
      // two black rooks

      checkException(board, "Rcc5", SanValidationProblem.INSUFFICIENTLY_SPECIFIED_RNBQ_FILE_RANK_REQUIRED);

      board.moveStrict("R4c5");

      board.movesStrict("Nf3", "Na6", "Nc3", "Rb6", "Ne4", "Nf6", "Ng3", "Nd5", "Rc4", "c6", "Nf5", "Ndc7", "N5d4",
          "Na8", "Nf5", "N8c7");
      // two white knights

      checkException(board, "Nfd4", SanValidationProblem.INSUFFICIENTLY_SPECIFIED_RNBQ_FILE_RANK_REQUIRED);

      board.moveStrict("N5d4");
      // two black knights
      board.movesStrict("Na8", "Nf5");

      checkException(board, "Nac7", SanValidationProblem.INSUFFICIENTLY_SPECIFIED_RNBQ_FILE_RANK_REQUIRED);

      board.moveStrict("N8c7");

      board.movesStrict("g4", "Rb4", "gxh5", "Rxa4", "Bg2", "g6", "hxg6", "Rb4", "g7", "a4", "g8=B", "a3", "Bh7", "a2",
          "Ne3", "a1=B", "Bf5", "Bxb2", "Bg4", "Bf6");
      // two white bishops

      checkException(board, "Bgh3", SanValidationProblem.INSUFFICIENTLY_SPECIFIED_RNBQ_FILE_RANK_REQUIRED);

      board.moveStrict("B4h3");
      // two black bishops

      checkException(board, "Bfg7", SanValidationProblem.INSUFFICIENTLY_SPECIFIED_RNBQ_FILE_RANK_REQUIRED);

      board.moveStrict("B6g7");

      board.movesStrict("h5", "b5", "h6", "Ra4", "h7", "b4", "h8=Q", "b3", "Rdd4", "bxc2", "Qh7", "Bb7", "Rb4", "Qb8",
          "Rb1", "cxb1=Q", "Qd3", "Qb6");
      // two white queens

      checkException(board, "Qdc2", SanValidationProblem.INSUFFICIENTLY_SPECIFIED_RNBQ_FILE_RANK_REQUIRED);

      board.moveStrict("Q3c2");
      // two black queens
      board.movesStrict("Ne6", "Qd3");

      checkException(board, "Qbc7", SanValidationProblem.INSUFFICIENTLY_SPECIFIED_RNBQ_FILE_RANK_REQUIRED);

      board.moveStrict("Q6c7");
    }

    {
      final Board board = new Board();

      board.movesStrict("e4", "e5", "Bc4", "Bc5", "Nf3", "Nf6", "O-O", "O-O", "Nxe5", "Nxe4", "Qg4", "Qg5", "Na3",
          "Na6", "d4", "d5", "Be3", "Be6", "Bb5", "Bb4", "c4", "c5", "cxd5", "cxd4", "d6", "d3", "d7", "d2", "d8=R",
          "d1=R", "Rd3", "Rc1", "Rfd1", "Rc6", "Rac1", "Rac8", "R1d2", "Rfd8", "Rdc3", "Rd7", "R1c2", "h5", "h4",
          "hxg4", "hxg5", "g6", "g3", "f6", "f3", "gxf3", "gxf6", "f2+", "Kh2", "f1=R", "f7+", "Kh7", "f8=R", "Rff7",
          "Rdd3", "Rfe7", "Rff2", "Rdd6", "Rc1", "b6", "R3c2", "Rdd7", "Rcd1", "Rb7");

      // three white rooks

      checkException(board, "Rdd2", SanValidationProblem.INSUFFICIENTLY_SPECIFIED_RNBQ_FILE_RANK_REQUIRED);

      board.moveStrict("R1d2");
      // three black rooks

      checkException(board, "Rcc7", SanValidationProblem.INSUFFICIENTLY_SPECIFIED_RNBQ_FILE_RANK_REQUIRED);

      board.moveStrict("R8c7");
    }

  }

  @SuppressWarnings("static-method")
  @Test
  void testPieceFile4MustUseRankOrSquare() {
    {
      final Board board = new Board();

      board.movesStrict("c4", "f5", "c5", "f4", "c6", "f3", "cxb7", "fxg2", "bxa8=N", "gxh1=N", "Nc3", "Nf6", "Nf3",
          "Nc6", "Nb6", "Ng3", "Nba4", "Ngh5", "Nc5", "Nf4", "Ng5", "Nb4", "Nge4", "Nbd5", "Ng5", "Nb4");

      // three white knights

      checkException(board, "Nce4",
          SanValidationProblem.INSUFFICIENTLY_SPECIFIED_RNBQ_FILE_EITHER_RANK_OR_SQUARE_REQUIRED);

      board.moveStrict("N3e4");
      // three black knights

      checkException(board, "Nfd5",
          SanValidationProblem.INSUFFICIENTLY_SPECIFIED_RNBQ_FILE_EITHER_RANK_OR_SQUARE_REQUIRED);

      board.moveStrict("N6d5");
    }

    {
      final Board board = new Board();

      board.movesStrict("c4", "f5", "c5", "f4", "c6", "f3", "cxb7", "fxg2", "bxa8=N", "gxh1=N", "Nc3", "Nf6", "Nf3",
          "Nc6", "Nb6", "Ng3", "Nba4", "Ngh5", "Nc5", "Nf4", "Ng5", "Nb4", "Nge4", "Nbd5", "Ng5", "Nb4", "a4", "h5",
          "a5", "h4", "a6", "h3", "Bg2", "hxg2", "Rb1", "g1=N", "Ra1", "Bb7", "axb7", "Ngh3", "b8=N", "Nxf2", "Nc6",
          "N2e4", "Ne5", "Nd6", "Ned3", "Nc8", "Nf2", "Nb6", "Nh1", "Qc8", "Ng3", "Qb8");
      // four white knights

      checkException(board, "Nce4",
          SanValidationProblem.INSUFFICIENTLY_SPECIFIED_RNBQ_FILE_EITHER_RANK_OR_SQUARE_REQUIRED);

      board.moveStrict("Nc3e4");
      // four black knights

      checkException(board, "Nbd5",
          SanValidationProblem.INSUFFICIENTLY_SPECIFIED_RNBQ_FILE_EITHER_RANK_OR_SQUARE_REQUIRED);

      board.moveStrict("Nb6d5");
    }

    {
      final Board board = new Board();

      board.movesStrict("c4", "f5", "c5", "f4", "c6", "f3", "cxb7", "fxg2", "bxa8=B", "gxh1=B", "e4", "d5", "e5", "d4",
          "e6", "d3", "Qc2", "Nh6", "Bh3", "dxc2", "Bg4", "cxb1=B", "Bgf3", "Nf7", "exf7+", "Kd7", "Be2", "Rg8",
          "fxg8=B", "Bg6", "Bgd5", "Bg2", "Bh5", "Bh3", "Bb3", "Be6", "Bad5", "Be4", "Bbd1", "Ba6", "Bde2", "Bed3",
          "Be4", "Bdc4", "Bf5", "Bxa2", "Bef3", "Qe8");
      // three white bishops

      checkException(board, "Bfg4",
          SanValidationProblem.INSUFFICIENTLY_SPECIFIED_RNBQ_FILE_EITHER_RANK_OR_SQUARE_REQUIRED);

      board.moveStrict("Bf5g4");
      board.movesStrict("Kd8", "Kd1"); // unpin black bishop
      // three black bishops

      checkException(board, "Bac4",
          SanValidationProblem.INSUFFICIENTLY_SPECIFIED_RNBQ_FILE_EITHER_RANK_OR_SQUARE_REQUIRED);

      board.moveStrict("Ba6c4");

      board.movesStrict("b4", "g5", "Bh3", "g4", "b5", "g3", "b6", "g2", "b7", "Nc6", "Ne2", "Nb4", "Ng3", "Nd5", "Nf1",
          "gxf1=B", "Bfg4", "Nb6", "Bf5", "Na8", "bxa8=B", "Bb5", "Baf3", "Bc6", "Ke1", "Bd3", "Kd1", "Bac4", "Ke1",
          "Bde4");
      // four white bishops

      checkException(board, "Bfg4",
          SanValidationProblem.INSUFFICIENTLY_SPECIFIED_RNBQ_FILE_EITHER_RANK_OR_SQUARE_REQUIRED);

      board.moveStrict("Bf3g4");
      // four black bishops

      checkException(board, "Bed5",
          SanValidationProblem.INSUFFICIENTLY_SPECIFIED_RNBQ_FILE_EITHER_RANK_OR_SQUARE_REQUIRED);

      board.moveStrict("Be4d5");
    }

    {
      final Board board = new Board();

      board.movesStrict("f4", "c5", "f5", "c4", "f6", "c3", "fxg7", "cxb2", "gxh8=Q", "bxa1=Q", "c4", "f5", "c5", "f4",
          "c6", "f3", "cxb7", "fxg2", "bxa8=Q", "gxh1=Q", "Qb3", "Qf3", "Qab7", "Qe5", "Qf6", "Qeh5", "Qd6", "e6",
          "Q3b4", "Qdf6", "Q7b6", "Q6f5");
      // three white queens

      checkException(board, "Qbc5",
          SanValidationProblem.INSUFFICIENTLY_SPECIFIED_RNBQ_FILE_EITHER_RANK_OR_SQUARE_REQUIRED);

      board.moveStrict("Qb6c5");
      // three black queens

      checkException(board, "Qfg4",
          SanValidationProblem.INSUFFICIENTLY_SPECIFIED_RNBQ_FILE_EITHER_RANK_OR_SQUARE_REQUIRED);

      board.moveStrict("Qf5g4");

      board.movesStrict("a4", "Qxh2", "Qxa7", "h5", "a5", "h4", "a6", "Qhg3+", "Kd1", "h3", "Qab6", "h2", "a7", "h1=Q",
          "a8=Q", "Qgf5", "Qad5", "Qhh5", "Qdd4", "Qgh3");
      // four white queens

      checkException(board, "Qbc5",
          SanValidationProblem.INSUFFICIENTLY_SPECIFIED_RNBQ_FILE_EITHER_RANK_OR_SQUARE_REQUIRED);

      board.moveStrict("Qb4c5");
      // four black queens

      checkException(board, "Qfg4",
          SanValidationProblem.INSUFFICIENTLY_SPECIFIED_RNBQ_FILE_EITHER_RANK_OR_SQUARE_REQUIRED);

      board.moveStrict("Qf3g4");

      board.movesStrict("Qcb4", "Qgf3", "d3", "Be7", "Kc2", "Bf8", "Kb2", "Be7", "Ka1", "Qxe2", "Qa7", "Kf8");
      // four white queens

      checkException(board, "Qdc5",
          SanValidationProblem.INSUFFICIENTLY_SPECIFIED_RNBQ_FILE_EITHER_RANK_OR_SQUARE_REQUIRED);

      board.moveStrict("Qd4c5");
      // four black queens

      checkException(board, "Qhg4",
          SanValidationProblem.INSUFFICIENTLY_SPECIFIED_RNBQ_FILE_EITHER_RANK_OR_SQUARE_REQUIRED);

      board.moveStrict("Qh5g4");
    }
  }

  @SuppressWarnings("static-method")
  @Test
  void testPieceRank1NoLegalMove() {
    final Board board = new Board();

    // white
    // rook
    checkException(board, "R1a4", SanValidationProblem.NOT_REACHABLE_RNBQ_RANK_MULTIPLE);

    // knight
    checkException(board, "N1b3", SanValidationProblem.NOT_REACHABLE_RNBQ_RANK_MULTIPLE);

    // bishop
    checkException(board, "B1d4", SanValidationProblem.NOT_REACHABLE_RNBQ_RANK_MULTIPLE);

    // queen
    checkException(board, "Q1d3", SanValidationProblem.NOT_REACHABLE_RNBQ_RANK_SINGLE);

    // black
    board.movesStrict("e4");
    // rook
    checkException(board, "R8a5", SanValidationProblem.NOT_REACHABLE_RNBQ_RANK_MULTIPLE);

    // knight
    checkException(board, "N8c5", SanValidationProblem.MOVEMENT_RNBQ_FROM_RANK);

    // bishop
    checkException(board, "B8d5", SanValidationProblem.NOT_REACHABLE_RNBQ_RANK_MULTIPLE);

    // queen
    checkException(board, "Q8d6", SanValidationProblem.NOT_REACHABLE_RNBQ_RANK_SINGLE);

  }

  @SuppressWarnings("static-method")
  @Test
  void testPieceRank2OnlyOneLegalMove() {
    final Board board = new Board();

    // white
    // rook
    board.movesStrict("a4");
    board.movesStrict("a5");
    checkException(board, "R1a2", SanValidationProblem.OVERSPECIFIED_RNBQ_RANK_ONLY_ONE_LEGAL_MOVE);

    // knight
    checkException(board, "N1c3", SanValidationProblem.OVERSPECIFIED_RNBQ_RANK_ONLY_ONE_LEGAL_MOVE);

    // bishop
    board.movesStrict("b3");
    board.movesStrict("b6");
    checkException(board, "B1b2", SanValidationProblem.OVERSPECIFIED_RNBQ_RANK_ONLY_ONE_LEGAL_MOVE);

    // queen
    board.movesStrict("d4");
    board.movesStrict("d5");
    checkException(board, "Q1d2", SanValidationProblem.OVERSPECIFIED_RNBQ_RANK_ONLY_ONE_LEGAL_MOVE);

    // black
    board.movesStrict("e4");
    // rook
    checkException(board, "R8a7", SanValidationProblem.OVERSPECIFIED_RNBQ_RANK_ONLY_ONE_LEGAL_MOVE);

    // knight
    checkException(board, "N8c6", SanValidationProblem.OVERSPECIFIED_RNBQ_RANK_ONLY_ONE_LEGAL_MOVE);

    // bishop
    checkException(board, "B8b7", SanValidationProblem.OVERSPECIFIED_RNBQ_RANK_ONLY_ONE_LEGAL_MOVE);

    // queen
    checkException(board, "Q8d7", SanValidationProblem.OVERSPECIFIED_RNBQ_RANK_ONLY_ONE_LEGAL_MOVE);

  }

  @SuppressWarnings("static-method")
  @Test
  void testPieceRank3MustUseFile() {
    {
      final Board board = new Board();

      board.movesStrict("a4", "a5", "h4", "h5", "Ra3", "Ra6", "Rhh3", "Rhh6", "Rab3", "Rad6", "Rhd3", "Rd5", "Rd4",
          "Rg6");
      // two white rooks

      checkException(board, "R4b4", SanValidationProblem.NON_STANDARD_SPECIFIED_RNBQ_RANK_INSTEAD_OF_FILE);

      board.moveStrict("Rdb4");
      // two black rooks

      checkException(board, "R5g5", SanValidationProblem.NON_STANDARD_SPECIFIED_RNBQ_RANK_INSTEAD_OF_FILE);

      board.moveStrict("Rdg5");

      board.movesStrict("Nf3", "Nf6", "Nc3", "Nc6", "Nd4", "Nd5");
      // two white knights

      checkException(board, "N3b5", SanValidationProblem.NON_STANDARD_SPECIFIED_RNBQ_RANK_INSTEAD_OF_FILE);

      board.moveStrict("Ncb5");
      // two black knights

      checkException(board, "N5xb4", SanValidationProblem.NON_STANDARD_SPECIFIED_RNBQ_RANK_INSTEAD_OF_FILE);

      board.moveStrict("Ndxb4");

      board.movesStrict("e4", "f5", "e5", "d6", "exd6", "f4", "dxc7", "f3", "d3", "fxg2", "Bd2", "g1=B", "Bc1", "Nb8",
          "cxb8=B", "Bxf2+", "Kd2", "e6", "Bf4", "Bxd4", "Ke1", "Rh6", "Bxg5", "g6", "Bxh6", "Bfg7");

      // two white bishops

      checkException(board, "B6f4", SanValidationProblem.NON_STANDARD_SPECIFIED_RNBQ_RANK_INSTEAD_OF_FILE);

      board.moveStrict("Bhf4");
      // two black bishops

      checkException(board, "B4e5", SanValidationProblem.NON_STANDARD_SPECIFIED_RNBQ_RANK_INSTEAD_OF_FILE);

      board.moveStrict("Bde5");

      board.movesStrict("c4", "g5", "c5", "g4", "c6", "g3", "cxb7", "g2", "bxc8=Q", "gxf1=Q+", "Kd2", "Kf7", "Qxh5+",
          "Kf6", "Qxe5+", "Kg6", "Qd4", "Qf6", "Qdc4", "Qf2+", "Kd1", "Na2", "Na3", "Nb4", "Q4a6", "Q2d4");

      // two white queens

      checkException(board, "Q6c6", SanValidationProblem.NON_STANDARD_SPECIFIED_RNBQ_RANK_INSTEAD_OF_FILE);

      board.moveStrict("Qac6");
      // two black queens

      checkException(board, "Q4xf4", SanValidationProblem.NON_STANDARD_SPECIFIED_RNBQ_RANK_INSTEAD_OF_FILE);

      board.moveStrict("Qdxf4");

    }
  }

  @SuppressWarnings("static-method")
  @Test
  void testPieceRank4MustUseFileOrSquare() {
    {
      final Board board = new Board();

      board.movesStrict("c4", "f5", "c5", "f4", "c6", "f3", "cxb7", "fxg2", "bxa8=N", "gxh1=N", "Nc3", "Nf6", "Nf3",
          "Nc6", "Nb6", "Ng3", "Nba4", "Ngh5", "Nc5", "Nf4", "Ng5", "Nb4", "Nge4", "Nbd5", "Ng5", "Nb4");

      // three white knights

      checkException(board, "N5e4",
          SanValidationProblem.INSUFFICIENTLY_SPECIFIED_RNBQ_RANK_EITHER_FILE_OR_SQUARE_REQUIRED);

      board.moveStrict("Nc5e4");
      // three black knights

      checkException(board, "N4d5",
          SanValidationProblem.INSUFFICIENTLY_SPECIFIED_RNBQ_RANK_EITHER_FILE_OR_SQUARE_REQUIRED);

      board.moveStrict("Nf4d5");
    }

    {
      final Board board = new Board();

      board.movesStrict("c4", "f5", "c5", "f4", "c6", "f3", "cxb7", "fxg2", "bxa8=N", "gxh1=N", "Nc3", "Nf6", "Nf3",
          "Nc6", "Nb6", "Ng3", "Nba4", "Ngh5", "Nc5", "Nf4", "Ng5", "Nb4", "Nge4", "Nbd5", "Ng5", "Nb4", "a4", "h5",
          "a5", "h4", "a6", "h3", "Bg2", "hxg2", "Rb1", "g1=N", "Ra1", "Bb7", "axb7", "Ngh3", "b8=N", "Nxf2", "Nc6",
          "N2e4", "Ne5", "Nd6", "Ned3", "Nc8", "Nf2", "Nb6", "Nh1", "Qc8", "Ng3", "Qb8");
      // four white knights

      checkException(board, "N3e4",
          SanValidationProblem.INSUFFICIENTLY_SPECIFIED_RNBQ_RANK_EITHER_FILE_OR_SQUARE_REQUIRED);

      board.moveStrict("Nc3e4");
      // four black knights

      checkException(board, "N6d5",
          SanValidationProblem.INSUFFICIENTLY_SPECIFIED_RNBQ_RANK_EITHER_FILE_OR_SQUARE_REQUIRED);

      board.moveStrict("Nb6d5");
    }

    {
      final Board board = new Board();

      board.movesStrict("c4", "f5", "c5", "f4", "c6", "f3", "cxb7", "fxg2", "bxa8=B", "gxh1=B", "e4", "d5", "e5", "d4",
          "e6", "d3", "Qc2", "Nh6", "Bh3", "dxc2", "Bg4", "cxb1=B", "Bgf3", "Nf7", "exf7+", "Kd7", "Be2", "Rg8",
          "fxg8=B", "Bg6", "Bgd5", "Bg2", "Bh5", "Bh3", "Bb3", "Be6", "Bad5", "Be4", "Bbd1", "Ba6", "Bde2", "Bed3",
          "Be4", "Bdc4", "Bf5", "Bxa2", "Bef3", "Qe8");
      // three white bishops

      checkException(board, "B5g4",
          SanValidationProblem.INSUFFICIENTLY_SPECIFIED_RNBQ_RANK_EITHER_FILE_OR_SQUARE_REQUIRED);

      board.moveStrict("Bf5g4");
      board.movesStrict("Kd8", "Kd1"); // unpin black bishop
      // three black bishops

      checkException(board, "B6c4",
          SanValidationProblem.INSUFFICIENTLY_SPECIFIED_RNBQ_RANK_EITHER_FILE_OR_SQUARE_REQUIRED);

      board.moveStrict("Ba6c4");

      board.movesStrict("b4", "g5", "Bh3", "g4", "b5", "g3", "b6", "g2", "b7", "Nc6", "Ne2", "Nb4", "Ng3", "Nd5", "Nf1",
          "gxf1=B", "Bfg4", "Nb6", "Bf5", "Na8", "bxa8=B", "Bb5", "Baf3", "Bc6", "Ke1", "Bd3", "Kd1", "Bac4", "Ke1",
          "Bde4");
      // four white bishops

      checkException(board, "B3g4",
          SanValidationProblem.INSUFFICIENTLY_SPECIFIED_RNBQ_RANK_EITHER_FILE_OR_SQUARE_REQUIRED);

      board.moveStrict("Bf3g4");
      // four black bishops

      checkException(board, "B4d5",
          SanValidationProblem.INSUFFICIENTLY_SPECIFIED_RNBQ_RANK_EITHER_FILE_OR_SQUARE_REQUIRED);

      board.moveStrict("Be4d5");
    }

    {
      final Board board = new Board();

      board.movesStrict("f4", "c5", "f5", "c4", "f6", "c3", "fxg7", "cxb2", "gxh8=Q", "bxa1=Q", "c4", "f5", "c5", "f4",
          "c6", "f3", "cxb7", "fxg2", "bxa8=Q", "gxh1=Q", "Qb3", "Qf3", "Qab7", "Qe5", "Qf6", "Qeh5", "Qd6", "e6",
          "Q3b4", "Qdf6", "Q7b6", "Q6f5");
      // three white queens

      checkException(board, "Q6c5",
          SanValidationProblem.INSUFFICIENTLY_SPECIFIED_RNBQ_RANK_EITHER_FILE_OR_SQUARE_REQUIRED);

      board.moveStrict("Qb6c5");
      // three black queens

      checkException(board, "Q5g4",
          SanValidationProblem.INSUFFICIENTLY_SPECIFIED_RNBQ_RANK_EITHER_FILE_OR_SQUARE_REQUIRED);

      board.moveStrict("Qf5g4");

      board.movesStrict("a4", "Qxh2", "Qxa7", "h5", "a5", "h4", "a6", "Qhg3+", "Kd1", "h3", "Qab6", "h2", "a7", "h1=Q",
          "a8=Q", "Qgf5", "Qad5", "Qhh5", "Qdd4", "Qgh3");
      // four white queens

      checkException(board, "Q4c5",
          SanValidationProblem.INSUFFICIENTLY_SPECIFIED_RNBQ_RANK_EITHER_FILE_OR_SQUARE_REQUIRED);

      board.moveStrict("Qb4c5");
      // four black queens

      checkException(board, "Q3g4",
          SanValidationProblem.INSUFFICIENTLY_SPECIFIED_RNBQ_RANK_EITHER_FILE_OR_SQUARE_REQUIRED);

      board.moveStrict("Qf3g4");

      board.movesStrict("Qcb4", "Qgf3", "d3", "Be7", "Kc2", "Bf8", "Kb2", "Be7", "Ka1", "Qxe2", "Qa7", "Kf8");
      // four white queens

      checkException(board, "Q4c5",
          SanValidationProblem.INSUFFICIENTLY_SPECIFIED_RNBQ_RANK_EITHER_FILE_OR_SQUARE_REQUIRED);

      board.moveStrict("Qd4c5");
      // four black queens

      checkException(board, "Q5g4",
          SanValidationProblem.INSUFFICIENTLY_SPECIFIED_RNBQ_RANK_EITHER_FILE_OR_SQUARE_REQUIRED);

      board.moveStrict("Qh5g4");
    }
  }

  @SuppressWarnings("static-method")
  @Test
  void testPieceSquare1NoLegalMove() {
    final Board board = new Board();

    // white
    // rook no square specification allowed
    // knight
    checkException(board, "Nb1c4", SanValidationProblem.MOVEMENT_RNBQ_FROM_SQUARE);

    // bishop
    checkException(board, "Bc1a3", SanValidationProblem.NOT_REACHABLE_RNBQ_SQUARE);

    // queen
    checkException(board, "Qd1d4", SanValidationProblem.NOT_REACHABLE_RNBQ_SQUARE);

    // black
    board.movesStrict("e4");
    // rook no square specification allowed
    // knight
    checkException(board, "Ng8d4", SanValidationProblem.MOVEMENT_RNBQ_FROM_SQUARE);

    // bishop
    checkException(board, "Bf8h6", SanValidationProblem.NOT_REACHABLE_RNBQ_SQUARE);

    // queen
    checkException(board, "Qd8d5", SanValidationProblem.NOT_REACHABLE_RNBQ_SQUARE);

  }

  @SuppressWarnings("static-method")
  @Test
  void testPieceSquare1NoLegalMoveMore() {
    final Board board = new Board("kn6/3p4/8/8/1Qp1pQ2/3p4/3Q4/2K5 w - - 0 100");

    // queen
    checkException(board, "Qd2d4", SanValidationProblem.NOT_REACHABLE_RNBQ_SQUARE);
  }

  @SuppressWarnings("static-method")
  @Test
  void testPieceSquare2OnlyOneLegalMove() {
    final Board board = new Board();

    // white
    // rook no square specification allowed
    // knight
    checkException(board, "Nb1c3", SanValidationProblem.OVERSPECIFIED_RNBQ_SQUARE_ONLY_ONE_LEGAL_MOVE);

    // bishop
    board.movesStrict("b3");
    board.movesStrict("b6");
    checkException(board, "Bc1b2", SanValidationProblem.OVERSPECIFIED_RNBQ_SQUARE_ONLY_ONE_LEGAL_MOVE);

    // queen
    board.movesStrict("d4");
    board.movesStrict("d5");
    checkException(board, "Qd1d2", SanValidationProblem.OVERSPECIFIED_RNBQ_SQUARE_ONLY_ONE_LEGAL_MOVE);

    // black
    board.movesStrict("e4");
    // rook no square specification allowed
    // knight
    checkException(board, "Nb8c6", SanValidationProblem.OVERSPECIFIED_RNBQ_SQUARE_ONLY_ONE_LEGAL_MOVE);

    // bishop
    checkException(board, "Bc8b7", SanValidationProblem.OVERSPECIFIED_RNBQ_SQUARE_ONLY_ONE_LEGAL_MOVE);

    // queen
    checkException(board, "Qd8d7", SanValidationProblem.OVERSPECIFIED_RNBQ_SQUARE_ONLY_ONE_LEGAL_MOVE);

  }

  @SuppressWarnings("static-method")
  @Test
  void testPieceSquare2OnlyOneLegalMoveMore() {
    final Board board = new Board("kn6/3p4/3Q4/3p4/1Qp1p3/8/3Q4/2K5 w - - 0 100");

    // queen
    checkException(board, "Qd2d4", SanValidationProblem.OVERSPECIFIED_RNBQ_SQUARE_ONLY_ONE_LEGAL_MOVE);
  }

  @SuppressWarnings("static-method")
  @Test
  void testPieceSquare3OnlyOnPieceOnFile() {
    {
      final Board board = new Board();

      board.movesStrict("a4", "a5", "h4", "h5", "Ra3", "Ra6", "Rhh3", "Rhh6", "Rab3", "Rad6", "Rhd3", "Rd5", "Rd4",
          "Rg6");
      // two white rooks
      // rook no square specification allowed
      board.moveStrict("Rdb4");
      // two black rooks
      // rook no square specification allowed
      board.moveStrict("Rdg5");

      board.movesStrict("Nf3", "Nf6", "Nc3", "Nc6", "Nd4", "Nd5");
      // two white knights

      checkException(board, "Nc3b5", SanValidationProblem.OVERSPECIFIED_RNBQ_SQUARE_RANK_NOT_NECESSARY);

      board.moveStrict("Ncb5");
      // two black knights

      checkException(board, "Nd5xb4", SanValidationProblem.OVERSPECIFIED_RNBQ_SQUARE_RANK_NOT_NECESSARY);

      board.moveStrict("Ndxb4");

      board.movesStrict("e4", "f5", "e5", "d6", "exd6", "f4", "dxc7", "f3", "d3", "fxg2", "Bd2", "g1=B", "Bc1", "Nb8",
          "cxb8=B", "Bxf2+", "Kd2", "e6", "Bf4", "Bxd4", "Ke1", "Rh6", "Bxg5", "g6", "Bxh6", "Bfg7");

      // two white bishops

      checkException(board, "Bh6f4", SanValidationProblem.OVERSPECIFIED_RNBQ_SQUARE_RANK_NOT_NECESSARY);

      board.moveStrict("Bhf4");
      // two black bishops

      checkException(board, "Bd4e5", SanValidationProblem.OVERSPECIFIED_RNBQ_SQUARE_RANK_NOT_NECESSARY);

      board.moveStrict("Bde5");

      board.movesStrict("c4", "g5", "c5", "g4", "c6", "g3", "cxb7", "g2", "bxc8=Q", "gxf1=Q+", "Kd2", "Kf7", "Qxh5+",
          "Kf6", "Qxe5+", "Kg6", "Qd4", "Qf6", "Qdc4", "Qf2+", "Kd1", "Na2", "Na3", "Nb4", "Q4a6", "Q2d4");

      // two white queens

      checkException(board, "Qa6c6", SanValidationProblem.OVERSPECIFIED_RNBQ_SQUARE_RANK_NOT_NECESSARY);

      board.moveStrict("Qac6");
      // two black queens

      checkException(board, "Qd4xf4", SanValidationProblem.OVERSPECIFIED_RNBQ_SQUARE_RANK_NOT_NECESSARY);

      board.moveStrict("Qdxf4");

    }
  }

  @SuppressWarnings("static-method")
  @Test
  void testPieceSquare3OnlyOnPieceOnFileMore() {
    final Board board = new Board("kn6/3p4/8/8/1Q3Q2/3Q4/8/2K5 w - - 0 100");

    // queen
    checkException(board, "Qd3d4", SanValidationProblem.OVERSPECIFIED_RNBQ_SQUARE_RANK_NOT_NECESSARY);
  }

  @SuppressWarnings("static-method")
  @Test
  void testPieceSquare4OnlyOnPieceOnRank() {
    {
      final Board board = new Board();

      board.movesStrict("a4", "a5", "h4", "h5", "Ra3", "Ra6", "Rhh3", "Rhh6", "Rad3", "Rac6", "Rd4", "Rc5", "Rhd3",
          "Rhc6", "Rd5", "Rc4");
      // two white rooks
      // rook no square specification allowed
      board.moveStrict("R5d4");
      // two black rooks
      // rook no square specification allowed
      board.moveStrict("R4c5");

      board.movesStrict("Nf3", "Na6", "Nc3", "Rb6", "Ne4", "Nf6", "Ng3", "Nd5", "Rc4", "c6", "Nf5", "Ndc7", "N5d4",
          "Na8", "Nf5", "N8c7");
      // two white knights

      checkException(board, "Nf5d4", SanValidationProblem.OVERSPECIFIED_RNBQ_SQUARE_FILE_NOT_NECESSARY);

      board.moveStrict("N5d4");
      // two black knights
      board.movesStrict("Na8", "Nf5");

      checkException(board, "Na8c7", SanValidationProblem.OVERSPECIFIED_RNBQ_SQUARE_FILE_NOT_NECESSARY);

      board.moveStrict("N8c7");

      board.movesStrict("g4", "Rb4", "gxh5", "Rxa4", "Bg2", "g6", "hxg6", "Rb4", "g7", "a4", "g8=B", "a3", "Bh7", "a2",
          "Ne3", "a1=B", "Bf5", "Bxb2", "Bg4", "Bf6");
      // two white bishops

      checkException(board, "Bg4h3", SanValidationProblem.OVERSPECIFIED_RNBQ_SQUARE_FILE_NOT_NECESSARY);

      board.moveStrict("B4h3");
      // two black bishops

      checkException(board, "Bf6g7", SanValidationProblem.OVERSPECIFIED_RNBQ_SQUARE_FILE_NOT_NECESSARY);

      board.moveStrict("B6g7");

      board.movesStrict("h5", "b5", "h6", "Ra4", "h7", "b4", "h8=Q", "b3", "Rdd4", "bxc2", "Qh7", "Bb7", "Rb4", "Qb8",
          "Rb1", "cxb1=Q", "Qd3", "Qb6");
      // two white queens

      checkException(board, "Qd3c2", SanValidationProblem.OVERSPECIFIED_RNBQ_SQUARE_FILE_NOT_NECESSARY);

      board.moveStrict("Q3c2");
      // two black queens
      board.movesStrict("Ne6", "Qd3");

      checkException(board, "Qb6c7", SanValidationProblem.OVERSPECIFIED_RNBQ_SQUARE_FILE_NOT_NECESSARY);

      board.moveStrict("Q6c7");
    }
  }

  private static void checkException(Board board, String san, SanValidationProblem expectedValidation) {
    boolean isException;
    try {
      StrictSanParser.parseText(san, board);
      isException = false;
    } catch (final SanValidationException e) {
      isException = true;
      assertEquals(expectedValidation, e.getSanValidationProblem());
    }
    assertTrue(isException);
  }

}
