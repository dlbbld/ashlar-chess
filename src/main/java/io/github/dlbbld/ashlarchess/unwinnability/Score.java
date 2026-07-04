// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.unwinnability;

import io.github.dlbbld.ashlarchess.bitboard.BitboardPosition;
import io.github.dlbbld.ashlarchess.board.MoveSpecification;
import io.github.dlbbld.ashlarchess.board.enums.Piece;
import io.github.dlbbld.ashlarchess.board.enums.PieceType;
import io.github.dlbbld.ashlarchess.board.enums.PromotionPieceType;
import io.github.dlbbld.ashlarchess.board.enums.Side;
import io.github.dlbbld.ashlarchess.board.enums.Square;

// Figure 12 Score of a move, used to adjust the depth budget of the Find-Helpmate search.
/**
 * The Score heuristic (paper Figure 12): classifies a move as Normal (0), Reward (+1) or Punish (-2), used to adjust
 * the search depth budget in {@link FindHelpmate}. This is a pure <em>efficiency</em> heuristic - soundness and
 * completeness do not depend on it.
 *
 * <p>
 * Full fidelity to Figure 12, including the {@code Going-to-corner} clauses (lines 2 and 8) via {@link GoingToCorner}
 * (Figure 13).
 */
final class Score {

  private Score() {
  }

  /** Depth increment for exploring {@code move}: 0 (Normal), +1 (Reward), -2 (Punish). */
  static int increment(BitboardPosition position, Side sideToMove, MoveSpecification move, Side winner) {
    final boolean winnerTurn = sideToMove == winner;

    if (move.isCastling()) {
      return 0; // neither a capture nor a pawn move
    }

    final Square from = move.fromSquare();
    final Square to = move.toSquare();
    final boolean isPawnMove = position.get(from).getPieceType() == PieceType.PAWN;
    final boolean sameFile = (from.ordinal() & 7) == (to.ordinal() & 7);
    final boolean isCapture = isPawnMove ? !sameFile : position.get(to) != Piece.NONE;
    final boolean isPawnPush = isPawnMove && sameFile;

    if (winnerTurn) {
      // Figure 12 line 2: capture OR pawn push OR Going-to-corner(Win) -> Reward.
      return isCapture || isPawnPush || GoingToCorner.towardCorner(position, move, winner, true) ? 1 : 0;
    }

    // Intended loser's turn.
    if (MaterialLemmas.scoreMaterialCondition(position, winner)) {
      if (move.isPromotion()) {
        final PromotionPieceType promotion = move.promotionPieceType();
        if (promotion == PromotionPieceType.QUEEN || promotion == PromotionPieceType.ROOK) {
          return -2; // line 6: promotion to queen or rook -> Punish
        }
      }
      if (isPawnMove) {
        return 1; // line 7: any (other) pawn move -> Reward
      }
    }
    if (GoingToCorner.towardCorner(position, move, winner, false)) {
      return 1; // line 8: Going-to-corner(Lose) -> Reward
    }
    if (isCapture) {
      return -2; // line 9: capture -> Punish
    }
    return 0; // line 10: Normal
  }
}
