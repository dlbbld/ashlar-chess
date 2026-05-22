package com.dlb.chess.moves;

import java.util.Set;
import java.util.TreeSet;

import com.dlb.chess.board.StaticPosition;
import com.dlb.chess.board.enums.Piece;
import com.dlb.chess.board.enums.Side;
import com.dlb.chess.board.enums.Square;
import com.dlb.chess.common.exceptions.ProgrammingMistakeException;
import com.dlb.chess.common.model.MoveSpecification;
import com.dlb.chess.common.utility.StaticPositionUtility;
import com.dlb.chess.model.LegalMove;
import com.dlb.chess.model.LegalMoveKind;
import com.dlb.chess.squares.PawnPotentialToSquares;

class PawnCaptureEnPassantCaptureLegalMoves extends PawnLegalMoves {
  public static Set<LegalMove> calculateLegalMoves(StaticPosition staticPosition, Square enPassantCaptureTargetSquare,
      Side havingMove, Square fromSquare) {

    final Piece movingPiece = staticPosition.get(fromSquare);
    checkPiece(havingMove, movingPiece, PAWN);

    if (enPassantCaptureTargetSquare == Square.NONE) {
      return new TreeSet<>();
    }

    final Set<Square> diagonalSquareToSet = PawnPotentialToSquares
        .calculatePawnPotentialDiagonalToSquares(staticPosition, enPassantCaptureTargetSquare, fromSquare, havingMove);

    if (!diagonalSquareToSet.contains(enPassantCaptureTargetSquare)) {
      return new TreeSet<>();
    }

    // the pawn on the from square can potentially capture en passant
    final Set<LegalMove> legalMoveSet = new TreeSet<>();

    final MoveSpecification moveSpecification = new MoveSpecification(fromSquare, enPassantCaptureTargetSquare);
    if (!StaticPositionUtility.calculateIsKingAttackedAfterMove(staticPosition, havingMove, moveSpecification)) {

      final Square squareOfCapturedPawnForEnPassantCapture = EnPassantCaptureUtility
          .calculateSquareOfCapturedPawnForEnPassantCapture(havingMove, moveSpecification);
      final Piece pieceCaptured = staticPosition.get(squareOfCapturedPawnForEnPassantCapture);

      final LegalMove legalMove = new LegalMove(moveSpecification, movingPiece, pieceCaptured,
          LegalMoveKind.EN_PASSANT_CAPTURE);
      legalMoveSet.add(legalMove);
    }

    if (legalMoveSet.size() > 1) {
      throw new ProgrammingMistakeException(
          "A pawn can not have more than one possibility to capture en passant at a time");
    }
    return legalMoveSet;
  }

}
