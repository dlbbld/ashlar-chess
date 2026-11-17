// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.fen;

import io.github.dlbbld.ashlarchess.bitboard.BitboardPosition;
import io.github.dlbbld.ashlarchess.board.enums.Piece;
import io.github.dlbbld.ashlarchess.board.enums.PieceType;
import io.github.dlbbld.ashlarchess.board.enums.Side;
import io.github.dlbbld.ashlarchess.board.enums.Square;
import io.github.dlbbld.ashlarchess.board.enums.SquareType;
import io.github.dlbbld.ashlarchess.internal.ChessConstants;

/**
 * Internal piece-count helpers used by FEN strict semantic validation. Not part of the public API: the library exposes
 * parsing and outcome reporting, not material arithmetic.
 */
final class FenMaterialCount {

  private FenMaterialCount() {
  }

  // A negative surplus leaves room to restore a captured original piece of that type.
  record Balance(int kings, int missingPawns, int rookSurplus, int knightSurplus, int lightBishopSurplus,
      int darkBishopSurplus, int queenSurplus) {
  }

  static Balance balance(Side side, BitboardPosition position) {
    return new Balance(calculateNumberOfPieces(side, position, PieceType.KING),
        ChessConstants.INITIAL_NUMBER_OF_PAWNS - calculateNumberOfPieces(side, position, PieceType.PAWN),
        calculateNumberOfPieces(side, position, PieceType.ROOK) - ChessConstants.INITIAL_NUMBER_OF_ROOKS,
        calculateNumberOfPieces(side, position, PieceType.KNIGHT) - ChessConstants.INITIAL_NUMBER_OF_KNIGHTS,
        calculateNumberOfBishops(side, position, SquareType.LIGHT_SQUARE)
            - ChessConstants.INITIAL_NUMBER_OF_LIGHT_SQUARE_BISHOPS,
        calculateNumberOfBishops(side, position, SquareType.DARK_SQUARE)
            - ChessConstants.INITIAL_NUMBER_OF_DARK_SQUARE_BISHOPS,
        calculateNumberOfPieces(side, position, PieceType.QUEEN) - ChessConstants.INITIAL_NUMBER_OF_QUEENS);
  }

  static int requiredPromotions(Balance balance) {
    return Math.max(0, balance.rookSurplus()) + Math.max(0, balance.knightSurplus())
        + Math.max(0, balance.lightBishopSurplus()) + Math.max(0, balance.darkBishopSurplus())
        + Math.max(0, balance.queenSurplus());
  }

  static boolean canBePromoted(Balance balance, PieceType pieceType, Square square) {
    final int surplus = switch (pieceType) {
      case QUEEN -> balance.queenSurplus();
      case ROOK -> balance.rookSurplus();
      case KNIGHT -> balance.knightSurplus();
      case BISHOP -> bishopSurplus(balance, square);
      case PAWN, KING, NONE -> throw new IllegalArgumentException("Invalid promotion piece: " + pieceType);
      default -> throw new IllegalArgumentException("Invalid promotion piece: " + pieceType);
    };
    return surplus > 0 || requiredPromotions(balance) < balance.missingPawns();
  }

  // A capture on the back rank can restore any missing non-pawn other than a bishop of the opposite square color.
  static boolean canRestorePromotionVictim(Balance balance, Square square) {
    return balance.queenSurplus() < 0 || balance.rookSurplus() < 0 || balance.knightSurplus() < 0
        || bishopSurplus(balance, square) < 0 || requiredPromotions(balance) < balance.missingPawns();
  }

  private static int bishopSurplus(Balance balance, Square square) {
    return square.getSquareType() == SquareType.LIGHT_SQUARE ? balance.lightBishopSurplus()
        : balance.darkBishopSurplus();
  }

  private static int calculateNumberOfPieces(Side side, BitboardPosition bitboardPosition, PieceType pieceType) {
    final Piece piece = Piece.of(side, pieceType);
    int total = 0;
    for (final Square boardSquare : Square.REAL) {
      if (bitboardPosition.get(boardSquare) == piece) {
        total++;
      }
    }
    return total;
  }

  private static int calculateNumberOfBishops(Side side, BitboardPosition bitboardPosition, SquareType squareType) {
    final Piece bishop = Piece.of(side, PieceType.BISHOP);
    int total = 0;
    for (final Square boardSquare : Square.REAL) {
      if (bitboardPosition.get(boardSquare) == bishop && boardSquare.getSquareType() == squareType) {
        total++;
      }
    }
    return total;
  }
}
