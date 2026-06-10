// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.adjudication;

import io.github.dlbbld.ashlarchess.board.Board;
import io.github.dlbbld.ashlarchess.board.enums.Side;
import io.github.dlbbld.ashlarchess.unwinnability.UnwinnabilityFullVerdict;
import io.github.dlbbld.ashlarchess.unwinnability.UnwinnabilityQuickVerdict;

/**
 * Adjudicates flag-fall and resignation - the terminations where a player loses by an external event, subject to the
 * FIDE exception that the game is instead drawn when the opponent cannot checkmate by any possible series of legal
 * moves. Flag-fall (FIDE 6.9) and resignation (FIDE 5.1.2) carry the identical exception, so the resignation methods
 * delegate to the flag-fall ones.
 *
 * <p>
 * Each event has a quick and a full variant, trading speed against certainty:
 * <ul>
 * <li><b>quick</b> - rules only {@link AdjudicationResult#DRAW} or {@link AdjudicationResult#LOSS}, from the fast
 * {@link Board#isUnwinnableQuick(Side)} analyzer. It draws only when it can <em>prove</em> the opponent cannot win;
 * otherwise it rules a loss (the flag stands when no draw can be shown). Latency is bounded - the right choice during
 * live play.</li>
 * <li><b>full</b> - rules {@link AdjudicationResult#DRAW}, {@link AdjudicationResult#LOSS}, or
 * {@link AdjudicationResult#UNDETERMINED}, from the complete {@link Board#isUnwinnableFull(Side)} analyzer. It draws on
 * a proven dead position, rules a loss on a proven win, and reports {@code UNDETERMINED} only when the search bound is
 * hit (rare). The recommended check at game end, where the extra cost is negligible.</li>
 * </ul>
 *
 * <p>
 * The quick draw set is a subset of the full draw set, and the quick analyzer never proves winnability, so a quick
 * {@code LOSS} is the practical ruling "no draw could be shown", whereas a full {@code LOSS} is a proven win for the
 * opponent.
 */
public final class Adjudicator {

  private Adjudicator() {
  }

  /**
   * Quickly adjudicates a flag-fall (<a href="https://handbook.fide.com/chapter/e012023">FIDE 6.9</a>): draws only if
   * the opponent is provably unwinnable by the quick analyzer, otherwise rules the flag-fall a loss.
   *
   * @param board the position at flag-fall
   * @param flaggingPlayer the player whose flag fell; must be {@link Side#WHITE} or {@link Side#BLACK}
   * @return {@link AdjudicationResult#DRAW} or {@link AdjudicationResult#LOSS}
   * @throws IllegalArgumentException if {@code flaggingPlayer} is {@link Side#NONE}
   */
  public static AdjudicationResult adjudicateFlagfallQuick(Board board, Side flaggingPlayer) {
    final Side wouldBeWinner = opponentOf(flaggingPlayer);
    return board.isUnwinnableQuick(wouldBeWinner) == UnwinnabilityQuickVerdict.UNWINNABLE ? AdjudicationResult.DRAW
        : AdjudicationResult.LOSS;
  }

  /**
   * Quickly adjudicates a resignation (<a href="https://handbook.fide.com/chapter/e012023">FIDE 5.1.2</a>) - identical
   * to {@link #adjudicateFlagfallQuick(Board, Side)}.
   *
   * @param board the position at resignation
   * @param resigningPlayer the player resigning; must be {@link Side#WHITE} or {@link Side#BLACK}
   * @return {@link AdjudicationResult#DRAW} or {@link AdjudicationResult#LOSS}
   * @throws IllegalArgumentException if {@code resigningPlayer} is {@link Side#NONE}
   */
  public static AdjudicationResult adjudicateResignationQuick(Board board, Side resigningPlayer) {
    return adjudicateFlagfallQuick(board, resigningPlayer);
  }

  /**
   * Adjudicates a flag-fall (<a href="https://handbook.fide.com/chapter/e012023">FIDE 6.9</a>) completely: draws on a
   * proven dead position, rules a loss on a proven win for the opponent, or {@link AdjudicationResult#UNDETERMINED}
   * when the complete analysis cannot decide within its search bound.
   *
   * @param board the position at flag-fall
   * @param flaggingPlayer the player whose flag fell; must be {@link Side#WHITE} or {@link Side#BLACK}
   * @return {@link AdjudicationResult#DRAW}, {@link AdjudicationResult#LOSS}, or {@link AdjudicationResult#UNDETERMINED}
   * @throws IllegalArgumentException if {@code flaggingPlayer} is {@link Side#NONE}
   */
  public static AdjudicationResult adjudicateFlagfallFull(Board board, Side flaggingPlayer) {
    final Side wouldBeWinner = opponentOf(flaggingPlayer);
    final UnwinnabilityFullVerdict verdict = board.isUnwinnableFull(wouldBeWinner);
    if (verdict == UnwinnabilityFullVerdict.UNWINNABLE) {
      return AdjudicationResult.DRAW;
    }
    if (verdict.isWinnable()) {
      return AdjudicationResult.LOSS;
    }
    return AdjudicationResult.UNDETERMINED;
  }

  /**
   * Adjudicates a resignation (<a href="https://handbook.fide.com/chapter/e012023">FIDE 5.1.2</a>) completely -
   * identical to {@link #adjudicateFlagfallFull(Board, Side)}.
   *
   * @param board the position at resignation
   * @param resigningPlayer the player resigning; must be {@link Side#WHITE} or {@link Side#BLACK}
   * @return {@link AdjudicationResult#DRAW}, {@link AdjudicationResult#LOSS}, or {@link AdjudicationResult#UNDETERMINED}
   * @throws IllegalArgumentException if {@code resigningPlayer} is {@link Side#NONE}
   */
  public static AdjudicationResult adjudicateResignationFull(Board board, Side resigningPlayer) {
    return adjudicateFlagfallFull(board, resigningPlayer);
  }

  private static Side opponentOf(Side flaggingPlayer) {
    if (flaggingPlayer == Side.NONE) {
      throw new IllegalArgumentException("flaggingPlayer must be WHITE or BLACK");
    }
    return flaggingPlayer.getOppositeSide();
  }
}
