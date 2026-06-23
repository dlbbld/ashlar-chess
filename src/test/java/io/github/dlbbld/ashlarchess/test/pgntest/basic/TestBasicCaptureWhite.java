// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.test.pgntest.basic;

import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.A3;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.A4;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.A8;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.B2;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.B3;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.B4;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.B5;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.B6;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.BLACK_BISHOP;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.BLACK_KNIGHT;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.BLACK_PAWN;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.BLACK_QUEEN;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.BLACK_ROOK;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.C3;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.C4;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.C6;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.D3;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.D4;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.D5;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.D6;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.E2;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.E3;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.E4;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.E5;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.E7;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.F3;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.F6;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.G4;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.G5;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.G7;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.H1;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.H3;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.H4;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.H5;
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
import io.github.dlbbld.ashlarchess.internal.Nulls;
import io.github.dlbbld.ashlarchess.test.model.PgnFen;
import io.github.dlbbld.ashlarchess.test.model.PgnTestCaseList;
import io.github.dlbbld.ashlarchess.test.pgn.setup.PgnTestCaseCatalog;
import io.github.dlbbld.ashlarchess.test.pgntest.enums.PgnTest;

class TestBasicCaptureWhite {

  private static final Logger logger = Nulls.getLogger(TestBasicCaptureWhite.class);

  static {
    final List<String> pgnNames = new ArrayList<>();

    pgnNames.add("01_white_capture_rook_rook.pgn");
    pgnNames.add("02_white_capture_rook_knight.pgn");
    pgnNames.add("03_white_capture_rook_bishop.pgn");
    pgnNames.add("04_white_capture_rook_queen.pgn");
    pgnNames.add("05_white_capture_rook_pawn.pgn");
    pgnNames.add("06_white_capture_knight_rook.pgn");
    pgnNames.add("07_white_capture_knight_knight.pgn");
    pgnNames.add("08_white_capture_knight_bishop.pgn");
    pgnNames.add("09_white_capture_knight_queen.pgn");
    pgnNames.add("10_white_capture_knight_pawn.pgn");
    pgnNames.add("11_white_capture_bishop_rook.pgn");
    pgnNames.add("12_white_capture_bishop_knight.pgn");
    pgnNames.add("13_white_capture_bishop_bishop.pgn");
    pgnNames.add("14_white_capture_bishop_queen.pgn");
    pgnNames.add("15_white_capture_bishop_pawn.pgn");
    pgnNames.add("16_white_capture_queen_rook.pgn");
    pgnNames.add("17_white_capture_queen_knight.pgn");
    pgnNames.add("18_white_capture_queen_bishop.pgn");
    pgnNames.add("19_white_capture_queen_queen.pgn");
    pgnNames.add("20_white_capture_queen_pawn.pgn");
    pgnNames.add("21_white_capture_king_rook.pgn");
    pgnNames.add("22_white_capture_king_knight.pgn");
    pgnNames.add("23_white_capture_king_bishop.pgn");
    pgnNames.add("24_white_capture_king_queen.pgn");
    pgnNames.add("25_white_capture_king_pawn.pgn");

    TestBasicSupport.checkTestFolder(pgnNames, PgnTest.BASIC_CAPTURE_WHITE);
  }

  @SuppressWarnings("static-method")
  @Test
  void test() throws Exception {
    final PgnTestCaseList testCaseList = PgnTestCaseCatalog.getTestList(PgnTest.BASIC_CAPTURE_WHITE);
    for (final PgnFen testCase : testCaseList.list()) {
      final Board board = testCase.game(testCaseList.pgnTest());

      logger.info(testCase.pgnName());

      switch (testCase.pgnName()) {
        case "01_white_capture_rook_rook.pgn" -> TestBasicSupport.checkCapture(B3, B6, WHITE_ROOK, BLACK_ROOK, board);
        case "02_white_capture_rook_knight.pgn" -> TestBasicSupport.checkCapture(E3, E5, WHITE_ROOK, BLACK_KNIGHT,
            board);
        case "03_white_capture_rook_bishop.pgn" -> TestBasicSupport.checkCapture(H3, A3, WHITE_ROOK, BLACK_BISHOP,
            board);
        case "04_white_capture_rook_queen.pgn" -> TestBasicSupport.checkCapture(H1, H4, WHITE_ROOK, BLACK_QUEEN, board);
        case "05_white_capture_rook_pawn.pgn" -> TestBasicSupport.checkCapture(B3, B5, WHITE_ROOK, BLACK_PAWN, board);
        case "06_white_capture_knight_rook.pgn" -> TestBasicSupport.checkCapture(B6, A8, WHITE_KNIGHT, BLACK_ROOK,
            board);
        case "07_white_capture_knight_knight.pgn" -> TestBasicSupport.checkCapture(D5, F6, WHITE_KNIGHT, BLACK_KNIGHT,
            board);
        case "08_white_capture_knight_bishop.pgn" -> TestBasicSupport.checkCapture(E4, D6, WHITE_KNIGHT, BLACK_BISHOP,
            board);
        case "09_white_capture_knight_queen.pgn" -> TestBasicSupport.checkCapture(D5, F6, WHITE_KNIGHT, BLACK_QUEEN,
            board);
        case "10_white_capture_knight_pawn.pgn" -> TestBasicSupport.checkCapture(C3, D5, WHITE_KNIGHT, BLACK_PAWN,
            board);
        case "11_white_capture_bishop_rook.pgn" -> TestBasicSupport.checkCapture(B2, H8, WHITE_BISHOP, BLACK_ROOK,
            board);
        case "12_white_capture_bishop_knight.pgn" -> TestBasicSupport.checkCapture(G5, F6, WHITE_BISHOP, BLACK_KNIGHT,
            board);
        case "13_white_capture_bishop_bishop.pgn" -> TestBasicSupport.checkCapture(B2, G7, WHITE_BISHOP, BLACK_BISHOP,
            board);
        case "14_white_capture_bishop_queen.pgn" -> TestBasicSupport.checkCapture(B2, F6, WHITE_BISHOP, BLACK_QUEEN,
            board);
        case "15_white_capture_bishop_pawn.pgn" -> TestBasicSupport.checkCapture(G5, E7, WHITE_BISHOP, BLACK_PAWN,
            board);
        case "16_white_capture_queen_rook.pgn" -> TestBasicSupport.checkCapture(F3, A8, WHITE_QUEEN, BLACK_ROOK, board);
        case "17_white_capture_queen_knight.pgn" -> TestBasicSupport.checkCapture(A4, C6, WHITE_QUEEN, BLACK_KNIGHT,
            board);
        case "18_white_capture_queen_bishop.pgn" -> TestBasicSupport.checkCapture(F3, G4, WHITE_QUEEN, BLACK_BISHOP,
            board);
        case "19_white_capture_queen_queen.pgn" -> TestBasicSupport.checkCapture(H5, H4, WHITE_QUEEN, BLACK_QUEEN,
            board);
        case "20_white_capture_queen_pawn.pgn" -> TestBasicSupport.checkCapture(H5, H7, WHITE_QUEEN, BLACK_PAWN, board);
        case "21_white_capture_king_rook.pgn" -> TestBasicSupport.checkCapture(E3, D3, WHITE_KING, BLACK_ROOK, board);
        case "22_white_capture_king_knight.pgn" -> TestBasicSupport.checkCapture(D3, D4, WHITE_KING, BLACK_KNIGHT,
            board);
        case "23_white_capture_king_bishop.pgn" -> TestBasicSupport.checkCapture(C4, B4, WHITE_KING, BLACK_BISHOP,
            board);
        case "24_white_capture_king_queen.pgn" -> TestBasicSupport.checkCapture(E2, E3, WHITE_KING, BLACK_QUEEN, board);
        case "25_white_capture_king_pawn.pgn" -> TestBasicSupport.checkCapture(D3, D4, WHITE_KING, BLACK_PAWN, board);
        default -> throw new IllegalArgumentException();
      }
    }
  }

}
