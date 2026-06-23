// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.test.pgntest.basic;

import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.A7;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.A8;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.B7;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.B8;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.C7;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.C8;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.D7;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.D8;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.E7;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.E8;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.F7;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.F8;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.G7;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.G8;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.H7;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.H8;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.WHITE;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;

import io.github.dlbbld.ashlarchess.board.Board;
import io.github.dlbbld.ashlarchess.board.enums.Piece;
import io.github.dlbbld.ashlarchess.board.enums.PromotionPieceType;
import io.github.dlbbld.ashlarchess.internal.Nulls;
import io.github.dlbbld.ashlarchess.test.model.PgnFen;
import io.github.dlbbld.ashlarchess.test.model.PgnTestCaseList;
import io.github.dlbbld.ashlarchess.test.pgn.setup.PgnTestCaseCatalog;
import io.github.dlbbld.ashlarchess.test.pgntest.enums.PgnTest;

class TestBasicPromotionSquareWhite {

  private static final Logger logger = Nulls.getLogger(TestBasicPromotionSquareWhite.class);

  static {
    final List<String> pgnNames = new ArrayList<>();

    pgnNames.add("01_white_promotion_square_straight_a8.pgn");
    pgnNames.add("02_white_promotion_square_straight_b8.pgn");
    pgnNames.add("03_white_promotion_square_straight_c8.pgn");
    pgnNames.add("04_white_promotion_square_straight_d8.pgn");
    pgnNames.add("05_white_promotion_square_straight_e8.pgn");
    pgnNames.add("06_white_promotion_square_straight_f8.pgn");
    pgnNames.add("07_white_promotion_square_straight_g8.pgn");
    pgnNames.add("08_white_promotion_square_straight_h8.pgn");
    pgnNames.add("09_white_promotion_square_right_a8.pgn");
    pgnNames.add("10_white_promotion_square_right_b8.pgn");
    pgnNames.add("11_white_promotion_square_right_c8.pgn");
    pgnNames.add("12_white_promotion_square_right_d8.pgn");
    pgnNames.add("13_white_promotion_square_right_e8.pgn");
    pgnNames.add("14_white_promotion_square_right_f8.pgn");
    pgnNames.add("15_white_promotion_square_right_g8.pgn");
    pgnNames.add("16_white_promotion_square_left_b8.pgn");
    pgnNames.add("17_white_promotion_square_left_c8.pgn");
    pgnNames.add("18_white_promotion_square_left_d8.pgn");
    pgnNames.add("19_white_promotion_square_left_e8.pgn");
    pgnNames.add("20_white_promotion_square_left_f8.pgn");
    pgnNames.add("21_white_promotion_square_left_g8.pgn");
    pgnNames.add("22_white_promotion_square_left_h8.pgn");

    TestBasicSupport.checkTestFolder(pgnNames, PgnTest.BASIC_PROMOTION_SQUARE_WHITE);
  }

  @SuppressWarnings("static-method")
  @Test
  void test() throws Exception {
    final PgnTestCaseList testCaseList = PgnTestCaseCatalog.getTestList(PgnTest.BASIC_PROMOTION_SQUARE_WHITE);
    for (final PgnFen testCase : testCaseList.list()) {
      final Board board = testCase.game(testCaseList.pgnTest());

      logger.info(testCase.pgnName());

      switch (testCase.pgnName()) {
        case "01_white_promotion_square_straight_a8.pgn" -> TestBasicSupport.checkPromotion(WHITE, A7, A8, Piece.NONE,
            PromotionPieceType.QUEEN, board);
        case "02_white_promotion_square_straight_b8.pgn" -> TestBasicSupport.checkPromotion(WHITE, B7, B8, Piece.NONE,
            PromotionPieceType.QUEEN, board);
        case "03_white_promotion_square_straight_c8.pgn" -> TestBasicSupport.checkPromotion(WHITE, C7, C8, Piece.NONE,
            PromotionPieceType.QUEEN, board);
        case "04_white_promotion_square_straight_d8.pgn" -> TestBasicSupport.checkPromotion(WHITE, D7, D8, Piece.NONE,
            PromotionPieceType.QUEEN, board);
        case "05_white_promotion_square_straight_e8.pgn" -> TestBasicSupport.checkPromotion(WHITE, E7, E8, Piece.NONE,
            PromotionPieceType.QUEEN, board);
        case "06_white_promotion_square_straight_f8.pgn" -> TestBasicSupport.checkPromotion(WHITE, F7, F8, Piece.NONE,
            PromotionPieceType.QUEEN, board);
        case "07_white_promotion_square_straight_g8.pgn" -> TestBasicSupport.checkPromotion(WHITE, G7, G8, Piece.NONE,
            PromotionPieceType.QUEEN, board);
        case "08_white_promotion_square_straight_h8.pgn" -> TestBasicSupport.checkPromotion(WHITE, H7, H8, Piece.NONE,
            PromotionPieceType.QUEEN, board);
        case "09_white_promotion_square_right_a8.pgn" -> TestBasicSupport.checkPromotion(WHITE, B7, A8,
            Piece.BLACK_ROOK, PromotionPieceType.QUEEN, board);
        case "10_white_promotion_square_right_b8.pgn" -> TestBasicSupport.checkPromotion(WHITE, C7, B8,
            Piece.BLACK_ROOK, PromotionPieceType.QUEEN, board);
        case "11_white_promotion_square_right_c8.pgn" -> TestBasicSupport.checkPromotion(WHITE, D7, C8,
            Piece.BLACK_ROOK, PromotionPieceType.QUEEN, board);
        case "12_white_promotion_square_right_d8.pgn" -> TestBasicSupport.checkPromotion(WHITE, E7, D8,
            Piece.BLACK_ROOK, PromotionPieceType.QUEEN, board);
        case "13_white_promotion_square_right_e8.pgn" -> TestBasicSupport.checkPromotion(WHITE, F7, E8,
            Piece.BLACK_BISHOP, PromotionPieceType.QUEEN, board);
        case "14_white_promotion_square_right_f8.pgn" -> TestBasicSupport.checkPromotion(WHITE, G7, F8,
            Piece.BLACK_ROOK, PromotionPieceType.QUEEN, board);
        case "15_white_promotion_square_right_g8.pgn" -> TestBasicSupport.checkPromotion(WHITE, H7, G8,
            Piece.BLACK_ROOK, PromotionPieceType.QUEEN, board);
        case "16_white_promotion_square_left_b8.pgn" -> TestBasicSupport.checkPromotion(WHITE, A7, B8,
            Piece.BLACK_KNIGHT, PromotionPieceType.QUEEN, board);
        case "17_white_promotion_square_left_c8.pgn" -> TestBasicSupport.checkPromotion(WHITE, B7, C8,
            Piece.BLACK_KNIGHT, PromotionPieceType.QUEEN, board);
        case "18_white_promotion_square_left_d8.pgn" -> TestBasicSupport.checkPromotion(WHITE, C7, D8,
            Piece.BLACK_KNIGHT, PromotionPieceType.QUEEN, board);
        case "19_white_promotion_square_left_e8.pgn" -> TestBasicSupport.checkPromotion(WHITE, D7, E8,
            Piece.BLACK_KNIGHT, PromotionPieceType.QUEEN, board);
        case "20_white_promotion_square_left_f8.pgn" -> TestBasicSupport.checkPromotion(WHITE, E7, F8,
            Piece.BLACK_KNIGHT, PromotionPieceType.QUEEN, board);
        case "21_white_promotion_square_left_g8.pgn" -> TestBasicSupport.checkPromotion(WHITE, F7, G8,
            Piece.BLACK_KNIGHT, PromotionPieceType.QUEEN, board);
        case "22_white_promotion_square_left_h8.pgn" -> TestBasicSupport.checkPromotion(WHITE, G7, H8,
            Piece.BLACK_KNIGHT, PromotionPieceType.QUEEN, board);
        default -> throw new IllegalArgumentException();
      }
    }
  }

}
