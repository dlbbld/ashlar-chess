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

class TestBasicCheckmateBlack extends AbstractTestBasic {

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

    checkTestFolder(pgnNameList, PgnTest.BASIC_CHECKMATE_BLACK);
  }

  @SuppressWarnings("static-method")
  @Test
  void test() throws Exception {
    final PgnTestCaseList testCaseList = PgnTestCaseCatalog.getTestList(PgnTest.BASIC_CHECKMATE_BLACK);
    for (final PgnFen testCase : testCaseList.list()) {
      final Board board = testCase.game(testCaseList.pgnTest());

      logger.info(testCase.pgnName());

      switch (testCase.pgnName()) {
        case "01_black_checkmate_rook_direct_adjacent.pgn" -> checkNonCaptureCheckmate(D2, D8, BLACK_ROOK, board);
        case "02_black_checkmate_rook_direct_range.pgn" -> checkNonCaptureCheckmate(G3, G2, BLACK_ROOK, board);
        case "03_black_checkmate_rook_discover.pgn" -> checkNonCaptureCheckmate(B6, B7, BLACK_ROOK, board);
        case "04_black_checkmate_knight_direct.pgn" -> checkNonCaptureCheckmate(G2, F4, BLACK_KNIGHT, board);
        case "05_black_checkmate_knight_discover_orthogonal.pgn" -> checkNonCaptureCheckmate(H5, F6, BLACK_KNIGHT,
            board);
        case "06_black_checkmate_knight_discover_diagonal.pgn" -> checkNonCaptureCheckmate(B4, A6, BLACK_KNIGHT, board);
        case "07_black_checkmate_bishop_direct_adjacent.pgn" -> checkNonCaptureCheckmate(E5, H2, BLACK_BISHOP, board);
        case "08_black_checkmate_bishop_direct_range.pgn" -> checkNonCaptureCheckmate(E7, C5, BLACK_BISHOP, board);
        case "09_black_checkmate_bishop_discover.pgn" -> checkNonCaptureCheckmate(G3, E5, BLACK_BISHOP, board);
        case "10_black_checkmate_queen_direct_orthogonal_adjacent.pgn" -> checkNonCaptureCheckmate(C6, G2, BLACK_QUEEN,
            board);
        case "11_black_checkmate_queen_direct_orthogonal_range.pgn" -> checkNonCaptureCheckmate(E7, G5, BLACK_QUEEN,
            board);
        case "12_black_checkmate_queen_direct_diagonal_adjacent.pgn" -> checkNonCaptureCheckmate(B2, F2, BLACK_QUEEN,
            board);
        case "13_black_checkmate_queen_direct_diagonal_range.pgn" -> checkNonCaptureCheckmate(D8, B6, BLACK_QUEEN,
            board);
        case "14_black_checkmate_king_discover_orthogonal.pgn" -> checkNonCaptureCheckmate(E3, D4, BLACK_KING, board);
        case "15_black_checkmate_king_discover_diagonal.pgn" -> checkNonCaptureCheckmate(F5, G5, BLACK_KING, board);
        default -> throw new IllegalArgumentException();
      }
    }
  }

}
