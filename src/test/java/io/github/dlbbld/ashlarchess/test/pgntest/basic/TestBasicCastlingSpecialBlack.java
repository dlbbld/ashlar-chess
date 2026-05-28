package io.github.dlbbld.ashlarchess.test.pgntest.basic;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;

import io.github.dlbbld.ashlarchess.board.Board;
import io.github.dlbbld.ashlarchess.board.enums.CastlingMove;
import io.github.dlbbld.ashlarchess.common.Nulls;
import io.github.dlbbld.ashlarchess.test.model.PgnFen;
import io.github.dlbbld.ashlarchess.test.model.PgnTestCaseList;
import io.github.dlbbld.ashlarchess.test.pgn.setup.PgnTestCaseCatalog;
import io.github.dlbbld.ashlarchess.test.pgntest.enums.PgnTest;

class TestBasicCastlingSpecialBlack extends AbstractTestBasic {

  private static final Logger logger = Nulls.getLogger(TestBasicCastlingSpecialBlack.class);

  static {
    final List<String> pgnNameList = new ArrayList<>();

    pgnNameList.add("01_black_castling_special_kingside_check.pgn");
    pgnNameList.add("02_black_castling_special_kingside_checkmate.pgn");
    pgnNameList.add("03_black_castling_special_kingside_fifty_move.pgn");
    pgnNameList.add("04_black_castling_special_kingside_seventy_five_move.pgn");
    pgnNameList.add("05_black_castling_special_kingside_stalemate.pgn");
    pgnNameList.add("06_black_castling_special_queenside_check.pgn");
    pgnNameList.add("07_black_castling_special_queenside_checkmate.pgn");
    pgnNameList.add("08_black_castling_special_queenside_fifty_move.pgn");
    pgnNameList.add("09_black_castling_special_queenside_seventy_five_move.pgn");
    pgnNameList.add("10_black_castling_special_queenside_stalemate.pgn");

    checkTestFolder(pgnNameList, PgnTest.BASIC_CASTLING_SPECIAL_BLACK);
  }

  @SuppressWarnings("static-method")
  @Test
  void test() throws Exception {
    final PgnTestCaseList testCaseList = PgnTestCaseCatalog.getTestList(PgnTest.BASIC_CASTLING_SPECIAL_BLACK);
    for (final PgnFen testCase : testCaseList.list()) {
      final Board board = testCase.game(testCaseList.pgnTest());

      logger.info(testCase.pgnName());

      switch (testCase.pgnName()) {
        case "01_black_castling_special_kingside_check.pgn" -> checkCastle(BLACK, CastlingMove.KING_SIDE, board);
        case "02_black_castling_special_kingside_checkmate.pgn" -> checkCastle(BLACK, CastlingMove.KING_SIDE, board);
        case "03_black_castling_special_kingside_fifty_move.pgn" -> checkCastle(BLACK, CastlingMove.KING_SIDE, board);
        case "04_black_castling_special_kingside_seventy_five_move.pgn" -> checkCastle(BLACK, CastlingMove.KING_SIDE,
            board);
        case "05_black_castling_special_kingside_stalemate.pgn" -> checkCastle(BLACK, CastlingMove.KING_SIDE, board);
        case "06_black_castling_special_queenside_check.pgn" -> checkCastle(BLACK, CastlingMove.QUEEN_SIDE, board);
        case "07_black_castling_special_queenside_checkmate.pgn" -> checkCastle(BLACK, CastlingMove.QUEEN_SIDE, board);
        case "08_black_castling_special_queenside_fifty_move.pgn" -> checkCastle(BLACK, CastlingMove.QUEEN_SIDE, board);
        case "09_black_castling_special_queenside_seventy_five_move.pgn" -> checkCastle(BLACK, CastlingMove.QUEEN_SIDE,
            board);
        case "10_black_castling_special_queenside_stalemate.pgn" -> checkCastle(BLACK, CastlingMove.QUEEN_SIDE, board);
        default -> throw new IllegalArgumentException();
      }
    }
  }

}
