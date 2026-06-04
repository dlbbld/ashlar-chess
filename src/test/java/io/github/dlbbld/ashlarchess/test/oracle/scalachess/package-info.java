// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

/**
 * Cross-validation of ashlar-chess against scalachess (lichess.org's rules engine), the third differential oracle. The
 * committed JSONL oracle is generated out-of-process by {@code tools/scalachess-oracle/generate_legal_moves_oracle.scala}
 * and read here via the provider-neutral {@link io.github.dlbbld.ashlarchess.test.oracle.movegen} reader.
 */
@NonNullByDefault
package io.github.dlbbld.ashlarchess.test.oracle.scalachess;

import org.eclipse.jdt.annotation.NonNullByDefault;
