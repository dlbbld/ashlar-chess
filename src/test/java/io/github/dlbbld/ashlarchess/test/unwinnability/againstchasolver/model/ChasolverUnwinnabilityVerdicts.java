// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.test.unwinnability.againstchasolver.model;

import io.github.dlbbld.ashlarchess.unwinnability.UnwinnabilityFullVerdict;
import io.github.dlbbld.ashlarchess.unwinnability.UnwinnabilityQuickVerdict;

/**
 * The four unwinnability verdicts captured from Miguel Ambrona's Rust {@code chasolver} for a single position: the
 * complete (full) verdict and the fast (quick) verdict, each for both intended winners. Mirrors the cha (C++) oracle's
 * {@code AmbronaUnwinnabilityVerdicts} so the two oracles share an identical shape.
 */
public record ChasolverUnwinnabilityVerdicts(UnwinnabilityFullVerdict fullWhite, UnwinnabilityFullVerdict fullBlack,
    UnwinnabilityQuickVerdict quickWhite, UnwinnabilityQuickVerdict quickBlack) {

}
