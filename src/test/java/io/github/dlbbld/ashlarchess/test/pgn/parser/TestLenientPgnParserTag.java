// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.test.pgn.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;

import io.github.dlbbld.ashlarchess.common.Nulls;
import io.github.dlbbld.ashlarchess.pgn.PgnGame;
import io.github.dlbbld.ashlarchess.pgn.ResultTagValue;
import io.github.dlbbld.ashlarchess.pgn.TagUtility;
import io.github.dlbbld.ashlarchess.test.pgntest.constants.PgnTestConstants;

class TestLenientPgnParserTag extends AbstractTestLenientPgnParser {
  private static final Path PGN_TEST_FOLDER_PATH = Nulls
      .pathResolve(PgnTestConstants.LENIENT_PGN_PARSER_TEST_ROOT_FOLDER_PATH, "tag");

  @SuppressWarnings("static-method")
  @Test
  void testResultTagOnly() {

    final PgnGame actual = PgnCacheForStrictPgnParserTestCases.getPgn(PGN_TEST_FOLDER_PATH, "99_original.pgn");

    final PgnGame expected = PgnCacheForLenientPgnParserTestCases.getPgn(PGN_TEST_FOLDER_PATH,
        "01_result_tag_only.pgn");

    assertTrue(TagUtility.hasResult(expected.tagList()));
    assertEquals(ResultTagValue.ONGOING.getValue(), TagUtility.readResult(expected.tagList()));

    assertEqualsButTagList(actual, expected);
  }

  @SuppressWarnings("static-method")
  @Test
  void testNoSpaceBetweenTagNameAndValue() {

    final PgnGame actual = PgnCacheForStrictPgnParserTestCases.getPgn(PGN_TEST_FOLDER_PATH, "99_original.pgn");

    final PgnGame expected = PgnCacheForLenientPgnParserTestCases.getPgn(PGN_TEST_FOLDER_PATH,
        "02_no_space_between_tag_name_and_value.pgn");

    assertEquals(actual, expected);
  }

  @SuppressWarnings("static-method")
  @Test
  void testTooMuchSpaces() {

    final PgnGame actual = PgnCacheForStrictPgnParserTestCases.getPgn(PGN_TEST_FOLDER_PATH, "99_original.pgn");

    final PgnGame expected = PgnCacheForLenientPgnParserTestCases.getPgn(PGN_TEST_FOLDER_PATH,
        "03_too_much_space_in_tag.pgn");

    assertEquals(actual, expected);
  }

  @SuppressWarnings("static-method")
  @Test
  void testIgnoreSetUpTag() {

    final PgnGame actual = PgnCacheForStrictPgnParserTestCases.getPgn(PGN_TEST_FOLDER_PATH, "99_original.pgn");

    {
      final PgnGame expected = PgnCacheForLenientPgnParserTestCases.getPgn(PGN_TEST_FOLDER_PATH,
          "04_setup_tag_zero_remove.pgn");
      // Lenient parser preserves the redundant SetUp tag in the parsed model; archival normalisation drops it
      // because startFen is the initial position (FEN/SetUp are archival-only signals when not needed).
      assertEqualsArchival(actual, expected);
    }
    {
      final PgnGame expected = PgnCacheForLenientPgnParserTestCases.getPgn(PGN_TEST_FOLDER_PATH,
          "05_setup_tag_one_remove.pgn");
      assertEqualsArchival(actual, expected);
    }
  }

  @SuppressWarnings("static-method")
  @Test
  void testTabs() {
    final PgnGame actual = PgnCacheForStrictPgnParserTestCases.getPgn(PGN_TEST_FOLDER_PATH, "99_original.pgn");

    {
      final PgnGame expected = PgnCacheForLenientPgnParserTestCases.getPgn(PGN_TEST_FOLDER_PATH, "06_having_tabs.pgn");
      assertEquals(actual, expected);
    }
    {
      final PgnGame expected = PgnCacheForLenientPgnParserTestCases.getPgn(PGN_TEST_FOLDER_PATH, "07_having_tabs.pgn");
      assertEquals(actual, expected);
    }
  }

  @SuppressWarnings("static-method")
  @Test
  void testEmptyLines() {

    final PgnGame actual = PgnCacheForStrictPgnParserTestCases.getPgn(PGN_TEST_FOLDER_PATH, "99_original.pgn");

    {
      final PgnGame expected = PgnCacheForLenientPgnParserTestCases.getPgn(PGN_TEST_FOLDER_PATH, "08_empty_lines.pgn");
      assertEquals(actual, expected);
    }
    {
      final PgnGame expected = PgnCacheForLenientPgnParserTestCases.getPgn(PGN_TEST_FOLDER_PATH, "09_empty_lines.pgn");
      assertEquals(actual, expected);
    }

  }

  @SuppressWarnings("static-method")
  @Test
  void testNoTags() {
    final PgnGame actual = PgnCacheForStrictPgnParserTestCases.getPgn(PGN_TEST_FOLDER_PATH, "98_original.pgn");

    final PgnGame expected = PgnCacheForLenientPgnParserTestCases.getPgn(PGN_TEST_FOLDER_PATH, "10_empty_tag_list.pgn");
    assertEqualsButTagListAndResult(actual, expected);
  }

}
