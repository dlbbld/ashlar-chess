// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.test.unwinnability.oracle;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

import io.github.dlbbld.ashlarchess.board.Board;
import io.github.dlbbld.ashlarchess.board.enums.Side;
import io.github.dlbbld.ashlarchess.test.model.PgnFen;
import io.github.dlbbld.ashlarchess.test.pgn.setup.PgnTestCaseCatalog;
import io.github.dlbbld.ashlarchess.test.pgntest.enums.PgnTest;
import io.github.dlbbld.ashlarchess.test.unwinnability.againstcha.AmbronaUnwinnabilityOracle;
import io.github.dlbbld.ashlarchess.test.unwinnability.oracle.enums.LimitedUnwinnabilityVerdict;
import io.github.dlbbld.ashlarchess.unwinnability.UnwinnabilityFullVerdict;

/**
 * Exercises {@link ForcedLineOracle} against the BASIC_FORCED corpus: positions where the unique-legal-move chain from
 * the root leads to a terminal status (checkmate / stalemate / insufficient material).
 */
class TestForcedLineOracle {

  @SuppressWarnings("static-method")
  @Test
  void testStartPosition() {
    final Board board = new Board();

    assertEquals(LimitedUnwinnabilityVerdict.UNKNOWN, ForcedLineOracle.calculateUnwinnability(board, Side.WHITE));
    assertEquals(LimitedUnwinnabilityVerdict.UNKNOWN, ForcedLineOracle.calculateUnwinnability(board, Side.BLACK));
  }

  @SuppressWarnings("static-method")
  @Test
  void testBasicForcedCorpus() {
    final List<PgnFen> fixtures = PgnTestCaseCatalog.getTestList(PgnTest.BASIC_FORCED).list();

    for (final PgnFen testCase : fixtures) {
      final Board board = testCase.finalPosition();

      assertEquals(convert(AmbronaUnwinnabilityOracle.get(testCase.finalFen()).fullWhite()),
          ForcedLineOracle.calculateUnwinnability(board, Side.WHITE), testCase.pgnName());
      assertEquals(convert(AmbronaUnwinnabilityOracle.get(testCase.finalFen()).fullBlack()),
          ForcedLineOracle.calculateUnwinnability(board, Side.BLACK), testCase.pgnName());
    }
  }

  private static LimitedUnwinnabilityVerdict convert(UnwinnabilityFullVerdict verdict) {
    return switch (verdict) {
      case UNWINNABLE -> LimitedUnwinnabilityVerdict.UNWINNABLE;
      case WINNABLE_HELPMATE, WINNABLE_BY_THEOREM -> LimitedUnwinnabilityVerdict.WINNABLE;
      case UNDETERMINED -> LimitedUnwinnabilityVerdict.UNKNOWN;
    };
  }
}
