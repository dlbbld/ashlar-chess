package com.dlb.chess.unwinnability;

import com.dlb.chess.bitboard.BitboardPosition;
import com.dlb.chess.board.StaticPosition;
import com.dlb.chess.board.enums.Piece;
import com.dlb.chess.board.enums.PieceType;
import com.dlb.chess.board.enums.Side;
import com.dlb.chess.board.enums.Square;
import com.dlb.chess.board.enums.SquareType;
import com.dlb.chess.common.constants.EnumConstants;

/**
 * Internal material predicates used by the unwinnability/helpmate analysis. Not part of the public API: the library's
 * public surface is the {@link UnwinnableFullAnalyzer} / {@link UnwinnableQuickAnalyzer} entry points, not the
 * per-square material arithmetic.
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
    var countKing = 0;
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
    var countKing = 0;
    var countKnights = 0;
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

  // ===========================================================================================
  // Bitboard variants. Same predicates, BitboardPosition input. Used by callers being ported off
  // the StaticPosition surface as part of the switchover release.
  // ===========================================================================================

  // Square-colour masks: A1 is dark, B1 is light, alternating. Bit i is light iff (file_i + rank_i) is odd.
  private static final long LIGHT_SQUARES = 0x55AA55AA55AA55AAL;
  private static final long DARK_SQUARES = ~LIGHT_SQUARES;

  static boolean calculateHasRook(BitboardPosition bitboardPosition) {
    return (bitboardPosition.whiteRooks() | bitboardPosition.blackRooks()) != 0L;
  }

  static boolean calculateHasRook(Side side, BitboardPosition bitboardPosition) {
    return (side == Side.WHITE ? bitboardPosition.whiteRooks() : bitboardPosition.blackRooks()) != 0L;
  }

  static boolean calculateHasKnight(BitboardPosition bitboardPosition) {
    return (bitboardPosition.whiteKnights() | bitboardPosition.blackKnights()) != 0L;
  }

  static boolean calculateHasKnight(Side side, BitboardPosition bitboardPosition) {
    return (side == Side.WHITE ? bitboardPosition.whiteKnights() : bitboardPosition.blackKnights()) != 0L;
  }

  static boolean calculateHasQueen(BitboardPosition bitboardPosition) {
    return (bitboardPosition.whiteQueens() | bitboardPosition.blackQueens()) != 0L;
  }

  static boolean calculateHasQueen(Side side, BitboardPosition bitboardPosition) {
    return (side == Side.WHITE ? bitboardPosition.whiteQueens() : bitboardPosition.blackQueens()) != 0L;
  }

  static boolean calculateHasNoRooks(Side side, BitboardPosition bitboardPosition) {
    return !calculateHasRook(side, bitboardPosition);
  }

  static boolean calculateHasNoKnights(Side side, BitboardPosition bitboardPosition) {
    return !calculateHasKnight(side, bitboardPosition);
  }

  static boolean calculateHasNoBishops(Side side, BitboardPosition bitboardPosition) {
    return (side == Side.WHITE ? bitboardPosition.whiteBishops() : bitboardPosition.blackBishops()) == 0L;
  }

  static boolean calculateHasNoBishops(Side side, BitboardPosition bitboardPosition, SquareType squareType) {
    return countBishops(side, bitboardPosition, squareType) == 0;
  }

  static boolean calculateHasNoPawns(Side side, BitboardPosition bitboardPosition) {
    return (side == Side.WHITE ? bitboardPosition.whitePawns() : bitboardPosition.blackPawns()) == 0L;
  }

  static boolean calculateHasLightSquareBishops(Side side, BitboardPosition bitboardPosition) {
    final long bishops = side == Side.WHITE ? bitboardPosition.whiteBishops() : bitboardPosition.blackBishops();
    return (bishops & LIGHT_SQUARES) != 0L;
  }

  static boolean calculateHasDarkSquareBishops(Side side, BitboardPosition bitboardPosition) {
    final long bishops = side == Side.WHITE ? bitboardPosition.whiteBishops() : bitboardPosition.blackBishops();
    return (bishops & DARK_SQUARES) != 0L;
  }

  static boolean calculateHasKingOnly(Side side, BitboardPosition bitboardPosition) {
    final long sideOccupancy = bitboardPosition.occupied(side);
    final long sideKings = side == Side.WHITE ? bitboardPosition.whiteKings() : bitboardPosition.blackKings();
    return sideOccupancy == sideKings && Long.bitCount(sideKings) == 1;
  }

  static boolean calculateHasKingAndKnightOnly(Side side, BitboardPosition bitboardPosition) {
    final long sideKings = side == Side.WHITE ? bitboardPosition.whiteKings() : bitboardPosition.blackKings();
    final long sideKnights = side == Side.WHITE ? bitboardPosition.whiteKnights() : bitboardPosition.blackKnights();
    final long sideOccupancy = bitboardPosition.occupied(side);
    return sideOccupancy == (sideKings | sideKnights) && Long.bitCount(sideKings) == 1 && Long.bitCount(sideKnights) == 1;
  }

  static boolean calculateHasKingAndBishopsOnly(Side side, BitboardPosition bitboardPosition, SquareType squareType) {
    final long sideKings = side == Side.WHITE ? bitboardPosition.whiteKings() : bitboardPosition.blackKings();
    final long sideBishops = side == Side.WHITE ? bitboardPosition.whiteBishops() : bitboardPosition.blackBishops();
    final long sideOccupancy = bitboardPosition.occupied(side);
    if (sideOccupancy != (sideKings | sideBishops)) {
      return false;
    }
    if (Long.bitCount(sideKings) != 1) {
      return false;
    }
    final long colourMask = squareType == SquareType.LIGHT_SQUARE ? LIGHT_SQUARES : DARK_SQUARES;
    return (sideBishops & colourMask) != 0L && (sideBishops & ~colourMask) == 0L;
  }

  private static int countBishops(Side side, BitboardPosition bitboardPosition, SquareType squareType) {
    final long bishops = side == Side.WHITE ? bitboardPosition.whiteBishops() : bitboardPosition.blackBishops();
    final long colourMask = squareType == SquareType.LIGHT_SQUARE ? LIGHT_SQUARES : DARK_SQUARES;
    return Long.bitCount(bishops & colourMask);
  }

  // ===========================================================================================
  // StaticPosition variants — legacy callers. To be removed once all sites switch to bitboard.
  // ===========================================================================================

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
    var total = 0;
    for (final Square boardSquare : Square.REAL) {
      if (staticPosition.get(boardSquare) == piece) {
        total++;
      }
    }
    return total;
  }

  private static int countBishops(Side side, StaticPosition staticPosition, SquareType squareType) {
    final Piece bishop = Piece.calculate(side, PieceType.BISHOP);
    var total = 0;
    for (final Square boardSquare : Square.REAL) {
      if (staticPosition.get(boardSquare) == bishop && boardSquare.getSquareType() == squareType) {
        total++;
      }
    }
    return total;
  }

}
