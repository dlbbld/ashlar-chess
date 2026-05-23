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
import com.dlb.chess.test.model.PgnTestCase;
import com.dlb.chess.test.pgn.setup.PgnTestCaseCatalog;
import com.dlb.chess.unwinnability.UnwinnabilityQuickAnalysis;
import com.dlb.chess.unwinnability.UnwinnableQuickAnalyzer;

class TestUnwinnabilityQuickHelpMateIsHelpMate {

  private static final Logger logger = Nulls.getLogger(TestUnwinnabilityQuickHelpMateIsHelpMate.class);

  @SuppressWarnings("static-method")
  @Test
  void mateLinesActuallyCheckmate() {

    final PgnTestCase testCase1 = PgnTestCaseCatalog.findTestCase("01_forced_checkmate.pgn");
    mateLinesActuallyCheckmate(testCase1, Side.WHITE);

    final PgnTestCase testCase2 = PgnTestCaseCatalog.findTestCase("lichess_pUEeHLfu.pgn");
    mateLinesActuallyCheckmate(testCase2, Side.WHITE);

    final PgnTestCase testCase3 = PgnTestCaseCatalog.findTestCase("lichess_UNX9jAKK.pgn");
    mateLinesActuallyCheckmate(testCase3, Side.BLACK);

    final PgnTestCase testCase4 = PgnTestCaseCatalog.findTestCase("lichess_sMv8Hh43.pgn");
    mateLinesActuallyCheckmate(testCase4, Side.BLACK);
  }

  private static void mateLinesActuallyCheckmate(PgnTestCase testCase, Side winner) {
    logger.info(testCase.pgnName());
    final Board board = testCase.finalPosition();
    final String fen = testCase.finalFen();
    final UnwinnabilityQuickAnalysis analysis = UnwinnableQuickAnalyzer.unwinnableQuick(board, winner);
    assertHelpmateLine(fen, winner, analysis.mateLine());
  }

  private static void assertHelpmateLine(String fen, Side winner, List<UciMove> mateLine) {
    final var board = new Board(fen, false);
    for (final UciMove uciMove : mateLine) {
      board.move(UciMoveUtility.convertUciMoveToMoveSpecification(board, uciMove));
    }
    assertEquals(winner.getOppositeSide(), board.getHavingMove());
    assertTrue(board.isCheckmate());
  }

}
