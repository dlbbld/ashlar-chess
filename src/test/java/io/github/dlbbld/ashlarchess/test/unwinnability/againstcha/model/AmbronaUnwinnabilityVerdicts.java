package io.github.dlbbld.ashlarchess.test.unwinnability.againstcha.model;

import io.github.dlbbld.ashlarchess.unwinnability.UnwinnabilityFullVerdict;
import io.github.dlbbld.ashlarchess.unwinnability.UnwinnabilityQuickVerdict;

public record AmbronaUnwinnabilityVerdicts(UnwinnabilityFullVerdict fullWhite, UnwinnabilityFullVerdict fullBlack,
    UnwinnabilityQuickVerdict quickWhite, UnwinnabilityQuickVerdict quickBlack) {

}
