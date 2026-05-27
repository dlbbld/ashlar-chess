package com.dlb.chess.report;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.dlb.chess.board.Board;
import com.dlb.chess.board.enums.Side;
import com.dlb.chess.common.Nulls;

/**
 * Direct unit tests for {@link FiftyMoveClaimAheadReportBuilder} under the missed-opportunity filter with boundary-
 * level collapse. Each {@link FiftyMoveClaimAheadEntry} now represents one clock-99 boundary at which the player had at
 * least one non-zeroing legal move available but the actually-played move broke the sequence (or the game ended at the
 * boundary). The number of alternative legal moves at the boundary does not affect the entry count — multiple
 * candidates collapse into one row per boundary, since listing all 30+ alternatives at a single ply would be noise with
 * no informational gain over a single "opportunity existed" row.
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
  void singleBoundaryEntryWhenGameEndsAtClock99() {
    // 99 plies of rook+king shuffle from clock 0 → game ends at clock 99 with no further play.
    // The boundary at the final position has many non-zeroing legal moves available (king + rook
    // shuffle), but the new collapse model emits exactly ONE entry — the missed-opportunity ply.
    final Board board = new Board("4k3/8/8/8/8/8/8/R3K3 w - - 0 30");
    for (var i = 0; i < 24; i++) {
      board.movesStrict("Ra3", "Kd8", "Ra1", "Ke8");
    }
    board.movesStrict("Ra3", "Kd8", "Ra1");
    assertEquals(99, board.getHalfMoveClock(), "precondition: clock at 99, game ends here");
    assertTrue(board.getLegalMoves().size() > 1, "precondition: many alternative legal moves exist at the boundary");

    final FiftyMoveClaimAheadReport report = FiftyMoveClaimAheadReportBuilder.build(board);
    assertEquals(1, report.entries().size(), "boundary collapse: many candidates at one ply produce exactly one entry");

    final FiftyMoveClaimAheadEntry entry = Nulls.get(report.entries(), 0);
    assertTrue(entry.sequenceStart() instanceof AfterResetStart);
    assertEquals("Ra3", ((AfterResetStart) entry.sequenceStart()).firstNonZeroingMove().san(),
        "sequence anchored at the first non-zeroing ply of the shuffle (Ra3)");
    // The boundary ply is Black's (the played history ended after a White move; Black to move next).
    assertEquals(Side.BLACK, entry.sideHavingMove(),
        "the boundary ply is Black-to-move (White just played the 99th half-move)");
  }

  @SuppressWarnings("static-method")
  @Test
  void emptyWhenSequenceReachesThreshold() {
    // Same fixture but play one MORE non-zeroing move so clock crosses 99 → 100. Sequence reached
    // threshold; the missed-opportunity filter suppresses the boundary entry entirely.
    final Board board = new Board("4k3/8/8/8/8/8/8/R3K3 w - - 0 30");
    for (var i = 0; i < 25; i++) {
      board.movesStrict("Ra3", "Kd8", "Ra1", "Ke8");
    }
    assertEquals(100, board.getHalfMoveClock(), "precondition: 100-ply shuffle reaches the threshold");

    final FiftyMoveClaimAheadReport report = FiftyMoveClaimAheadReportBuilder.build(board);
    assertEquals(0, report.entries().size(),
        "sequence reached clock 100 in actual play — missed-opportunity filter suppresses the boundary");
  }

  @SuppressWarnings("static-method")
  @Test
  void singleBoundaryEntryWhenPawnMoveBreaksSequenceAtClock99() {
    // FEN clock 98. One non-zeroing move brings clock to 99. Then a pawn push resets the clock.
    // The boundary at the clock-99 ply has multiple non-zeroing legal alternatives (the black king
    // could have moved instead of pushing the pawn). The collapse model emits exactly ONE entry.
    final Board board = new Board("4k3/p7/8/8/8/8/P7/4K2R w - - 98 80");
    assertEquals(98, board.getHalfMoveClock(), "precondition: FEN clock 98");

    board.movesStrict("Rg1");
    assertEquals(99, board.getHalfMoveClock(), "precondition: clock at 99");
    board.movesStrict("a6");
    assertEquals(0, board.getHalfMoveClock(), "precondition: pawn push reset the clock");

    final FiftyMoveClaimAheadReport report = FiftyMoveClaimAheadReportBuilder.build(board);
    assertEquals(1, report.entries().size(),
        "boundary collapse: a single missed-opportunity ply produces one entry regardless of how many alternatives existed");

    final FiftyMoveClaimAheadEntry entry = Nulls.get(report.entries(), 0);
    assertTrue(entry.sequenceStart() instanceof InitialFenStart,
        "FEN clock 98 inherited — sequence-start is InitialFenStart");
    assertEquals(98, ((InitialFenStart) entry.sequenceStart()).initialClockValue());
    assertEquals(Side.BLACK, entry.sideHavingMove(),
        "boundary ply is Black-to-move (White's Rg1 took clock from 98 to 99; Black's turn next, before the pawn push)");
  }

  @SuppressWarnings("static-method")
  @Test
  void initialFenAlreadyAtThresholdHasNoBoundaryEntriesRegardlessOfContinuation() {
    // FEN clock 100; no boundary entry under the filter — clock starts past 99, never sits at the
    // missed-opportunity boundary. Holds whether the only legal move is a capture (Kxb2 case) or
    // non-zeroing legal moves are available.
    final Board boardNoCont = new Board("7k/8/8/8/8/8/1q6/K7 w - - 100 80");
    final FiftyMoveClaimAheadReport reportNoCont = FiftyMoveClaimAheadReportBuilder.build(boardNoCont);
    assertEquals(0, reportNoCont.entries().size(), "FEN at clock 100, no continuation: 0 entries (Kxb2 case)");

    final Board boardWithCont = new Board("4k3/8/8/8/8/8/8/R3K3 w - - 100 80");
    boardWithCont.movesStrict("Ra3", "Kd8", "Ra1", "Ke8");
    final FiftyMoveClaimAheadReport reportWithCont = FiftyMoveClaimAheadReportBuilder.build(boardWithCont);
    assertEquals(0, reportWithCont.entries().size(),
        "FEN at clock 100, continuation: 0 entries (sequence past 99, no boundary ply)");
  }

  @SuppressWarnings("static-method")
  @Test
  void multipleBoundariesAcrossDistinctSequencesAreOrderedChronologically() {
    // Two distinct sequences, each reaching clock 99 and then resetting via a pawn move. Each ply
    // emits one boundary entry; the two entries should be ordered chronologically — InitialFenStart
    // first (sentinel anchor -1), then the AfterResetStart sequence by its first-non-zeroing-ply
    // half-move count.
    //
    // Sequence 1: FEN clock 98 → Rg1 (clock 99) → a6 (pawn push, clock 0). Boundary at Black's ply.
    // Sequence 2 (after the reset): play 99 non-zeroing plies to reach clock 99, then a pawn push
    // resets again. Boundary at the second clock-99 ply.
    final Board board = new Board("4k3/p7/8/8/8/8/P7/4K2R w - - 98 80");
    board.movesStrict("Rg1", "a6");
    // Now drive a fresh sequence to clock 99.
    board.movesStrict("Kd1");
    for (var i = 0; i < 24; i++) {
      board.movesStrict("Kd8", "Ke1", "Ke8", "Kd1");
    }
    board.movesStrict("Kd8", "Ke1");
    assertEquals(99, board.getHalfMoveClock(), "precondition: second sequence reached clock 99");
    // Break it with a pawn push.
    board.movesStrict("a5");
    assertEquals(0, board.getHalfMoveClock(), "precondition: second sequence reset by Black pawn push");

    final FiftyMoveClaimAheadReport report = FiftyMoveClaimAheadReportBuilder.build(board);
    assertEquals(2, report.entries().size(), "two distinct boundaries — one per sequence");

    // First entry: InitialFenStart anchor.
    assertTrue(Nulls.get(report.entries(), 0).sequenceStart() instanceof InitialFenStart,
        "chronological first: InitialFenStart-anchored boundary");
    // Second entry: AfterResetStart anchor.
    assertTrue(Nulls.get(report.entries(), 1).sequenceStart() instanceof AfterResetStart,
        "chronological second: AfterResetStart-anchored boundary");
    // Sort invariant: ascending half-move count.
    assertTrue(Nulls.get(report.entries(), 0).halfMoveCount() < Nulls.get(report.entries(), 1).halfMoveCount(),
        "entries sorted by chronological boundary half-move count");
  }
}
