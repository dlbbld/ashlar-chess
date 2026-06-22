// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

/**
 * Internal bitboard engine: the per-piece attack/move bitboard tables, the
 * {@link io.github.dlbbld.ashlarchess.bitboard.BitboardPosition}-to-{@code LegalMove} factory, and bitboard decode
 * helpers. These are implementation details of {@link io.github.dlbbld.ashlarchess.bitboard.BitboardPosition} and are
 * deliberately <em>not</em> exported from the module ({@code module-info.java} exports only {@code bitboard}, where the
 * sole public advanced API is {@code BitboardPosition}). They stay {@code public} (rather than package-private) so the
 * rest of the library - and the white-box tests, which live in sibling packages - can still use them across packages.
 */
@NonNullByDefault
package io.github.dlbbld.ashlarchess.bitboard.internal;

import org.eclipse.jdt.annotation.NonNullByDefault;
