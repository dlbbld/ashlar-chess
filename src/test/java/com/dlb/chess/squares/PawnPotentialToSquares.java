package com.dlb.chess.squares;

import java.util.Set;
import java.util.TreeSet;

import com.dlb.chess.board.StaticPosition;
import com.dlb.chess.board.enums.Piece;
import com.dlb.chess.board.enums.Side;
import com.dlb.chess.board.enums.Square;

public class PawnPotentialToSquares extends AbstractPotentialToSquares {

  public static Set<Square> calculatePawnPotentialToSquares(StaticPosition staticPosition,
      Square enPassantCaptureTargetSquare, Square fromSquare, Side havingMove) {

    checkPiece(staticPosition, havingMove, fromSquare, PAWN);

    final Set<Square> advanceSquareSet = calculatePawnPotentialAdvanceToSquares(staticPosition, fromSquare, havingMove);
    final Set<Square> diagonalSquareSet = calculatePawnPotentialDiagonalToSquares(staticPosition,
        enPassantCaptureTargetSquare, fromSquare, havingMove);

    final Set<Square> all = new TreeSet<>(advanceSquareSet);
    all.addAll(diagonalSquareSet);

    return all;
  }

  public static Set<Square> calculatePawnPotentialAdvanceToSquares(StaticPosition staticPosition, Square fromSquare,
      Side havingMove) {

    checkPiece(staticPosition, havingMove, fromSquare, PAWN);

    final Set<Square> advanceSquareSet = new TreeSet<>();

    final Set<Square> emptyBoardOneAdvanceSet = PawnOneAdvanceEmptyBoardSquares.getPawnSquares(havingMove, fromSquare);
    for (final Square oneAdvanceSquare : emptyBoardOneAdvanceSet) {
      final Piece pieceOnToSquare = staticPosition.get(oneAdvanceSquare);
      if (pieceOnToSquare == Piece.NONE) {
        // to square is empty
        advanceSquareSet.add(oneAdvanceSquare);
      }
    }

    final Set<Square> emptyBoardTwoAdvanceSet = PawnTwoAdvanceEmptyBoardSquares.getPawnSquares(havingMove, fromSquare);
    for (final Square twoAdvanceSquare : emptyBoardTwoAdvanceSet) {
      final Square squareJumpOver = Square.calculateJumpOverSquare(havingMove, twoAdvanceSquare);
      final Piece pieceOnJumpOverSquare = staticPosition.get(squareJumpOver);
      if (pieceOnJumpOverSquare == Piece.NONE) {
        // square travelled over is empty
        final Piece pieceOnToSquare = staticPosition.get(twoAdvanceSquare);
        if (pieceOnToSquare == Piece.NONE) {
          // to square is empty
          advanceSquareSet.add(twoAdvanceSquare);
        }
      }
    }
    return advanceSquareSet;
  }

  public static Set<Square> calculatePawnPotentialDiagonalToSquares(StaticPosition staticPosition,
      Square enPassantCaptureTargetSquare, Square fromSquare, Side havingMove) {

    checkPiece(staticPosition, havingMove, fromSquare, PAWN);

    final Set<Square> diagonalSquareSet = new TreeSet<>();

    // first we check for diagonal capturing moves, if none, we return potential en passant moves
    final Set<Square> diagonalSquareCandidateSet = PawnDiagonalSquares.getPawnDiagonalSquares(havingMove, fromSquare);
    for (final Square diagonalSquareCandidate : diagonalSquareCandidateSet) {
      if (staticPosition.isOpponentPiece(diagonalSquareCandidate, havingMove)
          && !staticPosition.isOpponentKing(diagonalSquareCandidate, havingMove)
          || staticPosition.isEmpty(diagonalSquareCandidate)
              && enPassantCaptureTargetSquare == diagonalSquareCandidate) {
        diagonalSquareSet.add(diagonalSquareCandidate);
      }
    }
    return diagonalSquareSet;
  }

}
