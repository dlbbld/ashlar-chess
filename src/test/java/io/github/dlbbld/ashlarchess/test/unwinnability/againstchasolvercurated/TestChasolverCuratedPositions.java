// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.test.unwinnability.againstchasolvercurated;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.eclipse.jdt.annotation.Nullable;
import org.junit.jupiter.api.Test;

import io.github.dlbbld.ashlarchess.board.Board;
import io.github.dlbbld.ashlarchess.board.enums.Side;
import io.github.dlbbld.ashlarchess.exceptions.ProgrammingMistakeException;
import io.github.dlbbld.ashlarchess.internal.Nulls;
import io.github.dlbbld.ashlarchess.test.ConfigurationTestConstants;
import io.github.dlbbld.ashlarchess.test.common.utility.FileUtility;
import io.github.dlbbld.ashlarchess.test.common.utility.Loggers;
import io.github.dlbbld.ashlarchess.unwinnability.UnwinnabilityFullVerdict;
import io.github.dlbbld.ashlarchess.unwinnability.UnwinnabilityQuickVerdict;
import io.github.dlbbld.ashlarchess.unwinnability.UnwinnableFullAnalyzer;
import io.github.dlbbld.ashlarchess.unwinnability.UnwinnableQuickAnalyzer;

class TestChasolverCuratedPositions {
  private static final Logger logger = Loggers.getLogger(TestChasolverCuratedPositions.class);

  private static final int PROGRESS_LOG_INTERVAL = 100;
  private static final int MAX_PRINTED_FAILURES = 30;
  private static final int MAX_PRINTED_IMPORT_FAILURES = 200;
  private static final Path POSITION_PATH = Nulls.pathResolve(ConfigurationTestConstants.PROJECT_ROOT_FOLDER_PATH,
      "src/test/resources/oracle/chasolver/curated/positions.txt");

  @SuppressWarnings("static-method")
  @Test
  void printImportRejectedPositions() {
    final List<CuratedPosition> positions = readPositions();
    final List<String> importFailures = new ArrayList<>();

    for (final CuratedPosition position : positions) {
      try {
        Board.fromFenLenient(position.fen());
      } catch (final RuntimeException exception) {
        importFailures.add("line " + position.lineNumber() + " " + position.code() + " import failed: "
            + exception.getMessage() + " FEN " + position.fen());
      }
    }

    final List<String> printedFailures = Nulls.subList(importFailures, 0,
        Math.min(MAX_PRINTED_IMPORT_FAILURES, importFailures.size()));
    logger.info("Chasolver curated positions rejected by ashlar FEN import: {}/{}", importFailures.size(),
        positions.size());
    logger.info("Rejected position sample:\n{}", Nulls.join("\n", printedFailures));

    assertTrue(importFailures.size() < positions.size(),
        "the lenient parser must import the large majority of curated positions, but rejected " + importFailures.size()
            + " of " + positions.size());
  }

  @SuppressWarnings("static-method")
  @Test
  void fullAnalyzerMatchesCuratedPositions() {
    final List<CuratedPosition> positions = readPositions();
    final List<String> failures = new ArrayList<>();
    int skippedImportCount = 0;
    int abstainedCount = 0;
    int checkedPositionCount = 0;

    for (final CuratedPosition position : positions) {
      final @Nullable Board board = parseBoardOrNull(position);
      if (board == null) {
        skippedImportCount++;
      } else {
        checkedPositionCount++;
        abstainedCount += checkFull(position, board, Side.WHITE, position.expectedWhite(), failures);
        abstainedCount += checkFull(position, board, Side.BLACK, position.expectedBlack(), failures);
      }

      if ((checkedPositionCount + skippedImportCount) % PROGRESS_LOG_INTERVAL == 0) {
        logger.info("Checked {} chasolver curated positions, skipped imports: {}, abstentions: {}, failures so far: {}",
            checkedPositionCount, skippedImportCount, abstainedCount, failures.size());
      }
    }

    logger.info("Checked {} chasolver curated positions, skipped imports: {}, abstentions: {}, failures: {}",
        checkedPositionCount, skippedImportCount, abstainedCount, failures.size());
    assertTrue(failures.isEmpty(), formatFailureMessage("Full analyzer", checkedPositionCount, failures));
  }

  @SuppressWarnings("static-method")
  @Test
  void quickAnalyzerDoesNotContradictCuratedPositions() {
    final List<CuratedPosition> positions = readPositions();
    final List<String> failures = new ArrayList<>();
    int skippedImportCount = 0;
    int checkedPositionCount = 0;

    for (final CuratedPosition position : positions) {
      final @Nullable Board board = parseBoardOrNull(position);
      if (board == null) {
        skippedImportCount++;
      } else {
        checkedPositionCount++;
        checkQuick(position, board, Side.WHITE, position.expectedWhite(), failures);
        checkQuick(position, board, Side.BLACK, position.expectedBlack(), failures);
      }

      if ((checkedPositionCount + skippedImportCount) % PROGRESS_LOG_INTERVAL == 0) {
        logger.info(
            "Checked quick soundness for {} chasolver curated positions, skipped imports: {}, " + "failures so far: {}",
            checkedPositionCount, skippedImportCount, failures.size());
      }
    }

    logger.info("Checked quick soundness for {} chasolver curated positions, skipped imports: {}, failures: {}",
        checkedPositionCount, skippedImportCount, failures.size());
    assertTrue(failures.isEmpty(), formatFailureMessage("Quick analyzer", checkedPositionCount, failures));
  }

  // Only strictly-legal positions are in scope for the unwinnability question. Strict FEN validation rejects
  // unreachable positions - including impossible check configurations (a double check by two same-coloured bishops,
  // two rooks, two queens, two knights, or three+ checkers) - so positions that fail it are skipped rather than
  // analysed. The half/full-move clocks do not affect the verdict, so a placeholder " 0 1" is appended to satisfy the
  // strict six-field requirement.
  @Nullable
  private static Board parseBoardOrNull(CuratedPosition position) {
    try {
      Board.fromFenStrict(position.fen() + " 0 1");
      return Board.fromFenLenient(position.fen());
    } catch (@SuppressWarnings("unused") final RuntimeException exception) {
      return null;
    }
  }

  private static int checkFull(CuratedPosition position, Board board, Side winner, ExpectedWinnability expected,
      List<String> failures) {
    final UnwinnabilityFullVerdict actual = UnwinnableFullAnalyzer.unwinnableFull(board, winner).verdict();
    final UnwinnabilityFullVerdict expectedVerdict = expected.toFullVerdict();
    if (actual == UnwinnabilityFullVerdict.UNDETERMINED && actual != expectedVerdict) {
      return 1;
    }
    if (actual != expectedVerdict) {
      failures.add("line " + position.lineNumber() + " " + winner + " expected " + expectedVerdict + " actual " + actual
          + " FEN " + position.fen());
    }
    return 0;
  }

  private static void checkQuick(CuratedPosition position, Board board, Side winner, ExpectedWinnability expected,
      List<String> failures) {
    final UnwinnabilityQuickVerdict actual = UnwinnableQuickAnalyzer.unwinnableQuick(board, winner).verdict();
    if (expected == ExpectedWinnability.WINNABLE && actual == UnwinnabilityQuickVerdict.UNWINNABLE) {
      failures.add("line " + position.lineNumber() + " " + winner + " expected not-unwinnable actual " + actual
          + " FEN " + position.fen());
    }
  }

  private static List<CuratedPosition> readPositions() {
    final List<String> lines = FileUtility.readFileLines(POSITION_PATH);
    final List<CuratedPosition> positions = new ArrayList<>();
    for (int i = 0; i < lines.size(); i++) {
      final String line = Nulls.get(lines, i);
      if (line.isBlank() || line.startsWith("#")) {
        continue;
      }
      if (line.length() < 4 || line.charAt(2) != ' ') {
        throw new ProgrammingMistakeException("Invalid chasolver curated position row: " + line);
      }
      positions.add(parsePosition(i + 1, Nulls.substring(line, 0, 2), Nulls.substring(line, 3)));
    }
    if (positions.isEmpty()) {
      throw new ProgrammingMistakeException("The chasolver curated position oracle is empty");
    }
    return positions;
  }

  private static CuratedPosition parsePosition(int lineNumber, String code, String fen) {
    return switch (code) {
      case "WB" -> new CuratedPosition(lineNumber, code, fen, ExpectedWinnability.WINNABLE,
          ExpectedWinnability.WINNABLE);
      case "W-" -> new CuratedPosition(lineNumber, code, fen, ExpectedWinnability.WINNABLE,
          ExpectedWinnability.UNWINNABLE);
      case "-B" -> new CuratedPosition(lineNumber, code, fen, ExpectedWinnability.UNWINNABLE,
          ExpectedWinnability.WINNABLE);
      case "--" -> new CuratedPosition(lineNumber, code, fen, ExpectedWinnability.UNWINNABLE,
          ExpectedWinnability.UNWINNABLE);
      default -> throw new ProgrammingMistakeException("Unexpected chasolver curated position verdict code: " + code);
    };
  }

  private static String formatFailureMessage(String analyzerName, int checkedPositionCount, List<String> failures) {
    final List<String> printedFailures = Nulls.subList(failures, 0, Math.min(MAX_PRINTED_FAILURES, failures.size()));
    return analyzerName + " mismatches for " + failures.size() + " of " + checkedPositionCount
        + " chasolver curated positions:\n" + Nulls.join("\n", printedFailures);
  }

  private enum ExpectedWinnability {
    WINNABLE,
    UNWINNABLE;

    private UnwinnabilityFullVerdict toFullVerdict() {
      return switch (this) {
        case WINNABLE -> UnwinnabilityFullVerdict.WINNABLE;
        case UNWINNABLE -> UnwinnabilityFullVerdict.UNWINNABLE;
      };
    }
  }

  private record CuratedPosition(int lineNumber, String code, String fen, ExpectedWinnability expectedWhite,
      ExpectedWinnability expectedBlack) {
  }
}
