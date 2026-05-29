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
import io.github.dlbbld.ashlarchess.squares.DiagonalRange;
import io.github.dlbbld.ashlarchess.squares.OrthogonalRange;

/**
 * Calculated to defined as all squares the piece can move to, being empty, occupied by an opponent piece or by
 * parameterized allowing or disallowing an own piece, in the case of rook, bishop or queen additionally additionally
 * requiring that the square is "visible", that is the squares between the from and to squares are all empty and in the
 * case of pawns excluding the diagonal moves (for the reason, that these moves requires additional conditions, contrary
 * to the other pieces).
 */
abstract class AbstractRangeSquares extends AbstractToSquares {

  static Set<Square> calculateOrthogonalRangeSquare(StaticPosition staticPosition, Side havingMove, Square fromSquare,
      PieceType expectedSourcePieceType, OrthogonalRange orthogonalMoves, boolean isAllowOwnPiece) {

    final Set<Square> calculatedToSquareSet = new TreeSet<>(calculateRangeSquare(staticPosition, havingMove, fromSquare,
        expectedSourcePieceType, orthogonalMoves.squareListNorth(), isAllowOwnPiece));

    calculatedToSquareSet.addAll(calculateRangeSquare(staticPosition, havingMove, fromSquare, expectedSourcePieceType,
        orthogonalMoves.squareListEast(), isAllowOwnPiece));
    calculatedToSquareSet.addAll(calculateRangeSquare(staticPosition, havingMove, fromSquare, expectedSourcePieceType,
        orthogonalMoves.squareListSouth(), isAllowOwnPiece));
    calculatedToSquareSet.addAll(calculateRangeSquare(staticPosition, havingMove, fromSquare, expectedSourcePieceType,
        orthogonalMoves.squareListWest(), isAllowOwnPiece));
    return calculatedToSquareSet;
  }

  static Set<Square> calculateDiagonalRangeSquare(StaticPosition staticPosition, Side havingMove, Square fromSquare,
      PieceType expectedSourcePieceType, DiagonalRange diagonalMoves, boolean isAllowOwnPiece) {

    final Set<Square> calculatedToSquareSet = new TreeSet<>(calculateRangeSquare(staticPosition, havingMove, fromSquare,
        expectedSourcePieceType, diagonalMoves.squareListNorthEast(), isAllowOwnPiece));

    calculatedToSquareSet.addAll(calculateRangeSquare(staticPosition, havingMove, fromSquare, expectedSourcePieceType,
        diagonalMoves.squareListSouthEast(), isAllowOwnPiece));
    calculatedToSquareSet.addAll(calculateRangeSquare(staticPosition, havingMove, fromSquare, expectedSourcePieceType,
        diagonalMoves.squareListSouthWest(), isAllowOwnPiece));
    calculatedToSquareSet.addAll(calculateRangeSquare(staticPosition, havingMove, fromSquare, expectedSourcePieceType,
        diagonalMoves.squareListNorthWest(), isAllowOwnPiece));
    return calculatedToSquareSet;
  }

  private static Set<Square> calculateRangeSquare(StaticPosition staticPosition, Side havingMove, Square fromSquare,
      PieceType expectedSourcePieceType, ImmutableList<Square> emptyBoardSquareList, boolean isAllowOwnPiece) {

    checkPiece(staticPosition, havingMove, fromSquare, expectedSourcePieceType);

    final Set<Square> calculatedToSquareSet = new TreeSet<>();

    final List<Square> calculatedToSquareList = calculateRangeSquareList(staticPosition, havingMove,
        emptyBoardSquareList, isAllowOwnPiece);
    calculatedToSquareSet.addAll(calculatedToSquareList);
    return calculatedToSquareSet;
  }

  public static Set<Square> calculateRangeSquare(StaticPosition staticPosition, Side havingMove, Square fromSquare,
      boolean isAllowOwnPiece) {

    final Piece piece = staticPosition.get(fromSquare);

    if (piece == Piece.NONE) {
      throw new IllegalArgumentException();
    }
    return switch (piece.getPieceType()) {
      case ROOK -> RookRangeSquares.calculateRookRangeSquares(staticPosition, fromSquare, havingMove, isAllowOwnPiece);
      case BISHOP -> BishopRangeSquares.calculateBishopRangeSquares(staticPosition, fromSquare, havingMove,
          isAllowOwnPiece);
      case QUEEN -> QueenRangeSquares.calculateQueenRangeSquares(staticPosition, fromSquare, havingMove,
          isAllowOwnPiece);
      case PAWN, KNIGHT, KING, NONE -> throw new IllegalArgumentException();
      default -> throw new IllegalArgumentException();
    };

  }

  private static List<Square> calculateRangeSquareList(StaticPosition staticPosition, Side havingMove,
      List<Square> emptyBoardSquareList, boolean isAllowOwnPiece) {

    final List<Square> calculatedToSquareList = new ArrayList<>();

    for (final Square toSquare : emptyBoardSquareList) {
      final SquareOccupation squareOccupation = calculateSquareOccupation(staticPosition, havingMove, toSquare);
      switch (squareOccupation) {
        case NONE:
          calculatedToSquareList.add(toSquare);
          continue;
        case OPPONENT_PIECE:
          calculatedToSquareList.add(toSquare);
          return calculatedToSquareList;
        case OWN_PIECE:
          if (isAllowOwnPiece) {
            calculatedToSquareList.add(toSquare);
          }
          return calculatedToSquareList;
        default:
          throw new IllegalArgumentException();
      }
    }
    return calculatedToSquareList;
  }

}
