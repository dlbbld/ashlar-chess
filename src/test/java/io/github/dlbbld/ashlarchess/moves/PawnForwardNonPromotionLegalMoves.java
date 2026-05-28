package io.github.dlbbld.ashlarchess.moves;

import java.util.Set;
import java.util.TreeSet;

import io.github.dlbbld.ashlarchess.board.StaticPosition;
import io.github.dlbbld.ashlarchess.board.enums.Piece;
import io.github.dlbbld.ashlarchess.board.enums.Rank;
import io.github.dlbbld.ashlarchess.board.enums.Side;
import io.github.dlbbld.ashlarchess.board.enums.Square;
import io.github.dlbbld.ashlarchess.common.model.MoveSpecification;
import io.github.dlbbld.ashlarchess.common.utility.StaticPositionUtility;
import io.github.dlbbld.ashlarchess.model.LegalMove;
import io.github.dlbbld.ashlarchess.model.LegalMoveKind;
import io.github.dlbbld.ashlarchess.moves.EnPassantCaptureUtility;
import io.github.dlbbld.ashlarchess.squares.PawnPotentialToSquares;

class PawnForwardNonPromotionLegalMoves extends PawnLegalMoves {

  public static Set<LegalMove> calculateLegalMoves(StaticPosition staticPosition, Side havingMove, Square fromSquare) {

    final Piece movingPiece = staticPosition.get(fromSquare);
    checkPiece(havingMove, movingPiece, PAWN);

    final Set<LegalMove> legalMoveSet = new TreeSet<>();

    final Set<Square> pawnPotentialToSquareSet = PawnPotentialToSquares
        .calculatePawnPotentialAdvanceToSquares(staticPosition, fromSquare, havingMove);

    for (final Square toSquare : pawnPotentialToSquareSet) {
      if (!Rank.calculateIsPromotionRank(havingMove, toSquare.getRank())) {
        final MoveSpecification moveSpecification = new MoveSpecification(fromSquare, toSquare);
        if (!StaticPositionUtility.calculateIsKingAttackedAfterMove(staticPosition, havingMove, moveSpecification)) {
          final Piece pieceCaptured = staticPosition.get(toSquare);
          final LegalMoveKind kind = EnPassantCaptureUtility.calculateIsPawnTwoSquareAdvanceMove(movingPiece,
              moveSpecification) ? LegalMoveKind.PAWN_TWO_SQUARE_ADVANCE : LegalMoveKind.NORMAL;
          final LegalMove legalMove = new LegalMove(moveSpecification, movingPiece, pieceCaptured, kind);
          legalMoveSet.add(legalMove);
        }
      }
    }

    return legalMoveSet;
  }
}
