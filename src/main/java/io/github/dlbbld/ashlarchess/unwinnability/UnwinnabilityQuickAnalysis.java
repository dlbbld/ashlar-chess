package io.github.dlbbld.ashlarchess.unwinnability;

import java.util.List;

import io.github.dlbbld.ashlarchess.model.UciMove;

public record UnwinnabilityQuickAnalysis(UnwinnabilityQuickVerdict verdict, List<UciMove> mateLine) {
}
