package io.github.dlbbld.ashlarchess.squares;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import io.github.dlbbld.ashlarchess.board.enums.PieceType;
import io.github.dlbbld.ashlarchess.board.enums.Side;
import io.github.dlbbld.ashlarchess.board.enums.Square;
import io.github.dlbbld.ashlarchess.model.EmptyBoardMove;

public abstract class AbstractEmptyBoardSquares {

  public static Set<EmptyBoardMove> calculateNonPawnEmptyBoardMoves(PieceType pieceType, Square fromSquare) {
    return switch (pieceType) {
      case ROOK -> calculateRookEmptyBoardMoves(fromSquare);
      case KNIGHT -> calculateKnightEmptyBoardMoves(fromSquare);
      case BISHOP -> calculateBishopEmptyBoardMoves(fromSquare);
      case QUEEN -> calculateQueenEmptyBoardMoves(fromSquare);
      case KING -> calculateKingEmptyBoardMoves(fromSquare);
      case PAWN, NONE -> throw new IllegalArgumentException();
      default -> throw new IllegalArgumentException();
    };
  }

  public static Set<EmptyBoardMove> calculateNonPawnEmptyBoardMoves(PieceType pieceType) {
    return switch (pieceType) {
      case ROOK -> calculateRookEmptyBoardMoves();
      case KNIGHT -> calculateKnightEmptyBoardMoves();
      case BISHOP -> calculateBishopEmptyBoardMoves();
      case QUEEN -> calculateQueenEmptyBoardMoves();
      case KING -> calculateKingEmptyBoardMoves();
      case PAWN, NONE -> throw new IllegalArgumentException();
      default -> throw new IllegalArgumentException();
    };
  }

  public static Set<EmptyBoardMove> calculateNonPawnEmptyBoardMovesTo(PieceType pieceType, Square toSquare) {
    return reverse(calculateNonPawnEmptyBoardMoves(pieceType, toSquare));
  }

  public static Set<EmptyBoardMove> calculatePawnEmptyBoardMoves(Side havingMove, Square fromSquare) {
    return calculateEmptyBoardMovesFromSet(fromSquare,
        PawnAnyAdvanceEmptyBoardSquares.getPawnSquares(havingMove, fromSquare));
  }

  /**
   * Calculate the pawn moves.
   *
   * @param havingMove The side having the move.
   * @return All pawn moves except diagonal moves (implemented as such because they are not possible on an empty board).
   *
   */
  public static Set<EmptyBoardMove> calculatePawnEmptyBoardMoves(Side havingMove) {
    final Set<EmptyBoardMove> emptyBoardMoves = new TreeSet<>();
    for (final Square fromSquare : Square.REAL) {
      emptyBoardMoves.addAll(calculateEmptyBoardMovesFromSet(fromSquare,
          PawnAnyAdvanceEmptyBoardSquares.getPawnSquares(havingMove, fromSquare)));
    }
    return emptyBoardMoves;
  }

  private static Set<EmptyBoardMove> calculateRookEmptyBoardMoves(Square fromSquare) {
    return calculateOrthogonalEmptyBoardMoves(fromSquare, RookEmptyBoardSquares.getRookSquares(fromSquare));
  }

  private static Set<EmptyBoardMove> calculateRookEmptyBoardMoves() {
    final Set<EmptyBoardMove> emptyBoardMoves = new TreeSet<>();
    for (final Square fromSquare : Square.REAL) {
      emptyBoardMoves
          .addAll(calculateOrthogonalEmptyBoardMoves(fromSquare, RookEmptyBoardSquares.getRookSquares(fromSquare)));
    }
    return emptyBoardMoves;
  }

  private static Set<EmptyBoardMove> calculateKnightEmptyBoardMoves(Square fromSquare) {
    return calculateEmptyBoardMovesFromSet(fromSquare, KnightEmptyBoardSquares.getKnightSquares(fromSquare));
  }

  private static Set<EmptyBoardMove> calculateKnightEmptyBoardMoves() {
    final Set<EmptyBoardMove> emptyBoardMoves = new TreeSet<>();
    for (final Square fromSquare : Square.REAL) {
      emptyBoardMoves
          .addAll(calculateEmptyBoardMovesFromSet(fromSquare, KnightEmptyBoardSquares.getKnightSquares(fromSquare)));
    }
    return emptyBoardMoves;
  }

  private static Set<EmptyBoardMove> calculateBishopEmptyBoardMoves(Square fromSquare) {
    return calculateDiagonalEmptyBoardMoves(fromSquare, BishopEmptyBoardSquares.getBishopSquares(fromSquare));
  }

  private static Set<EmptyBoardMove> calculateBishopEmptyBoardMoves() {
    final Set<EmptyBoardMove> emptyBoardMoves = new TreeSet<>();
    for (final Square fromSquare : Square.REAL) {
      emptyBoardMoves
          .addAll(calculateDiagonalEmptyBoardMoves(fromSquare, BishopEmptyBoardSquares.getBishopSquares(fromSquare)));
    }
    return emptyBoardMoves;
  }

  private static Set<EmptyBoardMove> calculateQueenEmptyBoardMoves(Square fromSquare) {
    final Set<EmptyBoardMove> result = new TreeSet<>(
        calculateOrthogonalEmptyBoardMoves(fromSquare, QueenEmptyBoardSquares.getQueenSquares(fromSquare)));

    result.addAll(calculateDiagonalEmptyBoardMoves(fromSquare, QueenEmptyBoardSquares.getQueenSquares(fromSquare)));

    return result;
  }

  private static Set<EmptyBoardMove> calculateQueenEmptyBoardMoves() {
    final Set<EmptyBoardMove> emptyBoardMoves = new TreeSet<>();
    for (final Square fromSquare : Square.REAL) {
      emptyBoardMoves
          .addAll(calculateOrthogonalEmptyBoardMoves(fromSquare, QueenEmptyBoardSquares.getQueenSquares(fromSquare)));
    }
    for (final Square fromSquare : Square.REAL) {
      emptyBoardMoves
          .addAll(calculateDiagonalEmptyBoardMoves(fromSquare, QueenEmptyBoardSquares.getQueenSquares(fromSquare)));
    }
    return emptyBoardMoves;
  }

  private static Set<EmptyBoardMove> calculateKingEmptyBoardMoves(Square fromSquare) {
    return calculateEmptyBoardMovesFromSet(fromSquare, KingNonCastlingEmptyBoardSquares.getKingSquares(fromSquare));
  }

  private static Set<EmptyBoardMove> calculateKingEmptyBoardMoves() {
    final Set<EmptyBoardMove> emptyBoardMoves = new TreeSet<>();
    for (final Square fromSquare : Square.REAL) {
      emptyBoardMoves.addAll(
          calculateEmptyBoardMovesFromSet(fromSquare, KingNonCastlingEmptyBoardSquares.getKingSquares(fromSquare)));
    }
    return emptyBoardMoves;
  }

  private static Set<EmptyBoardMove> reverse(Set<EmptyBoardMove> emptyBoardMoveSet) {
    final Set<EmptyBoardMove> reversedSet = new TreeSet<>();
    for (final EmptyBoardMove emptyBoardMove : emptyBoardMoveSet) {
      reversedSet.add(new EmptyBoardMove(emptyBoardMove.toSquare(), emptyBoardMove.fromSquare()));
    }
    return reversedSet;
  }

  private static Set<EmptyBoardMove> calculateEmptyBoardMovesFromSet(Square fromSquare, Set<Square> toQuareSet) {
    final Set<EmptyBoardMove> emptyBoardMovesFromSet = new TreeSet<>();
    for (final Square toSquare : toQuareSet) {
      emptyBoardMovesFromSet.add(new EmptyBoardMove(fromSquare, toSquare));
    }
    return emptyBoardMovesFromSet;
  }

  private static Set<EmptyBoardMove> calculateEmptyBoardMovesFromList(Square fromSquare, List<Square> toQuareList) {
    final Set<EmptyBoardMove> emptyBoardMovesFromList = new TreeSet<>();
    for (final Square toSquare : toQuareList) {
      emptyBoardMovesFromList.add(new EmptyBoardMove(fromSquare, toSquare));
    }
    return emptyBoardMovesFromList;
  }

  private static Set<EmptyBoardMove> calculateOrthogonalEmptyBoardMoves(Square fromSquare,
      OrthogonalRange toQuareRange) {
    final Set<EmptyBoardMove> emptyBoardMovesFromListList = new TreeSet<>(
        calculateEmptyBoardMovesFromList(fromSquare, toQuareRange.squareListNorth()));

    emptyBoardMovesFromListList.addAll(calculateEmptyBoardMovesFromList(fromSquare, toQuareRange.squareListEast()));
    emptyBoardMovesFromListList.addAll(calculateEmptyBoardMovesFromList(fromSquare, toQuareRange.squareListSouth()));
    emptyBoardMovesFromListList.addAll(calculateEmptyBoardMovesFromList(fromSquare, toQuareRange.squareListWest()));

    return emptyBoardMovesFromListList;
  }

  private static Set<EmptyBoardMove> calculateDiagonalEmptyBoardMoves(Square fromSquare, DiagonalRange toQuareRange) {
    final Set<EmptyBoardMove> emptyBoardMovesFromListList = new TreeSet<>(
        calculateEmptyBoardMovesFromList(fromSquare, toQuareRange.squareListNorthEast()));

    emptyBoardMovesFromListList
        .addAll(calculateEmptyBoardMovesFromList(fromSquare, toQuareRange.squareListSouthEast()));
    emptyBoardMovesFromListList
        .addAll(calculateEmptyBoardMovesFromList(fromSquare, toQuareRange.squareListSouthWest()));
    emptyBoardMovesFromListList
        .addAll(calculateEmptyBoardMovesFromList(fromSquare, toQuareRange.squareListNorthWest()));

    return emptyBoardMovesFromListList;
  }

}
