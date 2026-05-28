package io.github.dlbbld.ashlarchess.test.unwinnability.oracle.model;

import io.github.dlbbld.ashlarchess.test.unwinnability.oracle.enums.GameStatusAnalysis;

public record GameForcedAnalysis(GameStatusAnalysis gameStatus, int numberOfForcedHalfMoves) {
}
