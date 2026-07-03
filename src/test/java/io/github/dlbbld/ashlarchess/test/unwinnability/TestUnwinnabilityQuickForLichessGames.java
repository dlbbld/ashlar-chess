// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.test.unwinnability;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;

import io.github.dlbbld.ashlarchess.board.Board;
import io.github.dlbbld.ashlarchess.internal.Nulls;
import io.github.dlbbld.ashlarchess.test.common.utility.Loggers;
import io.github.dlbbld.ashlarchess.test.model.PgnFen;
import io.github.dlbbld.ashlarchess.test.model.PgnTestCaseList;
import io.github.dlbbld.ashlarchess.test.pgn.setup.PgnTestCaseCatalog;
import io.github.dlbbld.ashlarchess.test.pgntest.enums.PgnTest;
import io.github.dlbbld.ashlarchess.test.unwinnability.againstcha.AmbronaUnwinnabilityOracle;
import io.github.dlbbld.ashlarchess.test.unwinnability.againstcha.model.AmbronaUnwinnabilityVerdicts;
import io.github.dlbbld.ashlarchess.unwinnability.UnwinnabilityQuickVerdict;
import io.github.dlbbld.ashlarchess.unwinnability.UnwinnableQuickAnalyzer;

class TestUnwinnabilityQuickForLichessGames {
  private static final Logger logger = Loggers.getLogger(TestUnwinnabilityQuickForLichessGames.class);

  @SuppressWarnings("static-method")
  @Test
  void test() throws Exception {
    final List<String> failures = new ArrayList<>();

    for (final PgnTest pgnTest : PgnTest.values()) {
      final PgnTestCaseList testCaseList = PgnTestCaseCatalog.getTestList(pgnTest);
      for (final PgnFen testCase : testCaseList.list()) {
        switch (testCaseList.pgnTest()) {
          case CHA_LICHESS_QUICK_DEPTH_ABOVE_FOUR:
          case CHA_LICHESS_QUICK_DEPTH_THREE:
          case CHA_LICHESS_QUICK_DEPTH_FOUR:
            break;
          default:
            continue;
        }

        switch (testCase.pgnName()) {
          // Paper-formulation trade-off (22.0.0): this locked pawn fortress needs cha's beyond-paper quick extras;
          // the Figure 10 routine leaves it open (the full analyzer still proves it UNWINNABLE - see the
          // accepted-differences fixtures for the corresponding quick oracle rows).
          case "lichess_f6c1lu7R.pgn":
            continue;
          default:
            break;
        }

        final Board board = testCase.finalPosition();

        logger.info(testCase.pgnName());

        final UnwinnabilityQuickVerdict unwinnableQuickNotSideToMove = UnwinnableQuickAnalyzer
            .unwinnableQuick(board, board.getSideToMove().getOppositeSide()).verdict();
        if (unwinnableQuickNotSideToMove != UnwinnabilityQuickVerdict.UNWINNABLE) {
          failures.add(testCase.pgnName() + " " + board.getSideToMove().getOppositeSide() + " expected UNWINNABLE"
              + " actual " + unwinnableQuickNotSideToMove + " FEN " + testCase.finalFen());
        }

        final AmbronaUnwinnabilityVerdicts ambronaVerdict = AmbronaUnwinnabilityOracle.get(board.getFen());
        switch (board.getSideToMove().getOppositeSide()) {
          case WHITE:
            assertEquals(UnwinnabilityQuickVerdict.UNWINNABLE, ambronaVerdict.quickWhite());
            break;
          case BLACK:
            assertEquals(UnwinnabilityQuickVerdict.UNWINNABLE, ambronaVerdict.quickBlack());
            break;
          default:
            throw new IllegalStateException("Unexpected side: " + board.getSideToMove().getOppositeSide());
        }
      }
    }
    assertTrue(failures.isEmpty(), Nulls.join("\n", failures));
  }
}
