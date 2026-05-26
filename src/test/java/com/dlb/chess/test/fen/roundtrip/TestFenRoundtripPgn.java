package com.dlb.chess.test.fen.roundtrip;

import java.nio.file.Path;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;

import com.dlb.chess.board.Board;
import com.dlb.chess.common.Nulls;
import com.dlb.chess.common.model.MoveSpecification;
import com.dlb.chess.model.PgnHalfMove;
import com.dlb.chess.pgn.PgnGame;
import com.dlb.chess.test.RestrictTestConstants;
import com.dlb.chess.test.model.PgnFen;
import com.dlb.chess.test.model.PgnTestCaseList;
import com.dlb.chess.test.pgn.parser.PgnCacheForStrictPgnParserTestCases;
import com.dlb.chess.test.pgn.setup.PgnTestCaseCatalog;

class TestFenRoundtripPgn extends AbstractTestFenRoundtrip {

  private static final Logger logger = Nulls.getLogger(TestFenRoundtripPgn.class);

  @SuppressWarnings("static-method")
  @Test
  void testPgnSample() throws Exception {

    for (final PgnTestCaseList testCaseList : PgnTestCaseCatalog.getRestrictedTestListList()) {
      if (RestrictTestConstants.IS_RESTRICT_PGN_FEN_PARSER_ALL_TEST) {
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
      for (final PgnFen testCase : testCaseList.list()) {
        checkFenRoundtrip(testCaseList.pgnTest().getFolderPath(), testCase.pgnName());
      }
    }
  }

  private static void checkFenRoundtrip(Path folderPath, String pgnName) throws Exception {

    logger.info(pgnName);

    final PgnGame pgnGame = PgnCacheForStrictPgnParserTestCases.getPgn(folderPath, pgnName);

    final Board board = new Board(pgnGame.startFen());
    for (final PgnHalfMove halfMove : pgnGame.halfMoveList()) {
      board.moveStrict(halfMove.san());
    }
    final List<MoveSpecification> moveList = board.getPerformedMoveSpecificationList();
    checFenRoundtrip(pgnGame.startFen().fen(), moveList);
  }

}
