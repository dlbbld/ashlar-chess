package io.github.dlbbld.ashlarchess.test.convention;

import static org.junit.jupiter.api.Assertions.fail;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import io.github.dlbbld.ashlarchess.common.Nulls;
import io.github.dlbbld.ashlarchess.test.ConfigurationTestConstants;
import io.github.dlbbld.ashlarchess.test.common.utility.FileUtility;

/**
 * Convention test: every Java file under {@code src/test/java} must declare a package starting with
 * {@code io.github.dlbbld.ashlarchess}.
 *
 * <p>
 * Two flavours of test live under this prefix:
 * <ul>
 * <li><b>Black-box / integration tests</b> under {@code io.github.dlbbld.ashlarchess.test.*}: exercise production code
 * through its public API. The majority of the suite lives here.</li>
 * <li><b>Same-package white-box tests</b> under {@code io.github.dlbbld.ashlarchess.<production-package>}: exercise
 * package-private production code that does not belong on the public API surface. Use sparingly - only when an internal
 * class genuinely needs test coverage that can't be achieved through the public surface.</li>
 * </ul>
 *
 * <p>
 * The test-scope marker (this file is {@code src/test/java}) keeps the source-tree separation; the
 * {@code io.github.dlbbld.ashlarchess} prefix keeps the package tree single-rooted and prevents stray test files in
 * unrelated namespaces.
 *
 * <p>
 * The package name is derived from the file's path under {@code src/test/java}; Java requires the {@code package}
 * declaration to match, so reading the path is equivalent to reading the declaration and avoids parsing the source.
 */
class TestConventionTestPackageNaming {

  private static final Path TEST_JAVA_ROOT = Nulls.pathResolve(ConfigurationTestConstants.PROJECT_ROOT_FOLDER_PATH,
      "src/test/java");

  private static final String REQUIRED_PACKAGE_PREFIX = "io.github.dlbbld.ashlarchess";

  @SuppressWarnings("static-method")
  @Test
  void everyTestSourceFileIsUnderRequiredPackagePrefix() {
    final List<String> violations = new ArrayList<>();

    for (final Path p : FileUtility.listAllFilesRecursively(TEST_JAVA_ROOT)) {
      if (!Nulls.toString(p).endsWith(".java")) {
        continue;
      }
      final String packageName = derivePackageName(p);
      if (!REQUIRED_PACKAGE_PREFIX.equals(packageName) && !packageName.startsWith(REQUIRED_PACKAGE_PREFIX + ".")) {
        violations.add(
            packageName + "  in  " + Nulls.replace(Nulls.toString(Nulls.pathRelativize(TEST_JAVA_ROOT, p)), '\\', '/'));
      }
    }

    if (!violations.isEmpty()) {
      final StringBuilder report = new StringBuilder().append(violations.size())
          .append(" file(s) under src/test/java declare a package outside ").append(REQUIRED_PACKAGE_PREFIX)
          .append(":\n");
      for (final String v : violations) {
        report.append("  ").append(v).append('\n');
      }
      fail(report.toString());
    }
  }

  private static String derivePackageName(Path javaFile) {
    final Path parent = javaFile.getParent();
    if (parent == null) {
      return "";
    }
    final Path relative = Nulls.pathRelativize(TEST_JAVA_ROOT, parent);
    return Nulls.replace(Nulls.replace(Nulls.toString(relative), '\\', '.'), '/', '.');
  }
}
