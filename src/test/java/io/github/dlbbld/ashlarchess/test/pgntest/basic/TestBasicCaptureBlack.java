// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.test.pgntest.basic;

import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.A1;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.A2;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.A3;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.A6;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.A7;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.B1;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.B2;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.B7;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.BLACK_BISHOP;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.BLACK_KING;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.BLACK_KNIGHT;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.BLACK_QUEEN;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.BLACK_ROOK;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.C1;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.C2;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.C6;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.D1;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.D4;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.D5;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.D6;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.D8;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.E3;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.E8;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.F1;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.F2;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.F3;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.F4;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.F5;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.F6;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.F7;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.G1;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.G2;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.G3;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.G4;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.G5;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.H1;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.H2;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.H3;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.H5;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.H6;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.H8;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.WHITE_BISHOP;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.WHITE_KNIGHT;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.WHITE_PAWN;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.WHITE_QUEEN;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.WHITE_ROOK;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;

import io.github.dlbbld.ashlarchess.test.common.utility.Loggers;
import io.github.dlbbld.ashlarchess.board.Board;
import io.github.dlbbld.ashlarchess.test.model.PgnFen;
import io.github.dlbbld.ashlarchess.test.model.PgnTestCaseList;
import io.github.dlbbld.ashlarchess.test.pgn.setup.PgnTestCaseCatalog;
import io.github.dlbbld.ashlarchess.test.pgntest.enums.PgnTest;

class TestBasicCaptureBlack {
  private static final Logger logger = Loggers.getLogger(TestBasicCaptureBlack.class);

  static {
    final List<String> pgnNames = new ArrayList<>();

    pgnNames.add("01_black_capture_rook_rook.pgn");
    pgnNames.add("02_black_capture_rook_knight.pgn");
    pgnNames.add("03_black_capture_rook_bishop.pgn");
    pgnNames.add("04_black_capture_rook_queen.pgn");
    pgnNames.add("05_black_capture_rook_pawn.pgn");
    pgnNames.add("06_black_capture_knight_rook.pgn");
    pgnNames.add("07_black_capture_knight_knight.pgn");
    pgnNames.add("08_black_capture_knight_bishop.pgn");
    pgnNames.add("09_black_capture_knight_queen.pgn");
    pgnNames.add("10_black_capture_knight_pawn.pgn");
    pgnNames.add("11_black_capture_bishop_rook.pgn");
    pgnNames.add("12_black_capture_bishop_knight.pgn");
    pgnNames.add("13_black_capture_bishop_bishop.pgn");
    pgnNames.add("14_black_capture_bishop_queen.pgn");
    pgnNames.add("15_black_capture_bishop_pawn.pgn");
    pgnNames.add("16_black_capture_queen_rook.pgn");
    pgnNames.add("17_black_capture_queen_knight.pgn");
    pgnNames.add("18_black_capture_queen_bishop.pgn");
    pgnNames.add("19_black_capture_queen_queen.pgn");
    pgnNames.add("20_black_capture_queen_pawn.pgn");
    pgnNames.add("21_black_capture_king_rook.pgn");
    pgnNames.add("22_black_capture_king_knight.pgn");
    pgnNames.add("23_black_capture_king_bishop.pgn");
    pgnNames.add("24_black_capture_king_queen.pgn");
    pgnNames.add("25_black_capture_king_pawn.pgn");

    TestBasicSupport.checkTestFolder(pgnNames, PgnTest.BASIC_CAPTURE_BLACK);
  }

  @SuppressWarnings("static-method")
  @Test
  void test() throws Exception {
    final PgnTestCaseList testCaseList = PgnTestCaseCatalog.getTestList(PgnTest.BASIC_CAPTURE_BLACK);
    for (final PgnFen testCase : testCaseList.list()) {
      final Board board = testCase.game(testCaseList.pgnTest());

      logger.info(testCase.pgnName());

      switch (testCase.pgnName()) {
        case "01_black_capture_rook_rook.pgn" -> TestBasicSupport.checkCapture(D6, D5, BLACK_ROOK, WHITE_ROOK, board);
        case "02_black_capture_rook_knight.pgn" -> TestBasicSupport.checkCapture(A6, A7, BLACK_ROOK, WHITE_KNIGHT,
            board);
        case "03_black_capture_rook_bishop.pgn" -> TestBasicSupport.checkCapture(A6, H6, BLACK_ROOK, WHITE_BISHOP,
            board);
        case "04_black_capture_rook_queen.pgn" -> TestBasicSupport.checkCapture(H8, H5, BLACK_ROOK, WHITE_QUEEN, board);
        case "05_black_capture_rook_pawn.pgn" -> TestBasicSupport.checkCapture(H6, D6, BLACK_ROOK, WHITE_PAWN, board);
        case "06_black_capture_knight_rook.pgn" -> TestBasicSupport.checkCapture(C2, A1, BLACK_KNIGHT, WHITE_ROOK,
            board);
        case "07_black_capture_knight_knight.pgn" -> TestBasicSupport.checkCapture(F3, G1, BLACK_KNIGHT, WHITE_KNIGHT,
            board);
        case "08_black_capture_knight_bishop.pgn" -> TestBasicSupport.checkCapture(G3, F1, BLACK_KNIGHT, WHITE_BISHOP,
            board);
        case "09_black_capture_knight_queen.pgn" -> TestBasicSupport.checkCapture(E3, D1, BLACK_KNIGHT, WHITE_QUEEN,
            board);
        case "10_black_capture_knight_pawn.pgn" -> TestBasicSupport.checkCapture(C6, D4, BLACK_KNIGHT, WHITE_PAWN,
            board);
        case "11_black_capture_bishop_rook.pgn" -> TestBasicSupport.checkCapture(B7, H1, BLACK_BISHOP, WHITE_ROOK,
            board);
        case "12_black_capture_bishop_knight.pgn" -> TestBasicSupport.checkCapture(F5, B1, BLACK_BISHOP, WHITE_KNIGHT,
            board);
        case "13_black_capture_bishop_bishop.pgn" -> TestBasicSupport.checkCapture(A6, F1, BLACK_BISHOP, WHITE_BISHOP,
            board);
        case "14_black_capture_bishop_queen.pgn" -> TestBasicSupport.checkCapture(G4, D1, BLACK_BISHOP, WHITE_QUEEN,
            board);
        case "15_black_capture_bishop_pawn.pgn" -> TestBasicSupport.checkCapture(H3, G2, BLACK_BISHOP, WHITE_PAWN,
            board);
        case "16_black_capture_queen_rook.pgn" -> TestBasicSupport.checkCapture(F6, A1, BLACK_QUEEN, WHITE_ROOK, board);
        case "17_black_capture_queen_knight.pgn" -> TestBasicSupport.checkCapture(F5, B1, BLACK_QUEEN, WHITE_KNIGHT,
            board);
        case "18_black_capture_queen_bishop.pgn" -> TestBasicSupport.checkCapture(D8, G5, BLACK_QUEEN, WHITE_BISHOP,
            board);
        case "19_black_capture_queen_queen.pgn" -> TestBasicSupport.checkCapture(G5, F4, BLACK_QUEEN, WHITE_QUEEN,
            board);
        case "20_black_capture_queen_pawn.pgn" -> TestBasicSupport.checkCapture(F6, F2, BLACK_QUEEN, WHITE_PAWN, board);
        case "21_black_capture_king_rook.pgn" -> TestBasicSupport.checkCapture(B2, A1, BLACK_KING, WHITE_ROOK, board);
        case "22_black_capture_king_knight.pgn" -> TestBasicSupport.checkCapture(H2, G1, BLACK_KING, WHITE_KNIGHT,
            board);
        case "23_black_capture_king_bishop.pgn" -> TestBasicSupport.checkCapture(C2, C1, BLACK_KING, WHITE_BISHOP,
            board);
        case "24_black_capture_king_queen.pgn" -> TestBasicSupport.checkCapture(E8, F7, BLACK_KING, WHITE_QUEEN, board);
        case "25_black_capture_king_pawn.pgn" -> TestBasicSupport.checkCapture(A3, A2, BLACK_KING, WHITE_PAWN, board);
        default -> throw new IllegalArgumentException();
      }
    }
  }

}
