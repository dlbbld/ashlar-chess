// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.test.unwinnability;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;

import io.github.dlbbld.ashlarchess.board.Board;
import io.github.dlbbld.ashlarchess.board.enums.Side;
import io.github.dlbbld.ashlarchess.common.Nulls;
import io.github.dlbbld.ashlarchess.test.model.PgnFen;
import io.github.dlbbld.ashlarchess.test.model.PgnTestCaseList;
import io.github.dlbbld.ashlarchess.test.pgn.setup.PgnTestCaseCatalog;
import io.github.dlbbld.ashlarchess.test.pgntest.enums.PgnTest;
import io.github.dlbbld.ashlarchess.test.unwinnability.oracle.LimitedUnwinnabilityOracle;
import io.github.dlbbld.ashlarchess.test.unwinnability.oracle.enums.LimitedUnwinnabilityVerdict;
import io.github.dlbbld.ashlarchess.unwinnability.UnwinnabilityFullVerdict;
import io.github.dlbbld.ashlarchess.unwinnability.UnwinnableFullAnalyzer;

class TestUnwinnabilityFullAgainstLimitedOracle {

  private static final Logger logger = Nulls.getLogger(TestUnwinnabilityFullAgainstLimitedOracle.class);

  @SuppressWarnings("static-method")
  @Test
  void test() throws Exception {

    final PgnTestCaseList testCaseList = PgnTestCaseCatalog.getTestList(PgnTest.CHA_LICHESS_QUICK_DEPTH_ABOVE_FOUR);
    for (final PgnFen testCase : testCaseList.list()) {
      test(testCase);
    }
  }

  private static void test(PgnFen testCase) {
    final Board board = testCase.finalPosition();
    logger.info(testCase.pgnName());

    final LimitedUnwinnabilityVerdict verdictWhite = LimitedUnwinnabilityOracle.calculateUnwinnability(board,
        Side.WHITE);
    final UnwinnabilityFullVerdict unwinnableFullWhite = UnwinnableFullAnalyzer.unwinnableFull(board, Side.WHITE)
        .verdict();
    check(verdictWhite, unwinnableFullWhite);

    final LimitedUnwinnabilityVerdict verdictBlack = LimitedUnwinnabilityOracle.calculateUnwinnability(board,
        Side.BLACK);
    final UnwinnabilityFullVerdict unwinnableFullBlack = UnwinnableFullAnalyzer.unwinnableFull(board, Side.BLACK)
        .verdict();
    check(verdictBlack, unwinnableFullBlack);

  }

  private static void check(LimitedUnwinnabilityVerdict verdict, UnwinnabilityFullVerdict unwinnableFull) {
    switch (verdict) {
      case UNWINNABLE:
        assertEquals(UnwinnabilityFullVerdict.UNWINNABLE, unwinnableFull);
        break;
      case WINNABLE:
        assertTrue(unwinnableFull.isWinnable());
        break;
      case UNKNOWN:
        break;
      default:
        throw new IllegalArgumentException();
    }

    switch (unwinnableFull) {
      case WINNABLE_HELPMATE, WINNABLE_BY_THEOREM -> assertNotEquals(LimitedUnwinnabilityVerdict.UNWINNABLE, verdict);
      case UNWINNABLE -> {
        final boolean isIncomplete = verdict == LimitedUnwinnabilityVerdict.UNWINNABLE
            || verdict == LimitedUnwinnabilityVerdict.UNKNOWN;
        assertTrue(isIncomplete);
      }
      case UNDETERMINED -> assertEquals(LimitedUnwinnabilityVerdict.UNKNOWN, verdict);
    }
  }

}
