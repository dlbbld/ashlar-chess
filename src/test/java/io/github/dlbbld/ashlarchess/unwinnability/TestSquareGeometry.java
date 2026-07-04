// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.unwinnability;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import io.github.dlbbld.ashlarchess.board.enums.Side;
import io.github.dlbbld.ashlarchess.internal.Nulls;

// Known-value checks for the paper's square-geometry sets (fun22-spec.pdf section 1), ported from the
// fun22-reference unit tests.
class TestSquareGeometry {

  /** Algebraic square name to index (a1 = 0 ... h8 = 63). */
  private static int sq(String name) {
    final int file = name.charAt(0) - 'a';
    final int rank = name.charAt(1) - '1';
    return (rank << 3) | file;
  }

  private static long set(String... names) {
    long result = 0L;
    for (int i = 0; i < names.length; i++) {
      result |= 1L << sq(Nulls.get(names, i));
    }
    return result;
  }

  @SuppressWarnings("static-method")
  @Test
  void indexingMatchesAshlarLayout() {
    assertEquals(0, sq("a1"));
    assertEquals(7, sq("h1"));
    assertEquals(63, sq("h8"));
    assertEquals(28, sq("e4"));
  }

  @SuppressWarnings("static-method")
  @Test
  void knightFromCornerAndCentre() {
    assertEquals(set("b3", "c2"), SquareGeometry.knight(sq("a1")));
    assertEquals(set("d2", "f2", "c3", "g3", "c5", "g5", "d6", "f6"), SquareGeometry.knight(sq("e4")));
  }

  @SuppressWarnings("static-method")
  @Test
  void alphaBetaDeltaFromCorner() {
    assertEquals(set("a2", "b1"), SquareGeometry.alpha(sq("a1")));
    assertEquals(set("b2"), SquareGeometry.beta(sq("a1")));
    assertEquals(set("a2", "b1", "b2"), SquareGeometry.delta(sq("a1")));
  }

  @SuppressWarnings("static-method")
  @Test
  void deltaCardinalityByLocation() {
    assertEquals(3, Long.bitCount(SquareGeometry.delta(sq("a1")))); // corner
    assertEquals(5, Long.bitCount(SquareGeometry.delta(sq("a4")))); // edge
    assertEquals(8, Long.bitCount(SquareGeometry.delta(sq("e4")))); // centre
  }

  @SuppressWarnings("static-method")
  @Test
  void alphaFromCentreIsOrthogonalOnly() {
    assertEquals(set("c4", "e4", "d3", "d5"), SquareGeometry.alpha(sq("d4")));
  }

  @SuppressWarnings("static-method")
  @Test
  void pawnPushPredecessors() {
    assertEquals(set("e3"), SquareGeometry.pawnPushPredecessors(Side.WHITE, sq("e4")));
    assertEquals(set("e5"), SquareGeometry.pawnPushPredecessors(Side.BLACK, sq("e4")));
    // No square below rank 1 / above rank 8.
    assertEquals(0L, SquareGeometry.pawnPushPredecessors(Side.WHITE, sq("a1")));
    assertEquals(0L, SquareGeometry.pawnPushPredecessors(Side.BLACK, sq("h8")));
  }

  @SuppressWarnings("static-method")
  @Test
  void pawnAttackPredecessors() {
    assertEquals(set("d3", "f3"), SquareGeometry.pawnAttackPredecessors(Side.WHITE, sq("e4")));
    assertEquals(set("d5", "f5"), SquareGeometry.pawnAttackPredecessors(Side.BLACK, sq("e4")));
    // Edge file has a single attacker.
    assertEquals(set("b3"), SquareGeometry.pawnAttackPredecessors(Side.WHITE, sq("a4")));
  }

  @SuppressWarnings("static-method")
  @Test
  void promotionRanks() {
    assertEquals(set("a8", "b8", "c8", "d8", "e8", "f8", "g8", "h8"), SquareGeometry.promotion(Side.WHITE));
    assertEquals(set("a1", "b1", "c1", "d1", "e1", "f1", "g1", "h1"), SquareGeometry.promotion(Side.BLACK));
  }

  @SuppressWarnings("static-method")
  @Test
  void kingAndKnightDistances() {
    assertEquals(7, SquareGeometry.kingDistance(sq("a1"), sq("h8")));
    assertEquals(1, SquareGeometry.kingDistance(sq("e4"), sq("d5")));
    assertEquals(0, SquareGeometry.kingDistance(sq("e4"), sq("e4")));
    assertEquals(6, SquareGeometry.knightDistance(sq("a1"), sq("h8")));
    assertEquals(4, SquareGeometry.knightDistance(sq("a1"), sq("b2"))); // the classic corner-diagonal detour
    assertEquals(0, SquareGeometry.knightDistance(sq("e4"), sq("e4")));
  }
}
