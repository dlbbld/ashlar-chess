// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.test.unwinnability;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import io.github.dlbbld.ashlarchess.board.Board;
import io.github.dlbbld.ashlarchess.board.enums.Side;
import io.github.dlbbld.ashlarchess.test.model.PgnFen;
import io.github.dlbbld.ashlarchess.test.pgn.setup.PgnTestCaseCatalog;
import io.github.dlbbld.ashlarchess.test.pgntest.enums.PgnTest;
import io.github.dlbbld.ashlarchess.unwinnability.UnwinnabilityQuickVerdict;

// Basic-endgame helpmate-reachability theorem, White holding the mating material, checked against the quick analyzer.
// The quick analysis is two-valued (UNWINNABLE / POSSIBLY_WINNABLE) and never claims winnability, so it both stays
// sound and characterizes a known gap:
//   - Forced-capture (theorem-unwinnable) positions: quick proves UNWINNABLE, by advancing the single forced capture
//     into insufficient material.
//   - Winnable positions: quick returns POSSIBLY_WINNABLE - it only proves unwinnability and never searches for the far
//     helpmate, so it never contradicts the theorem (never UNWINNABLE on a winnable position).
class TestUnwinnabilityQuickBasicHelpmateExistenceTheorem {

  @SuppressWarnings("static-method")
  @Test
  void quickIsSoundAndDecidesOnlyForcedCaptures() {
    for (final PgnFen testCase : PgnTestCaseCatalog.getTestList(PgnTest.CHA_BASIC_HELPMATE_EXISTENCE_THEOREM).list()) {
      final Board board = testCase.finalPosition();
      final boolean theoremUnwinnable = board.getHavingMove() == Side.BLACK
          && testCase.pgnName().contains("black_forced_to_capture");
      final UnwinnabilityQuickVerdict quick = board.isUnwinnableQuick(Side.WHITE);

      final UnwinnabilityQuickVerdict expected = theoremUnwinnable ? UnwinnabilityQuickVerdict.UNWINNABLE
          : UnwinnabilityQuickVerdict.POSSIBLY_WINNABLE;
      assertEquals(expected, quick, testCase.pgnName());
    }
  }
}
