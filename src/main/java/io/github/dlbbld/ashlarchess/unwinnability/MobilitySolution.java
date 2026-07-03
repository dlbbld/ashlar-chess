// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.unwinnability;

/**
 * Output of {@link Mobility}: for each piece of a {@link SemiStaticPosition}, the set of squares it can eventually
 * reach ({@code region(P) = { s | M[P][s] = 1 }} in {@code fun22-spec.md}), as a 64-bit bitboard. Indexed by the
 * piece's position index.
 */
final class MobilitySolution {

  private final SemiStaticPosition position;
  private final long[] regions;

  MobilitySolution(SemiStaticPosition position, long[] regions) {
    this.position = position;
    this.regions = regions;
  }

  SemiStaticPosition position() {
    return position;
  }

  /** region(P) for the piece at index {@code pieceIndex}, as a bitboard. */
  long region(int pieceIndex) {
    return regions[pieceIndex];
  }
}
