package io.github.dlbbld.ashlarchess.test.unwinnability.againstcha;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import io.github.dlbbld.ashlarchess.unwinnability.CompareAmbronaMobilityOracle;

class TestAmbronaMobilityOracleComparison {

  @SuppressWarnings("static-method")
  @Test
  void currentMobilityComparisonMatchesKnownBaseline() throws Exception {
    final CompareAmbronaMobilityOracle.MobilityOracleComparison comparison = CompareAmbronaMobilityOracle.compare();

    assertEquals(1249, comparison.comparedFenCount());
    assertEquals(0, comparison.fenDifferenceCount());
    assertEquals(0, comparison.rowDifferenceCount());
    assertTrue(comparison.differentFenList().isEmpty());
  }
}
