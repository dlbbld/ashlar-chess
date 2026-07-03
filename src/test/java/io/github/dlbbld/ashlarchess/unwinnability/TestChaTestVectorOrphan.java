// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.unwinnability;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;

import io.github.dlbbld.ashlarchess.board.Board;
import io.github.dlbbld.ashlarchess.board.enums.Side;

// The "orphan" cha test vector: diffing Miguel Ambrona's C++ test set (D3-Chess tests/test-vector.txt, 1798
// positions; vendored as oracle/d3chess/test-vectors.txt, last line) against his Rust successor's test set
// (chasolver tests/positions.txt, 3401 positions) leaves exactly ONE position present in the C++ suite but absent
// from the Rust one - the family siblings made it over, this exact vector did not (established 2026-07-03,
// presumably an accidental drop). Adopted here so the position stays a living test somewhere.
//
// Ground truth (label W-; both cha C++ and chasolver 3.0 engines confirm): White can helpmate, Black cannot - the
// black king is caged on f8 by Bg8/Rh8 and the f7/h7 pawns, and Black's forces can never deliver a mate.
class TestChaTestVectorOrphan {

  private static final String ORPHAN_FEN = "1b3kBR/4pP1P/1p1pP2P/1P1P4/8/K5p1/6P1/1B6 b - - 0 1";

  @SuppressWarnings("static-method")
  @Test
  void whiteCanHelpmateAndBlackCannot() {
    final Board board = Board.fromFenStrict(ORPHAN_FEN);

    final UnwinnabilityFullAnalysis whiteAnalysis = UnwinnableFullAnalyzer.unwinnableFull(board, Side.WHITE);
    assertEquals(UnwinnabilityFullVerdict.WINNABLE, whiteAnalysis.verdict());
    assertFalse(whiteAnalysis.mateLine().isEmpty(), "the win is search-proven with a witnessing helpmate line");

    // Ground truth is UNWINNABLE (cha C++ proves it via its beyond-paper semi-static extensions); the paper engine
    // soundly abstains - the Figure 8 check does not cover this cage and the search cannot exhaust it within the
    // budget. Pinned as a reverse test: if a future strengthening (fast-board pass, bigger budget, stronger
    // semi-static) resolves it, this fails and the expectation gets promoted to UNWINNABLE.
    assertEquals(UnwinnabilityFullVerdict.UNDETERMINED,
        UnwinnableFullAnalyzer.unwinnableFull(board, Side.BLACK).verdict());
  }

  @SuppressWarnings("static-method")
  @Test
  void quickStaysSound() {
    final Board board = Board.fromFenStrict(ORPHAN_FEN);
    // The quick analysis may not decide this composed cage either way, but it must never contradict the ground
    // truth: not UNWINNABLE for White (White can mate), not WINNABLE for Black (Black cannot).
    assertFalse(
        UnwinnableQuickAnalyzer.unwinnableQuick(board, Side.WHITE).verdict() == UnwinnabilityQuickVerdict.UNWINNABLE,
        "quick must not deny White's existing helpmate");
    assertFalse(
        UnwinnableQuickAnalyzer.unwinnableQuick(board, Side.BLACK).verdict() == UnwinnabilityQuickVerdict.WINNABLE,
        "quick must not claim a win for Black");
  }
}
