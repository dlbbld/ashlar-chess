package com.dlb.chess.bitboard;

import java.util.ArrayList;
import java.util.List;

import com.dlb.chess.board.StaticPosition;
import com.dlb.chess.board.enums.Piece;
import com.dlb.chess.board.enums.Square;
import com.dlb.chess.board.model.UpdateSquare;
import com.dlb.chess.common.Nulls;

/**
 * Test-side bridge between the bitboard production layer and the {@link StaticPosition} reference oracle. Lives in
 * {@code src/test/} because {@link StaticPosition} lives in {@code src/test/} too (as the permanent
 * differential-test oracle, per the Project Invariant). Production code in {@code src/main/} never references
 * {@link StaticPosition}, so these bridge methods cannot live alongside the other utilities in
 * {@link BitboardPositionUtility} on the production side — they would re-introduce the {@code StaticPosition}
 * import that the relocation removed.
 *
 * <p>
 * Round-tripping a {@code StaticPosition} through {@link #fromStaticPosition} followed by
 * {@link #toStaticPosition} reproduces the original; round-tripping a {@code BitboardPosition} likewise. The
 * differential-test harness depends on these inverses being faithful — see {@code TestBitboardPositionRoundTrip}.
 */
public final class StaticPositionBridge {

  private StaticPositionBridge() {
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
}
