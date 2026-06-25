// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.test.fen.roundtrip;

import java.nio.file.Path;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;

import io.github.dlbbld.ashlarchess.board.Board;
import io.github.dlbbld.ashlarchess.board.MoveSpecification;
import io.github.dlbbld.ashlarchess.pgn.PgnGame;
import io.github.dlbbld.ashlarchess.pgn.PgnMove;
import io.github.dlbbld.ashlarchess.test.RestrictTestConstants;
import io.github.dlbbld.ashlarchess.test.common.utility.Loggers;
import io.github.dlbbld.ashlarchess.test.model.PgnFen;
import io.github.dlbbld.ashlarchess.test.model.PgnTestCaseList;
import io.github.dlbbld.ashlarchess.test.pgn.parser.PgnCacheForStrictPgnParserTestCases;
import io.github.dlbbld.ashlarchess.test.pgn.setup.PgnTestCaseCatalog;

class TestFenRoundtripPgn {
  private static final Logger logger = Loggers.getLogger(TestFenRoundtripPgn.class);

  @SuppressWarnings("static-method")
  @Test
  void testPgnSample() {

    for (final PgnTestCaseList testCaseList : PgnTestCaseCatalog.getRestrictedTestCaseLists()) {
      if (RestrictTestConstants.IS_RESTRICT_PGN_FEN_PARSER_ALL_TEST) {
        switch (testCaseList.pgnTest()) {
          case BASIC_CHECK_WHITE:
          case BASIC_CHECK_BLACK:
          case BASIC_CHECKMATE_WHITE:
          case BASIC_CHECKMATE_BLACK:
          case BASIC_STALEMATE:
            break;
          // $CASES-OMITTED$
          default:
            continue;
        }
      }
      for (final PgnFen testCase : testCaseList.list()) {
        checkFenRoundtrip(testCaseList.pgnTest().getFolderPath(), testCase.pgnName());
      }
    }
  }

  private static void checkFenRoundtrip(Path folderPath, String pgnName) {

    logger.info(pgnName);

    final PgnGame pgnGame = PgnCacheForStrictPgnParserTestCases.getPgn(folderPath, pgnName);

    final Board board = new Board(pgnGame.startFen());
    for (final PgnMove move : pgnGame.moves()) {
      board.moveStrict(move.san());
    }
    final List<MoveSpecification> moves = board.getPerformedMoveSpecifications();
    TestFenRoundtripSupport.checkFenRoundtrip(pgnGame.startFen().fen(), moves);
  }

}
