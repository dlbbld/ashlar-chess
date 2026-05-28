// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.test.unwinnability.oracle.model;

import io.github.dlbbld.ashlarchess.test.unwinnability.oracle.enums.GameStatusAnalysis;

public record GameForcedAnalysis(GameStatusAnalysis gameStatus, int numberOfForcedHalfMoves) {
}
