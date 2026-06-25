// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

/**
 * Internal SAN helpers, not exported from the module: the SAN-conversion model ({@code SanParse},
 * {@code SanConversion}, {@code SanFormat}), the notation symbol enums ({@code NotationMovingPiece},
 * {@code NotationPromotionPiece}, {@code SanSymbol}), the format/terminal-marker utilities ({@code SanValidateFormat},
 * {@code SanFormatUtility}, {@code SanTerminalMarkerUtility}), the SAN/LAN generators ({@code MoveToSan},
 * {@code MoveToLan}) with the {@code SanTerminalMarker} check/checkmate marker and their disambiguation helpers
 * ({@code SanSourceSpecification}, {@code SanDisambiguationUtility}). They stay {@code public} so the rest of the
 * library and the white-box tests can use them across packages, but {@code module-info.java} does not export this
 * package. The public SAN API - the parsers, the result types, the forgiven-item taxonomy and the validation problem
 * enums - stays in {@code san}; SAN/LAN generation is reached from {@code Board} ({@code getSan()} / {@code getLan()}).
 */
@NonNullByDefault
package io.github.dlbbld.ashlarchess.san.internal;

import org.eclipse.jdt.annotation.NonNullByDefault;
