// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.bitboard;

import java.util.Random;

import io.github.dlbbld.ashlarchess.board.enums.Piece;
import io.github.dlbbld.ashlarchess.board.enums.Square;

/**
 * Pre-computed Zobrist random keys for chess positions. The piece-placement component is one {@code long} per (piece,
 * square) pair: 12 real pieces x 64 squares = 768 keys, indexed by {@link Piece#ordinal()} and
 * {@link Square#ordinal()}. The piece-placement hash of a position is the XOR of all (piece, square) keys for which the
 * piece sits on the square.
 *
 * <p>
 * Side-to-move, castling rights, and en-passant file keys are intentionally NOT defined here - those state pieces live
 * on {@code Board} / {@code DynamicPosition}, so their Zobrist keys (if needed) belong on a Board-side utility, not on
 * the bitboard layer.
 *
 * <p>
 * Keys are derived from a fixed-seed {@link Random} so the table is deterministic and reproducible: same seed -> same
 * keys -> same hashes across JVM runs. This matters for transposition keys persisted across processes (e.g.,
 * helpmate-search caches).
 */
public final class ZobristKeys {

  // Arbitrary fixed seed: stable hashes across runs without relying on any external secret.
  private static final long SEED = 0xC0FFEE_1234_ABCDL;

  private static final long[][] PIECE_SQUARE_KEYS = computePieceSquareKeys();

  private static long[][] computePieceSquareKeys() {
    final long[][] keys = new long[12][64];
    final Random rng = new Random(SEED);
    for (int pieceIdx = 0; pieceIdx < 12; pieceIdx++) {
      for (int squareOrdinal = 0; squareOrdinal < 64; squareOrdinal++) {
        keys[pieceIdx][squareOrdinal] = rng.nextLong();
      }
    }
    return keys;
  }

  private ZobristKeys() {
  }

  public static long pieceSquare(Piece piece, Square square) {
    if (piece == Piece.NONE) {
      throw new IllegalArgumentException("Piece.NONE has no Zobrist key");
    }
    if (square == Square.NONE) {
      throw new IllegalArgumentException("Square.NONE has no Zobrist key");
    }
    return PIECE_SQUARE_KEYS[piece.ordinal()][square.ordinal()];
  }
}
