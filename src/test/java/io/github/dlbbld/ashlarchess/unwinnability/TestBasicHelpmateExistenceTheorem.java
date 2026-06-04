// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.unwinnability;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import io.github.dlbbld.ashlarchess.board.Board;
import io.github.dlbbld.ashlarchess.board.enums.Side;

// Unit test for the basic-helpmate-existence theorem shortcut (BasicHelpmateExistenceTheorem.decide). It pins the
// theorem's three cases per covered class - winner to move -> WINNABLE; defender to move not forced -> WINNABLE;
// defender forced to capture the mating material -> UNWINNABLE - and the NOT_APPLICABLE guards that keep the shortcut
// from firing outside its proven domain (defender as intended winner, same-coloured bishops, uncovered material).
class TestBasicHelpmateExistenceTheorem {

  // ----- Winner (White) to move: WINNABLE for every covered class. -----

  @SuppressWarnings("static-method")
  @Test
  void winnerToMoveIsWinnableForEveryClass() {
    assertWhiteWinner(BasicHelpmateExistenceTheoremResult.WINNABLE, "4k3/8/8/8/8/8/8/R3K3 w - - 0 1"); // KRvK
    assertWhiteWinner(BasicHelpmateExistenceTheoremResult.WINNABLE, "4k3/8/8/8/8/8/8/Q3K3 w - - 0 1"); // KQvK
    assertWhiteWinner(BasicHelpmateExistenceTheoremResult.WINNABLE, "4k3/8/8/8/8/8/8/2B1KB2 w - - 0 1"); // KBBvK opp
    assertWhiteWinner(BasicHelpmateExistenceTheoremResult.WINNABLE, "4k3/8/8/8/8/8/8/4KBN1 w - - 0 1"); // KBNvK
    assertWhiteWinner(BasicHelpmateExistenceTheoremResult.WINNABLE, "2b1k3/8/8/8/8/8/8/R3K3 w - - 0 1"); // KRvKB
    assertWhiteWinner(BasicHelpmateExistenceTheoremResult.WINNABLE, "1n2k3/8/8/8/8/8/8/R3K3 w - - 0 1"); // KRvKN
  }

  // ----- Defender (Black) to move, not forced to capture: WINNABLE. -----

  @SuppressWarnings("static-method")
  @Test
  void defenderToMoveNotForcedIsWinnable() {
    assertWhiteWinner(BasicHelpmateExistenceTheoremResult.WINNABLE, "4k3/8/8/8/8/8/8/R3K3 b - - 0 1"); // KRvK
    assertWhiteWinner(BasicHelpmateExistenceTheoremResult.WINNABLE, "4k3/8/8/8/8/8/8/2B1KB2 b - - 0 1"); // KBBvK opp
    assertWhiteWinner(BasicHelpmateExistenceTheoremResult.WINNABLE, "1n2k3/8/8/8/8/8/8/R3K3 b - - 0 1"); // KRvKN
  }

  // ----- Defender (Black) to move, every legal move captures White's mating material: UNWINNABLE. -----

  @SuppressWarnings("static-method")
  @Test
  void defenderForcedToCaptureIsUnwinnable() {
    assertWhiteWinner(BasicHelpmateExistenceTheoremResult.UNWINNABLE, "k7/R1K5/8/8/8/8/8/8 b - - 0 1"); // KRvK
    assertWhiteWinner(BasicHelpmateExistenceTheoremResult.UNWINNABLE, "k7/Q1K5/8/8/8/8/8/8 b - - 0 1"); // KQvK
    assertWhiteWinner(BasicHelpmateExistenceTheoremResult.UNWINNABLE, "kB6/8/B1K5/8/8/8/8/8 b - - 0 1"); // KBBvK opp
    assertWhiteWinner(BasicHelpmateExistenceTheoremResult.UNWINNABLE, "kN6/8/BK6/8/8/8/8/8 b - - 0 1"); // KBNvK
    assertWhiteWinner(BasicHelpmateExistenceTheoremResult.UNWINNABLE, "k7/R1K5/8/8/8/8/8/7b b - - 0 1"); // KRvKB
    assertWhiteWinner(BasicHelpmateExistenceTheoremResult.UNWINNABLE, "k7/R1K5/8/8/8/8/8/7n b - - 0 1"); // KRvKN
  }

  // ----- NOT_APPLICABLE: outside the proven domain, the shortcut must defer to the regular analysis. -----

  @SuppressWarnings("static-method")
  @Test
  void defenderAsIntendedWinnerIsNotApplicable() {
    // KRvK, but Black (the bare king) is the intended winner: Black is not the mating side.
    assertEquals(BasicHelpmateExistenceTheoremResult.NOT_APPLICABLE,
        BasicHelpmateExistenceTheorem.decide(new Board("4k3/8/8/8/8/8/8/R3K3 w - - 0 1"), Side.BLACK));
  }

  @SuppressWarnings("static-method")
  @Test
  void sameColouredBishopsAreNotApplicable() {
    // KBBvK with both bishops on dark squares (c1, g1): not the opposite-bishop class the theorem covers.
    assertWhiteWinner(BasicHelpmateExistenceTheoremResult.NOT_APPLICABLE, "4k3/8/8/8/8/8/8/2B1K1B1 w - - 0 1");
  }

  @SuppressWarnings("static-method")
  @Test
  void uncoveredMaterialIsNotApplicable() {
    assertWhiteWinner(BasicHelpmateExistenceTheoremResult.NOT_APPLICABLE, "4k3/8/8/8/8/8/4P3/4K3 w - - 0 1"); // KPvK
    assertWhiteWinner(BasicHelpmateExistenceTheoremResult.NOT_APPLICABLE, "4k3/8/8/8/8/8/8/R3K2R w - - 0 1"); // KRRvK
    assertWhiteWinner(BasicHelpmateExistenceTheoremResult.NOT_APPLICABLE,
        "r1bqkbnr/pppppppp/2n5/8/8/5N2/PPPPPPPP/RNBQKB1R w KQkq - 4 3"); // opening
  }

  private static void assertWhiteWinner(BasicHelpmateExistenceTheoremResult expected, String fen) {
    assertEquals(expected, BasicHelpmateExistenceTheorem.decide(new Board(fen), Side.WHITE), fen);
  }
}
