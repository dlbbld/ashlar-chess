package com.dlb.chess.test.unwinnability;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;

import com.dlb.chess.board.Board;
import com.dlb.chess.common.Nulls;
import com.dlb.chess.test.model.PgnFen;
import com.dlb.chess.test.model.PgnTestCaseList;
import com.dlb.chess.test.pgn.setup.PgnTestCaseCatalog;
import com.dlb.chess.test.pgntest.enums.PgnTest;
import com.dlb.chess.test.unwinnability.againstcha.AmbronaUnwinnabilityOracle;
import com.dlb.chess.test.unwinnability.againstcha.model.AmbronaUnwinnabilityVerdicts;
import com.dlb.chess.unwinnability.UnwinnabilityFullVerdict;
import com.dlb.chess.unwinnability.UnwinnableFullAnalyzer;

class TestUnwinnabilityFullForLichessGames {

  private static final Logger logger = Nulls.getLogger(TestUnwinnabilityFullForLichessGames.class);

  @SuppressWarnings("static-method")
  @Test
  void test() throws Exception {

    for (final PgnTest pgnTest : PgnTest.values()) {
      final PgnTestCaseList testCaseList = PgnTestCaseCatalog.getTestList(pgnTest);
      for (final PgnFen testCase : testCaseList.list()) {
        switch (testCaseList.pgnTest()) {
          case CHA_LICHESS_QUICK_DEPTH_ABOVE_FOUR:
          case CHA_LICHESS_QUICK_DEPTH_THREE:
          case CHA_LICHESS_QUICK_DEPTH_FOUR:
            break;
          default:
            continue;
        }

        final Board board = testCase.finalPosition();

        logger.info(testCase.pgnName());

        final UnwinnabilityFullVerdict unwinnableFullNotHavingMove = UnwinnableFullAnalyzer
            .unwinnableFull(board, board.getHavingMove().getOppositeSide()).verdict();
        assertEquals(UnwinnabilityFullVerdict.UNWINNABLE, unwinnableFullNotHavingMove);

        final AmbronaUnwinnabilityVerdicts ambronaVerdict = AmbronaUnwinnabilityOracle.get(board.getFen());
        switch (board.getHavingMove().getOppositeSide()) {
          case WHITE:
            assertEquals(UnwinnabilityFullVerdict.UNWINNABLE, ambronaVerdict.fullWhite());
            break;
          case BLACK:
            assertEquals(UnwinnabilityFullVerdict.UNWINNABLE, ambronaVerdict.fullBlack());
            break;
          default:
            throw new IllegalStateException("Unexpected side: " + board.getHavingMove().getOppositeSide());
        }
      }
    }
  }
}
