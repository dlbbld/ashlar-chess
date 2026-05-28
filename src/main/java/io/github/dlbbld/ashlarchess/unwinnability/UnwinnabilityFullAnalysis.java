package io.github.dlbbld.ashlarchess.unwinnability;

import java.util.List;

import io.github.dlbbld.ashlarchess.model.UciMove;

public record UnwinnabilityFullAnalysis(UnwinnabilityFullVerdict verdict, List<UciMove> mateLine) {

}
