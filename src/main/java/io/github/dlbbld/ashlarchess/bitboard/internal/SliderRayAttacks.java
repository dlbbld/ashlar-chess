// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.bitboard.internal;

/**
 * Slider attacks by walking rays, one square at a time, until the ray leaves the board or meets a blocker. The blocker
 * square itself is included regardless of its colour, so own pieces read as defended.
 *
 * <p>
 * This is the reference semantics for sliding attacks. {@link MagicSliderAttacks} builds its lookup tables by calling
 * these methods over every relevant occupancy, so the two cannot disagree about what a slider attacks - the table is
 * a cache of this code's answers, and the only thing the magic layer adds is the indexing. That makes this class the
 * oracle the differential tests compare against, and the reason it stays in the production tree rather than being
 * replaced.
 */
public final class SliderRayAttacks {

  private static final int[][] BISHOP_STEPS = { { 1, 1 }, { -1, 1 }, { 1, -1 }, { -1, -1 } };
  private static final int[][] ROOK_STEPS = { { 1, 0 }, { -1, 0 }, { 0, 1 }, { 0, -1 } };

  private SliderRayAttacks() {
  }

  /** Bishop attacks from {@code squareOrdinal} given {@code occupied}. */
  public static long bishop(int squareOrdinal, long occupied) {
    return attacks(squareOrdinal, occupied, BISHOP_STEPS);
  }

  /** Rook attacks from {@code squareOrdinal} given {@code occupied}. */
  public static long rook(int squareOrdinal, long occupied) {
    return attacks(squareOrdinal, occupied, ROOK_STEPS);
  }

  private static long attacks(int squareOrdinal, long occupied, int[][] steps) {
    if (squareOrdinal < 0 || squareOrdinal >= 64) {
      throw new IllegalArgumentException("squareOrdinal out of range: " + squareOrdinal);
    }
    final int fromFile = squareOrdinal % 8;
    final int fromRank = squareOrdinal / 8;
    long attacks = 0L;
    for (final int[] step : steps) {
      attacks |= rayAttacks(fromFile, fromRank, step[0], step[1], occupied);
    }
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
