package io.github.dlbbld.ashlarchess.bitboard;

/**
 * Bishop attacks via classical ray loops. {@link #attacks(int, long)} returns the bitboard of squares a bishop on
 * {@code squareOrdinal} attacks given an {@code occupied}-mask of all pieces on the board. The result includes every
 * empty square along each of the four diagonals plus the first blocker in that direction (regardless of colour) -
 * matching the existing reference's "isAllowOwnPiece = true" semantics for attacked squares (own pieces are defended).
 *
 * <p>
 * Classical ray loops are deliberately the choice here; magic-bitboard acceleration is deferred to the switchover
 * release where it actually earns its keep on the hot path. The shape of this API ({@code int sq, long occupied})
 * matches what magics would slot into.
 */
public final class BishopAttacks {

  private BishopAttacks() {
  }

  public static long attacks(int squareOrdinal, long occupied) {
    if (squareOrdinal < 0 || squareOrdinal >= 64) {
      throw new IllegalArgumentException("squareOrdinal out of range: " + squareOrdinal);
    }
    final int fromFile = squareOrdinal % 8;
    final int fromRank = squareOrdinal / 8;
    long attacks = 0L;
    attacks |= rayAttacks(fromFile, fromRank, +1, +1, occupied);
    attacks |= rayAttacks(fromFile, fromRank, -1, +1, occupied);
    attacks |= rayAttacks(fromFile, fromRank, +1, -1, occupied);
    attacks |= rayAttacks(fromFile, fromRank, -1, -1, occupied);
    return attacks;
  }

  private static long rayAttacks(int fromFile, int fromRank, int fileStep, int rankStep, long occupied) {
    long attacks = 0L;
    int file = fromFile + fileStep;
    int rank = fromRank + rankStep;
    while (file >= 0 && file < 8 && rank >= 0 && rank < 8) {
      final long targetBit = 1L << (rank * 8 + file);
      attacks |= targetBit;
      if ((targetBit & occupied) != 0L) {
        break;
      }
      file += fileStep;
      rank += rankStep;
    }
    return attacks;
  }
}
