package com.dlb.chess.test.unwinnability.oracle;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.dlb.chess.board.Board;
import com.dlb.chess.board.enums.Side;
import com.dlb.chess.test.model.PgnFen;
import com.dlb.chess.test.pgn.setup.PgnTestCaseCatalog;
import com.dlb.chess.test.pgntest.enums.PgnTest;
import com.dlb.chess.test.unwinnability.againstcha.AmbronaUnwinnabilityOracle;
import com.dlb.chess.test.unwinnability.oracle.enums.LimitedUnwinnabilityVerdict;
import com.dlb.chess.unwinnability.UnwinnabilityFullVerdict;

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
      case WINNABLE -> LimitedUnwinnabilityVerdict.WINNABLE;
      case UNDETERMINED -> LimitedUnwinnabilityVerdict.UNKNOWN;
      default -> throw new IllegalArgumentException();
    };
  }
}
