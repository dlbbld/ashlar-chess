// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.test.unwinnability;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import io.github.dlbbld.ashlarchess.board.Board;
import io.github.dlbbld.ashlarchess.board.enums.Side;
import io.github.dlbbld.ashlarchess.unwinnability.UnwinnabilityFullAnalysis;
import io.github.dlbbld.ashlarchess.unwinnability.UnwinnabilityFullVerdict;
import io.github.dlbbld.ashlarchess.unwinnability.UnwinnableFullAnalyzer;

/**
 * Lock-down for the terminal-position contract of the full unwinnability analyzer, following Ambrona's CHA
 * Find-Helpmate base cases (Figure 5): an already-checkmate position is {@code WINNABLE} for the side that delivered
 * mate - a zero-move helpmate, so the mate line is empty - and {@code UNWINNABLE} for the mated side; a stalemate is
 * {@code UNWINNABLE} for both sides. Otherwise only exercised incidentally via the CHA oracle corpus.
 */
class TestUnwinnabilityTerminalPositions {

  /** Fool's mate (1. f3 e5 2. g4 Qh4#): White is to move and checkmated; Black delivered the mate. */
  private static final String FOOLS_MATE = "rnb1kbnr/pppp1ppp/8/4p3/6Pq/5P2/PPPPP2P/RNBQKBNR w KQkq - 1 3";

  /** Black to move and stalemated (black king h8; white queen f7, white king g6). */
  private static final String STALEMATE = "7k/5Q2/6K1/8/8/8/8/8 b - - 0 1";

  @SuppressWarnings("static-method")
  @Test
  void testCheckmateIsWinnableForTheMatingSideAsAZeroMoveHelpmate() {
    final Board board = Board.fromFenStrict(FOOLS_MATE);
    final UnwinnabilityFullAnalysis analysis = UnwinnableFullAnalyzer.unwinnableFull(board, Side.BLACK);
    assertEquals(UnwinnabilityFullVerdict.WINNABLE, analysis.verdict());
    assertTrue(analysis.mateLine().isEmpty(), "an already-delivered mate is a zero-move helpmate");
  }

  @SuppressWarnings("static-method")
  @Test
  void testCheckmateIsUnwinnableForTheMatedSide() {
    final Board board = Board.fromFenStrict(FOOLS_MATE);
    assertEquals(UnwinnabilityFullVerdict.UNWINNABLE,
        UnwinnableFullAnalyzer.unwinnableFull(board, Side.WHITE).verdict());
  }

  @SuppressWarnings("static-method")
  @Test
  void testStalemateIsUnwinnableForBothSides() {
    final Board board = Board.fromFenStrict(STALEMATE);
    assertEquals(UnwinnabilityFullVerdict.UNWINNABLE,
        UnwinnableFullAnalyzer.unwinnableFull(board, Side.WHITE).verdict());
    assertEquals(UnwinnabilityFullVerdict.UNWINNABLE,
        UnwinnableFullAnalyzer.unwinnableFull(board, Side.BLACK).verdict());
  }
}
