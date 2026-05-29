// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.test.pgn.parser;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;

import io.github.dlbbld.ashlarchess.common.Nulls;
import io.github.dlbbld.ashlarchess.pgn.PgnGame;
import io.github.dlbbld.ashlarchess.test.pgntest.constants.PgnTestConstants;

class TestLenientPgnParserResult extends AbstractTestLenientPgnParserException {
  private static final Path PGN_TEST_FOLDER_PATH = Nulls
      .pathResolve(PgnTestConstants.LENIENT_PGN_PARSER_TEST_ROOT_FOLDER_PATH, "result");

  @SuppressWarnings("static-method")
  @Test
  void test() {

    {
      final PgnGame expected = PgnCacheForStrictPgnParserTestCases.getPgn(PGN_TEST_FOLDER_PATH,
          "99_original_ongoing.pgn");
      final PgnGame actual = PgnCacheForLenientPgnParserTestCases.getPgn(PGN_TEST_FOLDER_PATH,
          "01_no_result_tag_no_termination_tag.pgn");
      assertEqualsArchival(expected, actual);
    }
    {
      final PgnGame expected = PgnCacheForStrictPgnParserTestCases.getPgn(PGN_TEST_FOLDER_PATH, "98_original_win.pgn");
      final PgnGame actual = PgnCacheForLenientPgnParserTestCases.getPgn(PGN_TEST_FOLDER_PATH,
          "02_no_result_tag_has_termination_tag.pgn");
      assertEqualsArchival(expected, actual);
    }
    {
      final PgnGame expected = PgnCacheForStrictPgnParserTestCases.getPgn(PGN_TEST_FOLDER_PATH, "98_original_win.pgn");
      final PgnGame actual = PgnCacheForLenientPgnParserTestCases.getPgn(PGN_TEST_FOLDER_PATH,
          "03_has_result_tag_no_termination_tag.pgn");
      assertEqualsArchival(expected, actual);
    }
  }

}