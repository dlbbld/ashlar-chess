package com.dlb.chess.san;

import com.dlb.chess.bitboard.BitboardPosition;
import com.dlb.chess.board.enums.File;
import com.dlb.chess.board.enums.Piece;
import com.dlb.chess.board.enums.PieceType;
import com.dlb.chess.board.enums.Rank;
import com.dlb.chess.board.enums.Side;
import com.dlb.chess.board.enums.Square;

/**
 * Internal piece-presence checks used by SAN piece-exists validation. Not part of the public API: SAN parsing is the
 * public entry point; the per-square scans are an implementation detail.
 */
abstract class SanPieceCheck {

  static boolean calculateHasPieceType(Side side, PieceType pieceType, BitboardPosition bitboardPosition) {
    final Piece piece = Piece.calculate(side, pieceType);
    for (final Square boardSquare : Square.REAL) {
      if (bitboardPosition.get(boardSquare) == piece) {
        return true;
      }
    }
    return false;
  }

  static boolean calculateHasPieceType(Side side, PieceType pieceType, BitboardPosition bitboardPosition, File file) {
    final Piece piece = Piece.calculate(side, pieceType);
    for (final Square boardSquare : Square.REAL) {
      if (boardSquare.getFile() != file) {
        continue;
      }
      if (bitboardPosition.get(boardSquare) == piece) {
        return true;
      }
    }
    return false;
  }

  static boolean calculateHasPieceType(Side side, PieceType pieceType, BitboardPosition bitboardPosition, Rank rank) {
    final Piece piece = Piece.calculate(side, pieceType);
    for (final Square boardSquare : Square.REAL) {
      if (boardSquare.getRank() != rank) {
        continue;
      }
      if (bitboardPosition.get(boardSquare) == piece) {
        return true;
      }
    }
    return false;
  }
}
