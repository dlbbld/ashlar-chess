package com.dlb.chess.test.unwinnability.againstcha;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.dlb.chess.test.RestrictTestConstants;
import com.dlb.chess.unwinnability.CompareAmbronaSemiStaticOracle;

class TestAmbronaSemiStaticOracleComparison {

  /** Cap on FENs processed when the smoke restriction is active. Full mode compares all 1249. */
  private static final int MAX_FENS_IN_SMOKE_MODE = 10;

  /** FEN count in the oracle file. Asserted as the expected comparedFenCount in full mode. */
  private static final int TOTAL_FENS_IN_ORACLE = 1249;

  @SuppressWarnings("static-method")
  @Test
  void currentSemiStaticComparisonMatchesKnownBaseline() throws Exception {
    final int maxFens = RestrictTestConstants.IS_RESTRICT_AMBRONA_SEMISTATIC_ORACLE_COMPARISON_TEST
        ? MAX_FENS_IN_SMOKE_MODE
        : Integer.MAX_VALUE;
    final CompareAmbronaSemiStaticOracle.SemiStaticOracleComparison comparison = CompareAmbronaSemiStaticOracle
        .compare(maxFens);

    final int expectedFenCount = RestrictTestConstants.IS_RESTRICT_AMBRONA_SEMISTATIC_ORACLE_COMPARISON_TEST
        ? MAX_FENS_IN_SMOKE_MODE
        : TOTAL_FENS_IN_ORACLE;
    assertEquals(expectedFenCount, comparison.comparedFenCount());
    assertEquals(0, comparison.fenDifferenceCount());
    assertEquals(0, comparison.rowDifferenceCount());
    assertTrue(comparison.differenceCountByKind().isEmpty());
    assertTrue(comparison.differentFenList().isEmpty());
  }
}
