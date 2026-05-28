package io.github.dlbbld.ashlarchess.model;

import com.google.common.collect.ImmutableSet;

import io.github.dlbbld.ashlarchess.enums.KingSafetyCheck;

public record LegalMoveCalculation(ImmutableSet<LegalMove> legalMoveSet,
    ImmutableSet<PseudoLegalMove> pseudoLegalMoveSet, KingSafetyCheck pseudoLegalKingSafety) {

}
