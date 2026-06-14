// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.fen;

import io.github.dlbbld.ashlarchess.bitboard.BitboardPosition;
import io.github.dlbbld.ashlarchess.board.enums.Piece;
import io.github.dlbbld.ashlarchess.board.enums.PieceUtility;
import io.github.dlbbld.ashlarchess.board.enums.PieceType;
import io.github.dlbbld.ashlarchess.board.enums.Side;
import io.github.dlbbld.ashlarchess.board.enums.Square;
import io.github.dlbbld.ashlarchess.board.enums.SquareType;

/**
 * Internal piece-count helpers used by FEN advanced validation. Not part of the public API: the library exposes parsing
 * and outcome reporting, not material arithmetic.
 */
abstract class FenMaterialCount {

  static int calculateNumberOfPieces(Side side, BitboardPosition bitboardPosition, PieceType pieceType) {
    final Piece piece = PieceUtility.calculate(side, pieceType);
    int total = 0;
    for (final Square boardSquare : Square.REAL) {
      if (bitboardPosition.get(boardSquare) == piece) {
        total++;
      }
    }
    return total;
  }

  static int calculateNumberOfBishops(Side side, BitboardPosition bitboardPosition, SquareType squareType) {
    final Piece bishop = PieceUtility.calculate(side, PieceType.BISHOP);
    int total = 0;
    for (final Square boardSquare : Square.REAL) {
      if (bitboardPosition.get(boardSquare) == bishop && boardSquare.getSquareType() == squareType) {
        total++;
      }
    }
    return total;
  }
}
