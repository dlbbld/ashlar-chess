// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.unwinnability;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import io.github.dlbbld.ashlarchess.board.Board;
import io.github.dlbbld.ashlarchess.board.MoveSpecification;
import io.github.dlbbld.ashlarchess.board.enums.Side;

// Direct unit tests for the Figure 13 Going-to-corner heuristic (fun22-spec.md section 6): only slow pieces (king,
// knight) qualify, the mating corner follows the bishop square colours, and the target is rotated 180 degrees for a
// Black winner.
class TestGoingToCornerFigure13 {

  private static MoveSpecification move(Board board, String from, String to) {
    for (final MoveSpecification candidate : board.getLegalMoveSpecifications()) {
      if (candidate.fromSquare().toString().equalsIgnoreCase(from)
          && candidate.toSquare().toString().equalsIgnoreCase(to)) {
        return candidate;
      }
    }
    throw new AssertionError("no legal move " + from + "-" + to);
  }

  @SuppressWarnings("static-method")
  @Test
  void onlySlowPiecesQualify() {
    final Board board = Board.fromFenStrict("4k3/8/8/8/3R4/8/8/4K3 w - - 0 1");
    assertFalse(GoingToCorner.towardCorner(board, move(board, "d4", "a4"), Side.WHITE, true));
    assertFalse(GoingToCorner.towardCorner(board, move(board, "d4", "d8"), Side.WHITE, true));
  }

  @SuppressWarnings("static-method")
  @Test
  void kingApproachingTheCornerTargetQualifies() {
    // No bishops -> a8 corner; winner-king target a6. d4-c5 approaches (distance 3 -> 2), d4-e3 leaves.
    final Board board = Board.fromFenStrict("4k3/8/8/8/3K4/8/8/8 w - - 0 1");
    assertTrue(GoingToCorner.towardCorner(board, move(board, "d4", "c5"), Side.WHITE, true));
    assertFalse(GoingToCorner.towardCorner(board, move(board, "d4", "e3"), Side.WHITE, true));
  }

  @SuppressWarnings("static-method")
  @Test
  void knightUsesKnightDistance() {
    // No bishops -> a8 corner; winner-knight target a8. c4-b6 approaches (knight distance 2 -> 1), c4-e3 leaves.
    final Board board = Board.fromFenStrict("4k3/8/8/8/2N5/8/8/4K3 w - - 0 1");
    assertTrue(GoingToCorner.towardCorner(board, move(board, "c4", "b6"), Side.WHITE, true));
    assertFalse(GoingToCorner.towardCorner(board, move(board, "c4", "e3"), Side.WHITE, true));
  }

  @SuppressWarnings("static-method")
  @Test
  void darkSquaredWinnerBishopSwitchesToTheH8Corner() {
    // Winner has a dark-squared bishop (c1) -> h8 corner; winner-king target h6. d4-e5 approaches h6, d4-c5 leaves.
    final Board board = Board.fromFenStrict("4k3/8/8/8/3K4/8/8/2B5 w - - 0 1");
    assertTrue(GoingToCorner.towardCorner(board, move(board, "d4", "e5"), Side.WHITE, true));
    assertFalse(GoingToCorner.towardCorner(board, move(board, "d4", "c5"), Side.WHITE, true));
  }

  @SuppressWarnings("static-method")
  @Test
  void blackWinnerTargetsTheRotatedCorner() {
    // Winner is Black, no bishops -> the a8-side target rotates 180 degrees to h3. e5-f4 approaches, e5-d6 leaves.
    final Board board = Board.fromFenStrict("8/8/8/4k3/8/8/8/K7 b - - 0 1");
    assertTrue(GoingToCorner.towardCorner(board, move(board, "e5", "f4"), Side.BLACK, true));
    assertFalse(GoingToCorner.towardCorner(board, move(board, "e5", "d6"), Side.BLACK, true));
  }

  @SuppressWarnings("static-method")
  @Test
  void loserKingTargetsTheCornerItself() {
    // No bishops -> a8 corner; loser-king target a8. d7-c8 approaches, d7-e6 leaves.
    final Board board = Board.fromFenStrict("8/3k4/8/8/8/8/8/K5R1 b - - 0 1");
    assertTrue(GoingToCorner.towardCorner(board, move(board, "d7", "c8"), Side.WHITE, false));
    assertFalse(GoingToCorner.towardCorner(board, move(board, "d7", "e6"), Side.WHITE, false));
  }
}
