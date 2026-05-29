// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.unwinnability;

import io.github.dlbbld.ashlarchess.bitboard.BitboardPosition;
import io.github.dlbbld.ashlarchess.board.enums.Side;
import io.github.dlbbld.ashlarchess.board.enums.SquareType;
import io.github.dlbbld.ashlarchess.common.constants.EnumConstants;

/**
 * Bitboard-backed material predicates used by the unwinnability/helpmate analysis. Production callers in
 * {@link UnwinnableQuickAnalyzer}, {@link FindHelpmateExhaust}, and {@link GoingToCorner} consume this class.
 *
 * <p>
 * The StaticPosition-backed reference implementations of the same predicates live in the test tree as the permanent
 * differential-test oracle; this bitboard class is the production surface.
 */
abstract class UnwinnabilityMaterialBitboard implements EnumConstants {

  // Square-colour masks: A1 is dark, B1 is light, alternating. Bit i is light iff (file_i + rank_i) is odd.
  private static final long LIGHT_SQUARES = 0x55AA55AA55AA55AAL;
  private static final long DARK_SQUARES = ~LIGHT_SQUARES;

  // --- existence checks (any side or specific side) ---

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

  // --- absence checks ---

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

  // --- bishop colour-class checks ---

  static boolean calculateHasLightSquareBishops(Side side, BitboardPosition bitboardPosition) {
    final long bishops = side == Side.WHITE ? bitboardPosition.whiteBishops() : bitboardPosition.blackBishops();
    return (bishops & LIGHT_SQUARES) != 0L;
  }

  static boolean calculateHasDarkSquareBishops(Side side, BitboardPosition bitboardPosition) {
    final long bishops = side == Side.WHITE ? bitboardPosition.whiteBishops() : bitboardPosition.blackBishops();
    return (bishops & DARK_SQUARES) != 0L;
  }

  // --- aggregate shape checks ---

  static boolean calculateHasKingOnly(Side side, BitboardPosition bitboardPosition) {
    final long sideOccupancy = bitboardPosition.occupied(side);
    final long sideKings = side == Side.WHITE ? bitboardPosition.whiteKings() : bitboardPosition.blackKings();
    return sideOccupancy == sideKings && Long.bitCount(sideKings) == 1;
  }

  static boolean calculateIsInsufficientMaterial(Side side, BitboardPosition bitboardPosition) {
    final Side oppositeSide = side.getOppositeSide();
    if (calculateHasKingOnly(side, bitboardPosition)) {
      return true;
    }
    if (calculateHasKingAndKnightOnly(side, bitboardPosition)) {
      return calculateHasKingAndQueensOnly(oppositeSide, bitboardPosition);
    }
    if (calculateHasKingAndBishopsOnly(side, bitboardPosition, SquareType.LIGHT_SQUARE)) {
      return calculateHasNoPawns(oppositeSide, bitboardPosition)
          && calculateHasNoKnights(oppositeSide, bitboardPosition)
          && calculateHasNoBishops(oppositeSide, bitboardPosition, SquareType.DARK_SQUARE);
    }
    if (calculateHasKingAndBishopsOnly(side, bitboardPosition, SquareType.DARK_SQUARE)) {
      return calculateHasNoPawns(oppositeSide, bitboardPosition)
          && calculateHasNoKnights(oppositeSide, bitboardPosition)
          && calculateHasNoBishops(oppositeSide, bitboardPosition, SquareType.LIGHT_SQUARE);
    }
    return false;
  }

  static boolean calculateHasKingAndKnightOnly(Side side, BitboardPosition bitboardPosition) {
    final long sideKings = side == Side.WHITE ? bitboardPosition.whiteKings() : bitboardPosition.blackKings();
    final long sideKnights = side == Side.WHITE ? bitboardPosition.whiteKnights() : bitboardPosition.blackKnights();
    final long sideOccupancy = bitboardPosition.occupied(side);
    return sideOccupancy == (sideKings | sideKnights) && Long.bitCount(sideKings) == 1
        && Long.bitCount(sideKnights) == 1;
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

  private static boolean calculateHasKingAndQueensOnly(Side side, BitboardPosition bitboardPosition) {
    final long sideKings = side == Side.WHITE ? bitboardPosition.whiteKings() : bitboardPosition.blackKings();
    final long sideQueens = side == Side.WHITE ? bitboardPosition.whiteQueens() : bitboardPosition.blackQueens();
    final long sideOccupancy = bitboardPosition.occupied(side);
    return sideOccupancy == (sideKings | sideQueens) && Long.bitCount(sideKings) == 1;
  }
}
