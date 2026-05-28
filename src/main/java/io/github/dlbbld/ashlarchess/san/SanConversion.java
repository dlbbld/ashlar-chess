package io.github.dlbbld.ashlarchess.san;

import io.github.dlbbld.ashlarchess.board.enums.File;
import io.github.dlbbld.ashlarchess.board.enums.PieceType;
import io.github.dlbbld.ashlarchess.board.enums.PromotionPieceType;
import io.github.dlbbld.ashlarchess.board.enums.Rank;
import io.github.dlbbld.ashlarchess.board.enums.Square;

public record SanConversion(PieceType movingPieceType, File fromFile, Rank fromRank, Square toSquare,
    PromotionPieceType promotionPieceType, SanTerminalMarker sanTerminalMarker) {

  public static final SanConversion EMPTY = new SanConversion(PieceType.NONE, File.NONE, Rank.NONE, Square.NONE,
      PromotionPieceType.NONE, SanTerminalMarker.NONE);

}
