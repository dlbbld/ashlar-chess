package com.dlb.chess.moves;

import java.util.Set;
import java.util.TreeSet;

import com.dlb.chess.board.StaticPosition;
import com.dlb.chess.board.enums.Piece;
import com.dlb.chess.board.enums.PieceType;
import com.dlb.chess.board.enums.PromotionPieceType;
import com.dlb.chess.board.enums.Rank;
import com.dlb.chess.board.enums.Side;
import com.dlb.chess.board.enums.Square;
import com.dlb.chess.common.model.MoveSpecification;
import com.dlb.chess.common.utility.StaticPositionUtility;
import com.dlb.chess.model.LegalMove;
import com.dlb.chess.model.LegalMoveKind;
import com.dlb.chess.squares.PawnDiagonalSquares;

class PawnCaptureNonEnPassantCapturePromotionLegalMoves extends PawnLegalMoves {

  public static Set<LegalMove> calculateLegalMoves(StaticPosition staticPosition, Side havingMove, Square fromSquare) {

    final Piece movingPiece = staticPosition.get(fromSquare);
    checkPiece(havingMove, movingPiece, PAWN);

    final Set<LegalMove> legalMoveSet = new TreeSet<>();
    final Set<Square> diagonalSquareToSet = PawnDiagonalSquares.getPawnDiagonalSquares(havingMove, fromSquare);
    for (final Square diagonalSquareTo : diagonalSquareToSet) {
      if (Rank.calculateIsPromotionRank(havingMove, diagonalSquareTo.getRank())
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
