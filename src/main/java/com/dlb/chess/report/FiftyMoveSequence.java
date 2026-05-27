package com.dlb.chess.report;

import com.dlb.chess.common.Nulls;
import com.google.common.collect.ImmutableList;

/**
 * One run of non-progress halfmoves that reached the 50-move-rule threshold (halfmove clock {@code >= 100}).
 *
 * <p>
 * {@code entries} carry the structure produced by {@link NoProgressMoveUtility}: the first entry marks where the
 * sequence started, an optional middle entry marks the ply at which the clock first reached 100 (for sequences that
 * extend past the threshold), and the last entry marks where the sequence ended (either by a clock-resetting move
 * being played or by the end of the played history).
 *
 * <p>
 * Three shape possibilities, distinguished by the two flags:
 *
 * <ul>
 * <li><strong>Pure played</strong> ({@code includesInitialFen == false}, {@code thresholdReachedDuringInitialFen ==
 * false}): the sequence both starts and reaches threshold via played halfmoves. The familiar mid-game case.
 * <li><strong>Initial-FEN-continued</strong> ({@code includesInitialFen == true},
 * {@code thresholdReachedDuringInitialFen == false}): the initial FEN had a partial halfmove clock and the sequence
 * extended into play, reaching the threshold at a played halfmove.
 * <li><strong>Initial-FEN-at-threshold</strong> ({@code includesInitialFen == true},
 * {@code thresholdReachedDuringInitialFen == true}): the initial FEN's halfmove clock <em>already</em> met or
 * exceeded the threshold; the 50-move rule was met before any halfmoves were played. The sequence may continue past
 * the initial-FEN portion (if a non-zeroing legal move is played from the initial position) or it may not (if the
 * only legal moves zero the clock, e.g. the constructed corner where the only legal move is a capture).
 * </ul>
 *
 * <p>
 * {@code finalSequenceLength} is the final halfmove-clock value reached by the sequence ({@code >= 100} by
 * construction — sequences below the threshold are not surfaced here).
 */
record FiftyMoveSequence(ImmutableList<NoProgressHalfMove> entries, boolean includesInitialFen,
    boolean thresholdReachedDuringInitialFen, int finalSequenceLength) {

  public FiftyMoveSequence {
    entries = Nulls.copyOfList(entries);
    if (thresholdReachedDuringInitialFen && !includesInitialFen) {
      throw new IllegalArgumentException(
          "thresholdReachedDuringInitialFen=true requires includesInitialFen=true");
    }
  }
}
