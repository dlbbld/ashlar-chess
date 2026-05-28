// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.test.unwinnability;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;

import io.github.dlbbld.ashlarchess.board.Board;
import io.github.dlbbld.ashlarchess.board.enums.Side;
import io.github.dlbbld.ashlarchess.common.Nulls;
import io.github.dlbbld.ashlarchess.common.ucimove.utility.UciMoveUtility;
import io.github.dlbbld.ashlarchess.model.UciMove;
import io.github.dlbbld.ashlarchess.test.model.PgnFen;
import io.github.dlbbld.ashlarchess.test.pgn.setup.PgnTestCaseCatalog;
import io.github.dlbbld.ashlarchess.unwinnability.UnwinnabilityQuickAnalysis;
import io.github.dlbbld.ashlarchess.unwinnability.UnwinnableQuickAnalyzer;

class TestUnwinnabilityQuickHelpMateIsHelpMate {

  private static final Logger logger = Nulls.getLogger(TestUnwinnabilityQuickHelpMateIsHelpMate.class);

  @SuppressWarnings("static-method")
  @Test
  void mateLinesActuallyCheckmate() {

    final PgnFen testCase1 = PgnTestCaseCatalog.findTestCase("01_forced_checkmate.pgn");
    mateLinesActuallyCheckmate(testCase1, Side.WHITE);

    final PgnFen testCase2 = PgnTestCaseCatalog.findTestCase("lichess_pUEeHLfu.pgn");
    mateLinesActuallyCheckmate(testCase2, Side.WHITE);

    final PgnFen testCase3 = PgnTestCaseCatalog.findTestCase("lichess_UNX9jAKK.pgn");
    mateLinesActuallyCheckmate(testCase3, Side.BLACK);

    final PgnFen testCase4 = PgnTestCaseCatalog.findTestCase("lichess_sMv8Hh43.pgn");
    mateLinesActuallyCheckmate(testCase4, Side.BLACK);
  }

  private static void mateLinesActuallyCheckmate(PgnFen testCase, Side winner) {
    logger.info(testCase.pgnName());
    final Board board = testCase.finalPosition();
    final String fen = testCase.finalFen();
    final UnwinnabilityQuickAnalysis analysis = UnwinnableQuickAnalyzer.unwinnableQuick(board, winner);
    assertHelpmateLine(fen, winner, analysis.mateLine());
  }

  private static void assertHelpmateLine(String fen, Side winner, List<UciMove> mateLine) {
    final Board board = new Board(fen);
    for (final UciMove uciMove : mateLine) {
      board.move(UciMoveUtility.convertUciMoveToMoveSpecification(board, uciMove));
    }
    assertEquals(winner.getOppositeSide(), board.getHavingMove());
    assertTrue(board.isCheckmate());
  }

}
