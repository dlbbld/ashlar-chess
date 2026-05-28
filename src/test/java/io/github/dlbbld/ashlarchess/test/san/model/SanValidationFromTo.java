// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.test.san.model;

import io.github.dlbbld.ashlarchess.board.enums.File;
import io.github.dlbbld.ashlarchess.board.enums.Rank;
import io.github.dlbbld.ashlarchess.board.enums.Square;

public record SanValidationFromTo(File fromFile, Rank fromRank, Square toSquare) {
}
