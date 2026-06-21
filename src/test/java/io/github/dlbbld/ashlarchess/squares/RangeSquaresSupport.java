// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.squares;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import com.google.common.collect.ImmutableList;

import io.github.dlbbld.ashlarchess.board.StaticPosition;
import io.github.dlbbld.ashlarchess.board.enums.Piece;
import io.github.dlbbld.ashlarchess.board.enums.PieceType;
import io.github.dlbbld.ashlarchess.board.enums.Side;
import io.github.dlbbld.ashlarchess.board.enums.Square;
import io.github.dlbbld.ashlarchess.enums.SquareOccupation;

/**
 * Calculated to defined as all squares the piece can move to, being empty, occupied by an opponent piece or by
 * parameterized allowing or disallowing an own piece, in the case of rook, bishop or queen additionally additionally
 * requiring that the square is "visible", that is the squares between the from and to squares are all empty and in the
 * case of pawns excluding the diagonal moves (for the reason, that these moves requires additional conditions, contrary
 * to the other pieces).
 */
final class RangeSquaresSupport {

  private RangeSquaresSupport() {
  }

  static Set<Square> calculateOrthogonalRangeSquare(StaticPosition staticPosition, Side sideToMove, Square fromSquare,
      PieceType expectedSourcePieceType, OrthogonalRange orthogonalMoves, boolean isAllowOwnPiece) {

    final Set<Square> calculatedToSquareSet = new TreeSet<>(calculateRangeSquare(staticPosition, sideToMove, fromSquare,
        expectedSourcePieceType, orthogonalMoves.northSquares(), isAllowOwnPiece));

    calculatedToSquareSet.addAll(calculateRangeSquare(staticPosition, sideToMove, fromSquare, expectedSourcePieceType,
        orthogonalMoves.eastSquares(), isAllowOwnPiece));
    calculatedToSquareSet.addAll(calculateRangeSquare(staticPosition, sideToMove, fromSquare, expectedSourcePieceType,
        orthogonalMoves.southSquares(), isAllowOwnPiece));
    calculatedToSquareSet.addAll(calculateRangeSquare(staticPosition, sideToMove, fromSquare, expectedSourcePieceType,
        orthogonalMoves.westSquares(), isAllowOwnPiece));
    return calculatedToSquareSet;
  }

  static Set<Square> calculateDiagonalRangeSquare(StaticPosition staticPosition, Side sideToMove, Square fromSquare,
      PieceType expectedSourcePieceType, DiagonalRange diagonalMoves, boolean isAllowOwnPiece) {

    final Set<Square> calculatedToSquareSet = new TreeSet<>(calculateRangeSquare(staticPosition, sideToMove, fromSquare,
        expectedSourcePieceType, diagonalMoves.northEastSquares(), isAllowOwnPiece));

    calculatedToSquareSet.addAll(calculateRangeSquare(staticPosition, sideToMove, fromSquare, expectedSourcePieceType,
        diagonalMoves.southEastSquares(), isAllowOwnPiece));
    calculatedToSquareSet.addAll(calculateRangeSquare(staticPosition, sideToMove, fromSquare, expectedSourcePieceType,
        diagonalMoves.southWestSquares(), isAllowOwnPiece));
    calculatedToSquareSet.addAll(calculateRangeSquare(staticPosition, sideToMove, fromSquare, expectedSourcePieceType,
        diagonalMoves.northWestSquares(), isAllowOwnPiece));
    return calculatedToSquareSet;
  }

  private static Set<Square> calculateRangeSquare(StaticPosition staticPosition, Side sideToMove, Square fromSquare,
      PieceType expectedSourcePieceType, ImmutableList<Square> emptyBoardSquares, boolean isAllowOwnPiece) {

    ToSquaresSupport.checkPiece(staticPosition, sideToMove, fromSquare, expectedSourcePieceType);

    final Set<Square> calculatedToSquareSet = new TreeSet<>();

    final List<Square> calculatedToSquares = calculateRangeSquares(staticPosition, sideToMove, emptyBoardSquares,
        isAllowOwnPiece);
    calculatedToSquareSet.addAll(calculatedToSquares);
    return calculatedToSquareSet;
  }

  static Set<Square> calculateRangeSquare(StaticPosition staticPosition, Side sideToMove, Square fromSquare,
      boolean isAllowOwnPiece) {

    final Piece piece = staticPosition.get(fromSquare);

    if (piece == Piece.NONE) {
      throw new IllegalArgumentException();
    }
    return switch (piece.getPieceType()) {
      case ROOK -> RookRangeSquares.calculateRookRangeSquares(staticPosition, fromSquare, sideToMove, isAllowOwnPiece);
      case BISHOP -> BishopRangeSquares.calculateBishopRangeSquares(staticPosition, fromSquare, sideToMove,
          isAllowOwnPiece);
      case QUEEN -> QueenRangeSquares.calculateQueenRangeSquares(staticPosition, fromSquare, sideToMove,
          isAllowOwnPiece);
      case PAWN, KNIGHT, KING, NONE -> throw new IllegalArgumentException();
      default -> throw new IllegalArgumentException();
    };

  }

  private static List<Square> calculateRangeSquares(StaticPosition staticPosition, Side sideToMove,
      List<Square> emptyBoardSquares, boolean isAllowOwnPiece) {

    final List<Square> calculatedToSquares = new ArrayList<>();

    for (final Square toSquare : emptyBoardSquares) {
      final SquareOccupation squareOccupation = ToSquaresSupport.calculateSquareOccupation(staticPosition, sideToMove,
          toSquare);
      switch (squareOccupation) {
        case NONE:
          calculatedToSquares.add(toSquare);
          continue;
        case OPPONENT_PIECE:
          calculatedToSquares.add(toSquare);
          return calculatedToSquares;
        case OWN_PIECE:
          if (isAllowOwnPiece) {
            calculatedToSquares.add(toSquare);
          }
          return calculatedToSquares;
        default:
          throw new IllegalArgumentException();
      }
    }
    return calculatedToSquares;
  }

}
