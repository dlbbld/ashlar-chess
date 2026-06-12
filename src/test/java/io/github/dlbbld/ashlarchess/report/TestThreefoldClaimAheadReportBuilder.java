// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.report;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import io.github.dlbbld.ashlarchess.board.Board;
import io.github.dlbbld.ashlarchess.common.Nulls;
import io.github.dlbbld.ashlarchess.pgn.PgnUtility;
import io.github.dlbbld.ashlarchess.test.pgn.setup.PgnTestCaseCatalog;
import io.github.dlbbld.ashlarchess.test.pgntest.enums.PgnTest;

/**
 * Direct unit tests for {@link ThreefoldClaimAheadReportBuilder}: assertions made directly against the
 * {@link ThreefoldClaimAheadReport} record returned by the builder. Pins the claim-ahead semantics (one entry per legal
 * move that would create a threefold position from the parent move), the hasBeenPlayed asterisk distinction, the
 * includesInitialPosition flag, the totalRepetitionCount math, and the entry ordering.
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
    // 1. Nf3 Nf6 2. Ng1 Ng8 3. Nf3 Nf6 4. Ng1 - 7 moves, black to move next; black's Ng8 would
    // bring the initial position to its third occurrence (claim-ahead). The game stops here, so
    // black did NOT play Ng8 -> hasBeenPlayed == false.
    final Board board = new Board();
    board.movesStrict("Nf3", "Nf6", "Ng1", "Ng8", "Nf3", "Nf6", "Ng1");

    final ThreefoldClaimAheadReport report = ThreefoldClaimAheadReportBuilder.build(board);
    assertEquals(1, report.entries().size(), "exactly one claim-ahead opportunity");

    final ClaimAheadEntry entry = Nulls.get(report.entries(), 0);
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
    // position for the 3rd time on the board. The claim-ahead detected at the prior move now
    // matches a played MoveRecord -> hasBeenPlayed == true (the asterisked case).
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
    // 11_threefold_castling_one_before_first_threefold.pgn - threefold position involves castling
    // rights, so it differs from the initial position. The fixture stops one move before the third
    // occurrence -> at least one claim-ahead exists, includesInitialPosition == false.
    final Board board = loadCorpusBoard("11_threefold_castling_one_before_first_threefold.pgn");

    final ThreefoldClaimAheadReport report = ThreefoldClaimAheadReportBuilder.build(board);
    assertTrue(report.entries().size() >= 1, "fixture stops one move before threefold; at least one claim-ahead");

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
  void entriesAreOrderedLexicographicallyByDisplayedSequence() {
    // 18_threefold_two_threefolds_beyond.pgn - long enough to expose claim-aheads at multiple
    // moves. The builder sorts entries lexicographically on the displayed move-count sequence
    // (priorOccurrences ++ claimAheadMove, prefixed by virtual -1 when includesInitialPosition).
    // Pin that lex order here: within a group of entries sharing an earlier-move prefix, the
    // shorter one (lower totalRepetitionCount) sorts first, and across groups, the entry whose
    // first displayed move is earlier sorts first.
    final Board board = loadCorpusBoard("18_threefold_two_threefolds_beyond.pgn");

    final ThreefoldClaimAheadReport report = ThreefoldClaimAheadReportBuilder.build(board);
    assertTrue(report.entries().size() >= 2, "fixture exercises multiple claim-aheads across moves");

    for (int i = 1; i < report.entries().size(); i++) {
      final int index = i;
      final ClaimAheadEntry prev = Nulls.get(report.entries(), i - 1);
      final ClaimAheadEntry curr = Nulls.get(report.entries(), i);
      assertTrue(compareLexKey(prev, curr) <= 0,
          () -> "entries must be sorted lexicographically by displayed-move sequence; violation at index " + index);
    }
  }

  /**
   * Helper: builds the displayed-move sort key for an entry (matching {@code ReportLineOrder}'s comparator) and compares
   * lex-order. {@code -1} prefix for initial-position-inclusive entries; ties broken by sequence length (shorter first,
   * since a prefix sorts before its extension).
   */
  private static int compareLexKey(ClaimAheadEntry a, ClaimAheadEntry b) {
    final List<Integer> keyA = sortKey(a);
    final List<Integer> keyB = sortKey(b);
    final int n = Math.min(keyA.size(), keyB.size());
    for (int i = 0; i < n; i++) {
      final int cmp = Integer.compare(keyA.get(i), keyB.get(i));
      if (cmp != 0) {
        return cmp;
      }
    }
    return Integer.compare(keyA.size(), keyB.size());
  }

  private static List<Integer> sortKey(ClaimAheadEntry e) {
    final List<Integer> key = new ArrayList<>();
    if (e.includesInitialPosition()) {
      key.add(-1);
    }
    for (final MoveRecord hm : e.priorOccurrences()) {
      key.add(hm.performedMoveCount());
    }
    key.add(e.claimAheadMove().performedMoveCount());
    return key;
  }

  private static Board loadCorpusBoard(String pgnName) {
    final PgnTest pgnTest = PgnTestCaseCatalog.findPgnTestPgnNotListed(pgnName);
    return PgnUtility.calculateBoard(pgnTest.getFolderPath(), pgnName);
  }
}
