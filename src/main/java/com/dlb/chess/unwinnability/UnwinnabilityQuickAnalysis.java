package com.dlb.chess.unwinnability;

import java.util.List;

import com.dlb.chess.model.UciMove;

public record UnwinnabilityQuickAnalysis(UnwinnabilityQuickVerdict verdict, List<UciMove> mateLine) {
}
