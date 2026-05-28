package io.github.dlbbld.ashlarchess.test.unwinnability;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;

import io.github.dlbbld.ashlarchess.board.Board;
import io.github.dlbbld.ashlarchess.board.enums.Side;
import io.github.dlbbld.ashlarchess.common.Nulls;
import io.github.dlbbld.ashlarchess.test.RestrictTestConstants;
import io.github.dlbbld.ashlarchess.test.model.PgnFen;
import io.github.dlbbld.ashlarchess.test.model.PgnTestCaseList;
import io.github.dlbbld.ashlarchess.test.pgn.setup.PgnTestCaseCatalog;
import io.github.dlbbld.ashlarchess.test.pgntest.enums.PgnTest;
import io.github.dlbbld.ashlarchess.test.unwinnability.oracle.LimitedUnwinnabilityOracle;
import io.github.dlbbld.ashlarchess.test.unwinnability.oracle.enums.LimitedUnwinnabilityVerdict;
import io.github.dlbbld.ashlarchess.unwinnability.UnwinnabilityQuickVerdict;
import io.github.dlbbld.ashlarchess.unwinnability.UnwinnableQuickAnalyzer;

class TestUnwinnabilityQuickAgainstLimitedOracle {

  private static final Logger logger = Nulls.getLogger(TestUnwinnabilityQuickAgainstLimitedOracle.class);

  @SuppressWarnings("static-method")
  @Test
  void test() throws Exception {

    for (final PgnTest pgnTest : PgnTest.values()) {
      final PgnTestCaseList testCaseList = PgnTestCaseCatalog.getTestList(pgnTest);
      for (final PgnFen testCase : testCaseList.list()) {
        if (RestrictTestConstants.IS_RESTRICT_PGN_UNWINNABILITY_QUICK_AGAINST_LIMITED_ORACLE_TEST) {
          switch (testCaseList.pgnTest()) {
            case CHA_LICHESS_QUICK_DEPTH_ABOVE_FOUR:
            case CHA_LICHESS_QUICK_DEPTH_THREE:
            case CHA_LICHESS_QUICK_DEPTH_FOUR:
            case CHA_AMBRONA:
              break;
            // $CASES-OMITTED$
            default:
              continue;
          }
        }

        switch (testCase.pgnName()) {
          // here my tool sees unwinnability but not the quick analysis
          case "ambrona_10.pgn":
          case "ambrona_16.pgn":
          case "pawn_wall_norgaard_additional_own_pawns_not_marched_up_with_opponent_pawns_between.pgn":
            continue;
          default:
            break;
        }

        final Board board = testCase.finalPosition();

        logger.info(testCase.pgnName());

        final LimitedUnwinnabilityVerdict verdictWhite = LimitedUnwinnabilityOracle.calculateUnwinnability(board,
            Side.WHITE);
        final UnwinnabilityQuickVerdict unwinnableQuickWhite = UnwinnableQuickAnalyzer
            .unwinnableQuick(board, Side.WHITE).verdict();
        check(verdictWhite, unwinnableQuickWhite);

        final LimitedUnwinnabilityVerdict verdictBlack = LimitedUnwinnabilityOracle.calculateUnwinnability(board,
            Side.BLACK);
        final UnwinnabilityQuickVerdict unwinnableQuickBlack = UnwinnableQuickAnalyzer
            .unwinnableQuick(board, Side.BLACK).verdict();

        check(verdictBlack, unwinnableQuickBlack);
      }
    }
  }

  private static void check(LimitedUnwinnabilityVerdict verdict, UnwinnabilityQuickVerdict unwinnableQuick) {
    switch (verdict) {
      case UNWINNABLE:
        assertEquals(UnwinnabilityQuickVerdict.UNWINNABLE, unwinnableQuick);
        break;
      case WINNABLE:
        assertNotEquals(UnwinnabilityQuickVerdict.UNWINNABLE, unwinnableQuick);
        break;
      case UNKNOWN:
        break;
      default:
        throw new IllegalArgumentException();
    }

    switch (unwinnableQuick) {
      case WINNABLE:
        assertNotEquals(LimitedUnwinnabilityVerdict.UNWINNABLE, verdict);
        break;
      case UNWINNABLE:
        assertNotEquals(LimitedUnwinnabilityVerdict.WINNABLE, verdict);
        break;
      case POSSIBLY_WINNABLE:
        break;
      default:
        throw new IllegalArgumentException();
    }
  }

}
