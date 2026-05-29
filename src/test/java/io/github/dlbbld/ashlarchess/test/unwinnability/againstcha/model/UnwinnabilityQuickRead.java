// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.test.unwinnability.againstcha.model;

import io.github.dlbbld.ashlarchess.board.enums.Side;
import io.github.dlbbld.ashlarchess.fen.model.Fen;
import io.github.dlbbld.ashlarchess.unwinnability.UnwinnabilityQuickVerdict;

public record UnwinnabilityQuickRead(Fen fen, String lichessGameId, Side winner,
    UnwinnabilityQuickVerdict unwinnableQuick, String mateLine) {

}
