// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.test.pgn.parser;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;

import io.github.dlbbld.ashlarchess.common.Nulls;
import io.github.dlbbld.ashlarchess.pgn.PgnGame;
import io.github.dlbbld.ashlarchess.test.pgntest.constants.PgnTestConstants;

class TestLenientPgnParserFromCustomPosition extends AbstractTestLenientPgnParser {
  private static final Path PGN_TEST_FOLDER_PATH = Nulls
      .pathResolve(PgnTestConstants.LENIENT_PGN_PARSER_TEST_ROOT_FOLDER_PATH, "fromCustomPosition");

  @SuppressWarnings("static-method")
  @Test
  void testWhite() {

    final PgnGame expected = PgnCacheForStrictPgnParserTestCases.getPgn(PGN_TEST_FOLDER_PATH,
        "98_white_start_original.pgn");

    {
      final PgnGame actual = PgnCacheForLenientPgnParserTestCases.getPgn(PGN_TEST_FOLDER_PATH, "01_white_start.pgn");
      assertEqualsArchival(expected, actual);
    }
    {
      final PgnGame actual = PgnCacheForLenientPgnParserTestCases.getPgn(PGN_TEST_FOLDER_PATH, "02_white_start.pgn");
      assertEqualsArchival(expected, actual);
    }
    {
      final PgnGame actual = PgnCacheForLenientPgnParserTestCases.getPgn(PGN_TEST_FOLDER_PATH, "03_white_start.pgn");
      assertEqualsArchival(expected, actual);
    }
    {
      final PgnGame actual = PgnCacheForLenientPgnParserTestCases.getPgn(PGN_TEST_FOLDER_PATH, "04_white_start.pgn");
      assertEqualsArchival(expected, actual);
    }
  }

  @SuppressWarnings("static-method")
  @Test
  void testBlack() {

    final PgnGame expected = PgnCacheForStrictPgnParserTestCases.getPgn(PGN_TEST_FOLDER_PATH,
        "99_black_start_original.pgn");

    {
      final PgnGame actual = PgnCacheForLenientPgnParserTestCases.getPgn(PGN_TEST_FOLDER_PATH, "05_black_start.pgn");
      assertEqualsArchival(expected, actual);
    }
    {
      final PgnGame actual = PgnCacheForLenientPgnParserTestCases.getPgn(PGN_TEST_FOLDER_PATH, "06_black_start.pgn");
      assertEqualsArchival(expected, actual);
    }
    {
      final PgnGame actual = PgnCacheForLenientPgnParserTestCases.getPgn(PGN_TEST_FOLDER_PATH, "07_black_start.pgn");
      assertEqualsArchival(expected, actual);
    }
    {
      final PgnGame actual = PgnCacheForLenientPgnParserTestCases.getPgn(PGN_TEST_FOLDER_PATH, "08_black_start.pgn");
      assertEqualsArchival(expected, actual);
    }
  }

}
