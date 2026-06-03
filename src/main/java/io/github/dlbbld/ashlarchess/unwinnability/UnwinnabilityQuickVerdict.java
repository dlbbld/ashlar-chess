// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.unwinnability;

/**
 * Public verdict of the quick unwinnability analysis. The quick analysis is deliberately two-valued: it either proves
 * unwinnability for the intended winner or leaves it open. It never claims winnability - establishing a concrete win is
 * the complete (full) analysis's job. (The analyzer keeps a richer three-valued representation internally; see
 * {@code UnwinnabilityQuickVerdictInternal}.)
 */
public enum UnwinnabilityQuickVerdict {

  /** Proven unwinnable for the intended winner. */
  UNWINNABLE,

  /** Not proven unwinnable; the intended winner may or may not be able to win. */
  POSSIBLY_WINNABLE
}
