package com.dlb.chess.test.pgn.parser.commentary;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import com.dlb.chess.common.Nulls;
import com.dlb.chess.pgn.LenientPgnParser;
import com.dlb.chess.pgn.LenientPgnParserValidationException;
import com.dlb.chess.pgn.LenientPgnParserValidationProblem;
import com.dlb.chess.pgn.LenientPgnParserValidationResult;
import com.dlb.chess.pgn.PgnGame;
import com.dlb.chess.pgn.StrictPgnParser;
import com.dlb.chess.pgn.StrictPgnParserValidationException;
import com.dlb.chess.pgn.StrictPgnParserValidationProblem;
import com.dlb.chess.pgn.StrictPgnParserValidationResult;
import com.dlb.chess.san.SanValidationProblem;
import com.dlb.chess.test.pgntest.constants.PgnTestConstants;

/**
 * Cross-parser fidelity: each fixture must yield the same {@link PgnGame} through both strict and lenient.
 */
class TestCommentaryFixturesBothParsers {

  private static final Path COMMENTARY_FOLDER_PATH = Nulls
      .pathResolve(PgnTestConstants.PGN_PARSER_TEST_ROOT_FOLDER_PATH, "common/commentary");

  private static final Path PREGAME_COMMENTARY_SUCCESS_FOLDER_PATH = Nulls.pathResolve(COMMENTARY_FOLDER_PATH,
      "pregameCommentary/success");

  private static final Path PREGAME_COMMENTARY_EXCEPTION_FOLDER_PATH = Nulls.pathResolve(COMMENTARY_FOLDER_PATH,
      "pregameCommentary/exception");

  private static final Path NON_PREGAME_COMMENTARY_SUCCESS_FOLDER_PATH = Nulls.pathResolve(COMMENTARY_FOLDER_PATH,
      "nonPregameCommentary/success");

  private static final Path NON_PREGAME_COMMENTARY_EXCEPTION_FOLDER_PATH = Nulls.pathResolve(COMMENTARY_FOLDER_PATH,
      "nonPregameCommentary/exception");

  private static final Path COMBINED_COMMENTARY_SUCCESS_FOLDER_PATH = Nulls.pathResolve(COMMENTARY_FOLDER_PATH,
      "combinedCommentary/success");

  @ParameterizedTest(name = "pregameCommentary/success/{0}")
  @ValueSource(strings = { "01_example.pgn", "02_example.pgn", "03_example.pgn",
      "10_example_left_curly_bracket_in_comment.pgn" })
  @SuppressWarnings("static-method")
  void parseAgreesAcrossParsersPregameCommentary(String pgnName) {
    assertParseAgrees(PREGAME_COMMENTARY_SUCCESS_FOLDER_PATH, pgnName);
  }

  @ParameterizedTest(name = "nonPregameCommentary/success/{0}")
  @ValueSource(strings = { "02_1_example.pgn", "02_2_example.pgn", "02_3_example.pgn", "02_4_example.pgn",
      "03_1_example.pgn", "03_2_example.pgn", "03_3_example.pgn", "03_4_example.pgn", "03_5_example.pgn",
      "03_6_example.pgn", "04_1_example.pgn", "04_2_example.pgn", "04_3_example.pgn", "04_4_example.pgn",
      "05_1_example.pgn", "06_example_whitespace.pgn", "07_example_left_curly_bracket_in_comment.pgn" })
  @SuppressWarnings("static-method")
  void parseAgreesAcrossParsersNonPregameCommentary(String pgnName) {
    assertParseAgrees(NON_PREGAME_COMMENTARY_SUCCESS_FOLDER_PATH, pgnName);
  }

  @ParameterizedTest(name = "combinedCommentary/success/{0}")
  @ValueSource(strings = { "01_example.pgn", "02_1_example.pgn", "02_2_example.pgn", "02_3_example.pgn",
      "02_4_example.pgn", "03_1_example.pgn", "03_2_example.pgn", "03_3_example.pgn", "03_4_example.pgn",
      "03_5_example.pgn", "03_6_example.pgn", "04_1_example.pgn", "04_2_example.pgn", "04_3_example.pgn",
      "04_4_example.pgn", "05_1_example.pgn", "06_example_whitespace.pgn" })
  @SuppressWarnings("static-method")
  void parseAgreesAcrossParsersCombinedCommentary(String pgnName) {
    assertParseAgrees(COMBINED_COMMENTARY_SUCCESS_FOLDER_PATH, pgnName);
  }

  @ParameterizedTest(name = "strict/pregameCommentary/exception/unclosed/{0}")
  @ValueSource(strings = { "01_movetext_commentary_ends_after_start_brace.pgn",
      "02_1_movetext_commentary_start_brace_not_followed_by_end_brace.pgn",
      "02_2_movetext_commentary_start_brace_not_followed_by_end_brace.pgn",
      "02_3_movetext_commentary_start_brace_not_followed_by_end_brace.pgn" })
  @SuppressWarnings("static-method")
  void strictRejectsPregameCommentaryUnclosedFixtures(String pgnName) {
    assertStrictRejects(PREGAME_COMMENTARY_EXCEPTION_FOLDER_PATH, pgnName,
        StrictPgnParserValidationProblem.MOVETEXT_COMMENTARY_START_BRACE_NOT_FOLLOWED_BY_END_BRACE);
  }

  @ParameterizedTest(name = "strict/pregameCommentary/exception/spacing/{0}")
  @ValueSource(strings = { "03_movetext_commentary_not_followed_by_space.pgn",
      "04_movetext_commentary_followed_by_space_but_ending.pgn", "05_movetext_commentary_followed_by_two_spaces.pgn",
      "06_movetext_commentary_not_followed_by_value.pgn" })
  @SuppressWarnings("static-method")
  void strictRejectsPregameCommentarySpacingFixtures(String pgnName) {
    assertStrictRejects(PREGAME_COMMENTARY_EXCEPTION_FOLDER_PATH, pgnName,
        StrictPgnParserValidationProblem.MOVETEXT_COMMENTARY_NOT_FOLLOWED_BY_SPACE);
  }

  @ParameterizedTest(name = "strict/pregameCommentary/exception/stray-close/{0}")
  @ValueSource(strings = { "07_movetext_commentary_end_brace_without_start_brace.pgn" })
  @SuppressWarnings("static-method")
  void strictRejectsPregameCommentaryStrayCloseFixtures(String pgnName) {
    assertStrictRejects(PREGAME_COMMENTARY_EXCEPTION_FOLDER_PATH, pgnName,
        StrictPgnParserValidationProblem.MOVETEXT_COMMENTARY_END_BRACE_WITHOUT_START_BRACE);
  }

  @ParameterizedTest(name = "strict/nonPregameCommentary/exception/unclosed/{0}")
  @ValueSource(strings = { "01_movetext_commentary_ends_after_start_brace.pgn",
      "02_1_movetext_commentary_start_brace_not_followed_by_end_brace.pgn",
      "02_2_movetext_commentary_start_brace_not_followed_by_end_brace.pgn",
      "02_3_movetext_commentary_start_brace_not_followed_by_end_brace.pgn",
      "02_4_movetext_commentary_start_brace_not_followed_by_end_brace.pgn" })
  @SuppressWarnings("static-method")
  void strictRejectsNonPregameCommentaryUnclosedFixtures(String pgnName) {
    assertStrictRejects(NON_PREGAME_COMMENTARY_EXCEPTION_FOLDER_PATH, pgnName,
        StrictPgnParserValidationProblem.MOVETEXT_COMMENTARY_START_BRACE_NOT_FOLLOWED_BY_END_BRACE);
  }

  @ParameterizedTest(name = "strict/nonPregameCommentary/exception/spacing/{0}")
  @ValueSource(strings = { "03_1_movetext_commentary_not_followed_by_space.pgn",
      "03_2_movetext_commentary_not_followed_by_space.pgn", "04_movetext_commentary_followed_by_space_but_ending.pgn",
      "05_1_movetext_commentary_followed_by_two_spaces.pgn", "05_2_movetext_commentary_followed_by_two_spaces.pgn",
      "06_movetext_commentary_not_followed_by_value.pgn" })
  @SuppressWarnings("static-method")
  void strictRejectsNonPregameCommentarySpacingFixtures(String pgnName) {
    assertStrictRejects(NON_PREGAME_COMMENTARY_EXCEPTION_FOLDER_PATH, pgnName,
        StrictPgnParserValidationProblem.MOVETEXT_COMMENTARY_NOT_FOLLOWED_BY_SPACE);
  }

  @ParameterizedTest(name = "strict/nonPregameCommentary/exception/stray-close/{0}")
  @ValueSource(strings = { "07_1_movetext_commentary_end_brace_without_start_brace.pgn",
      "07_2_movetext_commentary_end_brace_without_start_brace.pgn",
      "07_3_movetext_commentary_end_brace_without_start_brace.pgn" })
  @SuppressWarnings("static-method")
  void strictRejectsNonPregameCommentaryStrayCloseFixtures(String pgnName) {
    assertStrictRejects(NON_PREGAME_COMMENTARY_EXCEPTION_FOLDER_PATH, pgnName,
        StrictPgnParserValidationProblem.MOVETEXT_COMMENTARY_END_BRACE_WITHOUT_START_BRACE);
  }

  @ParameterizedTest(name = "lenient/pregameCommentary/exception/unclosed/{0}")
  @ValueSource(strings = { "01_movetext_commentary_ends_after_start_brace.pgn",
      "02_1_movetext_commentary_start_brace_not_followed_by_end_brace.pgn",
      "02_2_movetext_commentary_start_brace_not_followed_by_end_brace.pgn",
      "02_3_movetext_commentary_start_brace_not_followed_by_end_brace.pgn" })
  @SuppressWarnings("static-method")
  void lenientRejectsPregameCommentaryUnclosedFixtures(String pgnName) {
    assertLenientRejects(PREGAME_COMMENTARY_EXCEPTION_FOLDER_PATH, pgnName,
        LenientPgnParserValidationProblem.MOVETEXT_COMMENTARY_START_BRACE_NOT_FOLLOWED_BY_END_BRACE);
  }

  @ParameterizedTest(name = "lenient/pregameCommentary/exception/stray-close/{0}")
  @ValueSource(strings = { "07_movetext_commentary_end_brace_without_start_brace.pgn" })
  @SuppressWarnings("static-method")
  void lenientRejectsPregameCommentaryStrayCloseFixtures(String pgnName) {
    assertLenientRejects(PREGAME_COMMENTARY_EXCEPTION_FOLDER_PATH, pgnName,
        LenientPgnParserValidationProblem.MOVETEXT_COMMENTARY_END_BRACE_WITHOUT_START_BRACE);
  }

  @ParameterizedTest(name = "lenient/pregameCommentary/strict-only-exception/{0}")
  @ValueSource(strings = { "03_movetext_commentary_not_followed_by_space.pgn",
      "04_movetext_commentary_followed_by_space_but_ending.pgn", "05_movetext_commentary_followed_by_two_spaces.pgn",
      "06_movetext_commentary_not_followed_by_value.pgn" })
  @SuppressWarnings("static-method")
  void lenientAcceptsPregameCommentaryStrictOnlyExceptionFixtures(String pgnName) {
    assertLenientAccepts(PREGAME_COMMENTARY_EXCEPTION_FOLDER_PATH, pgnName);
  }

  @ParameterizedTest(name = "lenient/nonPregameCommentary/exception/unclosed/{0}")
  @ValueSource(strings = { "01_movetext_commentary_ends_after_start_brace.pgn",
      "02_1_movetext_commentary_start_brace_not_followed_by_end_brace.pgn",
      "02_2_movetext_commentary_start_brace_not_followed_by_end_brace.pgn",
      "02_3_movetext_commentary_start_brace_not_followed_by_end_brace.pgn",
      "02_4_movetext_commentary_start_brace_not_followed_by_end_brace.pgn" })
  @SuppressWarnings("static-method")
  void lenientRejectsNonPregameCommentaryUnclosedFixtures(String pgnName) {
    assertLenientRejects(NON_PREGAME_COMMENTARY_EXCEPTION_FOLDER_PATH, pgnName,
        LenientPgnParserValidationProblem.MOVETEXT_COMMENTARY_START_BRACE_NOT_FOLLOWED_BY_END_BRACE);
  }

  @ParameterizedTest(name = "lenient/nonPregameCommentary/exception/stray-close/{0}")
  @ValueSource(strings = { "07_1_movetext_commentary_end_brace_without_start_brace.pgn",
      "07_2_movetext_commentary_end_brace_without_start_brace.pgn",
      "07_3_movetext_commentary_end_brace_without_start_brace.pgn" })
  @SuppressWarnings("static-method")
  void lenientRejectsNonPregameCommentaryStrayCloseFixtures(String pgnName) {
    assertLenientRejects(NON_PREGAME_COMMENTARY_EXCEPTION_FOLDER_PATH, pgnName,
        LenientPgnParserValidationProblem.MOVETEXT_COMMENTARY_END_BRACE_WITHOUT_START_BRACE);
  }

  @ParameterizedTest(name = "lenient/nonPregameCommentary/strict-only-exception/{0}")
  @ValueSource(strings = { "03_1_movetext_commentary_not_followed_by_space.pgn",
      "03_2_movetext_commentary_not_followed_by_space.pgn", "04_movetext_commentary_followed_by_space_but_ending.pgn",
      "05_1_movetext_commentary_followed_by_two_spaces.pgn", "05_2_movetext_commentary_followed_by_two_spaces.pgn",
      "06_movetext_commentary_not_followed_by_value.pgn" })
  @SuppressWarnings("static-method")
  void lenientAcceptsNonPregameCommentaryStrictOnlyExceptionFixtures(String pgnName) {
    assertLenientAccepts(NON_PREGAME_COMMENTARY_EXCEPTION_FOLDER_PATH, pgnName);
  }

  private static void assertParseAgrees(Path folder, String pgnName) {
    final PgnGame strictModel = StrictPgnParser.parse(folder, pgnName);
    final PgnGame lenientModel = LenientPgnParser.parse(folder, pgnName);
    assertEquals(strictModel, lenientModel,
        "Strict and lenient parsers disagree on " + folder.getFileName() + "/" + pgnName);
  }

  private static void assertStrictRejects(Path folder, String pgnName, StrictPgnParserValidationProblem expected) {
      boolean isException = false;
    try {
      StrictPgnParser.parse(folder, pgnName);
    } catch (final StrictPgnParserValidationException e) {
      isException = true;
      assertEquals(expected, e.getStrictPgnParserValidationProblem());
      assertEquals(SanValidationProblem.NONE, e.getSanValidationProblem());
    }
    assertTrue(isException, "Expected " + expected + " but strict parser accepted " + pgnName);

    final StrictPgnParserValidationResult result = StrictPgnParser.validate(folder, pgnName);
    assertFalse(result.isValid());
    assertEquals(expected, result.problemParser());
    assertEquals(SanValidationProblem.NONE, result.problemSan());
  }

  private static void assertLenientRejects(Path folder, String pgnName, LenientPgnParserValidationProblem expected) {
      boolean isException = false;
    try {
      LenientPgnParser.parse(folder, pgnName);
    } catch (final LenientPgnParserValidationException e) {
      isException = true;
      assertEquals(expected, e.getLenientPgnParserValidationProblem());
      assertEquals(SanValidationProblem.NONE, e.getSanValidationProblem());
    }
    assertTrue(isException, "Expected " + expected + " but lenient parser accepted " + pgnName);

    final LenientPgnParserValidationResult result = LenientPgnParser.validate(folder, pgnName);
    assertFalse(result.isValid());
    assertEquals(expected, result.problemParser());
    assertEquals(SanValidationProblem.NONE, result.problemSan());
  }

  private static void assertLenientAccepts(Path folder, String pgnName) {
    LenientPgnParser.parse(folder, pgnName);

    final LenientPgnParserValidationResult result = LenientPgnParser.validate(folder, pgnName);
    assertTrue(result.isValid());
    assertEquals(LenientPgnParserValidationProblem.OK, result.problemParser());
    assertEquals(SanValidationProblem.NONE, result.problemSan());
  }
}
