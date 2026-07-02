// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.test.unwinnability.againstcha;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;

import io.github.dlbbld.ashlarchess.internal.Nulls;
import io.github.dlbbld.ashlarchess.test.RestrictTestConstants;
import io.github.dlbbld.ashlarchess.unwinnability.CompareAmbronaSemiStaticOracle;

class TestAmbronaSemiStaticOracleComparison {

  /** Cap on FENs processed when the smoke restriction is active. Full mode compares all 1249. */
  private static final int MAX_FENS_IN_SMOKE_MODE = 10;

  /** FEN count in the oracle file. Asserted as the expected comparedFenCount in full mode. */
  private static final int TOTAL_FENS_IN_ORACLE = 1249;

  // The single accepted semi-static divergence from cha (C++), on one pawn-wall position. cha applies the
  // `isIgnorePawns` shortcut - flagged "(is this sound?)" in its own semistatic.cpp - that drops the blocked black
  // pawns from the semi-static visitor set and so proves UNWINNABLE. ashlar, after the 21.0.0 soundness fix in
  // UnwinnableSemiStatic, keeps those pawns as visitors and reports POSSIBLY_WINNABLE; it is the sound side (see
  // CHANGELOG 21.0.0). A divergence on any other FEN, or of any other kind, is a real regression.
  //
  // The position appears under TWO move clocks: the committed oracle TSV keeps the row generated from the since-
  // deduplicated ambrona_16.pgn fixture (same board, clock "10 100"), so the comparison observes both FEN strings.
  // These are two recordings of the one divergence, not two divergences - both stay accepted unless the TSV is
  // regenerated without the removed fixture.
  private static final Set<String> ACCEPTED_DIVERGENT_FENS = Nulls.setOf(
      "1k6/p1p1p1p1/P1P1P1P1/p1p1p1p1/8/8/P1P1P1P1/4K3 w - - 0 34",
      "1k6/p1p1p1p1/P1P1P1P1/p1p1p1p1/8/8/P1P1P1P1/4K3 w - - 10 100");

  private static final Set<String> ACCEPTED_DIFFERENCE_KINDS = Nulls.setOf("VERDICT", "AMBRONA_VISITORS_EXPANDED");

  @SuppressWarnings("static-method")
  @Test
  void currentSemiStaticComparisonMatchesKnownBaseline() throws Exception {
    final boolean isSmoke = RestrictTestConstants.IS_RESTRICT_AMBRONA_SEMISTATIC_ORACLE_COMPARISON_TEST;
    final CompareAmbronaSemiStaticOracle.SemiStaticOracleComparison comparison = CompareAmbronaSemiStaticOracle
        .compare(isSmoke ? MAX_FENS_IN_SMOKE_MODE : Integer.MAX_VALUE);

    assertEquals(isSmoke ? MAX_FENS_IN_SMOKE_MODE : TOTAL_FENS_IN_ORACLE, comparison.comparedFenCount());

    // Every observed difference must be one of the accepted soundness divergences; anything else is a regression.
    assertTrue(ACCEPTED_DIVERGENT_FENS.containsAll(comparison.differentFens()),
        "unexpected semi-static divergence from cha on " + comparison.differentFens());
    assertTrue(ACCEPTED_DIFFERENCE_KINDS.containsAll(comparison.differenceCountByKind().keySet()),
        "unexpected semi-static difference kinds " + comparison.differenceCountByKind());

    if (!isSmoke) {
      // The full run must observe exactly the two accepted divergences - the VERDICT flip and the visitor-set change
      // per FEN - keeping the accepted set current and catching the case where cha's shortcut difference disappears.
      assertEquals(ACCEPTED_DIVERGENT_FENS, new HashSet<>(comparison.differentFens()));
      assertEquals(Map.of("VERDICT", 2, "AMBRONA_VISITORS_EXPANDED", 2), comparison.differenceCountByKind());
      assertEquals(4, comparison.rowDifferenceCount());
    }
  }
}
