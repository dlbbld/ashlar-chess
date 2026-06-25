// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.unwinnability;

/**
 * How a {@link UnwinnabilityFullVerdict#WINNABLE} result was established, carried on {@link UnwinnabilityFullAnalysis}.
 * Compare the constant directly; there is no boolean shortcut.
 */
public enum WinnableProof {

  /** Winnability certified by the basic-helpmate-existence theorem; the analysis carries no mate line. */
  THEOREM,

  /** Winnability shown by a concrete cooperative-mate search; the analysis carries a mate line. */
  HELPMATE,

  /**
   * Not a win - the verdict is {@link UnwinnabilityFullVerdict#UNWINNABLE} or
   * {@link UnwinnabilityFullVerdict#UNDETERMINED}.
   */
  NONE
}
