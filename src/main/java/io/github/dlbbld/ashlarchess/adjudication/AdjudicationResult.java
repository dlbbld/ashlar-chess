// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.adjudication;

/**
 * Outcome of adjudicating a flag-fall or resignation, from the perspective of the player who flagged or resigned.
 */
public enum AdjudicationResult {

  /** The game is drawn: the opponent cannot checkmate by any possible series of legal moves. */
  DRAW,

  /** The flagging or resigning player loses; the opponent wins. */
  LOSS,

  /**
   * The outcome could not be determined within the search bound. Only the full adjudication can return this - the quick
   * adjudication always rules {@link #DRAW} or {@link #LOSS}. A caller ruling a live game typically treats it as a
   * {@link #LOSS} (the flag stands when no draw can be shown).
   */
  UNDETERMINED;
}
