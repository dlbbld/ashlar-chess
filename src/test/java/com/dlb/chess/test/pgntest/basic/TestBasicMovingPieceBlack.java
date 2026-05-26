package com.dlb.chess.test.pgntest.basic;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;

import com.dlb.chess.board.Board;
import com.dlb.chess.common.Nulls;
import com.dlb.chess.model.LegalMoveKind;
import com.dlb.chess.test.model.PgnFen;
import com.dlb.chess.test.model.PgnTestCaseList;
import com.dlb.chess.test.pgn.setup.PgnTestCaseCatalog;
import com.dlb.chess.test.pgntest.enums.PgnTest;

class TestBasicMovingPieceBlack extends AbstractTestBasic {

  private static final Logger logger = Nulls.getLogger(TestBasicMovingPieceBlack.class);

  static {
    final List<String> pgnNameList = new ArrayList<>();

    pgnNameList.add("01_black_moving_piece_rook.pgn");
    pgnNameList.add("02_black_moving_piece_knight.pgn");
    pgnNameList.add("03_black_moving_piece_bishop.pgn");
    pgnNameList.add("04_black_moving_piece_queen.pgn");
    pgnNameList.add("05_black_moving_piece_king.pgn");
    pgnNameList.add("06_black_moving_piece_pawn.pgn");

    checkTestFolder(pgnNameList, PgnTest.BASIC_MOVING_PIECE_BLACK);
  }

  @SuppressWarnings("static-method")
  @Test
  void test() throws Exception {
    final PgnTestCaseList testCaseList = PgnTestCaseCatalog.getTestList(PgnTest.BASIC_MOVING_PIECE_BLACK);
    for (final PgnFen testCase : testCaseList.list()) {
      final Board board = testCase.game(testCaseList.pgnTest());

      logger.info(testCase.pgnName());

      switch (testCase.pgnName()) {
        case "01_black_moving_piece_rook.pgn" -> checkMovingPiece(H8, H7, BLACK_ROOK, board);
        case "02_black_moving_piece_knight.pgn" -> checkMovingPiece(G8, F6, BLACK_KNIGHT, board);
        case "03_black_moving_piece_bishop.pgn" -> checkMovingPiece(F8, G7, BLACK_BISHOP, board);
        case "04_black_moving_piece_queen.pgn" -> checkMovingPiece(D8, H4, BLACK_QUEEN, board);
        case "05_black_moving_piece_king.pgn" -> checkMovingPiece(E8, E7, BLACK_KING, board);
        case "06_black_moving_piece_pawn.pgn" -> checkMovingPiece(E7, E5, BLACK_PAWN, board,
            LegalMoveKind.PAWN_TWO_SQUARE_ADVANCE);
        default -> throw new IllegalArgumentException();
      }
    }
  }
}
