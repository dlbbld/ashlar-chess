// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

/**
 * Internal SAN helpers, not exported from the module: the SAN-conversion model ({@code SanParse},
 * {@code SanConversion}, {@code SanFormat}), the notation symbol enums ({@code NotationMovingPiece},
 * {@code NotationPromotionPiece}, {@code SanSymbol}), and the format/terminal-marker utilities
 * ({@code SanValidateFormat}, {@code SanFormatUtility}, {@code SanTerminalMarkerUtility}). They stay {@code public} so
 * the rest of the library and the white-box tests can use them across packages, but {@code module-info.java} does not
 * export this package. The public SAN API - the parsers, {@code MoveToSan} / {@code MoveToLan}, the result types, the
 * forgiven-item taxonomy and {@code SanTerminalMarker} - stays in {@code san}.
 */
@NonNullByDefault
package io.github.dlbbld.ashlarchess.san.internal;

import org.eclipse.jdt.annotation.NonNullByDefault;
