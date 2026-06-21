// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.model;

import com.google.common.collect.ImmutableSet;

import io.github.dlbbld.ashlarchess.enums.KingSafetyCheck;

public record LegalMoveCalculation(ImmutableSet<LegalMove> legalMoveSet,
    ImmutableSet<PseudoLegalMove> pseudoLegalMoveSet, KingSafetyCheck pseudoLegalKingSafety) {

}
