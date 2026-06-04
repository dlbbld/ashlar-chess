// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

/**
 * Provider-neutral reader and model for the move-generation oracle (per-position legal-UCI sets). Shared by the
 * python-chess and scalachess cross-validation tests, which supply different JSONL roots but the same schema.
 */
@NonNullByDefault
package io.github.dlbbld.ashlarchess.test.oracle.movegen;

import org.eclipse.jdt.annotation.NonNullByDefault;
