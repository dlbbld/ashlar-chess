// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.test.pgn.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
import io.github.dlbbld.ashlarchess.test.common.utility.FileUtility;
import io.github.dlbbld.ashlarchess.test.pgntest.constants.PgnTestConstants;

class TestPgnParserIllegalFenImport {

  private static final Path ILLEGAL_FEN_ROOT = Nulls
      .pathResolve(PgnTestConstants.PGN_PARSER_TEST_ROOT_FOLDER_PATH, "common/illegalFen");

  @SuppressWarnings("static-method")
  @Test
  void testStrictParserRejectsIllegalFenImports() {
    for (final Path pgnPath : illegalFenPgns()) {
      @SuppressWarnings("null") final StrictPgnParserValidationException parseException = assertThrows(
          StrictPgnParserValidationException.class, () -> StrictPgnParser.parsePath(pgnPath), pgnPath::toString);
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
  void testLenientParserRejectsIllegalFenImports() {
    for (final Path pgnPath : illegalFenPgns()) {
      assertThrows(LenientFenParserValidationException.class, () -> LenientPgnParser.parsePath(pgnPath),
          pgnPath::toString);

      final LenientPgnParserValidationResult result = LenientPgnParser.validatePath(pgnPath);
      assertFalse(result.isValid(), pgnPath::toString);
      assertEquals(LenientPgnParserValidationProblem.FEN_TAG_INVALID, result.parserProblem(), pgnPath::toString);
      assertEquals(SanValidationProblem.NONE, result.sanProblem(), pgnPath::toString);
    }
  }

  private static List<Path> illegalFenPgns() {
    final List<Path> pgnPaths = new ArrayList<>();
    for (final Path path : FileUtility.listAllFilesRecursively(ILLEGAL_FEN_ROOT)) {
      if (Nulls.toString(Nulls.getFileName(path)).endsWith(".pgn")) {
        pgnPaths.add(path);
      }
    }
    Collections.sort(pgnPaths);
    return pgnPaths;
  }
}
