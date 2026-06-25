// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.unwinnability;

import io.github.dlbbld.ashlarchess.board.Board;
import io.github.dlbbld.ashlarchess.board.enums.Side;

/**
 * Decides whether a position is a <em>dead position</em> under FIDE 5.2.2: one in which neither side can deliver
 * checkmate by any sequence of legal moves, even with the fullest cooperation. This is the symmetric, whole-position
 * counterpart to the per-side unwinnability analyzers ({@link UnwinnableQuickAnalyzer},
 * {@link UnwinnableFullAnalyzer}): a position is dead exactly when it is unwinnable for both sides.
 *
 * <p>
 * Two variants mirror the underlying analyzers. {@link #deadPositionQuick(Board)} is the cheap, structural,
 * during-the-game check; {@link #deadPositionFull(Board)} is the complete check suggested at game end (resignation or
 * flag-fall). Like the analyzers, both run on a history-less copy of the position and do not mutate the caller's board.
 */
public final class DeadPositionAnalyzer {

  private DeadPositionAnalyzer() {
  }

  /**
   * Quick dead-position check. Returns {@link DeadPositionQuickVerdict#DEAD} when the quick analyzer proves both sides
   * unwinnable, and {@link DeadPositionQuickVerdict#POSSIBLY_ALIVE} otherwise. Short-circuits: it stops as soon as one
   * side is not provably unwinnable.
   */
  public static DeadPositionQuickVerdict deadPositionQuick(Board board) {
    if (UnwinnableQuickAnalyzer.unwinnableQuick(board, Side.WHITE).verdict() != UnwinnabilityQuickVerdict.UNWINNABLE) {
      return DeadPositionQuickVerdict.POSSIBLY_ALIVE;
    }
    if (UnwinnableQuickAnalyzer.unwinnableQuick(board, Side.BLACK).verdict() != UnwinnabilityQuickVerdict.UNWINNABLE) {
      return DeadPositionQuickVerdict.POSSIBLY_ALIVE;
    }
    return DeadPositionQuickVerdict.DEAD;
  }

  /**
   * Complete dead-position check. Returns {@link DeadPositionFullVerdict#ALIVE} as soon as either side is found
   * winnable, {@link DeadPositionFullVerdict#DEAD} when both sides are proven unwinnable, and
   * {@link DeadPositionFullVerdict#UNDETERMINED} when neither could be decided within the search bound. Short-circuits:
   * it stops as soon as one side is found winnable.
   */
  public static DeadPositionFullVerdict deadPositionFull(Board board) {
    final UnwinnabilityFullVerdict white = UnwinnableFullAnalyzer.unwinnableFull(board, Side.WHITE).verdict();
    if (white == UnwinnabilityFullVerdict.WINNABLE) {
      return DeadPositionFullVerdict.ALIVE;
    }
    final UnwinnabilityFullVerdict black = UnwinnableFullAnalyzer.unwinnableFull(board, Side.BLACK).verdict();
    if (black == UnwinnabilityFullVerdict.WINNABLE) {
      return DeadPositionFullVerdict.ALIVE;
    }
    if (white == UnwinnabilityFullVerdict.UNWINNABLE && black == UnwinnabilityFullVerdict.UNWINNABLE) {
      return DeadPositionFullVerdict.DEAD;
    }
    return DeadPositionFullVerdict.UNDETERMINED;
  }
}
