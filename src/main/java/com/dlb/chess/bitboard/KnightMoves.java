package com.dlb.chess.bitboard;

import com.dlb.chess.board.enums.Square;

/**
 * Pseudo-legal knight target squares: the knight's geometric attack pattern from {@code fromSquare}, minus the
 * squares occupied by own pieces. Empty squares and opponent-occupied squares (captures) remain. King-safety and pin
 * filtering are applied later, in the legal-move generation layer.
 */
public final class KnightMoves {

  private KnightMoves() {
  }

  public static long targets(Square fromSquare, long ownPieces) {
    return KnightAttacks.attacks(fromSquare) & ~ownPieces;
  }
}
