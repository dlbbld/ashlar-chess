// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.test.pgntest.basic;

import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.A5;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.B3;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.B4;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.B6;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.B7;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.C2;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.C4;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.C6;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.C7;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.D5;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.D7;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.D8;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.E2;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.E5;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.F3;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.F6;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.G7;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.G8;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.H6;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.H7;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.H8;
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
import io.github.dlbbld.ashlarchess.test.common.utility.Loggers;
import io.github.dlbbld.ashlarchess.test.model.PgnFen;
import io.github.dlbbld.ashlarchess.test.model.PgnTestCaseList;
import io.github.dlbbld.ashlarchess.test.pgn.setup.PgnTestCaseCatalog;
import io.github.dlbbld.ashlarchess.test.pgntest.enums.PgnTest;

class TestBasicCheckmateWhite {
  private static final Logger logger = Loggers.getLogger(TestBasicCheckmateWhite.class);

  static {
    final List<String> pgnNames = new ArrayList<>();

    pgnNames.add("01_white_checkmate_rook_direct_adjacent.pgn");
    pgnNames.add("02_white_checkmate_rook_direct_range.pgn");
    pgnNames.add("03_white_checkmate_rook_discover.pgn");
    pgnNames.add("04_white_checkmate_knight_direct.pgn");
    pgnNames.add("05_white_checkmate_knight_discover_orthogonal.pgn");
    pgnNames.add("06_white_checkmate_knight_discover_diagonal.pgn");
    pgnNames.add("07_white_checkmate_bishop_direct_adjacent.pgn");
    pgnNames.add("08_white_checkmate_bishop_direct_range.pgn");
    pgnNames.add("09_white_checkmate_bishop_discover.pgn");
    pgnNames.add("10_white_checkmate_queen_direct_orthogonal_adjacent.pgn");
    pgnNames.add("11_white_checkmate_queen_direct_orthogonal_range.pgn");
    pgnNames.add("12_white_checkmate_queen_direct_diagonal_adjacent.pgn");
    pgnNames.add("13_white_checkmate_queen_direct_diagonal_range.pgn");
    pgnNames.add("14_white_checkmate_king_discover_orthogonal.pgn");
    pgnNames.add("15_white_checkmate_king_discover_diagonal.pgn");

    TestBasicSupport.checkTestFolder(pgnNames, PgnTest.BASIC_CHECKMATE_WHITE);
  }

  @SuppressWarnings("static-method")
  @Test
  void test() throws Exception {
    final PgnTestCaseList testCaseList = PgnTestCaseCatalog.getTestList(PgnTest.BASIC_CHECKMATE_WHITE);
    for (final PgnFen testCase : testCaseList.list()) {
      final Board board = testCase.game(testCaseList.pgnTest());

      logger.info(testCase.pgnName());

      switch (testCase.pgnName()) {
        case "01_white_checkmate_rook_direct_adjacent.pgn" -> TestBasicSupport.checkNonCaptureCheckmate(D7, D8,
            WHITE_ROOK, board);
        case "02_white_checkmate_rook_direct_range.pgn" -> TestBasicSupport.checkNonCaptureCheckmate(H7, H8, WHITE_ROOK,
            board);
        case "03_white_checkmate_rook_discover.pgn" -> TestBasicSupport.checkNonCaptureCheckmate(D7, H7, WHITE_ROOK,
            board);
        case "04_white_checkmate_knight_direct.pgn" -> TestBasicSupport.checkNonCaptureCheckmate(B4, C6, WHITE_KNIGHT,
            board);
        case "05_white_checkmate_knight_discover_orthogonal.pgn" -> TestBasicSupport.checkNonCaptureCheckmate(D5, F6,
            WHITE_KNIGHT, board);
        case "06_white_checkmate_knight_discover_diagonal.pgn" -> TestBasicSupport.checkNonCaptureCheckmate(F6, G8,
            WHITE_KNIGHT, board);
        case "07_white_checkmate_bishop_direct_adjacent.pgn" -> TestBasicSupport.checkNonCaptureCheckmate(B3, C4,
            WHITE_BISHOP, board);
        case "08_white_checkmate_bishop_direct_range.pgn" -> TestBasicSupport.checkNonCaptureCheckmate(E2, F3,
            WHITE_BISHOP, board);
        case "09_white_checkmate_bishop_discover.pgn" -> TestBasicSupport.checkNonCaptureCheckmate(E5, C7, WHITE_BISHOP,
            board);
        case "10_white_checkmate_queen_direct_orthogonal_adjacent.pgn" -> TestBasicSupport.checkNonCaptureCheckmate(F6,
            C6, WHITE_QUEEN, board);
        case "11_white_checkmate_queen_direct_orthogonal_range.pgn" -> TestBasicSupport.checkNonCaptureCheckmate(D8, A5,
            WHITE_QUEEN, board);
        case "12_white_checkmate_queen_direct_diagonal_adjacent.pgn" -> TestBasicSupport.checkNonCaptureCheckmate(B6,
            B7, WHITE_QUEEN, board);
        case "13_white_checkmate_queen_direct_diagonal_range.pgn" -> TestBasicSupport.checkNonCaptureCheckmate(C2, B3,
            WHITE_QUEEN, board);
        case "14_white_checkmate_king_discover_orthogonal.pgn" -> TestBasicSupport.checkNonCaptureCheckmate(G7, H6,
            WHITE_KING, board);
        case "15_white_checkmate_king_discover_diagonal.pgn" -> TestBasicSupport.checkNonCaptureCheckmate(B6, B7,
            WHITE_KING, board);
        default -> throw new IllegalArgumentException();
      }
    }
  }

}
