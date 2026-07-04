// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.unwinnability;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import io.github.dlbbld.ashlarchess.board.Board;

// Mobility fixpoint checks on positions verifiable by hand, ported from the fun22-reference unit tests. The bishop
// case is the meaningful geometric test - a bishop can never change square colour, so its region must be exactly the
// 32 squares of its own colour.
class TestMobility {

  private static final long ALL_SQUARES = -1L; // 64 set bits

  private static int sq(String name) {
    return ((name.charAt(1) - '1') << 3) | (name.charAt(0) - 'a');
  }

  private static long regionAt(MobilitySolution mobilitySolution, int square) {
    final SemiStaticPosition position = mobilitySolution.position();
    for (int i = 0; i < position.count(); i++) {
      if (position.piece(i).square() == square) {
        return mobilitySolution.region(i);
      }
    }
    throw new AssertionError("no piece at square " + square);
  }

  private static MobilitySolution mobility(String fen) {
    return Mobility.mobility(SemiStaticPosition.fromBoard(Board.fromFenStrict(fen)));
  }

  @SuppressWarnings("static-method")
  @Test
  void loneKnightReachesTheWholeBoard() {
    final MobilitySolution mobilitySolution = mobility("7k/8/8/8/8/8/8/KN6 w - - 0 1");
    assertEquals(ALL_SQUARES, regionAt(mobilitySolution, sq("b1")));
  }

  @SuppressWarnings("static-method")
  @Test
  void bishopIsConfinedToItsSquareColour() {
    final MobilitySolution mobilitySolution = mobility("7k/8/8/8/8/8/8/K1B5 w - - 0 1");
    final long region = regionAt(mobilitySolution, sq("c1"));
    assertEquals(SquareGeometry.DARK_SQUARES, region, "bishop reaches exactly its 32 dark squares");
    assertEquals(32, Long.bitCount(region));
    assertEquals(0L, region & (1L << sq("h1")), "h1 is a light square, unreachable");
  }

  @SuppressWarnings("static-method")
  @Test
  void pawnRegionBecomesUniversalViaPromotion() {
    final MobilitySolution mobilitySolution = mobility("7k/8/8/8/8/8/4P3/K7 w - - 0 1");
    assertEquals(ALL_SQUARES, regionAt(mobilitySolution, sq("e2")),
        "pushing up the empty file reaches the promotion rank, then 'go everywhere'");
  }
}
