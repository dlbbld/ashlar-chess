// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.test.readme;

import static org.junit.jupiter.api.Assertions.fail;

import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;

import io.github.dlbbld.ashlarchess.internal.Nulls;
import io.github.dlbbld.ashlarchess.test.common.utility.FileUtility;

/**
 * Setup invariant: the committed user-facing Markdown docs equal fresh renders of their templates through
 * {@link ReadmeDoc}. This is what makes the compile-and-output guarantee real: a shown snippet is sliced from compiled
 * source and a shown output is captured from running it, so if this test is green every rendered example provably
 * compiles and prints exactly what is shown. On failure, regenerate with {@link GenerateReadme}.
 */
class TestReadmeUpToDate {

  private static final Path README_PATH = Nulls.pathOf("README.md");
  private static final Path MANUAL_PATH = Nulls.pathOf("manual.md");

  @SuppressWarnings("static-method")
  @Test
  void readmeMatchesFreshRender() {
    final List<String> expected = ReadmeDoc.generateReadme();
    final List<String> actual = FileUtility.readFileLines(README_PATH);
    if (expected.equals(actual)) {
      return;
    }
    fail(describeFirstDifference("README.md", expected, actual));
  }

  @SuppressWarnings("static-method")
  @Test
  void manualMatchesFreshRender() {
    final List<String> expected = ReadmeDoc.generateManual();
    final List<String> actual = FileUtility.readFileLines(MANUAL_PATH);
    if (expected.equals(actual)) {
      return;
    }
    fail(describeFirstDifference("manual.md", expected, actual));
  }

  private static String describeFirstDifference(String documentName, List<String> expected, List<String> actual) {
    final StringBuilder message = new StringBuilder(
        documentName + " is out of date - run GenerateReadme to regenerate the public Markdown docs.\n");
    final int max = Math.max(expected.size(), actual.size());
    for (int i = 0; i < max; i++) {
      final String expectedLine = i < expected.size() ? Nulls.get(expected, i) : "<no such line>";
      final String actualLine = i < actual.size() ? Nulls.get(actual, i) : "<no such line>";
      if (!expectedLine.equals(actualLine)) {
        message.append("First difference at line ").append(i + 1).append(":\n");
        message.append("  rendered: ").append(expectedLine).append('\n');
        message.append("  committed: ").append(actualLine).append('\n');
        break;
      }
    }
    return Nulls.toString(message);
  }
}
