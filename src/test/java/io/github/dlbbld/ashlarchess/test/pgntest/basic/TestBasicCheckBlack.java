// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.test.pgntest.basic;

import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.A3;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.A4;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.B4;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.B6;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.B7;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.BLACK_BISHOP;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.BLACK_KING;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.BLACK_KNIGHT;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.BLACK_QUEEN;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.BLACK_ROOK;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.C4;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.C5;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.C6;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.C8;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.D2;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.D4;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.D6;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.D8;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.E2;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.E5;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.E6;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.E7;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.F2;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.F3;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.G4;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.G5;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.G6;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;

import io.github.dlbbld.ashlarchess.board.Board;
import io.github.dlbbld.ashlarchess.test.common.utility.Loggers;
import io.github.dlbbld.ashlarchess.test.model.PgnFen;
import io.github.dlbbld.ashlarchess.test.model.PgnTestCaseList;
import io.github.dlbbld.ashlarchess.test.pgn.setup.PgnTestCaseCatalog;
import io.github.dlbbld.ashlarchess.test.pgntest.enums.PgnTest;

class TestBasicCheckBlack {
  private static final Logger logger = Loggers.getLogger(TestBasicCheckBlack.class);

  static {
    final List<String> pgnNames = new ArrayList<>();

    pgnNames.add("01_black_check_rook_direct_adjacent.pgn");
    pgnNames.add("02_black_check_rook_direct_range.pgn");
    pgnNames.add("03_black_check_rook_discover.pgn");
    pgnNames.add("04_black_check_knight_direct.pgn");
    pgnNames.add("05_black_check_knight_discover_orthogonal.pgn");
    pgnNames.add("06_black_check_knight_discover_diagonal.pgn");
    pgnNames.add("07_black_check_bishop_direct_adjacent.pgn");
    pgnNames.add("08_black_check_bishop_direct_range.pgn");
    pgnNames.add("09_black_check_bishop_discover.pgn");
    pgnNames.add("10_black_check_queen_direct_orthogonal_adjacent.pgn");
    pgnNames.add("11_black_check_queen_direct_orthogonal_range.pgn");
    pgnNames.add("12_black_check_queen_direct_diagonal_adjacent.pgn");
    pgnNames.add("13_black_check_queen_direct_diagonal_range.pgn");
    pgnNames.add("14_black_check_king_discover_orthogonal.pgn");
    pgnNames.add("15_black_check_king_discover_diagonal.pgn");

    TestBasicSupport.checkTestFolder(pgnNames, PgnTest.BASIC_CHECK_BLACK);
  }

  @SuppressWarnings("static-method")
  @Test
  void test() throws Exception {
    final PgnTestCaseList testCaseList = PgnTestCaseCatalog.getTestList(PgnTest.BASIC_CHECK_BLACK);
    for (final PgnFen testCase : testCaseList.list()) {
      logger.info(testCase.pgnName());
      final Board board = testCase.game(testCaseList.pgnTest());

      switch (testCase.pgnName()) {
        case "01_black_check_rook_direct_adjacent.pgn" -> TestBasicSupport.checkNonCaptureCheck(C6, C4, BLACK_ROOK,
            board);
        case "02_black_check_rook_direct_range.pgn" -> TestBasicSupport.checkNonCaptureCheck(A4, A3, BLACK_ROOK, board);
        case "03_black_check_rook_discover.pgn" -> TestBasicSupport.checkNonCaptureCheck(B4, B7, BLACK_ROOK, board);
        case "04_black_check_knight_direct.pgn" -> TestBasicSupport.checkNonCaptureCheck(D4, F3, BLACK_KNIGHT, board);
        case "05_black_check_knight_discover_orthogonal.pgn" -> TestBasicSupport.checkNonCaptureCheck(E5, G6,
            BLACK_KNIGHT, board);
        case "06_black_check_knight_discover_diagonal.pgn" -> TestBasicSupport.checkNonCaptureCheck(C5, E6,
            BLACK_KNIGHT, board);
        case "07_black_check_bishop_direct_adjacent.pgn" -> TestBasicSupport.checkNonCaptureCheck(C5, F2, BLACK_BISHOP,
            board);
        case "08_black_check_bishop_direct_range.pgn" -> TestBasicSupport.checkNonCaptureCheck(C8, B7, BLACK_BISHOP,
            board);
        case "09_black_check_bishop_discover.pgn" -> TestBasicSupport.checkNonCaptureCheck(E6, G4, BLACK_BISHOP, board);
        case "10_black_check_queen_direct_orthogonal_adjacent.pgn" -> TestBasicSupport.checkNonCaptureCheck(G4, E2,
            BLACK_QUEEN, board);
        case "11_black_check_queen_direct_orthogonal_range.pgn" -> TestBasicSupport.checkNonCaptureCheck(D6, E5,
            BLACK_QUEEN, board);
        case "12_black_check_queen_direct_diagonal_adjacent.pgn" -> TestBasicSupport.checkNonCaptureCheck(G5, D2,
            BLACK_QUEEN, board);
        case "13_black_check_queen_direct_diagonal_range.pgn" -> TestBasicSupport.checkNonCaptureCheck(D8, B6,
            BLACK_QUEEN, board);
        case "14_black_check_king_discover_orthogonal.pgn" -> TestBasicSupport.checkNonCaptureCheck(E7, D6, BLACK_KING,
            board);
        case "15_black_check_king_discover_diagonal.pgn" -> TestBasicSupport.checkNonCaptureCheck(G5, G4, BLACK_KING,
            board);
        default -> throw new IllegalArgumentException();
      }
    }
  }

}
