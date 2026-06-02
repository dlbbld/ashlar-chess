// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.unwinnability;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import io.github.dlbbld.ashlarchess.board.Board;
import io.github.dlbbld.ashlarchess.board.enums.PromotionPieceType;
import io.github.dlbbld.ashlarchess.board.enums.Side;
import io.github.dlbbld.ashlarchess.board.enums.Square;
import io.github.dlbbld.ashlarchess.common.constants.EnumConstants;
import io.github.dlbbld.ashlarchess.model.LegalMove;
import io.github.dlbbld.ashlarchess.model.LegalMoveKind;

// Spec test for FUN22 Figure 12 (Score). Score is a pure function of (position, move, intended-winner); it does NOT
// depend on move ordering, so positions alone suffice. Expected values are derived from Figure 12. The two methods
// suffixed _deviates / _followsChaNotPdf pin a deviation from the PDF (see notes on each); they assert current behaviour
// so they stay green and will flag if the implementation changes.
class TestScore implements EnumConstants {

  // ----- Intended winner to move (Figure 12, steps 1-3): capture / advanced pawn push / going-to-corner -> Reward. -----

  @SuppressWarnings("static-method")
  @Test
  void winnerCaptureIsReward() {
    // Ra1xa8.
    assertEquals(ScoreResult.REWARD, score(Side.WHITE, new Board("r3k3/8/8/8/8/8/8/R3K3 w - - 0 1"), A1, A8));
  }

  @SuppressWarnings("static-method")
  @Test
  void winnerAdvancedPawnPushIsReward() {
    // a6-a7 (CHA uses advanced_pawn_push, i.e. ranks 6-8 for White, matching the PDF's "pawn push" here).
    assertEquals(ScoreResult.REWARD, score(Side.WHITE, new Board("4k3/8/P7/8/8/8/8/4K3 w - - 0 1"), A6, A7));
  }

  @SuppressWarnings("static-method")
  @Test
  void winnerKingTowardCornerIsReward() {
    // e1-d2 steps toward the light corner a6.
    assertEquals(ScoreResult.REWARD, score(Side.WHITE, new Board("4k3/8/8/8/8/8/8/R3K3 w - - 0 1"), E1, D2));
  }

  @SuppressWarnings("static-method")
  @Test
  void winnerQuietMoveIsNormal() {
    // e1-f1: not a capture, not a pawn push, not toward the corner.
    assertEquals(ScoreResult.NORMAL, score(Side.WHITE, new Board("4k3/8/8/8/8/8/8/R3K3 w - - 0 1"), E1, F1));
  }

  // ----- Intended loser to move (Figure 12, steps 4-10). -----

  @SuppressWarnings("static-method")
  @Test
  void loserGoingToCornerIsReward() {
    // Black (loser) e8-d7 steps toward a8; winner White has a rook so the promotion clause does not apply.
    assertEquals(ScoreResult.REWARD, score(Side.WHITE, new Board("4k3/8/8/8/8/8/8/R3K3 b - - 0 1"), E8, D7));
  }

  @SuppressWarnings("static-method")
  @Test
  void loserCaptureAwayFromCornerIsPunish() {
    // Black (loser) Kxf5 captures the rook moving away from a8 -> step 9 Punish.
    assertEquals(ScoreResult.PUNISH, score(Side.WHITE, new Board("8/8/8/5R2/4k3/8/8/4K3 b - - 0 1"), E4, F5));
  }

  @SuppressWarnings("static-method")
  @Test
  void loserMustPromoteHeavyPromotionIsPunish() {
    // Winner has only a knight, loser only pawns -> need-loser-promotion. a1=Q (heavy) -> step 6 Punish.
    assertEquals(ScoreResult.PUNISH,
        scorePromotion(Side.WHITE, new Board("4k3/8/8/8/7p/8/p7/1N2K3 b - - 0 1"), A2, A1, PromotionPieceType.QUEEN));
  }

  @SuppressWarnings("static-method")
  @Test
  void loserMustPromotePawnPushIsReward() {
    // Same material: a non-promoting pawn push h4-h3 -> step 7 Reward.
    assertEquals(ScoreResult.REWARD, score(Side.WHITE, new Board("4k3/8/8/8/7p/8/p7/1N2K3 b - - 0 1"), H4, H3));
  }

  // ----- Going-to-corner vs capture precedence, and need-loser-promotion interactions (CHA 2.6.1 values). -----

  // Corner wins over capture: a loser move that is BOTH going-to-corner AND a capture scores Reward, because CHA uses
  // `if going_to_square ... else if capture ...` (the PDF agrees via its step-8-before-step-9 early returns). Kxd5
  // captures the rook while stepping toward a8.
  @SuppressWarnings("static-method")
  @Test
  void loserGoingToCornerAndCaptureIsReward() {
    assertEquals(ScoreResult.REWARD, score(Side.WHITE, new Board("8/8/8/3R4/4k3/8/8/4K3 b - - 0 1"), E4, D5));
  }

  // Need-loser-promotion sets a base (Punish for a non-pawn move), but going-to-corner still overrides it: a loser
  // king walking to the corner scores Reward.
  @SuppressWarnings("static-method")
  @Test
  void loserMustPromoteKingTowardCornerIsReward() {
    assertEquals(ScoreResult.REWARD, score(Side.WHITE, new Board("4k3/7p/8/8/8/8/8/1N2K3 b - - 0 1"), E8, D7));
  }

  // Need-loser-promotion: a pawn CAPTURE. CHA's base Reward (pawn) is overridden to Punish by the capture branch.
  // Deviates from the PDF-literal step 7 (Reward for any pawn move); we follow CHA. b4xc3 takes the knight.
  @SuppressWarnings("static-method")
  @Test
  void loserMustPromotePawnCaptureIsPunish_followsChaNotPdf() {
    assertEquals(ScoreResult.PUNISH, score(Side.WHITE, new Board("4k3/8/8/8/1p6/2N5/8/4K3 b - - 0 1"), B4, C3));
  }

  // Need-loser-promotion: a quiet non-pawn move keeps the base Punish (no corner, no capture). Deviates from the
  // PDF-literal, which falls through to Normal; we follow CHA.
  @SuppressWarnings("static-method")
  @Test
  void loserMustPromoteQuietKingMoveIsPunish_followsChaNotPdf() {
    assertEquals(ScoreResult.PUNISH, score(Side.WHITE, new Board("4k3/8/8/8/8/8/p7/1N2K3 b - - 0 1"), E8, E7));
  }

  // No need-loser-promotion: a quiet loser move (no corner, no capture) keeps the base Normal.
  @SuppressWarnings("static-method")
  @Test
  void loserQuietMoveWithoutPromotionPressureIsNormal() {
    assertEquals(ScoreResult.NORMAL, score(Side.WHITE, new Board("4k3/8/8/8/8/8/8/R3K3 b - - 0 1"), E8, F8));
  }

  private static ScoreResult score(Side winner, Board board, Square from, Square to) {
    return Score.score(winner, board.getHavingMove(), board.getBitboardPosition(), move(board, from, to));
  }

  private static ScoreResult scorePromotion(Side winner, Board board, Square from, Square to, PromotionPieceType promo) {
    return Score.score(winner, board.getHavingMove(), board.getBitboardPosition(), promotion(board, from, to, promo));
  }

  private static LegalMove move(Board board, Square from, Square to) {
    for (final LegalMove legalMove : board.getLegalMoves()) {
      if (legalMove.kind() != LegalMoveKind.CASTLING && legalMove.moveSpecification().fromSquare() == from
          && legalMove.moveSpecification().toSquare() == to
          && legalMove.moveSpecification().promotionPieceType() == PromotionPieceType.NONE) {
        return legalMove;
      }
    }
    throw new IllegalStateException("No legal move " + from + " -> " + to);
  }

  private static LegalMove promotion(Board board, Square from, Square to, PromotionPieceType promo) {
    for (final LegalMove legalMove : board.getLegalMoves()) {
      if (legalMove.moveSpecification().fromSquare() == from && legalMove.moveSpecification().toSquare() == to
          && legalMove.moveSpecification().promotionPieceType() == promo) {
        return legalMove;
      }
    }
    throw new IllegalStateException("No promotion " + from + " -> " + to + " = " + promo);
  }
}
