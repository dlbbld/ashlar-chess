package com.dlb.chess.bitboard;

import com.dlb.chess.board.enums.Square;

/**
 * Precomputed knight-attack table. The geometric pattern is position-independent: for any square {@code sq},
 * {@link #attacks(Square)} returns the bitboard of the 2–8 squares a knight on {@code sq} attacks on an otherwise
 * empty board. Differential-tested bit-exact against {@code KnightEmptyBoardSquares.getKnightSquares} (the
 * {@code StaticPosition}-layer table, test-side since the 11.0.0 role-inversion release).
 */
public final class KnightAttacks {

  private static final int[][] KNIGHT_OFFSETS = { { 1, 2 }, { 1, -2 }, { -1, 2 }, { -1, -2 }, { 2, 1 }, { 2, -1 },
      { -2, 1 }, { -2, -1 } };

  private static final long[] ATTACKS = computeAttacks();

  private static long[] computeAttacks() {
    final long[] table = new long[64];
    for (int squareOrdinal = 0; squareOrdinal < 64; squareOrdinal++) {
      final int fromFile = squareOrdinal % 8;
      final int fromRank = squareOrdinal / 8;
      long attacks = 0L;
      for (final int[] offset : KNIGHT_OFFSETS) {
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

  private KnightAttacks() {
  }

  public static long attacks(Square square) {
    if (square == Square.NONE) {
      throw new IllegalArgumentException("The NONE square does not belong to the board");
    }
    return ATTACKS[square.ordinal()];
  }
}
