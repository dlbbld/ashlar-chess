// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.board;

import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.BISHOP;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.KING;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.KNIGHT;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.PAWN;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.QUEEN;

import io.github.dlbbld.ashlarchess.bitboard.BitboardPosition;
import io.github.dlbbld.ashlarchess.board.enums.Piece;
import io.github.dlbbld.ashlarchess.board.enums.PieceType;
import io.github.dlbbld.ashlarchess.board.enums.Side;
import io.github.dlbbld.ashlarchess.board.enums.Square;
import io.github.dlbbld.ashlarchess.board.enums.SquareType;

abstract class InsufficientMaterialUtility {

  public static boolean isInsufficientMaterial(Side side, BitboardPosition bitboardPosition) {
    final Side oppositeSide = side.getOppositeSide();

    if (InsufficientMaterialUtility.hasKingOnly(side, bitboardPosition)) {
      return true;
    }
    if (InsufficientMaterialUtility.hasKingAndKnightOnly(side, bitboardPosition)) {
      return hasZeroOrMultipleQueenOnly(oppositeSide, bitboardPosition);
    }
    if (hasZeroOrMultipleLightSquareBishopOnly(side, bitboardPosition)) {
      return hasNoPawnAndNoKnightAndNoDarkSquareBishop(oppositeSide, bitboardPosition);
    }
    if (hasZeroOrMultipleDarkSquareBishopOnly(side, bitboardPosition)) {
      return hasNoPawnAndNoKnightAndNoLightSquareBishop(oppositeSide, bitboardPosition);
    }

    return false;
  }

  private static boolean hasZeroOrMultipleSquareBishopOnlyForSpecifiedColor(Side side,
      BitboardPosition bitboardPosition, SquareType squareType) {
    final Piece king = Piece.of(side, KING);
    final Piece bishop = Piece.of(side, BISHOP);
    for (final Square boardSquare : Square.REAL) {
      final Piece pieceOnSquare = bitboardPosition.get(boardSquare);
      if (InsufficientMaterialUtility.iisOwnPiece(side, pieceOnSquare)) {
        if (pieceOnSquare == king || pieceOnSquare == bishop && boardSquare.getSquareType() == squareType) {
          continue;
        }
        return false;
      }
    }
    return true;
  }

  static boolean hasZeroOrMultipleLightSquareBishopOnly(Side side, BitboardPosition bitboardPosition) {
    return hasZeroOrMultipleSquareBishopOnlyForSpecifiedColor(side, bitboardPosition, SquareType.LIGHT_SQUARE);
  }

  static boolean hasZeroOrMultipleDarkSquareBishopOnly(Side side, BitboardPosition bitboardPosition) {
    return hasZeroOrMultipleSquareBishopOnlyForSpecifiedColor(side, bitboardPosition, SquareType.DARK_SQUARE);
  }

  private static boolean hasZeroOrMultipleQueenOnly(Side side, BitboardPosition bitboardPosition) {
    final Piece king = Piece.of(side, KING);
    final Piece queen = Piece.of(side, QUEEN);
    for (final Square boardSquare : Square.REAL) {
      final Piece pieceOnSquare = bitboardPosition.get(boardSquare);
      if (InsufficientMaterialUtility.iisOwnPiece(side, pieceOnSquare)) {
        if (pieceOnSquare == king || pieceOnSquare == queen) {
          continue;
        }
        return false;
      }
    }
    return true;
  }

  private static boolean hasNoPawnAndNoKnightAndNoLightSquareBishop(Side side, BitboardPosition bitboardPosition) {
    return !hasPawn(side, bitboardPosition) && !hasKnight(side, bitboardPosition)
        && !hasBishopForSpecifiedColor(side, SquareType.LIGHT_SQUARE, bitboardPosition);
  }

  private static boolean hasNoPawnAndNoKnightAndNoDarkSquareBishop(Side side, BitboardPosition bitboardPosition) {
    return !hasPawn(side, bitboardPosition) && !hasKnight(side, bitboardPosition)
        && !hasBishopForSpecifiedColor(side, SquareType.DARK_SQUARE, bitboardPosition);
  }

  private static boolean hasPawn(Side side, BitboardPosition bitboardPosition) {
    return InsufficientMaterialUtility.hasPieceType(side, PAWN, bitboardPosition);
  }

  private static boolean hasKnight(Side side, BitboardPosition bitboardPosition) {
    return InsufficientMaterialUtility.hasPieceType(side, KNIGHT, bitboardPosition);
  }

  private static boolean hasBishopForSpecifiedColor(Side side, SquareType squareType,
      BitboardPosition bitboardPosition) {
    final Piece bishop = Piece.of(side, BISHOP);
    for (final Square boardSquare : Square.REAL) {
      final Piece pieceOnSquare = bitboardPosition.get(boardSquare);
      if (pieceOnSquare == bishop && boardSquare.getSquareType() == squareType) {
        return true;
      }
    }
    return false;
  }

  private static boolean iisOwnPiece(Side side, Piece pieceOnSquare) {
    return pieceOnSquare != Piece.NONE && pieceOnSquare.getSide() == side;
  }

  private static boolean hasPieceType(Side side, PieceType pieceType, BitboardPosition bitboardPosition) {
    final Piece piece = Piece.of(side, pieceType);
    for (final Square boardSquare : Square.REAL) {
      if (bitboardPosition.get(boardSquare) == piece) {
        return true;
      }
    }
    return false;
  }

  static boolean hasKingOnly(Side side, BitboardPosition bitboardPosition) {
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

  static boolean hasKingAndKnightOnly(Side side, BitboardPosition bitboardPosition) {
    return hasKingAndAnotherPieceOnly(side, KNIGHT, bitboardPosition);
  }

  static boolean hasKingAndBishopOnly(Side side, BitboardPosition bitboardPosition) {
    return hasKingAndAnotherPieceOnly(side, BISHOP, bitboardPosition);
  }

  private static boolean hasKingAndAnotherPieceOnly(Side side, PieceType anotherPieceType,
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
