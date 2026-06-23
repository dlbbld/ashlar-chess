// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.unwinnability;

import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.A1;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.A4;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.A8;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.B6;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.D2;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.D7;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.E1;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.E8;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.F1;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.F2;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.G3;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.H1;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import io.github.dlbbld.ashlarchess.board.Board;
import io.github.dlbbld.ashlarchess.board.LegalMove;
import io.github.dlbbld.ashlarchess.board.LegalMoveKind;
import io.github.dlbbld.ashlarchess.board.enums.PromotionPieceType;
import io.github.dlbbld.ashlarchess.board.enums.Side;
import io.github.dlbbld.ashlarchess.board.enums.Square;

// Spec test for FUN22 Figure 13 (Going-to-corner). It is a pure function of (position, move, goal); it does NOT depend
// on move ordering (that only affects the Figure 5 search traversal), so positions alone are enough. Expected values
// are derived directly from Figure 13.
class TestGoingToCorner {

  // 2: a non-king, non-knight move is never going to the corner.
  @SuppressWarnings("static-method")
  @Test
  void nonKingNonKnightMoveIsNever() {
    final Board board = Board.fromFenStrict("r3k3/8/8/8/8/8/8/R3K3 w - - 0 1");
    assertFalse(corner(Side.WHITE, board, A1, A4, Goal.WIN), "rook move");
  }

  // 9: King, default (light) corner. Winner White, no bishops -> light corner; winner-king target (Win) is a6.
  @SuppressWarnings("static-method")
  @Test
  void winnerKingTowardLightCorner() {
    final Board board = Board.fromFenStrict("4k3/8/8/8/8/8/8/R3K3 w - - 0 1");
    assertTrue(corner(Side.WHITE, board, E1, D2, Goal.WIN), "e1-d2 steps toward a6");
    assertFalse(corner(Side.WHITE, board, E1, F1, Goal.WIN), "e1-f1 steps away from a6");
  }

  // 3-6: corner colour depends on bishops. The same move e1-f2 goes toward h6 (dark corner) only when the winner has a
  // dark-squared bishop; with no bishop it targets the light corner a6 and e1-f2 is not progress.
  @SuppressWarnings("static-method")
  @Test
  void cornerColourDependsOnBishops() {
    final Board light = Board.fromFenStrict("4k3/8/8/8/8/8/8/R3K3 w - - 0 1");
    assertFalse(corner(Side.WHITE, light, E1, F2, Goal.WIN), "light corner a6: e1-f2 not progress");

    final Board dark = Board.fromFenStrict("4k3/8/8/8/8/8/8/2B1K3 w - - 0 1");
    assertTrue(corner(Side.WHITE, dark, E1, F2, Goal.WIN), "dark-bishop -> h6: e1-f2 is progress");
  }

  // 6: loser king, light corner. Loser-king target (Lose) is a8.
  @SuppressWarnings("static-method")
  @Test
  void loserKingTowardCorner() {
    final Board board = Board.fromFenStrict("4k3/8/8/8/8/8/8/R3K3 b - - 0 1");
    assertTrue(corner(Side.WHITE, board, E8, D7, Goal.LOSE), "e8-d7 steps toward a8");
  }

  // 10: knight. Landing on the target corner (a8) is distance 0, the minimum, so it is always progress; leaving it
  // never
  // is. (Avoids hand-computing the irregular knight-distance table.)
  @SuppressWarnings("static-method")
  @Test
  void knightOntoAndOffCorner() {
    final Board onto = Board.fromFenStrict("8/8/1N6/8/4k3/8/8/4K3 w - - 0 1");
    assertTrue(corner(Side.WHITE, onto, B6, A8, Goal.WIN), "Nb6-a8 lands on a8");

    final Board off = Board.fromFenStrict("N7/8/8/8/4k3/8/8/4K3 w - - 0 1");
    assertFalse(corner(Side.WHITE, off, A8, B6, Goal.WIN), "Na8-b6 leaves a8");
  }

  // 7-8: Black winner flips the target across the centre, so the light corner a8 becomes h1. Ng3-h1 lands on h1 (the
  // flipped target); without the flip it would be moving to the far corner and would not be progress.
  @SuppressWarnings("static-method")
  @Test
  void blackWinnerTargetIsFlipped() {
    final Board board = Board.fromFenStrict("k7/8/8/4K3/8/6n1/8/8 b - - 0 1");
    assertTrue(corner(Side.BLACK, board, G3, H1, Goal.WIN), "Black winner: target flips to h1");
  }

  private static boolean corner(Side winner, Board board, Square from, Square to, Goal goal) {
    return GoingToCorner.goingToCorner(winner, board.getBitboardPosition(), move(board, from, to), goal);
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
}
