package com.dlb.chess.report;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.dlb.chess.board.Board;
import com.dlb.chess.pgn.PgnUtility;
import com.dlb.chess.test.pgn.setup.PgnTestCaseCatalog;
import com.dlb.chess.test.pgntest.enums.PgnTest;

/**
 * Direct unit tests for {@link ThreefoldClaimAheadReportBuilder}: assertions made directly against the
 * {@link ThreefoldClaimAheadReport} record returned by the builder. Pins the claim-ahead semantics (one entry per
 * legal move that would create a threefold position from the parent ply), the hasBeenPlayed asterisk distinction,
 * the includesInitialPosition flag, the totalRepetitionCount math, and the entry ordering.
 */
class TestThreefoldClaimAheadReportBuilder {

  @SuppressWarnings("static-method")
  @Test
  void emptyWhenNoClaimAheadOpportunity() {
    final Board board = new Board();
    board.movesStrict("e4", "e5", "Nf3", "Nf6");

    final ThreefoldClaimAheadReport report = ThreefoldClaimAheadReportBuilder.build(board);
    assertEquals(0, report.entries().size(), "no claim-ahead in this short game");
  }

  @SuppressWarnings("static-method")
  @Test
  void claimAheadNotPlayedOnInitialPosition() {
    // 1. Nf3 Nf6 2. Ng1 Ng8 3. Nf3 Nf6 4. Ng1 — 7 plies, black to move next; black's Ng8 would
    // bring the initial position to its third occurrence (claim-ahead). The game stops here, so
    // black did NOT play Ng8 -> hasBeenPlayed == false.
    final Board board = new Board();
    board.movesStrict("Nf3", "Nf6", "Ng1", "Ng8", "Nf3", "Nf6", "Ng1");

    final ThreefoldClaimAheadReport report = ThreefoldClaimAheadReportBuilder.build(board);
    assertEquals(1, report.entries().size(), "exactly one claim-ahead opportunity");

    final ClaimAheadEntry entry = report.entries().get(0);
    assertEquals(false, entry.hasBeenPlayed(), "the claim-ahead move was not played");
    assertEquals(true, entry.includesInitialPosition(), "the claim-ahead position is the initial position");
    assertEquals(3, entry.totalRepetitionCount(), "threefold count after the claim-ahead would be 3");
    assertEquals(1, entry.priorOccurrences().size(),
        "one prior played occurrence (after 2...Ng8) + 1 for claim-ahead + 1 for initial position = 3");
    assertEquals("Ng8", entry.claimAheadMove().san());
  }

  @SuppressWarnings("static-method")
  @Test
  void claimAheadPlayedOnInitialPositionMarkedHasBeenPlayed() {
    // Extend the previous game by playing the claim-ahead move. Black plays 4...Ng8 -> initial
    // position for the 3rd time on the board. The claim-ahead detected at the prior ply now
    // matches a played HalfMove -> hasBeenPlayed == true (the asterisked case).
    final Board board = new Board();
    board.movesStrict("Nf3", "Nf6", "Ng1", "Ng8", "Nf3", "Nf6", "Ng1", "Ng8");

    final ThreefoldClaimAheadReport report = ThreefoldClaimAheadReportBuilder.build(board);
    assertTrue(report.entries().size() >= 1, "at least one claim-ahead opportunity");

    ClaimAheadEntry initialPlayed = null;
    for (final ClaimAheadEntry e : report.entries()) {
      if (e.includesInitialPosition() && e.hasBeenPlayed()) {
        initialPlayed = e;
        break;
      }
    }
    assertTrue(initialPlayed != null,
        "the played-on-board Ng8 must surface as a claim-ahead entry with hasBeenPlayed == true");
    assertEquals(3, initialPlayed.totalRepetitionCount());
  }

  @SuppressWarnings("static-method")
  @Test
  void claimAheadOnNonInitialPositionFromCorpus() {
    // 11_threefold_castling_one_before_first_threefold.pgn — threefold position involves castling
    // rights, so it differs from the initial position. The fixture stops one ply before the third
    // occurrence -> at least one claim-ahead exists, includesInitialPosition == false.
    final Board board = loadCorpusBoard("11_threefold_castling_one_before_first_threefold.pgn");

    final ThreefoldClaimAheadReport report = ThreefoldClaimAheadReportBuilder.build(board);
    assertTrue(report.entries().size() >= 1, "fixture stops one ply before threefold; at least one claim-ahead");

    boolean foundNonInitial = false;
    for (final ClaimAheadEntry entry : report.entries()) {
      if (!entry.includesInitialPosition()) {
        foundNonInitial = true;
        // Math invariant on the non-initial entry.
        final int expected = entry.priorOccurrences().size() + 1;
        assertEquals(expected, entry.totalRepetitionCount(),
            "non-initial: priorOccurrences.size + 1 (for claimAheadMove)");
      }
    }
    assertTrue(foundNonInitial,
        "fixture has castling-related repetition; expected at least one non-initial-position claim-ahead");
  }

  @SuppressWarnings("static-method")
  @Test
  void entriesAreOrderedByClaimAheadHalfMoveCount() {
    // 18_threefold_two_threefolds_beyond.pgn — long enough to expose claim-aheads at multiple
    // plies. The builder sorts entries by (claimAheadMove.halfMoveCount(), legal-move-iteration
    // order at that ply). Pin the outer monotonicity here.
    final Board board = loadCorpusBoard("18_threefold_two_threefolds_beyond.pgn");

    final ThreefoldClaimAheadReport report = ThreefoldClaimAheadReportBuilder.build(board);
    assertTrue(report.entries().size() >= 2,
        "fixture exercises multiple claim-aheads across plies");

    for (var i = 1; i < report.entries().size(); i++) {
      final int prev = report.entries().get(i - 1).claimAheadMove().halfMoveCount();
      final int curr = report.entries().get(i).claimAheadMove().halfMoveCount();
      assertTrue(prev <= curr, "entries must be sorted by claimAheadMove.halfMoveCount()");
    }
  }

  private static Board loadCorpusBoard(String pgnName) {
    final PgnTest pgnTest = PgnTestCaseCatalog.findPgnTestPgnNotListed(pgnName);
    return PgnUtility.calculateBoard(pgnTest.getFolderPath(), pgnName);
  }
}
