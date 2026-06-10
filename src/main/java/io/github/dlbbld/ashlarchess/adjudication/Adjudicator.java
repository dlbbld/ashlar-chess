// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.adjudication;

import io.github.dlbbld.ashlarchess.board.Board;
import io.github.dlbbld.ashlarchess.board.enums.Side;
import io.github.dlbbld.ashlarchess.unwinnability.UnwinnabilityQuickVerdict;

/**
 * Adjudicates flag-fall and resignation - the two terminations where a player loses by an external event, subject to
 * the FIDE exception that the game is instead drawn when the opponent cannot checkmate by any possible series of legal
 * moves.
 *
 * <p>
 * The result is the winning {@link Side}: the opponent of the flagging / resigning player when the loss stands, or
 * {@link Side#NONE} when the game is drawn because that opponent cannot win. (This mirrors
 * {@link io.github.dlbbld.ashlarchess.common.model.Outcome#winner()}, which uses {@link Side#NONE} for a non-decisive
 * result.)
 *
 * <p>
 * The "cannot win" exception is decided in two cheap steps, exactly as FIDE frames it - material first, then position:
 * the opponent {@linkplain Board#isInsufficientMaterial(Side) has insufficient mating material}, or the position is
 * {@linkplain Board#isUnwinnableQuick(Side) quick-unwinnable} for it (for example a blocked pawn wall). Both are
 * caller-invoked and fast; no bounded search runs here.
 */
public final class Adjudicator {

  private Adjudicator() {
  }

  /**
   * Adjudicates a flag-fall under <a href="https://handbook.fide.com/chapter/e012023">FIDE 6.9</a>: the player whose
   * flag falls loses, unless the opponent cannot checkmate by any possible series of legal moves, in which case the
   * game is drawn.
   *
   * @param board the position at flag-fall
   * @param flaggingPlayer the player whose flag fell; must be {@link Side#WHITE} or {@link Side#BLACK}
   * @return the opponent of {@code flaggingPlayer} when the flag-fall is decisive, or {@link Side#NONE} when the game
   *         is drawn because the opponent cannot win
   * @throws IllegalArgumentException if {@code flaggingPlayer} is {@link Side#NONE}
   */
  public static Side adjudicateFlagfall(Board board, Side flaggingPlayer) {
    if (flaggingPlayer == Side.NONE) {
      throw new IllegalArgumentException("flaggingPlayer must be WHITE or BLACK");
    }
    final Side wouldBeWinner = flaggingPlayer.getOppositeSide();
    if (board.isInsufficientMaterial(wouldBeWinner)) {
      return Side.NONE;
    }
    if (board.isUnwinnableQuick(wouldBeWinner) == UnwinnabilityQuickVerdict.UNWINNABLE) {
      return Side.NONE;
    }
    return wouldBeWinner;
  }

  /**
   * Adjudicates a resignation under <a href="https://handbook.fide.com/chapter/e012023">FIDE 5.1.2</a>. Resignation
   * carries the same exception as flag-fall, so this is identical to {@link #adjudicateFlagfall(Board, Side)}: the
   * resigning player loses unless the opponent cannot checkmate by any possible series of legal moves.
   *
   * @param board the position at resignation
   * @param resigningPlayer the player resigning; must be {@link Side#WHITE} or {@link Side#BLACK}
   * @return the opponent of {@code resigningPlayer} when the resignation is decisive, or {@link Side#NONE} when the
   *         game is drawn because the opponent cannot win
   * @throws IllegalArgumentException if {@code resigningPlayer} is {@link Side#NONE}
   */
  public static Side adjudicateResignation(Board board, Side resigningPlayer) {
    return adjudicateFlagfall(board, resigningPlayer);
  }
}
