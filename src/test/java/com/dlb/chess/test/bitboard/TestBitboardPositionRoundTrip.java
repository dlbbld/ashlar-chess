package com.dlb.chess.test.bitboard;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import com.dlb.chess.bitboard.BitboardPosition;
import com.dlb.chess.bitboard.BitboardPositionUtility;
import com.dlb.chess.board.StaticPosition;
import com.dlb.chess.board.enums.Square;
import com.dlb.chess.test.model.PgnTestCase;
import com.dlb.chess.test.model.PgnTestCaseList;
import com.dlb.chess.test.pgn.setup.PgnTestCaseCatalog;
import com.dlb.chess.test.pgntest.enums.PgnTest;

/**
 * Differential round-trip test: for every fixture in the corpus, converting the {@link StaticPosition} to
 * {@link BitboardPosition} and back must reproduce the original. This is the spine of the bitboard release —
 * every later step depends on the two representations being faithful inverses of each other.
 */
class TestBitboardPositionRoundTrip {

  @SuppressWarnings("static-method")
  @Test
  void initialPosition() {
    final StaticPosition staticPosition = StaticPosition.INITIAL_POSITION;
    final BitboardPosition bitboardPosition = BitboardPositionUtility.fromStaticPosition(staticPosition);
    assertEquals(staticPosition, BitboardPositionUtility.toStaticPosition(bitboardPosition));
    assertEquals(bitboardPosition, BitboardPosition.INITIAL_POSITION);
  }

  @SuppressWarnings("static-method")
  @Test
  void emptyPosition() {
    final StaticPosition staticPosition = StaticPosition.EMPTY_POSITION;
    final BitboardPosition bitboardPosition = BitboardPositionUtility.fromStaticPosition(staticPosition);
    assertEquals(staticPosition, BitboardPositionUtility.toStaticPosition(bitboardPosition));
    assertEquals(bitboardPosition, BitboardPosition.EMPTY_POSITION);
  }

  @SuppressWarnings("static-method")
  @Test
  void fullCorpus() {
    for (final PgnTest pgnTest : PgnTest.values()) {
      final PgnTestCaseList testCaseList = PgnTestCaseCatalog.getTestList(pgnTest);
      for (final PgnTestCase testCase : testCaseList.list()) {
        final StaticPosition staticPosition = testCase.finalPosition().getStaticPosition();
        final BitboardPosition bitboardPosition = BitboardPositionUtility.fromStaticPosition(staticPosition);
        assertEquals(staticPosition, BitboardPositionUtility.toStaticPosition(bitboardPosition),
            "round-trip mismatch for fixture " + testCase.pgnName());
      }
    }
  }

  @SuppressWarnings("static-method")
  @Test
  void constructorRejectsOverlappingBitboards() {
    // A1 claimed by both whitePawns and whiteRooks — the compact constructor must reject.
    final long a1Bit = 1L << Square.A1.ordinal();
    assertThrows(IllegalArgumentException.class,
        () -> new BitboardPosition(a1Bit, a1Bit, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L));
    // Cross-side overlap (D4 in both whiteKnights and blackBishops) likewise rejected.
    final long d4Bit = 1L << Square.D4.ordinal();
    assertThrows(IllegalArgumentException.class,
        () -> new BitboardPosition(0L, 0L, d4Bit, 0L, 0L, 0L, 0L, 0L, 0L, d4Bit, 0L, 0L));
  }
}
