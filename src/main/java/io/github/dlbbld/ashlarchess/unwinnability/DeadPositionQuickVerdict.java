// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.unwinnability;

/**
 * Whole-position verdict of the quick dead-position analysis. A position is <em>dead</em> when neither side can deliver
 * checkmate by any sequence of legal moves (FIDE 5.2.2). The quick analysis is deliberately two-valued: it either proves
 * the position dead or leaves it open. It never claims a position alive - establishing that a side can win is the
 * complete (full) analysis's job.
 */
public enum DeadPositionQuickVerdict {

  /** Proven dead: neither side can deliver checkmate. */
  DEAD,

  /** Not proven dead; the position may or may not be alive. */
  POSSIBLY_ALIVE
}
