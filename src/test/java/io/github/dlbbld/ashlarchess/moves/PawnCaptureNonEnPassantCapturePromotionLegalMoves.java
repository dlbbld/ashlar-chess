// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.moves;

import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.PAWN;

import java.util.Set;
import java.util.TreeSet;

import io.github.dlbbld.ashlarchess.board.StaticPosition;
import io.github.dlbbld.ashlarchess.board.enums.Piece;
import io.github.dlbbld.ashlarchess.board.enums.PieceType;
import io.github.dlbbld.ashlarchess.board.enums.PromotionPieceType;
import io.github.dlbbld.ashlarchess.board.enums.internal.RankUtility;
import io.github.dlbbld.ashlarchess.board.enums.Side;
import io.github.dlbbld.ashlarchess.board.enums.Square;
import io.github.dlbbld.ashlarchess.board.MoveSpecification;
import io.github.dlbbld.ashlarchess.common.utility.StaticPositionUtility;
import io.github.dlbbld.ashlarchess.board.LegalMove;
import io.github.dlbbld.ashlarchess.board.LegalMoveKind;
import io.github.dlbbld.ashlarchess.squares.PawnDiagonalSquares;

class PawnCaptureNonEnPassantCapturePromotionLegalMoves extends PawnLegalMoves {

  public static Set<LegalMove> calculateLegalMoves(StaticPosition staticPosition, Side sideToMove, Square fromSquare) {

    final Piece movingPiece = staticPosition.get(fromSquare);
    LegalMovesSupport.checkPiece(sideToMove, movingPiece, PAWN);

    final Set<LegalMove> legalMoveSet = new TreeSet<>();
    final Set<Square> diagonalSquareToSet = PawnDiagonalSquares.getPawnDiagonalSquares(sideToMove, fromSquare);
    for (final Square diagonalSquareTo : diagonalSquareToSet) {
      if (RankUtility.isPromotionRank(sideToMove, diagonalSquareTo.getRank())
          && staticPosition.isOpponentPiece(diagonalSquareTo, sideToMove)) {
        for (final PromotionPieceType promotionPieceType : PromotionPieceType.REAL) {
          final MoveSpecification moveSpecification = new MoveSpecification(fromSquare, diagonalSquareTo,
              promotionPieceType);
          if (!StaticPositionUtility.calculateIsKingAttackedAfterMove(staticPosition, sideToMove, moveSpecification)) {

            final Piece capturedPiece = staticPosition.get(diagonalSquareTo);
            if (capturedPiece.getPieceType() != PieceType.KING) {
              final LegalMove legalMove = new LegalMove(moveSpecification, movingPiece, capturedPiece,
                  LegalMoveKind.PROMOTION);
              legalMoveSet.add(legalMove);
            }
          }
        }
      }
    }

    return legalMoveSet;
  }
}
