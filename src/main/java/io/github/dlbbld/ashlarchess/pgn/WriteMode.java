// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.pgn;

/**
 * Output mode for {@link PgnWriter} and {@link PgnCreate}.
 *
 * <ul>
 * <li>{@link #SEMANTIC} (the default) - honest preservation. Emits the parse model as given: same tags, same values,
 * same order, same termination-marker presence/absence. The library never silently invents content. Formatting trivia
 * is normalised (single-space tag brackets, standard line wrapping) and movetext SAN is canonical (already
 * canonicalised at parse time).</li>
 * <li>{@link #ARCHIVAL} - PGN spec section 8.1.1 archival storage. Opt-in. Before emission the parse model is
 * normalised by {@code PgnArchivalNormalization}: the full Seven Tag Roster is filled with spec-defined placeholders,
 * the SetUp/FEN coupling is enforced, redundant initial-position FEN/SetUp tags are dropped, a Result tag is
 * synthesised from the termination marker (or {@code *} if neither is present), the termination marker is guaranteed
 * present in the movetext, and tags are sorted into canonical order.</li>
 * </ul>
 */
public enum WriteMode {
  SEMANTIC,
  ARCHIVAL
}
