// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.moves;

import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.PAWN;

import java.util.Set;
import java.util.TreeSet;

import io.github.dlbbld.ashlarchess.board.LegalMove;
import io.github.dlbbld.ashlarchess.board.LegalMoveKind;
import io.github.dlbbld.ashlarchess.board.MoveSpecification;
import io.github.dlbbld.ashlarchess.board.StaticPosition;
import io.github.dlbbld.ashlarchess.board.enums.Piece;
import io.github.dlbbld.ashlarchess.board.enums.Side;
import io.github.dlbbld.ashlarchess.board.enums.Square;
import io.github.dlbbld.ashlarchess.common.utility.StaticPositionUtility;
import io.github.dlbbld.ashlarchess.exceptions.ProgrammingMistakeException;
import io.github.dlbbld.ashlarchess.squares.PawnPotentialToSquares;

class PawnCaptureEnPassantCaptureLegalMoves extends PawnLegalMoves {
  public static Set<LegalMove> calculateLegalMoves(StaticPosition staticPosition, Square enPassantCaptureTargetSquare,
      Side sideToMove, Square fromSquare) {

    final Piece movingPiece = staticPosition.get(fromSquare);
    LegalMovesSupport.checkPiece(sideToMove, movingPiece, PAWN);

    if (enPassantCaptureTargetSquare == Square.NONE) {
      return new TreeSet<>();
    }

    final Set<Square> diagonalSquareToSet = PawnPotentialToSquares
        .calculatePawnPotentialDiagonalToSquares(staticPosition, enPassantCaptureTargetSquare, fromSquare, sideToMove);

    if (!diagonalSquareToSet.contains(enPassantCaptureTargetSquare)) {
      return new TreeSet<>();
    }

    // the pawn on the from square can potentially capture en passant
    final Set<LegalMove> legalMoveSet = new TreeSet<>();

    final MoveSpecification moveSpecification = new MoveSpecification(fromSquare, enPassantCaptureTargetSquare);
    if (!StaticPositionUtility.calculateIsKingAttackedAfterMove(staticPosition, sideToMove, moveSpecification)) {

      final Square squareOfCapturedPawnForEnPassantCapture = EnPassantCaptureUtility
          .calculateSquareOfCapturedPawnForEnPassantCapture(sideToMove, moveSpecification);
      final Piece capturedPiece = staticPosition.get(squareOfCapturedPawnForEnPassantCapture);

      final LegalMove legalMove = new LegalMove(moveSpecification, movingPiece, capturedPiece,
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
