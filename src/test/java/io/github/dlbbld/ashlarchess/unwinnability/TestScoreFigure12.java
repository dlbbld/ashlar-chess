// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.unwinnability;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import io.github.dlbbld.ashlarchess.board.Board;
import io.github.dlbbld.ashlarchess.board.MoveSpecification;
import io.github.dlbbld.ashlarchess.board.enums.PromotionPieceType;
import io.github.dlbbld.ashlarchess.board.enums.Side;

// Direct unit tests for the Figure 12 Score heuristic (fun22-spec.md section 6), one case per figure line. The
// heuristic is efficiency-only, but its increments shape which helpmates the bounded search reaches, so the figure
// lines are pinned here rather than only observed through end-to-end verdicts.
class TestScoreFigure12 {

  private static MoveSpecification move(Board board, String from, String to) {
    for (final MoveSpecification candidate : board.getLegalMoveSpecifications()) {
      if (candidate.fromSquare().toString().equalsIgnoreCase(from)
          && candidate.toSquare().toString().equalsIgnoreCase(to) && !candidate.isPromotion()) {
        return candidate;
      }
    }
    throw new AssertionError("no legal move " + from + "-" + to);
  }

  private static MoveSpecification promotion(Board board, String from, String to, PromotionPieceType promotionPiece) {
    for (final MoveSpecification candidate : board.getLegalMoveSpecifications()) {
      if (candidate.fromSquare().toString().equalsIgnoreCase(from)
          && candidate.toSquare().toString().equalsIgnoreCase(to) && candidate.isPromotion()
          && candidate.promotionPieceType() == promotionPiece) {
        return candidate;
      }
    }
    throw new AssertionError("no legal promotion " + from + "-" + to + "=" + promotionPiece);
  }

  // ----- Winner's turn (Figure 12 lines 1-3). -----

  @SuppressWarnings("static-method")
  @Test
  void winnerCaptureIsReward() {
    final Board board = Board.fromFenStrict("4k3/8/8/4p3/4R3/8/8/4K3 w - - 0 1");
    assertEquals(1, Score.increment(board.getBitboardPosition(), board.getSideToMove(), move(board, "e4", "e5"), Side.WHITE));
  }

  @SuppressWarnings("static-method")
  @Test
  void winnerPawnPushIsReward() {
    final Board board = Board.fromFenStrict("4k3/8/8/8/8/8/4P3/4K3 w - - 0 1");
    assertEquals(1, Score.increment(board.getBitboardPosition(), board.getSideToMove(), move(board, "e2", "e3"), Side.WHITE));
    assertEquals(1, Score.increment(board.getBitboardPosition(), board.getSideToMove(), move(board, "e2", "e4"), Side.WHITE));
  }

  @SuppressWarnings("static-method")
  @Test
  void winnerKingTowardCornerIsReward() {
    // No bishops on the board -> the a8 corner branch; the winner-king target is a6. d4-c5 approaches, d4-e3 leaves.
    final Board board = Board.fromFenStrict("4k3/8/8/8/3K4/8/8/8 w - - 0 1");
    assertEquals(1, Score.increment(board.getBitboardPosition(), board.getSideToMove(), move(board, "d4", "c5"), Side.WHITE));
    assertEquals(0, Score.increment(board.getBitboardPosition(), board.getSideToMove(), move(board, "d4", "e3"), Side.WHITE));
  }

  @SuppressWarnings("static-method")
  @Test
  void winnerPlainPieceMoveIsNormal() {
    final Board board = Board.fromFenStrict("4k3/8/8/8/3R4/8/8/4K3 w - - 0 1");
    assertEquals(0, Score.increment(board.getBitboardPosition(), board.getSideToMove(), move(board, "d4", "a4"), Side.WHITE));
  }

  // ----- Intended loser's turn (Figure 12 lines 5-10). -----

  @SuppressWarnings("static-method")
  @Test
  void loserPromotionToQueenOrRookIsPunishUnderTheMaterialCondition() {
    // Winner (White) has just a knight; the loser has no knight/bishop/rook -> the material condition holds even
    // though the loser still has a pawn (Figure 12 ignores the Lemma 5/6 pawn-freeness requirement).
    final Board board = Board.fromFenStrict("4k3/8/8/8/8/8/2N1K1p1/8 b - - 0 1");
    assertEquals(-2, Score.increment(board.getBitboardPosition(), board.getSideToMove(), promotion(board, "g2", "g1", PromotionPieceType.QUEEN), Side.WHITE));
    assertEquals(-2, Score.increment(board.getBitboardPosition(), board.getSideToMove(), promotion(board, "g2", "g1", PromotionPieceType.ROOK), Side.WHITE));
    // Line 7: any other pawn move - including an underpromotion - is a Reward.
    assertEquals(1, Score.increment(board.getBitboardPosition(), board.getSideToMove(), promotion(board, "g2", "g1", PromotionPieceType.KNIGHT), Side.WHITE));
    assertEquals(1, Score.increment(board.getBitboardPosition(), board.getSideToMove(), promotion(board, "g2", "g1", PromotionPieceType.BISHOP), Side.WHITE));
  }

  @SuppressWarnings("static-method")
  @Test
  void loserPawnMoveIsRewardUnderTheMaterialCondition() {
    final Board board = Board.fromFenStrict("4k3/8/8/8/6p1/8/2N1K3/8 b - - 0 1");
    assertEquals(1, Score.increment(board.getBitboardPosition(), board.getSideToMove(), move(board, "g4", "g3"), Side.WHITE));
  }

  @SuppressWarnings("static-method")
  @Test
  void loserCaptureIsPunish() {
    // No material condition (winner has a rook); the loser's capture destroys mating material.
    final Board board = Board.fromFenStrict("4k3/8/8/3p4/4R3/8/8/K7 b - - 0 1");
    assertEquals(-2, Score.increment(board.getBitboardPosition(), board.getSideToMove(), move(board, "d5", "e4"), Side.WHITE));
  }

  @SuppressWarnings("static-method")
  @Test
  void loserKingTowardCornerIsReward() {
    // No bishops -> a8 corner branch; the loser-king target is a8 itself. d7-c8 approaches, d7-e6 leaves.
    final Board board = Board.fromFenStrict("8/3k4/8/8/8/8/8/K5R1 b - - 0 1");
    assertEquals(1, Score.increment(board.getBitboardPosition(), board.getSideToMove(), move(board, "d7", "c8"), Side.WHITE));
    assertEquals(0, Score.increment(board.getBitboardPosition(), board.getSideToMove(), move(board, "d7", "e6"), Side.WHITE));
  }

  @SuppressWarnings("static-method")
  @Test
  void castlingIsNormal() {
    final Board board = Board.fromFenStrict("4k3/8/8/8/8/8/8/4K2R w K - 0 1");
    for (final MoveSpecification candidate : board.getLegalMoveSpecifications()) {
      if (candidate.isCastling()) {
        assertEquals(0, Score.increment(board.getBitboardPosition(), board.getSideToMove(), candidate, Side.WHITE));
        return;
      }
    }
    throw new AssertionError("no castling move found");
  }
}
