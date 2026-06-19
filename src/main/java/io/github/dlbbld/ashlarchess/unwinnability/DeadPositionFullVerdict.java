// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.unwinnability;

/**
 * Whole-position verdict of the complete (full) dead-position analysis. A position is <em>dead</em> when neither side
 * can deliver checkmate by any sequence of legal moves (FIDE 5.2.2), and <em>alive</em> when at least one side can. The
 * undetermined value records that neither could be established within the search bound.
 */
public enum DeadPositionFullVerdict {

  /** Proven dead: neither side can deliver checkmate. */
  DEAD,

  /** Proven alive: at least one side can deliver checkmate. */
  ALIVE,

  /** Neither dead nor alive could be established within the search bound. */
  UNDETERMINED
}
