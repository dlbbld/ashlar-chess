package com.dlb.chess.test.pgn.parser.beyond;

import static com.dlb.chess.common.enums.GameStatus.DEAD_POSITION_INSUFFICIENT_MATERIAL;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.Assumptions.assumeFalse;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.annotation.Nullable;
import org.junit.jupiter.api.Test;

import com.dlb.chess.common.Nulls;
import com.dlb.chess.common.enums.GameStatus;
import com.dlb.chess.pgn.StrictPgnParser;
import com.dlb.chess.pgn.StrictPgnParserValidationException;
import com.dlb.chess.pgn.StrictPgnParserValidationProblem;
import com.dlb.chess.san.SanValidationProblem;
import com.dlb.chess.test.ConfigurationTestConstants;
import com.dlb.chess.test.RestrictTestConstants;

/**
 * Verifies that the lone remaining PGN fixture under {@code pgnParser/legacy/common/beyond/} — a KvK game with one
 * recorded halfmove past the dead-position-insufficient-material moment — is rejected by the strict parser exactly as
 * expected. The fivefold and 75-move legacy fixtures that previously lived alongside it were reactivated into the
 * regular corpus when those rules became queryable rather than enforced; this last fixture stays parked until the
 * dead-position auto-termination is similarly dropped in the next release.
 */
class TestLegacyPgnParsePlaysBeyondAudit {

  private static final Path LEGACY_FOLDER = Nulls.pathResolve(ConfigurationTestConstants.PROJECT_ROOT_FOLDER_PATH,
      "src/test/resources/pgnParser/legacy/common/beyond");

  private record Expected(StrictPgnParserValidationProblem problem, SanValidationProblem sanProblem,
      @Nullable GameStatus gameStatus) {
  }

  private static Expected sanGameEnded(GameStatus gameStatus) {
    return new Expected(StrictPgnParserValidationProblem.SAN, SanValidationProblem.GAME_ALREADY_ENDED, gameStatus);
  }

  private static final Map<String, Expected> EXPECTED = buildExpected();

  @SuppressWarnings("static-method")
  @Test
  void test() {
    assumeFalse(RestrictTestConstants.IS_EXCLUDE_LONG_RUNNING_LEGACY_PGN_PARSE_PLAYS_BEYOND_AUDIT,
        "Long-running legacy parse audit excluded by IS_EXCLUDE_LONG_RUNNING_LEGACY_PGN_PARSE_PLAYS_BEYOND_AUDIT");

    final List<String> failures = new ArrayList<>();
    var totalFiles = 0;

    for (final Map.Entry<String, Expected> entry : Nulls.entrySet(EXPECTED)) {
      totalFiles++;
      final String relativePath = Nulls.getKey(entry);
      final Expected expected = Nulls.getValue(entry);

      final var slash = relativePath.lastIndexOf('/');
      final var subfolder = Nulls.substring(relativePath, 0, slash);
      final var fileName = Nulls.substring(relativePath, slash + 1);
      final var folderPath = Nulls.pathResolve(LEGACY_FOLDER, subfolder);

      try {
        StrictPgnParser.parse(folderPath, fileName);
        failures.add(relativePath + " — expected rejection (" + expected + ") but parsed cleanly");
      } catch (final StrictPgnParserValidationException e) {
        if (e.getStrictPgnParserValidationProblem() != expected.problem
            || e.getSanValidationProblem() != expected.sanProblem || e.getGameStatus() != expected.gameStatus) {
          failures.add(relativePath + " — expected " + expected + ", got problem="
              + e.getStrictPgnParserValidationProblem() + ", sanProblem=" + e.getSanValidationProblem()
              + ", gameStatus=" + e.getGameStatus() + " (" + e.getMessage() + ")");
        }
      }
    }

    if (totalFiles != 1) {
      fail("Expected 1 legacy fixture in the EXPECTED map, found " + totalFiles);
    }
    if (!failures.isEmpty()) {
      final var report = new StringBuilder().append(failures.size()).append(" of ").append(totalFiles)
          .append(" legacy fixtures did not match expected ").append("rejection:\n");
      for (final String f : failures) {
        report.append("  ").append(f).append('\n');
      }
      fail(report.toString());
    }
  }

  private static Map<String, Expected> buildExpected() {
    final Map<String, Expected> m = new LinkedHashMap<>();
    m.put("lastMoveAddedAccidentally/02_last_move_added_accidentally_result_draw_one_move_in_KvK.pgn",
        sanGameEnded(DEAD_POSITION_INSUFFICIENT_MATERIAL));
    return Nulls.copyOfMap(m);
  }
}
