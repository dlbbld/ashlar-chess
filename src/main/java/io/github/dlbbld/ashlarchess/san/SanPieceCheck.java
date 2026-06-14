// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.san;

import io.github.dlbbld.ashlarchess.bitboard.BitboardPosition;
import io.github.dlbbld.ashlarchess.board.enums.File;
import io.github.dlbbld.ashlarchess.board.enums.Piece;
import io.github.dlbbld.ashlarchess.board.enums.PieceType;
import io.github.dlbbld.ashlarchess.board.enums.PieceUtility;
import io.github.dlbbld.ashlarchess.board.enums.Rank;
import io.github.dlbbld.ashlarchess.board.enums.Side;
import io.github.dlbbld.ashlarchess.board.enums.Square;

/**
 * Internal piece-presence checks used by SAN piece-exists validation. Not part of the public API: SAN parsing is the
 * public entry point; the per-square scans are an implementation detail.
 */
abstract class SanPieceCheck {

  static boolean calculateHasPieceType(Side side, PieceType pieceType, BitboardPosition bitboardPosition) {
    final Piece piece = PieceUtility.calculate(side, pieceType);
    for (final Square boardSquare : Square.REAL) {
      if (bitboardPosition.get(boardSquare) == piece) {
        return true;
      }
    }
    return false;
  }

  static boolean calculateHasPieceType(Side side, PieceType pieceType, BitboardPosition bitboardPosition, File file) {
    final Piece piece = PieceUtility.calculate(side, pieceType);
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
    final Piece piece = PieceUtility.calculate(side, pieceType);
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
