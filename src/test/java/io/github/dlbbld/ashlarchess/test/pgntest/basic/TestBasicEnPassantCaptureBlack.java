// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.test.pgntest.basic;

import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.A3;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.A4;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.B3;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.B4;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.BLACK;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.C3;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.C4;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.D3;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.D4;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.E3;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.E4;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.F3;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.F4;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.G3;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.G4;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.H3;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.H4;

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

class TestBasicEnPassantCaptureBlack {

  private static final Logger logger = Nulls.getLogger(TestBasicEnPassantCaptureBlack.class);

  static {
    final List<String> pgnNames = new ArrayList<>();

    pgnNames.add("01_black_en_passant_capture_left_a3.pgn");
    pgnNames.add("02_black_en_passant_capture_left_b3.pgn");
    pgnNames.add("03_black_en_passant_capture_left_c3.pgn");
    pgnNames.add("04_black_en_passant_capture_left_d3.pgn");
    pgnNames.add("05_black_en_passant_capture_left_e3.pgn");
    pgnNames.add("06_black_en_passant_capture_left_f3.pgn");
    pgnNames.add("07_black_en_passant_capture_left_g3.pgn");
    pgnNames.add("08_black_en_passant_capture_right_b3.pgn");
    pgnNames.add("09_black_en_passant_capture_right_c3.pgn");
    pgnNames.add("10_black_en_passant_capture_right_d3.pgn");
    pgnNames.add("11_black_en_passant_capture_right_e3.pgn");
    pgnNames.add("12_black_en_passant_capture_right_f3.pgn");
    pgnNames.add("13_black_en_passant_capture_right_g3.pgn");
    pgnNames.add("14_black_en_passant_capture_right_h3.pgn");

    TestBasicSupport.checkTestFolder(pgnNames, PgnTest.BASIC_EN_PASSANT_CAPTURE_BLACK);
  }

  @SuppressWarnings("static-method")
  @Test
  void test() throws Exception {
    final PgnTestCaseList testCaseList = PgnTestCaseCatalog.getTestList(PgnTest.BASIC_EN_PASSANT_CAPTURE_BLACK);
    for (final PgnFen testCase : testCaseList.list()) {
      final Board board = testCase.game(testCaseList.pgnTest());

      logger.info(testCase.pgnName());

      switch (testCase.pgnName()) {
        case "01_black_en_passant_capture_left_a3.pgn" -> TestBasicSupport.checkEnPassantCapture(BLACK, B4, A3, board);
        case "02_black_en_passant_capture_left_b3.pgn" -> TestBasicSupport.checkEnPassantCapture(BLACK, C4, B3, board);
        case "03_black_en_passant_capture_left_c3.pgn" -> TestBasicSupport.checkEnPassantCapture(BLACK, D4, C3, board);
        case "04_black_en_passant_capture_left_d3.pgn" -> TestBasicSupport.checkEnPassantCapture(BLACK, E4, D3, board);
        case "05_black_en_passant_capture_left_e3.pgn" -> TestBasicSupport.checkEnPassantCapture(BLACK, F4, E3, board);
        case "06_black_en_passant_capture_left_f3.pgn" -> TestBasicSupport.checkEnPassantCapture(BLACK, G4, F3, board);
        case "07_black_en_passant_capture_left_g3.pgn" -> TestBasicSupport.checkEnPassantCapture(BLACK, H4, G3, board);
        case "08_black_en_passant_capture_right_b3.pgn" -> TestBasicSupport.checkEnPassantCapture(BLACK, A4, B3, board);
        case "09_black_en_passant_capture_right_c3.pgn" -> TestBasicSupport.checkEnPassantCapture(BLACK, B4, C3, board);
        case "10_black_en_passant_capture_right_d3.pgn" -> TestBasicSupport.checkEnPassantCapture(BLACK, C4, D3, board);
        case "11_black_en_passant_capture_right_e3.pgn" -> TestBasicSupport.checkEnPassantCapture(BLACK, D4, E3, board);
        case "12_black_en_passant_capture_right_f3.pgn" -> TestBasicSupport.checkEnPassantCapture(BLACK, E4, F3, board);
        case "13_black_en_passant_capture_right_g3.pgn" -> TestBasicSupport.checkEnPassantCapture(BLACK, F4, G3, board);
        case "14_black_en_passant_capture_right_h3.pgn" -> TestBasicSupport.checkEnPassantCapture(BLACK, G4, H3, board);
        default -> throw new IllegalArgumentException();
      }
    }
  }

}
