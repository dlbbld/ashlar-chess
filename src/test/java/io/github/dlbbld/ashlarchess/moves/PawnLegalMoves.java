// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.moves;

import java.util.Set;
import java.util.TreeSet;

import io.github.dlbbld.ashlarchess.board.StaticPosition;
import io.github.dlbbld.ashlarchess.board.enums.Side;
import io.github.dlbbld.ashlarchess.board.enums.Square;
import io.github.dlbbld.ashlarchess.model.LegalMove;

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
