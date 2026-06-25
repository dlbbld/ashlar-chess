// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.test.pgn.export;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;

import io.github.dlbbld.ashlarchess.internal.Nulls;
import io.github.dlbbld.ashlarchess.pgn.PgnCreate;
import io.github.dlbbld.ashlarchess.pgn.PgnGame;
import io.github.dlbbld.ashlarchess.test.common.utility.FileUtility;
import io.github.dlbbld.ashlarchess.test.pgn.parser.PgnCacheForStrictPgnParserTestCases;
import io.github.dlbbld.ashlarchess.test.pgntest.constants.PgnTestConstants;

class TestPgnExportLineLength {

  private static final Path TEST_FOLDER_PATH = Nulls.pathResolve(PgnTestConstants.PGN_EXPORT_TEST_ROOT_FOLDER_PATH,
      "lineLength");

  @SuppressWarnings("null")
  private static final Logger logger = LogManager.getLogger(TestPgnExportLineLength.class);

  @SuppressWarnings("static-method")
  @Test
  void test() {
    final List<String> pgnNames = calculatePgnNames();
    assertFalse(pgnNames.isEmpty(), "The PGN export line-length test folder must contain PGN files");

    for (final String pgnName : pgnNames) {
      checkFile(pgnName);
    }
  }

  private static List<String> calculatePgnNames() {
    final List<String> result = new ArrayList<>();

    for (final String fileName : FileUtility.readFileNames(TEST_FOLDER_PATH)) {
      if (fileName.endsWith(".pgn")) {
        result.add(fileName);
      }
    }
    result.sort(String::compareTo);
    return result;
  }

  private static void checkFile(String pgnName) {

    logger.info(pgnName);

    final List<String> fileLinesExpectedFromFileSystem = FileUtility.readFileLines(TEST_FOLDER_PATH, pgnName);

    final PgnGame pgnGameFromFileSystem = PgnCacheForStrictPgnParserTestCases.getPgn(TEST_FOLDER_PATH, pgnName);
    final List<String> fileLinesActualFromPgn = PgnCreate.toPgnLines(pgnGameFromFileSystem);
    assertEquals(fileLinesExpectedFromFileSystem, fileLinesActualFromPgn);
  }
}
