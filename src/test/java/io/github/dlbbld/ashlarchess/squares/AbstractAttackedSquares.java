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
public abstract class AbstractAttackedSquares extends AbstractToSquares {
  public static Set<Square> calculateAttackedSquares(StaticPosition staticPosition, Side havingMove) {

    final Set<Square> squareSet = new TreeSet<>();

    for (final Square fromSquare : Square.REAL) {
      if (staticPosition.isOwnPiece(fromSquare, havingMove)) {
        final Piece piece = staticPosition.get(fromSquare);
        switch (piece.getPieceType()) {
          case PAWN -> squareSet
              .addAll(PawnAttackedSquares.calculatePawnAttackedSquares(staticPosition, fromSquare, havingMove));
          case ROOK -> squareSet
              .addAll(RookAttackedSquares.calculateRookAttackedSquares(staticPosition, fromSquare, havingMove));
          case KNIGHT -> squareSet
              .addAll(KnightAttackedSquares.calculateKnightAttackedSquares(staticPosition, fromSquare, havingMove));
          case BISHOP -> squareSet
              .addAll(BishopAttackedSquares.calculateBishopAttackedSquares(staticPosition, fromSquare, havingMove));
          case QUEEN -> squareSet
              .addAll(QueenAttackedSquares.calculateQueenAttackedSquares(staticPosition, fromSquare, havingMove));
          case KING -> squareSet.addAll(KingNonCastlingAttackedSquares
              .calculateKingNonCastlingAttackedSquares(staticPosition, fromSquare, havingMove));
          case NONE -> throw new IllegalArgumentException();
          default -> throw new IllegalArgumentException();
        }
      }
    }

    return squareSet;
  }

}
