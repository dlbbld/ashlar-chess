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

class TestBasicCheckBlack extends AbstractTestBasic {

  private static final Logger logger = Nulls.getLogger(TestBasicCheckBlack.class);

  static {
    final List<String> pgnNameList = new ArrayList<>();

    pgnNameList.add("01_black_check_rook_direct_adjacent.pgn");
    pgnNameList.add("02_black_check_rook_direct_range.pgn");
    pgnNameList.add("03_black_check_rook_discover.pgn");
    pgnNameList.add("04_black_check_knight_direct.pgn");
    pgnNameList.add("05_black_check_knight_discover_orthogonal.pgn");
    pgnNameList.add("06_black_check_knight_discover_diagonal.pgn");
    pgnNameList.add("07_black_check_bishop_direct_adjacent.pgn");
    pgnNameList.add("08_black_check_bishop_direct_range.pgn");
    pgnNameList.add("09_black_check_bishop_discover.pgn");
    pgnNameList.add("10_black_check_queen_direct_orthogonal_adjacent.pgn");
    pgnNameList.add("11_black_check_queen_direct_orthogonal_range.pgn");
    pgnNameList.add("12_black_check_queen_direct_diagonal_adjacent.pgn");
    pgnNameList.add("13_black_check_queen_direct_diagonal_range.pgn");
    pgnNameList.add("14_black_check_king_discover_orthogonal.pgn");
    pgnNameList.add("15_black_check_king_discover_diagonal.pgn");

    checkTestFolder(pgnNameList, PgnTest.BASIC_CHECK_BLACK);
  }

  @SuppressWarnings("static-method")
  @Test
  void test() throws Exception {
    final PgnTestCaseList testCaseList = PgnTestCaseCatalog.getTestList(PgnTest.BASIC_CHECK_BLACK);
    for (final PgnFen testCase : testCaseList.list()) {
      logger.info(testCase.pgnName());
      final Board board = testCase.game(testCaseList.pgnTest());

      switch (testCase.pgnName()) {
        case "01_black_check_rook_direct_adjacent.pgn" -> checkNonCaptureCheck(C6, C4, BLACK_ROOK, board);
        case "02_black_check_rook_direct_range.pgn" -> checkNonCaptureCheck(A4, A3, BLACK_ROOK, board);
        case "03_black_check_rook_discover.pgn" -> checkNonCaptureCheck(B4, B7, BLACK_ROOK, board);
        case "04_black_check_knight_direct.pgn" -> checkNonCaptureCheck(D4, F3, BLACK_KNIGHT, board);
        case "05_black_check_knight_discover_orthogonal.pgn" -> checkNonCaptureCheck(E5, G6, BLACK_KNIGHT, board);
        case "06_black_check_knight_discover_diagonal.pgn" -> checkNonCaptureCheck(C5, E6, BLACK_KNIGHT, board);
        case "07_black_check_bishop_direct_adjacent.pgn" -> checkNonCaptureCheck(C5, F2, BLACK_BISHOP, board);
        case "08_black_check_bishop_direct_range.pgn" -> checkNonCaptureCheck(C8, B7, BLACK_BISHOP, board);
        case "09_black_check_bishop_discover.pgn" -> checkNonCaptureCheck(E6, G4, BLACK_BISHOP, board);
        case "10_black_check_queen_direct_orthogonal_adjacent.pgn" -> checkNonCaptureCheck(G4, E2, BLACK_QUEEN, board);
        case "11_black_check_queen_direct_orthogonal_range.pgn" -> checkNonCaptureCheck(D6, E5, BLACK_QUEEN, board);
        case "12_black_check_queen_direct_diagonal_adjacent.pgn" -> checkNonCaptureCheck(G5, D2, BLACK_QUEEN, board);
        case "13_black_check_queen_direct_diagonal_range.pgn" -> checkNonCaptureCheck(D8, B6, BLACK_QUEEN, board);
        case "14_black_check_king_discover_orthogonal.pgn" -> checkNonCaptureCheck(E7, D6, BLACK_KING, board);
        case "15_black_check_king_discover_diagonal.pgn" -> checkNonCaptureCheck(G5, G4, BLACK_KING, board);
        default -> throw new IllegalArgumentException();
      }
    }
  }

}
