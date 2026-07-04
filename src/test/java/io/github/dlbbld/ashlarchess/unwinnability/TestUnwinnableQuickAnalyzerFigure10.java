// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.unwinnability;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import io.github.dlbbld.ashlarchess.board.Board;
import io.github.dlbbld.ashlarchess.board.enums.Side;

// One test per Figure 10 exit path, on hand-verified positions; ported from the fun22-reference unit tests.
class TestUnwinnableQuickAnalyzerFigure10 {

  private static UnwinnabilityQuickVerdict quick(String fen, Side winner) {
    return UnwinnableQuickAnalyzer.unwinnableQuick(Board.fromFenStrict(fen), winner).verdict();
  }

  @SuppressWarnings("static-method")
  @Test
  void deliveredCheckmateIsWinnable() {
    // Step 3 exit: Ka8 is already checkmated (Rh8 along the rank, Kb6 covering a7). Note the quick DFS hard-stops on
    // its FIRST line reaching the depth bound (Figure 10's literal interrupt - the source of its speed), so even a
    // mate-in-1 is found only if move ordering visits it before the first deep dive; a delivered mate is the
    // reliable WINNABLE fixture.
    assertEquals(UnwinnabilityQuickVerdict.WINNABLE, quick("k6R/8/1K6/8/8/8/8/8 b - - 0 1", Side.WHITE));
  }

  @SuppressWarnings("static-method")
  @Test
  void bareKingWinnerExhaustsImmediately() {
    // Step 4 exit via footnote-b leaves: the intended winner (Black) has a bare king, so the root itself is an
    // insufficient-material leaf - the "tree" is exhausted without interruption.
    assertEquals(UnwinnabilityQuickVerdict.UNWINNABLE, quick("4k3/8/8/8/8/8/1Q6/4K3 w - - 0 1", Side.BLACK));
  }

  @SuppressWarnings("static-method")
  @Test
  void blockedFortressCaughtBySemiStaticGate() {
    // Step 6 exit: only pawn/bishop/king material, no semi-open files (b, d, f, h carry both colours' pawns; a, c,
    // e, g carry none), kings sealed apart -> the semi-static check proves both sides unwinnable.
    final String fen = "2b1k3/8/8/1p1p1p1p/1P1P1P1P/8/8/2B1K3 w - - 0 1";
    assertEquals(UnwinnabilityQuickVerdict.UNWINNABLE, quick(fen, Side.WHITE));
    assertEquals(UnwinnabilityQuickVerdict.UNWINNABLE, quick(fen, Side.BLACK));
  }

  @SuppressWarnings("static-method")
  @Test
  void kingAndQueenIsPossiblyWinnableAtQuickDepth() {
    // Step 7 exit: K+Q vs K needs a deeper helpmate than the depth bound, and the queen fails the pawn/bishop/king
    // gate - the designed incompleteness of the quick routine (the full analyzer proves this WINNABLE).
    assertEquals(UnwinnabilityQuickVerdict.POSSIBLY_WINNABLE, quick("4k3/8/8/8/8/8/1Q6/4K3 w - - 0 1", Side.WHITE));
  }

  @SuppressWarnings("static-method")
  @Test
  void deadPositionWithKnightIsMissedByDesign() {
    // Step 7 exit: a dead position (D3-Chess ground truth '--') containing knights; the pawn/bishop/king gate blocks
    // the semi-static check, so quick cannot decide.
    assertEquals(UnwinnabilityQuickVerdict.POSSIBLY_WINNABLE,
        quick("8/1k5B/7b/8/1p1p1p1p/1PpP1P1P/2P3K1/N3b3 b - - 0 1", Side.WHITE));
  }
}
