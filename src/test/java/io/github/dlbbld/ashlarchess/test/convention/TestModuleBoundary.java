// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.test.convention;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.lang.module.ModuleDescriptor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

import io.github.dlbbld.ashlarchess.board.Board;
import io.github.dlbbld.ashlarchess.internal.Nulls;

/**
 * Guards the JPMS module boundary so it cannot drift silently. The boundary is otherwise maintained by hand (the
 * {@code module-info.java} exports and the {@code maven-javadoc-plugin} {@code excludePackageNames} are kept in sync by
 * code review only). These tests fail the build if:
 * <ul>
 * <li>the set of exported packages stops matching the intended public API ({@link #EXPECTED_EXPORTS}) - catching an
 * internal package accidentally exported, or a public package dropped;</li>
 * <li>the javadoc {@code excludePackageNames} list drifts from the non-exported production packages - catching an
 * internal package that would leak into the published {@code -javadoc.jar}, an exported package wrongly excluded, or a
 * dangling entry.</li>
 * </ul>
 * Changing the public API is therefore a deliberate act: it requires updating {@link #EXPECTED_EXPORTS} here too.
 */
class TestModuleBoundary {

  /**
   * The intended public API surface. Must be updated deliberately, in lock-step with {@code module-info.java}.
   */
  @SuppressWarnings("null")
  private static final Set<String> EXPECTED_EXPORTS = Set.of("io.github.dlbbld.ashlarchess.board",
      "io.github.dlbbld.ashlarchess.board.enums", "io.github.dlbbld.ashlarchess.fen",
      "io.github.dlbbld.ashlarchess.fen.model", "io.github.dlbbld.ashlarchess.pgn", "io.github.dlbbld.ashlarchess.san",
      "io.github.dlbbld.ashlarchess.adjudication", "io.github.dlbbld.ashlarchess.report",
      "io.github.dlbbld.ashlarchess.unwinnability", "io.github.dlbbld.ashlarchess.exceptions",
      "io.github.dlbbld.ashlarchess.bitboard");

  @SuppressWarnings("static-method")
  @Test
  void testExportsAreExactlyTheIntendedApi() {
    final Module module = Board.class.getModule();
    assertTrue(module.isNamed(), "ashlar-chess must run as the named module io.github.dlbbld.ashlarchess");
    assertEquals("io.github.dlbbld.ashlarchess", module.getName());

    final ModuleDescriptor descriptor = module.getDescriptor();
    @SuppressWarnings("null") final Set<String> actualExports = descriptor.exports().stream()
        .filter(e -> !e.isQualified()).map(ModuleDescriptor.Exports::source)
        .collect(Collectors.toCollection(TreeSet::new));

    assertEquals(new TreeSet<>(EXPECTED_EXPORTS), actualExports,
        "module-info exports drifted from the intended public API; update EXPECTED_EXPORTS only on purpose");
  }

  @SuppressWarnings("static-method")
  @Test
  void testJavadocExcludesMatchNonExportedPackages() throws IOException {
    final Set<String> excluded = parseJavadocExcludePackageNames();
    final Set<String> productionPackages = productionPackages();

    final Set<String> nonExported = new TreeSet<>(productionPackages);
    nonExported.removeAll(EXPECTED_EXPORTS);

    final Set<String> missingFromExcludes = new TreeSet<>(nonExported);
    missingFromExcludes.removeAll(excluded);
    assertTrue(missingFromExcludes.isEmpty(),
        "non-exported production packages missing from javadoc excludePackageNames (they would leak into the "
            + "-javadoc.jar): " + missingFromExcludes);

    final Set<String> wronglyExcluded = new TreeSet<>(excluded);
    wronglyExcluded.retainAll(EXPECTED_EXPORTS);
    assertTrue(wronglyExcluded.isEmpty(),
        "exported (public-API) packages must not be in javadoc excludePackageNames: " + wronglyExcluded);

    final Set<String> dangling = new TreeSet<>(excluded);
    dangling.removeAll(productionPackages);
    assertTrue(dangling.isEmpty(),
        "javadoc excludePackageNames lists packages that do not exist in production: " + dangling);
  }

  @SuppressWarnings("null")
  private static Set<String> parseJavadocExcludePackageNames() throws IOException {
    final String pom = Files.readString(Paths.get("pom.xml"));
    final Matcher matcher = Pattern.compile("<excludePackageNames>(.*?)</excludePackageNames>", Pattern.DOTALL)
        .matcher(pom);
    assertTrue(matcher.find(), "excludePackageNames not found in pom.xml");
    return Arrays.stream(Nulls.split(matcher.group(1), ",")).map(Nulls::trim).filter(s -> !s.isEmpty())
        .collect(Collectors.toCollection(TreeSet::new));
  }

  @SuppressWarnings("null")
  private static Set<String> productionPackages() throws IOException {
    final Path sourceRoot = Paths.get("src/main/java");
    try (Stream<Path> paths = Files.walk(sourceRoot)) {
      return paths.filter(Files::isRegularFile).filter(p -> p.getFileName().toString().endsWith(".java"))
          .filter(p -> !"module-info.java".equals(p.getFileName().toString()))
          .map(p -> sourceRoot.relativize(p.getParent()).toString().replace(File.separatorChar, '.'))
          .collect(Collectors.toCollection(TreeSet::new));
    }
  }
}
