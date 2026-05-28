package io.github.dlbbld.ashlarchess.test.pgntest.basic;

import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;

import io.github.dlbbld.ashlarchess.board.Board;
import io.github.dlbbld.ashlarchess.common.Nulls;
import io.github.dlbbld.ashlarchess.test.model.PgnFen;
import io.github.dlbbld.ashlarchess.test.model.PgnTestCaseList;
import io.github.dlbbld.ashlarchess.test.pgn.setup.PgnTestCaseCatalog;
import io.github.dlbbld.ashlarchess.test.pgntest.enums.PgnTest;

class TestBasicCheckmateVariousBlack extends AbstractTestBasic {

  private static final Logger logger = Nulls.getLogger(TestBasicCheckmateVariousBlack.class);

  @SuppressWarnings("static-method")
  @Test
  void test() throws Exception {
    final PgnTestCaseList testCaseList = PgnTestCaseCatalog.getTestList(PgnTest.BASIC_CHECKMATE_VARIOUS_BLACK);
    for (final PgnFen testCase : testCaseList.list()) {
      logger.info(testCase.pgnName());
      final Board board = testCase.finalPosition();
      checkCheckmate(board);
    }
  }

}
