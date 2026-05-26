package com.dlb.chess.test.pgntest.basic;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;

import com.dlb.chess.board.Board;
import com.dlb.chess.board.enums.Piece;
import com.dlb.chess.board.enums.PromotionPieceType;
import com.dlb.chess.common.Nulls;
import com.dlb.chess.test.model.PgnFen;
import com.dlb.chess.test.model.PgnTestCaseList;
import com.dlb.chess.test.pgn.setup.PgnTestCaseCatalog;
import com.dlb.chess.test.pgntest.enums.PgnTest;

class TestBasicPromotionPieceWhite extends AbstractTestBasic {

  private static final Logger logger = Nulls.getLogger(TestBasicPromotionPieceWhite.class);

  static {
    final List<String> pgnNameList = new ArrayList<>();

    pgnNameList.add("01_white_promotion_piece_capture_no_rook.pgn");
    pgnNameList.add("02_white_promotion_piece_capture_no_knight.pgn");
    pgnNameList.add("03_white_promotion_piece_capture_no_bishop.pgn");
    pgnNameList.add("04_white_promotion_piece_capture_no_queen.pgn");
    pgnNameList.add("05_white_promotion_piece_capture_yes_rook.pgn");
    pgnNameList.add("06_white_promotion_piece_capture_yes_knight.pgn");
    pgnNameList.add("07_white_promotion_piece_capture_yes_bishop.pgn");
    pgnNameList.add("08_white_promotion_piece_capture_yes_queen.pgn");

    checkTestFolder(pgnNameList, PgnTest.BASIC_PROMOTION_PIECE_WHITE);
  }

  @SuppressWarnings("static-method")
  @Test
  void test() throws Exception {
    final PgnTestCaseList testCaseList = PgnTestCaseCatalog.getTestList(PgnTest.BASIC_PROMOTION_PIECE_WHITE);
    for (final PgnFen testCase : testCaseList.list()) {
      final Board board = testCase.game(testCaseList.pgnTest());

      logger.info(testCase.pgnName());

      switch (testCase.pgnName()) {
        case "01_white_promotion_piece_capture_no_rook.pgn" -> checkPromotion(WHITE, B7, B8, Piece.NONE,
            PromotionPieceType.ROOK, board);
        case "02_white_promotion_piece_capture_no_knight.pgn" -> checkPromotion(WHITE, B7, B8, Piece.NONE,
            PromotionPieceType.KNIGHT, board);
        case "03_white_promotion_piece_capture_no_bishop.pgn" -> checkPromotion(WHITE, B7, B8, Piece.NONE,
            PromotionPieceType.BISHOP, board);
        case "04_white_promotion_piece_capture_no_queen.pgn" -> checkPromotion(WHITE, B7, B8, Piece.NONE,
            PromotionPieceType.QUEEN, board);
        case "05_white_promotion_piece_capture_yes_rook.pgn" -> checkPromotion(WHITE, B7, A8, Piece.BLACK_ROOK,
            PromotionPieceType.ROOK, board);
        case "06_white_promotion_piece_capture_yes_knight.pgn" -> checkPromotion(WHITE, B7, A8, Piece.BLACK_ROOK,
            PromotionPieceType.KNIGHT, board);
        case "07_white_promotion_piece_capture_yes_bishop.pgn" -> checkPromotion(WHITE, B7, A8, Piece.BLACK_ROOK,
            PromotionPieceType.BISHOP, board);
        case "08_white_promotion_piece_capture_yes_queen.pgn" -> checkPromotion(WHITE, B7, A8, Piece.BLACK_ROOK,
            PromotionPieceType.QUEEN, board);
        default -> throw new IllegalArgumentException();
      }
    }
  }

}
