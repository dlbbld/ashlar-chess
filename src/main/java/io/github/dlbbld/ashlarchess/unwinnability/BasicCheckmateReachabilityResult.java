// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.unwinnability;

/**
 * Outcome of the basic-checkmate-reachability theorem shortcut. {@code NOT_APPLICABLE} means the position is not in a
 * covered material class (with the intended winner holding the mating material) or is a terminal position the regular
 * analysis should handle, so the caller must continue with the ordinary search.
 */
enum BasicCheckmateReachabilityResult {
  WINNABLE, UNWINNABLE, NOT_APPLICABLE;
}
