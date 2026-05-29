// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.unwinnability;

import java.util.List;

import io.github.dlbbld.ashlarchess.model.UciMove;

public record UnwinnabilityQuickAnalysis(UnwinnabilityQuickVerdict verdict, List<UciMove> mateLine) {
}
