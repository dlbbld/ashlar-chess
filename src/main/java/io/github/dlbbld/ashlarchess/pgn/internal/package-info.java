// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

/**
 * Internal PGN helpers, not exported from the module: {@code TagUtility} (Seven-Tag-Roster tag access) and the
 * {@code StandardTag} enum. They stay {@code public} so the rest of the library and the white-box tests can use them
 * across packages, but {@code module-info.java} does not export this package, so modular consumers do not see them.
 */
@NonNullByDefault
package io.github.dlbbld.ashlarchess.pgn.internal;

import org.eclipse.jdt.annotation.NonNullByDefault;
