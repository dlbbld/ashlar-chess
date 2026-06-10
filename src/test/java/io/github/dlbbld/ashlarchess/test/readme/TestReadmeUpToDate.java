// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.test.readme;

import static org.junit.jupiter.api.Assertions.fail;

import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;

import io.github.dlbbld.ashlarchess.common.Nulls;
import io.github.dlbbld.ashlarchess.test.common.utility.FileUtility;

/**
 * Setup invariant: the committed {@code README.md} equals a fresh render of {@code README.template.md} through
 * {@link ReadmeDoc}. This is what makes the README's compile-and-output guarantee real: a shown snippet is sliced from
 * compiled source and a shown output is captured from running it, so if this test is green every example in the README
 * provably compiles and prints exactly what is shown. On failure, regenerate with {@link GenerateReadme}.
 */
class TestReadmeUpToDate {

  private static final Path README_PATH = Path.of("README.md");

  @SuppressWarnings("static-method")
  @Test
  void readmeMatchesFreshRender() {
    final List<String> expected = ReadmeDoc.generate();
    final List<String> actual = FileUtility.readFileLines(README_PATH);
    if (expected.equals(actual)) {
      return;
    }
    fail(describeFirstDifference(expected, actual));
  }

  private static String describeFirstDifference(List<String> expected, List<String> actual) {
    final StringBuilder message = new StringBuilder(
        "README.md is out of date - run GenerateReadme to regenerate it from README.template.md.\n");
    final int max = Math.max(expected.size(), actual.size());
    for (int i = 0; i < max; i++) {
      final String expectedLine = i < expected.size() ? expected.get(i) : "<no such line>";
      final String actualLine = i < actual.size() ? actual.get(i) : "<no such line>";
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
