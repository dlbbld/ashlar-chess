// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.test.board;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import io.github.dlbbld.ashlarchess.board.Board;
import io.github.dlbbld.ashlarchess.board.enums.Side;
import io.github.dlbbld.ashlarchess.common.enums.Termination;
import io.github.dlbbld.ashlarchess.common.model.Outcome;
import io.github.dlbbld.ashlarchess.unwinnability.DeadPositionQuickVerdict;

/**
 * Game-end semantics. The raw rule predicates on {@link Board} are independent - several may be true at one position -
 * while {@link Board#outcome()} projects them through the python-chess precedence stack into
 * a single {@link Outcome}. These tests pin that independence and the projection together.
 *
 * <p>
 * {@code outcome()} stays cheap and never invokes a CHA / dead-position analyzer; the analyzer-driven dead
 * position is a separate query via {@link Board#deadPositionQuick()} ({@code DEAD} = dead) and is deliberately not a
 * {@link Termination}.
 */
class TestBoardGameEndOutcome {

  // Case 1 - checkmate + 75-move both true; outcome.termination is CHECKMATE.

  @SuppressWarnings("static-method")
  @Test
  void checkmateAndSeventyFiveMoveFactsBothTrueOutcomeIsCheckmate() {
    // White K g6, white Q g7 mating black K h8. Halfmove clock 150 - both checkmate and 75-move conditions hold. The
    // mating move must have been non-pawn non-capture for the clock to be 150 (otherwise it would have reset).
    final Board board = Board.fromFenStrict("7k/6Q1/6K1/8/8/8/8/8 b - - 150 80");
    assertEquals(150, board.getHalfMoveClock(), "precondition: clock at 75-move threshold");

    assertTrue(board.isCheckmate(), "checkmate fact must be true");
    assertTrue(board.isSeventyFiveMove(), "seventyFiveMove fact must be true - independent of checkmate");
    assertFalse(board.isStalemate());
    assertFalse(board.isInsufficientMaterial());
    assertFalse(board.isFivefoldRepetition());

    final Outcome outcome = board.outcome();
    assertEquals(Termination.CHECKMATE, outcome.termination(), "precedence: CHECKMATE outranks SEVENTY_FIVE_MOVES");
    assertEquals(Side.WHITE, outcome.winner(), "white delivered mate");
    assertNotEquals(Termination.NONE, outcome.termination());
  }

  // Case 2 - stalemate + 75-move both true; outcome.termination is STALEMATE.

  @SuppressWarnings("static-method")
  @Test
  void stalemateAndSeventyFiveMoveFactsBothTrueOutcomeIsStalemate() {
    // White Kf7, Ph6. Black Kh8, Ph7. Black to move, stalemated (no king square, h7 pawn blocked by h6). Halfmove
    // clock 150 - both stalemate and 75-move conditions hold.
    final Board board = Board.fromFenStrict("7k/5K1p/7P/8/8/8/8/8 b - - 150 80");
    assertEquals(150, board.getHalfMoveClock(), "precondition: clock at 75-move threshold");

    assertTrue(board.isStalemate(), "stalemate fact must be true");
    assertTrue(board.isSeventyFiveMove(), "seventyFiveMove fact must be true - independent of stalemate");
    assertFalse(board.isCheckmate());
    assertFalse(board.isInsufficientMaterial());

    final Outcome outcome = board.outcome();
    assertEquals(Termination.STALEMATE, outcome.termination(), "precedence: STALEMATE outranks SEVENTY_FIVE_MOVES");
    assertEquals(Side.NONE, outcome.winner(), "stalemate is a draw");
    assertNotEquals(Termination.NONE, outcome.termination());
  }

  // Case 3 - fivefold + 75-move both true; outcome.termination is SEVENTY_FIVE_MOVES.

  @SuppressWarnings("static-method")
  @Test
  void fivefoldAndSeventyFiveMoveFactsBothTrueOutcomeIsSeventyFiveMove() {
    // 38 cycles of "Nf3 Nf6 Ng1 Ng8" from the initial position: 152 moves, clock 152, the initial position has occurred
    // 39 times. Both fivefold and 75-move fire; neither checkmate, stalemate, nor insufficient material applies (full
    // piece complement). Precedence resolves to 75-move.
    final Board board = new Board();
    for (int i = 0; i < 38; i++) {
      board.movesStrict("Nf3", "Nf6", "Ng1", "Ng8");
    }
    assertEquals(152, board.getHalfMoveClock(), "precondition: 38 shuffle cycles -> clock 152");

    assertTrue(board.isFivefoldRepetition(), "fivefold fact must be true");
    assertTrue(board.isSeventyFiveMove(), "seventyFiveMove fact must be true - independent of fivefold");
    assertFalse(board.isCheckmate());
    assertFalse(board.isStalemate());
    assertFalse(board.isInsufficientMaterial(), "still 32 pieces on the board, not insufficient");

    final Outcome outcome = board.outcome();
    assertEquals(Termination.SEVENTY_FIVE_MOVES, outcome.termination(),
        "precedence: SEVENTY_FIVE_MOVES outranks FIVEFOLD_REPETITION");
    assertEquals(Side.NONE, outcome.winner());
  }

  // Case 4 - insufficient material + dead position + fivefold + 75-move; outcome is INSUFFICIENT_MATERIAL.

  @SuppressWarnings("static-method")
  @Test
  void deadAndInsufficientMaterialFactsBothTrueOutcomeIsInsufficientMaterial() {
    // KvK position: dead AND insufficient. Play 38 cycles of king-shuffle to drive the clock past 150 and fivefold the
    // starting position. Multiple facts simultaneously true; precedence resolves to INSUFFICIENT_MATERIAL (it outranks
    // 75-move and fivefold).
    final Board board = Board.fromFenStrict("4k3/8/8/8/8/8/8/4K3 w - - 0 1");
    for (int i = 0; i < 38; i++) {
      board.movesStrict("Kd2", "Kd8", "Ke1", "Ke8");
    }
    assertEquals(152, board.getHalfMoveClock(), "precondition: 38 king-shuffle cycles -> clock 152");

    assertTrue(board.isInsufficientMaterial(), "KvK is structurally insufficient");
    assertEquals(DeadPositionQuickVerdict.DEAD, board.deadPositionQuick(),
        "KvK is also dead under the analyzer (superset of insufficient material)");
    assertTrue(board.isFivefoldRepetition(), "initial position recurs > 5 times across the shuffle");
    assertTrue(board.isSeventyFiveMove(), "clock past 150");
    assertFalse(board.isCheckmate());
    assertFalse(board.isStalemate());

    final Outcome outcome = board.outcome();
    assertEquals(Termination.INSUFFICIENT_MATERIAL, outcome.termination(),
        "precedence: INSUFFICIENT_MATERIAL outranks both SEVENTY_FIVE_MOVES and FIVEFOLD_REPETITION");
    assertEquals(Side.NONE, outcome.winner());
  }

  // Case 5 - no termination condition fires; outcome carries Termination.NONE.

  @SuppressWarnings("static-method")
  @Test
  void noEndAllFactsFalseOutcomeIsOngoing() {
    final Board board = new Board();
    board.movesStrict("e4", "e5", "Nf3", "Nf6");

    assertFalse(board.isCheckmate());
    assertFalse(board.isStalemate());
    assertFalse(board.isInsufficientMaterial());
    assertEquals(DeadPositionQuickVerdict.POSSIBLY_ALIVE, board.deadPositionQuick(),
        "ongoing opening position is not dead");
    assertFalse(board.isFivefoldRepetition());
    assertFalse(board.isSeventyFiveMove());

    final Outcome outcome = board.outcome();
    assertEquals(Termination.NONE, outcome.termination(),
        "no termination condition -> outcome.termination is Termination.NONE");
    assertEquals(Side.NONE, outcome.winner(), "ongoing outcome's winner is Side.NONE");
    assertEquals(Termination.NONE, outcome.termination(), "game has not ended");
  }

  // Single-condition cases: each isolates one termination so the precedence stack resolves it without an overlapping
  // condition.

  @SuppressWarnings("static-method")
  @Test
  void insufficientMaterialBeatsSeventyFiveMoveWithNoOtherCondition() {
    // KvK (insufficient material) with the clock at the 75-move threshold; IM outranks 75-move.
    final Board board = Board.fromFenStrict("4k3/8/8/8/8/8/8/4K3 w - - 150 76");
    assertTrue(board.isInsufficientMaterial(), "precondition: insufficient material");
    assertTrue(board.isSeventyFiveMove(), "precondition: 75-move threshold reached");
    assertEquals(new Outcome(Termination.INSUFFICIENT_MATERIAL, Side.NONE), board.outcome());
  }

  @SuppressWarnings("static-method")
  @Test
  void fivefoldFiresWhenItIsTheOnlyCondition() {
    // Four "Nf3 Nf6 Ng1 Ng8" shuffle cycles: fivefold fires, but the clock (16) is below the 75-move threshold.
    final Board board = new Board();
    board.movesStrict("Nf3", "Nf6", "Ng1", "Ng8", "Nf3", "Nf6", "Ng1", "Ng8", "Nf3", "Nf6", "Ng1", "Ng8", "Nf3", "Nf6",
        "Ng1", "Ng8");
    assertTrue(board.isFivefoldRepetition(), "precondition: fivefold threshold reached");
    assertFalse(board.isSeventyFiveMove(), "precondition: clock below the 75-move threshold");
    assertEquals(new Outcome(Termination.FIVEFOLD_REPETITION, Side.NONE), board.outcome());
  }

  @SuppressWarnings("static-method")
  @Test
  void seventyFiveMoveFiresWhenItIsTheOnlyCondition() {
    // Clock at the 75-move threshold with enough material (not insufficient) and no repetition.
    final Board board = Board.fromFenStrict("4k3/8/4P3/8/8/8/2N1B3/3KQ2R w - - 150 76");
    assertTrue(board.isSeventyFiveMove(), "precondition: 75-move threshold reached");
    assertFalse(board.isInsufficientMaterial(), "precondition: not insufficient material");
    assertEquals(new Outcome(Termination.SEVENTY_FIVE_MOVES, Side.NONE), board.outcome());
  }
}
