package com.dlb.chess.bitboard;

/**
 * Queen attacks: the union of the bishop and rook attack patterns from the same square. Delegates to
 * {@link BishopAttacks} and {@link RookAttacks}; the union is the only thing that makes a queen a queen at the bitboard
 * layer.
 */
public final class QueenAttacks {

  private QueenAttacks() {
  }

  public static long attacks(int squareOrdinal, long occupied) {
    return BishopAttacks.attacks(squareOrdinal, occupied) | RookAttacks.attacks(squareOrdinal, occupied);
  }
}
