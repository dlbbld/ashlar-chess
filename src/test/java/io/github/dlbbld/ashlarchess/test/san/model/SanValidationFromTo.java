package io.github.dlbbld.ashlarchess.test.san.model;

import io.github.dlbbld.ashlarchess.board.enums.File;
import io.github.dlbbld.ashlarchess.board.enums.Rank;
import io.github.dlbbld.ashlarchess.board.enums.Square;

public record SanValidationFromTo(File fromFile, Rank fromRank, Square toSquare) {
}
