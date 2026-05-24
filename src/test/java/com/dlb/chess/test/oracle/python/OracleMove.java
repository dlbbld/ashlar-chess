package com.dlb.chess.test.oracle.python;

public record OracleMove(String san, String uci, String fenAfter, int halfmoveClock, int fullmoveNumber,
    boolean isCheck, boolean isCheckmate, boolean isStalemate, boolean isInsufficientMaterial,
    boolean hasInsufficientMaterialWhite, boolean hasInsufficientMaterialBlack, boolean isRepetition2,
    boolean isRepetition3, boolean isRepetition4, boolean isFivefoldRepetition, boolean isFiftyMoves,
    boolean isSeventyFiveMoves, boolean canClaimThreefold, boolean canClaimFifty) {
}
