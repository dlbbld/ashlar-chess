package com.dlb.chess.test.unwinnability;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;

import com.dlb.chess.board.Board;
import com.dlb.chess.board.enums.Side;
import com.dlb.chess.common.Nulls;
import com.dlb.chess.test.model.PgnFen;
import com.dlb.chess.test.pgn.setup.PgnTestCaseCatalog;
import com.dlb.chess.unwinnability.UnwinnabilityQuickAnalysis;
import com.dlb.chess.unwinnability.UnwinnabilityQuickVerdict;
import com.dlb.chess.unwinnability.UnwinnableQuickAnalyzer;

class TestUnwinnabilityQuickWinnable {

  private static final Logger logger = Nulls.getLogger(TestUnwinnabilityQuickWinnable.class);

  @SuppressWarnings("static-method")
  @Test
  void verdictsAreWinnable() {

    final PgnFen testCase1 = PgnTestCaseCatalog.findTestCase("01_forced_checkmate.pgn");
    actuallyWinnable(testCase1, Side.WHITE);

    final PgnFen testCase2 = PgnTestCaseCatalog.findTestCase("lichess_pUEeHLfu.pgn");
    actuallyWinnable(testCase2, Side.WHITE);

    final PgnFen testCase3 = PgnTestCaseCatalog.findTestCase("lichess_UNX9jAKK.pgn");
    actuallyWinnable(testCase3, Side.BLACK);

    final PgnFen testCase4 = PgnTestCaseCatalog.findTestCase("lichess_sMv8Hh43.pgn");
    actuallyWinnable(testCase4, Side.BLACK);

  }

  private static void actuallyWinnable(PgnFen testCase, Side winner) {
    logger.info(testCase.pgnName());
    final Board board = testCase.finalPosition();
    final UnwinnabilityQuickAnalysis analysis = UnwinnableQuickAnalyzer.unwinnableQuick(board, winner);
    assertEquals(UnwinnabilityQuickVerdict.WINNABLE, analysis.verdict(), testCase.pgnName());
  }

}
