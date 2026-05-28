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
import io.github.dlbbld.ashlarchess.test.RestrictTestConstants;
import io.github.dlbbld.ashlarchess.test.common.utility.PgnExtensionUtility;
import io.github.dlbbld.ashlarchess.test.model.PgnFen;
import io.github.dlbbld.ashlarchess.test.model.PgnTestCaseList;
import io.github.dlbbld.ashlarchess.test.pgn.setup.PgnTestCaseCatalog;
import io.github.dlbbld.ashlarchess.test.pgntest.enums.PgnTest;
import io.github.dlbbld.ashlarchess.unwinnability.UnwinnabilityFullAnalysis;
import io.github.dlbbld.ashlarchess.unwinnability.UnwinnableFullAnalyzer;

class TestUnwinnabilityFullHelpMateIsHelpMate {

  private static final Logger logger = Nulls.getLogger(TestUnwinnabilityFullHelpMateIsHelpMate.class);

  /** Cap on files tested when the smoke restriction is active. */
  private static final int MAX_FILES = 10;

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
      assertHelpmateLine(fen, winner, analysis.mateLine());
    }
  }

  private static List<PgnFen> helpmateFixtures() {
    final PgnTestCaseList testCaseHavingHelpmateList = PgnTestCaseCatalog
        .getTestList(PgnTest.CHA_LICHESS_QUICK_DEPTH_ABOVE_FOUR_WINNABLE_FOR_FLAGGING_WITH_HELPMATE);
    if (!RestrictTestConstants.IS_RESTRICT_UNWINNABLE_FULL_FOR_LICHESS_HELPMATE_TEST) {
      return testCaseHavingHelpmateList.list();
    }
    return Nulls.subList(testCaseHavingHelpmateList.list(), 0,
        Math.min(MAX_FILES, testCaseHavingHelpmateList.list().size()));
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
