// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

/**
 * Internal FEN helpers, not exported from the module: the {@code FenBoard} parse helper, {@code FenConstants}, the
 * {@code FenPieceSymbol} / {@code FenSideSymbol} symbol enums, and the {@code FenField} raw-field record. They stay
 * {@code public} so the rest of the library and the white-box tests can use them across packages, but
 * {@code module-info.java} does not export this package. The public FEN value type {@code Fen} stays in
 * {@code fen.model}; the public parsers and result types stay in {@code fen}.
 */
@NonNullByDefault
package io.github.dlbbld.ashlarchess.fen.internal;

import org.eclipse.jdt.annotation.NonNullByDefault;
