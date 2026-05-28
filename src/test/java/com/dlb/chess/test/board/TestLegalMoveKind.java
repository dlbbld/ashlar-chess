package com.dlb.chess.test.board;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.dlb.chess.board.Board;
import com.dlb.chess.board.enums.CastlingMove;
import com.dlb.chess.board.enums.Square;
import com.dlb.chess.common.model.MoveSpecification;
import com.dlb.chess.model.LegalMove;
import com.dlb.chess.model.LegalMoveKind;

/**
 * Lock-down coverage for {@link LegalMoveKind} as stored on {@link LegalMove}.
 *
 * <p>
 * Each of the five enum values must be producible by the legal-move generator for at least one constructible position.
 * These tests exercise representative board states and assert that the expected move in each position carries the
 * expected {@code LegalMoveKind}.
 */
class TestLegalMoveKind {

  @SuppressWarnings("static-method")
  @Test
  void testNormalForKnightMove() {
    final Board board = new Board();
    // From the initial position, any piece-move (e.g. Nb1-c3) has kind NORMAL.
    final LegalMove knightToC3 = findLegalMoveByFromTo(board, Square.B1, Square.C3);
    assertEquals(LegalMoveKind.NORMAL, knightToC3.kind());
  }

  @SuppressWarnings("static-method")
  @Test
  void testPawnTwoSquareAdvanceForPawnInitialDoublePush() {
    final Board board = new Board();
    // 1.e4 from the initial position - white pawn e2 -> e4 is a two-square advance.
    final LegalMove e2ToE4 = findLegalMoveByFromTo(board, Square.E2, Square.E4);
    assertEquals(LegalMoveKind.PAWN_TWO_SQUARE_ADVANCE, e2ToE4.kind());
  }

  @SuppressWarnings("static-method")
  @Test
  void testNormalForPawnSingleAdvance() {
    final Board board = new Board();
    // 1.e3 is a one-square advance, not two-square - kind NORMAL.
    final LegalMove e2ToE3 = findLegalMoveByFromTo(board, Square.E2, Square.E3);
    assertEquals(LegalMoveKind.NORMAL, e2ToE3.kind());
  }

  @SuppressWarnings("static-method")
  @Test
  void testEnPassantCaptureAfterTwoSquareAdvance() {
    // Classic en-passant setup: 1.e4 Nf6 2.e5 d5 - now white's e5 pawn can capture d-pawn en passant.
    final Board board = new Board();
    board.movesStrict("e4", "Nf6", "e5", "d5");
    final LegalMove exd6 = findLegalMoveByFromTo(board, Square.E5, Square.D6);
    assertEquals(LegalMoveKind.EN_PASSANT_CAPTURE, exd6.kind());
  }

  @SuppressWarnings("static-method")
  @Test
  void testNormalForRegularPawnCapture() {
    // Plain diagonal pawn capture (not en passant).
    final Board board = new Board();
    board.movesStrict("e4", "d5");
    // Now 2.exd5 is a regular capture, not en passant.
    final LegalMove exd5 = findLegalMoveByFromTo(board, Square.E4, Square.D5);
    assertEquals(LegalMoveKind.NORMAL, exd5.kind());
  }

  @SuppressWarnings("static-method")
  @Test
  void testCastlingForKingSideCastle() {
    // Quick king-side castling setup for white: 1.e4 e5 2.Nf3 Nc6 3.Bc4 Bc5 - now white can castle short.
    final Board board = new Board();
    board.movesStrict("e4", "e5", "Nf3", "Nc6", "Bc4", "Bc5");
    final LegalMove castling = findLegalMoveByCastlingMove(board, CastlingMove.KING_SIDE);
    assertEquals(LegalMoveKind.CASTLING, castling.kind());
  }

  @SuppressWarnings("static-method")
  @Test
  void testPromotionForPawnReachingPromotionRank() {
    // Custom position: white pawn on a7 with empty a8 - any of the four promotion moves carries kind PROMOTION.
    final Board board = new Board("4k3/P7/8/8/8/8/8/4K3 w - - 0 1");
    final LegalMove promotion = findFirstLegalMoveFromSquare(board, Square.A7);
    assertEquals(LegalMoveKind.PROMOTION, promotion.kind());
  }

  /**
   * Utility: find the unique legal move from {@code from} to {@code to} on the given board, or fail.
   */
  private static LegalMove findLegalMoveByFromTo(Board board, Square from, Square to) {
    for (final LegalMove legalMove : board.getLegalMoves()) {
      if (legalMove.moveSpecification().fromSquare() == from && legalMove.moveSpecification().toSquare() == to) {
        return legalMove;
      }
    }
    throw new AssertionError("no legal move from " + from + " to " + to);
  }

  private static LegalMove findLegalMoveByCastlingMove(Board board, CastlingMove castlingMove) {
    for (final LegalMove legalMove : board.getLegalMoves()) {
      final MoveSpecification spec = legalMove.moveSpecification();
      if (spec.castlingMove() == castlingMove) {
        return legalMove;
      }
    }
    throw new AssertionError("no legal " + castlingMove + " castling move");
  }

  private static LegalMove findFirstLegalMoveFromSquare(Board board, Square from) {
    for (final LegalMove legalMove : board.getLegalMoves()) {
      if (legalMove.moveSpecification().fromSquare() == from) {
        return legalMove;
      }
    }
    throw new AssertionError("no legal move from " + from);
  }
}
