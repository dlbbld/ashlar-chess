// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.test.pgn.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import io.github.dlbbld.ashlarchess.pgn.StrictPgnParser;
import io.github.dlbbld.ashlarchess.pgn.StrictPgnParserValidationException;
import io.github.dlbbld.ashlarchess.pgn.StrictPgnParserValidationProblem;
import io.github.dlbbld.ashlarchess.pgn.StrictPgnParserValidationResult;
import io.github.dlbbld.ashlarchess.san.SanValidationProblem;

public abstract class AbstractTestStrictPgnParserException {
  static void checkException(Path pgnTestFolderPath, String pgnName,
      StrictPgnParserValidationProblem expectedProblemParser, SanValidationProblem expectedProblemSan) {
    checkParse(pgnTestFolderPath, pgnName, expectedProblemParser, expectedProblemSan);
    checkValidate(pgnTestFolderPath, pgnName, expectedProblemParser, expectedProblemSan);
  }

  private static void checkParse(Path pgnTestFolderPath, String pgnName,
      StrictPgnParserValidationProblem expectedProblemParser, SanValidationProblem expectedProblemSan) {
    boolean isException = false;
    try {
      StrictPgnParser.parse(pgnTestFolderPath, pgnName);
    } catch (final StrictPgnParserValidationException e) {
      isException = true;
      assertEquals(expectedProblemParser, e.getStrictPgnParserValidationProblem());
      assertEquals(expectedProblemSan, e.getSanValidationProblem());
    }
    assertTrue(isException);
  }

  private static void checkValidate(Path pgnTestFolderPath, String pgnName,
      StrictPgnParserValidationProblem expectedParserProblem, SanValidationProblem expectedSanProblem) {
    final StrictPgnParserValidationResult result = StrictPgnParser.validate(pgnTestFolderPath, pgnName);
    assertFalse(result.isValid());
    assertEquals(expectedParserProblem, result.problemParser());
    assertEquals(expectedSanProblem, result.problemSan());
  }

}
