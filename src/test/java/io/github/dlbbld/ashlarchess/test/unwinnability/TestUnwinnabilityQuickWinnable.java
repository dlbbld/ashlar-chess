package io.github.dlbbld.ashlarchess.test.unwinnability;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;

import io.github.dlbbld.ashlarchess.board.Board;
import io.github.dlbbld.ashlarchess.board.enums.Side;
import io.github.dlbbld.ashlarchess.common.Nulls;
import io.github.dlbbld.ashlarchess.test.model.PgnFen;
import io.github.dlbbld.ashlarchess.test.pgn.setup.PgnTestCaseCatalog;
import io.github.dlbbld.ashlarchess.unwinnability.UnwinnabilityQuickAnalysis;
import io.github.dlbbld.ashlarchess.unwinnability.UnwinnabilityQuickVerdict;
import io.github.dlbbld.ashlarchess.unwinnability.UnwinnableQuickAnalyzer;

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
