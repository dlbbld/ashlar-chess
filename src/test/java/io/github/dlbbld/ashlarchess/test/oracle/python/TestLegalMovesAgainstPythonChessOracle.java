// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.test.oracle.python;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;

import com.google.common.collect.ImmutableList;

import io.github.dlbbld.ashlarchess.board.Board;
import io.github.dlbbld.ashlarchess.common.Nulls;
import io.github.dlbbld.ashlarchess.common.utility.BasicUtility;
import io.github.dlbbld.ashlarchess.model.PgnMove;
import io.github.dlbbld.ashlarchess.pgn.PgnGame;
import io.github.dlbbld.ashlarchess.pgn.StrictPgnParser;
import io.github.dlbbld.ashlarchess.test.ConfigurationTestConstants;
import io.github.dlbbld.ashlarchess.test.pgntest.constants.PgnTestConstants;
import io.github.dlbbld.ashlarchess.test.pgntest.enums.PgnTest;

/**
 * Cross-validates ashlar-chess's legal-move generator against python-chess's by replaying each PGN in the covered
 * move-rule-mechanics buckets and asserting set-equality on the legal-move set at every visited position.
 *
 * <p>
 * For each fixture: parses the PGN, walks the board through the played sequence, and at each ply (including ply 0
 * before any move and the position after the final move) asserts that the sorted UCI set of
 * {@code board.getLegalMovesUci()} equals the python-chess set recorded in the JSONL oracle under
 * {@code src/test/resources/oracle/python-chess/move-gen/<folderPart>.jsonl}.
 *
 * <p>
 * This is a separate oracle from {@link TestPgnImportAgainstPythonChessOracle} - that test answers "did we import this
 * PGN the same way as python-chess?", this one answers "does our legal-move generator match at every position?". Both
 * questions matter; merging them would conflate the import surface and the move-generation surface.
 *
 * <p>
 * Bucket coverage is intentionally narrower than the PGN-import oracle: move-rule-mechanics buckets only (piece moves,
 * captures, en passant, promotion, check, double check, castling, plus parserFenMechanics for the unusual FEN-load
 * positions). Long-game corpora are excluded to keep the JSONL file sizes proportional to the value delivered - move
 * generation is already heavily exercised by the internal differential-test layer against the relocated
 * {@code StaticPosition} oracle.
 *
 * <p>
 * See {@code src/test/python/generate_move_gen_oracle.py} module docstring for the schema source of truth, the
 * reproducibility install command, and the version of python-chess that produced the committed oracle.
 */
class TestLegalMovesAgainstPythonChessOracle {

  private static final Logger LOGGER = Nulls.getLogger(TestLegalMovesAgainstPythonChessOracle.class);

  private static final Path ORACLE_ROOT = Nulls.pathResolve(ConfigurationTestConstants.PROJECT_ROOT_FOLDER_PATH,
      "src/test/resources/oracle/python-chess/move-gen");

  private static final ImmutableList<PgnTest> BUCKETS = Nulls.listOf(PgnTest.PARSER_FROM_FEN,
      PgnTest.BASIC_MOVING_PIECE_WHITE, PgnTest.BASIC_MOVING_PIECE_BLACK, PgnTest.BASIC_CAPTURE_WHITE,
      PgnTest.BASIC_CAPTURE_BLACK, PgnTest.BASIC_CAPTURE_LAST_MOVE, PgnTest.BASIC_EN_PASSANT_CAPTURE_WHITE,
      PgnTest.BASIC_EN_PASSANT_CAPTURE_BLACK, PgnTest.BASIC_PROMOTION_PIECE_WHITE, PgnTest.BASIC_PROMOTION_PIECE_BLACK,
      PgnTest.BASIC_PROMOTION_SQUARE_WHITE, PgnTest.BASIC_PROMOTION_SQUARE_BLACK, PgnTest.BASIC_CHECK_WHITE,
      PgnTest.BASIC_CHECK_BLACK, PgnTest.BASIC_DOUBLE_CHECK_WHITE, PgnTest.BASIC_DOUBLE_CHECK_BLACK,
      PgnTest.BASIC_CHECKMATE_DOUBLE_CHECK_WHITE, PgnTest.BASIC_CHECKMATE_DOUBLE_CHECK_BLACK,
      PgnTest.BASIC_CASTLING_WHITE, PgnTest.BASIC_CASTLING_BLACK, PgnTest.BASIC_CASTLING_SPECIAL_WHITE,
      PgnTest.BASIC_CASTLING_SPECIAL_BLACK);

  @SuppressWarnings("static-method")
  @Test
  void legalMovesAgainstPythonChessOracle() throws IOException {
    final List<String> failures = new ArrayList<>();
    int totalFixtures = 0;
    int totalPositions = 0;

    for (final PgnTest bucket : BUCKETS) {
      final Path jsonlPath = jsonlPathFor(bucket);
      LOGGER.info("Bucket {} -> {}", bucket, jsonlPath);

      final List<LegalMovesRecord> records = LegalMovesJsonlReader.readAll(jsonlPath);
      if (records.isEmpty()) {
        failures.add(bucket + " - oracle file is empty or missing: " + jsonlPath);
        continue;
      }

      final Path folderPath = bucket.getFolderPath();
      for (final LegalMovesRecord record : records) {
        totalFixtures++;
        final PgnGame pgnGame = StrictPgnParser.parse(folderPath, record.pgn());
        final int plyCount = pgnGame.moveList().size();

        try {
          assertEquals(plyCount + 1, record.perPly().size(),
              () -> bucket + " / " + record.pgn() + " - perPly length should be plyCount + 1");
        } catch (final AssertionError e) {
          failures.add(BasicUtility.getMessage(e));
          continue;
        }

        final Board board = new Board(pgnGame.startFen());
        for (int ply = 0; ply <= plyCount; ply++) {
          totalPositions++;
          final LegalMovesPly expectedPly = Nulls.get(record.perPly(), ply);
          final List<String> actualSorted = sortedCopy(board.getLegalMovesUci());

          final int positionLabel = ply;
          try {
            assertEquals(expectedPly.legalMovesUci(), actualSorted, () -> bucket + " / " + record.pgn() + " position "
                + positionLabel + " - legal-move set mismatch (ashlar-chess vs python-chess)");
          } catch (final AssertionError e) {
            failures.add(BasicUtility.getMessage(e));
          }

          if (ply < plyCount) {
            final PgnMove move = Nulls.get(pgnGame.moveList(), ply);
            board.moveStrict(move.san());
          }
        }
      }
    }

    if (totalFixtures == 0) {
      fail("No fixtures iterated - bucket wiring is broken");
    }
    LOGGER.info("Cross-validated legal moves at {} positions across {} fixtures in {} buckets", totalPositions,
        totalFixtures, BUCKETS.size());

    if (!failures.isEmpty()) {
      final StringBuilder report = new StringBuilder();
      report.append(failures.size()).append(" legal-move set disagreement(s) across ").append(totalFixtures)
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

  private static List<String> sortedCopy(List<String> list) {
    final List<String> copy = new ArrayList<>(list);
    Collections.sort(copy);
    return copy;
  }
}
