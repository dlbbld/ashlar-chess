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

class TestBasicCheckmateWhite extends AbstractTestBasic {

  private static final Logger logger = Nulls.getLogger(TestBasicCheckmateWhite.class);

  static {
    final List<String> pgnNameList = new ArrayList<>();

    pgnNameList.add("01_white_checkmate_rook_direct_adjacent.pgn");
    pgnNameList.add("02_white_checkmate_rook_direct_range.pgn");
    pgnNameList.add("03_white_checkmate_rook_discover.pgn");
    pgnNameList.add("04_white_checkmate_knight_direct.pgn");
    pgnNameList.add("05_white_checkmate_knight_discover_orthogonal.pgn");
    pgnNameList.add("06_white_checkmate_knight_discover_diagonal.pgn");
    pgnNameList.add("07_white_checkmate_bishop_direct_adjacent.pgn");
    pgnNameList.add("08_white_checkmate_bishop_direct_range.pgn");
    pgnNameList.add("09_white_checkmate_bishop_discover.pgn");
    pgnNameList.add("10_white_checkmate_queen_direct_orthogonal_adjacent.pgn");
    pgnNameList.add("11_white_checkmate_queen_direct_orthogonal_range.pgn");
    pgnNameList.add("12_white_checkmate_queen_direct_diagonal_adjacent.pgn");
    pgnNameList.add("13_white_checkmate_queen_direct_diagonal_range.pgn");
    pgnNameList.add("14_white_checkmate_king_discover_orthogonal.pgn");
    pgnNameList.add("15_white_checkmate_king_discover_diagonal.pgn");

    checkTestFolder(pgnNameList, PgnTest.BASIC_CHECKMATE_WHITE);
  }

  @SuppressWarnings("static-method")
  @Test
  void test() throws Exception {
    final PgnTestCaseList testCaseList = PgnTestCaseCatalog.getTestList(PgnTest.BASIC_CHECKMATE_WHITE);
    for (final PgnFen testCase : testCaseList.list()) {
      final Board board = testCase.game(testCaseList.pgnTest());

      logger.info(testCase.pgnName());

      switch (testCase.pgnName()) {
        case "01_white_checkmate_rook_direct_adjacent.pgn" -> checkNonCaptureCheckmate(D7, D8, WHITE_ROOK, board);
        case "02_white_checkmate_rook_direct_range.pgn" -> checkNonCaptureCheckmate(H7, H8, WHITE_ROOK, board);
        case "03_white_checkmate_rook_discover.pgn" -> checkNonCaptureCheckmate(D7, H7, WHITE_ROOK, board);
        case "04_white_checkmate_knight_direct.pgn" -> checkNonCaptureCheckmate(B4, C6, WHITE_KNIGHT, board);
        case "05_white_checkmate_knight_discover_orthogonal.pgn" -> checkNonCaptureCheckmate(D5, F6, WHITE_KNIGHT,
            board);
        case "06_white_checkmate_knight_discover_diagonal.pgn" -> checkNonCaptureCheckmate(F6, G8, WHITE_KNIGHT, board);
        case "07_white_checkmate_bishop_direct_adjacent.pgn" -> checkNonCaptureCheckmate(B3, C4, WHITE_BISHOP, board);
        case "08_white_checkmate_bishop_direct_range.pgn" -> checkNonCaptureCheckmate(E2, F3, WHITE_BISHOP, board);
        case "09_white_checkmate_bishop_discover.pgn" -> checkNonCaptureCheckmate(E5, C7, WHITE_BISHOP, board);
        case "10_white_checkmate_queen_direct_orthogonal_adjacent.pgn" -> checkNonCaptureCheckmate(F6, C6, WHITE_QUEEN,
            board);
        case "11_white_checkmate_queen_direct_orthogonal_range.pgn" -> checkNonCaptureCheckmate(D8, A5, WHITE_QUEEN,
            board);
        case "12_white_checkmate_queen_direct_diagonal_adjacent.pgn" -> checkNonCaptureCheckmate(B6, B7, WHITE_QUEEN,
            board);
        case "13_white_checkmate_queen_direct_diagonal_range.pgn" -> checkNonCaptureCheckmate(C2, B3, WHITE_QUEEN,
            board);
        case "14_white_checkmate_king_discover_orthogonal.pgn" -> checkNonCaptureCheckmate(G7, H6, WHITE_KING, board);
        case "15_white_checkmate_king_discover_diagonal.pgn" -> checkNonCaptureCheckmate(B6, B7, WHITE_KING, board);
        default -> throw new IllegalArgumentException();
      }
    }
  }

}
