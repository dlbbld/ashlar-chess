package io.github.dlbbld.ashlarchess.test.pgn.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;

import io.github.dlbbld.ashlarchess.common.Nulls;
import io.github.dlbbld.ashlarchess.pgn.PgnGame;
import io.github.dlbbld.ashlarchess.test.pgntest.constants.PgnTestConstants;

class TestLenientPgnParserFromInitialPosition extends AbstractTestLenientPgnParser {
  private static final Path PGN_TEST_FOLDER_PATH = Nulls
      .pathResolve(PgnTestConstants.LENIENT_PGN_PARSER_TEST_ROOT_FOLDER_PATH, "fromInitialPosition");

  @SuppressWarnings("static-method")
  @Test
  void testEmptyLines() {

    final PgnGame pgnGameStrict = PgnCacheForStrictPgnParserTestCases.getPgn(PGN_TEST_FOLDER_PATH, "99_original.pgn");

    {
      final PgnGame pgnGame = PgnCacheForLenientPgnParserTestCases.getPgn(PGN_TEST_FOLDER_PATH, "01_empty_line.pgn");
      assertEquals(pgnGameStrict, pgnGame);
    }
    {
      final PgnGame pgnGame = PgnCacheForLenientPgnParserTestCases.getPgn(PGN_TEST_FOLDER_PATH, "02_empty_line.pgn");
      assertEquals(pgnGameStrict, pgnGame);
    }
    {
      final PgnGame pgnGame = PgnCacheForLenientPgnParserTestCases.getPgn(PGN_TEST_FOLDER_PATH, "03_empty_line.pgn");
      assertEquals(pgnGameStrict, pgnGame);
    }
    {
      final PgnGame pgnGame = PgnCacheForLenientPgnParserTestCases.getPgn(PGN_TEST_FOLDER_PATH, "04_empty_line.pgn");
      assertEquals(pgnGameStrict, pgnGame);
    }
    {
      final PgnGame pgnGame = PgnCacheForLenientPgnParserTestCases.getPgn(PGN_TEST_FOLDER_PATH, "05_empty_line.pgn");
      assertEquals(pgnGameStrict, pgnGame);
    }
    {
      final PgnGame pgnGame = PgnCacheForLenientPgnParserTestCases.getPgn(PGN_TEST_FOLDER_PATH, "06_empty_line.pgn");
      assertEquals(pgnGameStrict, pgnGame);
    }
    {
      final PgnGame pgnGame = PgnCacheForLenientPgnParserTestCases.getPgn(PGN_TEST_FOLDER_PATH, "07_empty_line.pgn");
      assertEquals(pgnGameStrict, pgnGame);
    }
    {
      final PgnGame pgnGame = PgnCacheForLenientPgnParserTestCases.getPgn(PGN_TEST_FOLDER_PATH, "08_empty_line.pgn");
      assertEquals(pgnGameStrict, pgnGame);
    }
    {
      final PgnGame pgnGame = PgnCacheForLenientPgnParserTestCases.getPgn(PGN_TEST_FOLDER_PATH, "09_empty_line.pgn");
      assertEquals(pgnGameStrict, pgnGame);
    }
    {
      final PgnGame pgnGame = PgnCacheForLenientPgnParserTestCases.getPgn(PGN_TEST_FOLDER_PATH, "10_empty_line.pgn");
      assertEquals(pgnGameStrict, pgnGame);
    }
  }

  @SuppressWarnings("static-method")
  @Test
  void testResult() {
    final PgnGame expected = PgnCacheForStrictPgnParserTestCases.getPgn(PGN_TEST_FOLDER_PATH, "99_original.pgn");

    {
      final PgnGame actual = PgnCacheForLenientPgnParserTestCases.getPgn(PGN_TEST_FOLDER_PATH,
          "11_result_incomplete_missing_tag.pgn");
      assertEqualsArchival(expected, actual);
    }
    {
      final PgnGame actual = PgnCacheForLenientPgnParserTestCases.getPgn(PGN_TEST_FOLDER_PATH,
          "12_result_incomplete_missing_termination_marker.pgn");
      assertEqualsArchival(expected, actual);

    }
    {
      final PgnGame actual = PgnCacheForLenientPgnParserTestCases.getPgn(PGN_TEST_FOLDER_PATH,
          "13_result_incomplete_missing_tag_missing_termination_marker.pgn");
      assertEqualsArchival(expected, actual);
    }
  }

  @SuppressWarnings("static-method")
  @Test
  void testWhitespace() {
    final PgnGame expected = PgnCacheForStrictPgnParserTestCases.getPgn(PGN_TEST_FOLDER_PATH, "99_original.pgn");
    final PgnGame actual = PgnCacheForLenientPgnParserTestCases.getPgn(PGN_TEST_FOLDER_PATH,
        "14_movetext_whitespace.pgn");
    assertEquals(expected, actual);
  }

  @SuppressWarnings("static-method")
  @Test
  void testMoveNumbers() {
    final PgnGame expected = PgnCacheForStrictPgnParserTestCases.getPgn(PGN_TEST_FOLDER_PATH, "99_original.pgn");

    {
      final PgnGame actual = PgnCacheForLenientPgnParserTestCases.getPgn(PGN_TEST_FOLDER_PATH,
          "15_movetext_no_move_numbers.pgn");
      assertEquals(expected, actual);
    }
    {
      final PgnGame actual = PgnCacheForLenientPgnParserTestCases.getPgn(PGN_TEST_FOLDER_PATH,
          "16_movetext_wrong_move_numbers.pgn");
      assertEquals(expected, actual);
    }
    {
      final PgnGame actual = PgnCacheForLenientPgnParserTestCases.getPgn(PGN_TEST_FOLDER_PATH,
          "17_movetext_wrong_move_numbers.pgn");
      assertEquals(expected, actual);
    }
    {
      final PgnGame actual = PgnCacheForLenientPgnParserTestCases.getPgn(PGN_TEST_FOLDER_PATH,
          "18_movetext_wrong_move_numbers.pgn");
      assertEquals(expected, actual);
    }
    {
      final PgnGame actual = PgnCacheForLenientPgnParserTestCases.getPgn(PGN_TEST_FOLDER_PATH,
          "19_movetext_wrong_move_numbers.pgn");
      assertEquals(expected, actual);
    }
    {
      final PgnGame actual = PgnCacheForLenientPgnParserTestCases.getPgn(PGN_TEST_FOLDER_PATH,
          "20_movetext_wrong_move_numbers.pgn");
      assertEquals(expected, actual);
    }
  }

}
