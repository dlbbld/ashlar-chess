package com.dlb.chess.squares;

import java.util.Set;
import java.util.TreeSet;

import com.dlb.chess.board.StaticPosition;
import com.dlb.chess.board.enums.Piece;
import com.dlb.chess.board.enums.PieceType;
import com.dlb.chess.board.enums.Side;
import com.dlb.chess.board.enums.Square;
import com.dlb.chess.enums.SquareOccupation;

/**
 * Potentially to defined as all squares the piece can move to, being empty or occupied by an opponent piece, in the
 * case of rook, bishop or queen additionally additionally requiring that the square is "visible", that is the squares
 * between the from and to squares are all empty and in the case of pawns excluding the diagonal moves (for the reason,
 * that these moves requires additional conditions, contrary to the other pieces).
 *
 *
 */
public abstract class AbstractPotentialToSquares extends AbstractToSquares {

  public static Set<Square> calculatePotentialToSquare(StaticPosition staticPosition,
      Square enPassantCaptureTargetSquare, Side havingMove, Square fromSquare) {

    final Piece pieceOnFromSquare = staticPosition.get(fromSquare);

    return switch (pieceOnFromSquare.getPieceType()) {
      case PAWN -> PawnPotentialToSquares.calculatePawnPotentialToSquares(staticPosition, enPassantCaptureTargetSquare,
          fromSquare, havingMove);
      case ROOK, BISHOP, QUEEN -> AbstractRangeSquares.calculateRangeSquare(staticPosition, havingMove, fromSquare,
          false);
      case KNIGHT -> KnightPotentialToSquares.calculateKnightPotentialToSquares(staticPosition, fromSquare, havingMove);
      case KING -> KingNonCastlingPotentialToSquares.calculateKingNonCastlingPotentialToSquares(staticPosition,
          fromSquare, havingMove);
      case NONE -> new TreeSet<>();
      default -> throw new IllegalArgumentException();
    };
  }

  // knight or non castling king
  static Set<Square> calculateNonRangeNonPawnPotentialToSquares(StaticPosition staticPosition, Square fromSquare,
      PieceType pieceType, Set<Square> emptyBoardSquareSet, Side havingMove) {

    checkPiece(staticPosition, havingMove, fromSquare, pieceType);

    final Set<Square> potentialToSquareSet = new TreeSet<>();

    for (final Square toSquare : emptyBoardSquareSet) {
      final SquareOccupation squareOccupation = calculateSquareOccupation(staticPosition, havingMove, toSquare);
      switch (squareOccupation) {
        case NONE:
          potentialToSquareSet.add(toSquare);
          break;
        case OPPONENT_PIECE:
          potentialToSquareSet.add(toSquare);
          break;
        case OWN_PIECE:
          break;
        default:
          throw new IllegalArgumentException();
      }
    }
    return potentialToSquareSet;
  }
}
