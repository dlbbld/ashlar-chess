// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.test.common.utility;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import io.github.dlbbld.ashlarchess.common.Nulls;
import io.github.dlbbld.ashlarchess.common.constants.ConfigurationConstants;
import io.github.dlbbld.ashlarchess.common.exceptions.FileSystemAccessException;
import io.github.dlbbld.ashlarchess.test.ConfigurationTestConstants;
import io.github.dlbbld.ashlarchess.test.FileComparison;

public class TestFileUtility {

  private static final Path TEST_FOLDER_PATH = Nulls.pathResolve(ConfigurationTestConstants.PROJECT_ROOT_FOLDER_PATH,
      "src/test/resources/fileUtility/utf8");

  private static final String TEST_FILE_NAME = "utf8.txt";

  private static final Path TEST_SOURCE_FILE_PATH = Nulls.pathResolve(TEST_FOLDER_PATH, TEST_FILE_NAME);

  private static final Path TEST_DESTINATION_FILE_PATH = Nulls.pathResolve(ConfigurationConstants.TEMP_FOLDER_PATH,
      TEST_FILE_NAME);

  // UTF-8 test string with diverse characters
  private static final String TEST_CONTENT = """
      ASCII: Hello, World!
      German: ä, ö, ü, ß
      Emojis: 😀, 🐍, 🚀
      Chinese: 你好
      Hindi: नमस्ते
      Russian: Привет
      Special: €, ©, ™, ✓, ∞""";

  @SuppressWarnings("static-method")
  @Test
  void testReadUtf8File() {
    final List<String> expectedLines = Nulls.asList(Nulls.split(TEST_CONTENT, "\\n"));
    final List<String> actualLines = FileUtility.readFileLines(TEST_SOURCE_FILE_PATH);

    assertEquals(expectedLines, actualLines);
  }

  @SuppressWarnings("static-method")
  @Test
  void testWriteUtf8File() {
    final List<String> lines = Nulls.asList(Nulls.split(TEST_CONTENT, "\\n"));
    FileUtility.writeFile(TEST_DESTINATION_FILE_PATH, lines);
    assertTrue(FileComparison.checkWithLineEndingsConversion(TEST_SOURCE_FILE_PATH, TEST_DESTINATION_FILE_PATH));
  }

  @SuppressWarnings("static-method")
  @Test
  void testWriteFileThrowsWhenParentFolderDoesNotExist(@TempDir Path tempFolder) {
    final Path filePath = Nulls.pathResolve(tempFolder, "missing/output.txt");

    @SuppressWarnings("null") final FileSystemAccessException exception = assertThrows(FileSystemAccessException.class,
        () -> FileUtility.writeFile(filePath, "text"));

    assertTrue(exception.getCause() instanceof IOException);
  }
}
