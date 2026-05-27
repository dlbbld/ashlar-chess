package com.dlb.chess.report;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.dlb.chess.board.Board;

/**
 * Direct unit tests for {@link FiftyMoveClaimAheadReportBuilder} under the missed-opportunity filter: each
 * {@link FiftyMoveClaimAheadEntry} represents a non-zeroing legal move at the clock-99 boundary of a sequence that did
 * not actually reach the 50-move-rule threshold in the played history. Sequences that did reach the threshold are
 * surfaced only by {@link FiftyMoveSequenceReportBuilder}; their would-be claim-aheads are intentionally suppressed.
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
  void claimAheadEntriesAtFinalPositionWhenGameEndsAtClock99() {
    // 99 plies of rook+king shuffle from clock 0 → game ends at clock 99 with no further play.
    // Under the missed-opportunity filter, the boundary ply at clock 99 with no further played move
    // emits entries for every non-zeroing legal move that the predicate accepts.
    final Board board = new Board("4k3/8/8/8/8/8/8/R3K3 w - - 0 30");
    for (var i = 0; i < 24; i++) {
      board.movesStrict("Ra3", "Kd8", "Ra1", "Ke8");
    }
    board.movesStrict("Ra3", "Kd8", "Ra1");
    assertEquals(99, board.getHalfMoveClock(), "precondition: clock at 99, game ends here");

    final FiftyMoveClaimAheadReport report = FiftyMoveClaimAheadReportBuilder.build(board);
    assertTrue(report.entries().size() >= 1,
        "game ends at clock 99 without reaching threshold — every non-zeroing legal move at the final position is a missed opportunity");

    // All entries belong to the same AfterResetStart anchor (the shuffle's first ply, Ra3 at
    // halfMoveClock 1), and every claimAheadMove has post-move clock exactly 100 by predicate.
    for (final FiftyMoveClaimAheadEntry entry : report.entries()) {
      assertTrue(entry.sequenceStart() instanceof AfterResetStart);
      assertEquals("Ra3", ((AfterResetStart) entry.sequenceStart()).firstNonZeroingMove().san());
      assertEquals(100, entry.claimAheadMove().halfMoveClock(),
          "by predicate construction, the candidate must bring the clock to exactly 100");
    }
  }

  @SuppressWarnings("static-method")
  @Test
  void emptyWhenSequenceReachesThreshold() {
    // Same fixture as the test above, but play one MORE non-zeroing move so clock crosses 99 → 100
    // in the played history. The sequence reaches the threshold; under the missed-opportunity
    // filter the claim-aheads at clock 99 are NOT emitted — they're informationally redundant with
    // the "Fifty moves and beyond" sequence row.
    final Board board = new Board("4k3/8/8/8/8/8/8/R3K3 w - - 0 30");
    for (var i = 0; i < 25; i++) {
      board.movesStrict("Ra3", "Kd8", "Ra1", "Ke8");
    }
    assertEquals(100, board.getHalfMoveClock(), "precondition: 100-ply shuffle reaches the threshold");

    final FiftyMoveClaimAheadReport report = FiftyMoveClaimAheadReportBuilder.build(board);
    assertEquals(0, report.entries().size(),
        "sequence reached clock 100 in actual play — missed-opportunity filter suppresses the claim-aheads");
  }

  @SuppressWarnings("static-method")
  @Test
  void claimAheadEntriesAtClock99BoundaryWhenPawnMoveBreaksSequence() {
    // Initial FEN clock 98, plus a king + rook + pawn material. One non-zeroing move brings clock
    // to 99, then a pawn push resets to 0. The sequence ends at clock 99 without reaching 100, so
    // the missed-opportunity filter emits one entry per non-zeroing legal move at the clock-99 ply.
    final Board board = new Board("4k3/p7/8/8/8/8/P7/4K2R w - - 98 80");
    assertEquals(98, board.getHalfMoveClock(), "precondition: FEN clock 98");

    board.movesStrict("Rg1");
    assertEquals(99, board.getHalfMoveClock(), "precondition: clock at 99");
    board.movesStrict("a6");
    assertEquals(0, board.getHalfMoveClock(), "precondition: pawn push reset the clock");

    final FiftyMoveClaimAheadReport report = FiftyMoveClaimAheadReportBuilder.build(board);
    assertTrue(report.entries().size() >= 1,
        "pawn push at the clock-99 boundary breaks the sequence — at least one missed-opportunity entry must surface");

    // All entries are attributed to the InitialFenStart(98), since the sequence inherits from the
    // FEN's pre-existing clock. Each candidate's resulting clock is exactly 100.
    for (final FiftyMoveClaimAheadEntry entry : report.entries()) {
      assertTrue(entry.sequenceStart() instanceof InitialFenStart,
          "FEN clock 98 inherited — sequence-start is InitialFenStart, not AfterResetStart");
      assertEquals(98, ((InitialFenStart) entry.sequenceStart()).initialClockValue());
      assertEquals(100, entry.claimAheadMove().halfMoveClock());
    }
  }

  @SuppressWarnings("static-method")
  @Test
  void initialFenAlreadyAtThresholdHasNoClaimAheadsRegardlessOfContinuation() {
    // FEN clock 100; no claim-ahead under the new filter — neither for the Kxb2-style no-
    // continuation case (predicate rejects all clock-resetting candidates anyway) nor for the
    // continuation case (sequence is already past 99, never sits at the missed-opportunity
    // boundary).
    final Board boardNoCont = new Board("7k/8/8/8/8/8/1q6/K7 w - - 100 80");
    final FiftyMoveClaimAheadReport reportNoCont = FiftyMoveClaimAheadReportBuilder.build(boardNoCont);
    assertEquals(0, reportNoCont.entries().size(),
        "FEN at clock 100 with no continuation: 0 entries (Kxb2 special case unchanged)");

    final Board boardWithCont = new Board("4k3/8/8/8/8/8/8/R3K3 w - - 100 80");
    boardWithCont.movesStrict("Ra3", "Kd8", "Ra1", "Ke8");
    final FiftyMoveClaimAheadReport reportWithCont = FiftyMoveClaimAheadReportBuilder.build(boardWithCont);
    assertEquals(0, reportWithCont.entries().size(),
        "FEN at clock 100 with continuation: 0 entries (sequence past 99 cannot produce missed-opportunity claim-aheads)");
  }

  @SuppressWarnings("static-method")
  @Test
  void entriesAtFinalPositionAreOrderedByCandidateHalfMoveCount() {
    final Board board = new Board("4k3/8/8/8/8/8/8/R3K3 w - - 0 30");
    for (var i = 0; i < 24; i++) {
      board.movesStrict("Ra3", "Kd8", "Ra1", "Ke8");
    }
    board.movesStrict("Ra3", "Kd8", "Ra1");

    final FiftyMoveClaimAheadReport report = FiftyMoveClaimAheadReportBuilder.build(board);
    assertTrue(report.entries().size() >= 2, "fixture exposes multiple legal-move candidates at the boundary");

    for (var i = 1; i < report.entries().size(); i++) {
      final int prev = report.entries().get(i - 1).claimAheadMove().halfMoveCount();
      final int curr = report.entries().get(i).claimAheadMove().halfMoveCount();
      assertTrue(prev <= curr,
          "within a single sequence-start anchor, entries are ordered by claimAheadMove halfMoveCount");
    }
  }
}
