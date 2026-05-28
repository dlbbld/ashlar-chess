// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

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

class TestBasicPromotionPieceBlack extends AbstractTestBasic {

  private static final Logger logger = Nulls.getLogger(TestBasicPromotionPieceBlack.class);

  static {
    final List<String> pgnNameList = new ArrayList<>();

    pgnNameList.add("01_black_promotion_piece_capture_no_rook.pgn");
    pgnNameList.add("02_black_promotion_piece_capture_no_knight.pgn");
    pgnNameList.add("03_black_promotion_piece_capture_no_bishop.pgn");
    pgnNameList.add("04_black_promotion_piece_capture_no_queen.pgn");
    pgnNameList.add("05_black_promotion_piece_capture_yes_rook.pgn");
    pgnNameList.add("06_black_promotion_piece_capture_yes_knight.pgn");
    pgnNameList.add("07_black_promotion_piece_capture_yes_bishop.pgn");
    pgnNameList.add("08_black_promotion_piece_capture_yes_queen.pgn");

    checkTestFolder(pgnNameList, PgnTest.BASIC_PROMOTION_PIECE_BLACK);
  }

  @SuppressWarnings("static-method")
  @Test
  void test() throws Exception {
    final PgnTestCaseList testCaseList = PgnTestCaseCatalog.getTestList(PgnTest.BASIC_PROMOTION_PIECE_BLACK);
    for (final PgnFen testCase : testCaseList.list()) {
      final Board board = testCase.game(testCaseList.pgnTest());

      logger.info(testCase.pgnName());

      switch (testCase.pgnName()) {
        case "01_black_promotion_piece_capture_no_rook.pgn" -> checkPromotion(BLACK, H2, H1, Piece.NONE,
            PromotionPieceType.ROOK, board);
        case "02_black_promotion_piece_capture_no_knight.pgn" -> checkPromotion(BLACK, H2, H1, Piece.NONE,
            PromotionPieceType.KNIGHT, board);
        case "03_black_promotion_piece_capture_no_bishop.pgn" -> checkPromotion(BLACK, H2, H1, Piece.NONE,
            PromotionPieceType.BISHOP, board);
        case "04_black_promotion_piece_capture_no_queen.pgn" -> checkPromotion(BLACK, H2, H1, Piece.NONE,
            PromotionPieceType.QUEEN, board);
        case "05_black_promotion_piece_capture_yes_rook.pgn" -> checkPromotion(BLACK, B2, C1, Piece.WHITE_BISHOP,
            PromotionPieceType.ROOK, board);
        case "06_black_promotion_piece_capture_yes_knight.pgn" -> checkPromotion(BLACK, B2, C1, Piece.WHITE_BISHOP,
            PromotionPieceType.KNIGHT, board);
        case "07_black_promotion_piece_capture_yes_bishop.pgn" -> checkPromotion(BLACK, B2, C1, Piece.WHITE_BISHOP,
            PromotionPieceType.BISHOP, board);
        case "08_black_promotion_piece_capture_yes_queen.pgn" -> checkPromotion(BLACK, B2, C1, Piece.WHITE_BISHOP,
            PromotionPieceType.QUEEN, board);
        default -> throw new IllegalArgumentException();
      }
    }
  }

}
