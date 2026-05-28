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
import io.github.dlbbld.ashlarchess.san.SanValidationProblem;
import io.github.dlbbld.ashlarchess.test.pgntest.constants.PgnTestConstants;

class TestStrictPgnParserSanException extends AbstractTestStrictPgnParserException {
  private static final Path PGN_TEST_FOLDER_PATH = Nulls
      .pathResolve(PgnTestConstants.STRICT_PGN_PARSER_TEST_ROOT_FOLDER_PATH, "exception/san");

  @SuppressWarnings("static-method")
  @Test
  void testException() {
    checkException("01_initial_position.pgn", SanValidationProblem.NOT_REACHABLE_PAWN_NON_CAPTURING);
    checkException("02_initial_position.pgn", SanValidationProblem.NOT_REACHABLE_PAWN_CAPTURING);

    checkException("03_custom_position_white_start.pgn", SanValidationProblem.DESTINATION_RNBQK_EMPTY_CAPTURE_SYMBOL);
    checkException("04_custom_position_black_start.pgn", SanValidationProblem.MOVEMENT_RNBQ_FROM_RANK);

  }

  private static void checkException(String pgnName, SanValidationProblem expected) {
    boolean isException = false;
    try {
      StrictPgnParser.parse(PGN_TEST_FOLDER_PATH, pgnName);
    } catch (final StrictPgnParserValidationException e) {
      isException = true;
      assertEquals(StrictPgnParserValidationProblem.SAN, e.getStrictPgnParserValidationProblem());
      assertEquals(expected, e.getSanValidationProblem());
    }
    assertTrue(isException);
  }
}
