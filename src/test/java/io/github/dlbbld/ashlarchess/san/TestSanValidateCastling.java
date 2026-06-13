// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.san;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import io.github.dlbbld.ashlarchess.analyze.CastlingCheckTranslator;
import io.github.dlbbld.ashlarchess.board.Board;
import io.github.dlbbld.ashlarchess.board.enums.CastlingRightLoss;
import io.github.dlbbld.ashlarchess.enums.CastlingCheck;

class TestSanValidateCastling {

  // --- Priority 2: No castling right - king moved ---

  @SuppressWarnings("static-method")
  @Test
  void testNoRightKingMoved() {
    final Board board = new Board();
    board.movesStrict("e4", "e5", "Ke2", "d6", "Ke1", "d5");
    checkCastlingException("O-O", board, CastlingCheck.FINAL_NO_RIGHT, CastlingRightLoss.KING_MOVED);
    checkCastlingException("O-O-O", board, CastlingCheck.FINAL_NO_RIGHT, CastlingRightLoss.KING_MOVED);
  }

  // --- Priority 2: No castling right - rook moved ---

  @SuppressWarnings("static-method")
  @Test
  void testNoRightKingSideRookMoved() {
    final Board board = new Board();
    board.movesStrict("h4", "e5", "Rh3", "d6", "Rh1", "d5");
    checkCastlingException("O-O", board, CastlingCheck.FINAL_NO_RIGHT, CastlingRightLoss.ROOK_MOVED);
  }

  @SuppressWarnings("static-method")
  @Test
  void testNoRightQueenSideRookMoved() {
    final Board board = new Board();
    board.movesStrict("a4", "e5", "Ra3", "d6", "Ra1", "d5");
    board.movesStrict("b3", "Nc6", "Bb2", "Be7", "Nc3", "Nf6", "Qc1", "a6");
    checkCastlingException("O-O-O", board, CastlingCheck.FINAL_NO_RIGHT, CastlingRightLoss.ROOK_MOVED);
  }

  // --- Priority 2: No castling right - unknown (FEN import) ---

  @SuppressWarnings("static-method")
  @Test
  void testNoRightUnknownFenImport() {
    final Board board = new Board("r3k2r/pppppppp/8/8/8/8/PPPPPPPP/R3K2R w - - 0 1");
    checkCastlingException("O-O", board, CastlingCheck.FINAL_NO_RIGHT, CastlingRightLoss.UNKNOWN_FEN_IMPORT);
    checkCastlingException("O-O-O", board, CastlingCheck.FINAL_NO_RIGHT, CastlingRightLoss.UNKNOWN_FEN_IMPORT);
  }

  // --- Priority 3: Squares between king and rook not empty ---

  @SuppressWarnings("static-method")
  @Test
  void testSquaresNotEmpty() {
    final Board board = new Board();
    board.movesStrict("e4");
    checkCastlingException("O-O", board, CastlingCheck.TEMPORARY_SQUARES_NOT_EMPTY, CastlingRightLoss.NOT_LOST);
    checkCastlingException("O-O-O", board, CastlingCheck.TEMPORARY_SQUARES_NOT_EMPTY, CastlingRightLoss.NOT_LOST);
  }

  // --- Priority 4: King in check ---

  @SuppressWarnings("static-method")
  @Test
  void testKingInCheck() {
    final Board board = new Board("rnbqk2r/pppp1ppp/5n2/4p3/2B1P3/2N2N2/PPPP1bPP/R1BQK2R w KQkq - 0 5");
    checkCastlingException("O-O", board, CastlingCheck.TEMPORARY_KING_IN_CHECK, CastlingRightLoss.NOT_LOST);
  }

  // --- Priority 5: King would travel through check ---

  @SuppressWarnings("static-method")
  @Test
  void testKingWouldTravelThroughCheck() {
    final Board board = new Board("rnb1kbnr/pppp2pp/5q2/8/2B5/7N/PPPP2PP/RNBQK2R w KQkq - 0 25");
    checkCastlingException("O-O", board, CastlingCheck.TEMPORARY_KING_TRAVELS_THROUGH_CHECK,
        CastlingRightLoss.NOT_LOST);
  }

  // --- Priority 6: King would end in check ---

  @SuppressWarnings("static-method")
  @Test
  void testKingWouldEndInCheck() {
    final Board board = new Board("rnbqk1nr/pppp1ppp/4p3/2b5/2B1P3/5P1N/PPPP2PP/RNBQK2R w KQkq - 0 25");
    checkCastlingException("O-O", board, CastlingCheck.TEMPORARY_KING_ENDS_IN_CHECK, CastlingRightLoss.NOT_LOST);
  }

  private static void checkCastlingException(String san, Board board, CastlingCheck expectedCastlingCheck,
      CastlingRightLoss expectedLoss) {
    try {
      StrictSanParser.parseText(san, board);
      throw new AssertionError("Expected SanValidationException");
    } catch (final SanValidationException e) {
      assertEquals(CastlingCheckMapper.map(expectedCastlingCheck, expectedLoss), e.getSanValidationProblem());
      assertEquals(CastlingCheckTranslator.toMoveCheck(expectedCastlingCheck, expectedLoss), e.getMoveCheck());
      assertEquals(expectedLoss, e.getCastlingRightLoss());
    }
  }

}
