package com.dlb.chess.report;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.dlb.chess.board.Board;

/**
 * Direct unit tests for {@link FiftyMoveClaimAheadReportBuilder}: assertions against the
 * {@link FiftyMoveClaimAheadReport} record returned by the builder. Mirrors the threefold claim-ahead test set in
 * shape; the per-move predicate {@code canClaimFiftyMoveRuleFor} is the single source of truth, so any future change
 * to that predicate is automatically reflected here.
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
    // King + rook shuffle from initial position to clock 99; black to move at the boundary.
    // Initial FEN clock 0, so we drive the clock up via plays. Use a no-castling-rights FEN to
    // make the shuffle stable cycle-over-cycle.
    final Board board = new Board("4k3/8/8/8/8/8/8/R3K3 w - - 0 30");
    // 99 plies of shuffle. Build them as 24 full cycles (96 plies) plus 3 extras.
    for (var i = 0; i < 24; i++) {
      board.movesStrict("Ra3", "Kd8", "Ra1", "Ke8");
    }
    board.movesStrict("Ra3", "Kd8", "Ra1");
    assertEquals(99, board.getHalfMoveClock(), "precondition: clock at 99");

    final FiftyMoveClaimAheadReport report = FiftyMoveClaimAheadReportBuilder.build(board);
    assertTrue(report.entries().size() >= 1,
        "at clock 99 with non-zeroing legal moves available, the claim-ahead report must list at least one entry");

    // Entries must be ordered by claimAheadMove.halfMoveCount() — the canonical builder sort.
    for (var i = 1; i < report.entries().size(); i++) {
      final int prev = report.entries().get(i - 1).claimAheadMove().halfMoveCount();
      final int curr = report.entries().get(i).claimAheadMove().halfMoveCount();
      assertTrue(prev <= curr, "entries must be sorted by claimAheadMove halfMoveCount");
    }
  }

  @SuppressWarnings("static-method")
  @Test
  void initialFenAlreadyAtThresholdWithContinuationHasClaimAheads() {
    // Initial FEN at clock 100 + non-zeroing legal moves available. The per-move predicate accepts
    // any non-zeroing legal move at clock >= 99, so claim-ahead opportunities exist at the initial
    // position even though the threshold is already met.
    final Board board = new Board("4k3/8/8/8/8/8/8/R3K3 w - - 100 80");
    assertEquals(100, board.getHalfMoveClock(), "precondition: initial FEN already at threshold");

    final FiftyMoveClaimAheadReport report = FiftyMoveClaimAheadReportBuilder.build(board);
    assertTrue(report.entries().size() >= 1,
        "non-zeroing legal moves at clock >= 99 yield claim-ahead entries even when threshold is already met");
  }

  @SuppressWarnings("static-method")
  @Test
  void initialFenAlreadyAtThresholdWithNoContinuationHasNoClaimAheads() {
    // The user's special case: initial FEN at clock 100, only legal move is Kxb2 (a capture).
    // Kxb2 is clock-resetting, so canClaimFiftyMoveRuleFor returns false. No claim-ahead entries —
    // the situation is "claim now or never", not "claim ahead". The 50-move sequence report
    // surfaces the threshold-met state; the claim-ahead report is correctly empty.
    final Board board = new Board("7k/8/8/8/8/8/1q6/K7 w - - 100 80");
    assertEquals(100, board.getHalfMoveClock(), "precondition: initial FEN already at threshold");
    assertEquals(1, board.getLegalMoves().size(), "precondition: only Kxb2 legal");

    final FiftyMoveClaimAheadReport report = FiftyMoveClaimAheadReportBuilder.build(board);
    assertEquals(0, report.entries().size(),
        "only legal move is a capture (clock-resetting) — no per-move claim-ahead available");
  }

  @SuppressWarnings("static-method")
  @Test
  void claimAheadHasBeenPlayedMarkedWhenMovedThroughThreshold() {
    // Same shuffle as the playedHistory test, but play one MORE non-zeroing move so the clock
    // crosses 99 -> 100 in the played history. The claim-ahead detected at the prior ply (clock
    // 99) now matches a played HalfMove -> hasBeenPlayed == true.
    final Board board = new Board("4k3/8/8/8/8/8/8/R3K3 w - - 0 30");
    for (var i = 0; i < 25; i++) {
      board.movesStrict("Ra3", "Kd8", "Ra1", "Ke8");
    }
    assertEquals(100, board.getHalfMoveClock(), "precondition: 100 plies of shuffle reach threshold");

    final FiftyMoveClaimAheadReport report = FiftyMoveClaimAheadReportBuilder.build(board);
    assertTrue(report.entries().size() >= 1, "claim-aheads exist at clock 99 prior to the crossing move");

    var anyHasBeenPlayed = false;
    for (final FiftyMoveClaimAheadEntry entry : report.entries()) {
      if (entry.hasBeenPlayed()) {
        anyHasBeenPlayed = true;
        break;
      }
    }
    assertTrue(anyHasBeenPlayed,
        "at least one claim-ahead entry must have hasBeenPlayed == true (the move that actually crossed the threshold)");
  }
}
