package com.dlb.chess.test.unwinnability.againstcha;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;

import com.dlb.chess.board.Board;
import com.dlb.chess.board.enums.Side;
import com.dlb.chess.common.Nulls;
import com.dlb.chess.common.exceptions.ProgrammingMistakeException;
import com.dlb.chess.test.ConfigurationTestConstants;
import com.dlb.chess.test.RestrictTestConstants;
import com.dlb.chess.test.common.utility.FileUtility;
import com.dlb.chess.test.model.PgnTestCase;
import com.dlb.chess.test.model.PgnTestCaseList;
import com.dlb.chess.test.pgn.setup.PgnTestCaseCatalog;
import com.dlb.chess.test.pgntest.enums.PgnTest;
import com.dlb.chess.unwinnability.UnwinnabilityQuickVerdict;
import com.dlb.chess.unwinnability.UnwinnableQuickAnalyzer;

class TestAmbronaUnwinnabilityQuickOracleComparison {

  private static final Logger logger = Nulls.getLogger(TestAmbronaUnwinnabilityQuickOracleComparison.class);
  private static final Path ACCEPTED_DIFFERENCE_PATH = Nulls.pathResolve(
      ConfigurationTestConstants.PROJECT_ROOT_FOLDER_PATH,
      "src/test/resources/oracle/ambrona-unwinnability-quick-accepted-differences.tsv");

  @SuppressWarnings("static-method")
  @Test
  void test() {
    final Set<AcceptedDifference> remainingAcceptedDifferenceSet = readAcceptedDifferenceSet();
    final List<String> failureList = new ArrayList<>();

    for (final PgnTest pgnTest : PgnTest.values()) {
      if (!AbstractCheckAgainstCha.isUseTestForCha(pgnTest)) {
        continue;
      }

      if (RestrictTestConstants.IS_RESTRICT_UNWINNABILITY_QUICK_AGAINST_AMBRONA_ORACLE_TEST
          && !AbstractCheckAgainstCha.isUseTestForCha(pgnTest)) {
        continue;
      }

      // the difference between CHA quick implementation and the spec: the CHA quick implementation does not return
      // winnable on forced lines leading to winnable
      // case BASIC_FORCED:
      if (pgnTest == PgnTest.BASIC_FORCED) {
        continue;
      }

      final PgnTestCaseList testCaseList = PgnTestCaseCatalog.getTestList(pgnTest);
      for (final PgnTestCase testCase : testCaseList.list()) {
        logger.info(testCase.pgnName());
        final Board board = testCase.finalPosition();
        logger.info(testCase.pgnName());

        final UnwinnabilityQuickVerdict unwinnableQuickWhite = UnwinnableQuickAnalyzer.unwinnableQuick(board,
            Side.WHITE).verdict();
        check(testCase, Side.WHITE, AmbronaUnwinnabilityOracle.get(testCase.finalFen()).quickWhite(),
            unwinnableQuickWhite, failureList, remainingAcceptedDifferenceSet);

        final UnwinnabilityQuickVerdict unwinnableQuickBlack = UnwinnableQuickAnalyzer.unwinnableQuick(board,
            Side.BLACK).verdict();
        check(testCase, Side.BLACK, AmbronaUnwinnabilityOracle.get(testCase.finalFen()).quickBlack(),
            unwinnableQuickBlack, failureList, remainingAcceptedDifferenceSet);
      }
    }
    for (final AcceptedDifference acceptedDifference : remainingAcceptedDifferenceSet) {
      failureList.add("Accepted difference was not observed: " + acceptedDifference);
    }
    assertTrue(failureList.isEmpty(), Nulls.join("\n", failureList));
  }

  private static void check(PgnTestCase testCase, Side intendedWinner, UnwinnabilityQuickVerdict expected,
      UnwinnabilityQuickVerdict actual, List<String> failureList,
      Set<AcceptedDifference> remainingAcceptedDifferenceSet) {
    if (actual != expected) {
      final var difference = new AcceptedDifference(testCase.pgnName(), intendedWinner, expected, actual,
          testCase.finalFen());
      if (!remainingAcceptedDifferenceSet.remove(difference)) {
        failureList.add(
            testCase.pgnName() + "\t" + intendedWinner + "\t" + expected + "\t" + actual + "\t" + testCase.finalFen());
      }
    }
  }

  private static Set<AcceptedDifference> readAcceptedDifferenceSet() {
    final List<String> lineList = FileUtility.readFileLines(ACCEPTED_DIFFERENCE_PATH);
    if (lineList.isEmpty() || !"pgnName\tside\texpected\tactual\tfen\treason".equals(Nulls.get(lineList, 0))) {
      throw new ProgrammingMistakeException("Unexpected quick unwinnability accepted-differences header");
    }

    final Set<AcceptedDifference> result = new HashSet<>();
    for (var i = 1; i < lineList.size(); i++) {
      final String line = Nulls.get(lineList, i);
      final String[] itemArray = Nulls.split(line, "\t");
      if (itemArray.length != 6) {
        throw new ProgrammingMistakeException("Invalid quick unwinnability accepted-differences row: " + line);
      }
      final var difference = new AcceptedDifference(Nulls.get(itemArray, 0), Side.valueOf(Nulls.get(itemArray, 1)),
          UnwinnabilityQuickVerdict.valueOf(Nulls.get(itemArray, 2)),
          UnwinnabilityQuickVerdict.valueOf(Nulls.get(itemArray, 3)), Nulls.get(itemArray, 4));
      if (!result.add(difference)) {
        throw new ProgrammingMistakeException("Duplicate quick unwinnability accepted-differences row: " + line);
      }
    }
    return result;
  }

  @SuppressWarnings("static-method")
  @Test
  void testStartPosition() {
    final Board board = new Board();
    assertEquals(UnwinnabilityQuickVerdict.POSSIBLY_WINNABLE,
        UnwinnableQuickAnalyzer.unwinnableQuick(board, board.getHavingMove().getOppositeSide()).verdict());
  }

  private record AcceptedDifference(String pgnName, Side side, UnwinnabilityQuickVerdict expected,
      UnwinnabilityQuickVerdict actual, String fen) {
  }
}
