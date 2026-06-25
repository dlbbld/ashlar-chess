// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.test.pgntest.basic;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;

import io.github.dlbbld.ashlarchess.test.common.utility.Loggers;
import io.github.dlbbld.ashlarchess.board.Board;
import io.github.dlbbld.ashlarchess.board.enums.Piece;
import io.github.dlbbld.ashlarchess.test.model.PgnFen;
import io.github.dlbbld.ashlarchess.test.model.PgnTestCaseList;
import io.github.dlbbld.ashlarchess.test.pgn.setup.PgnTestCaseCatalog;
import io.github.dlbbld.ashlarchess.test.pgntest.enums.PgnTest;

class TestBasicDoubleCheckCheckmateBlack {
  private static final Logger logger = Loggers.getLogger(TestBasicDoubleCheckCheckmateBlack.class);

  static {
    final List<String> pgnNames = new ArrayList<>();

    pgnNames.add("01_black_double_check_checkmate_rook.pgn");
    pgnNames.add("02_black_double_check_checkmate_knight_orthogonal.pgn");
    pgnNames.add("03_black_double_check_checkmate_knight_diagonal.pgn");
    pgnNames.add("04_black_double_check_checkmate_bishop.pgn");

    TestBasicSupport.checkTestFolder(pgnNames, PgnTest.BASIC_CHECKMATE_DOUBLE_CHECK_BLACK);
  }

  @SuppressWarnings("static-method")
  @Test
  void test() throws Exception {
    final PgnTestCaseList testCaseList = PgnTestCaseCatalog.getTestList(PgnTest.BASIC_CHECKMATE_DOUBLE_CHECK_BLACK);
    for (final PgnFen testCase : testCaseList.list()) {
      final Board board = testCase.game(testCaseList.pgnTest());

      logger.info(testCase.pgnName());

      switch (testCase.pgnName()) {
        case "01_black_double_check_checkmate_rook.pgn" -> TestBasicSupport.checkDoubleCheckCheckmate(Piece.BLACK_ROOK,
            board);
        case "02_black_double_check_checkmate_knight_orthogonal.pgn" -> TestBasicSupport
            .checkDoubleCheckCheckmate(Piece.BLACK_KNIGHT, board);
        case "03_black_double_check_checkmate_knight_diagonal.pgn" -> TestBasicSupport
            .checkDoubleCheckCheckmate(Piece.BLACK_KNIGHT, board);
        case "04_black_double_check_checkmate_bishop.pgn" -> TestBasicSupport
            .checkDoubleCheckCheckmate(Piece.BLACK_BISHOP, board);
        default -> throw new IllegalArgumentException();
      }
    }
  }

}
