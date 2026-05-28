package io.github.dlbbld.ashlarchess.test.unwinnability.againstcha.model;

import io.github.dlbbld.ashlarchess.board.enums.Side;
import io.github.dlbbld.ashlarchess.fen.model.Fen;
import io.github.dlbbld.ashlarchess.test.unwinnability.enums.UnwinnabilityMode;

public record UnwinnabilityRawRead(Fen fen, String lichessGameId, UnwinnabilityMode mode, Side winner, String result,
    String mateLine) {

}
