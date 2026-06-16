// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.test.basic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import io.github.dlbbld.ashlarchess.board.enums.Rank;

class TestBasicRank {

  @SuppressWarnings("static-method")
  @Test
  void testCount() throws Exception {
    int totalRanks = 0;
    for (@SuppressWarnings("unused") final Rank rank : Rank.REAL) {
      totalRanks++;
    }
    assertEquals(8, totalRanks);
  }

  @SuppressWarnings("static-method")
  @Test
  void testMethodsDirect() throws Exception {
    assertFalse(Rank.exists('-'));
    assertFalse(Rank.exists('\0'));
    assertFalse(Rank.exists('0'));
    assertFalse(Rank.exists('9'));

    assertTrue(Rank.exists('1'));
    assertTrue(Rank.exists('2'));
    assertTrue(Rank.exists('3'));
    assertTrue(Rank.exists('4'));
    assertTrue(Rank.exists('5'));
    assertTrue(Rank.exists('6'));
    assertTrue(Rank.exists('7'));
    assertTrue(Rank.exists('8'));

    assertEquals(Rank.RANK_1, Rank.parse('1'));
    assertEquals(Rank.RANK_2, Rank.parse('2'));
    assertEquals(Rank.RANK_3, Rank.parse('3'));
    assertEquals(Rank.RANK_4, Rank.parse('4'));
    assertEquals(Rank.RANK_5, Rank.parse('5'));
    assertEquals(Rank.RANK_6, Rank.parse('6'));
    assertEquals(Rank.RANK_7, Rank.parse('7'));
    assertEquals(Rank.RANK_8, Rank.parse('8'));
  }
}
