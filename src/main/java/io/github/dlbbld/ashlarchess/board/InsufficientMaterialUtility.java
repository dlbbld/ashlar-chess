// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.board;

import io.github.dlbbld.ashlarchess.bitboard.BitboardPosition;
import io.github.dlbbld.ashlarchess.board.enums.Piece;
import io.github.dlbbld.ashlarchess.board.enums.PieceUtility;
import io.github.dlbbld.ashlarchess.board.enums.Side;
import io.github.dlbbld.ashlarchess.board.enums.Square;
import io.github.dlbbld.ashlarchess.board.enums.SquareType;
import io.github.dlbbld.ashlarchess.common.constants.EnumConstants;

abstract class InsufficientMaterialUtility implements EnumConstants {

  public static boolean calculateIsInsufficientMaterial(Side side, BitboardPosition bitboardPosition) {
    final Side oppositeSide = side.getOppositeSide();

    if (BoardMaterial.calculateHasKingOnly(side, bitboardPosition)) {
      return true;
    }
    if (BoardMaterial.calculateHasKingAndKnightOnly(side, bitboardPosition)) {
      return calculateHasZeroOrMultipleQueenOnly(oppositeSide, bitboardPosition);
    }
    if (calculateHasZeroOrMultipleLightSquareBishopOnly(side, bitboardPosition)) {
      return calculateHasNoPawnAndNoKnightAndNoDarkSquareBishop(oppositeSide, bitboardPosition);
    }
    if (calculateHasZeroOrMultipleDarkSquareBishopOnly(side, bitboardPosition)) {
      return calculateHasNoPawnAndNoKnightAndNoLightSquareBishop(oppositeSide, bitboardPosition);
    }

    return false;
  }

  private static boolean calculateHasZeroOrMultipleSquareBishopOnlyForSpecifiedColor(Side side,
      BitboardPosition bitboardPosition, SquareType squareType) {
    final Piece king = PieceUtility.calculate(side, KING);
    final Piece bishop = PieceUtility.calculate(side, BISHOP);
    for (final Square boardSquare : Square.REAL) {
      final Piece pieceOnSquare = bitboardPosition.get(boardSquare);
      if (BoardMaterial.calculateIsOwnPiece(side, pieceOnSquare)) {
        if (pieceOnSquare == king || pieceOnSquare == bishop && boardSquare.getSquareType() == squareType) {
          continue;
        }
        return false;
      }
    }
    return true;
  }

  static boolean calculateHasZeroOrMultipleLightSquareBishopOnly(Side side, BitboardPosition bitboardPosition) {
    return calculateHasZeroOrMultipleSquareBishopOnlyForSpecifiedColor(side, bitboardPosition, SquareType.LIGHT_SQUARE);
  }

  static boolean calculateHasZeroOrMultipleDarkSquareBishopOnly(Side side, BitboardPosition bitboardPosition) {
    return calculateHasZeroOrMultipleSquareBishopOnlyForSpecifiedColor(side, bitboardPosition, SquareType.DARK_SQUARE);
  }

  private static boolean calculateHasZeroOrMultipleQueenOnly(Side side, BitboardPosition bitboardPosition) {
    final Piece king = PieceUtility.calculate(side, KING);
    final Piece queen = PieceUtility.calculate(side, QUEEN);
    for (final Square boardSquare : Square.REAL) {
      final Piece pieceOnSquare = bitboardPosition.get(boardSquare);
      if (BoardMaterial.calculateIsOwnPiece(side, pieceOnSquare)) {
        if (pieceOnSquare == king || pieceOnSquare == queen) {
          continue;
        }
        return false;
      }
    }
    return true;
  }

  private static boolean calculateHasNoPawnAndNoKnightAndNoLightSquareBishop(Side side,
      BitboardPosition bitboardPosition) {
    return !calculateHasPawn(side, bitboardPosition) && !calculateHasKnight(side, bitboardPosition)
        && !calculateHasBishopForSpecifiedColor(side, SquareType.LIGHT_SQUARE, bitboardPosition);
  }

  private static boolean calculateHasNoPawnAndNoKnightAndNoDarkSquareBishop(Side side,
      BitboardPosition bitboardPosition) {
    return !calculateHasPawn(side, bitboardPosition) && !calculateHasKnight(side, bitboardPosition)
        && !calculateHasBishopForSpecifiedColor(side, SquareType.DARK_SQUARE, bitboardPosition);
  }

  private static boolean calculateHasPawn(Side side, BitboardPosition bitboardPosition) {
    return BoardMaterial.calculateHasPieceType(side, PAWN, bitboardPosition);
  }

  private static boolean calculateHasKnight(Side side, BitboardPosition bitboardPosition) {
    return BoardMaterial.calculateHasPieceType(side, KNIGHT, bitboardPosition);
  }

  private static boolean calculateHasBishopForSpecifiedColor(Side side, SquareType squareType,
      BitboardPosition bitboardPosition) {
    final Piece bishop = PieceUtility.calculate(side, BISHOP);
    for (final Square boardSquare : Square.REAL) {
      final Piece pieceOnSquare = bitboardPosition.get(boardSquare);
      if (pieceOnSquare == bishop && boardSquare.getSquareType() == squareType) {
        return true;
      }
    }
    return false;
  }

}
