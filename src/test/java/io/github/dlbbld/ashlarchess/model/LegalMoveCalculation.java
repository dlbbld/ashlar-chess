// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.model;

import java.util.Set;

import io.github.dlbbld.ashlarchess.board.LegalMove;
import io.github.dlbbld.ashlarchess.moves.KingSafetyCheck;

public record LegalMoveCalculation(Set<LegalMove> legalMoveSet, Set<PseudoLegalMove> pseudoLegalMoveSet,
    KingSafetyCheck pseudoLegalKingSafety) {

}
