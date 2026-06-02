// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.unwinnability;

import io.github.dlbbld.ashlarchess.board.StaticPosition;
import io.github.dlbbld.ashlarchess.board.enums.Piece;
import io.github.dlbbld.ashlarchess.board.enums.PieceType;
import io.github.dlbbld.ashlarchess.board.enums.Side;
import io.github.dlbbld.ashlarchess.board.enums.Square;
import io.github.dlbbld.ashlarchess.board.enums.SquareType;
import io.github.dlbbld.ashlarchess.common.constants.EnumConstants;

/**
 * StaticPosition-backed material predicates used by the unwinnability/helpmate analysis. Reference implementations of
 * the same predicates that {@link UnwinnabilityMaterialBitboard} computes from a
 * {@link io.github.dlbbld.ashlarchess.bitboard .BitboardPosition}. This class is the differential-test oracle -
 * production callers all consume the bitboard sibling. When Phase 6 of the switchover lands and the StaticPosition
 * subtree relocates to {@code src/test/}, this class moves with it as a single {@code git mv}.
 */
abstract class UnwinnabilityMaterial implements EnumConstants {

  // --- existence checks (any side or specific side) ---

  static boolean calculateHasRook(StaticPosition staticPosition) {
    return hasAnySide(PieceType.ROOK, staticPosition);
  }

  static boolean calculateHasRook(Side side, StaticPosition staticPosition) {
    return hasPieceType(side, PieceType.ROOK, staticPosition);
  }

  static boolean calculateHasKnight(StaticPosition staticPosition) {
    return hasAnySide(PieceType.KNIGHT, staticPosition);
  }

  static boolean calculateHasKnight(Side side, StaticPosition staticPosition) {
    return hasPieceType(side, PieceType.KNIGHT, staticPosition);
  }

  static boolean calculateHasQueen(StaticPosition staticPosition) {
    return hasAnySide(PieceType.QUEEN, staticPosition);
  }

  static boolean calculateHasQueen(Side side, StaticPosition staticPosition) {
    return hasPieceType(side, PieceType.QUEEN, staticPosition);
  }

  // --- absence checks ---

  static boolean calculateHasNoRooks(Side side, StaticPosition staticPosition) {
    return !hasPieceType(side, PieceType.ROOK, staticPosition);
  }

  static boolean calculateHasNoKnights(Side side, StaticPosition staticPosition) {
    return !hasPieceType(side, PieceType.KNIGHT, staticPosition);
  }

  static boolean calculateHasNoBishops(Side side, StaticPosition staticPosition) {
    return !hasPieceType(side, PieceType.BISHOP, staticPosition);
  }

  static boolean calculateHasNoBishops(Side side, StaticPosition staticPosition, SquareType squareType) {
    return countBishops(side, staticPosition, squareType) == 0;
  }

  static boolean calculateHasNoPawns(Side side, StaticPosition staticPosition) {
    return !hasPieceType(side, PieceType.PAWN, staticPosition);
  }

  // --- bishop colour-class checks ---

  static boolean calculateHasLightSquareBishops(Side side, StaticPosition staticPosition) {
    return countBishops(side, staticPosition, SquareType.LIGHT_SQUARE) > 0;
  }

  static boolean calculateHasDarkSquareBishops(Side side, StaticPosition staticPosition) {
    return countBishops(side, staticPosition, SquareType.DARK_SQUARE) > 0;
  }

  // --- aggregate shape checks ---

  static boolean calculateHasKingOnly(Side side, StaticPosition staticPosition) {
    int countKing = 0;
    for (final Square boardSquare : Square.REAL) {
      final Piece pieceOnSquare = staticPosition.get(boardSquare);
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

  static boolean calculateHasKingAndKnightOnly(Side side, StaticPosition staticPosition) {
    int countKing = 0;
    int countKnights = 0;
    for (final Square boardSquare : Square.REAL) {
      final Piece pieceOnSquare = staticPosition.get(boardSquare);
      if (pieceOnSquare == Piece.NONE || pieceOnSquare.getSide() != side) {
        continue;
      }
      if (pieceOnSquare.getPieceType() == KING) {
        countKing++;
        continue;
      }
      if (pieceOnSquare.getPieceType() != KNIGHT) {
        return false;
      }
      countKnights++;
      if (countKnights > 1) {
        return false;
      }
    }
    return countKing == 1 && countKnights == 1;
  }

  static boolean calculateHasKingAndBishopsOnly(Side side, StaticPosition staticPosition, SquareType squareType) {
    return countPieces(side, staticPosition, PieceType.ROOK) == 0
        && countPieces(side, staticPosition, PieceType.KNIGHT) == 0
        && countBishops(side, staticPosition, squareType) >= 1
        && countBishops(side, staticPosition, squareType.getOppositeSquareType()) == 0
        && countPieces(side, staticPosition, PieceType.QUEEN) == 0
        && countPieces(side, staticPosition, PieceType.KING) == 1
        && countPieces(side, staticPosition, PieceType.PAWN) == 0;
  }

  // --- private helpers ---

  private static boolean hasAnySide(PieceType pieceType, StaticPosition staticPosition) {
    return hasPieceType(Side.WHITE, pieceType, staticPosition) || hasPieceType(Side.BLACK, pieceType, staticPosition);
  }

  private static boolean hasPieceType(Side side, PieceType pieceType, StaticPosition staticPosition) {
    final Piece piece = Piece.calculate(side, pieceType);
    for (final Square boardSquare : Square.REAL) {
      if (staticPosition.get(boardSquare) == piece) {
        return true;
      }
    }
    return false;
  }

  private static int countPieces(Side side, StaticPosition staticPosition, PieceType pieceType) {
    final Piece piece = Piece.calculate(side, pieceType);
    int total = 0;
    for (final Square boardSquare : Square.REAL) {
      if (staticPosition.get(boardSquare) == piece) {
        total++;
      }
    }
    return total;
  }

  private static int countBishops(Side side, StaticPosition staticPosition, SquareType squareType) {
    final Piece bishop = Piece.calculate(side, PieceType.BISHOP);
    int total = 0;
    for (final Square boardSquare : Square.REAL) {
      if (staticPosition.get(boardSquare) == bishop && boardSquare.getSquareType() == squareType) {
        total++;
      }
    }
    return total;
  }
}
