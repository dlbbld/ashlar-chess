package com.dlb.chess.test.san.move;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.dlb.chess.board.Board;
import com.dlb.chess.san.SanValidationException;
import com.dlb.chess.san.SanValidationProblem;
import com.dlb.chess.san.StrictSanParser;

class TestSanValidateRnbqRankPseudoLegal {

  // --- Not reachable, single piece on rank ---

  @SuppressWarnings("static-method")
  @Test
  void testWhiteNotReachableSingle() {
    // Single white rook on a1, blocked by own pawn on a2. R1a4 not reachable.
    final Board board = new Board("k7/8/8/8/8/8/P7/R3K3 w - - 0 1");
    checkException("R1a4", board, SanValidationProblem.NOT_REACHABLE_RNBQ_RANK_SINGLE);
  }

  @SuppressWarnings("static-method")
  @Test
  void testBlackNotReachableSingle() {
    // Single black rook on a8, blocked by own pawn on a7. R8a5 not reachable.
    final Board board = new Board("r3k3/p7/8/8/8/8/8/4K3 b - - 0 1");
    checkException("R8a5", board, SanValidationProblem.NOT_REACHABLE_RNBQ_RANK_SINGLE);
  }

  // --- Not reachable, multiple pieces on rank ---

  @SuppressWarnings("static-method")
  @Test
  void testWhiteNotReachableMultiple() {
    // Two white rooks on a1 and h1, both blocked by own pawns. R1a4 not reachable.
    final Board board = new Board("k7/8/8/8/8/8/P6P/R3K2R w - - 0 1");
    checkException("R1a4", board, SanValidationProblem.NOT_REACHABLE_RNBQ_RANK_MULTIPLE);
  }

  @SuppressWarnings("static-method")
  @Test
  void testBlackNotReachableMultiple() {
    // Two black rooks on a8 and h8, both blocked by own pawns. R8a5 not reachable.
    final Board board = new Board("r3k2r/p6p/8/8/8/8/8/4K3 b - - 0 1");
    checkException("R8a5", board, SanValidationProblem.NOT_REACHABLE_RNBQ_RANK_MULTIPLE);
  }

  // --- King in check, single pseudo-legal move on rank ---

  @SuppressWarnings("static-method")
  @Test
  void testWhiteKingInCheckSingle() {
    // White bishop on e4 pinned along e-file (king e1, rook e8). B4d5 would expose king.
    final Board board = new Board("4r2k/8/8/8/4B3/8/8/4K3 w - - 0 1");
    checkException("B4d5", board, SanValidationProblem.KING_EXPOSED_TO_CHECK_RNBQ_RANK_SINGLE);
  }

  @SuppressWarnings("static-method")
  @Test
  void testBlackKingInCheckSingle() {
    // Black bishop on e5 pinned along e-file (king e8, rook e1). B5d4 would expose king.
    final Board board = new Board("4k3/8/8/4b3/8/8/8/4R2K b - - 0 1");
    checkException("B5d4", board, SanValidationProblem.KING_EXPOSED_TO_CHECK_RNBQ_RANK_SINGLE);
  }

  // --- King in check, multiple pseudo-legal moves on rank ---

  @SuppressWarnings("static-method")
  @Test
  void testWhiteKingInCheckMultiple() {
    // Two white bishops on b4 and d4, pinned on separate diagonals from c3.
    // b4 pinned along c3-b4-a5 (bishop a5), d4 pinned along c3-d4-e5 (bishop e5).
    // Both can reach c5 but each would expose king.
    // Black pawn on h7 breaks the otherwise mutual same-color-bishops-only material configuration
    // so the position is not in DEAD_POSITION_INSUFFICIENT_MATERIAL. h7 does not interact with the test.
    final Board board = new Board("k7/7p/8/b3b3/1B1B4/2K5/8/8 w - - 0 1");
    checkException("B4c5", board, SanValidationProblem.KING_EXPOSED_TO_CHECK_RNBQ_RANK_MULTIPLE);
  }

  @SuppressWarnings("static-method")
  @Test
  void testBlackKingInCheckMultiple() {
    // Two black bishops on b5 and d5, pinned on separate diagonals from c6.
    // b5 pinned along c6-b5-a4 (bishop a4), d5 pinned along c6-d5-e4 (bishop e4).
    // Both can reach c4 but each would expose king.
    // White pawn on h2 breaks the otherwise mutual same-color-bishops-only material configuration
    // so the position is not in DEAD_POSITION_INSUFFICIENT_MATERIAL. h2 does not interact with the test.
    final Board board = new Board("8/8/2k5/1b1b4/B3B3/8/7P/K7 b - - 0 1");
    checkException("B5c4", board, SanValidationProblem.KING_EXPOSED_TO_CHECK_RNBQ_RANK_MULTIPLE);
  }

  // --- King left in check ---

  @SuppressWarnings("static-method")
  @Test
  void testWhiteKingLeftInCheckSingle() {
    // White king e1 in check from black rook e8. White bishop on c4 can reach B4d5 but doesn't resolve check.
    final Board board = new Board("4r2k/8/8/8/2B5/8/8/4K3 w - - 0 1");
    checkException("B4d5", board, SanValidationProblem.KING_LEFT_IN_CHECK_RNBQ_RANK_SINGLE);
  }

  @SuppressWarnings("static-method")
  @Test
  void testBlackKingLeftInCheckSingle() {
    // Black king e8 in check from white rook e1. Black bishop on c5 can reach B5d4 but doesn't resolve check.
    final Board board = new Board("4k3/8/8/2b5/8/8/8/4R2K b - - 0 1");
    checkException("B5d4", board, SanValidationProblem.KING_LEFT_IN_CHECK_RNBQ_RANK_SINGLE);
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
