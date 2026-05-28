package io.github.dlbbld.ashlarchess.bitboard;

/**
 * Pseudo-legal bishop target squares: the bishop's classical-ray attack pattern (which already includes the first
 * blocker square in each direction) minus own-piece-occupied squares. Empty squares and opponent-occupied first
 * blockers remain. Pin and check filtering are applied in the legal-move generation layer.
 */
public final class BishopMoves {

  private BishopMoves() {
  }

  public static long targets(int squareOrdinal, long occupied, long ownPieces) {
    return BishopAttacks.attacks(squareOrdinal, occupied) & ~ownPieces;
  }
}
