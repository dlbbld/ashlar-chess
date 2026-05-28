// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

/**
 * Game-level reports about a {@link io.github.dlbbld.ashlarchess.board.Board}: threefold-repetition listings (including
 * the missed-claim-ahead opportunities other libraries don't surface) and no-progress (50/75-move-rule) sequences,
 * rendered as a human-readable summary to {@code stdout} via {@link io.github.dlbbld.ashlarchess.report.Reporter}.
 *
 * <p>
 * Distinguishes the on-board predicates ("threefold has occurred") from the with-move predicates ("some legal move
 * would create a threefold position the side could claim before playing it") - the latter is the missed-claim feature.
 *
 * <p>
 * Internally, analysis records carry the facts and print classes format them. The records are package-private; only
 * {@code Reporter} is part of the public surface.
 */
@NonNullByDefault
package io.github.dlbbld.ashlarchess.report;

import org.eclipse.jdt.annotation.NonNullByDefault;
