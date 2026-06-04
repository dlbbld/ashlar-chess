// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.test.board;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import io.github.dlbbld.ashlarchess.board.Board;
import io.github.dlbbld.ashlarchess.board.enums.Side;
import io.github.dlbbld.ashlarchess.common.enums.Termination;
import io.github.dlbbld.ashlarchess.common.model.Outcome;
import io.github.dlbbld.ashlarchess.common.utility.BasicChessUtility;
import io.github.dlbbld.ashlarchess.unwinnability.UnwinnabilityQuickVerdict;
import io.github.dlbbld.ashlarchess.unwinnability.UnwinnableQuickAnalyzer;

/**
 * Game-end semantics. The raw rule predicates on {@link Board} are independent - several may be true at one position -
 * while {@link BasicChessUtility#calculateOutcome(Board)} projects them through the python-chess precedence stack into a
 * single {@link Outcome}. These tests pin that independence and the projection together.
 *
 * <p>
 * {@code calculateOutcome} stays cheap and never invokes a CHA / dead-position analyzer; the analyzer-driven dead
 * position is a separate query via the no-side {@link UnwinnableQuickAnalyzer#unwinnableQuick(Board)} overload
 * ({@code UNWINNABLE} = dead) and is deliberately not a {@link Termination}.
 */
class TestBoardGameEndOutcome {

  // Case 1 - checkmate + 75-move both true; outcome.termination is CHECKMATE.

  @SuppressWarnings("static-method")
  @Test
  void checkmateAndSeventyFiveMoveFactsBothTrueOutcomeIsCheckmate() {
    // White K g6, white Q g7 mating black K h8. Halfmove clock 150 - both checkmate and 75-move conditions hold. The
    // mating move must have been non-pawn non-capture for the clock to be 150 (otherwise it would have reset).
    final Board board = new Board("7k/6Q1/6K1/8/8/8/8/8 b - - 150 80");
    assertEquals(150, board.getHalfMoveClock(), "precondition: clock at 75-move threshold");

    assertTrue(board.isCheckmate(), "checkmate fact must be true");
    assertTrue(board.isSeventyFiveMove(), "seventyFiveMove fact must be true - independent of checkmate");
    assertFalse(board.isStalemate());
    assertFalse(board.isInsufficientMaterial());
    assertFalse(board.isFivefoldRepetition());

    final Outcome outcome = BasicChessUtility.calculateOutcome(board);
    assertEquals(Termination.CHECKMATE, outcome.termination(), "precedence: CHECKMATE outranks SEVENTY_FIVE_MOVES");
    assertEquals(Side.WHITE, outcome.winner(), "white delivered mate");
    assertTrue(outcome.termination() != Termination.NONE);
  }

  // Case 2 - stalemate + 75-move both true; outcome.termination is STALEMATE.

  @SuppressWarnings("static-method")
  @Test
  void stalemateAndSeventyFiveMoveFactsBothTrueOutcomeIsStalemate() {
    // White Kf7, Ph6. Black Kh8, Ph7. Black to move, stalemated (no king square, h7 pawn blocked by h6). Halfmove
    // clock 150 - both stalemate and 75-move conditions hold.
    final Board board = new Board("7k/5K1p/7P/8/8/8/8/8 b - - 150 80");
    assertEquals(150, board.getHalfMoveClock(), "precondition: clock at 75-move threshold");

    assertTrue(board.isStalemate(), "stalemate fact must be true");
    assertTrue(board.isSeventyFiveMove(), "seventyFiveMove fact must be true - independent of stalemate");
    assertFalse(board.isCheckmate());
    assertFalse(board.isInsufficientMaterial());

    final Outcome outcome = BasicChessUtility.calculateOutcome(board);
    assertEquals(Termination.STALEMATE, outcome.termination(), "precedence: STALEMATE outranks SEVENTY_FIVE_MOVES");
    assertEquals(Side.NONE, outcome.winner(), "stalemate is a draw");
    assertTrue(outcome.termination() != Termination.NONE);
  }

  // Case 3 - fivefold + 75-move both true; outcome.termination is SEVENTY_FIVE_MOVES.

  @SuppressWarnings("static-method")
  @Test
  void fivefoldAndSeventyFiveMoveFactsBothTrueOutcomeIsSeventyFiveMove() {
    // 38 cycles of "Nf3 Nf6 Ng1 Ng8" from the initial position: 152 plies, clock 152, the initial position has occurred
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

    final Outcome outcome = BasicChessUtility.calculateOutcome(board);
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
    final Board board = new Board("4k3/8/8/8/8/8/8/4K3 w - - 0 1");
    for (int i = 0; i < 38; i++) {
      board.movesStrict("Kd2", "Kd8", "Ke1", "Ke8");
    }
    assertEquals(152, board.getHalfMoveClock(), "precondition: 38 king-shuffle cycles -> clock 152");

    assertTrue(board.isInsufficientMaterial(), "KvK is structurally insufficient");
    assertEquals(UnwinnabilityQuickVerdict.UNWINNABLE, UnwinnableQuickAnalyzer.unwinnableQuick(board),
        "KvK is also dead under the analyzer (superset of insufficient material)");
    assertTrue(board.isFivefoldRepetition(), "initial position recurs > 5 times across the shuffle");
    assertTrue(board.isSeventyFiveMove(), "clock past 150");
    assertFalse(board.isCheckmate());
    assertFalse(board.isStalemate());

    final Outcome outcome = BasicChessUtility.calculateOutcome(board);
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
    assertEquals(UnwinnabilityQuickVerdict.POSSIBLY_WINNABLE, UnwinnableQuickAnalyzer.unwinnableQuick(board),
        "ongoing opening position is not dead");
    assertFalse(board.isFivefoldRepetition());
    assertFalse(board.isSeventyFiveMove());

    final Outcome outcome = BasicChessUtility.calculateOutcome(board);
    assertEquals(Termination.NONE, outcome.termination(),
        "no termination condition -> outcome.termination is Termination.NONE");
    assertEquals(Side.NONE, outcome.winner(), "ongoing outcome's winner is Side.NONE");
    assertEquals(Termination.NONE, outcome.termination(), "game has not ended");
  }
}
