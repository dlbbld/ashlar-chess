// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.test.pgn.parser;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;

import io.github.dlbbld.ashlarchess.common.Nulls;
import io.github.dlbbld.ashlarchess.pgn.LenientPgnParserValidationProblem;
import io.github.dlbbld.ashlarchess.san.SanValidationProblem;
import io.github.dlbbld.ashlarchess.test.pgntest.constants.PgnTestConstants;

class TestLenientPgnParserResultException extends AbstractTestLenientPgnParserException {
  private static final Path PGN_TEST_FOLDER_PATH = Nulls
      .pathResolve(PgnTestConstants.LENIENT_PGN_PARSER_TEST_ROOT_FOLDER_PATH, "result");

  @SuppressWarnings("static-method")
  @Test
  void testException() {

    checkException("04_has_result_tag_has_termination_tag_different.pgn",
        LenientPgnParserValidationProblem.TAG_RESULT_BOTH_SET_BUT_DIFFERENT);
    checkException("05_has_result_tag_has_termination_tag_different.pgn",
        LenientPgnParserValidationProblem.TAG_RESULT_BOTH_SET_BUT_DIFFERENT);
    checkException("06_has_result_tag_has_termination_tag_different.pgn",
        LenientPgnParserValidationProblem.TAG_RESULT_BOTH_SET_BUT_DIFFERENT);
  }

  private static void checkException(String pgnName, LenientPgnParserValidationProblem expected) {
    checkException(PGN_TEST_FOLDER_PATH, pgnName, expected, SanValidationProblem.NONE);
  }
}