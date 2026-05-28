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

class TestBasicEnPassantCaptureWhite extends AbstractTestBasic {

  private static final Logger logger = Nulls.getLogger(TestBasicEnPassantCaptureWhite.class);

  static {
    final List<String> pgnNameList = new ArrayList<>();

    pgnNameList.add("01_white_en_passant_capture_right_a6.pgn");
    pgnNameList.add("02_white_en_passant_capture_right_b6.pgn");
    pgnNameList.add("03_white_en_passant_capture_right_c6.pgn");
    pgnNameList.add("04_white_en_passant_capture_right_d6.pgn");
    pgnNameList.add("05_white_en_passant_capture_right_e6.pgn");
    pgnNameList.add("06_white_en_passant_capture_right_f6.pgn");
    pgnNameList.add("07_white_en_passant_capture_right_g6.pgn");
    pgnNameList.add("08_white_en_passant_capture_left_b6.pgn");
    pgnNameList.add("09_white_en_passant_capture_left_c6.pgn");
    pgnNameList.add("10_white_en_passant_capture_left_d6.pgn");
    pgnNameList.add("11_white_en_passant_capture_left_e6.pgn");
    pgnNameList.add("12_white_en_passant_capture_left_f6.pgn");
    pgnNameList.add("13_white_en_passant_capture_left_g6.pgn");
    pgnNameList.add("14_white_en_passant_capture_left_h6.pgn");

    checkTestFolder(pgnNameList, PgnTest.BASIC_EN_PASSANT_CAPTURE_WHITE);
  }

  @SuppressWarnings("static-method")
  @Test
  void test() throws Exception {
    final PgnTestCaseList testCaseList = PgnTestCaseCatalog.getTestList(PgnTest.BASIC_EN_PASSANT_CAPTURE_WHITE);
    for (final PgnFen testCase : testCaseList.list()) {
      final Board board = testCase.game(testCaseList.pgnTest());

      logger.info(testCase.pgnName());

      switch (testCase.pgnName()) {
        case "01_white_en_passant_capture_right_a6.pgn" -> checkEnPassantCapture(WHITE, B5, A6, board);
        case "02_white_en_passant_capture_right_b6.pgn" -> checkEnPassantCapture(WHITE, C5, B6, board);
        case "03_white_en_passant_capture_right_c6.pgn" -> checkEnPassantCapture(WHITE, D5, C6, board);
        case "04_white_en_passant_capture_right_d6.pgn" -> checkEnPassantCapture(WHITE, E5, D6, board);
        case "05_white_en_passant_capture_right_e6.pgn" -> checkEnPassantCapture(WHITE, F5, E6, board);
        case "06_white_en_passant_capture_right_f6.pgn" -> checkEnPassantCapture(WHITE, G5, F6, board);
        case "07_white_en_passant_capture_right_g6.pgn" -> checkEnPassantCapture(WHITE, H5, G6, board);
        case "08_white_en_passant_capture_left_b6.pgn" -> checkEnPassantCapture(WHITE, A5, B6, board);
        case "09_white_en_passant_capture_left_c6.pgn" -> checkEnPassantCapture(WHITE, B5, C6, board);
        case "10_white_en_passant_capture_left_d6.pgn" -> checkEnPassantCapture(WHITE, C5, D6, board);
        case "11_white_en_passant_capture_left_e6.pgn" -> checkEnPassantCapture(WHITE, D5, E6, board);
        case "12_white_en_passant_capture_left_f6.pgn" -> checkEnPassantCapture(WHITE, E5, F6, board);
        case "13_white_en_passant_capture_left_g6.pgn" -> checkEnPassantCapture(WHITE, F5, G6, board);
        case "14_white_en_passant_capture_left_h6.pgn" -> checkEnPassantCapture(WHITE, G5, H6, board);
        default -> throw new IllegalArgumentException();
      }
    }
  }

}
