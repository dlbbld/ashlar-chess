// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.test.librarycomparison;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;

import io.github.dlbbld.ashlarchess.common.Nulls;
import io.github.dlbbld.ashlarchess.fen.model.Fen;
import io.github.dlbbld.ashlarchess.model.PgnMove;
import io.github.dlbbld.ashlarchess.pgn.PgnGame;
import io.github.dlbbld.ashlarchess.test.RestrictTestConstants;
import io.github.dlbbld.ashlarchess.test.librarycarlos.pgn.parser.PgnParserLibraryCarlos;
import io.github.dlbbld.ashlarchess.test.model.PgnFen;
import io.github.dlbbld.ashlarchess.test.model.PgnTestCaseList;
import io.github.dlbbld.ashlarchess.test.pgn.parser.PgnCacheForStrictPgnParserTestCases;
import io.github.dlbbld.ashlarchess.test.pgn.parser.model.PgnSan;
import io.github.dlbbld.ashlarchess.test.pgn.setup.PgnTestCaseCatalog;

class TestLenientPgnParserAgainstEachOther {

  // Leave empty to test all games, put a game name to only test this game.
  // private static final String ONLY_TEST_GAME = "threefold_castling_white_both_sides_lost";

  private static final Logger logger = Nulls.getLogger(TestLenientPgnParserAgainstEachOther.class);

  @SuppressWarnings("static-method")
  @Test
  void test() throws Exception {
    for (final PgnTestCaseList testCaseList : PgnTestCaseCatalog.getRestrictedTestListList()) {
      for (final PgnFen testCase : testCaseList.list()) {
        if (RestrictTestConstants.IS_RESTRICT_PGN_LENIENT_PARSER_API_AGAINST_EACH_OTHER_TEST) {
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

        final String pgnName = testCase.pgnName();

        logger.info(pgnName);

        final PgnSan parsedPgn = parcePgnSan(testCaseList.pgnTest().getFolderPath(), pgnName);
        final PgnSan carlosParsedPgn = PgnParserLibraryCarlos.parsePgnSan(testCaseList.pgnTest().getFolderPath(),
            pgnName);

        assertEquals(parsedPgn, carlosParsedPgn);
      }
    }
  }

  // we extract some of the most important information from the PGN reader we can test against API carlos PGN reader
  // we cannot test the full information against API carlos PGN reader
  public static PgnSan parcePgnSan(Path pgnFolderPath, String pgnName) {
    final PgnGame pgnGame = PgnCacheForStrictPgnParserTestCases.getPgn(pgnFolderPath, pgnName);
    final Fen startFen = pgnGame.startFen();

    final List<String> sanList = new ArrayList<>();
    for (final PgnMove pgnHalfMove : pgnGame.moveList()) {
      sanList.add(pgnHalfMove.san());
    }
    return new PgnSan(startFen.fen(), sanList);
  }

}
