// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.test.unwinnability.againstchasolver;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;

import io.github.dlbbld.ashlarchess.board.Board;
import io.github.dlbbld.ashlarchess.board.enums.Side;
import io.github.dlbbld.ashlarchess.exceptions.ProgrammingMistakeException;
import io.github.dlbbld.ashlarchess.internal.Nulls;
import io.github.dlbbld.ashlarchess.test.ConfigurationTestConstants;
import io.github.dlbbld.ashlarchess.test.common.utility.FileUtility;
import io.github.dlbbld.ashlarchess.test.common.utility.Loggers;
import io.github.dlbbld.ashlarchess.test.model.PgnFen;
import io.github.dlbbld.ashlarchess.test.model.PgnTestCaseList;
import io.github.dlbbld.ashlarchess.test.pgn.setup.PgnTestCaseCatalog;
import io.github.dlbbld.ashlarchess.test.pgntest.enums.PgnTest;
import io.github.dlbbld.ashlarchess.test.unwinnability.againstcha.CheckAgainstCha;
import io.github.dlbbld.ashlarchess.unwinnability.UnwinnabilityQuickVerdict;
import io.github.dlbbld.ashlarchess.unwinnability.UnwinnableQuickAnalyzer;

/**
 * Compares ashlar's quick unwinnability verdict against Miguel Ambrona's Rust {@code chasolver} oracle (the
 * {@code is_unwinnable_fast} fast check). Analogue of
 * {@link io.github.dlbbld.ashlarchess.test.unwinnability.againstcha.TestAmbronaUnwinnabilityQuickOracleComparison} for
 * the cha (C++) oracle.
 *
 * <p>Unlike cha's three-valued {@code quick_analysis}, chasolver's {@code is_unwinnable_fast} is two-valued (it never
 * claims winnability), so it maps exactly onto ashlar's two-valued {@link UnwinnabilityQuickVerdict}. The cha test's
 * {@code BASIC_FORCED} skip is therefore not needed here.
 */
class TestChasolverUnwinnabilityQuickOracleComparison {
  private static final Logger logger = Loggers.getLogger(TestChasolverUnwinnabilityQuickOracleComparison.class);
  private static final Path ACCEPTED_DIFFERENCE_PATH = Nulls.pathResolve(
      ConfigurationTestConstants.PROJECT_ROOT_FOLDER_PATH,
      "src/test/resources/oracle/chasolver-unwinnability-quick-accepted-differences.tsv");

  @SuppressWarnings("static-method")
  @Test
  void chaPositionsMatchQuickChasolverOracle() {
    final Set<AcceptedDifference> remainingAcceptedDifferenceSet = readAcceptedDifferenceSet();
    final List<String> failures = new ArrayList<>();

    for (final PgnTest pgnTest : PgnTest.values()) {
      if (!CheckAgainstCha.isUseTestForCha(pgnTest)) {
        continue;
      }

      final PgnTestCaseList testCaseList = PgnTestCaseCatalog.getTestList(pgnTest);
      for (final PgnFen testCase : testCaseList.list()) {
        logger.info(testCase.pgnName());
        final Board board = testCase.finalPosition();

        final UnwinnabilityQuickVerdict unwinnableQuickWhite = UnwinnableQuickAnalyzer
            .unwinnableQuick(board, Side.WHITE).verdict();
        check(testCase, Side.WHITE, ChasolverUnwinnabilityOracle.get(testCase.finalFen()).quickWhite(),
            unwinnableQuickWhite, failures, remainingAcceptedDifferenceSet);

        final UnwinnabilityQuickVerdict unwinnableQuickBlack = UnwinnableQuickAnalyzer
            .unwinnableQuick(board, Side.BLACK).verdict();
        check(testCase, Side.BLACK, ChasolverUnwinnabilityOracle.get(testCase.finalFen()).quickBlack(),
            unwinnableQuickBlack, failures, remainingAcceptedDifferenceSet);
      }
    }
    for (final AcceptedDifference acceptedDifference : remainingAcceptedDifferenceSet) {
      failures.add("Accepted difference was not observed: " + acceptedDifference);
    }
    assertTrue(failures.isEmpty(), Nulls.join("\n", failures));
  }

  private static void check(PgnFen testCase, Side intendedWinner, UnwinnabilityQuickVerdict expected,
      UnwinnabilityQuickVerdict actual, List<String> failures, Set<AcceptedDifference> remainingAcceptedDifferenceSet) {
    if (actual != expected) {
      final AcceptedDifference difference = new AcceptedDifference(testCase.pgnName(), intendedWinner, expected, actual,
          testCase.finalFen());
      if (!remainingAcceptedDifferenceSet.remove(difference)) {
        failures.add(
            testCase.pgnName() + "\t" + intendedWinner + "\t" + expected + "\t" + actual + "\t" + testCase.finalFen());
      }
    }
  }

  private static Set<AcceptedDifference> readAcceptedDifferenceSet() {
    final List<String> lines = FileUtility.readFileLines(ACCEPTED_DIFFERENCE_PATH);
    if (lines.isEmpty() || !"pgnName\tside\texpected\tactual\tfen\treason".equals(Nulls.get(lines, 0))) {
      throw new ProgrammingMistakeException("Unexpected quick chasolver unwinnability accepted-differences header");
    }

    final Set<AcceptedDifference> result = new HashSet<>();
    for (int i = 1; i < lines.size(); i++) {
      final String line = Nulls.get(lines, i);
      final String[] itemArray = Nulls.split(line, "\t");
      if (itemArray.length != 6) {
        throw new ProgrammingMistakeException("Invalid quick chasolver unwinnability accepted-differences row: " + line);
      }
      final AcceptedDifference difference = new AcceptedDifference(Nulls.get(itemArray, 0),
          Side.valueOf(Nulls.get(itemArray, 1)), UnwinnabilityQuickVerdict.valueOf(Nulls.get(itemArray, 2)),
          UnwinnabilityQuickVerdict.valueOf(Nulls.get(itemArray, 3)), Nulls.get(itemArray, 4));
      if (!result.add(difference)) {
        throw new ProgrammingMistakeException("Duplicate quick chasolver unwinnability accepted-differences row: " + line);
      }
    }
    return result;
  }

  @SuppressWarnings("static-method")
  @Test
  void testStartPosition() {
    final Board board = new Board();
    assertEquals(UnwinnabilityQuickVerdict.POSSIBLY_WINNABLE,
        UnwinnableQuickAnalyzer.unwinnableQuick(board, board.getSideToMove().getOppositeSide()).verdict());
  }

  private record AcceptedDifference(String pgnName, Side side, UnwinnabilityQuickVerdict expected,
      UnwinnabilityQuickVerdict actual, String fen) {
  }
}
