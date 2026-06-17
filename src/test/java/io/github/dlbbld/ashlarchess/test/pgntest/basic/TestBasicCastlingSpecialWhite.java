// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.test.pgntest.basic;

import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.WHITE;

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

class TestBasicCastlingSpecialWhite {

  private static final Logger logger = Nulls.getLogger(TestBasicCastlingSpecialWhite.class);

  static {
    final List<String> pgnNameList = new ArrayList<>();

    pgnNameList.add("01_white_castling_special_kingside_check.pgn");
    pgnNameList.add("02_white_castling_special_kingside_checkmate.pgn");
    pgnNameList.add("03_white_castling_special_kingside_fifty_move.pgn");
    pgnNameList.add("04_white_castling_special_kingside_seventy_five_move.pgn");
    pgnNameList.add("05_white_castling_special_kingside_stalemate.pgn");
    pgnNameList.add("06_white_castling_special_queenside_check.pgn");
    pgnNameList.add("07_white_castling_special_queenside_checkmate.pgn");
    pgnNameList.add("08_white_castling_special_queenside_fifty_move.pgn");
    pgnNameList.add("09_white_castling_special_queenside_seventy_five_move.pgn");
    pgnNameList.add("10_white_castling_special_queenside_stalemate.pgn");

    TestBasicSupport.checkTestFolder(pgnNameList, PgnTest.BASIC_CASTLING_SPECIAL_WHITE);
  }

  @SuppressWarnings("static-method")
  @Test
  void test() throws Exception {
    final PgnTestCaseList testCaseList = PgnTestCaseCatalog.getTestList(PgnTest.BASIC_CASTLING_SPECIAL_WHITE);
    for (final PgnFen testCase : testCaseList.list()) {
      final Board board = testCase.game(testCaseList.pgnTest());

      logger.info(testCase.pgnName());

      switch (testCase.pgnName()) {
        case "01_white_castling_special_kingside_check.pgn" -> TestBasicSupport.checkCastle(WHITE, CastlingMove.KING_SIDE, board);
        case "02_white_castling_special_kingside_checkmate.pgn" -> TestBasicSupport.checkCastle(WHITE, CastlingMove.KING_SIDE, board);
        case "03_white_castling_special_kingside_fifty_move.pgn" -> TestBasicSupport.checkCastle(WHITE, CastlingMove.KING_SIDE, board);
        case "04_white_castling_special_kingside_seventy_five_move.pgn" -> TestBasicSupport.checkCastle(WHITE, CastlingMove.KING_SIDE,
            board);
        case "05_white_castling_special_kingside_stalemate.pgn" -> TestBasicSupport.checkCastle(WHITE, CastlingMove.KING_SIDE, board);
        case "06_white_castling_special_queenside_check.pgn" -> TestBasicSupport.checkCastle(WHITE, CastlingMove.QUEEN_SIDE, board);
        case "07_white_castling_special_queenside_checkmate.pgn" -> TestBasicSupport.checkCastle(WHITE, CastlingMove.QUEEN_SIDE, board);
        case "08_white_castling_special_queenside_fifty_move.pgn" -> TestBasicSupport.checkCastle(WHITE, CastlingMove.QUEEN_SIDE, board);
        case "09_white_castling_special_queenside_seventy_five_move.pgn" -> TestBasicSupport.checkCastle(WHITE, CastlingMove.QUEEN_SIDE,
            board);
        case "10_white_castling_special_queenside_stalemate.pgn" -> TestBasicSupport.checkCastle(WHITE, CastlingMove.QUEEN_SIDE, board);
        default -> throw new IllegalArgumentException();
      }
    }
  }

}
