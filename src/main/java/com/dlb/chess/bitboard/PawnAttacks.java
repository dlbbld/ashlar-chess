package com.dlb.chess.bitboard;

import com.dlb.chess.board.enums.Side;
import com.dlb.chess.board.enums.Square;

/**
 * Precomputed pawn-attack tables, one {@code long[64]} per side. {@link #attacks(Square, Side)} returns the diagonal
 * capture squares a pawn of the given side would reach from the given from-square - the <em>geometric</em> pattern,
 * defined for all 64 from-squares (including rank 1 and rank 8). Off-board target ranks return {@code 0L}.
 *
 * <p>
 * This is deliberately geometric, not chess-legality-restricted. The reference {@code PawnDiagonalSquares} returns an
 * empty set for from-squares on rank 1 / rank 8 ("pawns only exist on ranks 2-7"), and that convention is the right one
 * for forward queries - real pawn bitboards never have bits on those ranks anyway. But the bitboard layer also uses the
 * reverse-attack identity in {@code BitboardPosition.attackersTo}: white pawns attacking a target X are the squares
 * returned by {@code PawnAttacks.attacks(X, BLACK)}. That query is well-defined for target X on <em>any</em> rank,
 * including the back ranks where promotions resolve. Hence the geometric pattern.
 *
 * <p>
 * For ranks 2-7 the bitboard table agrees bit-exact with {@code PawnDiagonalSquares}; ranks 1 and 8 are intentionally
 * non-empty here and the differential test below restricts the direct comparison accordingly.
 */
public final class PawnAttacks {

  private static final long[] WHITE_ATTACKS = computeAttacks(Side.WHITE);
  private static final long[] BLACK_ATTACKS = computeAttacks(Side.BLACK);

  private static long[] computeAttacks(Side side) {
    final long[] table = new long[64];
    final int rankOffset = side == Side.WHITE ? 1 : -1;
    for (int squareOrdinal = 0; squareOrdinal < 64; squareOrdinal++) {
      final int fromFile = squareOrdinal % 8;
      final int fromRank = squareOrdinal / 8;
      final int toRank = fromRank + rankOffset;
      if (toRank < 0 || toRank > 7) {
        continue;
      }
      long attacks = 0L;
      if (fromFile - 1 >= 0) {
        attacks |= 1L << (toRank * 8 + (fromFile - 1));
      }
      if (fromFile + 1 <= 7) {
        attacks |= 1L << (toRank * 8 + (fromFile + 1));
      }
      table[squareOrdinal] = attacks;
    }
    return table;
  }

  private PawnAttacks() {
  }

  public static long attacks(Square square, Side side) {
    if (square == Square.NONE) {
      throw new IllegalArgumentException("The NONE square does not belong to the board");
    }
    return switch (side) {
      case WHITE -> WHITE_ATTACKS[square.ordinal()];
      case BLACK -> BLACK_ATTACKS[square.ordinal()];
      case NONE -> throw new IllegalArgumentException("Side.NONE does not have pawn attacks");
      default -> throw new IllegalArgumentException();
    };
  }
}
