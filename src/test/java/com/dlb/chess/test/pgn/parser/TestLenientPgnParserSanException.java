package com.dlb.chess.test.pgn.parser;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;

import com.dlb.chess.common.Nulls;
import com.dlb.chess.pgn.LenientPgnParserValidationProblem;
import com.dlb.chess.san.SanValidationProblem;
import com.dlb.chess.test.pgntest.constants.PgnTestConstants;

class TestLenientPgnParserSanException extends AbstractTestLenientPgnParserException {

  private static final Path PGN_TEST_FOLDER_PATH = Nulls
      .pathResolve(PgnTestConstants.LENIENT_PGN_PARSER_TEST_ROOT_FOLDER_PATH, "exception/san");

  @SuppressWarnings("static-method")
  @Test
  void testException() {
    checkException("01_initial_position.pgn", SanValidationProblem.DESTINATION_RNBQK_OWN_PIECE_NON_CAPTURING);
    checkException("02_initial_position.pgn", SanValidationProblem.NOT_REACHABLE_RNBQ_NEITHER_MULTIPLE);

    checkException("03_custom_position_white_start.pgn", SanValidationProblem.NOT_REACHABLE_RNBQ_NEITHER_MULTIPLE);
    checkException("04_custom_position_black_start.pgn", SanValidationProblem.NOT_REACHABLE_RNBQ_NEITHER_SINGLE);

  }

  private static void checkException(String pgnName, SanValidationProblem expectedSanValidationProblem) {
    checkException(PGN_TEST_FOLDER_PATH, pgnName, LenientPgnParserValidationProblem.SAN, expectedSanValidationProblem);
  }
}
