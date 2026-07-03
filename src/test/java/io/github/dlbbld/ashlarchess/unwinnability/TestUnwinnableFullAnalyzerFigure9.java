// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.unwinnability;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import io.github.dlbbld.ashlarchess.board.Board;
import io.github.dlbbld.ashlarchess.board.enums.Side;

// End-to-end checks of the full routine (Figure 9: semi-static shortcut + Find-Helpmate under iterative deepening,
// plus ashlar's theorem extension), ported from the fun22-reference unit tests and adapted to the theorem shortcut
// where it intercepts a covered material class.
class TestUnwinnableFullAnalyzerFigure9 {

  private static UnwinnabilityFullAnalysis full(String fen, Side winner) {
    return UnwinnableFullAnalyzer.unwinnableFull(Board.fromFenStrict(fen), winner);
  }

  @SuppressWarnings("static-method")
  @Test
  void blockedFortressIsUnwinnableForBoth() {
    final String fen = "2b1k3/8/8/1p1p1p1p/1P1P1P1P/8/8/2B1K3 w - - 0 1";
    assertEquals(UnwinnabilityFullVerdict.UNWINNABLE, full(fen, Side.WHITE).verdict());
    assertEquals(UnwinnabilityFullVerdict.UNWINNABLE, full(fen, Side.BLACK).verdict());
  }

  @SuppressWarnings("static-method")
  @Test
  void kingAndQueenCanHelpmateButBareKingCannot() {
    final String fen = "4k3/8/8/8/8/8/1Q6/4K3 w - - 0 1";
    final UnwinnabilityFullAnalysis whiteAnalysis = full(fen, Side.WHITE);
    assertEquals(UnwinnabilityFullVerdict.WINNABLE, whiteAnalysis.verdict());
    assertEquals(WinnableProof.THEOREM, whiteAnalysis.winnableProof()); // KQvK is theorem-certified, no mate line
    assertEquals(UnwinnabilityFullVerdict.UNWINNABLE, full(fen, Side.BLACK).verdict()); // Black is a bare king
  }

  @SuppressWarnings("static-method")
  @Test
  void pawnFreeKnightIsUnwinnableByLemma5() {
    // K+N vs K, pawn-free: Lemma 5 leaves make the bounded tree exhaust - the knight's owner cannot mate.
    assertEquals(UnwinnabilityFullVerdict.UNWINNABLE, full("4k3/8/8/8/8/8/8/3NK3 w - - 0 1", Side.WHITE).verdict());
  }

  @SuppressWarnings("static-method")
  @Test
  void searchedMateInOneCarriesAMateLine() {
    // White (K+Q+R - not a theorem-covered class) mates in one: the search exhibits the helpmate and the analysis
    // carries the one-move line.
    final UnwinnabilityFullAnalysis analysis = full("k7/8/1K6/8/8/8/8/1Q5R w - - 0 1", Side.WHITE);
    assertEquals(UnwinnabilityFullVerdict.WINNABLE, analysis.verdict());
    assertEquals(WinnableProof.HELPMATE, analysis.winnableProof());
    assertEquals(1, analysis.mateLine().size());
  }

  @SuppressWarnings("static-method")
  @Test
  void alreadyCheckmatedPositionIsAZeroMoveHelpmate() {
    // Terminal handling (Figure 5 base case): checkmate on the board is WINNABLE for the mater with an empty mate
    // line, and UNWINNABLE for the mated side.
    final String fen = "k6R/8/1K6/8/8/8/8/8 b - - 0 1";
    final UnwinnabilityFullAnalysis whiteAnalysis = full(fen, Side.WHITE);
    assertEquals(UnwinnabilityFullVerdict.WINNABLE, whiteAnalysis.verdict());
    assertEquals(WinnableProof.HELPMATE, whiteAnalysis.winnableProof());
    assertTrue(whiteAnalysis.mateLine().isEmpty());
    assertEquals(UnwinnabilityFullVerdict.UNWINNABLE, full(fen, Side.BLACK).verdict());
  }

  @SuppressWarnings("static-method")
  @Test
  void stalemateIsUnwinnableForBoth() {
    // Terminal handling (Figure 5 base case): stalemate ends the game drawn.
    final String fen = "k7/8/1Q6/8/8/8/8/K7 b - - 0 1";
    assertEquals(UnwinnabilityFullVerdict.UNWINNABLE, full(fen, Side.WHITE).verdict());
    assertEquals(UnwinnabilityFullVerdict.UNWINNABLE, full(fen, Side.BLACK).verdict());
  }

  @SuppressWarnings("static-method")
  @Test
  void deepHelpmateNeedsTheFootnoteBRewardChain() {
    // K+Q vs K+pawn: the loser's pawn keeps this outside every theorem class, so the search itself must exhibit the
    // deep queen helpmate - which the bounded deepening only reaches with the Figure 5 footnote-b reward chaining
    // (rewarding a Normal move that follows a Reward move). This is the end-to-end pin for that footnote and for
    // the reward-chain-aware transposition key.
    final UnwinnabilityFullAnalysis analysis = full("4k3/4p3/8/8/8/8/8/3QK3 w - - 0 1", Side.WHITE);
    assertEquals(UnwinnabilityFullVerdict.WINNABLE, analysis.verdict());
    assertEquals(WinnableProof.HELPMATE, analysis.winnableProof());
    assertTrue(analysis.mateLine().size() >= 2);
  }

  @SuppressWarnings("static-method")
  @Test
  void twoKnightsAndOppositeBishopsRemainWinnable() {
    // Lemma 5/6 strictness end-to-end (TestMaterialLemmas pins the predicates themselves): two knights and
    // opposite-coloured bishops CAN helpmate. Both classes are theorem-covered, so the verdicts are certified.
    final UnwinnabilityFullAnalysis knights = full("k7/8/8/8/8/8/8/K5NN w - - 0 1", Side.WHITE);
    assertEquals(UnwinnabilityFullVerdict.WINNABLE, knights.verdict());
    assertEquals(WinnableProof.THEOREM, knights.winnableProof());

    final UnwinnabilityFullAnalysis bishops = full("k7/8/8/8/8/8/8/K1B2B2 w - - 0 1", Side.WHITE);
    assertEquals(UnwinnabilityFullVerdict.WINNABLE, bishops.verdict());
    assertEquals(WinnableProof.THEOREM, bishops.winnableProof());
  }
}
