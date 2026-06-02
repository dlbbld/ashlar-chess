// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.unwinnability;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import io.github.dlbbld.ashlarchess.board.enums.Square;
import io.github.dlbbld.ashlarchess.common.constants.EnumConstants;

/**
 * Cross-validates the BFS-based {@link KnightDistance} against an independent reference port of the closed-form
 * rank/file-distance algorithm from Miguel Ambrona's <em>D3-Chess</em> ({@code src/util.cpp},
 * {@code KnightDistance::knight_distance}). The two implementations use entirely different approaches - graph search
 * vs. table lookup with corner exception - so agreement on every one of the 64x64 = 4096 ordered square pairs is strong
 * evidence that both are correct.
 *
 * <p>
 * The reference algorithm: for two squares, take the minimum and maximum of the file-distance and rank-distance. Three
 * small lookup tables map the (min, max) pair to the knight distance, partitioned by parity. One exceptional case
 * (corner adjacent-diagonal, e.g. {@code a8 <-> b7}) cannot be derived from the tables and returns 4 explicitly.
 *
 * <p>
 * Source: <a href="https://github.com/miguel-ambrona/D3-Chess">D3-Chess</a> (GPL v3).
 */
class TestKnightDistanceAgainstAmbronaReference implements EnumConstants {

  @SuppressWarnings("static-method")
  @Test
  void allOrderedPairsAgreeWithAmbronaReference() {
    for (final Square fromSquare : Square.REAL) {
      for (final Square toSquare : Square.REAL) {
        final int actual = KnightDistance.distance(fromSquare, toSquare);
        final int expected = ambronaKnightDistance(fromSquare, toSquare);
        assertEquals(expected, actual, () -> "knight distance mismatch for " + fromSquare + " -> " + toSquare + " (BFS="
            + actual + ", Ambrona=" + expected + ")");
      }
    }
  }

  /**
   * Java port of {@code KnightDistance::knight_distance} from D3-Chess {@code util.cpp}. Computes the knight distance
   * between two squares using a closed-form lookup over (min, max) of file-distance and rank-distance, with one
   * corner-exception case.
   */
  private static int ambronaKnightDistance(Square x, Square y) {
    final int fileDist = Math.abs(x.getFile().getNumber() - y.getFile().getNumber());
    final int rankDist = Math.abs(x.getRank().getNumber() - y.getRank().getNumber());
    final int idxFirst = Math.min(fileDist, rankDist);
    final int idxSecond = Math.max(fileDist, rankDist);

    // Corner exception: a knight needs 4 moves to reach the diagonally adjacent square of a corner (a8 <-> b7
    // etc.).
    // The table lookup would say 2; override.
    if (idxFirst == 1 && idxSecond == 1 && (isCorner(x) || isCorner(y))) {
      return 4;
    }

    // Same parity (both even or both odd) - tables 1 and 2 in util.cpp.
    if (idxFirst % 2 == idxSecond % 2) {
      if (idxFirst == 0 && idxSecond == 0) {
        return 0;
      }
      if ((idxFirst == 0 && (idxSecond == 2 || idxSecond == 4))) {
        return 2;
      }
      if ((idxFirst == 2 && idxSecond == 4) || (idxFirst == 1 && idxSecond == 1)) {
        return 2;
      }
      if (idxFirst == 1 && idxSecond == 3) {
        return 2;
      }
      if (idxFirst == 3 && idxSecond == 3) {
        return 2;
      }
      if (idxFirst == 7 && idxSecond == 7) {
        return 6;
      }
      return 4;
    }

    // Different parity - table 3 in util.cpp.
    if (idxSecond == 7) {
      return 5;
    }
    if (idxFirst == 1 && idxSecond == 2) {
      return 1;
    }
    if (idxFirst == 5 && idxSecond == 6) {
      return 5;
    }
    return 3;
  }

  private static boolean isCorner(Square s) {
    return s == A1 || s == A8 || s == H1 || s == H8;
  }
}
