// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.test.board;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import io.github.dlbbld.ashlarchess.board.Board;
import io.github.dlbbld.ashlarchess.board.LegalMove;
import io.github.dlbbld.ashlarchess.board.enums.CastlingMove;
import io.github.dlbbld.ashlarchess.board.enums.Square;

/**
 * Coverage for the {@link LegalMove} convenience predicates ({@code isCapture}/{@code isCastling}/{@code isPromotion}/
 * {@code isEnPassant}) and the {@link LegalMove#enPassantCapturedPawnSquare()} accessor.
 */
class TestLegalMovePredicates {

  @SuppressWarnings("static-method")
  @Test
  void testNormalQuietMovePredicates() {
    final Board board = new Board();
    final LegalMove knightToC3 = findByFromTo(board, Square.B1, Square.C3);
    assertFalse(knightToC3.isCapture());
    assertFalse(knightToC3.isCastling());
    assertFalse(knightToC3.isPromotion());
    assertFalse(knightToC3.isEnPassant());
  }

  @SuppressWarnings("static-method")
  @Test
  void testRegularCaptureIsCaptureButNotEnPassant() {
    final Board board = new Board();
    board.movesStrict("e4", "d5");
    final LegalMove exd5 = findByFromTo(board, Square.E4, Square.D5);
    assertTrue(exd5.isCapture());
    assertFalse(exd5.isEnPassant());
  }

  @SuppressWarnings("static-method")
  @Test
  void testEnPassantPredicatesAndCapturedPawnSquare() {
    // 1.e4 Nf6 2.e5 d5 - now white's e5 pawn can capture the d-pawn en passant (e5xd6).
    final Board board = new Board();
    board.movesStrict("e4", "Nf6", "e5", "d5");
    final LegalMove exd6 = findByFromTo(board, Square.E5, Square.D6);
    assertTrue(exd6.isEnPassant());
    assertTrue(exd6.isCapture());
    assertFalse(exd6.isCastling());
    assertFalse(exd6.isPromotion());
    // The captured black pawn stands on d5 (destination file d, origin rank 5), not on the destination d6.
    assertEquals(Square.D5, exd6.enPassantCapturedPawnSquare());
  }

  @SuppressWarnings("static-method")
  @Test
  void testCastlingPredicate() {
    final Board board = new Board();
    board.movesStrict("e4", "e5", "Nf3", "Nc6", "Bc4", "Bc5");
    final LegalMove castling = findByCastlingMove(board, CastlingMove.KING_SIDE);
    assertTrue(castling.isCastling());
    assertFalse(castling.isEnPassant());
    assertFalse(castling.isPromotion());
  }

  @SuppressWarnings("static-method")
  @Test
  void testPromotionPredicate() {
    final Board board = Board.fromFenStrict("4k3/P7/8/8/8/8/8/4K3 w - - 0 1");
    final LegalMove promotion = findFromSquare(board, Square.A7);
    assertTrue(promotion.isPromotion());
    assertFalse(promotion.isCapture());
    assertFalse(promotion.isEnPassant());
  }

  @SuppressWarnings("static-method")
  @Test
  void testEnPassantCapturedPawnSquareThrowsForNonEnPassant() {
    final Board board = new Board();
    final LegalMove e2ToE4 = findByFromTo(board, Square.E2, Square.E4);
    assertThrows(IllegalStateException.class, e2ToE4::enPassantCapturedPawnSquare);
  }

  private static LegalMove findByFromTo(Board board, Square from, Square to) {
    for (final LegalMove legalMove : board.getLegalMoves()) {
      if (legalMove.moveSpecification().fromSquare() == from && legalMove.moveSpecification().toSquare() == to) {
        return legalMove;
      }
    }
    throw new AssertionError("no legal move from " + from + " to " + to);
  }

  private static LegalMove findByCastlingMove(Board board, CastlingMove castlingMove) {
    for (final LegalMove legalMove : board.getLegalMoves()) {
      if (legalMove.moveSpecification().castlingMove() == castlingMove) {
        return legalMove;
      }
    }
    throw new AssertionError("no legal " + castlingMove + " castling move");
  }

  private static LegalMove findFromSquare(Board board, Square from) {
    for (final LegalMove legalMove : board.getLegalMoves()) {
      if (legalMove.moveSpecification().fromSquare() == from) {
        return legalMove;
      }
    }
    throw new AssertionError("no legal move from " + from);
  }
}
