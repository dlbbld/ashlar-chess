// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.test.librarycomparison;

import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;

import io.github.dlbbld.ashlarchess.board.Board;
import io.github.dlbbld.ashlarchess.board.CommonTestUtility;
import io.github.dlbbld.ashlarchess.board.LibraryCarlosBoard;
import io.github.dlbbld.ashlarchess.common.Nulls;
import io.github.dlbbld.ashlarchess.fen.constants.FenConstants;
import io.github.dlbbld.ashlarchess.model.PgnMove;
import io.github.dlbbld.ashlarchess.pgn.PgnGame;
import io.github.dlbbld.ashlarchess.test.RestrictTestConstants;
import io.github.dlbbld.ashlarchess.test.model.PgnFen;
import io.github.dlbbld.ashlarchess.test.model.PgnTestCaseList;
import io.github.dlbbld.ashlarchess.test.pgn.parser.PgnCacheForStrictPgnParserTestCases;
import io.github.dlbbld.ashlarchess.test.pgn.setup.PgnTestCaseCatalog;

class TestBoardAgainstEachOther {

  // Leave empty to test all games, put a game name to only test this game.
  // private static final String ONLY_TEST_GAME = "threefold_castling_white_both_sides_lost";

  private static final Logger logger = Nulls.getLogger(TestBoardAgainstEachOther.class);

  @SuppressWarnings("static-method")
  @Test
  void test() throws Exception {
    for (final PgnTestCaseList testCaseList : PgnTestCaseCatalog.getRestrictedTestListList()) {
      for (final PgnFen testCase : testCaseList.list()) {
        // takes 50 minutes with all test cases
        if (RestrictTestConstants.IS_RESTRICT_PGN_BOARD_API_AGAINST_EACH_OTHER_TEST) {
          switch (testCaseList.pgnTest()) {
            case BASIC_CHECK_WHITE:
            case BASIC_CHECK_BLACK:
            case BASIC_CHECKMATE_WHITE:
            case BASIC_CHECKMATE_BLACK:
            case BASIC_STALEMATE:
              break;
            // $CASES-OMITTED$
            default:
              continue;
          }
        }

        final String pgnName = testCase.pgnName();
        logger.info(pgnName);

        final PgnGame pgnGame = PgnCacheForStrictPgnParserTestCases.getPgn(testCaseList.pgnTest().getFolderPath(),
            pgnName);

        if (pgnGame.startFen() != FenConstants.FEN_INITIAL) {
          // API Carlos does not generate correct SAN when starting from position
          logger.warn("Skipping PGN as starting from non-initital position:" + pgnName);
          continue;
        }

        final Board board = new Board();
        final LibraryCarlosBoard carlosBoard = new LibraryCarlosBoard();

        for (final PgnMove pgnHalfMove : pgnGame.moveList()) {

          final String san = pgnHalfMove.san();
          board.moveStrict(san);
          carlosBoard.moveStrict(san);

          CommonTestUtility.checkBoardsAgainstEachOtherAll(board, carlosBoard);

        }
      }
    }
  }

}
