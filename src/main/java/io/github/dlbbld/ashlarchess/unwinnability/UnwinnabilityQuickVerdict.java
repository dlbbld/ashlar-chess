// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.unwinnability;

/**
 * Public verdict of the quick unwinnability analysis (the paper's {@code Unwinnable_quick}, Figure 10). The quick
 * analysis is sound and deliberately incomplete: a definite verdict ({@code UNWINNABLE} or {@code WINNABLE}) is
 * always correct, and {@code POSSIBLY_WINNABLE} asserts nothing - establishing a concrete win in general is the
 * complete (full) analysis's job.
 */
public enum UnwinnabilityQuickVerdict {

  /** Proven unwinnable for the intended winner. */
  UNWINNABLE,

  /**
   * Proven winnable for the intended winner: the bounded search met a checkmate by the intended winner before its
   * first depth interrupt. The quick analysis proves winnability only on such quickly matable positions; it carries
   * no mate line.
   */
  WINNABLE,

  /** Not decided either way; the intended winner may or may not be able to win. */
  POSSIBLY_WINNABLE
}
