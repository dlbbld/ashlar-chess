// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.report;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import io.github.dlbbld.ashlarchess.board.Board;
import io.github.dlbbld.ashlarchess.common.Nulls;

/**
 * Direct unit tests for {@link FiftyMoveSequenceReportBuilder}: assertions against the {@link FiftyMoveSequenceReport}
 * record returned by the builder. Each sequence carries a {@link SequenceStart} (initial-FEN-anchored or after-reset
 * shape) and an optional {@code endMove}; the tests pin which shape is produced for each canonical case:
 *
 * <ul>
 * <li>Pure played history: after-reset shape (game starts with FEN clock 0; the first non-zeroing move opens the
 * sequence).
 * <li>Initial-FEN-continued: initial-FEN-anchored shape with a played {@code endMove} extending past the threshold.
 * <li>Initial-FEN-already-at-threshold with no continuation: initial-FEN-anchored shape with {@code endMove == null}.
 * <li>Initial-FEN-already-at-threshold with continuation: initial-FEN-anchored shape with a played {@code endMove}.
 * </ul>
 */
class TestFiftyMoveSequenceReportBuilder {

  @SuppressWarnings("static-method")
  @Test
  void emptyWhenNoSequenceReachesThreshold() {
    final Board board = new Board();
    board.movesStrict("e4", "e5", "Nf3", "Nf6");

    final FiftyMoveSequenceReport report = FiftyMoveSequenceReportBuilder.build(board);
    assertEquals(0, report.sequences().size(),
        "no sequence reached the 50-move threshold in this short pawn-and-knight opening");
  }

  // === Pure played history ===

  @SuppressWarnings("static-method")
  @Test
  void purePlayedSequenceReachingThreshold() {
    // 100 moves of knight shuffle from the initial position. FEN clock is 0, so the sequence's
    // start is the after-reset shape, anchored at firstNonZeroingMove. The first move is Nf3 - that's the sequence
    // anchor.
    final Board board = new Board();
    for (int i = 0; i < 25; i++) {
      board.movesStrict("Nf3", "Nf6", "Ng1", "Ng8");
    }
    assertEquals(100, board.getHalfMoveClock(), "precondition: 100-move knight shuffle reaches the threshold");

    final FiftyMoveSequenceReport report = FiftyMoveSequenceReportBuilder.build(board);
    assertEquals(1, report.sequences().size(), "exactly one no-progress sequence in this game");

    final FiftyMoveSequence sequence = Nulls.get(report.sequences(), 0);
    assertFalse(sequence.start().isInitialFen(),
        "pure-played: sequence starts at the first played non-zeroing move, no initial-FEN inheritance");
    assertEquals("Nf3", sequence.start().firstNonZeroingMoveOrThrow().san(),
        "first non-zeroing move is Nf3 (white's first move)");
    assertEquals(1, sequence.start().firstNonZeroingMoveOrThrow().halfMoveClock(),
        "by construction the start move's halfmove clock is 1");
    assertNotNull(sequence.endMove(), "sequence has played continuation - endMove must be present");
    assertEquals(100, sequence.finalClock(), "final clock value at end of sequence equals played-history clock");
  }

  // === Initial-FEN-continued ===

  @SuppressWarnings("static-method")
  @Test
  void initialFenContinuedSequenceReachingThreshold() {
    // Initial FEN clock 50 - half a 50-move run already inherited. Play 50 non-zeroing moves
    // to bring it to 100. The sequence's start is InitialFenStart(50); the endMove is the played
    // move that completed the threshold (and possibly further).
    final Board board = new Board("4k3/8/8/8/8/8/8/R3K3 w - - 50 30");
    assertEquals(50, board.getHalfMoveClock(), "precondition: initial FEN at clock 50");

    for (int i = 0; i < 12; i++) {
      board.movesStrict("Ra3", "Kd8", "Ra1", "Ke8");
    }
    board.movesStrict("Ra3", "Kd8");
    assertEquals(100, board.getHalfMoveClock(), "precondition: clock now at threshold via played continuation");

    final FiftyMoveSequenceReport report = FiftyMoveSequenceReportBuilder.build(board);
    assertEquals(1, report.sequences().size(), "one sequence - initial FEN continued into play");

    final FiftyMoveSequence sequence = Nulls.get(report.sequences(), 0);
    assertTrue(sequence.start().isInitialFen(), "initial-FEN-continued: sequence inherits the FEN's clock");
    assertEquals(50, sequence.start().initialClockValue(),
        "the initial-FEN start carries the FEN's clock value verbatim");
    assertNotNull(sequence.endMove(), "sequence has played continuation - endMove must be present");
    assertEquals(100, sequence.finalClock());
  }

  // === Initial-FEN-already-at-threshold ===

  @SuppressWarnings("static-method")
  @Test
  void initialFenAlreadyAtThresholdWithNoContinuation() {
    // The user-supplied special case. Black queen on b2 caging white's king on a1; white's only
    // legal move is Kxb2, a capture that resets the clock. No non-zeroing continuation possible.
    // The sequence must still appear in the report with endMove == null - the print layer renders
    // only the start marker, e.g. "[Starting position] (100)".
    final Board board = new Board("7k/8/8/8/8/8/1q6/K7 w - - 100 80");
    assertEquals(100, board.getHalfMoveClock(), "precondition: FEN clock already at threshold");
    assertEquals(1, board.getLegalMoves().size(), "precondition: only Kxb2 legal");

    final FiftyMoveSequenceReport report = FiftyMoveSequenceReportBuilder.build(board);
    assertEquals(1, report.sequences().size(),
        "even with no continuation, an at-threshold initial FEN must surface as a sequence");

    final FiftyMoveSequence sequence = Nulls.get(report.sequences(), 0);
    assertTrue(sequence.start().isInitialFen());
    assertEquals(100, sequence.start().initialClockValue());
    assertNull(sequence.endMove(),
        "endMove is null when threshold is met by FEN alone with no non-zeroing continuation");
    assertEquals(100, sequence.finalClock(), "finalClock derives from start when endMove is null");
  }

  @SuppressWarnings("static-method")
  @Test
  void initialFenAlreadyAtThresholdWithContinuation() {
    // Sister case: FEN already at clock 100 AND non-zeroing legal moves available. Sequence's
    // start is InitialFenStart(100); endMove extends past the threshold via the played moves.
    final Board board = new Board("4k3/8/8/8/8/8/8/R3K3 w - - 100 80");
    assertEquals(100, board.getHalfMoveClock(), "precondition: FEN clock already at threshold");

    board.movesStrict("Ra3", "Kd8", "Ra1", "Ke8");

    final FiftyMoveSequenceReport report = FiftyMoveSequenceReportBuilder.build(board);
    assertEquals(1, report.sequences().size(), "still one continuous no-progress sequence");

    final FiftyMoveSequence sequence = Nulls.get(report.sequences(), 0);
    assertTrue(sequence.start().isInitialFen());
    assertEquals(100, sequence.start().initialClockValue());
    assertNotNull(sequence.endMove(), "played moves extend the sequence - endMove must be present");
    assertEquals(104, sequence.finalClock(), "four extra non-zeroing moves extend the clock past 100");
  }

  // === Mid-game reset opening a fresh sequence ===

  @SuppressWarnings("static-method")
  @Test
  void midGameResetOpensFreshSequenceWithAfterResetStart() {
    // Initial FEN clock 50 inherited, but the very first played move is a pawn push (resets the
    // clock). The inherited InitialFenStart sequence ends below threshold and is not reported.
    // Then 100 non-zeroing moves form a fresh sequence with AfterResetStart anchored at the first
    // non-zeroing move after the pawn push. White rook lives on h1 so the rook shuffle does not
    // collide with the white a-pawn after the push.
    final Board board = new Board("4k3/p7/8/8/8/8/P7/4K2R w - - 50 30");
    assertEquals(50, board.getHalfMoveClock(), "precondition: inherited clock 50");

    board.movesStrict("a3"); // White pawn push resets clock
    assertEquals(0, board.getHalfMoveClock(), "precondition: pawn push reset the clock");

    // Black opens the new sequence with a non-zeroing king move (move 1, clock 1).
    board.movesStrict("Kd8");
    // 99 more non-zeroing moves via Rg1/Ke8/Rh1/Kd8 cycle: 24 full cycles (96 moves) + 3 trailing.
    for (int i = 0; i < 24; i++) {
      board.movesStrict("Rg1", "Ke8", "Rh1", "Kd8");
    }
    board.movesStrict("Rg1", "Ke8", "Rh1");
    assertEquals(100, board.getHalfMoveClock(), "precondition: fresh sequence reaches the threshold");

    final FiftyMoveSequenceReport report = FiftyMoveSequenceReportBuilder.build(board);
    assertEquals(1, report.sequences().size(),
        "the inherited-FEN sequence ended below threshold (not reported); the fresh after-reset sequence reaches threshold");

    final FiftyMoveSequence sequence = Nulls.get(report.sequences(), 0);
    assertFalse(sequence.start().isInitialFen(),
        "mid-game start: after-reset start anchored at the first non-zeroing move after the pawn push");
    assertEquals("Kd8", sequence.start().firstNonZeroingMoveOrThrow().san(),
        "first non-zeroing move after the pawn push is Kd8");
    assertEquals(100, sequence.finalClock());
  }
}
