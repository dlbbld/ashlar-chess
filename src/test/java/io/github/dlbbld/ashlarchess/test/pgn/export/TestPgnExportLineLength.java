// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.test.pgn.export;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;

import io.github.dlbbld.ashlarchess.common.Nulls;
import io.github.dlbbld.ashlarchess.pgn.PgnCreate;
import io.github.dlbbld.ashlarchess.pgn.PgnGame;
import io.github.dlbbld.ashlarchess.test.common.utility.FileUtility;
import io.github.dlbbld.ashlarchess.test.pgn.parser.PgnCacheForStrictPgnParserTestCases;
import io.github.dlbbld.ashlarchess.test.pgntest.constants.PgnTestConstants;

class TestPgnExportLineLength {

  private static final Path TEST_FOLDER_PATH = Nulls.pathResolve(PgnTestConstants.PGN_EXPORT_TEST_ROOT_FOLDER_PATH,
      "lineLength");

  private static final Logger logger = Nulls.getLogger(TestPgnExportLineLength.class);

  @SuppressWarnings("static-method")
  @Test
  void test() {
    final List<String> pgnNameList = calculatePgnNameList();
    assertFalse(pgnNameList.isEmpty(), "The PGN export line-length test folder must contain PGN files");

    for (final String pgnName : pgnNameList) {
      checkFile(pgnName);
    }
  }

  private static List<String> calculatePgnNameList() {
    final List<String> result = new ArrayList<>();

    for (final String fileName : FileUtility.readFileNameList(TEST_FOLDER_PATH)) {
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
    final List<String> fileLinesActualFromPgn = PgnCreate.createPgnLines(pgnGameFromFileSystem);
    assertEquals(fileLinesExpectedFromFileSystem, fileLinesActualFromPgn);
  }
}
