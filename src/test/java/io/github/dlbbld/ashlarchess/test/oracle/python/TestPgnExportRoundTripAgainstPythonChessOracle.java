// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.test.oracle.python;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.eclipse.jdt.annotation.NonNull;
import org.junit.jupiter.api.Test;

import com.google.common.collect.ImmutableList;

import io.github.dlbbld.ashlarchess.board.Board;
import io.github.dlbbld.ashlarchess.common.Nulls;
import io.github.dlbbld.ashlarchess.common.ucimove.utility.UciMoveUtility;
import io.github.dlbbld.ashlarchess.common.utility.BasicUtility;
import io.github.dlbbld.ashlarchess.model.LegalMove;
import io.github.dlbbld.ashlarchess.model.PgnMove;
import io.github.dlbbld.ashlarchess.model.UciMove;
import io.github.dlbbld.ashlarchess.pgn.PgnCreate;
import io.github.dlbbld.ashlarchess.pgn.PgnGame;
import io.github.dlbbld.ashlarchess.pgn.StrictPgnParser;
import io.github.dlbbld.ashlarchess.pgn.WriteMode;
import io.github.dlbbld.ashlarchess.test.ConfigurationTestConstants;
import io.github.dlbbld.ashlarchess.test.pgntest.constants.PgnTestConstants;
import io.github.dlbbld.ashlarchess.test.pgntest.enums.PgnTest;

/**
 * Cross-validates that ashlar-chess's PGN exporter preserves semantic content under round-trip, using the python-chess
 * PGN-import oracle (slice 2 onward) as the ground-truth reference.
 *
 * <p>
 * For each fixture: parses the original PGN with ashlar-chess, plays it through to extract the played UCI sequence, then
 * emits the parsed game in each {@link WriteMode} ({@code SEMANTIC} and {@code ARCHIVAL}), re-parses the emitted string
 * with ashlar-chess, and asserts that the re-played UCI sequence and bounding FENs (start and final) still match what
 * python-chess sees on the source PGN. The python-chess oracle data lives in
 * {@code src/test/resources/oracle/python-chess/<folderPart>.jsonl}; this test does not introduce a new oracle file.
 *
 * <p>
 * Semantic round-trip equivalence (the user's framing): python-chess, if it could re-parse ashlar-chess's emitted PGN,
 * would see the same move UCI sequence, the same starting position, and the same ending position as it does on the
 * source. We assert this transitively: the import oracle establishes that ashlar-chess's parse of the source agrees with
 * python-chess (slice 2); this slice extends that to "and ashlar-chess's emit round-trips to the same UCI / FEN sequence
 * under both modes." If both conditions hold, python-chess's parse of either the source or the round-tripped artifact
 * would yield the same content.
 *
 * <p>
 * What this test does <em>not</em> assert: byte equality between source PGN and emitted PGN (would fail trivially on
 * whitespace, line wrapping, archival normalisation), nor selected-header equivalence (a separate slice). Termination
 * marker survival is not asserted here either since the import oracle does not currently carry the source result tag
 * value.
 *
 * <p>
 * Bucket coverage matches {@link TestPgnImportAgainstPythonChessOracle}: PARSER_FROM_FEN + BASIC_* + real-games /
 * Wikipedia / WCC. CHA_* and edge-cases are intentionally skipped per the release plan.
 */
class TestPgnExportRoundTripAgainstPythonChessOracle {

  private static final Logger LOGGER = Nulls.getLogger(TestPgnExportRoundTripAgainstPythonChessOracle.class);

  private static final Path ORACLE_ROOT = Nulls.pathResolve(ConfigurationTestConstants.PROJECT_ROOT_FOLDER_PATH,
      "src/test/resources/oracle/python-chess");

  private static final ImmutableList<PgnTest> BUCKETS = Nulls.listOf(PgnTest.PARSER_FROM_FEN,
      PgnTest.BASIC_MOVING_PIECE_WHITE, PgnTest.BASIC_MOVING_PIECE_BLACK, PgnTest.BASIC_CAPTURE_WHITE,
      PgnTest.BASIC_CAPTURE_BLACK, PgnTest.BASIC_CAPTURE_LAST_MOVE, PgnTest.BASIC_EN_PASSANT_CAPTURE_WHITE,
      PgnTest.BASIC_EN_PASSANT_CAPTURE_BLACK, PgnTest.BASIC_PROMOTION_PIECE_WHITE, PgnTest.BASIC_PROMOTION_PIECE_BLACK,
      PgnTest.BASIC_PROMOTION_SQUARE_WHITE, PgnTest.BASIC_PROMOTION_SQUARE_BLACK, PgnTest.BASIC_CHECK_WHITE,
      PgnTest.BASIC_CHECK_BLACK, PgnTest.BASIC_CHECKMATE_WHITE, PgnTest.BASIC_CHECKMATE_BLACK,
      PgnTest.BASIC_CHECKMATE_VARIOUS_WHITE, PgnTest.BASIC_CHECKMATE_VARIOUS_BLACK, PgnTest.BASIC_DOUBLE_CHECK_WHITE,
      PgnTest.BASIC_DOUBLE_CHECK_BLACK, PgnTest.BASIC_CHECKMATE_DOUBLE_CHECK_WHITE,
      PgnTest.BASIC_CHECKMATE_DOUBLE_CHECK_BLACK, PgnTest.BASIC_STALEMATE, PgnTest.BASIC_INSUFFICIENT_MATERIAL_BOTH,
      PgnTest.BASIC_INSUFFICIENT_MATERIAL_ONLY_WHITE, PgnTest.BASIC_INSUFFICIENT_MATERIAL_ONLY_BLACK,
      PgnTest.BASIC_INSUFFICIENT_MATERIAL_NONE, PgnTest.BASIC_THREEFOLD, PgnTest.BASIC_FIFTY, PgnTest.BASIC_FIVEFOLD,
      PgnTest.BASIC_SEVENTY_FIVE, PgnTest.BASIC_INTERVENING, PgnTest.BASIC_DOUBLE_DRAW, PgnTest.BASIC_CASTLING_WHITE,
      PgnTest.BASIC_CASTLING_BLACK, PgnTest.BASIC_CASTLING_SPECIAL_WHITE, PgnTest.BASIC_CASTLING_SPECIAL_BLACK,
      PgnTest.BASIC_FORCED, PgnTest.BASIC_REPORT_NO_PROGRESS_SEQUENCES_WHITE,
      PgnTest.BASIC_REPORT_NO_PROGRESS_SEQUENCES_BLACK, PgnTest.BASIC_REPORT_REPETITION,
      PgnTest.BASIC_REPORT_MAX_NO_PROGRESS, PgnTest.VARIOUS, PgnTest.WCC2021, PgnTest.FIVEFOLD_CORRECT,
      PgnTest.FIFTY_GENERAL, PgnTest.FIFTY_PATTERN, PgnTest.SEVENTY_FIVE_CORRECT, PgnTest.EARLY_DRAW,
      PgnTest.WIKIPEDIA_THREEFOLD, PgnTest.WIKIPEDIA_FIFTY_MOVE);

  @SuppressWarnings("static-method")
  @Test
  void pgnExportRoundTripAgainstPythonChessOracle() throws IOException {
    final @NonNull List<String> failures = new ArrayList<>();
    int totalFixtures = 0;

    for (final PgnTest bucket : BUCKETS) {
      final Path jsonlPath = jsonlPathFor(bucket);
      LOGGER.info("Bucket {} -> {}", bucket, jsonlPath);

      final List<OracleRecord> records = OracleJsonlReader.readAll(jsonlPath);
      if (records.isEmpty()) {
        failures.add(bucket + " - oracle file is empty or missing: " + jsonlPath);
        continue;
      }

      final Path folderPath = bucket.getFolderPath();
      for (final OracleRecord record : records) {
        totalFixtures++;
        final @NonNull List<String> expectedUcis = new ArrayList<>();
        for (final OracleMove move : record.moves()) {
          expectedUcis.add(move.uci());
        }

        final PgnGame original = StrictPgnParser.parse(folderPath, record.pgn());
        verify(record, expectedUcis, original, "original-parse", bucket, failures);

        final String semantic = PgnCreate.createPgnString(original, WriteMode.SEMANTIC);
        try {
          final PgnGame semanticReparsed = StrictPgnParser.parseText(semantic);
          verify(record, expectedUcis, semanticReparsed, "semantic-round-trip", bucket, failures);
        } catch (final RuntimeException e) {
          failures.add(bucket + " / " + record.pgn() + " - semantic round-trip threw: " + e.getMessage());
        }

        final String archival = PgnCreate.createPgnString(original, WriteMode.ARCHIVAL);
        try {
          final PgnGame archivalReparsed = StrictPgnParser.parseText(archival);
          verify(record, expectedUcis, archivalReparsed, "archival-round-trip", bucket, failures);
        } catch (final RuntimeException e) {
          failures.add(bucket + " / " + record.pgn() + " - archival round-trip threw: " + e.getMessage());
        }
      }
    }

    if (totalFixtures == 0) {
      fail("No fixtures iterated - bucket wiring is broken");
    }
    LOGGER.info("Round-trip cross-validated {} fixtures x 2 modes across {} buckets", totalFixtures, BUCKETS.size());

    if (!failures.isEmpty()) {
      final StringBuilder report = new StringBuilder();
      report.append(failures.size()).append(" PGN-export round-trip disagreement(s) across ").append(totalFixtures)
          .append(" fixtures in ").append(BUCKETS.size()).append(" buckets:\n");
      for (final String f : failures) {
        report.append("  ").append(f).append('\n');
      }
      fail(Nulls.toString(report));
    }
  }

  private static void verify(OracleRecord record, List<String> expectedUcis, PgnGame parsed, String stage,
      PgnTest bucket, List<String> failures) {
    final String label = bucket + " / " + record.pgn() + " [" + stage + "]";

    try {
      assertEquals(record.startFen(), parsed.startFen().fen(), () -> label + " - startFen mismatch");
      assertEquals(expectedUcis.size(), parsed.moveList().size(), () -> label + " - half-move count mismatch");
    } catch (final AssertionError e) {
      failures.add(BasicUtility.getMessage(e));
      return;
    }

    final Board board = new Board(parsed.startFen());
    final List<String> actualUcis = new ArrayList<>(parsed.moveList().size());
    for (final PgnMove halfMove : parsed.moveList()) {
      board.moveStrict(halfMove.san());
      final LegalMove last = board.getLastMove();
      final UciMove uci = UciMoveUtility.convertMoveSpecificationToUci(last.havingMove(), last.moveSpecification());
      actualUcis.add(uci.text());
    }

    try {
      assertEquals(expectedUcis, actualUcis, () -> label + " - played UCI sequence mismatch");
      assertEquals(record.finalFen(), board.getFen(), () -> label + " - finalFen mismatch");
    } catch (final AssertionError e) {
      failures.add(BasicUtility.getMessage(e));
    }
  }

  private static Path jsonlPathFor(PgnTest bucket) {
    final Path pgnRoot = PgnTestConstants.PGN_TEST_ROOT_FOLDER_PATH;
    final Path bucketPath = bucket.getFolderPath();
    final String relative = Nulls.replace(Nulls.toString(Nulls.pathRelativize(pgnRoot, bucketPath)), '\\', '/');
    return Nulls.pathResolve(ORACLE_ROOT, relative + ".jsonl");
  }
}
