package io.github.dlbbld.ashlarchess.test.pgntest.basic;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;

import io.github.dlbbld.ashlarchess.board.Board;
import io.github.dlbbld.ashlarchess.board.enums.Piece;
import io.github.dlbbld.ashlarchess.common.Nulls;
import io.github.dlbbld.ashlarchess.test.model.PgnFen;
import io.github.dlbbld.ashlarchess.test.model.PgnTestCaseList;
import io.github.dlbbld.ashlarchess.test.pgn.setup.PgnTestCaseCatalog;
import io.github.dlbbld.ashlarchess.test.pgntest.enums.PgnTest;

class TestBasicDoubleCheckWhite extends AbstractTestBasic {

  private static final Logger logger = Nulls.getLogger(TestBasicDoubleCheckWhite.class);

  static {
    final List<String> pgnNameList = new ArrayList<>();

    pgnNameList.add("01_white_double_check_rook.pgn");
    pgnNameList.add("02_white_double_check_knight_orthogonal.pgn");
    pgnNameList.add("03_white_double_check_knight_diagonal.pgn");
    pgnNameList.add("04_white_double_check_bishop.pgn");

    checkTestFolder(pgnNameList, PgnTest.BASIC_DOUBLE_CHECK_WHITE);
  }

  @SuppressWarnings("static-method")
  @Test
  void test() throws Exception {
    final PgnTestCaseList testCaseList = PgnTestCaseCatalog.getTestList(PgnTest.BASIC_DOUBLE_CHECK_WHITE);
    for (final PgnFen testCase : testCaseList.list()) {
      final Board board = testCase.game(testCaseList.pgnTest());

      logger.info(testCase.pgnName());

      switch (testCase.pgnName()) {
        case "01_white_double_check_rook.pgn" -> checkDoubleCheck(Piece.WHITE_ROOK, board);
        case "02_white_double_check_knight_orthogonal.pgn" -> checkDoubleCheck(Piece.WHITE_KNIGHT, board);
        case "03_white_double_check_knight_diagonal.pgn" -> checkDoubleCheck(Piece.WHITE_KNIGHT, board);
        case "04_white_double_check_bishop.pgn" -> checkDoubleCheck(Piece.WHITE_BISHOP, board);
        default -> throw new IllegalArgumentException();
      }
    }
  }

}
