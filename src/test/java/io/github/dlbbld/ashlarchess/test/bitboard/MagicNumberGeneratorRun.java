// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.test.bitboard;

import io.github.dlbbld.ashlarchess.bitboard.internal.MagicSliderAttacks;
import io.github.dlbbld.ashlarchess.bitboard.internal.SliderRayAttacks;

/**
 * Searches the magic constants that {@link MagicSliderAttacks} carries, and prints them as pasteable Java.
 *
 * <p>
 * Run this when a relevant-occupancy mask changes. The constants are only valid for the masks they were found against,
 * and {@link MagicSliderAttacks} rejects a mismatched set at initialization rather than building a wrong table - so a
 * mask edit turns into a hard startup failure, and this is how a matching set is produced.
 *
 * <p>
 * Each square restarts the generator from its own seed. That is not cosmetic: a single stream shared across all 64
 * squares leaves some squares stuck in an unlucky stretch, and a plain sequential search then fails to find minimal
 * tables for about ten rook squares even after a million candidates. Per-square seeding finds all 128 in well under a
 * second. The output is therefore deterministic, and re-running this reproduces the committed constants exactly.
 *
 * <p>
 * The masks are duplicated here rather than exposed from the production class, so a wrong mask there cannot be
 * confirmed by a test tool that shares it.
 */
public final class MagicNumberGeneratorRun {

  private static final int SQUARES = 64;
  private static final int MAX_LINE_LENGTH = 118;

  private static long state;

  private MagicNumberGeneratorRun() {
  }

  public static void main(String[] args) {
    final long startNanos = System.nanoTime();
    emit("BISHOP_MAGIC", true);
    emit("ROOK_MAGIC", false);
    System.err.printf("searched in %.2f s%n", (System.nanoTime() - startNanos) / 1e9);
  }

  private static void emit(String fieldName, boolean isBishop) {
    System.out.println("  private static final long[] " + fieldName + " = {");
    StringBuilder line = new StringBuilder("     ");
    for (int square = 0; square < SQUARES; square++) {
      final String literal = String.format(" 0x%016XL,", findMagic(square, isBishop));
      if (line.length() + literal.length() > MAX_LINE_LENGTH) {
        System.out.println(line);
        line = new StringBuilder("     ");
      }
      line.append(literal);
    }
    if (!line.toString().isBlank()) {
      System.out.println(line);
    }
    System.out.println("  };");
  }

  private static long findMagic(int square, boolean isBishop) {
    final long mask = relevantMask(square, isBishop);
    final int shift = 64 - Long.bitCount(mask);
    final int size = 1 << Long.bitCount(mask);

    final long[] occupancies = new long[size];
    final long[] references = new long[size];
    int count = 0;
    long subset = 0L;
    do {
      occupancies[count] = subset;
      references[count] = isBishop ? SliderRayAttacks.bishop(square, subset) : SliderRayAttacks.rook(square, subset);
      count++;
      subset = (subset - mask) & mask;
    } while (subset != 0L);

    final long[] candidateTable = new long[size];
    final int[] writtenInAttempt = new int[size];
    state = 0x9E3779B97F4A7C15L ^ square * 0x100000001B3L ^ (isBishop ? 0x5DEECE66DL : 0L);
    if (state == 0L) {
      state = 1L;
    }

    for (int attempt = 1; attempt > 0; attempt++) {
      final long magic = nextSparse();
      // Cheap pre-filter: a magic that does not scatter the mask into the top byte of the product is very unlikely to
      // separate the subsets, and rejecting it costs one multiply instead of a full table pass.
      if (Long.bitCount(mask * magic >>> 56) < 6) {
        continue;
      }
      boolean collisionFree = true;
      for (int i = 0; i < size; i++) {
        final int index = (int) (occupancies[i] * magic >>> shift);
        if (writtenInAttempt[index] != attempt) {
          writtenInAttempt[index] = attempt;
          candidateTable[index] = references[i];
        } else if (candidateTable[index] != references[i]) {
          collisionFree = false;
          break;
        }
      }
      if (collisionFree) {
        return magic;
      }
    }
    throw new IllegalStateException("No magic found for square " + square + " (isBishop=" + isBishop + ")");
  }

  private static long relevantMask(int square, boolean isBishop) {
    final int file = square & 7;
    final int rank = square >> 3;
    long mask = 0L;
    if (isBishop) {
      for (int fileStep = -1; fileStep <= 1; fileStep += 2) {
        for (int rankStep = -1; rankStep <= 1; rankStep += 2) {
          int currentFile = file + fileStep;
          int currentRank = rank + rankStep;
          while (currentFile >= 1 && currentFile <= 6 && currentRank >= 1 && currentRank <= 6) {
            mask |= 1L << ((currentRank << 3) | currentFile);
            currentFile += fileStep;
            currentRank += rankStep;
          }
        }
      }
      return mask;
    }
    for (int currentRank = rank + 1; currentRank <= 6; currentRank++) {
      mask |= 1L << ((currentRank << 3) | file);
    }
    for (int currentRank = rank - 1; currentRank >= 1; currentRank--) {
      mask |= 1L << ((currentRank << 3) | file);
    }
    for (int currentFile = file + 1; currentFile <= 6; currentFile++) {
      mask |= 1L << ((rank << 3) | currentFile);
    }
    for (int currentFile = file - 1; currentFile >= 1; currentFile--) {
      mask |= 1L << ((rank << 3) | currentFile);
    }
    return mask;
  }

  // xorshift64*. Candidates are sparse - three draws ANDed together - because a magic needs few set bits to spread a
  // small mask across the index without the partial products overlapping.
  private static long nextSparse() {
    return next() & next() & next();
  }

  private static long next() {
    long value = state;
    value ^= value >>> 12;
    value ^= value << 25;
    value ^= value >>> 27;
    state = value;
    return value * 0x2545F4914F6CDD1DL;
  }
}
