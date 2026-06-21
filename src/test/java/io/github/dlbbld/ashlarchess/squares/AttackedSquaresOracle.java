// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.squares;

import java.util.Set;
import java.util.TreeSet;

import io.github.dlbbld.ashlarchess.board.StaticPosition;
import io.github.dlbbld.ashlarchess.board.enums.Piece;
import io.github.dlbbld.ashlarchess.board.enums.Side;
import io.github.dlbbld.ashlarchess.board.enums.Square;

/**
 * Potentially to defined as all squares the piece can move to, being empty or occupied by an opponent piece, in the
 * case of rook, bishop or queen additionally additionally requiring that the square is "visible", that is the squares
 * between the from and to squares are all empty and in the case of pawns excluding the diagonal moves (for the reason,
 * that these moves requires additional conditions, contrary to the other pieces).
 *
 *
 */
public final class AttackedSquaresOracle {

  private AttackedSquaresOracle() {
  }

  public static Set<Square> calculateAttackedSquares(StaticPosition staticPosition, Side sideToMove) {

    final Set<Square> squareSet = new TreeSet<>();

    for (final Square fromSquare : Square.REAL) {
      if (staticPosition.isOwnPiece(fromSquare, sideToMove)) {
        final Piece piece = staticPosition.get(fromSquare);
        switch (piece.getPieceType()) {
          case PAWN -> squareSet
              .addAll(PawnAttackedSquares.calculatePawnAttackedSquares(staticPosition, fromSquare, sideToMove));
          case ROOK -> squareSet
              .addAll(RookAttackedSquares.calculateRookAttackedSquares(staticPosition, fromSquare, sideToMove));
          case KNIGHT -> squareSet
              .addAll(KnightAttackedSquares.calculateKnightAttackedSquares(staticPosition, fromSquare, sideToMove));
          case BISHOP -> squareSet
              .addAll(BishopAttackedSquares.calculateBishopAttackedSquares(staticPosition, fromSquare, sideToMove));
          case QUEEN -> squareSet
              .addAll(QueenAttackedSquares.calculateQueenAttackedSquares(staticPosition, fromSquare, sideToMove));
          case KING -> squareSet.addAll(KingNonCastlingAttackedSquares
              .calculateKingNonCastlingAttackedSquares(staticPosition, fromSquare, sideToMove));
          case NONE -> throw new IllegalArgumentException();
          default -> throw new IllegalArgumentException();
        }
      }
    }

    return squareSet;
  }

}
