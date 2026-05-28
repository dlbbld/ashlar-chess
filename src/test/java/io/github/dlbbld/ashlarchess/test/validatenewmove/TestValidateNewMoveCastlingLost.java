// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.test.validatenewmove;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import io.github.dlbbld.ashlarchess.board.Board;
import io.github.dlbbld.ashlarchess.board.enums.CastlingRight;
import io.github.dlbbld.ashlarchess.board.enums.CastlingRightLoss;
import io.github.dlbbld.ashlarchess.common.constants.EnumConstants;

class TestValidateNewMoveCastlingLost implements EnumConstants {

  // --- King moved ---

  @SuppressWarnings("static-method")
  @Test
  void testWhiteKingMoved() {
    final Board board = new Board();
    board.movesStrict("e4", "e5", "Ke2");

    assertEquals(CastlingRight.NONE, board.getCastlingRightWhite());
    assertEquals(CastlingRightLoss.KING_MOVED, board.getWhiteKingSideLoss());
    assertEquals(CastlingRightLoss.KING_MOVED, board.getWhiteQueenSideLoss());
  }

  @SuppressWarnings("static-method")
  @Test
  void testBlackKingMoved() {
    final Board board = new Board();
    board.movesStrict("e4", "e5", "Nf3", "Ke7");

    assertEquals(CastlingRight.NONE, board.getCastlingRightBlack());
    assertEquals(CastlingRightLoss.KING_MOVED, board.getBlackKingSideLoss());
    assertEquals(CastlingRightLoss.KING_MOVED, board.getBlackQueenSideLoss());
  }

  // --- King-side rook moved ---

  @SuppressWarnings("static-method")
  @Test
  void testWhiteKingSideRookMoved() {
    final Board board = new Board();
    board.movesStrict("h4", "e5", "Rh3");

    assertEquals(CastlingRight.QUEEN_SIDE, board.getCastlingRightWhite());
    assertEquals(CastlingRightLoss.ROOK_MOVED, board.getWhiteKingSideLoss());
    assertEquals(CastlingRightLoss.NOT_LOST, board.getWhiteQueenSideLoss());
  }

  @SuppressWarnings("static-method")
  @Test
  void testBlackKingSideRookMoved() {
    final Board board = new Board();
    board.movesStrict("e4", "h5", "d4", "Rh6");

    assertEquals(CastlingRight.QUEEN_SIDE, board.getCastlingRightBlack());
    assertEquals(CastlingRightLoss.ROOK_MOVED, board.getBlackKingSideLoss());
    assertEquals(CastlingRightLoss.NOT_LOST, board.getBlackQueenSideLoss());
  }

  // --- Queen-side rook moved ---

  @SuppressWarnings("static-method")
  @Test
  void testWhiteQueenSideRookMoved() {
    final Board board = new Board();
    board.movesStrict("a4", "e5", "Ra3");

    assertEquals(CastlingRight.KING_SIDE, board.getCastlingRightWhite());
    assertEquals(CastlingRightLoss.NOT_LOST, board.getWhiteKingSideLoss());
    assertEquals(CastlingRightLoss.ROOK_MOVED, board.getWhiteQueenSideLoss());
  }

  @SuppressWarnings("static-method")
  @Test
  void testBlackQueenSideRookMoved() {
    final Board board = new Board();
    board.movesStrict("e4", "a5", "d4", "Ra6");

    assertEquals(CastlingRight.KING_SIDE, board.getCastlingRightBlack());
    assertEquals(CastlingRightLoss.NOT_LOST, board.getBlackKingSideLoss());
    assertEquals(CastlingRightLoss.ROOK_MOVED, board.getBlackQueenSideLoss());
  }

  // --- Castled king-side ---

  @SuppressWarnings("static-method")
  @Test
  void testWhiteCastledKingSide() {
    final Board board = new Board();
    board.movesStrict("e4", "e5", "Nf3", "Nc6", "Bc4", "Bc5", "O-O");

    assertEquals(CastlingRight.NONE, board.getCastlingRightWhite());
    assertEquals(CastlingRightLoss.CASTLED, board.getWhiteKingSideLoss());
    assertEquals(CastlingRightLoss.CASTLED, board.getWhiteQueenSideLoss());
  }

  @SuppressWarnings("static-method")
  @Test
  void testBlackCastledKingSide() {
    final Board board = new Board();
    board.movesStrict("e4", "e5", "Nf3", "Nf6", "Nc3", "Bc5", "Bc4", "O-O");

    assertEquals(CastlingRight.NONE, board.getCastlingRightBlack());
    assertEquals(CastlingRightLoss.CASTLED, board.getBlackKingSideLoss());
    assertEquals(CastlingRightLoss.CASTLED, board.getBlackQueenSideLoss());
  }

  // --- Castled queen-side ---

  @SuppressWarnings("static-method")
  @Test
  void testWhiteCastledQueenSide() {
    final Board board = new Board();
    board.movesStrict("d4", "d5", "Nc3", "Nc6", "Bf4", "Bf5", "Qd2", "Qd7", "O-O-O");

    assertEquals(CastlingRight.NONE, board.getCastlingRightWhite());
    assertEquals(CastlingRightLoss.CASTLED, board.getWhiteKingSideLoss());
    assertEquals(CastlingRightLoss.CASTLED, board.getWhiteQueenSideLoss());
  }

  @SuppressWarnings("static-method")
  @Test
  void testBlackCastledQueenSide() {
    final Board board = new Board();
    board.movesStrict("e4", "d5", "d4", "Nc6", "Nf3", "Bf5", "Nc3", "Qd7", "Bc4", "O-O-O");

    assertEquals(CastlingRight.NONE, board.getCastlingRightBlack());
    assertEquals(CastlingRightLoss.CASTLED, board.getBlackKingSideLoss());
    assertEquals(CastlingRightLoss.CASTLED, board.getBlackQueenSideLoss());
  }

  // --- Unknown FEN import ---

  @SuppressWarnings("static-method")
  @Test
  void testFenImportNoRights() {
    // FEN with no castling rights at all
    final Board board = new Board("r3k2r/pppppppp/8/8/8/8/PPPPPPPP/R3K2R w - - 0 1");

    assertEquals(CastlingRight.NONE, board.getCastlingRightWhite());
    assertEquals(CastlingRight.NONE, board.getCastlingRightBlack());
    assertEquals(CastlingRightLoss.UNKNOWN_FEN_IMPORT, board.getWhiteKingSideLoss());
    assertEquals(CastlingRightLoss.UNKNOWN_FEN_IMPORT, board.getWhiteQueenSideLoss());
    assertEquals(CastlingRightLoss.UNKNOWN_FEN_IMPORT, board.getBlackKingSideLoss());
    assertEquals(CastlingRightLoss.UNKNOWN_FEN_IMPORT, board.getBlackQueenSideLoss());
  }

  @SuppressWarnings("static-method")
  @Test
  void testFenImportPartialRights() {
    // FEN with only white king-side and black queen-side castling rights
    final Board board = new Board("r3k2r/pppppppp/8/8/8/8/PPPPPPPP/R3K2R w Kq - 0 1");

    assertEquals(CastlingRight.KING_SIDE, board.getCastlingRightWhite());
    assertEquals(CastlingRight.QUEEN_SIDE, board.getCastlingRightBlack());
    assertEquals(CastlingRightLoss.NOT_LOST, board.getWhiteKingSideLoss());
    assertEquals(CastlingRightLoss.UNKNOWN_FEN_IMPORT, board.getWhiteQueenSideLoss());
    assertEquals(CastlingRightLoss.UNKNOWN_FEN_IMPORT, board.getBlackKingSideLoss());
    assertEquals(CastlingRightLoss.NOT_LOST, board.getBlackQueenSideLoss());
  }

  @SuppressWarnings("static-method")
  @Test
  void testFenImportAllRights() {
    // FEN with all castling rights - no losses
    final Board board = new Board("r3k2r/pppppppp/8/8/8/8/PPPPPPPP/R3K2R w KQkq - 0 1");

    assertEquals(CastlingRight.KING_AND_QUEEN_SIDE, board.getCastlingRightWhite());
    assertEquals(CastlingRight.KING_AND_QUEEN_SIDE, board.getCastlingRightBlack());
    assertEquals(CastlingRightLoss.NOT_LOST, board.getWhiteKingSideLoss());
    assertEquals(CastlingRightLoss.NOT_LOST, board.getWhiteQueenSideLoss());
    assertEquals(CastlingRightLoss.NOT_LOST, board.getBlackKingSideLoss());
    assertEquals(CastlingRightLoss.NOT_LOST, board.getBlackQueenSideLoss());
  }

  // --- Rook captured (opponent captures the rook on its original square, king never moved) ---

  @SuppressWarnings("static-method")
  @Test
  void testBlackKingSideRookCapturedByWhite() {
    // White bishop goes Bb2-xg7-xh8, capturing black's h8 rook. Black king never moves.
    final Board board = new Board();
    board.movesStrict("b3", "a5", "Bb2", "a4", "Bxg7", "a3", "Bxh8");

    assertEquals(CastlingRight.QUEEN_SIDE, board.getCastlingRightBlack());
    assertEquals(CastlingRightLoss.ROOK_CAPTURED, board.getBlackKingSideLoss());
    assertEquals(CastlingRightLoss.NOT_LOST, board.getBlackQueenSideLoss());
  }

  @SuppressWarnings("static-method")
  @Test
  void testBlackQueenSideRookCapturedByWhite() {
    // White pawn marches a-file and promotes on a8 capturing the rook.
    final Board board = new Board();
    board.movesStrict("a4", "b5", "axb5", "c5", "b6", "c4", "b7", "c3", "bxa8=Q");

    assertEquals(CastlingRight.KING_SIDE, board.getCastlingRightBlack());
    assertEquals(CastlingRightLoss.NOT_LOST, board.getBlackKingSideLoss());
    assertEquals(CastlingRightLoss.ROOK_CAPTURED, board.getBlackQueenSideLoss());
  }

  @SuppressWarnings("static-method")
  @Test
  void testWhiteKingSideRookCapturedByBlack() {
    // Mirror of black king-side test: black bishop goes Bb7-xg2-xh1.
    final Board board = new Board();
    board.movesStrict("a3", "b6", "a4", "Bb7", "a5", "Bxg2", "a6", "Bxh1");

    assertEquals(CastlingRight.QUEEN_SIDE, board.getCastlingRightWhite());
    assertEquals(CastlingRightLoss.ROOK_CAPTURED, board.getWhiteKingSideLoss());
    assertEquals(CastlingRightLoss.NOT_LOST, board.getWhiteQueenSideLoss());
  }

  @SuppressWarnings("static-method")
  @Test
  void testWhiteQueenSideRookCapturedByBlack() {
    // Mirror of black queen-side test: black pawn marches b-file and promotes on b1 capturing a1 is complex.
    // Instead: black bishop goes via Bb4-xa3... no. Simpler: use Bc3-xa1 path.
    // Black bishop goes Bf8-b4-a3...no. Let me use: black queen captures a1 rook.
    final Board board = new Board();
    board.movesStrict("h3", "a5", "h4", "a4", "h5", "a3", "h6", "axb2", "hxg7", "bxa1=Q");

    assertEquals(CastlingRight.KING_SIDE, board.getCastlingRightWhite());
    assertEquals(CastlingRightLoss.NOT_LOST, board.getWhiteKingSideLoss());
    assertEquals(CastlingRightLoss.ROOK_CAPTURED, board.getWhiteQueenSideLoss());
  }

}
