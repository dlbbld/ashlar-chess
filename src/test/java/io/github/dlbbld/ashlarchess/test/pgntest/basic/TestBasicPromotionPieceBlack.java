// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.test.pgntest.basic;

import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.B2;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.BLACK;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.C1;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.H1;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.H2;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;

import io.github.dlbbld.ashlarchess.board.Board;
import io.github.dlbbld.ashlarchess.board.enums.Piece;
import io.github.dlbbld.ashlarchess.board.enums.PromotionPieceType;
import io.github.dlbbld.ashlarchess.test.model.PgnFen;
import io.github.dlbbld.ashlarchess.test.model.PgnTestCaseList;
import io.github.dlbbld.ashlarchess.test.pgn.setup.PgnTestCaseCatalog;
import io.github.dlbbld.ashlarchess.test.pgntest.enums.PgnTest;

class TestBasicPromotionPieceBlack {

  @SuppressWarnings("null")
  private static final Logger logger = LogManager.getLogger(TestBasicPromotionPieceBlack.class);

  static {
    final List<String> pgnNames = new ArrayList<>();

    pgnNames.add("01_black_promotion_piece_capture_no_rook.pgn");
    pgnNames.add("02_black_promotion_piece_capture_no_knight.pgn");
    pgnNames.add("03_black_promotion_piece_capture_no_bishop.pgn");
    pgnNames.add("04_black_promotion_piece_capture_no_queen.pgn");
    pgnNames.add("05_black_promotion_piece_capture_yes_rook.pgn");
    pgnNames.add("06_black_promotion_piece_capture_yes_knight.pgn");
    pgnNames.add("07_black_promotion_piece_capture_yes_bishop.pgn");
    pgnNames.add("08_black_promotion_piece_capture_yes_queen.pgn");

    TestBasicSupport.checkTestFolder(pgnNames, PgnTest.BASIC_PROMOTION_PIECE_BLACK);
  }

  @SuppressWarnings("static-method")
  @Test
  void test() throws Exception {
    final PgnTestCaseList testCaseList = PgnTestCaseCatalog.getTestList(PgnTest.BASIC_PROMOTION_PIECE_BLACK);
    for (final PgnFen testCase : testCaseList.list()) {
      final Board board = testCase.game(testCaseList.pgnTest());

      logger.info(testCase.pgnName());

      switch (testCase.pgnName()) {
        case "01_black_promotion_piece_capture_no_rook.pgn" -> TestBasicSupport.checkPromotion(BLACK, H2, H1,
            Piece.NONE, PromotionPieceType.ROOK, board);
        case "02_black_promotion_piece_capture_no_knight.pgn" -> TestBasicSupport.checkPromotion(BLACK, H2, H1,
            Piece.NONE, PromotionPieceType.KNIGHT, board);
        case "03_black_promotion_piece_capture_no_bishop.pgn" -> TestBasicSupport.checkPromotion(BLACK, H2, H1,
            Piece.NONE, PromotionPieceType.BISHOP, board);
        case "04_black_promotion_piece_capture_no_queen.pgn" -> TestBasicSupport.checkPromotion(BLACK, H2, H1,
            Piece.NONE, PromotionPieceType.QUEEN, board);
        case "05_black_promotion_piece_capture_yes_rook.pgn" -> TestBasicSupport.checkPromotion(BLACK, B2, C1,
            Piece.WHITE_BISHOP, PromotionPieceType.ROOK, board);
        case "06_black_promotion_piece_capture_yes_knight.pgn" -> TestBasicSupport.checkPromotion(BLACK, B2, C1,
            Piece.WHITE_BISHOP, PromotionPieceType.KNIGHT, board);
        case "07_black_promotion_piece_capture_yes_bishop.pgn" -> TestBasicSupport.checkPromotion(BLACK, B2, C1,
            Piece.WHITE_BISHOP, PromotionPieceType.BISHOP, board);
        case "08_black_promotion_piece_capture_yes_queen.pgn" -> TestBasicSupport.checkPromotion(BLACK, B2, C1,
            Piece.WHITE_BISHOP, PromotionPieceType.QUEEN, board);
        default -> throw new IllegalArgumentException();
      }
    }
  }

}
