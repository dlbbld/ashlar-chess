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

class TestBasicMovingPieceWhite extends AbstractTestBasic {

  private static final Logger logger = Nulls.getLogger(TestBasicMovingPieceWhite.class);

  static {
    final List<String> pgnNameList = new ArrayList<>();

    pgnNameList.add("01_white_moving_piece_rook.pgn");
    pgnNameList.add("02_white_moving_piece_knight.pgn");
    pgnNameList.add("03_white_moving_piece_bishop.pgn");
    pgnNameList.add("04_white_moving_piece_queen.pgn");
    pgnNameList.add("05_white_moving_piece_king.pgn");
    pgnNameList.add("06_white_moving_piece_pawn.pgn");

    checkTestFolder(pgnNameList, PgnTest.BASIC_MOVING_PIECE_WHITE);
  }

  @SuppressWarnings("static-method")
  @Test
  void test() throws Exception {
    final PgnTestCaseList testCaseList = PgnTestCaseCatalog.getTestList(PgnTest.BASIC_MOVING_PIECE_WHITE);
    for (final PgnFen testCase : testCaseList.list()) {
      final Board board = testCase.game(testCaseList.pgnTest());

      logger.info(testCase.pgnName());

      switch (testCase.pgnName()) {
        case "01_white_moving_piece_rook.pgn" -> checkMovingPiece(A1, A2, WHITE_ROOK, board);
        case "02_white_moving_piece_knight.pgn" -> checkMovingPiece(B1, C3, WHITE_KNIGHT, board);
        case "03_white_moving_piece_bishop.pgn" -> checkMovingPiece(C1, B2, WHITE_BISHOP, board);
        case "04_white_moving_piece_queen.pgn" -> checkMovingPiece(D1, D3, WHITE_QUEEN, board);
        case "05_white_moving_piece_king.pgn" -> checkMovingPiece(E1, E2, WHITE_KING, board);
        case "06_white_moving_piece_pawn.pgn" -> checkMovingPiece(E2, E4, WHITE_PAWN, board,
            LegalMoveKind.PAWN_TWO_SQUARE_ADVANCE);
        default -> throw new IllegalArgumentException();
      }
    }
  }

}
