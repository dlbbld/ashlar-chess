package com.dlb.chess.bitboard;

/**
 * Pseudo-legal queen target squares: bishop targets union rook targets, both with the same own-piece filter.
 */
public final class QueenMoves {

  private QueenMoves() {
  }

  public static long targets(int squareOrdinal, long occupied, long ownPieces) {
    return QueenAttacks.attacks(squareOrdinal, occupied) & ~ownPieces;
  }
}
