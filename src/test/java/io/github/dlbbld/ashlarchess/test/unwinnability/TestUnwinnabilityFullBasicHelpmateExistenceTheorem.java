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
import io.github.dlbbld.ashlarchess.unwinnability.UnwinnabilityFullVerdict;

// Basic-endgame helpmate-reachability theorem, White holding the mating material. The complete (full)
// analyzer must reproduce the theorem on every fixture, and (since the theorem shortcut precedes the search for these
// covered classes) the winnable verdict is always the line-less WINNABLE_BY_THEOREM:
//   White to move                                   -> White has a helpmate -> WINNABLE_BY_THEOREM
//   Black to move, forced to capture White material  -> no helpmate         -> UNWINNABLE
//   Black to move, not forced to capture             -> White has a helpmate -> WINNABLE_BY_THEOREM
class TestUnwinnabilityFullBasicHelpmateExistenceTheorem {

  @SuppressWarnings("static-method")
  @Test
  void fullVerdictMatchesTheorem() {
    for (final PgnFen testCase : PgnTestCaseCatalog.getTestList(PgnTest.CHA_BASIC_HELPMATE_EXISTENCE_THEOREM).list()) {
      final Board board = testCase.finalPosition();
      final UnwinnabilityFullVerdict expected = board.getHavingMove() == Side.BLACK
          && testCase.pgnName().contains("black_forced_to_capture") ? UnwinnabilityFullVerdict.UNWINNABLE
              : UnwinnabilityFullVerdict.WINNABLE_BY_THEOREM;
      assertEquals(expected, board.isUnwinnableFull(Side.WHITE), testCase.pgnName());
    }
  }
}
