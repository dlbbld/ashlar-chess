package io.github.dlbbld.ashlarchess.test.unwinnability.oracle.model;

import java.util.Set;

import io.github.dlbbld.ashlarchess.test.unwinnability.oracle.enums.GameStatusAnalysis;
import io.github.dlbbld.ashlarchess.test.unwinnability.oracle.enums.LimitedUnwinnabilityVerdict;

public record LimitedUnwinnabilityAnalysis(LimitedUnwinnabilityVerdict verdict, Set<GameStatusAnalysis> gameStatusSet) {

}
