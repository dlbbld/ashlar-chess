// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.bitboard.internal;

/**
 * Bishop attacks. {@link #attacks(int, long)} returns the bitboard of squares a bishop on {@code squareOrdinal} attacks
 * given an {@code occupied}-mask of all pieces on the board. The result includes every empty square along each of the
 * four diagonals plus the first blocker in that direction, whatever its colour - so own pieces read as defended.
 *
 * <p>
 * Served by {@link MagicSliderAttacks}. {@link SliderRayAttacks} holds the equivalent ray walk, which builds those
 * tables and remains the oracle the differential tests check against.
 */
public final class BishopAttacks {

  private BishopAttacks() {
  }

  public static long attacks(int squareOrdinal, long occupied) {
    if (squareOrdinal < 0 || squareOrdinal >= 64) {
      throw new IllegalArgumentException("squareOrdinal out of range: " + squareOrdinal);
    }
    return MagicSliderAttacks.bishop(squareOrdinal, occupied);
  }
}
