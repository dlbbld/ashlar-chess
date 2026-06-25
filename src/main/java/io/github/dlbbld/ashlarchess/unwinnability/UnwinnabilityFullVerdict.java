// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.unwinnability;

/** Verdict of the complete (full) unwinnability analysis for an intended winner. */
public enum UnwinnabilityFullVerdict {

  /** Proven winnable for the intended winner. */
  WINNABLE,

  /** Proven unwinnable for the intended winner. */
  UNWINNABLE,

  /** Neither winnable nor unwinnable could be established within the search bound. */
  UNDETERMINED
}
