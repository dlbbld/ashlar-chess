// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.test.pgntest.basic;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;

import io.github.dlbbld.ashlarchess.board.Board;
import io.github.dlbbld.ashlarchess.common.Nulls;
import io.github.dlbbld.ashlarchess.test.model.PgnFen;
import io.github.dlbbld.ashlarchess.test.model.PgnTestCaseList;
import io.github.dlbbld.ashlarchess.test.pgn.setup.PgnTestCaseCatalog;
import io.github.dlbbld.ashlarchess.test.pgntest.enums.PgnTest;

class TestBasicCheckWhite extends AbstractTestBasic {

  private static final Logger logger = Nulls.getLogger(TestBasicCheckWhite.class);

  static {
    final List<String> pgnNameList = new ArrayList<>();

    pgnNameList.add("01_white_check_rook_direct_adjacent.pgn");
    pgnNameList.add("02_white_check_rook_direct_range.pgn");
    pgnNameList.add("03_white_check_rook_discover.pgn");
    pgnNameList.add("04_white_check_knight_direct.pgn");
    pgnNameList.add("05_white_check_knight_discover_orthogonal.pgn");
    pgnNameList.add("06_white_check_knight_discover_diagonal.pgn");
    pgnNameList.add("07_white_check_bishop_direct_adjacent.pgn");
    pgnNameList.add("08_white_check_bishop_direct_range.pgn");
    pgnNameList.add("09_white_check_bishop_discover.pgn");
    pgnNameList.add("10_white_check_queen_direct_orthogonal_adjacent.pgn");
    pgnNameList.add("11_white_check_queen_direct_orthogonal_range.pgn");
    pgnNameList.add("12_white_check_queen_direct_diagonal_adjacent.pgn");
    pgnNameList.add("13_white_check_queen_direct_diagonal_range.pgn");
    pgnNameList.add("14_white_check_king_discover_orthogonal.pgn");
    pgnNameList.add("15_white_check_king_discover_diagonal.pgn");

    checkTestFolder(pgnNameList, PgnTest.BASIC_CHECK_WHITE);
  }

  @SuppressWarnings("static-method")
  @Test
  void test() throws Exception {
    final PgnTestCaseList testCaseList = PgnTestCaseCatalog.getTestList(PgnTest.BASIC_CHECK_WHITE);
    for (final PgnFen testCase : testCaseList.list()) {
      final Board board = testCase.game(testCaseList.pgnTest());

      logger.info(testCase.pgnName());

      switch (testCase.pgnName()) {
        case "01_white_check_rook_direct_adjacent.pgn" -> checkNonCaptureCheck(D3, D6, WHITE_ROOK, board);
        case "02_white_check_rook_direct_range.pgn" -> checkNonCaptureCheck(B3, B6, WHITE_ROOK, board);
        case "03_white_check_rook_discover.pgn" -> checkNonCaptureCheck(B5, D5, WHITE_ROOK, board);
        case "04_white_check_knight_direct.pgn" -> checkNonCaptureCheck(D5, F6, WHITE_KNIGHT, board);
        case "05_white_check_knight_discover_orthogonal.pgn" -> checkNonCaptureCheck(D5, B6, WHITE_KNIGHT, board);
        case "06_white_check_knight_discover_diagonal.pgn" -> checkNonCaptureCheck(B5, A3, WHITE_KNIGHT, board);
        case "07_white_check_bishop_direct_adjacent.pgn" -> checkNonCaptureCheck(C4, F7, WHITE_BISHOP, board);
        case "08_white_check_bishop_direct_range.pgn" -> checkNonCaptureCheck(F1, B5, WHITE_BISHOP, board);
        case "09_white_check_bishop_discover.pgn" -> checkNonCaptureCheck(B5, C4, WHITE_BISHOP, board);
        case "10_white_check_queen_direct_orthogonal_adjacent.pgn" -> checkNonCaptureCheck(D6, E7, WHITE_QUEEN, board);
        case "11_white_check_queen_direct_orthogonal_range.pgn" -> checkNonCaptureCheck(D2, E3, WHITE_QUEEN, board);
        case "12_white_check_queen_direct_diagonal_adjacent.pgn" -> checkNonCaptureCheck(B3, F7, WHITE_QUEEN, board);
        case "13_white_check_queen_direct_diagonal_range.pgn" -> checkNonCaptureCheck(D1, H5, WHITE_QUEEN, board);
        case "14_white_check_king_discover_orthogonal.pgn" -> checkNonCaptureCheck(E2, F3, WHITE_KING, board);
        case "15_white_check_king_discover_diagonal.pgn" -> checkNonCaptureCheck(B5, B4, WHITE_KING, board);
        default -> throw new IllegalArgumentException();
      }
    }
  }

}
