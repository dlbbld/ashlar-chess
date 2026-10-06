// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.test.bitboard;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import io.github.dlbbld.ashlarchess.bitboard.internal.MagicSliderAttacks;
import io.github.dlbbld.ashlarchess.bitboard.internal.SliderRayAttacks;

/**
 * Differential test for {@link MagicSliderAttacks} against {@link SliderRayAttacks}.
 *
 * <p>
 * The magic tables are built from the ray walk, so these tests are not checking that the two agree on the occupancies
 * used to build the table - that holds by construction. What they check is the indexing: that the magic hash maps
 * every occupancy onto the entry holding the right answer, and that the relevant-occupancy mask really is sufficient.
 *
 * <p>
 * The mask claim is the subtle one and {@link #fullBoardOccupanciesAgree} is the test that earns it. A magic index is
 * computed from {@code occupied & mask}, which discards the last square of every ray, yet the ray walk does look at
 * those squares. Dropping them is only sound because a blocker on the final square of a ray changes nothing: that
 * square is attacked either way and there is nothing beyond it to shadow. So occupancies that set bits outside the
 * mask have to be tried explicitly, not just subsets of it.
 */
class TestMagicSliderAttacks {

  private static final int SQUARES = 64;

  /** Pseudorandom full-board occupancies per square. Fixed seed, so a failure is reproducible. */
  private static final int RANDOM_OCCUPANCIES_PER_SQUARE = 4_096;

  /** Rook tables hold 102,400 entries and bishop tables 5,248 - the standard magic-bitboard footprint. */
  private static final int EXPECTED_TABLE_ENTRIES = 102_400 + 5_248;

  @SuppressWarnings("static-method")
  @Test
  void relevantOccupanciesAgreeExhaustively() {
    for (int square = 0; square < SQUARES; square++) {
      assertSubsetsAgree(square, true);
      assertSubsetsAgree(square, false);
    }
  }

  @SuppressWarnings("static-method")
  @Test
  void fullBoardOccupanciesAgree() {
    long state = 0x123456789ABCDEFL;
    for (int square = 0; square < SQUARES; square++) {
      for (int i = 0; i < RANDOM_OCCUPANCIES_PER_SQUARE; i++) {
        state = nextState(state);
        final long occupied = sparseOccupancy(state);
        assertEquals(SliderRayAttacks.bishop(square, occupied), MagicSliderAttacks.bishop(square, occupied),
            "bishop mismatch on square " + square + " occupancy " + Long.toHexString(occupied));
        assertEquals(SliderRayAttacks.rook(square, occupied), MagicSliderAttacks.rook(square, occupied),
            "rook mismatch on square " + square + " occupancy " + Long.toHexString(occupied));
      }
    }
  }

  @SuppressWarnings("static-method")
  @Test
  void emptyAndFullBoardsAgree() {
    for (int square = 0; square < SQUARES; square++) {
      assertEquals(SliderRayAttacks.bishop(square, 0L), MagicSliderAttacks.bishop(square, 0L),
          "bishop mismatch on empty board, square " + square);
      assertEquals(SliderRayAttacks.rook(square, 0L), MagicSliderAttacks.rook(square, 0L),
          "rook mismatch on empty board, square " + square);
      assertEquals(SliderRayAttacks.bishop(square, -1L), MagicSliderAttacks.bishop(square, -1L),
          "bishop mismatch on full board, square " + square);
      assertEquals(SliderRayAttacks.rook(square, -1L), MagicSliderAttacks.rook(square, -1L),
          "rook mismatch on full board, square " + square);
    }
  }

  /**
   * Pins the table footprint. The relevant-occupancy masks decide how much memory the whole scheme costs, and a mask
   * that accidentally keeps the edge squares still produces correct answers - just with tables several times larger.
   * That failure is invisible to the agreement tests, so it gets its own assertion.
   */
  @SuppressWarnings("static-method")
  @Test
  void tableFootprintIsTheExpectedSize() {
    assertEquals(EXPECTED_TABLE_ENTRIES, MagicSliderAttacks.tableEntryCount());
  }

  // Walks every occupancy subset of the square's relevant mask via the carry-rippler, exactly as the table builder
  // does, and checks the lookup returns the ray walk's answer for each.
  private static void assertSubsetsAgree(int square, boolean isBishop) {
    final long mask = relevantMask(square, isBishop);
    long subset = 0L;
    do {
      final long expected = isBishop ? SliderRayAttacks.bishop(square, subset) : SliderRayAttacks.rook(square, subset);
      final long actual = isBishop ? MagicSliderAttacks.bishop(square, subset) : MagicSliderAttacks.rook(square, subset);
      assertEquals(expected, actual, (isBishop ? "bishop" : "rook") + " mismatch on square " + square + " occupancy "
          + Long.toHexString(subset));
      subset = (subset - mask) & mask;
    } while (subset != 0L);
  }

  // Recomputed here rather than exposed from the production class: an independent derivation of the mask means a wrong
  // mask in MagicSliderAttacks cannot hide by also being wrong in the test.
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

  private static long nextState(long previous) {
    long value = previous;
    value ^= value >>> 12;
    value ^= value << 25;
    value ^= value >>> 27;
    return value * 0x2545F4914F6CDD1DL;
  }

  // Two draws ANDed: a real board is sparse, and a uniform 50%-density occupancy would almost always block every ray
  // at distance one, leaving the deeper table entries untested.
  private static long sparseOccupancy(long state) {
    return state & nextState(state);
  }
}
