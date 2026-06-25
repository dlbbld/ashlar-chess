// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

/**
 * Cross-cutting internal infrastructure, not exported from the module and with no single feature home: the
 * null-handling hub ({@code Nulls}), the generic collection / exception helpers ({@code ListUtility},
 * {@code SetUtility}, {@code ExceptionUtility}) and the global constants ({@code ChessConstants},
 * {@code ConfigurationConstants}, {@code CastlingConstants} - the last shared across move generation, SAN and the
 * bitboard engine). The types stay {@code public} so the rest of the library and the white-box tests can use them
 * across packages, but {@code module-info.java} does not export this package. Feature-specific helpers live in their
 * feature's own {@code *.internal} package instead.
 */
@NonNullByDefault
package io.github.dlbbld.ashlarchess.internal;

import org.eclipse.jdt.annotation.NonNullByDefault;
