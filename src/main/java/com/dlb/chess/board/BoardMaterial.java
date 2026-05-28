package com.dlb.chess.board;

import com.dlb.chess.bitboard.BitboardPosition;
import com.dlb.chess.board.enums.Piece;
import com.dlb.chess.board.enums.PieceType;
import com.dlb.chess.board.enums.Side;
import com.dlb.chess.board.enums.Square;
import com.dlb.chess.common.constants.EnumConstants;

/**
 * Internal material checks needed by the board package's rule helpers (currently {@link InsufficientMaterialUtility}).
 * Kept narrow and package-private: the public API does not expose material arithmetic.
 */
abstract class BoardMaterial implements EnumConstants {

  static boolean calculateIsOwnPiece(Side side, Piece pieceOnSquare) {
    return pieceOnSquare != Piece.NONE && pieceOnSquare.getSide() == side;
  }

  static boolean calculateHasPieceType(Side side, PieceType pieceType, BitboardPosition bitboardPosition) {
    final Piece piece = Piece.calculate(side, pieceType);
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
