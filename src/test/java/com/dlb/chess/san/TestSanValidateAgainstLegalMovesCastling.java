package com.dlb.chess.san;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.dlb.chess.board.Board;
import com.dlb.chess.board.enums.CastlingRightLoss;
import com.dlb.chess.enums.CastlingCheck;

class TestSanValidateAgainstLegalMovesCastling {

  // --- Priority 2: No castling right - king moved ---

  @SuppressWarnings("static-method")
  @Test
  void testNoRightKingMovedWhite() {
    final Board board = new Board();
    board.movesStrict("e4", "e5", "Ke2", "d6", "Ke1", "d5");
    checkCastlingException("O-O", board, CastlingCheck.FINAL_NO_RIGHT, CastlingRightLoss.KING_MOVED);
    checkCastlingException("O-O-O", board, CastlingCheck.FINAL_NO_RIGHT, CastlingRightLoss.KING_MOVED);
  }

  @SuppressWarnings("static-method")
  @Test
  void testNoRightKingMovedBlack() {
    final Board board = new Board();
    board.movesStrict("e4", "e5", "d4", "Ke7", "d5", "Ke8", "Nf3");
    checkCastlingException("O-O", board, CastlingCheck.FINAL_NO_RIGHT, CastlingRightLoss.KING_MOVED);
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
  void testSquaresNotEmptyWhite() {
    // Initial position, white tries to castle
    final Board board = new Board();
    board.movesStrict("e4", "e5");
    // King-side: f1 and g1 occupied
    checkCastlingException("O-O", board, CastlingCheck.TEMPORARY_SQUARES_NOT_EMPTY, CastlingRightLoss.NOT_LOST);
    // Queen-side: b1, c1, d1 occupied
    checkCastlingException("O-O-O", board, CastlingCheck.TEMPORARY_SQUARES_NOT_EMPTY, CastlingRightLoss.NOT_LOST);
  }

  @SuppressWarnings("static-method")
  @Test
  void testSquaresNotEmptyBlack() {
    final Board board = new Board();
    board.movesStrict("e4");
    checkCastlingException("O-O", board, CastlingCheck.TEMPORARY_SQUARES_NOT_EMPTY, CastlingRightLoss.NOT_LOST);
    checkCastlingException("O-O-O", board, CastlingCheck.TEMPORARY_SQUARES_NOT_EMPTY, CastlingRightLoss.NOT_LOST);
  }

  // --- Priority 4: King in check ---

  @SuppressWarnings("static-method")
  @Test
  void testKingInCheckWhite() {
    final Board board = new Board("rnbqk2r/pppp1ppp/5n2/4p3/2B1P3/2N2N2/PPPP1bPP/R1BQK2R w KQkq - 0 5");
    checkCastlingException("O-O", board, CastlingCheck.TEMPORARY_KING_IN_CHECK, CastlingRightLoss.NOT_LOST);
  }

  @SuppressWarnings("static-method")
  @Test
  void testKingInCheckBlack() {
    final Board board = new Board("r1bqk2r/1ppp1pNp/p1n2n2/2b1p3/2B1P3/8/PPPP1PPP/RNBQK2R b KQkq - 0 6");
    checkCastlingException("O-O", board, CastlingCheck.TEMPORARY_KING_IN_CHECK, CastlingRightLoss.NOT_LOST);
  }

  // --- Priority 5: King would travel through check ---

  @SuppressWarnings("static-method")
  @Test
  void testKingWouldTravelThroughCheckWhite() {
    final Board board = new Board("rnb1kbnr/pppp2pp/5q2/8/2B5/7N/PPPP2PP/RNBQK2R w KQkq - 0 25");
    checkCastlingException("O-O", board, CastlingCheck.TEMPORARY_KING_TRAVELS_THROUGH_CHECK,
        CastlingRightLoss.NOT_LOST);
  }

  @SuppressWarnings("static-method")
  @Test
  void testKingWouldTravelThroughCheckBlack() {
    final Board board = new Board("rnbqk2r/ppppppbp/4Nnp1/8/8/8/PPPPPPPP/R1BQKBNR b KQkq - 0 25");
    checkCastlingException("O-O", board, CastlingCheck.TEMPORARY_KING_TRAVELS_THROUGH_CHECK,
        CastlingRightLoss.NOT_LOST);
  }

  // --- Priority 6: King would end in check ---

  @SuppressWarnings("static-method")
  @Test
  void testKingWouldEndInCheckWhite() {
    final Board board = new Board("rnbqk1nr/pppp1ppp/4p3/2b5/2B1P3/5P1N/PPPP2PP/RNBQK2R w KQkq - 0 25");
    checkCastlingException("O-O", board, CastlingCheck.TEMPORARY_KING_ENDS_IN_CHECK, CastlingRightLoss.NOT_LOST);
  }

  @SuppressWarnings("static-method")
  @Test
  void testKingWouldEndInCheckBlack() {
    final Board board = new Board("rnbqk2r/ppppppbp/6pN/8/6n1/4P3/PPPP1PPP/RNBQKB1R b KQkq - 0 25");
    checkCastlingException("O-O", board, CastlingCheck.TEMPORARY_KING_ENDS_IN_CHECK, CastlingRightLoss.NOT_LOST);
  }

  private static void checkCastlingException(String san, Board board, CastlingCheck expectedCastlingCheck,
      CastlingRightLoss expectedLoss) {
    boolean isException;
    try {
      StrictSanParser.parseText(san, board);
      isException = false;
    } catch (final SanValidationException e) {
      isException = true;
      assertEquals(CastlingCheckMapper.map(expectedCastlingCheck, expectedLoss), e.getSanValidationProblem());
      assertEquals(expectedCastlingCheck.toMoveCheck(expectedLoss), e.getMoveCheck());
      assertEquals(expectedLoss, e.getCastlingRightLoss());
    }
    assertTrue(isException);
  }

}
