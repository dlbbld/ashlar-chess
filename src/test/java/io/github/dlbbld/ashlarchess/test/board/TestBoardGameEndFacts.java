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
import io.github.dlbbld.ashlarchess.common.model.GameEndFacts;

/**
 * Tests for {@link Board#calculateGameEndFacts()} and {@link Board#isGameEnd()} - the rich game-end snapshot that pairs
 * all condition-only facts with the precedence-projected {@link io.github.dlbbld.ashlarchess.common.model.Outcome}.
 * Pins the "facts are independent, outcome is a projection" architecture: multiple facts may be simultaneously true at
 * a single position; the {@code outcome} field reports the python-chess precedence stack's verdict.
 *
 * <p>
 * Sister to {@code TestBasicChessUtility} which pins the precedence rules directly on {@code calculateOutcome}; this
 * test verifies the same precedence flows through the {@code GameEndFacts} snapshot, plus the independence of the raw
 * fact booleans.
 */
class TestBoardGameEndFacts {

  // =============================================================================================
  // Case 1 - checkmate + 75-move both true; outcome.termination is CHECKMATE
  // =============================================================================================

  @SuppressWarnings("static-method")
  @Test
  void checkmateAndSeventyFiveMoveFactsBothTrueOutcomeIsCheckmate() {
    // White K g6, white Q g7 mating black K h8. Halfmove clock 150 - both checkmate and 75-move
    // conditions hold simultaneously. The mating move must have been non-pawn non-capture for the
    // clock to be 150 (otherwise it would have reset).
    final Board board = new Board("7k/6Q1/6K1/8/8/8/8/8 b - - 150 80");
    assertTrue(board.isCheckmate(), "precondition: black is mated");
    assertEquals(150, board.getHalfMoveClock(), "precondition: clock at 75-move threshold");

    final GameEndFacts facts = board.calculateGameEndFacts();
    assertTrue(facts.checkmate(), "checkmate fact must be true");
    assertTrue(facts.seventyFiveMove(), "seventyFiveMove fact must be true - independent of checkmate");
    assertFalse(facts.stalemate());
    assertFalse(facts.insufficientMaterial());
    assertFalse(facts.fivefoldRepetition());

    assertEquals(Termination.CHECKMATE, facts.outcome().termination(),
        "precedence: CHECKMATE outranks SEVENTY_FIVE_MOVES");
    assertEquals(Side.WHITE, facts.outcome().winner(), "white delivered mate");
    assertTrue(facts.isGameEnd());
  }

  // =============================================================================================
  // Case 2 - stalemate + 75-move both true; outcome.termination is STALEMATE
  // =============================================================================================

  @SuppressWarnings("static-method")
  @Test
  void stalemateAndSeventyFiveMoveFactsBothTrueOutcomeIsStalemate() {
    // White Kf7, Ph6. Black Kh8, Ph7. Black to move, stalemated (no king square, h7 pawn blocked
    // by h6). Halfmove clock 150 - both stalemate and 75-move conditions hold.
    final Board board = new Board("7k/5K1p/7P/8/8/8/8/8 b - - 150 80");
    assertTrue(board.isStalemate(), "precondition: black is stalemated");
    assertEquals(150, board.getHalfMoveClock(), "precondition: clock at 75-move threshold");

    final GameEndFacts facts = board.calculateGameEndFacts();
    assertTrue(facts.stalemate(), "stalemate fact must be true");
    assertTrue(facts.seventyFiveMove(), "seventyFiveMove fact must be true - independent of stalemate");
    assertFalse(facts.checkmate());
    assertFalse(facts.insufficientMaterial());

    assertEquals(Termination.STALEMATE, facts.outcome().termination(),
        "precedence: STALEMATE outranks SEVENTY_FIVE_MOVES");
    assertEquals(Side.NONE, facts.outcome().winner(), "stalemate is a draw");
    assertTrue(facts.isGameEnd());
  }

  // =============================================================================================
  // Case 3 - fivefold + 75-move both true; outcome.termination is SEVENTY_FIVE_MOVES
  // =============================================================================================

  @SuppressWarnings("static-method")
  @Test
  void fivefoldAndSeventyFiveMoveFactsBothTrueOutcomeIsSeventyFiveMove() {
    // 38 cycles of "Nf3 Nf6 Ng1 Ng8" from the initial position: 152 plies, clock 152, the initial
    // position has occurred 39 times. Both fivefold and 75-move fire; neither checkmate, stalemate,
    // nor insufficient material applies (full piece complement). Precedence resolves to 75-move.
    final Board board = new Board();
    for (int i = 0; i < 38; i++) {
      board.movesStrict("Nf3", "Nf6", "Ng1", "Ng8");
    }
    assertEquals(152, board.getHalfMoveClock(), "precondition: 38 shuffle cycles -> clock 152");
    assertTrue(board.isFivefoldRepetition(), "precondition: initial position has recurred >= 5 times");

    final GameEndFacts facts = board.calculateGameEndFacts();
    assertTrue(facts.fivefoldRepetition(), "fivefold fact must be true");
    assertTrue(facts.seventyFiveMove(), "seventyFiveMove fact must be true - independent of fivefold");
    assertFalse(facts.checkmate());
    assertFalse(facts.stalemate());
    assertFalse(facts.insufficientMaterial(), "still 32 pieces on the board, not insufficient");

    assertEquals(Termination.SEVENTY_FIVE_MOVES, facts.outcome().termination(),
        "precedence: SEVENTY_FIVE_MOVES outranks FIVEFOLD_REPETITION");
    assertEquals(Side.NONE, facts.outcome().winner());
  }

  // =============================================================================================
  // Case 4 - insufficient material + dead position + fivefold + 75-move; outcome is IM
  // =============================================================================================

  @SuppressWarnings("static-method")
  @Test
  void deadAndInsufficientMaterialFactsBothTrueOutcomeIsInsufficientMaterial() {
    // KvK position: dead AND insufficient. Play 38 cycles of king-shuffle to drive clock past 150
    // and fivefold the starting position. Multiple facts simultaneously true; precedence resolves
    // to INSUFFICIENT_MATERIAL (it outranks 75-move and fivefold).
    final Board board = new Board("4k3/8/8/8/8/8/8/4K3 w - - 0 1");
    for (int i = 0; i < 38; i++) {
      board.movesStrict("Kd2", "Kd8", "Ke1", "Ke8");
    }
    assertEquals(152, board.getHalfMoveClock(), "precondition: 38 king-shuffle cycles -> clock 152");

    final GameEndFacts facts = board.calculateGameEndFacts();
    assertTrue(facts.insufficientMaterial(), "KvK is structurally insufficient");
    assertTrue(facts.deadPosition(), "KvK is also dead under the analyzer (superset of IM)");
    assertTrue(facts.fivefoldRepetition(), "initial position recurs > 5 times across the shuffle");
    assertTrue(facts.seventyFiveMove(), "clock past 150");
    assertFalse(facts.checkmate());
    assertFalse(facts.stalemate());

    assertEquals(Termination.INSUFFICIENT_MATERIAL, facts.outcome().termination(),
        "precedence: INSUFFICIENT_MATERIAL outranks both SEVENTY_FIVE_MOVES and FIVEFOLD_REPETITION");
    assertEquals(Side.NONE, facts.outcome().winner());
  }

  // =============================================================================================
  // Case 5 - no termination condition fires; outcome carries Termination.NONE
  // =============================================================================================

  @SuppressWarnings("static-method")
  @Test
  void noEndAllFactsFalseOutcomeIsOngoing() {
    final Board board = new Board();
    board.movesStrict("e4", "e5", "Nf3", "Nf6");

    final GameEndFacts facts = board.calculateGameEndFacts();
    assertFalse(facts.checkmate());
    assertFalse(facts.stalemate());
    assertFalse(facts.insufficientMaterial());
    assertFalse(facts.deadPosition());
    assertFalse(facts.fivefoldRepetition());
    assertFalse(facts.seventyFiveMove());
    assertEquals(Termination.NONE, facts.outcome().termination(),
        "no termination condition -> outcome.termination is Termination.NONE");
    assertEquals(io.github.dlbbld.ashlarchess.board.enums.Side.NONE, facts.outcome().winner(),
        "ongoing outcome's winner is Side.NONE");
    assertFalse(facts.isGameEnd());
    assertFalse(board.isGameEnd(), "Board.isGameEnd() agrees with the snapshot");
  }
}
