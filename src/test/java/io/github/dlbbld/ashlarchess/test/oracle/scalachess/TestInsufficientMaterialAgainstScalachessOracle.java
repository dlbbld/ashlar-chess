// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.test.oracle.scalachess;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;

import com.google.common.collect.ImmutableList;

import io.github.dlbbld.ashlarchess.board.Board;
import io.github.dlbbld.ashlarchess.board.enums.Side;
import io.github.dlbbld.ashlarchess.common.Nulls;
import io.github.dlbbld.ashlarchess.common.utility.BasicUtility;
import io.github.dlbbld.ashlarchess.test.ConfigurationTestConstants;
import io.github.dlbbld.ashlarchess.test.oracle.insufficientmaterial.InsufficientMaterialJsonlReader;
import io.github.dlbbld.ashlarchess.test.oracle.insufficientmaterial.InsufficientMaterialRecord;
import io.github.dlbbld.ashlarchess.test.pgntest.constants.PgnTestConstants;
import io.github.dlbbld.ashlarchess.test.pgntest.enums.PgnTest;

/**
 * Checks insufficient-material assessment against scalachess, the third differential oracle (after python-chess and
 * chesslib).
 *
 * <p>
 * Position-only cross-validation of ashlar-chess's {@code InsufficientMaterialUtility} against scalachess's
 * {@code Variant.isInsufficientMaterial} / {@code playerHasInsufficientMaterial} / {@code opponentHasInsufficientMaterial}
 * (mapped to absolute white/black) on the <strong>final position</strong> of every PGN in the four core
 * insufficient-material buckets:
 *
 * <ul>
 * <li>{@link PgnTest#BASIC_INSUFFICIENT_MATERIAL_BOTH} - both sides hold insufficient material;</li>
 * <li>{@link PgnTest#BASIC_INSUFFICIENT_MATERIAL_ONLY_WHITE} - only white;</li>
 * <li>{@link PgnTest#BASIC_INSUFFICIENT_MATERIAL_ONLY_BLACK} - only black;</li>
 * <li>{@link PgnTest#BASIC_INSUFFICIENT_MATERIAL_NONE} - neither side.</li>
 * </ul>
 *
 * <p>
 * Insufficient material is a function of the position alone, so this test builds a history-less board directly from the
 * oracle's recorded FEN (no PGN parse, no replay) and compares the three predicates against the scalachess values for
 * the same final position. Mirrors {@code TestInsufficientMaterialAgainstPythonChessOracle}; scalachess and python-chess
 * agree on all of these positions, so this is an independent witness on a subtle predicate.
 *
 * <p>
 * See {@code tools/scalachess-oracle/generate_insufficient_material_oracle.scala} for the generator and the pinned
 * scalachess version of record.
 */
class TestInsufficientMaterialAgainstScalachessOracle {

  private static final Logger LOGGER = Nulls.getLogger(TestInsufficientMaterialAgainstScalachessOracle.class);

  private static final Path ORACLE_ROOT = Nulls.pathResolve(ConfigurationTestConstants.PROJECT_ROOT_FOLDER_PATH,
      "src/test/resources/oracle/scalachess/insufficient-material");

  private static final ImmutableList<PgnTest> BUCKETS = Nulls.listOf(PgnTest.BASIC_INSUFFICIENT_MATERIAL_BOTH,
      PgnTest.BASIC_INSUFFICIENT_MATERIAL_ONLY_WHITE, PgnTest.BASIC_INSUFFICIENT_MATERIAL_ONLY_BLACK,
      PgnTest.BASIC_INSUFFICIENT_MATERIAL_NONE);

  @SuppressWarnings("static-method")
  @Test
  void insufficientMaterialAgainstScalachessOracle() throws IOException {
    final List<String> failures = new ArrayList<>();
    int totalFixtures = 0;
    final int[] perBucketFixtureCounts = new int[BUCKETS.size()];

    for (int bucketIdx = 0; bucketIdx < BUCKETS.size(); bucketIdx++) {
      final PgnTest bucket = Nulls.get(BUCKETS, bucketIdx);
      final Path jsonlPath = jsonlPathFor(bucket);
      LOGGER.info("Bucket {} -> {}", bucket, jsonlPath);

      final List<InsufficientMaterialRecord> records = InsufficientMaterialJsonlReader.readAll(jsonlPath);
      if (records.isEmpty()) {
        failures.add(bucket + " - oracle file is empty or missing: " + jsonlPath);
        continue;
      }
      perBucketFixtureCounts[bucketIdx] = records.size();

      for (final InsufficientMaterialRecord record : records) {
        totalFixtures++;
        final Board board = new Board(record.fen());

        try {
          assertEquals(record.isInsufficientMaterial(), board.isInsufficientMaterial(),
              () -> bucket + " / " + record.pgn() + " - isInsufficientMaterial mismatch (combined)");
          assertEquals(record.hasInsufficientMaterialWhite(), board.isInsufficientMaterial(Side.WHITE),
              () -> bucket + " / " + record.pgn() + " - isInsufficientMaterial(WHITE) mismatch");
          assertEquals(record.hasInsufficientMaterialBlack(), board.isInsufficientMaterial(Side.BLACK),
              () -> bucket + " / " + record.pgn() + " - isInsufficientMaterial(BLACK) mismatch");
        } catch (final AssertionError e) {
          failures.add(BasicUtility.getMessage(e));
        }
      }
    }

    if (totalFixtures == 0) {
      fail("No fixtures iterated - bucket wiring is broken");
    }

    final StringBuilder summary = new StringBuilder().append("Insufficient-material final-position cross-validation: ");
    for (int i = 0; i < BUCKETS.size(); i++) {
      if (i > 0) {
        summary.append(", ");
      }
      summary.append(Nulls.get(BUCKETS, i).name().replace("BASIC_INSUFFICIENT_MATERIAL_", "")).append('=')
          .append(perBucketFixtureCounts[i]);
    }
    summary.append(" - ").append(totalFixtures).append(" fixtures total; ashlar-chess InsufficientMaterialUtility")
        .append(" vs scalachess 17.15.5");
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
