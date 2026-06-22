// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.squares;

import java.util.ArrayList;
import java.util.List;

import io.github.dlbbld.ashlarchess.board.enums.Square;

/**
 * Shared helper for the empty-board sliding-piece (rook, bishop, queen) tables: walk from a starting (file, rank)
 * coordinate in a given direction (fileDelta, rankDelta) until off the board, collecting each square visited.
 */
final class RayUtility {

  private RayUtility() {
  }

  @SuppressWarnings("null")
  static List<Square> ray(int fromFile, int fromRank, int fileDelta, int rankDelta) {
    final List<Square> builder = new ArrayList<>();
    int f = fromFile + fileDelta;
    int r = fromRank + rankDelta;
    while (f >= 1 && f <= 8 && r >= 1 && r <= 8) {
      builder.add(Square.of(f, r));
      f += fileDelta;
      r += rankDelta;
    }
    return List.copyOf(builder);
  }

}
