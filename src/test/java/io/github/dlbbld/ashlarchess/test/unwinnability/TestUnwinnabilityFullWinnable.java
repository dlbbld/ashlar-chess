// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.test.unwinnability;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;

import io.github.dlbbld.ashlarchess.board.Board;
import io.github.dlbbld.ashlarchess.board.enums.Side;
import io.github.dlbbld.ashlarchess.common.Nulls;
import io.github.dlbbld.ashlarchess.test.common.utility.PgnExtensionUtility;
import io.github.dlbbld.ashlarchess.test.model.PgnFen;
import io.github.dlbbld.ashlarchess.test.model.PgnTestCaseList;
import io.github.dlbbld.ashlarchess.test.pgn.setup.PgnTestCaseCatalog;
import io.github.dlbbld.ashlarchess.test.pgntest.enums.PgnTest;
import io.github.dlbbld.ashlarchess.unwinnability.UnwinnabilityFullAnalysis;
import io.github.dlbbld.ashlarchess.unwinnability.UnwinnabilityFullVerdict;
import io.github.dlbbld.ashlarchess.unwinnability.UnwinnableFullAnalyzer;

class TestUnwinnabilityFullWinnable {

  private static final Logger logger = Nulls.getLogger(TestUnwinnabilityFullWinnable.class);

  @SuppressWarnings("static-method")
  @Test
  void verdictsAreWinnable() {
    for (final PgnFen testCaseHavingHelpmate : helpmateFixtures()) {
      logger.info(testCaseHavingHelpmate.pgnName());
      final PgnFen lichessTestCase = PgnTestCaseCatalog
          .findTestCase(calculateCorrespondingLichessGame(testCaseHavingHelpmate.pgnName()));
      final Board board = lichessTestCase.finalPosition();
      final Side winner = board.getHavingMove();
      final UnwinnabilityFullAnalysis analysis = UnwinnableFullAnalyzer.unwinnableFull(board, winner);
      assertEquals(UnwinnabilityFullVerdict.WINNABLE, analysis.verdict(), testCaseHavingHelpmate.pgnName());
    }
  }

  private static List<PgnFen> helpmateFixtures() {
    final PgnTestCaseList testCaseHavingHelpmateList = PgnTestCaseCatalog
        .getTestList(PgnTest.CHA_LICHESS_QUICK_DEPTH_ABOVE_FOUR_WINNABLE_FOR_FLAGGING_WITH_HELPMATE);
    return testCaseHavingHelpmateList.list();
  }

  private static String calculateCorrespondingLichessGame(String lichessGameHelpmate) {
    String withoutExtension = PgnExtensionUtility.removePgnExtension(lichessGameHelpmate);
    withoutExtension = Nulls.replace(withoutExtension, "_helpmate", "");
    return PgnExtensionUtility.addPgnExtension(withoutExtension);
  }
}
