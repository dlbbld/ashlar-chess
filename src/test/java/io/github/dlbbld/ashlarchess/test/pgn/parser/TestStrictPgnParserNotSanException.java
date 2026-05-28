// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.test.pgn.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;

import io.github.dlbbld.ashlarchess.common.Nulls;
import io.github.dlbbld.ashlarchess.pgn.StrictPgnParser;
import io.github.dlbbld.ashlarchess.pgn.StrictPgnParserValidationException;
import io.github.dlbbld.ashlarchess.pgn.StrictPgnParserValidationProblem;
import io.github.dlbbld.ashlarchess.test.pgntest.constants.PgnTestConstants;

class TestStrictPgnParserNotSanException extends AbstractTestStrictPgnParserException {
  private static final Path PGN_TEST_FOLDER_PATH = Nulls
      .pathResolve(PgnTestConstants.STRICT_PGN_PARSER_TEST_ROOT_FOLDER_PATH, "exception/other");

  @SuppressWarnings("static-method")
  @Test
  void test() {
    checkException("02_starts_with_empty_line.pgn", StrictPgnParserValidationProblem.FILE_EMPTY_LINE_CANNOT_START_WITH);
    checkException("03_does_not_end_with_empty_line.pgn",
        StrictPgnParserValidationProblem.FILE_EMPTY_LINE_MUST_END_WITH);
    checkException("04_1_more_than_two_empty_lines.pgn", StrictPgnParserValidationProblem.FILE_EMPTY_LINE_EXACTLY_TWO);
    checkException("04_2_two_consecutive_empty_lines.pgn",
        StrictPgnParserValidationProblem.FILE_EMPTY_LINE_NOT_CONSECUTIVE);

    checkException("05_tag_no_start_brace.pgn",
        StrictPgnParserValidationProblem.TAG_FORMAT_NOT_STARTING_WITH_LEFT_SQUARE_BRACKET);
    checkException("06_tag_premature_end.pgn",
        StrictPgnParserValidationProblem.TAG_FORMAT_NOT_ENDING_WITH_RIGHT_SQUARE_BRACKET);
    checkException("07_tag_name_starts_with_space.pgn",
        StrictPgnParserValidationProblem.TAG_FORMAT_LEFT_SQUARE_BRACKET_FOLLOWED_BY_SPACE);
    checkException("08_tag_name_space_after_missing.pgn", StrictPgnParserValidationProblem.TAG_FORMAT_INVALID);
    checkException("09_tag_value_start_quote_missing.pgn", StrictPgnParserValidationProblem.TAG_FORMAT_INVALID);
    checkException("11_tag_value_ending_quote_not_found.pgn", StrictPgnParserValidationProblem.TAG_FORMAT_INVALID);
    checkException("12_1_tag_no_end_brace.pgn",
        StrictPgnParserValidationProblem.TAG_FORMAT_NOT_ENDING_WITH_RIGHT_SQUARE_BRACKET);
    checkException("12_2_tag_no_end_brace.pgn",
        StrictPgnParserValidationProblem.TAG_FORMAT_NOT_ENDING_WITH_RIGHT_SQUARE_BRACKET);
    checkException("13_tag_continues_above_end_brace.pgn",
        StrictPgnParserValidationProblem.TAG_FORMAT_NOT_ENDING_WITH_RIGHT_SQUARE_BRACKET);

    checkException("14_tag_name_not_unique.pgn", StrictPgnParserValidationProblem.TAG_NAME_NOT_UNIQUE);
    // 15: PGN spec section 8.1.1 makes the full Seven Tag Roster an archival-storage concern only. Strict parsing
    // requires the Result tag specifically (so its value can match the termination marker); the rest of the roster
    // is optional at the strict level. The fixture is missing only the Result tag.
    checkException("15_tag_result_missing.pgn", StrictPgnParserValidationProblem.TAG_RESULT_MISSING);

    checkException("16_tag_result_value_invalid.pgn", StrictPgnParserValidationProblem.TAG_RESULT_VALUE_INVALID);
    checkException("17_tag_result_value_invalid.pgn", StrictPgnParserValidationProblem.TAG_RESULT_VALUE_INVALID);

    checkException("18_file_end_invalid.pgn", StrictPgnParserValidationProblem.MOVETEXT_TERMINATION_INVALID);
    checkException("19_file_end_invalid.pgn", StrictPgnParserValidationProblem.MOVETEXT_TERMINATION_INVALID);
    checkException("20_file_end_invalid.pgn", StrictPgnParserValidationProblem.MOVETEXT_TERMINATION_INVALID);
    checkException("21_file_end_invalid.pgn", StrictPgnParserValidationProblem.MOVETEXT_TERMINATION_INVALID);

    checkException("22_tag_set_up_value_invalid.pgn", StrictPgnParserValidationProblem.TAG_SET_UP_VALUE_INVALID);
    checkException("23_tag_set_up_requires_fen_tag_but_missing.pgn",
        StrictPgnParserValidationProblem.TAG_SET_UP_REQUIRES_FEN_TAG_BUT_MISSING);

    checkException("24_fen_tag_set_but_not_required.pgn",
        StrictPgnParserValidationProblem.TAG_FEN_NOT_REQUIRED_BUT_SET);

    checkException("25_1_invalid_commentary.pgn",
        StrictPgnParserValidationProblem.MOVETEXT_COMMENTARY_START_BRACE_NOT_FOLLOWED_BY_END_BRACE);
    checkException("25_2_invalid_commentary.pgn",
        StrictPgnParserValidationProblem.MOVETEXT_MOVE_NUMBER_DOES_NOT_CONTINUE_AS_EXPECTED);
    // 25_3: `{{...` with no closing brace before EOF. T-003 makes the inner `{` content, so the outer comment runs
    // unclosed -> unclosed-commentary error.
    checkException("25_3_invalid_commentary.pgn",
        StrictPgnParserValidationProblem.MOVETEXT_COMMENTARY_START_BRACE_NOT_FOLLOWED_BY_END_BRACE);
    checkException("25_4_invalid_commentary.pgn",
        StrictPgnParserValidationProblem.MOVETEXT_MOVE_NUMBER_DOES_NOT_CONTINUE_AS_EXPECTED);
    checkException("25_5_invalid_commentary.pgn",
        StrictPgnParserValidationProblem.MOVETEXT_COMMENTARY_END_BRACE_WITHOUT_START_BRACE);

    checkException("26_1_move_number_for_non_initial_black_move.pgn",
        StrictPgnParserValidationProblem.MOVETEXT_MOVE_NUMBER_FOR_BLACK_NON_INITIAL_MOVE);
    checkException("26_2_move_number_for_non_initial_black_move.pgn",
        StrictPgnParserValidationProblem.MOVETEXT_MOVE_NUMBER_FOR_BLACK_NON_INITIAL_MOVE);
    checkException("26_3_move_number_for_non_initial_black_move.pgn",
        StrictPgnParserValidationProblem.MOVETEXT_MOVE_NUMBER_FOR_BLACK_NON_INITIAL_MOVE);
    checkException("26_4_move_number_for_non_initial_black_move.pgn",
        StrictPgnParserValidationProblem.MOVETEXT_MOVE_NUMBER_FOR_BLACK_NON_INITIAL_MOVE);

  }

  private static void checkException(String pgnName, StrictPgnParserValidationProblem expected) {
    boolean isException = false;
    try {
      StrictPgnParser.parse(PGN_TEST_FOLDER_PATH, pgnName);
    } catch (final StrictPgnParserValidationException e) {
      isException = true;
      assertEquals(expected, e.getStrictPgnParserValidationProblem());
    }
    assertTrue(isException);
  }

}
