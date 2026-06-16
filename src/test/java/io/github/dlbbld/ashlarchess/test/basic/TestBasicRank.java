// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.test.basic;

import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.BLACK;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.RANK_1;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.RANK_2;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.RANK_3;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.RANK_4;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.RANK_5;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.RANK_6;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.RANK_7;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.RANK_8;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.WHITE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import io.github.dlbbld.ashlarchess.board.enums.Rank;
import io.github.dlbbld.ashlarchess.board.enums.Side;

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

  @SuppressWarnings("static-method")
  @Test
  void testPreviousRank() throws Exception {
    // white
    checkExceptionPrevious(WHITE, RANK_1);
    assertEquals(RANK_1, RANK_2.getPreviousRank(WHITE));
    assertEquals(RANK_2, RANK_3.getPreviousRank(WHITE));
    assertEquals(RANK_3, RANK_4.getPreviousRank(WHITE));
    assertEquals(RANK_4, RANK_5.getPreviousRank(WHITE));
    assertEquals(RANK_5, RANK_6.getPreviousRank(WHITE));
    assertEquals(RANK_6, RANK_7.getPreviousRank(WHITE));
    assertEquals(RANK_7, RANK_8.getPreviousRank(WHITE));

    // black
    assertEquals(RANK_2, RANK_1.getPreviousRank(BLACK));
    assertEquals(RANK_3, RANK_2.getPreviousRank(BLACK));
    assertEquals(RANK_4, RANK_3.getPreviousRank(BLACK));
    assertEquals(RANK_5, RANK_4.getPreviousRank(BLACK));
    assertEquals(RANK_6, RANK_5.getPreviousRank(BLACK));
    assertEquals(RANK_7, RANK_6.getPreviousRank(BLACK));
    assertEquals(RANK_8, RANK_7.getPreviousRank(BLACK));
    checkExceptionPrevious(BLACK, RANK_8);
  }

  private static void checkExceptionPrevious(Side side, Rank rank) {
    boolean isException;
    try {
      rank.getPreviousRank(side);
      isException = false;
    } catch (@SuppressWarnings("unused") final IllegalArgumentException e) {
      isException = true;
    }
    assertTrue(isException);
  }
}
