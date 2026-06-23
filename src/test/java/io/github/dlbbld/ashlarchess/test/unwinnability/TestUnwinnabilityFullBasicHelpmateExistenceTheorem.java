// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.test.unwinnability;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import io.github.dlbbld.ashlarchess.board.Board;
import io.github.dlbbld.ashlarchess.board.enums.Side;
import io.github.dlbbld.ashlarchess.test.model.PgnFen;
import io.github.dlbbld.ashlarchess.test.pgn.setup.PgnTestCaseCatalog;
import io.github.dlbbld.ashlarchess.test.pgntest.enums.PgnTest;
import io.github.dlbbld.ashlarchess.unwinnability.UnwinnabilityFullAnalysis;
import io.github.dlbbld.ashlarchess.unwinnability.UnwinnabilityFullVerdict;
import io.github.dlbbld.ashlarchess.unwinnability.UnwinnableFullAnalyzer;
import io.github.dlbbld.ashlarchess.unwinnability.WinnableProof;

// Basic-endgame helpmate-reachability theorem, White holding the mating material. The complete (full)
// analyzer must reproduce the theorem on every fixture:
//   White to move                                   -> White has a helpmate -> WINNABLE
//   Black to move, forced to capture White material  -> no helpmate         -> UNWINNABLE
//   Black to move, not forced to capture             -> White has a helpmate -> WINNABLE
class TestUnwinnabilityFullBasicHelpmateExistenceTheorem {

  @SuppressWarnings("static-method")
  @Test
  void fullVerdictMatchesTheorem() {
    for (final PgnFen testCase : PgnTestCaseCatalog.getTestList(PgnTest.CHA_BASIC_HELPMATE_EXISTENCE_THEOREM).list()) {
      final Board board = testCase.finalPosition();
      final UnwinnabilityFullVerdict expected = board.getSideToMove() == Side.BLACK
          && testCase.pgnName().contains("black_forced_to_capture") ? UnwinnabilityFullVerdict.UNWINNABLE
              : UnwinnabilityFullVerdict.WINNABLE;
      final UnwinnabilityFullAnalysis analysis = UnwinnableFullAnalyzer.unwinnableFull(board, Side.WHITE);
      assertEquals(expected, analysis.verdict(), testCase.pgnName());
      if (expected == UnwinnabilityFullVerdict.WINNABLE) {
        assertEquals(WinnableProof.THEOREM, analysis.winnableProof(), testCase.pgnName());
        assertTrue(analysis.mateLine().isEmpty(), testCase.pgnName());
      }
    }
  }
}
