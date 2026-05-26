package com.dlb.chess.test.pgntest.basic;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;

import com.dlb.chess.board.Board;
import com.dlb.chess.common.Nulls;
import com.dlb.chess.test.model.PgnFen;
import com.dlb.chess.test.model.PgnTestCaseList;
import com.dlb.chess.test.pgn.setup.PgnTestCaseCatalog;
import com.dlb.chess.test.pgntest.enums.PgnTest;

class TestBasicStalemate extends AbstractTestBasic {

  private static final Logger logger = Nulls.getLogger(TestBasicStalemate.class);

  @SuppressWarnings("static-method")
  @Test
  void test() throws Exception {
    final PgnTestCaseList testCaseList = PgnTestCaseCatalog.getTestList(PgnTest.BASIC_STALEMATE);
    for (final PgnFen testCase : testCaseList.list()) {
      final Board board = testCase.finalPosition();

      logger.info(testCase.pgnName());

      assertFalse(board.isCheck());
      assertFalse(board.isCheckmate());
      assertTrue(board.isStalemate());
    }
  }
}
