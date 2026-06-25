// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

/**
 * Internal {@code board} helpers, not exported from the module: move-number formatting for PGN/report output
 * ({@code MoveNumberFormat} and its {@code AddSpace} flag) and the UCI-move helpers for the public {@code UciMove}
 * value type ({@code UciMoveUtility}, {@code UciMoveValidationUtility}). They stay {@code public} so the rest of the
 * library (e.g. {@code pgn}, {@code report}, {@code unwinnability}) can use them across packages, but
 * {@code module-info.java} does not export this package. The public game vocabulary stays in {@code board}.
 */
@NonNullByDefault
package io.github.dlbbld.ashlarchess.board.internal;

import org.eclipse.jdt.annotation.NonNullByDefault;
