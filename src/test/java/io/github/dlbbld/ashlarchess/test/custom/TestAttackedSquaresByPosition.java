// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.test.custom;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Set;
import java.util.TreeSet;

import org.eclipse.jdt.annotation.NonNull;
import org.junit.jupiter.api.Test;

import io.github.dlbbld.ashlarchess.bitboard.StaticPositionBridge;
import io.github.dlbbld.ashlarchess.board.enums.Square;
import io.github.dlbbld.ashlarchess.common.constants.EnumConstants;
import io.github.dlbbld.ashlarchess.fen.FenParserAdvanced;
import io.github.dlbbld.ashlarchess.fen.constants.FenConstants;
import io.github.dlbbld.ashlarchess.fen.model.Fen;
import io.github.dlbbld.ashlarchess.squares.AbstractAttackedSquares;

class TestAttackedSquaresByPosition implements EnumConstants {

  @SuppressWarnings("static-method")
  @Test
  void testWhite() {
    checkPosition(FenConstants.FEN_INITIAL_STR, "b1", "c1", "d1", "e1", "f1", "g1", "a2", "b2", "c2", "d2", "e2", "f2",
        "g2", "h2", "a3", "b3", "c3", "d3", "e3", "f3", "g3", "h3");

    checkPosition("8/8/3k4/8/5K2/8/8/8 w - - 10 100", "e3", "e4", "e5", "f5", "g5", "g4", "g3", "f3");

    checkPosition("8/8/3k4/8/4RK2/8/8/8 w - - 10 100", "e3", "e4", "e5", "f5", "g5", "g4", "g3", "f3", "d4", "c4", "b4",
        "a4", "e5", "e6", "e7", "e8", "f4", "e3", "e2", "e1");

    checkPosition("r1bq1rk1/pppp1ppp/2n5/2b1p3/2BPn3/P1P2N2/1P3PPP/RNBQK2R w KQ - 0 7", "b4", "a2", "a3", "b1", "a3",
        "c3", "a3", "c3", "d2", "b5", "a6", "d5", "e6", "f7", "d3", "e2", "f1", "b3", "a2", "b4", "d4", "b2", "d2",
        "e3", "f4", "g5", "h6", "c5", "e5", "c1", "c2", "b3", "a4", "d2", "d3", "d4", "e2", "f3", "e1", "d1", "d2",
        "e2", "f2", "f1", "e1", "d2", "d4", "e5", "g5", "h4", "h2", "g1", "e3", "g3", "f3", "h3", "g3", "g1", "h2");
  }

  @SuppressWarnings("static-method")
  @Test
  void testBlack() {
    checkPosition(FenConstants.FEN_AFTER_E4_STR, "b8", "c8", "d8", "e8", "f8", "g8", "a7", "b7", "c7", "d7", "e7", "f7",
        "g7", "h7", "a6", "b6", "c6", "d6", "e6", "f6", "g6", "h6");

    checkPosition("8/8/3k4/8/5K2/8/8/8 b - - 10 100", "e7", "e6", "e5", "d5", "c5", "c6", "c7", "d7");

    checkPosition("8/8/3k4/8/4RK2/8/8/8 b - - 10 100", "e7", "e6", "e5", "d5", "c5", "c6", "c7", "d7");

    checkPosition("8/3r4/3k4/8/4RK2/8/8/8 b - - 10 100", "e7", "e6", "e5", "d5", "c5", "c6", "c7", "d7", "f7", "g7",
        "h7", "d6", "b7", "a7", "d8");

    checkPosition("r1bq1rk1/pppp1ppp/2n5/2b1p3/2BPn3/P1P2N2/1P3PPP/RNBQK2R b KQ - 0 7", "b6", "a7", "b8", "a6", "b6",
        "b4", "a3", "b6", "a7", "d4", "d6", "e7", "f8", "b4", "a5", "a7", "b8", "d8", "e7", "e5", "d4", "b6", "d6",
        "b7", "d7", "c6", "e6", "d7", "c7", "c8", "e8", "f8", "e7", "f6", "g5", "h4", "d2", "c3", "c5", "d6", "f6",
        "g5", "g3", "f2", "d4", "f4", "e6", "g6", "f7", "e8", "g8", "f6", "h6", "g7", "f7", "f8", "h8", "h7", "g6");
  }

  private static void checkPosition(String fenStr, String... expectedSquareList) {
    final Fen fen = FenParserAdvanced.parseFenAdvanced(fenStr);

    // The relocated reference oracle (AbstractAttackedSquares) takes StaticPosition; derive it on demand from
    // Fen's bitboard via the test-oracle bridge.
    final Set<Square> actualSquareSet = AbstractAttackedSquares
        .calculateAttackedSquares(StaticPositionBridge.toStaticPosition(fen.bitboardPosition()), fen.havingMove());

    final Set<String> expected = new TreeSet<>();
    for (final String square : expectedSquareList) {
      @SuppressWarnings("null") @NonNull final String nonNullSquare = square;
      expected.add(nonNullSquare);
    }

    final Set<String> actual = new TreeSet<>();
    for (final Square square : actualSquareSet) {
      actual.add(square.getName());
    }

    assertEquals(expected, actual);

  }

}
