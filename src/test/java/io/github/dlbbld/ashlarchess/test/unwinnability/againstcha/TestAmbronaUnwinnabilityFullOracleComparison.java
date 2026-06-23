// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.test.unwinnability.againstcha;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;

import io.github.dlbbld.ashlarchess.board.Board;
import io.github.dlbbld.ashlarchess.board.enums.Side;
import io.github.dlbbld.ashlarchess.exceptions.ProgrammingMistakeException;
import io.github.dlbbld.ashlarchess.internal.Nulls;
import io.github.dlbbld.ashlarchess.test.ConfigurationTestConstants;
import io.github.dlbbld.ashlarchess.test.common.utility.FileUtility;
import io.github.dlbbld.ashlarchess.test.model.PgnFen;
import io.github.dlbbld.ashlarchess.test.model.PgnTestCaseList;
import io.github.dlbbld.ashlarchess.test.pgn.setup.PgnTestCaseCatalog;
import io.github.dlbbld.ashlarchess.test.pgntest.enums.PgnTest;
import io.github.dlbbld.ashlarchess.unwinnability.UnwinnabilityFullVerdict;
import io.github.dlbbld.ashlarchess.unwinnability.UnwinnableFullAnalyzer;

class TestAmbronaUnwinnabilityFullOracleComparison {

  private static final Logger logger = LogManager.getLogger(TestAmbronaUnwinnabilityFullOracleComparison.class);

  private static final int PROGRESS_LOG_INTERVAL = 25;
  private static final int MAX_PRINTED_FAILURES = 20;
  private static final Path ACCEPTED_DIFFERENCE_PATH = Nulls.pathResolve(
      ConfigurationTestConstants.PROJECT_ROOT_FOLDER_PATH,
      "src/test/resources/oracle/ambrona-unwinnability-full-accepted-differences.tsv");

  @SuppressWarnings("static-method")
  @Test
  void chaPositionsExceptLichessHelpmatesMatchFullOracle() {
    final Set<AcceptedDifference> remainingAcceptedDifferenceSet = readAcceptedDifferenceSet();
    final List<String> failures = new ArrayList<>();
    int checkedPositionCount = 0;

    for (final PgnTest pgnTest : PgnTest.values()) {
      if (!CheckAgainstCha.isUseTestForCha(pgnTest)) {
        continue;
      }

      final PgnTestCaseList testCaseList = PgnTestCaseCatalog.getTestList(pgnTest);
      for (final PgnFen testCase : testCaseList.list()) {
        logger.info(testCase.pgnName());
        checkedPositionCount++;
        check(testCase, Side.WHITE, AmbronaUnwinnabilityOracle.get(testCase.finalFen()).fullWhite(), failures,
            remainingAcceptedDifferenceSet);
        check(testCase, Side.BLACK, AmbronaUnwinnabilityOracle.get(testCase.finalFen()).fullBlack(), failures,
            remainingAcceptedDifferenceSet);

        if (checkedPositionCount % PROGRESS_LOG_INTERVAL == 0) {
          logger.info("Checked {} CHA positions, failures so far: {}", checkedPositionCount, failures.size());
        }
      }
    }

    logger.info("Checked {} CHA positions, failures: {}", checkedPositionCount, failures.size());
    for (final AcceptedDifference acceptedDifference : remainingAcceptedDifferenceSet) {
      failures.add("Accepted difference was not observed: " + acceptedDifference);
    }
    assertTrue(failures.isEmpty(), formatFailureMessage(checkedPositionCount, failures));
  }

  private static void check(PgnFen testCase, Side intendedWinner, UnwinnabilityFullVerdict expected,
      List<String> failures, Set<AcceptedDifference> remainingAcceptedDifferenceSet) {
    final Board board = testCase.finalPosition();
    final UnwinnabilityFullVerdict actual = UnwinnableFullAnalyzer.unwinnableFull(board, intendedWinner).verdict();
    if (!isSameVerdict(actual, expected)) {
      final AcceptedDifference difference = new AcceptedDifference(testCase.pgnName(), intendedWinner, expected, actual,
          testCase.finalFen());
      if (!remainingAcceptedDifferenceSet.remove(difference)) {
        failures.add(testCase.pgnName() + " " + intendedWinner + " expected " + expected + " actual " + actual + " FEN "
            + testCase.finalFen());
      }
    }
  }

  // CHA does not distinguish the WINNABLE_HELPMATE / WINNABLE_BY_THEOREM split, so a winnable oracle verdict matches
  // either of ours.
  private static boolean isSameVerdict(UnwinnabilityFullVerdict actual, UnwinnabilityFullVerdict expected) {
    if (actual == expected) {
      return true;
    }
    final boolean actualWinnable = actual == UnwinnabilityFullVerdict.WINNABLE_HELPMATE
        || actual == UnwinnabilityFullVerdict.WINNABLE_BY_THEOREM;
    final boolean expectedWinnable = expected == UnwinnabilityFullVerdict.WINNABLE_HELPMATE
        || expected == UnwinnabilityFullVerdict.WINNABLE_BY_THEOREM;
    return actualWinnable && expectedWinnable;
  }

  private static Set<AcceptedDifference> readAcceptedDifferenceSet() {
    final List<String> lines = FileUtility.readFileLines(ACCEPTED_DIFFERENCE_PATH);
    if (lines.isEmpty() || !"pgnName\tside\texpected\tactual\tfen\treason".equals(Nulls.get(lines, 0))) {
      throw new ProgrammingMistakeException("Unexpected full unwinnability accepted-differences header");
    }

    final Set<AcceptedDifference> result = new HashSet<>();
    for (int i = 1; i < lines.size(); i++) {
      final String line = Nulls.get(lines, i);
      final String[] itemArray = Nulls.split(line, "\t");
      if (itemArray.length != 6) {
        throw new ProgrammingMistakeException("Invalid full unwinnability accepted-differences row: " + line);
      }
      final AcceptedDifference difference = new AcceptedDifference(Nulls.get(itemArray, 0),
          Side.valueOf(Nulls.get(itemArray, 1)), UnwinnabilityFullVerdict.valueOf(Nulls.get(itemArray, 2)),
          UnwinnabilityFullVerdict.valueOf(Nulls.get(itemArray, 3)), Nulls.get(itemArray, 4));
      if (!result.add(difference)) {
        throw new ProgrammingMistakeException("Duplicate full unwinnability accepted-differences row: " + line);
      }
    }
    return result;
  }

  private static String formatFailureMessage(int checkedPositionCount, List<String> failures) {
    final List<String> printedFailures = Nulls.subList(failures, 0, Math.min(MAX_PRINTED_FAILURES, failures.size()));
    return "Full unwinnability oracle mismatches for " + failures.size() + " of " + checkedPositionCount
        + " CHA positions:\n" + Nulls.join("\n", printedFailures);
  }

  private record AcceptedDifference(String pgnName, Side side, UnwinnabilityFullVerdict expected,
      UnwinnabilityFullVerdict actual, String fen) {
  }
}
