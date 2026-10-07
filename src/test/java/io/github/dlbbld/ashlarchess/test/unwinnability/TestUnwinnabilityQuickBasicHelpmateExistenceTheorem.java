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
import io.github.dlbbld.ashlarchess.unwinnability.internal.BasicHelpmateExistenceTheorem;

// The basic-helpmate-existence theorem as a test oracle against the quick analyzer. The quick analysis is sound and
// deliberately incomplete, so the contract is one-directional:
//   - theorem UNWINNABLE (forced capture into insufficient material): quick must prove UNWINNABLE - it advances the
//     single forced capture and exhausts the insufficient-material leaves.
//   - theorem WINNABLE: quick must never contradict (never UNWINNABLE); it may prove WINNABLE when it meets a mate
//     before its first depth interrupt, or leave the position open as POSSIBLY_WINNABLE.
class TestUnwinnabilityQuickBasicHelpmateExistenceTheorem {

  @SuppressWarnings("static-method")
  @Test
  void quickIsSoundAgainstTheTheoremOracle() {
    for (final PgnFen testCase : PgnTestCaseCatalog.getTestList(PgnTest.CHA_BASIC_HELPMATE_EXISTENCE_THEOREM).list()) {
      final Board board = testCase.finalPosition();
      final UnwinnabilityQuickVerdict quick = board.unwinnableQuick(Side.WHITE);
      switch (BasicHelpmateExistenceTheorem.decide(board, Side.WHITE)) {
        case UNWINNABLE:
          assertEquals(UnwinnabilityQuickVerdict.UNWINNABLE, quick, testCase.pgnName());
          break;
        case WINNABLE:
          assertNotEquals(UnwinnabilityQuickVerdict.UNWINNABLE, quick, testCase.pgnName());
          break;
        case NOT_APPLICABLE:
          throw new AssertionError(testCase.pgnName() + ": every curated fixture is a covered, ongoing position");
        default:
          throw new IllegalArgumentException();
      }
    }
  }
}
