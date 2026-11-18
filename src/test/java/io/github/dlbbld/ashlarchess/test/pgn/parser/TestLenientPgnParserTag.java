// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.test.pgn.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import io.github.dlbbld.ashlarchess.internal.Nulls;
import io.github.dlbbld.ashlarchess.pgn.LenientPgnParser;
import io.github.dlbbld.ashlarchess.pgn.PgnCreate;
import io.github.dlbbld.ashlarchess.pgn.PgnGame;
import io.github.dlbbld.ashlarchess.pgn.ResultTagValue;
import io.github.dlbbld.ashlarchess.pgn.Tag;
import io.github.dlbbld.ashlarchess.pgn.WriteMode;
import io.github.dlbbld.ashlarchess.pgn.internal.TagUtility;
import io.github.dlbbld.ashlarchess.test.pgntest.constants.PgnTestConstants;

class TestLenientPgnParserTag {
  private static final Path PGN_TEST_FOLDER_PATH = Nulls
      .pathResolve(PgnTestConstants.LENIENT_PGN_PARSER_TEST_ROOT_FOLDER_PATH, "tag");

  @SuppressWarnings("static-method")
  @Test
  void testResultTagOnly() {

    final PgnGame actual = PgnCacheForStrictPgnParserTestCases.getPgn(PGN_TEST_FOLDER_PATH, "99_original.pgn");

    final PgnGame expected = PgnCacheForLenientPgnParserTestCases.getPgn(PGN_TEST_FOLDER_PATH,
        "01_result_tag_only.pgn");

    assertTrue(TagUtility.hasResult(expected.tags()));
    assertEquals(ResultTagValue.ONGOING.getValue(), TagUtility.readResult(expected.tags()));

    TestLenientPgnParserSupport.assertEqualsButTags(actual, expected);
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
  void preservesRedundantSetUpAndLowercaseCustomTagOnImport() {
    final PgnGame reference = PgnCacheForStrictPgnParserTestCases.getPgn(PGN_TEST_FOLDER_PATH, "99_original.pgn");
    assertSetUpPreserved(reference, "04_setup_tag_zero_remove.pgn", "0");
    assertSetUpPreserved(reference, "05_setup_tag_one_remove.pgn", "1");
  }

  private static void assertSetUpPreserved(PgnGame reference, String file, String setUp) {
    final PgnGame game = PgnCacheForLenientPgnParserTestCases.getPgn(PGN_TEST_FOLDER_PATH, file);
    assertEquals(setUp, TagUtility.readSetUp(game.tags()));
    final List<Tag> tags = new ArrayList<>(game.tags());
    TagUtility.removeSetUpTag(tags);
    assertEquals(reference,
        new PgnGame(tags, game.startFen(), game.pregameCommentary(), game.moves(), game.terminationMarker()));
    assertEquals("  tag value with white  space  ", TagUtility.readTagValue(game, "tagWithWhiteSpace"));
    assertEquals(game, LenientPgnParser.parseText(PgnCreate.toPgnString(game)));
    assertThrows(IllegalArgumentException.class, () -> PgnCreate.toPgnString(game, WriteMode.ARCHIVAL));
  }

  @SuppressWarnings("static-method")
  @Test
  void archivalExportDropsRedundantSetUpAtTheInitialPosition() {
    final PgnGame reference = LenientPgnParser.parseText("1. e4 *");
    TestLenientPgnParserSupport.assertEqualsArchival(reference, LenientPgnParser.parseText("[SetUp \"0\"]\n\n1. e4 *"));
    TestLenientPgnParserSupport.assertEqualsArchival(reference, LenientPgnParser.parseText("[SetUp \"1\"]\n\n1. e4 *"));
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
    TestLenientPgnParserSupport.assertEqualsButTagsAndResult(actual, expected);
  }

}
