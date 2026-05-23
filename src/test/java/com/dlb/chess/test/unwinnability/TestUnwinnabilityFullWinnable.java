package com.dlb.chess.test.unwinnability;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;

import com.dlb.chess.board.Board;
import com.dlb.chess.board.enums.Side;
import com.dlb.chess.common.Nulls;
import com.dlb.chess.test.RestrictTestConstants;
import com.dlb.chess.test.common.utility.PgnExtensionUtility;
import com.dlb.chess.test.model.PgnTestCase;
import com.dlb.chess.test.model.PgnTestCaseList;
import com.dlb.chess.test.pgn.setup.PgnTestCaseCatalog;
import com.dlb.chess.test.pgntest.enums.PgnTest;
import com.dlb.chess.unwinnability.UnwinnabilityFullAnalysis;
import com.dlb.chess.unwinnability.UnwinnabilityFullVerdict;
import com.dlb.chess.unwinnability.UnwinnableFullAnalyzer;

class TestUnwinnabilityFullWinnable {

  private static final Logger logger = Nulls.getLogger(TestUnwinnabilityFullWinnable.class);

  /** Cap on files tested when the smoke restriction is active. */
  private static final int MAX_FILES = 10;

  @SuppressWarnings("static-method")
  @Test
  void verdictsAreWinnable() {
    for (final PgnTestCase testCaseHavingHelpmate : helpmateFixtures()) {
      logger.info(testCaseHavingHelpmate.pgnName());
      final PgnTestCase lichessTestCase = PgnTestCaseCatalog
          .findTestCase(calculateCorrespondingLichessGame(testCaseHavingHelpmate.pgnName()));
      final Board board = lichessTestCase.finalPosition();
      final Side winner = board.getHavingMove();
      final UnwinnabilityFullAnalysis analysis = UnwinnableFullAnalyzer.unwinnableFull(board, winner);
      assertEquals(UnwinnabilityFullVerdict.WINNABLE, analysis.verdict(), testCaseHavingHelpmate.pgnName());
    }
  }

  private static List<PgnTestCase> helpmateFixtures() {
    final PgnTestCaseList testCaseHavingHelpmateList = PgnTestCaseCatalog
        .getTestList(PgnTest.CHA_LICHESS_QUICK_DEPTH_ABOVE_FOUR_WINNABLE_FOR_FLAGGING_WITH_HELPMATE);
    if (!RestrictTestConstants.IS_RESTRICT_UNWINNABLE_FULL_FOR_LICHESS_HELPMATE_TEST) {
      return testCaseHavingHelpmateList.list();
    }
    return Nulls.subList(testCaseHavingHelpmateList.list(), 0,
        Math.min(MAX_FILES, testCaseHavingHelpmateList.list().size()));
  }

  private static String calculateCorrespondingLichessGame(String lichessGameHelpmate) {
    var withoutExtension = PgnExtensionUtility.removePgnExtension(lichessGameHelpmate);
    withoutExtension = Nulls.replace(withoutExtension, "_helpmate", "");
    return PgnExtensionUtility.addPgnExtension(withoutExtension);
  }
}
