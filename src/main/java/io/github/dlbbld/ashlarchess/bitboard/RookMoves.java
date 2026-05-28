package io.github.dlbbld.ashlarchess.bitboard;

/**
 * Pseudo-legal rook target squares: the rook's classical-ray attack pattern (which already includes the first blocker
 * square in each direction) minus own-piece-occupied squares.
 */
public final class RookMoves {

  private RookMoves() {
  }

  public static long targets(int squareOrdinal, long occupied, long ownPieces) {
    return RookAttacks.attacks(squareOrdinal, occupied) & ~ownPieces;
  }
}
