package com.dlb.chess.bitboard;

import com.dlb.chess.board.enums.Square;

/**
 * Pseudo-legal king target squares (non-castling): the king's geometric attack pattern from {@code fromSquare},
 * minus own-piece-occupied squares. Castling lives on {@code Board} with the castling-rights state and is not part
 * of this bitboard layer. King-safety (don't move into check) is applied in the legal-move generation layer.
 */
public final class KingMoves {

  private KingMoves() {
  }

  public static long targets(Square fromSquare, long ownPieces) {
    return KingAttacks.attacks(fromSquare) & ~ownPieces;
  }
}
