// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.test.convention;

import static org.junit.jupiter.api.Assertions.fail;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.junit.jupiter.api.Test;

import io.github.dlbbld.ashlarchess.common.Nulls;
import io.github.dlbbld.ashlarchess.test.ConfigurationTestConstants;
import io.github.dlbbld.ashlarchess.test.common.utility.FileUtility;

/**
 * Convention test: every Java class under {@code src/test/java} whose simple name starts with {@code Test} must contain
 * at least one {@code @Test} annotation.
 *
 * <p>
 * Rationale: the {@code Test} prefix at every import site is a promise - readers expect a runnable JUnit class. A
 * {@code Test}-named class with no {@code @Test} is either a misplaced utility (which should be renamed to
 * {@code Generate*}/{@code Run*}/etc.) or a class whose test methods got their annotations stripped during a refactor
 * and silently stopped running. Either is drift worth catching at setup time.
 *
 * <p>
 * Pairs with the implicit convention that {@code Generate*} classes must NOT carry {@code @Test} (they are
 * {@code main}-driven utilities). Together the two enforce a clean shape: a class is runnable as a JUnit test iff its
 * simple name starts with {@code Test}.
 *
 * <p>
 * Detection is a regex against the source file's contents matching any JUnit-Jupiter test-method annotation: plain
 * {@code @Test}, plus the parameterized / repeated / templated / factory variants ({@code @ParameterizedTest},
 * {@code @RepeatedTest}, {@code @TestTemplate}, {@code @TestFactory}). All of these are runnable JUnit methods, so any
 * one is enough to make a {@code Test}-prefixed class non-empty.
 *
 * <p>
 * The test class itself satisfies the invariant - its name starts with {@code Test} and it has a plain {@code @Test}
 * above {@link #everyTestPrefixedClassDeclaresAtLeastOneTestMethod()}.
 */
class TestConventionTestClassHasActiveTest {

  private static final Path TEST_JAVA_ROOT = Nulls.pathResolve(ConfigurationTestConstants.PROJECT_ROOT_FOLDER_PATH,
      "src/test/java");

  private static final String REQUIRED_NAME_PREFIX = "Test";

  // Matches @Test, @ParameterizedTest, @RepeatedTest, @TestTemplate, @TestFactory. Any of these
  // makes a class runnable as a JUnit test.
  private static final Pattern TEST_ANNOTATION = Nulls
      .compile("@(?:Test|ParameterizedTest|RepeatedTest|TestTemplate|TestFactory)\\b");

  @SuppressWarnings("static-method")
  @Test
  void everyTestPrefixedClassDeclaresAtLeastOneTestMethod() {
    final List<String> violations = new ArrayList<>();

    for (final Path p : FileUtility.listAllFilesRecursively(TEST_JAVA_ROOT)) {
      if (!Nulls.toString(p).endsWith(".java")
          || !Nulls.toString(Nulls.getFileName(p)).startsWith(REQUIRED_NAME_PREFIX)) {
        continue;
      }
      final String contents = FileUtility.readFileAsString(p);
      if (!TEST_ANNOTATION.matcher(contents).find()) {
        violations.add(Nulls.replace(Nulls.toString(Nulls.pathRelativize(TEST_JAVA_ROOT, p)), '\\', '/'));
      }
    }

    if (!violations.isEmpty()) {
      final StringBuilder report = new StringBuilder().append(violations.size())
          .append(" Test-prefixed class(es) under src/test/java contain no @Test annotation:\n");
      for (final String violation : violations) {
        report.append("  ").append(violation).append('\n');
      }
      fail(report.toString());
    }
  }
}
