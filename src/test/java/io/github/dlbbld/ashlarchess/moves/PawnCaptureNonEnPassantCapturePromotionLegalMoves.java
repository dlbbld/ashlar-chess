// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.moves;

import java.util.Set;
import java.util.TreeSet;

import io.github.dlbbld.ashlarchess.board.StaticPosition;
import io.github.dlbbld.ashlarchess.board.enums.Piece;
import io.github.dlbbld.ashlarchess.board.enums.PieceType;
import io.github.dlbbld.ashlarchess.board.enums.PromotionPieceType;
import io.github.dlbbld.ashlarchess.board.enums.Rank;
import io.github.dlbbld.ashlarchess.board.enums.RankUtility;
import io.github.dlbbld.ashlarchess.board.enums.Side;
import io.github.dlbbld.ashlarchess.board.enums.Square;
import io.github.dlbbld.ashlarchess.common.model.MoveSpecification;
import io.github.dlbbld.ashlarchess.common.utility.StaticPositionUtility;
import io.github.dlbbld.ashlarchess.model.LegalMove;
import io.github.dlbbld.ashlarchess.model.LegalMoveKind;
import io.github.dlbbld.ashlarchess.squares.PawnDiagonalSquares;

class PawnCaptureNonEnPassantCapturePromotionLegalMoves extends PawnLegalMoves {

  public static Set<LegalMove> calculateLegalMoves(StaticPosition staticPosition, Side havingMove, Square fromSquare) {

    final Piece movingPiece = staticPosition.get(fromSquare);
    checkPiece(havingMove, movingPiece, PAWN);

    final Set<LegalMove> legalMoveSet = new TreeSet<>();
    final Set<Square> diagonalSquareToSet = PawnDiagonalSquares.getPawnDiagonalSquares(havingMove, fromSquare);
    for (final Square diagonalSquareTo : diagonalSquareToSet) {
      if (RankUtility.calculateIsPromotionRank(havingMove, diagonalSquareTo.getRank())
          && staticPosition.isOpponentPiece(diagonalSquareTo, havingMove)) {
        for (final PromotionPieceType promotionPieceType : PromotionPieceType.REAL) {
          final MoveSpecification moveSpecification = new MoveSpecification(fromSquare, diagonalSquareTo,
              promotionPieceType);
          if (!StaticPositionUtility.calculateIsKingAttackedAfterMove(staticPosition, havingMove, moveSpecification)) {

            final Piece pieceCaptured = staticPosition.get(diagonalSquareTo);
            if (pieceCaptured.getPieceType() != PieceType.KING) {
              final LegalMove legalMove = new LegalMove(moveSpecification, movingPiece, pieceCaptured,
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
