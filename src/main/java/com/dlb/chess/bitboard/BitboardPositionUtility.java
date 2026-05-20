package com.dlb.chess.bitboard;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import com.dlb.chess.board.StaticPosition;
import com.dlb.chess.board.enums.Piece;
import com.dlb.chess.board.enums.Square;
import com.dlb.chess.board.model.UpdateSquare;
import com.dlb.chess.common.Nulls;
import com.dlb.chess.fen.FenPieceSymbol;

/**
 * Bit-exact conversion between {@link BitboardPosition} and the existing {@link StaticPosition} mailbox representation.
 * Round-tripping a {@code StaticPosition} through {@code fromStaticPosition} followed by {@code toStaticPosition}
 * reproduces the original; round-tripping a {@code BitboardPosition} likewise.
 *
 * <p>
 * Used by the differential-test harness as the bridge between the bitboard and reference layers; production callers
 * stay on {@code StaticPosition} during this release (see the package-level Javadoc).
 */
public final class BitboardPositionUtility {

  private BitboardPositionUtility() {
  }

  public static BitboardPosition fromStaticPosition(StaticPosition staticPosition) {
    var whitePawns = 0L;
    var whiteRooks = 0L;
    var whiteKnights = 0L;
    var whiteBishops = 0L;
    var whiteQueens = 0L;
    var whiteKings = 0L;
    var blackPawns = 0L;
    var blackRooks = 0L;
    var blackKnights = 0L;
    var blackBishops = 0L;
    var blackQueens = 0L;
    var blackKings = 0L;

    for (final Square square : Square.REAL) {
      final Piece piece = staticPosition.get(square);
      if (piece == Piece.NONE) {
        continue;
      }
      final var bit = 1L << square.ordinal();
      switch (piece) {
        case WHITE_PAWN -> whitePawns |= bit;
        case WHITE_ROOK -> whiteRooks |= bit;
        case WHITE_KNIGHT -> whiteKnights |= bit;
        case WHITE_BISHOP -> whiteBishops |= bit;
        case WHITE_QUEEN -> whiteQueens |= bit;
        case WHITE_KING -> whiteKings |= bit;
        case BLACK_PAWN -> blackPawns |= bit;
        case BLACK_ROOK -> blackRooks |= bit;
        case BLACK_KNIGHT -> blackKnights |= bit;
        case BLACK_BISHOP -> blackBishops |= bit;
        case BLACK_QUEEN -> blackQueens |= bit;
        case BLACK_KING -> blackKings |= bit;
        case NONE -> throw new IllegalStateException();
        default -> throw new IllegalArgumentException();
      }
    }

    return new BitboardPosition(whitePawns, whiteRooks, whiteKnights, whiteBishops, whiteQueens, whiteKings, blackPawns,
        blackRooks, blackKnights, blackBishops, blackQueens, blackKings);
  }

  public static StaticPosition toStaticPosition(BitboardPosition bitboardPosition) {
    final List<UpdateSquare> updates = new ArrayList<>();
    collectOccupiedSquares(updates, bitboardPosition.whitePawns(), Piece.WHITE_PAWN);
    collectOccupiedSquares(updates, bitboardPosition.whiteRooks(), Piece.WHITE_ROOK);
    collectOccupiedSquares(updates, bitboardPosition.whiteKnights(), Piece.WHITE_KNIGHT);
    collectOccupiedSquares(updates, bitboardPosition.whiteBishops(), Piece.WHITE_BISHOP);
    collectOccupiedSquares(updates, bitboardPosition.whiteQueens(), Piece.WHITE_QUEEN);
    collectOccupiedSquares(updates, bitboardPosition.whiteKings(), Piece.WHITE_KING);
    collectOccupiedSquares(updates, bitboardPosition.blackPawns(), Piece.BLACK_PAWN);
    collectOccupiedSquares(updates, bitboardPosition.blackRooks(), Piece.BLACK_ROOK);
    collectOccupiedSquares(updates, bitboardPosition.blackKnights(), Piece.BLACK_KNIGHT);
    collectOccupiedSquares(updates, bitboardPosition.blackBishops(), Piece.BLACK_BISHOP);
    collectOccupiedSquares(updates, bitboardPosition.blackQueens(), Piece.BLACK_QUEEN);
    collectOccupiedSquares(updates, bitboardPosition.blackKings(), Piece.BLACK_KING);

    if (updates.isEmpty()) {
      return StaticPosition.EMPTY_POSITION;
    }
    return StaticPosition.EMPTY_POSITION.createChangedPosition(updates);
  }

  private static void collectOccupiedSquares(List<UpdateSquare> updates, long bitboard, Piece piece) {
    var remaining = bitboard;
    while (remaining != 0L) {
      final var squareOrdinal = Long.numberOfTrailingZeros(remaining);
      updates.add(new UpdateSquare(Nulls.get(Square.REAL, squareOrdinal), piece));
      remaining &= remaining - 1L;
    }
  }

  /**
   * Returns the FEN piece-placement string for {@code bitboardPosition} — the first space-separated field of a FEN
   * string. Format: rank 8 first, ranks separated by "/", consecutive empty squares within a rank collapsed to a
   * digit, pieces as {@link FenPieceSymbol} letters (uppercase = white, lowercase = black).
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
