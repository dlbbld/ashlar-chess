// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.test.bitboard;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Set;

import org.junit.jupiter.api.Test;

import io.github.dlbbld.ashlarchess.bitboard.BitboardPosition;
import io.github.dlbbld.ashlarchess.bitboard.BitboardPositionUtility;
import io.github.dlbbld.ashlarchess.bitboard.KingAttacks;
import io.github.dlbbld.ashlarchess.bitboard.StaticPositionBridge;
import io.github.dlbbld.ashlarchess.board.StaticPosition;
import io.github.dlbbld.ashlarchess.board.enums.Square;
import io.github.dlbbld.ashlarchess.common.Nulls;
import io.github.dlbbld.ashlarchess.squares.KingNonCastlingEmptyBoardSquares;
import io.github.dlbbld.ashlarchess.test.model.PgnFen;
import io.github.dlbbld.ashlarchess.test.model.PgnTestCaseList;
import io.github.dlbbld.ashlarchess.test.pgn.setup.PgnTestCaseCatalog;
import io.github.dlbbld.ashlarchess.test.pgntest.enums.PgnTest;

/**
 * Differential test for {@link KingAttacks}: the precomputed bitboard table must agree, for every from-square, with the
 * existing {@link KingNonCastlingEmptyBoardSquares}-backed reference. Geometric, position-independent. Castling targets
 * are intentionally out of scope at this layer and not tested here.
 */
class TestKingAttacks {

  @SuppressWarnings("static-method")
  @Test
  void directAgainstReference() {
    for (final Square fromSquare : Square.REAL) {
      final Set<Square> bitboardAttacks = BitboardPositionUtility.toSquareSet(KingAttacks.attacks(fromSquare));
      final Set<Square> referenceAttacks = KingNonCastlingEmptyBoardSquares.getKingSquares(fromSquare);
      assertEquals(referenceAttacks, bitboardAttacks, "king attacks from " + fromSquare.getName());
    }
  }

  @SuppressWarnings("static-method")
  @Test
  void corpusEveryKingAgrees() {
    for (final PgnTest pgnTest : PgnTest.values()) {
      final PgnTestCaseList testCaseList = PgnTestCaseCatalog.getTestList(pgnTest);
      for (final PgnFen testCase : testCaseList.list()) {
        final StaticPosition staticPosition = StaticPositionBridge
            .toStaticPosition(testCase.finalPosition().getBitboardPosition());
        final BitboardPosition bitboardPosition = StaticPositionBridge.fromStaticPosition(staticPosition);
        long kings = bitboardPosition.whiteKings() | bitboardPosition.blackKings();
        while (kings != 0L) {
          final Square fromSquare = Nulls.get(Square.REAL, Long.numberOfTrailingZeros(kings));
          final Set<Square> bitboardAttacks = BitboardPositionUtility.toSquareSet(KingAttacks.attacks(fromSquare));
          final Set<Square> referenceAttacks = KingNonCastlingEmptyBoardSquares.getKingSquares(fromSquare);
          assertEquals(referenceAttacks, bitboardAttacks,
              "king attacks from " + fromSquare.getName() + " in fixture " + testCase.pgnName());
          kings &= kings - 1L;
        }
      }
    }
  }

  @SuppressWarnings("static-method")
  @Test
  void noneSquareThrows() {
    assertThrows(IllegalArgumentException.class, () -> KingAttacks.attacks(Square.NONE));
  }
}
