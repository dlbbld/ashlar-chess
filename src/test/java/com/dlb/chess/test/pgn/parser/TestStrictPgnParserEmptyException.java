package com.dlb.chess.test.pgn.parser;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;

import com.dlb.chess.common.Nulls;
import com.dlb.chess.pgn.StrictPgnParserValidationProblem;
import com.dlb.chess.san.SanValidationProblem;
import com.dlb.chess.test.pgntest.constants.PgnTestConstants;

/**
 * Covers the strict parser's rejection of input that carries no signal - strictly-empty bytes, or whitespace-only
 * files (any combination of spaces, tabs, newlines). Both shapes collapse to
 * {@link StrictPgnParserValidationProblem#FILE_EMPTY}. Symmetric with the lenient parser's empty-input handling.
 */
class TestStrictPgnParserEmptyException extends AbstractTestStrictPgnParserException {

  private static final Path PGN_TEST_FOLDER_PATH = Nulls
      .pathResolve(PgnTestConstants.STRICT_PGN_PARSER_TEST_ROOT_FOLDER_PATH, "exception/empty");

  @SuppressWarnings("static-method")
  @Test
  void testException() {
    checkException("01_empty_file.pgn", StrictPgnParserValidationProblem.FILE_EMPTY);
    checkException("02_whitespace_only.pgn", StrictPgnParserValidationProblem.FILE_EMPTY);
  }

  private static void checkException(String pgnName, StrictPgnParserValidationProblem expected) {
    checkException(PGN_TEST_FOLDER_PATH, pgnName, expected, SanValidationProblem.NONE);
  }
}
