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

  @SuppressWarnings("static-method")
  @Test
  void testFolder() throws Exception {
    final PgnTestCaseList testCaseHavingHelpmateList = PgnTestCaseCatalog
        .getTestList(PgnTest.CHA_LICHESS_QUICK_NOT_DEPTH_THREE_HELPMATE);

    var processedFilesInFolder = 0;
    for (final PgnTestCase testCaseHavingHelpmate : testCaseHavingHelpmateList.list()) {
      if (RestrictTestConstants.IS_RESTRICT_UNWINNABLE_FULL_FOR_LICHESS_HELPMATE_TEST
          && processedFilesInFolder >= MAX_FILES) {
        break;
      }
      logger.info(testCaseHavingHelpmate.pgnName());

      // Cheap path: the lichess game's final position is already cached on its test case's FEN field, so the PGN
      // need not be parsed and replayed just to read the end state. The helpmate-line check below builds its own
      // mini-board from the FEN.
      final String lichessGame = calculateCorrespondingLichessGame(testCaseHavingHelpmate.pgnName());
      final PgnTestCase lichessTestCase = PgnTestCaseCatalog.findTestCase(lichessGame);
      final Board board = lichessTestCase.finalPosition();
      final String fen = lichessTestCase.finalFen();
      final Side winner = board.getHavingMove();
      final UnwinnabilityFullAnalysis analysis = UnwinnableFullAnalyzer.unwinnableFull(board, winner);

      assertEquals(UnwinnabilityFullVerdict.WINNABLE, analysis.verdict());
      assertHelpmateLine(fen, winner, analysis.mateLine());

      processedFilesInFolder++;
    }
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
