// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.test.bitboard;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Set;
import java.util.TreeSet;

import org.junit.jupiter.api.Test;

import io.github.dlbbld.ashlarchess.bitboard.BitboardPosition;
import io.github.dlbbld.ashlarchess.bitboard.BitboardPositionUtility;
import io.github.dlbbld.ashlarchess.bitboard.StaticPositionBridge;
import io.github.dlbbld.ashlarchess.board.StaticPosition;
import io.github.dlbbld.ashlarchess.board.enums.Side;
import io.github.dlbbld.ashlarchess.board.enums.Square;
import io.github.dlbbld.ashlarchess.common.Nulls;
import io.github.dlbbld.ashlarchess.moves.LegalMovesTestOracle;
import io.github.dlbbld.ashlarchess.test.model.PgnFen;
import io.github.dlbbld.ashlarchess.test.model.PgnTestCaseList;
import io.github.dlbbld.ashlarchess.test.pgn.setup.PgnTestCaseCatalog;
import io.github.dlbbld.ashlarchess.test.pgntest.enums.PgnTest;

/**
 * Differential test for {@link BitboardPosition#legalKingTargets(Side)}: for every king on every fixture, the
 * bitboard's legal-non-castling target set must agree with the reference
 * {@code KingNonCastlingLegalMoves.calculateKingNonCastlingLegalMoves} (reached via {@link LegalMovesTestOracle}).
 * Castling targets are intentionally out of scope here; they live on {@code Board} together with castling-rights state.
 */
class TestBitboardPositionLegalKingMoves {

  @SuppressWarnings("static-method")
  @Test
  void corpusAgreesPerSide() {
    for (final PgnTest pgnTest : PgnTest.values()) {
      final PgnTestCaseList testCaseList = PgnTestCaseCatalog.getTestList(pgnTest);
      for (final PgnFen testCase : testCaseList.list()) {
        final StaticPosition staticPosition = StaticPositionBridge
            .toStaticPosition(testCase.finalPosition().getBitboardPosition());
        final BitboardPosition bitboardPosition = StaticPositionBridge.fromStaticPosition(staticPosition);
        assertSideAgrees(staticPosition, bitboardPosition, Side.WHITE, testCase);
        assertSideAgrees(staticPosition, bitboardPosition, Side.BLACK, testCase);
      }
    }
  }

  private static void assertSideAgrees(StaticPosition staticPosition, BitboardPosition bitboardPosition, Side side,
      PgnFen testCase) {
    final long ownKings = side == Side.WHITE ? bitboardPosition.whiteKings() : bitboardPosition.blackKings();
    if (ownKings == 0L) {
      return;
    }
    // Bitboard returns the union of legal targets across all own kings; the reference is per-king. Compare per-king
    // by computing the bitboard's legal-king-targets and intersecting with each king's pseudo-legal pattern, but for
    // standard chess (one king per side) the union is just the one king's set.
    final Set<Square> bitboardTargets = BitboardPositionUtility.toSquareSet(bitboardPosition.legalKingTargets(side));

    // For each own king, ask the reference. Union the answers (handles the multi-king edge case symmetrically).
    final Set<Square> referenceTargets = new TreeSet<>();
    long remaining = ownKings;
    while (remaining != 0L) {
      final Square kingSquare = Nulls.get(Square.REAL, Long.numberOfTrailingZeros(remaining));
      referenceTargets.addAll(LegalMovesTestOracle.kingNonCastlingLegalTargets(staticPosition, kingSquare, side));
      remaining &= remaining - 1L;
    }

    assertEquals(referenceTargets, bitboardTargets, side + " legalKingTargets in fixture " + testCase.pgnName());
  }

  @SuppressWarnings("static-method")
  @Test
  void initialPositionKingsHaveNoTargets() {
    // In the initial position the king is surrounded by own pieces - no pseudo-legal squares to begin with.
    assertEquals(0L, BitboardPosition.INITIAL_POSITION.legalKingTargets(Side.WHITE));
    assertEquals(0L, BitboardPosition.INITIAL_POSITION.legalKingTargets(Side.BLACK));
  }

  @SuppressWarnings("static-method")
  @Test
  void emptyPositionReturnsZero() {
    assertEquals(0L, BitboardPosition.EMPTY_POSITION.legalKingTargets(Side.WHITE));
    assertEquals(0L, BitboardPosition.EMPTY_POSITION.legalKingTargets(Side.BLACK));
  }

  @SuppressWarnings("static-method")
  @Test
  void noneSideThrows() {
    assertThrows(IllegalArgumentException.class, () -> BitboardPosition.INITIAL_POSITION.legalKingTargets(Side.NONE));
  }
}
