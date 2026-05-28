package com.dlb.chess.bitboard;

import java.util.Set;
import java.util.TreeSet;

import com.dlb.chess.board.enums.Piece;
import com.dlb.chess.board.enums.Square;
import com.dlb.chess.common.Nulls;
import com.dlb.chess.fen.FenPieceSymbol;

/**
 * Production-side bitboard utility methods that have no dependency on the {@code StaticPosition} reference layer. The
 * {@code StaticPosition} <-> {@code BitboardPosition} bridge methods live in
 * {@code com.dlb.chess.bitboard.StaticPositionBridge} (under {@code src/test/}), since {@code StaticPosition} itself
 * relocated to {@code src/test/} as the permanent differential-test oracle.
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
    for (var rankNumber = 8; rankNumber >= 1; rankNumber--) {
      var consecutiveEmptySquares = 0;
      for (var fileNumber = 1; fileNumber <= 8; fileNumber++) {
        final Square square = Square.calculate(fileNumber, rankNumber);
        final Piece pieceOnSquare = bitboardPosition.get(square);
        final var isEmptySquare = pieceOnSquare == Piece.NONE;
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
          piecePlacement.append(FenPieceSymbol.calculate(pieceOnSquare).pieceLetter());
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
  public static Set<Square> toSquareSet(long bitboard) {
    if (bitboard == 0L) {
      return Nulls.emptySet();
    }
    final Set<Square> squares = new TreeSet<>();
    var remaining = bitboard;
    while (remaining != 0L) {
      squares.add(Nulls.get(Square.REAL, Long.numberOfTrailingZeros(remaining)));
      remaining &= remaining - 1L;
    }
    return squares;
  }
}
