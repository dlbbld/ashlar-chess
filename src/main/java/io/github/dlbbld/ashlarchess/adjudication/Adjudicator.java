// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.adjudication;

import org.eclipse.jdt.annotation.Nullable;

import io.github.dlbbld.ashlarchess.board.Board;
import io.github.dlbbld.ashlarchess.board.enums.Side;
import io.github.dlbbld.ashlarchess.unwinnability.UnwinnabilityFullVerdict;
import io.github.dlbbld.ashlarchess.unwinnability.UnwinnabilityQuickVerdict;
import io.github.dlbbld.ashlarchess.unwinnability.internal.BasicHelpmateExistenceTheorem;

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
 * {@link Board#unwinnableQuick(Side)} analyzer. It draws only when it can <em>prove</em> the opponent cannot win;
 * otherwise it rules a loss (the flag stands when no draw can be shown). Latency is bounded - the right choice during
 * live play.</li>
 * <li><b>full</b> - rules {@link AdjudicationResult#DRAW}, {@link AdjudicationResult#LOSS}, or
 * {@link AdjudicationResult#UNDETERMINED}, from the complete {@link Board#unwinnableFull(Side)} analyzer. It draws on a
 * proven dead position, rules a loss on a proven win, and reports {@code UNDETERMINED} only when the search bound is
 * hit (rare). The recommended check at game end, where the extra cost is negligible.</li>
 * </ul>
 *
 * <p>
 * Both variants first consult the basic-helpmate-existence theorem as a fast pre-check
 * ({@link BasicHelpmateExistenceTheorem#decideForAdjudication}): on the elementary-material classes it covers a proven
 * verdict settles the position immediately and the unwinnability search is skipped. The theorem is trusted only on the
 * classes where it is sound for any strictly FEN-legal board, so a pre-check ruling never disagrees with the
 * {@code Board} analyzer it stands in for.
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
   * @param board          the position at flag-fall
   * @param flaggingPlayer the player whose flag fell; must be {@link Side#WHITE} or {@link Side#BLACK}
   * @return {@link AdjudicationResult#DRAW} or {@link AdjudicationResult#LOSS}
   * @throws IllegalArgumentException if {@code flaggingPlayer} is {@link Side#NONE}
   */
  public static AdjudicationResult adjudicateFlagfallQuick(Board board, Side flaggingPlayer) {
    final Side wouldBeWinner = opponentOf(flaggingPlayer);
    final AdjudicationResult theoremResult = theoremPreCheck(board, wouldBeWinner);
    if (theoremResult != null) {
      return theoremResult;
    }
    return board.unwinnableQuick(wouldBeWinner) == UnwinnabilityQuickVerdict.UNWINNABLE ? AdjudicationResult.DRAW
        : AdjudicationResult.LOSS;
  }

  /**
   * Quickly adjudicates a resignation (<a href="https://handbook.fide.com/chapter/e012023">FIDE 5.1.2</a>) - identical
   * to {@link #adjudicateFlagfallQuick(Board, Side)}.
   *
   * @param board           the position at resignation
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
   * @param board          the position at flag-fall
   * @param flaggingPlayer the player whose flag fell; must be {@link Side#WHITE} or {@link Side#BLACK}
   * @return {@link AdjudicationResult#DRAW}, {@link AdjudicationResult#LOSS}, or
   *         {@link AdjudicationResult#UNDETERMINED}
   * @throws IllegalArgumentException if {@code flaggingPlayer} is {@link Side#NONE}
   */
  public static AdjudicationResult adjudicateFlagfallFull(Board board, Side flaggingPlayer) {
    final Side wouldBeWinner = opponentOf(flaggingPlayer);
    final AdjudicationResult theoremResult = theoremPreCheck(board, wouldBeWinner);
    if (theoremResult != null) {
      return theoremResult;
    }
    final UnwinnabilityFullVerdict verdict = board.unwinnableFull(wouldBeWinner);
    if (verdict == UnwinnabilityFullVerdict.UNWINNABLE) {
      return AdjudicationResult.DRAW;
    }
    if (verdict == UnwinnabilityFullVerdict.WINNABLE) {
      return AdjudicationResult.LOSS;
    }
    return AdjudicationResult.UNDETERMINED;
  }

  /**
   * Adjudicates a resignation (<a href="https://handbook.fide.com/chapter/e012023">FIDE 5.1.2</a>) completely -
   * identical to {@link #adjudicateFlagfallFull(Board, Side)}.
   *
   * @param board           the position at resignation
   * @param resigningPlayer the player resigning; must be {@link Side#WHITE} or {@link Side#BLACK}
   * @return {@link AdjudicationResult#DRAW}, {@link AdjudicationResult#LOSS}, or
   *         {@link AdjudicationResult#UNDETERMINED}
   * @throws IllegalArgumentException if {@code resigningPlayer} is {@link Side#NONE}
   */
  public static AdjudicationResult adjudicateResignationFull(Board board, Side resigningPlayer) {
    return adjudicateFlagfallFull(board, resigningPlayer);
  }

  /**
   * The basic-helpmate-existence theorem pre-check: {@link AdjudicationResult#DRAW} on a proven-unwinnable position or
   * {@link AdjudicationResult#LOSS} on a proven-winnable one, when the theorem settles a covered elementary-material
   * class outright, or {@code null} when it does not apply and the unwinnability analyzer must decide. Consulting the
   * theorem only on its adjudication-safe classes (see {@link BasicHelpmateExistenceTheorem#decideForAdjudication})
   * keeps this consistent with {@code Board.unwinnableQuick}/{@code Board.unwinnableFull} on every input, including the
   * retro-illegal positions that pass strict FEN parsing.
   */
  private static @Nullable AdjudicationResult theoremPreCheck(Board board, Side wouldBeWinner) {
    switch (BasicHelpmateExistenceTheorem.decideForAdjudication(board, wouldBeWinner)) {
      case UNWINNABLE:
        return AdjudicationResult.DRAW;
      case WINNABLE:
        return AdjudicationResult.LOSS;
      default:
        // NOT_APPLICABLE: outside the theorem's adjudication-safe classes; the analyzer decides.
        return null;
    }
  }

  private static Side opponentOf(Side flaggingPlayer) {
    if (flaggingPlayer == Side.NONE) {
      throw new IllegalArgumentException("flaggingPlayer must be WHITE or BLACK");
    }
    return flaggingPlayer.getOppositeSide();
  }
}
