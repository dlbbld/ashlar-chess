// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.adjudication;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import io.github.dlbbld.ashlarchess.board.Board;
import io.github.dlbbld.ashlarchess.board.enums.Side;

/**
 * Flag-fall / resignation adjudication across the three branches of the FIDE 6.9 / 5.1.2 procedure: a draw because the
 * opponent has insufficient material, a draw because the opponent is quick-unwinnable (a position-wise dead position
 * that is not material-only), and a decisive loss when the opponent can still win. Positions are the README examples,
 * whose verdicts are pinned elsewhere.
 */
class TestAdjudicator {

  // White: king + rook; Black: lone king. Black cannot mate with the king alone (insufficient material).
  private static final String KING_AND_ROOK_VS_KING = "8/8/4k3/3R4/2K5/8/8/8 w - - 0 50";

  // Blocked pawn wall: unwinnable for Black, but Black has pawns so it is not a material-only draw.
  private static final String PAWN_WALL_UNWINNABLE_FOR_BLACK = "8/8/3k4/1p2p1p1/pP1pP1P1/P2P4/1K6/8 b - - 32 62";

  // Many pieces on the board: White is possibly winnable, so a Black flag-fall is a real loss.
  private static final String WHITE_POSSIBLY_WINNABLE = "q4r2/pR3pkp/1p2p1p1/4P3/6P1/1P3Q2/1Pr2PK1/3R4 b - - 3 29";

  @SuppressWarnings("static-method")
  @Test
  void flagfallDrawWhenOpponentHasInsufficientMaterial() {
    final Board board = new Board(KING_AND_ROOK_VS_KING);
    // White flags; the would-be winner is Black, who has only the king - insufficient material, so a draw.
    assertEquals(Side.NONE, Adjudicator.adjudicateFlagfall(board, Side.WHITE));
  }

  @SuppressWarnings("static-method")
  @Test
  void flagfallDrawWhenOpponentIsQuickUnwinnable() {
    final Board board = new Board(PAWN_WALL_UNWINNABLE_FOR_BLACK);
    // White flags; the would-be winner is Black, who has pawns (not material-insufficient) but is quick-unwinnable.
    assertEquals(Side.NONE, Adjudicator.adjudicateFlagfall(board, Side.WHITE));
  }

  @SuppressWarnings("static-method")
  @Test
  void flagfallLossWhenOpponentCanWin() {
    final Board board = new Board(WHITE_POSSIBLY_WINNABLE);
    // Black flags; the would-be winner is White, who is possibly winnable - so Black loses and White wins.
    assertEquals(Side.WHITE, Adjudicator.adjudicateFlagfall(board, Side.BLACK));
  }

  @SuppressWarnings("static-method")
  @Test
  void flagfallLossWhenWouldBeWinnerIsKingAndRook() {
    final Board board = new Board(KING_AND_ROOK_VS_KING);
    // Black flags; the would-be winner is White (king + rook), who can mate a lone king - so White wins.
    assertEquals(Side.WHITE, Adjudicator.adjudicateFlagfall(board, Side.BLACK));
  }

  @SuppressWarnings("static-method")
  @Test
  void resignationAdjudicatesIdenticallyToFlagfall() {
    final Board board = new Board(WHITE_POSSIBLY_WINNABLE);
    assertEquals(Adjudicator.adjudicateFlagfall(board, Side.BLACK),
        Adjudicator.adjudicateResignation(board, Side.BLACK));
    final Board drawBoard = new Board(KING_AND_ROOK_VS_KING);
    assertEquals(Side.NONE, Adjudicator.adjudicateResignation(drawBoard, Side.WHITE));
  }

  @SuppressWarnings("static-method")
  @Test
  void rejectsSideNone() {
    final Board board = new Board(WHITE_POSSIBLY_WINNABLE);
    assertThrows(IllegalArgumentException.class, () -> Adjudicator.adjudicateFlagfall(board, Side.NONE));
    assertThrows(IllegalArgumentException.class, () -> Adjudicator.adjudicateResignation(board, Side.NONE));
  }
}
