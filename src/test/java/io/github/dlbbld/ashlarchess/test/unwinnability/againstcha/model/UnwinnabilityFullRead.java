package io.github.dlbbld.ashlarchess.test.unwinnability.againstcha.model;

import io.github.dlbbld.ashlarchess.board.enums.Side;
import io.github.dlbbld.ashlarchess.fen.model.Fen;
import io.github.dlbbld.ashlarchess.unwinnability.UnwinnabilityFullVerdict;

public record UnwinnabilityFullRead(Fen fen, String lichessGameId, Side winner, UnwinnabilityFullVerdict unwinnableFull,
    String mateLine) {

}
