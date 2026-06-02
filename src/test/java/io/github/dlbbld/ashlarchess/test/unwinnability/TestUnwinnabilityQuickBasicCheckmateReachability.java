// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.test.unwinnability;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;

import io.github.dlbbld.ashlarchess.board.Board;
import io.github.dlbbld.ashlarchess.board.enums.Side;
import io.github.dlbbld.ashlarchess.test.model.PgnFen;
import io.github.dlbbld.ashlarchess.test.pgn.setup.PgnTestCaseCatalog;
import io.github.dlbbld.ashlarchess.test.pgntest.enums.PgnTest;
import io.github.dlbbld.ashlarchess.unwinnability.UnwinnabilityQuickVerdict;

// Basic-endgame helpmate-reachability theorem, White holding the mating material, checked against the
// quick analyzer. Quick is sound but not complete, so two separate things are asserted:
//
//   1. Soundness (must always hold): quick never contradicts the theorem - never UNWINNABLE on a
//      winnable position, never WINNABLE on an unwinnable one.
//
//   2. Current decisiveness (characterization of a KNOWN GAP): quick decides only the forced-capture
//      (unwinnable) positions, via forced-move advancement into insufficient material. On the winnable
//      positions it returns POSSIBLY_WINNABLE, because our quick search is a flat depth-9 DFS that lacks
//      the FUN22 reward / Going-to-corner heuristic (Figures 5, 12, 13) and so cannot reach the far
//      helpmate. When that heuristic is ported, the winnable cases will become WINNABLE; flip the
//      expectation below at that point.
class TestUnwinnabilityQuickBasicCheckmateReachability {

  @SuppressWarnings("static-method")
  @Test
  void quickIsSoundAndDecidesOnlyForcedCaptures() {
    for (final PgnFen testCase : PgnTestCaseCatalog.getTestList(PgnTest.CHA_BASIC_CHECKMATE_REACHABILITY).list()) {
      final Board board = testCase.finalPosition();
      final boolean theoremUnwinnable = board.getHavingMove() == Side.BLACK
          && testCase.pgnName().contains("black_forced_to_capture");
      final UnwinnabilityQuickVerdict quick = board.isUnwinnableQuick(Side.WHITE);

      if (theoremUnwinnable) {
        assertNotEquals(UnwinnabilityQuickVerdict.WINNABLE, quick, testCase.pgnName());
        assertEquals(UnwinnabilityQuickVerdict.UNWINNABLE, quick, testCase.pgnName());
      } else {
        assertNotEquals(UnwinnabilityQuickVerdict.UNWINNABLE, quick, testCase.pgnName());
        // KNOWN GAP: theorem says WINNABLE; quick cannot decide it yet (see class comment).
        assertEquals(UnwinnabilityQuickVerdict.POSSIBLY_WINNABLE, quick, testCase.pgnName());
      }
    }
  }
}
