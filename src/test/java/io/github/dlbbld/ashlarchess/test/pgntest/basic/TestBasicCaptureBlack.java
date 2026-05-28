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

class TestBasicCaptureBlack extends AbstractTestBasic {

  private static final Logger logger = Nulls.getLogger(TestBasicCaptureBlack.class);

  static {
    final List<String> pgnNameList = new ArrayList<>();

    pgnNameList.add("01_black_capture_rook_rook.pgn");
    pgnNameList.add("02_black_capture_rook_knight.pgn");
    pgnNameList.add("03_black_capture_rook_bishop.pgn");
    pgnNameList.add("04_black_capture_rook_queen.pgn");
    pgnNameList.add("05_black_capture_rook_pawn.pgn");
    pgnNameList.add("06_black_capture_knight_rook.pgn");
    pgnNameList.add("07_black_capture_knight_knight.pgn");
    pgnNameList.add("08_black_capture_knight_bishop.pgn");
    pgnNameList.add("09_black_capture_knight_queen.pgn");
    pgnNameList.add("10_black_capture_knight_pawn.pgn");
    pgnNameList.add("11_black_capture_bishop_rook.pgn");
    pgnNameList.add("12_black_capture_bishop_knight.pgn");
    pgnNameList.add("13_black_capture_bishop_bishop.pgn");
    pgnNameList.add("14_black_capture_bishop_queen.pgn");
    pgnNameList.add("15_black_capture_bishop_pawn.pgn");
    pgnNameList.add("16_black_capture_queen_rook.pgn");
    pgnNameList.add("17_black_capture_queen_knight.pgn");
    pgnNameList.add("18_black_capture_queen_bishop.pgn");
    pgnNameList.add("19_black_capture_queen_queen.pgn");
    pgnNameList.add("20_black_capture_queen_pawn.pgn");
    pgnNameList.add("21_black_capture_king_rook.pgn");
    pgnNameList.add("22_black_capture_king_knight.pgn");
    pgnNameList.add("23_black_capture_king_bishop.pgn");
    pgnNameList.add("24_black_capture_king_queen.pgn");
    pgnNameList.add("25_black_capture_king_pawn.pgn");

    checkTestFolder(pgnNameList, PgnTest.BASIC_CAPTURE_BLACK);
  }

  @SuppressWarnings("static-method")
  @Test
  void test() throws Exception {
    final PgnTestCaseList testCaseList = PgnTestCaseCatalog.getTestList(PgnTest.BASIC_CAPTURE_BLACK);
    for (final PgnFen testCase : testCaseList.list()) {
      final Board board = testCase.game(testCaseList.pgnTest());

      logger.info(testCase.pgnName());

      switch (testCase.pgnName()) {
        case "01_black_capture_rook_rook.pgn" -> checkCapture(D6, D5, BLACK_ROOK, WHITE_ROOK, board);
        case "02_black_capture_rook_knight.pgn" -> checkCapture(A6, A7, BLACK_ROOK, WHITE_KNIGHT, board);
        case "03_black_capture_rook_bishop.pgn" -> checkCapture(A6, H6, BLACK_ROOK, WHITE_BISHOP, board);
        case "04_black_capture_rook_queen.pgn" -> checkCapture(H8, H5, BLACK_ROOK, WHITE_QUEEN, board);
        case "05_black_capture_rook_pawn.pgn" -> checkCapture(H6, D6, BLACK_ROOK, WHITE_PAWN, board);
        case "06_black_capture_knight_rook.pgn" -> checkCapture(C2, A1, BLACK_KNIGHT, WHITE_ROOK, board);
        case "07_black_capture_knight_knight.pgn" -> checkCapture(F3, G1, BLACK_KNIGHT, WHITE_KNIGHT, board);
        case "08_black_capture_knight_bishop.pgn" -> checkCapture(G3, F1, BLACK_KNIGHT, WHITE_BISHOP, board);
        case "09_black_capture_knight_queen.pgn" -> checkCapture(E3, D1, BLACK_KNIGHT, WHITE_QUEEN, board);
        case "10_black_capture_knight_pawn.pgn" -> checkCapture(C6, D4, BLACK_KNIGHT, WHITE_PAWN, board);
        case "11_black_capture_bishop_rook.pgn" -> checkCapture(B7, H1, BLACK_BISHOP, WHITE_ROOK, board);
        case "12_black_capture_bishop_knight.pgn" -> checkCapture(F5, B1, BLACK_BISHOP, WHITE_KNIGHT, board);
        case "13_black_capture_bishop_bishop.pgn" -> checkCapture(A6, F1, BLACK_BISHOP, WHITE_BISHOP, board);
        case "14_black_capture_bishop_queen.pgn" -> checkCapture(G4, D1, BLACK_BISHOP, WHITE_QUEEN, board);
        case "15_black_capture_bishop_pawn.pgn" -> checkCapture(H3, G2, BLACK_BISHOP, WHITE_PAWN, board);
        case "16_black_capture_queen_rook.pgn" -> checkCapture(F6, A1, BLACK_QUEEN, WHITE_ROOK, board);
        case "17_black_capture_queen_knight.pgn" -> checkCapture(F5, B1, BLACK_QUEEN, WHITE_KNIGHT, board);
        case "18_black_capture_queen_bishop.pgn" -> checkCapture(D8, G5, BLACK_QUEEN, WHITE_BISHOP, board);
        case "19_black_capture_queen_queen.pgn" -> checkCapture(G5, F4, BLACK_QUEEN, WHITE_QUEEN, board);
        case "20_black_capture_queen_pawn.pgn" -> checkCapture(F6, F2, BLACK_QUEEN, WHITE_PAWN, board);
        case "21_black_capture_king_rook.pgn" -> checkCapture(B2, A1, BLACK_KING, WHITE_ROOK, board);
        case "22_black_capture_king_knight.pgn" -> checkCapture(H2, G1, BLACK_KING, WHITE_KNIGHT, board);
        case "23_black_capture_king_bishop.pgn" -> checkCapture(C2, C1, BLACK_KING, WHITE_BISHOP, board);
        case "24_black_capture_king_queen.pgn" -> checkCapture(E8, F7, BLACK_KING, WHITE_QUEEN, board);
        case "25_black_capture_king_pawn.pgn" -> checkCapture(A3, A2, BLACK_KING, WHITE_PAWN, board);
        default -> throw new IllegalArgumentException();
      }
    }
  }

}
