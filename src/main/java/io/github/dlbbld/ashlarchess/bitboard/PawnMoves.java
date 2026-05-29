// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.bitboard;

import io.github.dlbbld.ashlarchess.board.enums.Side;
import io.github.dlbbld.ashlarchess.board.enums.Square;
import io.github.dlbbld.ashlarchess.common.Nulls;

/**
 * Pseudo-legal pawn target squares.
 *
 * <p>
 * {@link #pushes} returns single + double forward pushes (no captures). Single push lands one rank forward iff that
 * square is empty; double push lands two ranks forward iff the pawn is on its starting rank and both intermediate and
 * target squares are empty. Promotion targets are included in the push bitboard - the caller distinguishes promotion at
 * the move-generation layer.
 *
 * <p>
 * {@link #captures} returns regular diagonal captures plus en-passant. Regular captures are the diagonal-forward
 * squares occupied by opponent pieces; en-passant capture is recognised when the {@code enPassantBit} (single-bit
 * bitboard of the en-passant target square, or {@code 0L} for "no en-passant available for this side") matches the
 * pawn's diagonal-forward attack pattern. The bitboard layer is stateless about whose turn it is - the caller passes
 * {@code 0L} for {@code enPassantBit} when the EP opportunity does not apply to {@code side}.
 *
 * <p>
 * Side parameter is required for both methods: a "pawn" with no side has no defined forward direction.
 */
public final class PawnMoves {

  private PawnMoves() {
  }

  public static long pushes(int squareOrdinal, long occupied, Side side) {
    if (squareOrdinal < 0 || squareOrdinal >= 64) {
      throw new IllegalArgumentException("squareOrdinal out of range: " + squareOrdinal);
    }
    if (side != Side.WHITE && side != Side.BLACK) {
      throw new IllegalArgumentException("pushes requires Side.WHITE or Side.BLACK, got " + side);
    }

    final int forwardOrdinal = side == Side.WHITE ? squareOrdinal + 8 : squareOrdinal - 8;
    if (forwardOrdinal < 0 || forwardOrdinal >= 64) {
      return 0L;
    }
    final long forwardBit = 1L << forwardOrdinal;
    if ((forwardBit & occupied) != 0L) {
      return 0L;
    }

    long result = forwardBit;

    final int fromRank = squareOrdinal / 8;
    final boolean onStartingRank = side == Side.WHITE ? fromRank == 1 : fromRank == 6;
    if (onStartingRank) {
      final int doubleOrdinal = side == Side.WHITE ? squareOrdinal + 16 : squareOrdinal - 16;
      final long doubleBit = 1L << doubleOrdinal;
      if ((doubleBit & occupied) == 0L) {
        result |= doubleBit;
      }
    }

    return result;
  }

  public static long captures(int squareOrdinal, long opponentPieces, long enPassantBit, Side side) {
    if (squareOrdinal < 0 || squareOrdinal >= 64) {
      throw new IllegalArgumentException("squareOrdinal out of range: " + squareOrdinal);
    }
    if (side != Side.WHITE && side != Side.BLACK) {
      throw new IllegalArgumentException("captures requires Side.WHITE or Side.BLACK, got " + side);
    }
    final long diagonalAttacks = PawnAttacks.attacks(Nulls.get(Square.REAL, squareOrdinal), side);
    return (diagonalAttacks & opponentPieces) | (diagonalAttacks & enPassantBit);
  }
}
