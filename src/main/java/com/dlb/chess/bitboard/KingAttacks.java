package com.dlb.chess.bitboard;

import com.dlb.chess.board.enums.Square;

/**
 * Precomputed king-attack table (non-castling). For any square {@code sq}, {@link #attacks(Square)} returns the
 * bitboard of the 3-8 squares a king on {@code sq} attacks on an otherwise empty board. Castling targets live on
 * {@link com.dlb.chess.board.Board} together with the castling-rights state and are intentionally not part of this
 * geometric layer. Differential-tested bit-exact against {@code KingNonCastlingEmptyBoardSquares.getKingSquares} (the
 * {@code StaticPosition}-layer table, test-side since the 11.0.0 role-inversion release).
 */
public final class KingAttacks {

  private static final int[][] KING_OFFSETS = { { 0, 1 }, { 0, -1 }, { 1, 0 }, { -1, 0 }, { 1, 1 }, { 1, -1 },
      { -1, 1 }, { -1, -1 } };

  private static final long[] ATTACKS = computeAttacks();

  private static long[] computeAttacks() {
    final long[] table = new long[64];
    for (int squareOrdinal = 0; squareOrdinal < 64; squareOrdinal++) {
      final int fromFile = squareOrdinal % 8;
      final int fromRank = squareOrdinal / 8;
      long attacks = 0L;
      for (final int[] offset : KING_OFFSETS) {
        final int toFile = fromFile + offset[0];
        final int toRank = fromRank + offset[1];
        if (toFile >= 0 && toFile < 8 && toRank >= 0 && toRank < 8) {
          attacks |= 1L << (toRank * 8 + toFile);
        }
      }
      table[squareOrdinal] = attacks;
    }
    return table;
  }

  private KingAttacks() {
  }

  public static long attacks(Square square) {
    if (square == Square.NONE) {
      throw new IllegalArgumentException("The NONE square does not belong to the board");
    }
    return ATTACKS[square.ordinal()];
  }
}
