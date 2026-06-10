// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.adjudication;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import io.github.dlbbld.ashlarchess.board.Board;
import io.github.dlbbld.ashlarchess.board.enums.Side;

/**
 * Flag-fall / resignation adjudication, quick and full. The quick variant rules only DRAW / LOSS; the full variant adds
 * UNDETERMINED. Positions are the README examples plus the one corpus position whose full analysis exhausts the node
 * bound; their verdicts are pinned elsewhere.
 */
class TestAdjudicator {

  // White: king + rook; Black: lone king. Black cannot mate with the king alone (insufficient material).
  private static final String KING_AND_ROOK_VS_KING = "8/8/4k3/3R4/2K5/8/8/8 w - - 0 50";

  // Blocked pawn wall: provably unwinnable for Black (quick and full), but Black has pawns so it is not material-only.
  private static final String PAWN_WALL_UNWINNABLE_FOR_BLACK = "8/8/3k4/1p2p1p1/pP1pP1P1/P2P4/1K6/8 b - - 32 62";

  // Many pieces: White is possibly winnable (quick) / proven winnable (full), so a Black flag is a real loss.
  private static final String WHITE_WINNABLE = "q4r2/pR3pkp/1p2p1p1/4P3/6P1/1P3Q2/1Pr2PK1/3R4 b - - 3 29";

  // The one corpus position whose full analysis exhausts the 500k-node bound for Black: full -> UNDETERMINED.
  private static final String UNDETERMINED_FOR_BLACK = "2b5/1p6/pPp3k1/2Pp3p/P2PpBpP/4P1P1/5K2/8 b - - 46 59";

  // === quick: DRAW / LOSS only ===

  @SuppressWarnings("static-method")
  @Test
  void quickDrawsWhenOpponentIsProvablyUnwinnable() {
    // White flags; the would-be winner (Black) is provably unwinnable - lone king, and a blocked pawn wall.
    assertEquals(AdjudicationResult.DRAW,
        Adjudicator.adjudicateFlagfallQuick(new Board(KING_AND_ROOK_VS_KING), Side.WHITE));
    assertEquals(AdjudicationResult.DRAW,
        Adjudicator.adjudicateFlagfallQuick(new Board(PAWN_WALL_UNWINNABLE_FOR_BLACK), Side.WHITE));
  }

  @SuppressWarnings("static-method")
  @Test
  void quickRulesLossWhenNoDrawCanBeShown() {
    // Black flags; White is not provably unwinnable -> the flag stands.
    assertEquals(AdjudicationResult.LOSS, Adjudicator.adjudicateFlagfallQuick(new Board(WHITE_WINNABLE), Side.BLACK));
    // White flags; quick cannot prove Black unwinnable here (it is actually winnable) -> loss.
    assertEquals(AdjudicationResult.LOSS,
        Adjudicator.adjudicateFlagfallQuick(new Board(UNDETERMINED_FOR_BLACK), Side.WHITE));
  }

  // === full: DRAW / LOSS / UNDETERMINED ===

  @SuppressWarnings("static-method")
  @Test
  void fullDrawsOnAProvenDeadPosition() {
    assertEquals(AdjudicationResult.DRAW,
        Adjudicator.adjudicateFlagfallFull(new Board(KING_AND_ROOK_VS_KING), Side.WHITE));
    assertEquals(AdjudicationResult.DRAW,
        Adjudicator.adjudicateFlagfallFull(new Board(PAWN_WALL_UNWINNABLE_FOR_BLACK), Side.WHITE));
  }

  @SuppressWarnings("static-method")
  @Test
  void fullRulesLossOnAProvenWin() {
    // Black flags; the full analysis proves White can win -> Black loses.
    assertEquals(AdjudicationResult.LOSS, Adjudicator.adjudicateFlagfallFull(new Board(WHITE_WINNABLE), Side.BLACK));
  }

  @SuppressWarnings("static-method")
  @Test
  void fullReportsUndeterminedWhenTheSearchBoundIsHit() {
    // White flags; the full analysis of Black exhausts the node bound -> undetermined.
    assertEquals(AdjudicationResult.UNDETERMINED,
        Adjudicator.adjudicateFlagfallFull(new Board(UNDETERMINED_FOR_BLACK), Side.WHITE));
  }

  @SuppressWarnings("static-method")
  @Test
  void quickAndFullDifferOnTheUndeterminedPosition() {
    // Same position: quick rules a loss (no draw shown), full reports it cannot decide.
    assertEquals(AdjudicationResult.LOSS,
        Adjudicator.adjudicateFlagfallQuick(new Board(UNDETERMINED_FOR_BLACK), Side.WHITE));
    assertEquals(AdjudicationResult.UNDETERMINED,
        Adjudicator.adjudicateFlagfallFull(new Board(UNDETERMINED_FOR_BLACK), Side.WHITE));
  }

  // === resignation == flag-fall ===

  @SuppressWarnings("static-method")
  @Test
  void resignationAdjudicatesIdenticallyToFlagfall() {
    final Board drawBoard = new Board(KING_AND_ROOK_VS_KING);
    assertEquals(AdjudicationResult.DRAW, Adjudicator.adjudicateResignationQuick(drawBoard, Side.WHITE));
    assertEquals(AdjudicationResult.DRAW, Adjudicator.adjudicateResignationFull(drawBoard, Side.WHITE));
    final Board lossBoard = new Board(WHITE_WINNABLE);
    assertEquals(AdjudicationResult.LOSS, Adjudicator.adjudicateResignationQuick(lossBoard, Side.BLACK));
    assertEquals(AdjudicationResult.LOSS, Adjudicator.adjudicateResignationFull(lossBoard, Side.BLACK));
  }

  // === validation ===

  @SuppressWarnings("static-method")
  @Test
  void rejectsSideNone() {
    final Board board = new Board(WHITE_WINNABLE);
    assertThrows(IllegalArgumentException.class, () -> Adjudicator.adjudicateFlagfallQuick(board, Side.NONE));
    assertThrows(IllegalArgumentException.class, () -> Adjudicator.adjudicateResignationQuick(board, Side.NONE));
    assertThrows(IllegalArgumentException.class, () -> Adjudicator.adjudicateFlagfallFull(board, Side.NONE));
    assertThrows(IllegalArgumentException.class, () -> Adjudicator.adjudicateResignationFull(board, Side.NONE));
  }
}
