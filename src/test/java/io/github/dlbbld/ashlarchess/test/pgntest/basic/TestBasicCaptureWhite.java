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

class TestBasicCaptureWhite extends AbstractTestBasic {

  private static final Logger logger = Nulls.getLogger(TestBasicCaptureWhite.class);

  static {
    final List<String> pgnNameList = new ArrayList<>();

    pgnNameList.add("01_white_capture_rook_rook.pgn");
    pgnNameList.add("02_white_capture_rook_knight.pgn");
    pgnNameList.add("03_white_capture_rook_bishop.pgn");
    pgnNameList.add("04_white_capture_rook_queen.pgn");
    pgnNameList.add("05_white_capture_rook_pawn.pgn");
    pgnNameList.add("06_white_capture_knight_rook.pgn");
    pgnNameList.add("07_white_capture_knight_knight.pgn");
    pgnNameList.add("08_white_capture_knight_bishop.pgn");
    pgnNameList.add("09_white_capture_knight_queen.pgn");
    pgnNameList.add("10_white_capture_knight_pawn.pgn");
    pgnNameList.add("11_white_capture_bishop_rook.pgn");
    pgnNameList.add("12_white_capture_bishop_knight.pgn");
    pgnNameList.add("13_white_capture_bishop_bishop.pgn");
    pgnNameList.add("14_white_capture_bishop_queen.pgn");
    pgnNameList.add("15_white_capture_bishop_pawn.pgn");
    pgnNameList.add("16_white_capture_queen_rook.pgn");
    pgnNameList.add("17_white_capture_queen_knight.pgn");
    pgnNameList.add("18_white_capture_queen_bishop.pgn");
    pgnNameList.add("19_white_capture_queen_queen.pgn");
    pgnNameList.add("20_white_capture_queen_pawn.pgn");
    pgnNameList.add("21_white_capture_king_rook.pgn");
    pgnNameList.add("22_white_capture_king_knight.pgn");
    pgnNameList.add("23_white_capture_king_bishop.pgn");
    pgnNameList.add("24_white_capture_king_queen.pgn");
    pgnNameList.add("25_white_capture_king_pawn.pgn");

    checkTestFolder(pgnNameList, PgnTest.BASIC_CAPTURE_WHITE);
  }

  @SuppressWarnings("static-method")
  @Test
  void test() throws Exception {
    final PgnTestCaseList testCaseList = PgnTestCaseCatalog.getTestList(PgnTest.BASIC_CAPTURE_WHITE);
    for (final PgnFen testCase : testCaseList.list()) {
      final Board board = testCase.game(testCaseList.pgnTest());

      logger.info(testCase.pgnName());

      switch (testCase.pgnName()) {
        case "01_white_capture_rook_rook.pgn" -> checkCapture(B3, B6, WHITE_ROOK, BLACK_ROOK, board);
        case "02_white_capture_rook_knight.pgn" -> checkCapture(E3, E5, WHITE_ROOK, BLACK_KNIGHT, board);
        case "03_white_capture_rook_bishop.pgn" -> checkCapture(H3, A3, WHITE_ROOK, BLACK_BISHOP, board);
        case "04_white_capture_rook_queen.pgn" -> checkCapture(H1, H4, WHITE_ROOK, BLACK_QUEEN, board);
        case "05_white_capture_rook_pawn.pgn" -> checkCapture(B3, B5, WHITE_ROOK, BLACK_PAWN, board);
        case "06_white_capture_knight_rook.pgn" -> checkCapture(B6, A8, WHITE_KNIGHT, BLACK_ROOK, board);
        case "07_white_capture_knight_knight.pgn" -> checkCapture(D5, F6, WHITE_KNIGHT, BLACK_KNIGHT, board);
        case "08_white_capture_knight_bishop.pgn" -> checkCapture(E4, D6, WHITE_KNIGHT, BLACK_BISHOP, board);
        case "09_white_capture_knight_queen.pgn" -> checkCapture(D5, F6, WHITE_KNIGHT, BLACK_QUEEN, board);
        case "10_white_capture_knight_pawn.pgn" -> checkCapture(C3, D5, WHITE_KNIGHT, BLACK_PAWN, board);
        case "11_white_capture_bishop_rook.pgn" -> checkCapture(B2, H8, WHITE_BISHOP, BLACK_ROOK, board);
        case "12_white_capture_bishop_knight.pgn" -> checkCapture(G5, F6, WHITE_BISHOP, BLACK_KNIGHT, board);
        case "13_white_capture_bishop_bishop.pgn" -> checkCapture(B2, G7, WHITE_BISHOP, BLACK_BISHOP, board);
        case "14_white_capture_bishop_queen.pgn" -> checkCapture(B2, F6, WHITE_BISHOP, BLACK_QUEEN, board);
        case "15_white_capture_bishop_pawn.pgn" -> checkCapture(G5, E7, WHITE_BISHOP, BLACK_PAWN, board);
        case "16_white_capture_queen_rook.pgn" -> checkCapture(F3, A8, WHITE_QUEEN, BLACK_ROOK, board);
        case "17_white_capture_queen_knight.pgn" -> checkCapture(A4, C6, WHITE_QUEEN, BLACK_KNIGHT, board);
        case "18_white_capture_queen_bishop.pgn" -> checkCapture(F3, G4, WHITE_QUEEN, BLACK_BISHOP, board);
        case "19_white_capture_queen_queen.pgn" -> checkCapture(H5, H4, WHITE_QUEEN, BLACK_QUEEN, board);
        case "20_white_capture_queen_pawn.pgn" -> checkCapture(H5, H7, WHITE_QUEEN, BLACK_PAWN, board);
        case "21_white_capture_king_rook.pgn" -> checkCapture(E3, D3, WHITE_KING, BLACK_ROOK, board);
        case "22_white_capture_king_knight.pgn" -> checkCapture(D3, D4, WHITE_KING, BLACK_KNIGHT, board);
        case "23_white_capture_king_bishop.pgn" -> checkCapture(C4, B4, WHITE_KING, BLACK_BISHOP, board);
        case "24_white_capture_king_queen.pgn" -> checkCapture(E2, E3, WHITE_KING, BLACK_QUEEN, board);
        case "25_white_capture_king_pawn.pgn" -> checkCapture(D3, D4, WHITE_KING, BLACK_PAWN, board);
        default -> throw new IllegalArgumentException();
      }
    }
  }

}
