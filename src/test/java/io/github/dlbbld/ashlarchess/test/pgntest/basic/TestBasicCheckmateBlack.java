// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.test.pgntest.basic;

import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.A6;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.B2;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.B4;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.B6;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.B7;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.BLACK_BISHOP;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.BLACK_KING;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.BLACK_KNIGHT;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.BLACK_QUEEN;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.BLACK_ROOK;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.C5;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.C6;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.D2;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.D4;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.D8;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.E3;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.E5;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.E7;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.F2;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.F4;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.F5;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.F6;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.G2;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.G3;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.G5;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.H2;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.H5;

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

class TestBasicCheckmateBlack {

  private static final Logger logger = Nulls.getLogger(TestBasicCheckmateBlack.class);

  static {
    final List<String> pgnNameList = new ArrayList<>();

    pgnNameList.add("01_black_checkmate_rook_direct_adjacent.pgn");
    pgnNameList.add("02_black_checkmate_rook_direct_range.pgn");
    pgnNameList.add("03_black_checkmate_rook_discover.pgn");
    pgnNameList.add("04_black_checkmate_knight_direct.pgn");
    pgnNameList.add("05_black_checkmate_knight_discover_orthogonal.pgn");
    pgnNameList.add("06_black_checkmate_knight_discover_diagonal.pgn");
    pgnNameList.add("07_black_checkmate_bishop_direct_adjacent.pgn");
    pgnNameList.add("08_black_checkmate_bishop_direct_range.pgn");
    pgnNameList.add("09_black_checkmate_bishop_discover.pgn");
    pgnNameList.add("10_black_checkmate_queen_direct_orthogonal_adjacent.pgn");
    pgnNameList.add("11_black_checkmate_queen_direct_orthogonal_range.pgn");
    pgnNameList.add("12_black_checkmate_queen_direct_diagonal_adjacent.pgn");
    pgnNameList.add("13_black_checkmate_queen_direct_diagonal_range.pgn");
    pgnNameList.add("14_black_checkmate_king_discover_orthogonal.pgn");
    pgnNameList.add("15_black_checkmate_king_discover_diagonal.pgn");

    TestBasicSupport.checkTestFolder(pgnNameList, PgnTest.BASIC_CHECKMATE_BLACK);
  }

  @SuppressWarnings("static-method")
  @Test
  void test() throws Exception {
    final PgnTestCaseList testCaseList = PgnTestCaseCatalog.getTestList(PgnTest.BASIC_CHECKMATE_BLACK);
    for (final PgnFen testCase : testCaseList.list()) {
      final Board board = testCase.game(testCaseList.pgnTest());

      logger.info(testCase.pgnName());

      switch (testCase.pgnName()) {
        case "01_black_checkmate_rook_direct_adjacent.pgn" -> TestBasicSupport.checkNonCaptureCheckmate(D2, D8,
            BLACK_ROOK, board);
        case "02_black_checkmate_rook_direct_range.pgn" -> TestBasicSupport.checkNonCaptureCheckmate(G3, G2, BLACK_ROOK,
            board);
        case "03_black_checkmate_rook_discover.pgn" -> TestBasicSupport.checkNonCaptureCheckmate(B6, B7, BLACK_ROOK,
            board);
        case "04_black_checkmate_knight_direct.pgn" -> TestBasicSupport.checkNonCaptureCheckmate(G2, F4, BLACK_KNIGHT,
            board);
        case "05_black_checkmate_knight_discover_orthogonal.pgn" -> TestBasicSupport.checkNonCaptureCheckmate(H5, F6,
            BLACK_KNIGHT, board);
        case "06_black_checkmate_knight_discover_diagonal.pgn" -> TestBasicSupport.checkNonCaptureCheckmate(B4, A6,
            BLACK_KNIGHT, board);
        case "07_black_checkmate_bishop_direct_adjacent.pgn" -> TestBasicSupport.checkNonCaptureCheckmate(E5, H2,
            BLACK_BISHOP, board);
        case "08_black_checkmate_bishop_direct_range.pgn" -> TestBasicSupport.checkNonCaptureCheckmate(E7, C5,
            BLACK_BISHOP, board);
        case "09_black_checkmate_bishop_discover.pgn" -> TestBasicSupport.checkNonCaptureCheckmate(G3, E5, BLACK_BISHOP,
            board);
        case "10_black_checkmate_queen_direct_orthogonal_adjacent.pgn" -> TestBasicSupport.checkNonCaptureCheckmate(C6,
            G2, BLACK_QUEEN, board);
        case "11_black_checkmate_queen_direct_orthogonal_range.pgn" -> TestBasicSupport.checkNonCaptureCheckmate(E7, G5,
            BLACK_QUEEN, board);
        case "12_black_checkmate_queen_direct_diagonal_adjacent.pgn" -> TestBasicSupport.checkNonCaptureCheckmate(B2,
            F2, BLACK_QUEEN, board);
        case "13_black_checkmate_queen_direct_diagonal_range.pgn" -> TestBasicSupport.checkNonCaptureCheckmate(D8, B6,
            BLACK_QUEEN, board);
        case "14_black_checkmate_king_discover_orthogonal.pgn" -> TestBasicSupport.checkNonCaptureCheckmate(E3, D4,
            BLACK_KING, board);
        case "15_black_checkmate_king_discover_diagonal.pgn" -> TestBasicSupport.checkNonCaptureCheckmate(F5, G5,
            BLACK_KING, board);
        default -> throw new IllegalArgumentException();
      }
    }
  }

}
