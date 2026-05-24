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
import com.dlb.chess.test.ConfigurationTestConstants;
import com.dlb.chess.test.pgntest.constants.PgnTestConstants;
import com.dlb.chess.test.pgntest.enums.PgnTest;

/**
 * Checks insufficient material assessment against python-chess.
 *
 * <p>
 * Focused cross-validation of clean-chess's {@code InsufficientMaterialUtility} against python-chess's
 * {@code board.is_insufficient_material()} and {@code board.has_insufficient_material(color)} on the <strong>final
 * position</strong> of every PGN in the four core insufficient-material buckets:
 *
 * <ul>
 * <li>{@link PgnTest#BASIC_INSUFFICIENT_MATERIAL_BOTH} — fixtures whose final position has both sides holding
 * insufficient material (the FIDE 9.2 mutual-insufficient termination);</li>
 * <li>{@link PgnTest#BASIC_INSUFFICIENT_MATERIAL_ONLY_WHITE} — only white has insufficient material at the end;</li>
 * <li>{@link PgnTest#BASIC_INSUFFICIENT_MATERIAL_ONLY_BLACK} — only black has insufficient material at the end;</li>
 * <li>{@link PgnTest#BASIC_INSUFFICIENT_MATERIAL_NONE} — neither side has insufficient material at the end.</li>
 * </ul>
 *
 * <p>
 * Insufficient material is a function of the position alone, not the path that led to it, so this test builds a
 * history-less board directly from the oracle's recorded {@code finalFen} (no PGN parse, no per-ply replay) and
 * compares the three predicates against the python-chess values recorded for the same final position. Per-ply
 * cross-validation across the whole import corpus is the broader {@link TestPgnImportAgainstPythonChessOracle}'s job;
 * this test isolates the predicate subject of interest.
 *
 * <p>
 * Reads the existing JSONL oracle at {@code src/test/resources/oracle/python-chess/<folderPart>.jsonl} — no new oracle
 * data. The expected final-position material flags come from the last entry in {@link OracleRecord#moves()}, whose
 * {@code fenAfter} equals {@link OracleRecord#finalFen()} by construction (see the generator script docstring).
 */
class TestInsufficientMaterialAgainstPythonChessOracle {

  private static final Logger LOGGER = Nulls.getLogger(TestInsufficientMaterialAgainstPythonChessOracle.class);

  private static final Path ORACLE_ROOT = Nulls.pathResolve(ConfigurationTestConstants.PROJECT_ROOT_FOLDER_PATH,
      "src/test/resources/oracle/python-chess");

  private static final List<PgnTest> BUCKETS = Nulls.listOf(PgnTest.BASIC_INSUFFICIENT_MATERIAL_BOTH,
      PgnTest.BASIC_INSUFFICIENT_MATERIAL_ONLY_WHITE, PgnTest.BASIC_INSUFFICIENT_MATERIAL_ONLY_BLACK,
      PgnTest.BASIC_INSUFFICIENT_MATERIAL_NONE);

  @SuppressWarnings("static-method")
  @Test
  void insufficientMaterialAgainstPythonChessOracle() throws IOException {
    final List<String> failures = new ArrayList<>();
    var totalFixtures = 0;
    final var perBucketFixtureCounts = new int[BUCKETS.size()];

    for (var bucketIdx = 0; bucketIdx < BUCKETS.size(); bucketIdx++) {
      final PgnTest bucket = Nulls.get(BUCKETS, bucketIdx);
      final Path jsonlPath = jsonlPathFor(bucket);
      LOGGER.info("Bucket {} → {}", bucket, jsonlPath);

      final List<OracleRecord> records = OracleJsonlReader.readAll(jsonlPath);
      if (records.isEmpty()) {
        failures.add(bucket + " — oracle file is empty or missing: " + jsonlPath);
        continue;
      }
      perBucketFixtureCounts[bucketIdx] = records.size();

      for (final OracleRecord record : records) {
        totalFixtures++;
        if (record.moves().isEmpty()) {
          failures.add(
              bucket + " / " + record.pgn() + " — oracle record has no plies; material at start FEN is not recorded");
          continue;
        }
        final OracleMove expectedFinal = Nulls.get(record.moves(), record.moves().size() - 1);
        final Board board = new Board(record.finalFen());

        try {
          assertEquals(expectedFinal.isInsufficientMaterial(), board.isInsufficientMaterial(),
              () -> bucket + " / " + record.pgn() + " — isInsufficientMaterial mismatch (combined)");
          assertEquals(expectedFinal.hasInsufficientMaterialWhite(), board.isInsufficientMaterial(Side.WHITE),
              () -> bucket + " / " + record.pgn() + " — isInsufficientMaterial(WHITE) mismatch");
          assertEquals(expectedFinal.hasInsufficientMaterialBlack(), board.isInsufficientMaterial(Side.BLACK),
              () -> bucket + " / " + record.pgn() + " — isInsufficientMaterial(BLACK) mismatch");
        } catch (final AssertionError e) {
          failures.add(BasicUtility.getMessage(e));
        }
      }
    }

    if (totalFixtures == 0) {
      fail("No fixtures iterated — bucket wiring is broken");
    }

    final var summary = new StringBuilder().append("Insufficient-material final-position cross-validation: ");
    for (var i = 0; i < BUCKETS.size(); i++) {
      if (i > 0) {
        summary.append(", ");
      }
      summary.append(Nulls.get(BUCKETS, i).name().replace("BASIC_INSUFFICIENT_MATERIAL_", "")).append('=')
          .append(perBucketFixtureCounts[i]);
    }
    summary.append(" — ").append(totalFixtures).append(" fixtures total; clean-chess InsufficientMaterialUtility")
        .append(" vs python-chess 1.11.2");
    LOGGER.info(Nulls.toString(summary));

    if (!failures.isEmpty()) {
      final StringBuilder report = new StringBuilder();
      report.append(failures.size()).append(" insufficient-material disagreement(s) across ").append(totalFixtures)
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
