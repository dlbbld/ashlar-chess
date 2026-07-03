// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.unwinnability;

import java.util.Arrays;

import io.github.dlbbld.ashlarchess.board.enums.Side;

/**
 * Fixed square geometry in the FUN 2022 paper's notation (see {@code fun22-spec.md} section 1). Every set is
 * "over an empty board" and is expressed as a 64-bit bitboard mask with {@code a1 = bit 0, ..., h8 = bit 63}
 * (little-endian rank-file, so square indices coincide with {@code Square.ordinal()}).
 *
 * <p>
 * Naming follows the paper: {@code alpha} = &alpha; (orthogonal neighbours), {@code beta} = &beta; (diagonal
 * neighbours), {@code delta} = &delta; = &alpha; &cup; &beta; (the king's escape squares), {@code knight} = &nu;,
 * {@code pawnPushPredecessors} = &omega;, and {@code pawnAttackPredecessors} = &pi;. All tables are precomputed once.
 */
final class SquareGeometry {

  private SquareGeometry() {
  }

  /** Number of squares on the board. */
  static final int SQUARES = 64;

  private static final long[] ALPHA = new long[SQUARES];
  private static final long[] BETA = new long[SQUARES];
  private static final long[] DELTA = new long[SQUARES];
  private static final long[] KNIGHT = new long[SQUARES];

  // Indexed [side ordinal][square]; Side.WHITE.ordinal() = 0, Side.BLACK.ordinal() = 1.
  private static final long[][] PAWN_PUSH_PRED = new long[2][SQUARES];
  private static final long[][] PAWN_ATTACK_PRED = new long[2][SQUARES];

  /** White promotion rank (chess rank 8). */
  static final long PROMOTION_WHITE = 0xFF00_0000_0000_0000L;
  /** Black promotion rank (chess rank 1). */
  static final long PROMOTION_BLACK = 0x0000_0000_0000_00FFL;

  /** Dark squares (a1's colour: file + rank even) and their complement. */
  static final long DARK_SQUARES;
  static final long LIGHT_SQUARES;

  // knightDistance[a][b] = minimum knight moves from a to b over an empty board.
  private static final int[][] KNIGHT_DISTANCE = new int[SQUARES][SQUARES];

  private static final int[][] ALPHA_STEPS = { { 1, 0 }, { -1, 0 }, { 0, 1 }, { 0, -1 } };
  private static final int[][] BETA_STEPS = { { 1, 1 }, { 1, -1 }, { -1, 1 }, { -1, -1 } };
  private static final int[][] KNIGHT_STEPS = { { 1, 2 }, { 2, 1 }, { 2, -1 }, { 1, -2 }, { -1, -2 }, { -2, -1 },
      { -2, 1 }, { -1, 2 } };

  static {
    for (int s = 0; s < SQUARES; s++) {
      final int file = s & 7;
      final int rank = s >> 3;

      ALPHA[s] = mask(file, rank, ALPHA_STEPS);
      BETA[s] = mask(file, rank, BETA_STEPS);
      DELTA[s] = ALPHA[s] | BETA[s];
      KNIGHT[s] = mask(file, rank, KNIGHT_STEPS);

      // omega: the single square a pawn pushes FROM to reach s (white from below, black from above). Empty when off
      // the board.
      PAWN_PUSH_PRED[0][s] = bit(file, rank - 1);
      PAWN_PUSH_PRED[1][s] = bit(file, rank + 1);

      // pi: squares from which a pawn of that colour attacks s (white from the rank below, black from the rank above;
      // both diagonals).
      PAWN_ATTACK_PRED[0][s] = bit(file - 1, rank - 1) | bit(file + 1, rank - 1);
      PAWN_ATTACK_PRED[1][s] = bit(file - 1, rank + 1) | bit(file + 1, rank + 1);
    }
  }

  static {
    long dark = 0L;
    for (int s = 0; s < SQUARES; s++) {
      if ((((s & 7) + (s >> 3)) & 1) == 0) {
        dark |= 1L << s;
      }
    }
    DARK_SQUARES = dark;
    LIGHT_SQUARES = ~dark;

    // Breadth-first knight distances over the empty board (uses KNIGHT, filled above). Every square is enqueued at
    // most once, so a plain int array serves as the queue.
    final int[] queue = new int[SQUARES];
    for (int src = 0; src < SQUARES; src++) {
      final int[] dist = KNIGHT_DISTANCE[src];
      Arrays.fill(dist, -1);
      dist[src] = 0;
      int head = 0;
      int tail = 0;
      queue[tail++] = src;
      while (head < tail) {
        final int u = queue[head++];
        long neighbours = KNIGHT[u];
        while (neighbours != 0L) {
          final int v = Long.numberOfTrailingZeros(neighbours);
          neighbours &= neighbours - 1;
          if (dist[v] < 0) {
            dist[v] = dist[u] + 1;
            queue[tail++] = v;
          }
        }
      }
    }
  }

  /** King-distance (Chebyshev) between two squares over an empty board. */
  static int kingDistance(int a, int b) {
    final int fileDistance = Math.abs((a & 7) - (b & 7));
    final int rankDistance = Math.abs((a >> 3) - (b >> 3));
    return Math.max(fileDistance, rankDistance);
  }

  /** Knight-distance (minimum knight moves) between two squares over an empty board. */
  static int knightDistance(int a, int b) {
    return KNIGHT_DISTANCE[a][b];
  }

  private static long mask(int file, int rank, int[][] steps) {
    long result = 0L;
    for (final int[] step : steps) {
      result |= bit(file + step[0], rank + step[1]);
    }
    return result;
  }

  /** A single-bit mask for (file, rank), or 0 if the square is off the board. */
  private static long bit(int file, int rank) {
    if (file < 0 || file > 7 || rank < 0 || rank > 7) {
      return 0L;
    }
    return 1L << ((rank << 3) | file);
  }

  /** &alpha;(s): orthogonally adjacent squares (share a border; opposite square colour to s). */
  static long alpha(int square) {
    return ALPHA[square];
  }

  /** &beta;(s): diagonally adjacent squares (same square colour as s). */
  static long beta(int square) {
    return BETA[square];
  }

  /** &delta;(s) = &alpha;(s) &cup; &beta;(s): the &le; 8 king-neighbour (escape) squares. */
  static long delta(int square) {
    return DELTA[square];
  }

  /** &nu;(s): squares at knight-distance 1. */
  static long knight(int square) {
    return KNIGHT[square];
  }

  /** &omega;_side(s): the &le; 1 square a pawn of {@code side} pushes from to reach s. */
  static long pawnPushPredecessors(Side side, int square) {
    return PAWN_PUSH_PRED[side.ordinal()][square];
  }

  /** &pi;_side(s): squares from which a pawn of {@code side} attacks s. */
  static long pawnAttackPredecessors(Side side, int square) {
    return PAWN_ATTACK_PRED[side.ordinal()][square];
  }

  /** prom(P): promotion squares for a pawn of {@code side}. */
  static long promotion(Side side) {
    return side == Side.WHITE ? PROMOTION_WHITE : PROMOTION_BLACK;
  }
}
