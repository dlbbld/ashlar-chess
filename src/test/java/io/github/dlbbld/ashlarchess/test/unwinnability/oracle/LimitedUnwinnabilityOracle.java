// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.test.unwinnability.oracle;

import io.github.dlbbld.ashlarchess.board.Board;
import io.github.dlbbld.ashlarchess.board.enums.Side;
import io.github.dlbbld.ashlarchess.test.unwinnability.oracle.enums.LimitedUnwinnabilityVerdict;

/**
 * Test-only oracle that composes three independent sub-oracles in sequence, returning the first non-{@code UNKNOWN}
 * verdict any of them reports.
 *
 * <ol>
 * <li>{@link ForcedLineOracle} - walks the unique-legal-move chain to any depth.</li>
 * <li>{@link ShallowTerminationOracle} - bounded 1/2/3-ply scan over all legal moves.</li>
 * <li>{@link PawnWallGeometricAnalyzer} - geometric pawn-wall check; a YES verdict implies UNWINNABLE for both
 * sides.</li>
 * </ol>
 *
 * <p>
 * Each sub-oracle is self-contained (does its own pre-checks) and tested in isolation. This oracle's only
 * responsibility is the composition, which is intentionally explicit at the call site rather than buried in helpers so
 * the decision pipeline is visible when reading the code.
 */
public class LimitedUnwinnabilityOracle {

  public static LimitedUnwinnabilityVerdict calculateUnwinnability(Board board, Side side) {

    final LimitedUnwinnabilityVerdict forcedVerdict = ForcedLineOracle.calculateUnwinnability(board, side);
    if (forcedVerdict != LimitedUnwinnabilityVerdict.UNKNOWN) {
      return forcedVerdict;
    }

    final LimitedUnwinnabilityVerdict shallowVerdict = ShallowTerminationOracle.calculateUnwinnability(board, side);
    if (shallowVerdict != LimitedUnwinnabilityVerdict.UNKNOWN) {
      return shallowVerdict;
    }

    if (PawnWallGeometricAnalyzer.calculate(board) == PawnWallVerdict.YES) {
      return LimitedUnwinnabilityVerdict.UNWINNABLE;
    }

    return LimitedUnwinnabilityVerdict.UNKNOWN;
  }
}
