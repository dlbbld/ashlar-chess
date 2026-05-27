package com.dlb.chess.report;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.dlb.chess.board.Board;

/**
 * Direct unit tests for {@link FiftyMoveClaimAheadReportBuilder}: assertions against the
 * {@link FiftyMoveClaimAheadReport} record. Each entry carries a {@link SequenceStart}, a {@code claimAheadMove}, and
 * a {@code hasBeenPlayed} flag; the predicate {@code canClaimFiftyMoveRuleFor} is the single source of truth for which
 * candidate moves are admitted, so any future tightening of FIDE 9.3 semantics is automatically reflected here.
 */
class TestFiftyMoveClaimAheadReportBuilder {

  @SuppressWarnings("static-method")
  @Test
  void emptyWhenClockNeverApproachesThreshold() {
    final Board board = new Board();
    board.movesStrict("e4", "e5", "Nf3", "Nf6");

    final FiftyMoveClaimAheadReport report = FiftyMoveClaimAheadReportBuilder.build(board);
    assertEquals(0, report.entries().size(), "clock never reaches 99 in this short opening");
  }

  @SuppressWarnings("static-method")
  @Test
  void claimAheadEntriesAtClock99FromPlayedHistory() {
    // King + rook shuffle from clock 0; 99 plies of shuffle drive the clock to 99. The first ply
    // of the shuffle (Ra3) opens the sequence as AfterResetStart. At clock 99 White has multiple
    // non-zeroing legal moves; every one of them yields a claim-ahead entry attributed to the
    // AfterResetStart sequence.
    final Board board = new Board("4k3/8/8/8/8/8/8/R3K3 w - - 0 30");
    for (var i = 0; i < 24; i++) {
      board.movesStrict("Ra3", "Kd8", "Ra1", "Ke8");
    }
    board.movesStrict("Ra3", "Kd8", "Ra1");
    assertEquals(99, board.getHalfMoveClock(), "precondition: clock at 99");

    final FiftyMoveClaimAheadReport report = FiftyMoveClaimAheadReportBuilder.build(board);
    assertTrue(report.entries().size() >= 1,
        "at clock 99 with non-zeroing legal moves available, at least one claim-ahead entry must exist");

    // Every entry shares the same AfterResetStart anchor — the sequence started with the very first
    // move of the shuffle (Ra3, halfMoveClock 1).
    for (final FiftyMoveClaimAheadEntry entry : report.entries()) {
      assertTrue(entry.sequenceStart() instanceof AfterResetStart,
          "FEN clock was 0 — sequence-start is AfterResetStart");
      assertEquals("Ra3", ((AfterResetStart) entry.sequenceStart()).firstNonZeroingMove().san(),
          "the AfterResetStart anchor is the first non-zeroing ply (white's Ra3)");
    }

    // Sort invariant: entries ordered by (sequenceStart-anchor, claimAheadMove.halfMoveCount).
    // Since all entries share one sequence-start anchor here, the secondary key alone is monotone.
    for (var i = 1; i < report.entries().size(); i++) {
      final int prev = report.entries().get(i - 1).claimAheadMove().halfMoveCount();
      final int curr = report.entries().get(i).claimAheadMove().halfMoveCount();
      assertTrue(prev <= curr,
          "within the same sequence-start anchor, entries are ordered by claimAheadMove halfMoveCount");
    }
  }

  @SuppressWarnings("static-method")
  @Test
  void initialFenAlreadyAtThresholdWithContinuationHasClaimAheads() {
    // Initial FEN clock 100; non-zeroing legal moves available. Predicate accepts at clock >= 99,
    // so claim-ahead entries exist from the very first ply. Sequence-start is InitialFenStart(100).
    final Board board = new Board("4k3/8/8/8/8/8/8/R3K3 w - - 100 80");
    assertEquals(100, board.getHalfMoveClock(), "precondition: FEN already at threshold");

    final FiftyMoveClaimAheadReport report = FiftyMoveClaimAheadReportBuilder.build(board);
    assertTrue(report.entries().size() >= 1,
        "non-zeroing legal moves at clock >= 99 yield claim-ahead entries even when threshold is already met");

    for (final FiftyMoveClaimAheadEntry entry : report.entries()) {
      assertTrue(entry.sequenceStart() instanceof InitialFenStart,
          "FEN clock > 0 with no reset yet — sequence-start must be InitialFenStart");
      assertEquals(100, ((InitialFenStart) entry.sequenceStart()).initialClockValue(),
          "InitialFenStart carries the FEN's clock value");
    }
  }

  @SuppressWarnings("static-method")
  @Test
  void initialFenAlreadyAtThresholdWithNoContinuationHasNoClaimAheads() {
    // User's special case: FEN clock 100, only legal move is Kxb2 (capture). Kxb2 is clock-
    // resetting, so canClaimFiftyMoveRuleFor returns false. No per-move claim-ahead is available
    // (the situation is "claim now or never", not "claim ahead"). The 50-move sequence report
    // surfaces the threshold-met state via the start marker; the claim-ahead report is correctly
    // empty.
    final Board board = new Board("7k/8/8/8/8/8/1q6/K7 w - - 100 80");
    assertEquals(100, board.getHalfMoveClock(), "precondition: FEN already at threshold");
    assertEquals(1, board.getLegalMoves().size(), "precondition: only Kxb2 legal");

    final FiftyMoveClaimAheadReport report = FiftyMoveClaimAheadReportBuilder.build(board);
    assertEquals(0, report.entries().size(),
        "only legal move is a capture (clock-resetting) — no per-move claim-ahead available");
  }

  @SuppressWarnings("static-method")
  @Test
  void claimAheadHasBeenPlayedMarkedWhenMovedThroughThreshold() {
    // 100-ply shuffle reaching clock 100. The claim-aheads detected at the prior ply (clock 99)
    // include the move that the player actually played; that one entry has hasBeenPlayed == true.
    final Board board = new Board("4k3/8/8/8/8/8/8/R3K3 w - - 0 30");
    for (var i = 0; i < 25; i++) {
      board.movesStrict("Ra3", "Kd8", "Ra1", "Ke8");
    }
    assertEquals(100, board.getHalfMoveClock(), "precondition: 100 plies of shuffle reach threshold");

    final FiftyMoveClaimAheadReport report = FiftyMoveClaimAheadReportBuilder.build(board);
    assertTrue(report.entries().size() >= 1, "claim-aheads exist at clock 99 prior to the crossing move");

    var hasBeenPlayedCount = 0;
    for (final FiftyMoveClaimAheadEntry entry : report.entries()) {
      if (entry.hasBeenPlayed()) {
        hasBeenPlayedCount++;
      }
    }
    assertTrue(hasBeenPlayedCount >= 1,
        "at least one claim-ahead entry must have hasBeenPlayed == true (the move that actually crossed the threshold)");
  }
}
