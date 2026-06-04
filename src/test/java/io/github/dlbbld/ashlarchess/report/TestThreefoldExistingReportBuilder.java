// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.report;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import io.github.dlbbld.ashlarchess.board.Board;
import io.github.dlbbld.ashlarchess.common.Nulls;
import io.github.dlbbld.ashlarchess.common.constants.ChessConstants;
import io.github.dlbbld.ashlarchess.pgn.PgnUtility;
import io.github.dlbbld.ashlarchess.test.pgn.setup.PgnTestCaseCatalog;
import io.github.dlbbld.ashlarchess.test.pgntest.enums.PgnTest;

/**
 * Direct unit tests for {@link ThreefoldExistingReportBuilder}: assertions made directly against the
 * {@link ThreefoldExistingReport} record returned by the builder, rather than against printed output. Complements
 * {@code TestReporterGoldenOutput}, which catches end-to-end formatting drift but not object-level shape regressions.
 *
 * <p>
 * Inline boards are used for the basic shapes (no threefold; threefold on the initial position; threefold-and-beyond).
 * Committed PGN fixtures from the corpus stand in for the more complex shapes (multi-group, non-initial position
 * reaching threefold).
 */
class TestThreefoldExistingReportBuilder {

  @SuppressWarnings("static-method")
  @Test
  void emptyWhenNoRepetition() {
    final Board board = new Board();
    board.movesStrict("e4", "e5", "Nf3", "Nf6");

    final ThreefoldExistingReport report = build(board);
    assertEquals(0, report.groups().size(), "no repetition in this game; groups must be empty");
  }

  @SuppressWarnings("static-method")
  @Test
  void singleGroupOnInitialPositionAtThreshold() {
    // 1. Nf3 Nf6 2. Ng1 Ng8 3. Nf3 Nf6 4. Ng1 Ng8 - initial position now appears for the 3rd time.
    final Board board = new Board();
    board.movesStrict("Nf3", "Nf6", "Ng1", "Ng8", "Nf3", "Nf6", "Ng1", "Ng8");

    final ThreefoldExistingReport report = build(board);
    assertEquals(1, report.groups().size(), "exactly one repeated position");

    final RepetitionGroup group = Nulls.get(report.groups(), 0);
    assertEquals(true, group.includesInitialPosition(), "the repeated position is the initial position");
    assertEquals(3, group.totalRepetitionCount(), "threefold has been reached");
    assertEquals(2, group.occurrences().size(),
        "two played occurrences + 1 implicit initial-position occurrence = 3 total");
    assertEquals(board.getInitialDynamicPosition(), group.repeatedPosition());
  }

  @SuppressWarnings("static-method")
  @Test
  void initialPositionGroupReachesFivefoldWhenBeyond() {
    // Continue past threefold to fivefold of the initial position. Other intermediate positions
    // (after-Nf3, after-Nf6, after-Ng1) will also have reached the threefold threshold by then and
    // appear as their own groups; the assertion targets only the initial-position group.
    final Board board = new Board();
    board.movesStrict("Nf3", "Nf6", "Ng1", "Ng8", "Nf3", "Nf6", "Ng1", "Ng8", "Nf3", "Nf6", "Ng1", "Ng8", "Nf3", "Nf6",
        "Ng1", "Ng8");

    final ThreefoldExistingReport report = build(board);

    RepetitionGroup initialGroup = null;
    for (final RepetitionGroup g : report.groups()) {
      if (g.includesInitialPosition()) {
        initialGroup = g;
        break;
      }
    }
    assertTrue(initialGroup != null, "the initial position must appear as a repeated-position group");
    assertEquals(5, initialGroup.totalRepetitionCount(), "fivefold of the initial position reached");
    assertEquals(4, initialGroup.occurrences().size(), "four played occurrences + 1 implicit initial");
  }

  @SuppressWarnings("static-method")
  @Test
  void multipleGroupsFromCorpus() {
    // 18_threefold_two_threefolds_beyond.pgn reaches two distinct threefold positions and continues
    // playing past them - same fixture the golden-output test uses for this scenario.
    final Board board = loadCorpusBoard("18_threefold_two_threefolds_beyond.pgn");

    final ThreefoldExistingReport report = build(board);
    assertTrue(report.groups().size() >= 2, "fixture is named two-threefolds - at least 2 groups");

    // Outer sort: groups ordered by the half-move count of each group's first occurrence.
    for (int i = 1; i < report.groups().size(); i++) {
      final RepetitionGroup prevGroup = Nulls.get(report.groups(), i - 1);
      final int prev = Nulls.get(prevGroup.occurrences(), 0).halfMoveCount();

      final RepetitionGroup currGroup = Nulls.get(report.groups(), i);
      final int curr = Nulls.get(currGroup.occurrences(), 0).halfMoveCount();
      assertTrue(prev <= curr, "groups must be sorted by first-occurrence half-move count");
    }

    // Every group is consistent: totalRepetitionCount matches the occurrence count adjusted for
    // includesInitialPosition. The compact constructor enforces this, but pin it here too as a
    // shape-check on the builder's output.
    for (final RepetitionGroup group : report.groups()) {
      final int expected = group.occurrences().size() + (group.includesInitialPosition() ? 1 : 0);
      assertEquals(expected, group.totalRepetitionCount(), "totalRepetitionCount must match occurrences math");
      assertTrue(group.totalRepetitionCount() >= ChessConstants.THREEFOLD_REPETITION_RULE_THRESHOLD,
          "every group in the existing-threefold report must have reached the threefold threshold");
    }
  }

  @SuppressWarnings("static-method")
  @Test
  void groupOnNonInitialPositionFromCorpus() {
    // 11_threefold_castling_one_before_first_threefold.pgn - threefold position is not the initial
    // position (involves castling rights). The golden-output test uses this fixture too. Here we
    // pin the includesInitialPosition == false branch directly on the object.
    final Board board = loadCorpusBoard("11_threefold_castling_one_before_first_threefold.pgn");

    final ThreefoldExistingReport report = build(board);
    // The fixture file name says "one_before_first_threefold" - meaning the position is one ply
    // away from third occurrence and has not yet been reached. So the existing-report should be
    // empty; the claim-ahead report would have an entry. That pins the boundary semantic.
    assertEquals(0, report.groups().size(),
        "fixture stops one ply before third occurrence; threefold not yet on the board");
  }

  private static ThreefoldExistingReport build(Board board) {
    return ThreefoldExistingReportBuilder.build(board.getInitialDynamicPosition(), board.getHalfMoveList(),
        ChessConstants.THREEFOLD_REPETITION_RULE_THRESHOLD);
  }

  private static Board loadCorpusBoard(String pgnName) {
    final PgnTest pgnTest = PgnTestCaseCatalog.findPgnTestPgnNotListed(pgnName);
    return PgnUtility.calculateBoard(pgnTest.getFolderPath(), pgnName);
  }
}
