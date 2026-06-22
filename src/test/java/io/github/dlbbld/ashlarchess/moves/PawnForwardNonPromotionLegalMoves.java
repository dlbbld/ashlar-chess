// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.moves;

import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.PAWN;

import java.util.Set;
import java.util.TreeSet;

import io.github.dlbbld.ashlarchess.board.StaticPosition;
import io.github.dlbbld.ashlarchess.board.enums.Piece;
import io.github.dlbbld.ashlarchess.board.enums.RankUtility;
import io.github.dlbbld.ashlarchess.board.enums.Side;
import io.github.dlbbld.ashlarchess.board.enums.Square;
import io.github.dlbbld.ashlarchess.board.MoveSpecification;
import io.github.dlbbld.ashlarchess.common.utility.StaticPositionUtility;
import io.github.dlbbld.ashlarchess.board.LegalMove;
import io.github.dlbbld.ashlarchess.board.LegalMoveKind;
import io.github.dlbbld.ashlarchess.squares.PawnPotentialToSquares;

class PawnForwardNonPromotionLegalMoves extends PawnLegalMoves {

  public static Set<LegalMove> calculateLegalMoves(StaticPosition staticPosition, Side sideToMove, Square fromSquare) {

    final Piece movingPiece = staticPosition.get(fromSquare);
    LegalMovesSupport.checkPiece(sideToMove, movingPiece, PAWN);

    final Set<LegalMove> legalMoveSet = new TreeSet<>();

    final Set<Square> pawnPotentialToSquareSet = PawnPotentialToSquares
        .calculatePawnPotentialAdvanceToSquares(staticPosition, fromSquare, sideToMove);

    for (final Square toSquare : pawnPotentialToSquareSet) {
      if (!RankUtility.isPromotionRank(sideToMove, toSquare.getRank())) {
        final MoveSpecification moveSpecification = new MoveSpecification(fromSquare, toSquare);
        if (!StaticPositionUtility.calculateIsKingAttackedAfterMove(staticPosition, sideToMove, moveSpecification)) {
          final Piece capturedPiece = staticPosition.get(toSquare);
          final LegalMoveKind kind = EnPassantCaptureUtility.isPawnTwoSquareAdvanceMove(movingPiece, moveSpecification)
              ? LegalMoveKind.PAWN_TWO_SQUARE_ADVANCE
              : LegalMoveKind.NORMAL;
          final LegalMove legalMove = new LegalMove(moveSpecification, movingPiece, capturedPiece, kind);
          legalMoveSet.add(legalMove);
        }
      }
    }

    return legalMoveSet;
  }
}
