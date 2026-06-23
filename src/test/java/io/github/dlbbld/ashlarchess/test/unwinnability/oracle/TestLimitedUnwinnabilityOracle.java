// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.test.unwinnability.oracle;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;

import io.github.dlbbld.ashlarchess.board.Board;
import io.github.dlbbld.ashlarchess.board.enums.Side;
import io.github.dlbbld.ashlarchess.pgn.LenientPgnParser;
import io.github.dlbbld.ashlarchess.pgn.PgnGame;
import io.github.dlbbld.ashlarchess.pgn.PgnUtility;
import io.github.dlbbld.ashlarchess.test.pgn.setup.PgnTestCaseCatalog;
import io.github.dlbbld.ashlarchess.test.pgntest.enums.PgnTest;
import io.github.dlbbld.ashlarchess.test.unwinnability.oracle.enums.LimitedUnwinnabilityVerdict;

class TestLimitedUnwinnabilityOracle {

  private static final Logger logger = LogManager.getLogger(TestLimitedUnwinnabilityOracle.class);

  @SuppressWarnings("static-method")
  @Test
  void testHelpmate() {
    final String pgnName = "07_helpmate3_white_to_move.pgn";

    final PgnTest pgnTest = PgnTestCaseCatalog.findPgnTestPgnNotListed(pgnName);
    final PgnGame pgnGame = LenientPgnParser.parsePath(pgnTest.getFolderPath(), pgnName);
    final Board board = PgnUtility.toBoard(pgnGame);
    logger.info(pgnName);

    assertEquals(LimitedUnwinnabilityVerdict.WINNABLE,
        LimitedUnwinnabilityOracle.calculateUnwinnability(board, Side.WHITE));
    assertEquals(LimitedUnwinnabilityVerdict.UNWINNABLE,
        LimitedUnwinnabilityOracle.calculateUnwinnability(board, Side.BLACK));
  }

  @SuppressWarnings("static-method")
  @Test
  void testForced() {
    final String pgnName = "01_forced_checkmate.pgn";

    final PgnTest pgnTest = PgnTestCaseCatalog.findPgnTestPgnNotListed(pgnName);
    final PgnGame pgnGame = LenientPgnParser.parsePath(pgnTest.getFolderPath(), pgnName);
    final Board board = PgnUtility.toBoard(pgnGame);
    logger.info(pgnName);

    assertEquals(LimitedUnwinnabilityVerdict.WINNABLE,
        LimitedUnwinnabilityOracle.calculateUnwinnability(board, Side.WHITE));
    assertEquals(LimitedUnwinnabilityVerdict.UNWINNABLE,
        LimitedUnwinnabilityOracle.calculateUnwinnability(board, Side.BLACK));
  }

  @SuppressWarnings("static-method")
  @Test
  void testPawnWall() {
    final String pgnName = "pawn_wall_ambrona_lichess_Ob5ozxgG.pgn";

    final PgnTest pgnTest = PgnTestCaseCatalog.findPgnTestPgnNotListed(pgnName);
    final PgnGame pgnGame = LenientPgnParser.parsePath(pgnTest.getFolderPath(), pgnName);
    final Board board = PgnUtility.toBoard(pgnGame);
    logger.info(pgnName);

    assertEquals(LimitedUnwinnabilityVerdict.UNWINNABLE,
        LimitedUnwinnabilityOracle.calculateUnwinnability(board, Side.WHITE));
    assertEquals(LimitedUnwinnabilityVerdict.UNWINNABLE,
        LimitedUnwinnabilityOracle.calculateUnwinnability(board, Side.BLACK));
  }

}
