package io.github.dlbbld.ashlarchess.fen.model;

import io.github.dlbbld.ashlarchess.bitboard.BitboardPosition;
import io.github.dlbbld.ashlarchess.board.enums.CastlingRight;
import io.github.dlbbld.ashlarchess.board.enums.Side;
import io.github.dlbbld.ashlarchess.board.enums.Square;

public record Fen(String fen, BitboardPosition bitboardPosition, Side havingMove, CastlingRight castlingRightWhite,
    CastlingRight castlingRightBlack, Square enPassantCaptureTargetSquare, int halfMoveClock, int fullMoveNumber) {
}
