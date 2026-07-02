// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.unwinnability;

import io.github.dlbbld.ashlarchess.board.enums.PieceType;
import io.github.dlbbld.ashlarchess.board.enums.Side;
import io.github.dlbbld.ashlarchess.exceptions.ProgrammingMistakeException;

/**
 * The paper's {@code predP(s)} and {@code pred-captP(s)} sets ({@code fun22-spec.md} section 1.1), shared by
 * {@link Mobility} (Figure 6/7) and {@link UnwinnableSemiStatic} (Figure 8's {@code att-region}). Pure geometry keyed
 * by piece type/side and target square. Sliders use only the adjacent squares (&beta;/&alpha;/&delta;); long slides
 * emerge stepwise from the Figure 7 fixpoint.
 */
final class Predecessors {

  private Predecessors() {
  }

  /** predP(s): squares from which the piece reaches {@code s} in one non-capture move. */
  static long moves(PieceType pieceType, Side side, int s) {
    return switch (pieceType) {
      case KNIGHT -> SquareGeometry.knight(s);
      case BISHOP -> SquareGeometry.beta(s);
      case ROOK -> SquareGeometry.alpha(s);
      case QUEEN, KING -> SquareGeometry.delta(s);
      case PAWN -> SquareGeometry.pawnPushPredecessors(side, s);
      case NONE -> throw new ProgrammingMistakeException("No predecessors for the non piece type");
    };
  }

  /** pred-captP(s): squares from which the piece captures onto {@code s}. */
  static long captures(PieceType pieceType, Side side, int s) {
    if (pieceType == PieceType.PAWN) {
      return SquareGeometry.pawnAttackPredecessors(side, s);
    }
    return moves(pieceType, side, s);
  }
}
