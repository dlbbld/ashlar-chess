// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.test;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.logging.log4j.Logger;

import io.github.dlbbld.ashlarchess.common.Nulls;

public class FileComparison {

  private static final Logger logger = Nulls.getLogger(FileComparison.class);

  // problem PGN files in Eclipse suddenly have windows line breaks instead of unix line breaks
  @SuppressWarnings("null")
  public static boolean checkWithLineEndingsConversion(Path pgnExpectedPath, Path pgnActualPath) {
    logger.info("Testing " + pgnExpectedPath + " against " + pgnActualPath);

    try {
      final String expectedAsIs = Files.readString(pgnExpectedPath, Charset.forName("UTF-8"));
      final String expectedConverted = convertWindowsToUnixLineEndings(expectedAsIs);
      final String actualAsIs = Files.readString(pgnActualPath, Charset.forName("UTF-8"));
      return expectedConverted.equals(actualAsIs);
    } catch (final IOException e) {
      logger.error("Error while comparing files: " + e.getMessage());
      return false;
    }
  }

  private static String convertWindowsToUnixLineEndings(String text) {
    return Nulls.replace(text, "\r\n", "\n");
  }

  public static boolean check(Path pgnExpectedPath, Path pgnActualPath) {

    logger.info("Testing " + pgnExpectedPath + " against " + pgnActualPath);

    try {
      return determineIsFilesAreEqual(pgnExpectedPath, pgnActualPath);
    } catch (final IOException e) {
      logger.error("Error while comparing files: " + e.getMessage());
      return false;
    }
  }

  private static boolean determineIsFilesAreEqual(Path path1, Path path2) throws IOException {
    // Quick check: if sizes differ, no need to compare contents
    if (Files.size(path1) != Files.size(path2)) {
      return false;
    }
    // mismatch() returns -1 if there is no mismatch
    return Files.mismatch(path1, path2) == -1;
  }

}
