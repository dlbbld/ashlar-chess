package io.github.dlbbld.ashlarchess.test.fen.roundtrip;

import java.nio.file.Path;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;

import io.github.dlbbld.ashlarchess.board.Board;
import io.github.dlbbld.ashlarchess.common.Nulls;
import io.github.dlbbld.ashlarchess.common.model.MoveSpecification;
import io.github.dlbbld.ashlarchess.model.PgnHalfMove;
import io.github.dlbbld.ashlarchess.pgn.PgnGame;
import io.github.dlbbld.ashlarchess.test.RestrictTestConstants;
import io.github.dlbbld.ashlarchess.test.model.PgnFen;
import io.github.dlbbld.ashlarchess.test.model.PgnTestCaseList;
import io.github.dlbbld.ashlarchess.test.pgn.parser.PgnCacheForStrictPgnParserTestCases;
import io.github.dlbbld.ashlarchess.test.pgn.setup.PgnTestCaseCatalog;

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
