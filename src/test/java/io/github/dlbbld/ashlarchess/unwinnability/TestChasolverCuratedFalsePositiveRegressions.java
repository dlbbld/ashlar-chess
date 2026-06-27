// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.unwinnability;

import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;

import io.github.dlbbld.ashlarchess.board.Board;
import io.github.dlbbld.ashlarchess.board.enums.Side;

class TestChasolverCuratedFalsePositiveRegressions {

  @SuppressWarnings("static-method")
  @Test
  void fullDoesNotOverclaimUnwinnabilityForCuratedWinnablePositions() {
    assertFullDoesNotOverclaim(Side.WHITE, "8/1p2p1p1/1P2P1P1/Bp2pBp1/1P2P1P1/7k/1P2P3/7K b - -");
    assertFullDoesNotOverclaim(Side.WHITE, "k7/1pK3p1/6Pb/1p4pB/bP4P1/Bp6/1P4P1/8 b - -");
    assertFullDoesNotOverclaim(Side.BLACK, "k7/1pK3p1/6Pb/1p4pB/bP4P1/Bp6/1P4P1/8 b - -");
  }

  @SuppressWarnings("static-method")
  @Test
  void quickDoesNotOverclaimUnwinnabilityForCuratedWinnablePositions() {
    // Pawn-net helpmates: the semi-static pawn-intruder shortcut used to over-claim UNWINNABLE here; the intruders fix
    // (allowing limited pawns as visitors when the loser king's region spans more than one square) removes the
    // over-claim.
    assertQuickDoesNotOverclaim(Side.WHITE, "8/1p2p1p1/1P2P1P1/Bp2pBp1/1P2P1P1/7k/1P2P3/7K b - -");
    assertQuickDoesNotOverclaim(Side.WHITE, "k7/1pK3p1/6Pb/1p4pB/bP4P1/Bp6/1P4P1/8 b - -");
    assertQuickDoesNotOverclaim(Side.BLACK, "k7/1pK3p1/6Pb/1p4pB/bP4P1/Bp6/1P4P1/8 b - -");
  }

  private static void assertFullDoesNotOverclaim(Side intendedWinner, String fen) {
    final Board board = Board.fromFenLenient(fen);
    assertNotEquals(UnwinnabilityFullVerdict.UNWINNABLE,
        UnwinnableFullAnalyzer.unwinnableFull(board, intendedWinner).verdict(), fen);
  }

  private static void assertQuickDoesNotOverclaim(Side intendedWinner, String fen) {
    final Board board = Board.fromFenLenient(fen);
    assertNotEquals(UnwinnabilityQuickVerdict.UNWINNABLE,
        UnwinnableQuickAnalyzer.unwinnableQuick(board, intendedWinner).verdict(), fen);
  }
}
