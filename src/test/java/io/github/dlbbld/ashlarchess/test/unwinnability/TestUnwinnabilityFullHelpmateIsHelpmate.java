// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.test.unwinnability;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;

import io.github.dlbbld.ashlarchess.board.Board;
import io.github.dlbbld.ashlarchess.board.enums.Side;
import io.github.dlbbld.ashlarchess.common.Nulls;
import io.github.dlbbld.ashlarchess.common.ucimove.utility.UciMoveUtility;
import io.github.dlbbld.ashlarchess.model.UciMove;
import io.github.dlbbld.ashlarchess.test.common.utility.PgnExtensionUtility;
import io.github.dlbbld.ashlarchess.test.model.PgnFen;
import io.github.dlbbld.ashlarchess.test.model.PgnTestCaseList;
import io.github.dlbbld.ashlarchess.test.pgn.setup.PgnTestCaseCatalog;
import io.github.dlbbld.ashlarchess.test.pgntest.enums.PgnTest;
import io.github.dlbbld.ashlarchess.unwinnability.UnwinnabilityFullAnalysis;
import io.github.dlbbld.ashlarchess.unwinnability.UnwinnabilityFullVerdict;
import io.github.dlbbld.ashlarchess.unwinnability.UnwinnableFullAnalyzer;

class TestUnwinnabilityFullHelpmateIsHelpmate {

  private static final Logger logger = Nulls.getLogger(TestUnwinnabilityFullHelpmateIsHelpmate.class);

  @SuppressWarnings("static-method")
  @Test
  void mateLinesActuallyCheckmate() {
    for (final PgnFen testCaseHavingHelpmate : helpmateFixtures()) {
      logger.info(testCaseHavingHelpmate.pgnName());
      final PgnFen lichessTestCase = PgnTestCaseCatalog
          .findTestCase(calculateCorrespondingLichessGame(testCaseHavingHelpmate.pgnName()));
      final Board board = lichessTestCase.finalPosition();
      final String fen = lichessTestCase.finalFen();
      final Side winner = board.getHavingMove();
      final UnwinnabilityFullAnalysis analysis = UnwinnableFullAnalyzer.unwinnableFull(board, winner);
      // A searched winnable verdict must be WINNABLE_HELPMATE and carry a line; pin that before replaying it.
      assertEquals(UnwinnabilityFullVerdict.WINNABLE_HELPMATE, analysis.verdict(), fen);
      assertHelpmateLine(fen, winner, analysis.mateLine());
    }
  }

  private static List<PgnFen> helpmateFixtures() {
    final PgnTestCaseList testCaseHavingHelpmateList = PgnTestCaseCatalog
        .getTestList(PgnTest.CHA_LICHESS_QUICK_DEPTH_ABOVE_FOUR_WINNABLE_FOR_FLAGGING_WITH_HELPMATE);
    return testCaseHavingHelpmateList.list();
  }

  private static void assertHelpmateLine(String fen, Side winner, List<UciMove> mateLine) {
    final Board board = new Board(fen);
    for (final UciMove uciMove : mateLine) {
      board.move(UciMoveUtility.convertUciMoveToMoveSpecification(board, uciMove));
    }
    assertEquals(winner.getOppositeSide(), board.getHavingMove());
    assertTrue(board.isCheckmate());
  }

  private static String calculateCorrespondingLichessGame(String lichessGameHelpmate) {
    String withoutExtension = PgnExtensionUtility.removePgnExtension(lichessGameHelpmate);
    withoutExtension = Nulls.replace(withoutExtension, "_helpmate", "");
    return PgnExtensionUtility.addPgnExtension(withoutExtension);
  }
}
