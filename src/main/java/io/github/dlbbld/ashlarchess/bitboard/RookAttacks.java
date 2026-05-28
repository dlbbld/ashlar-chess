// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.bitboard;

/**
 * Rook attacks via classical ray loops. {@link #attacks(int, long)} returns the bitboard of squares a rook on
 * {@code squareOrdinal} attacks given an {@code occupied}-mask of all pieces on the board. Each of the four orthogonal
 * rays (N, S, E, W) accumulates target bits until the ray falls off the edge or hits an occupied square (the blocker
 * bit is included before stopping) - matching the existing reference's "isAllowOwnPiece = true" attack semantics.
 *
 * <p>
 * Classical ray loops are the chosen implementation here; magic-bitboard acceleration is deferred to the switchover
 * release.
 */
public final class RookAttacks {

  private RookAttacks() {
  }

  public static long attacks(int squareOrdinal, long occupied) {
    if (squareOrdinal < 0 || squareOrdinal >= 64) {
      throw new IllegalArgumentException("squareOrdinal out of range: " + squareOrdinal);
    }
    final int fromFile = squareOrdinal % 8;
    final int fromRank = squareOrdinal / 8;
    long attacks = 0L;
    attacks |= rayAttacks(fromFile, fromRank, +1, 0, occupied);
    attacks |= rayAttacks(fromFile, fromRank, -1, 0, occupied);
    attacks |= rayAttacks(fromFile, fromRank, 0, +1, occupied);
    attacks |= rayAttacks(fromFile, fromRank, 0, -1, occupied);
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
