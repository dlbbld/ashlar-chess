package com.dlb.chess.test.san.move;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.dlb.chess.board.Board;
import com.dlb.chess.san.SanValidationException;
import com.dlb.chess.san.SanValidationProblem;
import com.dlb.chess.san.StrictSanParser;

class TestSanValidateKingPseudoLegal {

  // --- King captures a guarded opponent piece ---

  @SuppressWarnings("static-method")
  @Test
  void testKingCapturesGuardedPiece() {
    // White king e1, black pawn d2 guarded by black bishop a5. Kxd2 captures a guarded piece.
    final Board board = new Board("4k3/8/8/b7/8/8/3p4/4K3 w - - 0 1");
    checkException("Kxd2", board, SanValidationProblem.KING_CAPTURES_GUARDED_PIECE);
  }

  // --- King moves next to the opponent king ---

  @SuppressWarnings("static-method")
  @Test
  void testKingMovesNextToOpponentKing() {
    // White king e1, black king e3. Ke2 lands adjacent to the black king.
    final Board board = new Board("r7/8/8/8/8/4k3/8/4K3 w - - 0 1");
    checkException("Ke2", board, SanValidationProblem.KING_MOVES_NEXT_TO_OPPONENT_KING);
  }

  // --- King moves to an attacked empty square ---

  @SuppressWarnings("static-method")
  @Test
  void testWhiteKingMovesToAttackedEmptySquare() {
    // White king d1 not in check. Ke2 lands on the e-file attacked by black rook e8.
    final Board board = new Board("4r3/7k/8/8/8/8/8/3K4 w - - 0 1");
    checkException("Ke2", board, SanValidationProblem.KING_MOVES_TO_ATTACKED_EMPTY_SQUARE);
  }

  @SuppressWarnings("static-method")
  @Test
  void testBlackKingMovesToAttackedEmptySquareWhileInCheck() {
    // Black king e7 in check by white rook e1. Ke6 stays on the attacked e-file.
    final Board board = new Board("8/4k3/8/8/8/8/7K/4R3 b - - 0 1");
    checkException("Ke6", board, SanValidationProblem.KING_MOVES_TO_ATTACKED_EMPTY_SQUARE);
  }

  private static void checkException(String san, Board board, SanValidationProblem expectedProblem) {
    boolean isException;
    try {
      StrictSanParser.parseText(san, board);
      isException = false;
    } catch (final SanValidationException e) {
      isException = true;
      assertEquals(expectedProblem, e.getSanValidationProblem());
    }
    assertTrue(isException);
  }

}
