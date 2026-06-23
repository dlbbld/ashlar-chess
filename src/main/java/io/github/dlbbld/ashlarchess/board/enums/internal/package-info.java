// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

/**
 * Internal {@code board.enums} geometry helpers, not exported from the module: the static square/rank utilities
 * ({@code SquareUtility}, {@code RankUtility}) used internally for move generation and validation. They stay
 * {@code public} so the rest of the library and the white-box tests can use them across packages, but
 * {@code module-info.java} does not export this package. The board vocabulary enums stay in {@code board.enums}.
 */
@NonNullByDefault
package io.github.dlbbld.ashlarchess.board.enums.internal;

import org.eclipse.jdt.annotation.NonNullByDefault;
