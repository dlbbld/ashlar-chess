// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.test.unwinnability.oracle.model;

import java.util.Set;

import io.github.dlbbld.ashlarchess.board.enums.Side;
import io.github.dlbbld.ashlarchess.test.unwinnability.oracle.enums.GameStatusAnalysis;

public record GameMultipleAnalysis(Set<GameStatusAnalysis> gameStatusSet, int numberOfHalfMoves, Side havingMove) {

}
