// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.test.pgntest.basic;

import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;

import io.github.dlbbld.ashlarchess.board.Board;
import io.github.dlbbld.ashlarchess.test.common.utility.Loggers;
import io.github.dlbbld.ashlarchess.test.model.PgnFen;
import io.github.dlbbld.ashlarchess.test.model.PgnTestCaseList;
import io.github.dlbbld.ashlarchess.test.pgn.setup.PgnTestCaseCatalog;
import io.github.dlbbld.ashlarchess.test.pgntest.enums.PgnTest;

class TestBasicCheckmateVariousBlack {
  private static final Logger logger = Loggers.getLogger(TestBasicCheckmateVariousBlack.class);

  @SuppressWarnings("static-method")
  @Test
  void test() {
    final PgnTestCaseList testCaseList = PgnTestCaseCatalog.getTestList(PgnTest.BASIC_CHECKMATE_VARIOUS_BLACK);
    for (final PgnFen testCase : testCaseList.list()) {
      logger.info(testCase.pgnName());
      final Board board = testCase.finalPosition();
      TestBasicSupport.checkCheckmate(board);
    }
  }

}
