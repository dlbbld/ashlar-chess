package com.dlb.chess.test.librarycomparison;

import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;

import com.dlb.chess.board.Board;
import com.dlb.chess.board.CommonTestUtility;
import com.dlb.chess.board.LibraryCarlosBoard;
import com.dlb.chess.common.Nulls;
import com.dlb.chess.fen.constants.FenConstants;
import com.dlb.chess.model.PgnHalfMove;
import com.dlb.chess.pgn.PgnGame;
import com.dlb.chess.test.RestrictTestConstants;
import com.dlb.chess.test.model.PgnTestCase;
import com.dlb.chess.test.model.PgnTestCaseList;
import com.dlb.chess.test.pgn.parser.PgnCacheForStrictPgnParserTestCases;
import com.dlb.chess.test.pgn.setup.PgnTestCaseCatalog;

class TestBoardAgainstEachOther {

  // Leave empty to test all games, put a game name to only test this game.
  // private static final String ONLY_TEST_GAME = "threefold_castling_white_both_sides_lost";

  private static final Logger logger = Nulls.getLogger(TestBoardAgainstEachOther.class);

  @SuppressWarnings("static-method")
  @Test
  void test() throws Exception {
    for (final PgnTestCaseList testCaseList : PgnTestCaseCatalog.getRestrictedTestListList()) {
      for (final PgnTestCase testCase : testCaseList.list()) {
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

        for (final PgnHalfMove pgnHalfMove : pgnGame.halfMoveList()) {

          final String san = pgnHalfMove.san();
          board.moveStrict(san);
          carlosBoard.moveStrict(san);

          CommonTestUtility.checkBoardsAgainstEachOtherAll(board, carlosBoard);

        }
      }
    }
  }

}
