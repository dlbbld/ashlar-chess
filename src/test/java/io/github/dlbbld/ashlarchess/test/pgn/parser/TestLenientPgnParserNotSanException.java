// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.test.pgn.parser;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;

import io.github.dlbbld.ashlarchess.common.Nulls;
import io.github.dlbbld.ashlarchess.pgn.LenientPgnParserValidationProblem;
import io.github.dlbbld.ashlarchess.san.SanValidationProblem;
import io.github.dlbbld.ashlarchess.test.pgntest.constants.PgnTestConstants;

class TestLenientPgnParserNotSanException extends AbstractTestLenientPgnParserException {
  private static final Path PGN_TEST_FOLDER_PATH = Nulls
      .pathResolve(PgnTestConstants.LENIENT_PGN_PARSER_TEST_ROOT_FOLDER_PATH, "exception/other");

  @SuppressWarnings("static-method")
  @Test
  void testException() {
    checkException("01_tag_format.pgn", LenientPgnParserValidationProblem.TAG_FORMAT_INVALID);
    checkException("02_tag_format.pgn", LenientPgnParserValidationProblem.TAG_FORMAT_INVALID);
    checkException("03_tag_format.pgn", LenientPgnParserValidationProblem.TAG_FORMAT_INVALID);
    checkException("04_tag_format.pgn", LenientPgnParserValidationProblem.TAG_FORMAT_INVALID);

    checkException("05_tag_name_not_unique.pgn", LenientPgnParserValidationProblem.TAG_NAME_NOT_UNIQUE);

    checkException("06_tag_reappear.pgn", LenientPgnParserValidationProblem.TAG_REAPPEAR);
    checkException("07_tag_reappear.pgn", LenientPgnParserValidationProblem.TAG_REAPPEAR);

    checkException("08_tag_result_incorrect_value.pgn", LenientPgnParserValidationProblem.TAG_RESULT_VALUE_INVALID);

    checkException("09_tag_setup_tag_value_invalid.pgn", LenientPgnParserValidationProblem.TAG_SET_UP_VALUE_INVALID);
    checkException("10_tag_setup_tag_zero_but_fen_provided.pgn",
        LenientPgnParserValidationProblem.TAG_SET_UP_VALUE_ZERO_BUT_FEN_PROVIDED);
  }

  private static void checkException(String pgnName, LenientPgnParserValidationProblem expected) {
    checkException(PGN_TEST_FOLDER_PATH, pgnName, expected, SanValidationProblem.NONE);
  }
}
