// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.unwinnability;

/**
 * Verdict of the complete (full) unwinnability analysis for an intended winner. The two winnable values record how
 * winnability was established: {@code WINNABLE_HELPMATE} found a concrete cooperative mate and the analysis carries the
 * mate line; {@code WINNABLE_BY_THEOREM} was certified by the basic-checkmate-reachability theorem and carries no line.
 */
public enum UnwinnabilityFullVerdict {

  /** Winnable: a concrete helpmate was found; the analysis carries the witnessing mate line. */
  WINNABLE_HELPMATE,

  /** Winnable: certified by the basic-checkmate-reachability theorem, without an explicit mate line. */
  WINNABLE_BY_THEOREM,

  /** Proven unwinnable for the intended winner. */
  UNWINNABLE,

  /** Neither winnable nor unwinnable could be established within the search bound. */
  UNDETERMINED;

  /** Whether the intended winner can win, regardless of how that was established. */
  public boolean isWinnable() {
    return this == WINNABLE_HELPMATE || this == WINNABLE_BY_THEOREM;
  }
}
