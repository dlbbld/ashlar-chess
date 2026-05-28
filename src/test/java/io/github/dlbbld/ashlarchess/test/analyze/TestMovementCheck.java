// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.test.analyze;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.EnumSet;
import java.util.Set;
import java.util.TreeSet;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;

import io.github.dlbbld.ashlarchess.analyze.ChessRuleAnalyzer;
import io.github.dlbbld.ashlarchess.board.Board;
import io.github.dlbbld.ashlarchess.common.constants.EnumConstants;
import io.github.dlbbld.ashlarchess.common.model.MoveSpecification;
import io.github.dlbbld.ashlarchess.enums.MovementCheck;

/**
 * Per-{@link MovementCheck}-value tests for {@link ChessRuleAnalyzer#analyzeMovement}. Each enum value (including
 * {@code SUCCESS}) has at least one test method exercising it. An {@code @AfterAll} hook verifies the entire enum is
 * covered - adding a new value without a test fails the build.
 */
class TestMovementCheck implements EnumConstants {

  private static final Set<MovementCheck> COVERED = new TreeSet<>();

  private static MovementCheck analyze(Board board, MoveSpecification move) {
    final MovementCheck result = ChessRuleAnalyzer.analyzeMovement(board.getBitboardPosition(), board.getHavingMove(),
        board.getEnPassantCaptureTargetSquare(), move);
    COVERED.add(result);
    return result;
  }

  private static void check(Board board, MoveSpecification move, MovementCheck expected) {
    assertEquals(expected, analyze(board, move));
  }

  private static void check(String fen, MoveSpecification move, MovementCheck expected) {
    check(new Board(fen), move, expected);
  }

  @SuppressWarnings("static-method")
  @Test
  void testSuccess() {
    check(new Board(), new MoveSpecification(E2, E4), MovementCheck.SUCCESS);
  }

  @SuppressWarnings("static-method")
  @Test
  void testNotPossible() {
    // knight cannot move three squares forward
    check(new Board(), new MoveSpecification(B1, B4), MovementCheck.NOT_POSSIBLE);
  }

  @SuppressWarnings("static-method")
  @Test
  void testToSquareOccupiedByOwnPiece() {
    // rook tries to move onto own pawn
    check(new Board(), new MoveSpecification(A1, A2), MovementCheck.TO_SQUARE_OCCUPIED_BY_OWN_PIECE);
  }

  @SuppressWarnings("static-method")
  @Test
  void testLongRangePieceJumpsOverPiece() {
    // rook on a1 cannot jump over own pawn on a4 to reach a8
    check("7k/8/8/8/P7/8/8/R3K3 w - - 0 1", new MoveSpecification(A1, A8),
        MovementCheck.LONG_RANGE_PIECE_JUMPS_OVER_PIECE);
  }

  @SuppressWarnings("static-method")
  @Test
  void testPawnForwardTwoSquareJumpOverSquareOnlyNotEmpty() {
    // jump-over square e3 occupied, destination e4 empty
    check("4k3/8/8/8/8/4n3/4P3/4K3 w - - 0 1", new MoveSpecification(E2, E4),
        MovementCheck.PAWN_FORWARD_TWO_SQUARE_JUMP_OVER_SQUARE_ONLY_NOT_EMPTY);
  }

  @SuppressWarnings("static-method")
  @Test
  void testPawnForwardTwoSquareToSquareOnlyNotEmpty() {
    // jump-over e3 empty, destination e4 occupied
    check("4k3/8/8/8/4n3/8/4P3/4K3 w - - 0 1", new MoveSpecification(E2, E4),
        MovementCheck.PAWN_FORWARD_TWO_SQUARE_TO_SQUARE_ONLY_NOT_EMPTY);
  }

  @SuppressWarnings("static-method")
  @Test
  void testPawnForwardTwoSquareBothSquareNotEmpty() {
    // both jump-over e3 and destination e4 occupied
    check("4k3/8/8/8/4n3/4r3/4P3/4K3 w - - 0 1", new MoveSpecification(E2, E4),
        MovementCheck.PAWN_FORWARD_TWO_SQUARE_BOTH_SQUARE_NOT_EMPTY);
  }

  @SuppressWarnings("static-method")
  @Test
  void testPawnForwardOneSquareToSquareNotEmptyOwnPiece() {
    // own knight on e3 blocks pawn e2-e3
    check("4k3/8/8/8/8/4N3/4P3/4K3 w - - 0 1", new MoveSpecification(E2, E3),
        MovementCheck.PAWN_FORWARD_ONE_SQUARE_TO_SQUARE_NOT_EMPTY_OWN_PIECE);
  }

  @SuppressWarnings("static-method")
  @Test
  void testPawnForwardOneSquareToSquareNotEmptyOpponentPiece() {
    // opponent knight on e3 blocks pawn e2-e3 (pawn can't capture forward)
    check("4k3/8/8/8/8/4n3/4P3/4K3 w - - 0 1", new MoveSpecification(E2, E3),
        MovementCheck.PAWN_FORWARD_ONE_SQUARE_TO_SQUARE_NOT_EMPTY_OPPONENT_PIECE);
  }

  @SuppressWarnings("static-method")
  @Test
  void testPawnDiagonalOwnPiece() {
    // pawn diagonal to own piece
    check("4k3/8/8/8/8/3N4/4P3/4K3 w - - 0 1", new MoveSpecification(E2, D3), MovementCheck.PAWN_DIAGONAL_OWN_PIECE);
  }

  @SuppressWarnings("static-method")
  @Test
  void testPawnEnPassantWrongRank() {
    // white pawn on e4 attempts diagonal to empty d5: rank 5 is not en passant rank for white
    check("4k3/8/8/8/4P3/8/8/4K3 w - - 0 1", new MoveSpecification(E4, D5), MovementCheck.PAWN_EN_PASSANT_WRONG_RANK);
  }

  @SuppressWarnings("static-method")
  @Test
  void testPawnEnPassantNoImmediateBeforeTwoSquareAdvance() {
    // white pawn on e5 attempts diagonal to empty d6 on en passant rank, but no e.p. target set
    check("4k3/8/8/4P3/8/8/8/4K3 w - - 0 1", new MoveSpecification(E5, D6),
        MovementCheck.PAWN_EN_PASSANT_NO_IMMEDIATE_BEFORE_TWO_SQUARE_ADVANCE);
  }

  @SuppressWarnings("static-method")
  @Test
  void testKingCapturesGuardedPiece() {
    // king captures bishop on d2 but bishop is guarded by another bishop on a5
    check("4k3/8/8/b7/8/8/3b4/4K3 w - - 0 1", new MoveSpecification(E1, D2), MovementCheck.KING_CAPTURES_GUARDED_PIECE);
  }

  @SuppressWarnings("static-method")
  @Test
  void testKingCapturesGuardedPieceDiscoveredCheck() {
    // discovered-check-on-self: king blocks rook on d-file, captures along the line
    check("2k5/3r4/8/8/3K1N2/3n4/8/8 w - - 0 1", new MoveSpecification(D4, D3),
        MovementCheck.KING_CAPTURES_GUARDED_PIECE);
  }

  @SuppressWarnings("static-method")
  @Test
  void testKingMovesNextToOpponentKing() {
    // white king e1 cannot move to e2 if black king is on e3
    check("8/8/8/8/8/4k3/8/4K3 w - - 0 1", new MoveSpecification(E1, E2),
        MovementCheck.KING_MOVES_NEXT_TO_OPPONENT_KING);
  }

  @SuppressWarnings("static-method")
  @Test
  void testKingMovesToAttackedEmptySquare() {
    // king moves to f1, attacked by black rook on f8 - empty destination
    check("4kr2/8/8/8/8/8/8/4K3 w - - 0 1", new MoveSpecification(E1, F1),
        MovementCheck.KING_MOVES_TO_ATTACKED_EMPTY_SQUARE);
  }

  @SuppressWarnings("static-method")
  @Test
  void testKingMovesToAttackedEmptySquareDiscoveredCheck() {
    // discovered-check-on-self: king on g4 in check from rook on a4, attempts to step away on rank
    // 4 to h4. Pre-move attackedSquares does not include h4 (king blocks on rank 4); post-move
    // it is attacked.
    check("7k/8/8/8/r5K1/8/8/8 w - - 0 1", new MoveSpecification(G4, H4),
        MovementCheck.KING_MOVES_TO_ATTACKED_EMPTY_SQUARE);
  }

  @AfterAll
  static void verifyExhaustive() {
    final Set<MovementCheck> missing = EnumSet.allOf(MovementCheck.class);
    missing.removeAll(COVERED);
    if (!missing.isEmpty()) {
      throw new AssertionError("MovementCheck values not covered by tests: " + missing);
    }
  }
}
