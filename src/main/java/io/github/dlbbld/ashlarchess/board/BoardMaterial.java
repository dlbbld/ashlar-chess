// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.board;

import io.github.dlbbld.ashlarchess.bitboard.BitboardPosition;
import io.github.dlbbld.ashlarchess.board.enums.Piece;
import io.github.dlbbld.ashlarchess.board.enums.PieceUtility;
import io.github.dlbbld.ashlarchess.board.enums.PieceType;
import io.github.dlbbld.ashlarchess.board.enums.Side;
import io.github.dlbbld.ashlarchess.board.enums.Square;
import io.github.dlbbld.ashlarchess.common.constants.EnumConstants;

/**
 * Internal material checks needed by the board package's rule helpers (currently {@link InsufficientMaterialUtility}).
 * Kept narrow and package-private: the public API does not expose material arithmetic.
 */
abstract class BoardMaterial implements EnumConstants {

  static boolean calculateIsOwnPiece(Side side, Piece pieceOnSquare) {
    return pieceOnSquare != Piece.NONE && pieceOnSquare.getSide() == side;
  }

  static boolean calculateHasPieceType(Side side, PieceType pieceType, BitboardPosition bitboardPosition) {
    final Piece piece = PieceUtility.calculate(side, pieceType);
    for (final Square boardSquare : Square.REAL) {
      if (bitboardPosition.get(boardSquare) == piece) {
        return true;
      }
    }
    return false;
  }

  static boolean calculateHasKingOnly(Side side, BitboardPosition bitboardPosition) {
    int countKing = 0;
    for (final Square boardSquare : Square.REAL) {
      final Piece pieceOnSquare = bitboardPosition.get(boardSquare);
      if (pieceOnSquare == Piece.NONE || pieceOnSquare.getSide() != side) {
        continue;
      }
      if (pieceOnSquare.getPieceType() == KING) {
        countKing++;
        continue;
      }
      return false;
    }
    return countKing == 1;
  }

  static boolean calculateHasKingAndKnightOnly(Side side, BitboardPosition bitboardPosition) {
    return calculateHasKingAndAnotherPieceOnly(side, KNIGHT, bitboardPosition);
  }

  static boolean calculateHasKingAndBishopOnly(Side side, BitboardPosition bitboardPosition) {
    return calculateHasKingAndAnotherPieceOnly(side, BISHOP, bitboardPosition);
  }

  private static boolean calculateHasKingAndAnotherPieceOnly(Side side, PieceType anotherPieceType,
      BitboardPosition bitboardPosition) {
    int countKing = 0;
    int countAnotherPieces = 0;
    for (final Square boardSquare : Square.REAL) {
      final Piece pieceOnSquare = bitboardPosition.get(boardSquare);
      if (pieceOnSquare == Piece.NONE || pieceOnSquare.getSide() != side) {
        continue;
      }
      if (pieceOnSquare.getPieceType() == KING) {
        countKing++;
        continue;
      }
      if (pieceOnSquare.getPieceType() != anotherPieceType) {
        return false;
      }
      countAnotherPieces++;
      if (countAnotherPieces > 1) {
        return false;
      }
    }
    return countKing == 1 && countAnotherPieces == 1;
  }

}
