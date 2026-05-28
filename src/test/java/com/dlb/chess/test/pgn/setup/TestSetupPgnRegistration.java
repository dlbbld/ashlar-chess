package com.dlb.chess.test.pgn.setup;

import static org.junit.jupiter.api.Assertions.fail;

import java.nio.file.Path;
import java.util.Set;
import java.util.TreeSet;

import org.junit.jupiter.api.Test;

import com.dlb.chess.common.Nulls;
import com.dlb.chess.test.common.utility.FileUtility;
import com.dlb.chess.test.model.PgnFen;
import com.dlb.chess.test.model.PgnTestCaseList;
import com.dlb.chess.test.pgntest.constants.PgnTestConstants;
import com.dlb.chess.test.pgntest.enums.PgnTest;

/**
 * Setup invariant: every PGN file under {@code src/test/resources/pgn} has a corresponding test case registered in
 * {@link PgnTestCaseCatalog}, and every registered test case has a matching PGN file on disk. The two sets must agree
 * exactly.
 *
 * <p>
 * Rationale: the test corpus is driven by the registry - a PGN file with no expected-value entry is invisible to every
 * corpus-iterating test, and a registered entry without a disk file would fail mid-run rather than at setup time.
 * Comparing the two sets up front surfaces either kind of drift with enough information to fix it (the set of orphan
 * file names on each side).
 *
 * <p>
 * The check relies on the existing uniqueness invariant on registered file names enforced by
 * {@code PgnExpectedValue.checkUniqueFileNames}, which lets a single set of file names stand in for the registry
 * without losing information.
 */
class TestSetupPgnRegistration {

  @SuppressWarnings("static-method")
  @Test
  void everyPgnOnDiskIsRegisteredAndViceVersa() {
    final Set<String> diskFileNames = collectPgnNamesOnDisk();
    final Set<String> registeredFileNames = collectRegisteredPgnNames();

    final Set<String> onDiskNotRegistered = new TreeSet<>(diskFileNames);
    onDiskNotRegistered.removeAll(registeredFileNames);

    final Set<String> registeredNotOnDisk = new TreeSet<>(registeredFileNames);
    registeredNotOnDisk.removeAll(diskFileNames);

    if (onDiskNotRegistered.isEmpty() && registeredNotOnDisk.isEmpty()) {
      return;
    }

    final StringBuilder report = new StringBuilder();
    report.append("PGN corpus and PgnExpectedValue registry are out of sync (").append(diskFileNames.size())
        .append(" on disk vs ").append(registeredFileNames.size()).append(" registered).\n");
    if (!onDiskNotRegistered.isEmpty()) {
      report.append(onDiskNotRegistered.size()).append(" PGN file(s) on disk with no registered test case:\n");
      for (final String fileName : onDiskNotRegistered) {
        report.append("  ").append(fileName).append('\n');
      }
    }
    if (!registeredNotOnDisk.isEmpty()) {
      report.append(registeredNotOnDisk.size()).append(" registered test case(s) with no PGN file on disk:\n");
      for (final String fileName : registeredNotOnDisk) {
        report.append("  ").append(fileName).append('\n');
      }
    }
    fail(report.toString());
  }

  private static Set<String> collectPgnNamesOnDisk() {
    final Set<String> names = new TreeSet<>();
    for (final Path p : FileUtility.listAllFilesRecursively(PgnTestConstants.PGN_TEST_ROOT_FOLDER_PATH)) {
      if (!Nulls.toString(p).endsWith(".pgn")) {
        continue;
      }
      names.add(Nulls.toString(Nulls.getFileName(p)));
    }
    return names;
  }

  private static Set<String> collectRegisteredPgnNames() {
    final Set<String> names = new TreeSet<>();
    for (final PgnTest pgnTest : PgnTest.values()) {
      final PgnTestCaseList testCaseList = PgnTestCaseCatalog.getTestList(pgnTest);
      for (final PgnFen testCase : testCaseList.list()) {
        names.add(testCase.pgnName());
      }
    }
    return names;
  }
}
