package io.github.dlbbld.ashlarchess.test.pgntest.basic;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;

import io.github.dlbbld.ashlarchess.board.Board;
import io.github.dlbbld.ashlarchess.board.enums.Piece;
import io.github.dlbbld.ashlarchess.board.enums.PromotionPieceType;
import io.github.dlbbld.ashlarchess.common.Nulls;
import io.github.dlbbld.ashlarchess.test.model.PgnFen;
import io.github.dlbbld.ashlarchess.test.model.PgnTestCaseList;
import io.github.dlbbld.ashlarchess.test.pgn.setup.PgnTestCaseCatalog;
import io.github.dlbbld.ashlarchess.test.pgntest.enums.PgnTest;

class TestBasicPromotionSquareBlack extends AbstractTestBasic {

  private static final Logger logger = Nulls.getLogger(TestBasicPromotionSquareBlack.class);

  static {
    final List<String> pgnNameList = new ArrayList<>();

    pgnNameList.add("01_black_promotion_square_straight_a1.pgn");
    pgnNameList.add("02_black_promotion_square_straight_b1.pgn");
    pgnNameList.add("03_black_promotion_square_straight_c1.pgn");
    pgnNameList.add("04_black_promotion_square_straight_d1.pgn");
    pgnNameList.add("05_black_promotion_square_straight_e1.pgn");
    pgnNameList.add("06_black_promotion_square_straight_f1.pgn");
    pgnNameList.add("07_black_promotion_square_straight_g1.pgn");
    pgnNameList.add("08_black_promotion_square_straight_h1.pgn");
    pgnNameList.add("09_black_promotion_square_left_a1.pgn");
    pgnNameList.add("10_black_promotion_square_left_b1.pgn");
    pgnNameList.add("11_black_promotion_square_left_c1.pgn");
    pgnNameList.add("12_black_promotion_square_left_d1.pgn");
    pgnNameList.add("13_black_promotion_square_left_e1.pgn");
    pgnNameList.add("14_black_promotion_square_left_f1.pgn");
    pgnNameList.add("15_black_promotion_square_left_g1.pgn");
    pgnNameList.add("16_black_promotion_square_right_b1.pgn");
    pgnNameList.add("17_black_promotion_square_right_c1.pgn");
    pgnNameList.add("18_black_promotion_square_right_d1.pgn");
    pgnNameList.add("19_black_promotion_square_right_e1.pgn");
    pgnNameList.add("20_black_promotion_square_right_f1.pgn");
    pgnNameList.add("21_black_promotion_square_right_g1.pgn");
    pgnNameList.add("22_black_promotion_square_right_h1.pgn");

    checkTestFolder(pgnNameList, PgnTest.BASIC_PROMOTION_SQUARE_BLACK);
  }

  @SuppressWarnings("static-method")
  @Test
  void test() throws Exception {
    final PgnTestCaseList testCaseList = PgnTestCaseCatalog.getTestList(PgnTest.BASIC_PROMOTION_SQUARE_BLACK);
    for (final PgnFen testCase : testCaseList.list()) {
      final Board board = testCase.game(testCaseList.pgnTest());

      logger.info(testCase.pgnName());

      switch (testCase.pgnName()) {
        case "01_black_promotion_square_straight_a1.pgn" -> checkPromotion(BLACK, A2, A1, Piece.NONE,
            PromotionPieceType.QUEEN, board);
        case "02_black_promotion_square_straight_b1.pgn" -> checkPromotion(BLACK, B2, B1, Piece.NONE,
            PromotionPieceType.QUEEN, board);
        case "03_black_promotion_square_straight_c1.pgn" -> checkPromotion(BLACK, C2, C1, Piece.NONE,
            PromotionPieceType.QUEEN, board);
        case "04_black_promotion_square_straight_d1.pgn" -> checkPromotion(BLACK, D2, D1, Piece.NONE,
            PromotionPieceType.QUEEN, board);
        case "05_black_promotion_square_straight_e1.pgn" -> checkPromotion(BLACK, E2, E1, Piece.NONE,
            PromotionPieceType.QUEEN, board);
        case "06_black_promotion_square_straight_f1.pgn" -> checkPromotion(BLACK, F2, F1, Piece.NONE,
            PromotionPieceType.QUEEN, board);
        case "07_black_promotion_square_straight_g1.pgn" -> checkPromotion(BLACK, G2, G1, Piece.NONE,
            PromotionPieceType.QUEEN, board);
        case "08_black_promotion_square_straight_h1.pgn" -> checkPromotion(BLACK, H2, H1, Piece.NONE,
            PromotionPieceType.QUEEN, board);
        case "09_black_promotion_square_left_a1.pgn" -> checkPromotion(BLACK, B2, A1, Piece.WHITE_QUEEN,
            PromotionPieceType.QUEEN, board);
        case "10_black_promotion_square_left_b1.pgn" -> checkPromotion(BLACK, C2, B1, Piece.WHITE_QUEEN,
            PromotionPieceType.QUEEN, board);
        case "11_black_promotion_square_left_c1.pgn" -> checkPromotion(BLACK, D2, C1, Piece.WHITE_QUEEN,
            PromotionPieceType.QUEEN, board);
        case "12_black_promotion_square_left_d1.pgn" -> checkPromotion(BLACK, E2, D1, Piece.WHITE_KNIGHT,
            PromotionPieceType.QUEEN, board);
        case "13_black_promotion_square_left_e1.pgn" -> checkPromotion(BLACK, F2, E1, Piece.WHITE_QUEEN,
            PromotionPieceType.QUEEN, board);
        case "14_black_promotion_square_left_f1.pgn" -> checkPromotion(BLACK, G2, F1, Piece.WHITE_QUEEN,
            PromotionPieceType.QUEEN, board);
        case "15_black_promotion_square_left_g1.pgn" -> checkPromotion(BLACK, H2, G1, Piece.WHITE_QUEEN,
            PromotionPieceType.QUEEN, board);
        case "16_black_promotion_square_right_b1.pgn" -> checkPromotion(BLACK, A2, B1, Piece.WHITE_BISHOP,
            PromotionPieceType.QUEEN, board);
        case "17_black_promotion_square_right_c1.pgn" -> checkPromotion(BLACK, B2, C1, Piece.WHITE_BISHOP,
            PromotionPieceType.QUEEN, board);
        case "18_black_promotion_square_right_d1.pgn" -> checkPromotion(BLACK, C2, D1, Piece.WHITE_KNIGHT,
            PromotionPieceType.QUEEN, board);
        case "19_black_promotion_square_right_e1.pgn" -> checkPromotion(BLACK, D2, E1, Piece.WHITE_BISHOP,
            PromotionPieceType.QUEEN, board);
        case "20_black_promotion_square_right_f1.pgn" -> checkPromotion(BLACK, E2, F1, Piece.WHITE_BISHOP,
            PromotionPieceType.QUEEN, board);
        case "21_black_promotion_square_right_g1.pgn" -> checkPromotion(BLACK, F2, G1, Piece.WHITE_BISHOP,
            PromotionPieceType.QUEEN, board);
        case "22_black_promotion_square_right_h1.pgn" -> checkPromotion(BLACK, G2, H1, Piece.WHITE_BISHOP,
            PromotionPieceType.QUEEN, board);
        default -> throw new IllegalArgumentException();
      }
    }
  }

}
