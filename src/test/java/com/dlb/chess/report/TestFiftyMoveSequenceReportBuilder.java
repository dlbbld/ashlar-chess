package com.dlb.chess.report;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.dlb.chess.board.Board;

/**
 * Direct unit tests for {@link FiftyMoveSequenceReportBuilder}: assertions against the {@link FiftyMoveSequenceReport}
 * record returned by the builder. Covers the three shape cases:
 *
 * <ul>
 * <li>Pure played history (no initial-FEN contribution)
 * <li>Initial-FEN-continued (initial FEN had partial clock, threshold reached during play)
 * <li>Initial-FEN-at-threshold (initial FEN already at clock 100), with two sub-cases: no continuation possible, and
 * with continuation
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

  // === Case A — pure played history ===

  @SuppressWarnings("static-method")
  @Test
  void purePlayedSequenceReachingThreshold() {
    // 100 plies of knight shuffle from the initial position -> halfmove clock 100 at the end of
    // the played history. Initial FEN's clock is 0, so the sequence starts at the first played
    // halfmove.
    final Board board = new Board();
    for (var i = 0; i < 25; i++) {
      board.movesStrict("Nf3", "Nf6", "Ng1", "Ng8");
    }
    assertEquals(100, board.getHalfMoveClock(), "precondition: 100-ply knight shuffle reaches the threshold");

    final FiftyMoveSequenceReport report = FiftyMoveSequenceReportBuilder.build(board);
    assertEquals(1, report.sequences().size(), "exactly one no-progress sequence in this game");

    final FiftyMoveSequence sequence = report.sequences().get(0);
    assertEquals(false, sequence.includesInitialFen(),
        "case A: sequence starts at the first played halfmove, no initial-FEN contribution");
    assertEquals(false, sequence.thresholdReachedDuringInitialFen(),
        "case A: initial FEN clock was 0, well below threshold");
    assertEquals(100, sequence.finalSequenceLength(), "sequence length at end equals the clock value");
  }

  // === Case B — initial-FEN-continued ===

  @SuppressWarnings("static-method")
  @Test
  void initialFenContinuedSequenceReachingThreshold() {
    // Initial FEN has halfmove clock 50 — half a 50-move rule run already accumulated, threshold
    // not yet reached. Play 50 non-zeroing halfmoves to bring it to 100. The sequence's first
    // marker is before-the-first-played-halfmove (initial FEN contribution), but the threshold
    // itself is reached at a played halfmove.
    final Board board = new Board("4k3/8/8/8/8/8/8/R3K3 w - - 50 30");
    assertEquals(50, board.getHalfMoveClock(), "precondition: initial FEN at clock 50");

    // 50 non-zeroing plies: 12 full rook+king shuffles (48 plies) + 2 extra plies.
    for (var i = 0; i < 12; i++) {
      board.movesStrict("Ra3", "Kd8", "Ra1", "Ke8");
    }
    board.movesStrict("Ra3", "Kd8");
    assertEquals(100, board.getHalfMoveClock(), "precondition: clock now at threshold via played continuation");

    final FiftyMoveSequenceReport report = FiftyMoveSequenceReportBuilder.build(board);
    assertEquals(1, report.sequences().size(), "one sequence — initial FEN continued into play");

    final FiftyMoveSequence sequence = report.sequences().get(0);
    assertEquals(true, sequence.includesInitialFen(),
        "case B: sequence starts before the first played halfmove (initial FEN had partial clock)");
    assertEquals(false, sequence.thresholdReachedDuringInitialFen(),
        "case B: initial FEN's clock was 50 — below threshold, reached during play");
    assertEquals(100, sequence.finalSequenceLength());
  }

  // === Case C — initial-FEN-at-threshold ===

  @SuppressWarnings("static-method")
  @Test
  void initialFenAlreadyAtThresholdWithNoContinuation() {
    // User-supplied special case: black queen on b2 caging the white king on a1; white's only
    // legal move is Kxb2, which is a CAPTURE -> resets the clock. No continuation past the
    // threshold. The 50-move rule has already been met by the initial FEN itself (halfmove clock
    // 100), and the report must surface this as a sequence even with empty halfmove history.
    final Board board = new Board("7k/8/8/8/8/8/1q6/K7 w - - 100 80");
    assertEquals(100, board.getHalfMoveClock(), "precondition: initial FEN's clock already at threshold");
    assertEquals(1, board.getLegalMoves().size(), "precondition: only one legal move (Kxb2, a capture)");

    final FiftyMoveSequenceReport report = FiftyMoveSequenceReportBuilder.build(board);
    assertEquals(1, report.sequences().size(),
        "the initial-FEN-already-at-threshold case must surface as a sequence even with no continuation");

    final FiftyMoveSequence sequence = report.sequences().get(0);
    assertEquals(true, sequence.includesInitialFen());
    assertEquals(true, sequence.thresholdReachedDuringInitialFen(),
        "case C: initial FEN's clock alone met the threshold (100 >= 100)");
    assertEquals(100, sequence.finalSequenceLength(),
        "sequence length equals the initial FEN's halfmove clock — no continuation possible from this position");
  }

  @SuppressWarnings("static-method")
  @Test
  void initialFenAlreadyAtThresholdWithContinuation() {
    // Sister case to the above: initial FEN already at clock 100 AND non-zeroing legal moves are
    // available, so play continues past the threshold. King + rook vs king with the rook free to
    // shuffle. The sequence still flags thresholdReachedDuringInitialFen because the threshold
    // was met by the initial FEN itself; the played halfmoves just extend it.
    final Board board = new Board("4k3/8/8/8/8/8/8/R3K3 w - - 100 80");
    assertEquals(100, board.getHalfMoveClock(), "precondition: initial FEN's clock already at threshold");

    board.movesStrict("Ra3", "Kd8", "Ra1", "Ke8");

    final FiftyMoveSequenceReport report = FiftyMoveSequenceReportBuilder.build(board);
    assertEquals(1, report.sequences().size(), "still one continuous no-progress sequence");

    final FiftyMoveSequence sequence = report.sequences().get(0);
    assertEquals(true, sequence.includesInitialFen());
    assertEquals(true, sequence.thresholdReachedDuringInitialFen(),
        "case C: threshold met by initial FEN, even though play extends the sequence further");
    assertTrue(sequence.finalSequenceLength() > 100,
        "sequence length grew past 100 due to the four extra non-zeroing plies; got "
            + sequence.finalSequenceLength());
  }
}
