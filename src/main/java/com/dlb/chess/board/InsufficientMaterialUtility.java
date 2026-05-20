package com.dlb.chess.board;

import com.dlb.chess.bitboard.BitboardPosition;
import com.dlb.chess.board.enums.Piece;
import com.dlb.chess.board.enums.Side;
import com.dlb.chess.board.enums.Square;
import com.dlb.chess.board.enums.SquareType;
import com.dlb.chess.common.constants.EnumConstants;

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
    final Piece king = Piece.calculate(side, KING);
    final Piece bishop = Piece.calculate(side, BISHOP);
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

  public static boolean calculateHasZeroOrMultipleLightSquareBishopOnly(Side side, BitboardPosition bitboardPosition) {
    return calculateHasZeroOrMultipleSquareBishopOnlyForSpecifiedColor(side, bitboardPosition, SquareType.LIGHT_SQUARE);
  }

  public static boolean calculateHasZeroOrMultipleDarkSquareBishopOnly(Side side, BitboardPosition bitboardPosition) {
    return calculateHasZeroOrMultipleSquareBishopOnlyForSpecifiedColor(side, bitboardPosition, SquareType.DARK_SQUARE);
  }

  private static boolean calculateHasZeroOrMultipleQueenOnly(Side side, BitboardPosition bitboardPosition) {
    final Piece king = Piece.calculate(side, KING);
    final Piece queen = Piece.calculate(side, QUEEN);
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
    final Piece bishop = Piece.calculate(side, BISHOP);
    for (final Square boardSquare : Square.REAL) {
      final Piece pieceOnSquare = bitboardPosition.get(boardSquare);
      if (pieceOnSquare == bishop && boardSquare.getSquareType() == squareType) {
        return true;
      }
    }
    return false;
  }

}
