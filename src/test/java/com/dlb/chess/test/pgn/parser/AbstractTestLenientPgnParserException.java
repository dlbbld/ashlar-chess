package com.dlb.chess.test.pgn.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import com.dlb.chess.pgn.LenientPgnParser;
import com.dlb.chess.pgn.LenientPgnParserValidationException;
import com.dlb.chess.pgn.LenientPgnParserValidationProblem;
import com.dlb.chess.pgn.LenientPgnParserValidationResult;
import com.dlb.chess.san.SanValidationProblem;

public abstract class AbstractTestLenientPgnParserException extends AbstractTestLenientPgnParser {
  static void checkException(Path pgnTestFolderPath, String pgnName,
      LenientPgnParserValidationProblem expectedProblemParser, SanValidationProblem expectedProblemSan) {
    checkParse(pgnTestFolderPath, pgnName, expectedProblemParser, expectedProblemSan);
    checkValidate(pgnTestFolderPath, pgnName, expectedProblemParser, expectedProblemSan);
  }

  private static void checkParse(Path pgnTestFolderPath, String pgnName,
      LenientPgnParserValidationProblem expectedProblemParser, SanValidationProblem expectedProblemSan) {
      boolean isException = false;
    try {
      LenientPgnParser.parse(pgnTestFolderPath, pgnName);
    } catch (final LenientPgnParserValidationException e) {
      isException = true;
      assertEquals(expectedProblemParser, e.getLenientPgnParserValidationProblem());
      assertEquals(expectedProblemSan, e.getSanValidationProblem());
    }
    assertTrue(isException);
  }

  private static void checkValidate(Path pgnTestFolderPath, String pgnName,
      LenientPgnParserValidationProblem expectedParserProblem, SanValidationProblem expectedSanProblem) {
    final LenientPgnParserValidationResult result = LenientPgnParser.validate(pgnTestFolderPath, pgnName);
    assertFalse(result.isValid());
    assertEquals(expectedParserProblem, result.problemParser());
    assertEquals(expectedSanProblem, result.problemSan());
  }

}
