// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.test.pgn.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

import io.github.dlbbld.ashlarchess.fen.LenientFenParserValidationException;
import io.github.dlbbld.ashlarchess.internal.Nulls;
import io.github.dlbbld.ashlarchess.pgn.LenientPgnParser;
import io.github.dlbbld.ashlarchess.pgn.LenientPgnParserValidationProblem;
import io.github.dlbbld.ashlarchess.pgn.LenientPgnParserValidationResult;
import io.github.dlbbld.ashlarchess.pgn.StrictPgnParser;
import io.github.dlbbld.ashlarchess.pgn.StrictPgnParserValidationException;
import io.github.dlbbld.ashlarchess.pgn.StrictPgnParserValidationProblem;
import io.github.dlbbld.ashlarchess.pgn.StrictPgnParserValidationResult;
import io.github.dlbbld.ashlarchess.san.SanValidationProblem;
import io.github.dlbbld.ashlarchess.test.pgntest.constants.PgnTestConstants;

class TestPgnParserIllegalFenImport {

  private static final Path ILLEGAL_FEN_ROOT = Nulls
      .pathResolve(PgnTestConstants.PGN_PARSER_TEST_ROOT_FOLDER_PATH, "common/illegalFen");

  @SuppressWarnings("static-method")
  @Test
  void testStrictParserRejectsIllegalFenImports() throws IOException {
    for (final Path pgnPath : illegalFenPgns()) {
      final StrictPgnParserValidationException parseException = assertThrows(StrictPgnParserValidationException.class,
          () -> StrictPgnParser.parsePath(pgnPath), pgnPath::toString);
      assertEquals(StrictPgnParserValidationProblem.TAG_SET_UP_REQUIRES_FEN_TAG_BUT_FEN_INVALID,
          parseException.getStrictPgnParserValidationProblem(), pgnPath::toString);
      assertEquals(SanValidationProblem.NONE, parseException.getSanValidationProblem(), pgnPath::toString);

      final StrictPgnParserValidationResult result = StrictPgnParser.validatePath(pgnPath);
      assertFalse(result.isValid(), pgnPath::toString);
      assertEquals(StrictPgnParserValidationProblem.TAG_SET_UP_REQUIRES_FEN_TAG_BUT_FEN_INVALID,
          result.parserProblem(), pgnPath::toString);
      assertEquals(SanValidationProblem.NONE, result.sanProblem(), pgnPath::toString);
    }
  }

  @SuppressWarnings("static-method")
  @Test
  void testLenientParserRejectsIllegalFenImports() throws IOException {
    for (final Path pgnPath : illegalFenPgns()) {
      assertThrows(LenientFenParserValidationException.class, () -> LenientPgnParser.parsePath(pgnPath),
          pgnPath::toString);

      final LenientPgnParserValidationResult result = LenientPgnParser.validatePath(pgnPath);
      assertFalse(result.isValid(), pgnPath::toString);
      assertEquals(LenientPgnParserValidationProblem.FEN_TAG_INVALID, result.parserProblem(), pgnPath::toString);
      assertEquals(SanValidationProblem.NONE, result.sanProblem(), pgnPath::toString);
    }
  }

  private static List<Path> illegalFenPgns() throws IOException {
    try (Stream<Path> paths = Files.walk(ILLEGAL_FEN_ROOT)) {
      return paths.filter(Files::isRegularFile).filter(path -> Nulls.toString(path.getFileName()).endsWith(".pgn"))
          .sorted().toList();
    }
  }
}
