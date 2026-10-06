// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.bitboard.internal;

/**
 * Slider attacks by walking rays, one square at a time, until the ray leaves the board or meets a blocker. The blocker
 * square itself is included regardless of its colour, so own pieces read as defended.
 *
 * <p>
 * This is the reference semantics for sliding attacks. {@link MagicSliderAttacks} builds its lookup tables by calling
 * these methods over every relevant occupancy, so the two cannot disagree about what a slider attacks - the table is a
 * cache of this code's answers, and the only thing the magic layer adds is the indexing. That makes this class the
 * oracle the differential tests compare against, and the reason it stays in the production tree rather than being
 * deleted.
 *
 * <p>
 * The four ray calls per piece are written out rather than looped over a direction table, which is also how this code
 * read before the magic tables arrived. That is deliberate and must stay: with the steps as constant arguments the
 * compiler specializes each call, and folding them into a loop over an {@code int[][]} costs roughly 50% on bishops and
 * 65% on rooks. Since this class doubles as the timed baseline for the magic-bitboard comparison, a rewrite that slowed
 * it down would silently inflate the measured speedup rather than show up as a regression.
 */
public final class SliderRayAttacks {

  private SliderRayAttacks() {
  }

  /** Bishop attacks from {@code squareOrdinal} given {@code occupied}. */
  public static long bishop(int squareOrdinal, long occupied) {
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

  /** Rook attacks from {@code squareOrdinal} given {@code occupied}. */
  public static long rook(int squareOrdinal, long occupied) {
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
