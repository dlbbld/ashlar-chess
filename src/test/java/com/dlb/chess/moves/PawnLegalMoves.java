package com.dlb.chess.moves;

import java.util.Set;
import java.util.TreeSet;

import com.dlb.chess.board.StaticPosition;
import com.dlb.chess.board.enums.Side;
import com.dlb.chess.board.enums.Square;
import com.dlb.chess.model.LegalMove;

class PawnLegalMoves extends AbstractLegalMoves {
  public static Set<LegalMove> calculatePawnLegalMoves(StaticPosition staticPosition,
      Square enPassantCaptureTargetSquare, Side havingMove, Square fromSquare) {

    final Set<LegalMove> legalMoveSet = new TreeSet<>(
        PawnForwardNonPromotionLegalMoves.calculateLegalMoves(staticPosition, havingMove, fromSquare));

    legalMoveSet.addAll(PawnForwardPromotionLegalMoves.calculateLegalMoves(staticPosition, havingMove, fromSquare));
    legalMoveSet.addAll(PawnCaptureNonEnPassantCaptureNonPromotionLegalMoves.calculateLegalMoves(staticPosition,
        havingMove, fromSquare));
    legalMoveSet.addAll(
        PawnCaptureNonEnPassantCapturePromotionLegalMoves.calculateLegalMoves(staticPosition, havingMove, fromSquare));
    legalMoveSet.addAll(PawnCaptureEnPassantCaptureLegalMoves.calculateLegalMoves(staticPosition,
        enPassantCaptureTargetSquare, havingMove, fromSquare));

    return legalMoveSet;
  }

}
