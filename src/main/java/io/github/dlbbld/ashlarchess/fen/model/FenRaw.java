package io.github.dlbbld.ashlarchess.fen.model;

public record FenRaw(String piecePlacement, String havingMove, String castlingRightBothStr,
    String enPassantCaptureTargetSquare, String halfMoveClock, String fullMoveNumber) {

}
