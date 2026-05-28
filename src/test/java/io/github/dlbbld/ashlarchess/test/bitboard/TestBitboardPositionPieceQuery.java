// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.test.bitboard;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import io.github.dlbbld.ashlarchess.bitboard.BitboardPosition;
import io.github.dlbbld.ashlarchess.bitboard.StaticPositionBridge;
import io.github.dlbbld.ashlarchess.board.StaticPosition;
import io.github.dlbbld.ashlarchess.board.enums.Piece;
import io.github.dlbbld.ashlarchess.board.enums.Square;
import io.github.dlbbld.ashlarchess.test.model.PgnFen;
import io.github.dlbbld.ashlarchess.test.model.PgnTestCaseList;
import io.github.dlbbld.ashlarchess.test.pgn.setup.PgnTestCaseCatalog;
import io.github.dlbbld.ashlarchess.test.pgntest.enums.PgnTest;

/**
 * Differential test for {@link BitboardPosition#get(Square)} and {@link BitboardPosition#isEmpty(Square)}: for every
 * fixture in the corpus and every one of the 64 real squares, the bitboard and the reference {@link StaticPosition}
 * must agree on which piece (if any) sits there.
 */
class TestBitboardPositionPieceQuery {

  @SuppressWarnings("static-method")
  @Test
  void initialPosition() {
    final BitboardPosition bitboardPosition = BitboardPosition.INITIAL_POSITION;
    final StaticPosition staticPosition = StaticPosition.INITIAL_POSITION;
    for (final Square square : Square.REAL) {
      assertEquals(staticPosition.get(square), bitboardPosition.get(square), "get(" + square.getName() + ")");
      assertEquals(staticPosition.isEmpty(square), bitboardPosition.isEmpty(square),
          "isEmpty(" + square.getName() + ")");
    }
  }

  @SuppressWarnings("static-method")
  @Test
  void emptyPosition() {
    final BitboardPosition bitboardPosition = BitboardPosition.EMPTY_POSITION;
    for (final Square square : Square.REAL) {
      assertEquals(Piece.NONE, bitboardPosition.get(square), "get(" + square.getName() + ")");
      assertTrue(bitboardPosition.isEmpty(square), "isEmpty(" + square.getName() + ")");
    }
  }

  @SuppressWarnings("static-method")
  @Test
  void noneSquareThrows() {
    final BitboardPosition bitboardPosition = BitboardPosition.INITIAL_POSITION;
    assertThrows(IllegalArgumentException.class, () -> bitboardPosition.get(Square.NONE));
    assertThrows(IllegalArgumentException.class, () -> bitboardPosition.isEmpty(Square.NONE));
  }

  @SuppressWarnings("static-method")
  @Test
  void fullCorpus() {
    for (final PgnTest pgnTest : PgnTest.values()) {
      final PgnTestCaseList testCaseList = PgnTestCaseCatalog.getTestList(pgnTest);
      for (final PgnFen testCase : testCaseList.list()) {
        final StaticPosition staticPosition = StaticPositionBridge
            .toStaticPosition(testCase.finalPosition().getBitboardPosition());
        final BitboardPosition bitboardPosition = StaticPositionBridge.fromStaticPosition(staticPosition);
        for (final Square square : Square.REAL) {
          assertEquals(staticPosition.get(square), bitboardPosition.get(square),
              "get(" + square.getName() + ") mismatch in fixture " + testCase.pgnName());
          assertEquals(staticPosition.isEmpty(square), bitboardPosition.isEmpty(square),
              "isEmpty(" + square.getName() + ") mismatch in fixture " + testCase.pgnName());
        }
      }
    }
  }
}
