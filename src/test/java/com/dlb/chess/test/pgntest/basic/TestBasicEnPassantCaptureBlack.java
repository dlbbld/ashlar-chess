package com.dlb.chess.test.pgntest.basic;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;

import com.dlb.chess.board.Board;
import com.dlb.chess.common.Nulls;
import com.dlb.chess.test.model.PgnFen;
import com.dlb.chess.test.model.PgnTestCaseList;
import com.dlb.chess.test.pgn.setup.PgnTestCaseCatalog;
import com.dlb.chess.test.pgntest.enums.PgnTest;

class TestBasicEnPassantCaptureBlack extends AbstractTestBasic {

  private static final Logger logger = Nulls.getLogger(TestBasicEnPassantCaptureBlack.class);

  static {
    final List<String> pgnNameList = new ArrayList<>();

    pgnNameList.add("01_black_en_passant_capture_left_a3.pgn");
    pgnNameList.add("02_black_en_passant_capture_left_b3.pgn");
    pgnNameList.add("03_black_en_passant_capture_left_c3.pgn");
    pgnNameList.add("04_black_en_passant_capture_left_d3.pgn");
    pgnNameList.add("05_black_en_passant_capture_left_e3.pgn");
    pgnNameList.add("06_black_en_passant_capture_left_f3.pgn");
    pgnNameList.add("07_black_en_passant_capture_left_g3.pgn");
    pgnNameList.add("08_black_en_passant_capture_right_b3.pgn");
    pgnNameList.add("09_black_en_passant_capture_right_c3.pgn");
    pgnNameList.add("10_black_en_passant_capture_right_d3.pgn");
    pgnNameList.add("11_black_en_passant_capture_right_e3.pgn");
    pgnNameList.add("12_black_en_passant_capture_right_f3.pgn");
    pgnNameList.add("13_black_en_passant_capture_right_g3.pgn");
    pgnNameList.add("14_black_en_passant_capture_right_h3.pgn");

    checkTestFolder(pgnNameList, PgnTest.BASIC_EN_PASSANT_CAPTURE_BLACK);
  }

  @SuppressWarnings("static-method")
  @Test
  void test() throws Exception {
    final PgnTestCaseList testCaseList = PgnTestCaseCatalog.getTestList(PgnTest.BASIC_EN_PASSANT_CAPTURE_BLACK);
    for (final PgnFen testCase : testCaseList.list()) {
      final Board board = testCase.game(testCaseList.pgnTest());

      logger.info(testCase.pgnName());

      switch (testCase.pgnName()) {
        case "01_black_en_passant_capture_left_a3.pgn" -> checkEnPassantCapture(BLACK, B4, A3, board);
        case "02_black_en_passant_capture_left_b3.pgn" -> checkEnPassantCapture(BLACK, C4, B3, board);
        case "03_black_en_passant_capture_left_c3.pgn" -> checkEnPassantCapture(BLACK, D4, C3, board);
        case "04_black_en_passant_capture_left_d3.pgn" -> checkEnPassantCapture(BLACK, E4, D3, board);
        case "05_black_en_passant_capture_left_e3.pgn" -> checkEnPassantCapture(BLACK, F4, E3, board);
        case "06_black_en_passant_capture_left_f3.pgn" -> checkEnPassantCapture(BLACK, G4, F3, board);
        case "07_black_en_passant_capture_left_g3.pgn" -> checkEnPassantCapture(BLACK, H4, G3, board);
        case "08_black_en_passant_capture_right_b3.pgn" -> checkEnPassantCapture(BLACK, A4, B3, board);
        case "09_black_en_passant_capture_right_c3.pgn" -> checkEnPassantCapture(BLACK, B4, C3, board);
        case "10_black_en_passant_capture_right_d3.pgn" -> checkEnPassantCapture(BLACK, C4, D3, board);
        case "11_black_en_passant_capture_right_e3.pgn" -> checkEnPassantCapture(BLACK, D4, E3, board);
        case "12_black_en_passant_capture_right_f3.pgn" -> checkEnPassantCapture(BLACK, E4, F3, board);
        case "13_black_en_passant_capture_right_g3.pgn" -> checkEnPassantCapture(BLACK, F4, G3, board);
        case "14_black_en_passant_capture_right_h3.pgn" -> checkEnPassantCapture(BLACK, G4, H3, board);
        default -> throw new IllegalArgumentException();
      }
    }
  }

}
