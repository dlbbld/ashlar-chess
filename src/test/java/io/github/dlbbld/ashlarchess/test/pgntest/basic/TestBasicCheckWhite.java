// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.test.pgntest.basic;

import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.A3;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.B3;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.B4;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.B5;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.B6;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.C4;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.D1;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.D2;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.D3;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.D5;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.D6;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.E2;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.E3;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.E7;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.F1;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.F3;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.F6;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.F7;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.H5;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.WHITE_BISHOP;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.WHITE_KING;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.WHITE_KNIGHT;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.WHITE_QUEEN;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.WHITE_ROOK;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;

import io.github.dlbbld.ashlarchess.board.Board;
import io.github.dlbbld.ashlarchess.internal.Nulls;
import io.github.dlbbld.ashlarchess.test.model.PgnFen;
import io.github.dlbbld.ashlarchess.test.model.PgnTestCaseList;
import io.github.dlbbld.ashlarchess.test.pgn.setup.PgnTestCaseCatalog;
import io.github.dlbbld.ashlarchess.test.pgntest.enums.PgnTest;

class TestBasicCheckWhite {

  private static final Logger logger = Nulls.getLogger(TestBasicCheckWhite.class);

  static {
    final List<String> pgnNames = new ArrayList<>();

    pgnNames.add("01_white_check_rook_direct_adjacent.pgn");
    pgnNames.add("02_white_check_rook_direct_range.pgn");
    pgnNames.add("03_white_check_rook_discover.pgn");
    pgnNames.add("04_white_check_knight_direct.pgn");
    pgnNames.add("05_white_check_knight_discover_orthogonal.pgn");
    pgnNames.add("06_white_check_knight_discover_diagonal.pgn");
    pgnNames.add("07_white_check_bishop_direct_adjacent.pgn");
    pgnNames.add("08_white_check_bishop_direct_range.pgn");
    pgnNames.add("09_white_check_bishop_discover.pgn");
    pgnNames.add("10_white_check_queen_direct_orthogonal_adjacent.pgn");
    pgnNames.add("11_white_check_queen_direct_orthogonal_range.pgn");
    pgnNames.add("12_white_check_queen_direct_diagonal_adjacent.pgn");
    pgnNames.add("13_white_check_queen_direct_diagonal_range.pgn");
    pgnNames.add("14_white_check_king_discover_orthogonal.pgn");
    pgnNames.add("15_white_check_king_discover_diagonal.pgn");

    TestBasicSupport.checkTestFolder(pgnNames, PgnTest.BASIC_CHECK_WHITE);
  }

  @SuppressWarnings("static-method")
  @Test
  void test() throws Exception {
    final PgnTestCaseList testCaseList = PgnTestCaseCatalog.getTestList(PgnTest.BASIC_CHECK_WHITE);
    for (final PgnFen testCase : testCaseList.list()) {
      final Board board = testCase.game(testCaseList.pgnTest());

      logger.info(testCase.pgnName());

      switch (testCase.pgnName()) {
        case "01_white_check_rook_direct_adjacent.pgn" -> TestBasicSupport.checkNonCaptureCheck(D3, D6, WHITE_ROOK,
            board);
        case "02_white_check_rook_direct_range.pgn" -> TestBasicSupport.checkNonCaptureCheck(B3, B6, WHITE_ROOK, board);
        case "03_white_check_rook_discover.pgn" -> TestBasicSupport.checkNonCaptureCheck(B5, D5, WHITE_ROOK, board);
        case "04_white_check_knight_direct.pgn" -> TestBasicSupport.checkNonCaptureCheck(D5, F6, WHITE_KNIGHT, board);
        case "05_white_check_knight_discover_orthogonal.pgn" -> TestBasicSupport.checkNonCaptureCheck(D5, B6,
            WHITE_KNIGHT, board);
        case "06_white_check_knight_discover_diagonal.pgn" -> TestBasicSupport.checkNonCaptureCheck(B5, A3,
            WHITE_KNIGHT, board);
        case "07_white_check_bishop_direct_adjacent.pgn" -> TestBasicSupport.checkNonCaptureCheck(C4, F7, WHITE_BISHOP,
            board);
        case "08_white_check_bishop_direct_range.pgn" -> TestBasicSupport.checkNonCaptureCheck(F1, B5, WHITE_BISHOP,
            board);
        case "09_white_check_bishop_discover.pgn" -> TestBasicSupport.checkNonCaptureCheck(B5, C4, WHITE_BISHOP, board);
        case "10_white_check_queen_direct_orthogonal_adjacent.pgn" -> TestBasicSupport.checkNonCaptureCheck(D6, E7,
            WHITE_QUEEN, board);
        case "11_white_check_queen_direct_orthogonal_range.pgn" -> TestBasicSupport.checkNonCaptureCheck(D2, E3,
            WHITE_QUEEN, board);
        case "12_white_check_queen_direct_diagonal_adjacent.pgn" -> TestBasicSupport.checkNonCaptureCheck(B3, F7,
            WHITE_QUEEN, board);
        case "13_white_check_queen_direct_diagonal_range.pgn" -> TestBasicSupport.checkNonCaptureCheck(D1, H5,
            WHITE_QUEEN, board);
        case "14_white_check_king_discover_orthogonal.pgn" -> TestBasicSupport.checkNonCaptureCheck(E2, F3, WHITE_KING,
            board);
        case "15_white_check_king_discover_diagonal.pgn" -> TestBasicSupport.checkNonCaptureCheck(B5, B4, WHITE_KING,
            board);
        default -> throw new IllegalArgumentException();
      }
    }
  }

}
