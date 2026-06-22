// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.bitboard.internal;

import java.util.Set;
import java.util.TreeSet;

import io.github.dlbbld.ashlarchess.bitboard.BitboardPosition;
import io.github.dlbbld.ashlarchess.board.enums.Piece;
import io.github.dlbbld.ashlarchess.board.enums.Square;
import io.github.dlbbld.ashlarchess.common.Nulls;
import io.github.dlbbld.ashlarchess.fen.internal.FenPieceSymbol;

/**
 * Production-side bitboard utility methods, with no dependency on the {@code StaticPosition} reference layer (which is
 * test-side). The {@code StaticPosition} / {@code BitboardPosition} bridge methods live test-side in
 * {@code io.github.dlbbld.ashlarchess.bitboard.StaticPositionBridge}.
 */
public final class BitboardPositionUtility {

  private BitboardPositionUtility() {
  }

  /**
   * Returns the FEN piece-placement string for {@code bitboardPosition} - the first space-separated field of a FEN
   * string. Format: rank 8 first, ranks separated by "/", consecutive empty squares within a rank collapsed to a digit,
   * pieces as {@link FenPieceSymbol} letters (uppercase = white, lowercase = black).
   */
  public static String calculatePiecePlacement(BitboardPosition bitboardPosition) {
    final StringBuilder piecePlacement = new StringBuilder();
    for (int rankNumber = 8; rankNumber >= 1; rankNumber--) {
      int consecutiveEmptySquares = 0;
      for (int fileNumber = 1; fileNumber <= 8; fileNumber++) {
        final Square square = Square.of(fileNumber, rankNumber);
        final Piece pieceOnSquare = bitboardPosition.get(square);
        final boolean isEmptySquare = pieceOnSquare == Piece.NONE;
        if (isEmptySquare) {
          consecutiveEmptySquares++;
          if (fileNumber == 8) {
            piecePlacement.append(consecutiveEmptySquares);
          }
        } else {
          if (consecutiveEmptySquares > 0) {
            piecePlacement.append(consecutiveEmptySquares);
            consecutiveEmptySquares = 0;
          }
          piecePlacement.append(FenPieceSymbol.of(pieceOnSquare).pieceLetter());
        }
      }
      if (rankNumber != 1) {
        piecePlacement.append("/");
      }
    }
    return Nulls.toString(piecePlacement);
  }

  /**
   * Decode a bitboard to the set of {@link Square}s whose bits are set. Used by the differential-test harness to
   * compare a {@code long}-shaped attack/move set against a {@code Set<Square>}-shaped reference. The returned set
   * iterates squares in ordinal order.
   */
  public static Set<Square> toSquares(long bitboard) {
    final Set<Square> squares = new TreeSet<>();
    long remaining = bitboard;
    while (remaining != 0L) {
      squares.add(Nulls.get(Square.REAL, Long.numberOfTrailingZeros(remaining)));
      remaining &= remaining - 1L;
    }
    return Nulls.copyOfSet(squares);
  }
}
