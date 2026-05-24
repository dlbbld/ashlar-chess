package com.dlb.chess.test.oracle.python;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;

import com.dlb.chess.board.Board;
import com.dlb.chess.board.enums.Side;
import com.dlb.chess.common.Nulls;
import com.dlb.chess.common.utility.BasicUtility;
import com.dlb.chess.model.PgnHalfMove;
import com.dlb.chess.pgn.PgnGame;
import com.dlb.chess.pgn.StrictPgnParser;
import com.dlb.chess.test.ConfigurationTestConstants;
import com.dlb.chess.test.pgntest.constants.PgnTestConstants;
import com.dlb.chess.test.pgntest.enums.PgnTest;

/**
 * Focused cross-validation of clean-chess's {@code InsufficientMaterialUtility} against python-chess's
 * {@code board.is_insufficient_material()} and {@code board.has_insufficient_material(color)} on every position of
 * every PGN in the four core insufficient-material buckets:
 *
 * <ul>
 * <li>{@link PgnTest#BASIC_INSUFFICIENT_MATERIAL_BOTH} — fixtures where the game reaches a position with both sides
 * holding insufficient material (the FIDE 9.2 mutual-insufficient termination);</li>
 * <li>{@link PgnTest#BASIC_INSUFFICIENT_MATERIAL_ONLY_WHITE} — only white has insufficient material;</li>
 * <li>{@link PgnTest#BASIC_INSUFFICIENT_MATERIAL_ONLY_BLACK} — only black has insufficient material;</li>
 * <li>{@link PgnTest#BASIC_INSUFFICIENT_MATERIAL_NONE} — neither side has insufficient material.</li>
 * </ul>
 *
 * <p>
 * These four buckets together exercise the full per-side / combined insufficient-material truth table. The broader
 * {@link TestPgnImportAgainstPythonChessOracle} also asserts the three predicates at every ply across the whole import
 * corpus, but bundles them with eight other per-ply fields; this test isolates the insufficient-material surface so
 * any divergence (clean-chess's {@code InsufficientMaterialUtility} vs python-chess) lands as a focused failure rather
 * than as one bullet among many.
 *
 * <p>
 * Reads the existing JSONL oracle at {@code src/test/resources/oracle/python-chess/<folderPart>.jsonl} — no new oracle
 * file format. The {@code isInsufficientMaterial} / {@code hasInsufficientMaterialWhite} /
 * {@code hasInsufficientMaterialBlack} fields have been part of {@link OracleMove} since slice 3.
 */
class TestInsufficientMaterialAgainstPythonChessOracle {

  private static final Logger LOGGER = Nulls
      .getLogger(TestInsufficientMaterialAgainstPythonChessOracle.class);

  private static final Path ORACLE_ROOT = Nulls.pathResolve(ConfigurationTestConstants.PROJECT_ROOT_FOLDER_PATH,
      "src/test/resources/oracle/python-chess");

  private static final List<PgnTest> BUCKETS = List.of(PgnTest.BASIC_INSUFFICIENT_MATERIAL_BOTH,
      PgnTest.BASIC_INSUFFICIENT_MATERIAL_ONLY_WHITE, PgnTest.BASIC_INSUFFICIENT_MATERIAL_ONLY_BLACK,
      PgnTest.BASIC_INSUFFICIENT_MATERIAL_NONE);

  @Test
  void insufficientMaterialAgainstPythonChessOracle() throws IOException {
    final List<String> failures = new ArrayList<>();
    var totalFixtures = 0;
    var totalPlies = 0;
    final int[] perBucketFixtureCounts = new int[BUCKETS.size()];

    for (var bIdx = 0; bIdx < BUCKETS.size(); bIdx++) {
      final PgnTest bucket = BUCKETS.get(bIdx);
      final Path jsonlPath = jsonlPathFor(bucket);
      LOGGER.info("Bucket {} → {}", bucket, jsonlPath);

      final List<OracleRecord> records = OracleJsonlReader.readAll(jsonlPath);
      if (records.isEmpty()) {
        failures.add(bucket + " — oracle file is empty or missing: " + jsonlPath);
        continue;
      }
      perBucketFixtureCounts[bIdx] = records.size();

      final Path folderPath = bucket.getFolderPath();
      for (final OracleRecord record : records) {
        totalFixtures++;
        final PgnGame pgnGame = StrictPgnParser.parse(folderPath, record.pgn());
        final Board board = new Board(pgnGame.startFen(), false);

        for (var ply = 0; ply < pgnGame.halfMoveList().size(); ply++) {
          totalPlies++;
          final PgnHalfMove halfMove = pgnGame.halfMoveList().get(ply);
          final OracleMove expected = record.moves().get(ply);
          board.moveStrict(halfMove.san());

          final var plyLabel = ply + 1;
          try {
            assertEquals(expected.isInsufficientMaterial(), board.isInsufficientMaterial(),
                () -> bucket + " / " + record.pgn() + " ply " + plyLabel
                    + " — isInsufficientMaterial mismatch (combined)");
            assertEquals(expected.hasInsufficientMaterialWhite(), board.isInsufficientMaterial(Side.WHITE),
                () -> bucket + " / " + record.pgn() + " ply " + plyLabel
                    + " — isInsufficientMaterial(WHITE) mismatch");
            assertEquals(expected.hasInsufficientMaterialBlack(), board.isInsufficientMaterial(Side.BLACK),
                () -> bucket + " / " + record.pgn() + " ply " + plyLabel
                    + " — isInsufficientMaterial(BLACK) mismatch");
          } catch (final AssertionError e) {
            failures.add(BasicUtility.getMessage(e));
          }
        }
      }
    }

    if (totalFixtures == 0) {
      fail("No fixtures iterated — bucket wiring is broken");
    }

    final var summary = new StringBuilder().append("Insufficient-material cross-validation: ");
    for (var i = 0; i < BUCKETS.size(); i++) {
      if (i > 0) {
        summary.append(", ");
      }
      summary.append(BUCKETS.get(i).name().replace("BASIC_INSUFFICIENT_MATERIAL_", "")).append('=')
          .append(perBucketFixtureCounts[i]);
    }
    summary.append(" — ").append(totalFixtures).append(" fixtures total, ").append(totalPlies)
        .append(" plies; clean-chess InsufficientMaterialUtility vs python-chess 1.11.2");
    LOGGER.info(Nulls.toString(summary));

    if (!failures.isEmpty()) {
      final var report = new StringBuilder().append(failures.size())
          .append(" insufficient-material disagreement(s) across ").append(totalFixtures)
          .append(" fixtures in ").append(BUCKETS.size()).append(" buckets:\n");
      for (final String f : failures) {
        report.append("  ").append(f).append('\n');
      }
      fail(Nulls.toString(report));
    }
  }

  private static Path jsonlPathFor(PgnTest bucket) {
    final Path pgnRoot = PgnTestConstants.PGN_TEST_ROOT_FOLDER_PATH;
    final Path bucketPath = bucket.getFolderPath();
    final String relative = Nulls.replace(Nulls.toString(Nulls.pathRelativize(pgnRoot, bucketPath)), '\\', '/');
    return Nulls.pathResolve(ORACLE_ROOT, relative + ".jsonl");
  }
}
