package com.dlb.chess.test.unwinnability;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;

import com.dlb.chess.board.Board;
import com.dlb.chess.board.enums.Side;
import com.dlb.chess.common.Nulls;
import com.dlb.chess.common.ucimove.utility.UciMoveUtility;
import com.dlb.chess.model.UciMove;
import com.dlb.chess.test.RestrictTestConstants;
import com.dlb.chess.test.common.utility.PgnExtensionUtility;
import com.dlb.chess.test.model.PgnTestCase;
import com.dlb.chess.test.model.PgnTestCaseList;
import com.dlb.chess.test.pgn.setup.PgnTestCaseCatalog;
import com.dlb.chess.test.pgntest.enums.PgnTest;
import com.dlb.chess.unwinnability.UnwinnabilityFullAnalysis;
import com.dlb.chess.unwinnability.UnwinnabilityFullVerdict;
import com.dlb.chess.unwinnability.UnwinnableFullAnalyzer;

class TestUnwinnableFullForLichessGamesHavingHelpMate {

  private static final Logger logger = Nulls.getLogger(TestUnwinnableFullForLichessGamesHavingHelpMate.class);

  /** Cap on files tested when the smoke restriction is active. */
  private static final int MAX_FILES = 10;

  /**
   * For each Lichess game in the helpmate corpus, the analyzer's verdict is {@link UnwinnabilityFullVerdict#WINNABLE}
   * for the side to move. The companion {@link #mateLinesActuallyCheckmate} test handles the orthogonal claim that
   * the returned mate line is a valid checkmate sequence.
   */
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

  /**
   * Property check: when the analyzer returns a mate line for a known-winnable position, playing the mate line out
   * from the start position (after any forced-move preamble) must deliver checkmate against the intended winner's
   * opponent. Decoupled from {@link #verdictsAreWinnable} so a regression in mate-line construction reports
   * separately from a regression in verdict computation.
   */
  @SuppressWarnings("static-method")
  @Test
  void mateLinesActuallyCheckmate() {
    for (final PgnTestCase testCaseHavingHelpmate : helpmateFixtures()) {
      logger.info(testCaseHavingHelpmate.pgnName());
      final PgnTestCase lichessTestCase = PgnTestCaseCatalog
          .findTestCase(calculateCorrespondingLichessGame(testCaseHavingHelpmate.pgnName()));
      final Board board = lichessTestCase.finalPosition();
      final String fen = lichessTestCase.finalFen();
      final Side winner = board.getHavingMove();
      final UnwinnabilityFullAnalysis analysis = UnwinnableFullAnalyzer.unwinnableFull(board, winner);
      assertHelpmateLine(fen, winner, analysis.mateLine());
    }
  }

  private static List<PgnTestCase> helpmateFixtures() {
    final PgnTestCaseList testCaseHavingHelpmateList = PgnTestCaseCatalog
        .getTestList(PgnTest.CHA_LICHESS_QUICK_DEPTH_ABOVE_FOUR_HELPMATE);
    if (!RestrictTestConstants.IS_RESTRICT_UNWINNABLE_FULL_FOR_LICHESS_HELPMATE_TEST) {
      return testCaseHavingHelpmateList.list();
    }
    return testCaseHavingHelpmateList.list().subList(0, Math.min(MAX_FILES, testCaseHavingHelpmateList.list().size()));
  }

  private static void assertHelpmateLine(String fen, Side winner, List<UciMove> mateLine) {
    final var board = new Board(fen, false);
    advanceForcedMoves(board);
    for (final UciMove uciMove : mateLine) {
      board.move(UciMoveUtility.convertUciMoveToMoveSpecification(board, uciMove));
    }
    assertEquals(winner.getOppositeSide(), board.getHavingMove());
    assertTrue(board.isCheckmate());
  }

  private static void advanceForcedMoves(Board board) {
    while (board.getLegalMoves().size() == 1 && !board.isFivefoldRepetition() && !board.isSeventyFiveMove()) {
      board.move(Nulls.getFirst(board.getLegalMoves()).moveSpecification());
    }
  }

  private static String calculateCorrespondingLichessGame(String lichessGameHelpmate) {
    var withoutExtension = PgnExtensionUtility.removePgnExtension(lichessGameHelpmate);
    withoutExtension = Nulls.replace(withoutExtension, "_helpmate", "");
    return PgnExtensionUtility.addPgnExtension(withoutExtension);
  }
}
