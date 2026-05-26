package com.dlb.chess.test.pgn.setup;

import static org.junit.jupiter.api.Assertions.fail;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;

import com.dlb.chess.board.Board;
import com.dlb.chess.common.Nulls;
import com.dlb.chess.test.model.PgnFen;
import com.dlb.chess.test.model.PgnTestCaseList;
import com.dlb.chess.test.pgntest.enums.PgnTest;

/**
 * On-demand audit that verifies every {@link PgnFen}'s cached {@code fen} matches the position actually reached by
 * replaying its PGN. Disabled by default because it replays the full corpus.
 *
 * <p>
 * Run with:
 *
 * <pre>{@code
 * mvn -q test -Dtest=TestFenAudit -Dfen.audit=true
 * }</pre>
 *
 * <p>
 * Every mismatch is reported on the test failure message: PGN filename, the {@link PgnTest} it belongs to, the
 * expected (cached) FEN, and the actual (replayed) FEN. Apply fixes by editing the matching literal in
 * {@code PgnTestCaseCatalog}.
 */
class TestSetupFinalFen {

  private static final int PROGRESS_LOG_INTERVAL = 50;

  private static final Logger logger = Nulls.getLogger(TestSetupFinalFen.class);

  @SuppressWarnings("static-method")
  @Test
  @EnabledIfSystemProperty(named = "fen.audit", matches = "true")
  void auditCachedFenAgainstPgnReplay() {
    final List<String> mismatches = new ArrayList<>();
    final List<String> errors = new ArrayList<>();
    var totalFixtures = 0;
    final var totalFixturesToCheck = countFixtures();

    logger.info("Auditing cached final FENs for {} fixtures across {} PgnTest folders.", totalFixturesToCheck,
        PgnTest.values().length);

    for (final PgnTest pgnTest : PgnTest.values()) {
      final PgnTestCaseList testCaseList = PgnTestCaseCatalog.getTestList(pgnTest);
      for (final PgnFen testCase : testCaseList.list()) {
        totalFixtures++;
        try {
          final Board board = testCase.game(pgnTest);
          final String actualFen = board.getFen();
          final String expectedFen = testCase.finalFen();
          if (!actualFen.equals(expectedFen)) {
            mismatches.add(Nulls.format("%s (%s)%n  expected: %s%n  actual:   %s", testCase.pgnName(), pgnTest,
                expectedFen, actualFen));
          }
        } catch (final RuntimeException e) {
          // PGN missing on disk, parse error, or illegal move: surface alongside the FEN mismatches so the
          // operator gets the full picture in one pass instead of crashing on the first bad fixture.
          errors.add(Nulls.format("%s (%s): %s", testCase.pgnName(), pgnTest, e.getMessage()));
        }
        if (totalFixtures % PROGRESS_LOG_INTERVAL == 0 || totalFixtures == totalFixturesToCheck) {
          logger.info("Checked {}/{} final FENs; {} mismatches; {} replay errors. Current: {} ({})", totalFixtures,
              totalFixturesToCheck, mismatches.size(), errors.size(), testCase.pgnName(), pgnTest);
        }
      }
    }

    logger.info("Audited {} fixtures across {} PgnTest folders; {} mismatches; {} replay errors.", totalFixtures,
        PgnTest.values().length, mismatches.size(), errors.size());

    if (!mismatches.isEmpty() || !errors.isEmpty()) {
      final StringBuilder sb = new StringBuilder();
      if (!mismatches.isEmpty()) {
        sb.append(String.format("%d fixture(s) have a stale cached FEN:%n%n", mismatches.size()));
        sb.append(String.join("\n\n", mismatches));
      }
      if (!errors.isEmpty()) {
        if (sb.length() > 0) {
          sb.append("\n\n");
        }
        sb.append(String.format("%d fixture(s) could not be replayed:%n%n", errors.size()));
        sb.append(String.join("\n", errors));
      }
      fail(sb.toString());
    }
  }

  private static int countFixtures() {
    var result = 0;
    for (final PgnTest pgnTest : PgnTest.values()) {
      result += PgnTestCaseCatalog.getTestList(pgnTest).list().size();
    }
    return result;
  }
}
