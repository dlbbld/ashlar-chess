package io.github.dlbbld.ashlarchess.test.pgn.setup;

import static org.junit.jupiter.api.Assertions.fail;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import io.github.dlbbld.ashlarchess.test.pgntest.enums.PgnTest;

/**
 * Setup invariant: every {@link PgnTest} enum value's folder path resolves to an existing directory under
 * {@code src/test/resources/pgn}.
 *
 * <p>
 * Rationale: every {@code PgnTest} value declares a relative folder string that the registry uses to locate fixtures. A
 * typo, a moved/deleted folder, or an enum entry that never had a backing folder turns into a runtime error or silent
 * zero-iteration only when a test that touches that bucket happens to run. Asserting the mapping at setup time surfaces
 * the breakage immediately and points at the specific enum value(s) to fix.
 *
 * <p>
 * The check is one-directional: every enum points to a real folder. The reverse direction (every folder under
 * {@code src/test/resources/pgn} maps to some enum) would conflate this with the "no orphan files" invariant covered by
 * {@link TestSetupPgnRegistration}.
 */
class TestSetupPgnTestEnumFolders {

  @SuppressWarnings("static-method")
  @Test
  void everyPgnTestEnumValueResolvesToExistingFolder() {
    final List<String> violations = new ArrayList<>();

    for (final PgnTest pgnTest : PgnTest.values()) {
      final Path folderPath = pgnTest.getFolderPath();
      if (!Files.isDirectory(folderPath)) {
        violations.add(pgnTest.name() + " -> " + folderPath);
      }
    }

    if (violations.isEmpty()) {
      return;
    }

    final StringBuilder report = new StringBuilder().append(violations.size())
        .append(" PgnTest enum value(s) point to a missing folder:\n");
    for (final String violation : violations) {
      report.append("  ").append(violation).append('\n');
    }
    fail(report.toString());
  }
}
