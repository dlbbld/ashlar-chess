// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.moves;

import java.util.Set;
import java.util.TreeSet;

import io.github.dlbbld.ashlarchess.board.StaticPosition;
import io.github.dlbbld.ashlarchess.board.enums.Side;
import io.github.dlbbld.ashlarchess.board.enums.Square;
import io.github.dlbbld.ashlarchess.board.LegalMove;

class PawnLegalMoves {
  public static Set<LegalMove> calculatePawnLegalMoves(StaticPosition staticPosition,
      Square enPassantCaptureTargetSquare, Side sideToMove, Square fromSquare) {

    final Set<LegalMove> legalMoveSet = new TreeSet<>(
        PawnForwardNonPromotionLegalMoves.calculateLegalMoves(staticPosition, sideToMove, fromSquare));

    legalMoveSet.addAll(PawnForwardPromotionLegalMoves.calculateLegalMoves(staticPosition, sideToMove, fromSquare));
    legalMoveSet.addAll(PawnCaptureNonEnPassantCaptureNonPromotionLegalMoves.calculateLegalMoves(staticPosition,
        sideToMove, fromSquare));
    legalMoveSet.addAll(
        PawnCaptureNonEnPassantCapturePromotionLegalMoves.calculateLegalMoves(staticPosition, sideToMove, fromSquare));
    legalMoveSet.addAll(PawnCaptureEnPassantCaptureLegalMoves.calculateLegalMoves(staticPosition,
        enPassantCaptureTargetSquare, sideToMove, fromSquare));

    return legalMoveSet;
  }

}
