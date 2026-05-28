package io.github.dlbbld.ashlarchess.moves;

import java.util.Set;
import java.util.TreeSet;

import io.github.dlbbld.ashlarchess.board.StaticPosition;
import io.github.dlbbld.ashlarchess.board.enums.Piece;
import io.github.dlbbld.ashlarchess.board.enums.Side;
import io.github.dlbbld.ashlarchess.board.enums.Square;
import io.github.dlbbld.ashlarchess.common.exceptions.ProgrammingMistakeException;
import io.github.dlbbld.ashlarchess.common.model.MoveSpecification;
import io.github.dlbbld.ashlarchess.common.utility.StaticPositionUtility;
import io.github.dlbbld.ashlarchess.model.LegalMove;
import io.github.dlbbld.ashlarchess.model.LegalMoveKind;
import io.github.dlbbld.ashlarchess.moves.EnPassantCaptureUtility;
import io.github.dlbbld.ashlarchess.squares.PawnPotentialToSquares;

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
