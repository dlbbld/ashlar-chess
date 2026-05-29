// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.common.model;

import io.github.dlbbld.ashlarchess.board.HalfMoveUtility;
import io.github.dlbbld.ashlarchess.board.enums.Piece;
import io.github.dlbbld.ashlarchess.board.enums.Side;

// only use half moves in lists, and then add them in order of play.
// not used in sets per this design, so we don't need sorting.
public record HalfMove(int halfMoveCount, int fullMoveNumber, int halfMoveClock, DynamicPosition dynamicPosition,
    int countRepetition, String san, Piece movingPiece, MoveSpecification moveSpecification) {

  public Side havingMove() {
    return movingPiece.getSide();
  }

  @Override
  public String toString() {
    return HalfMoveUtility.calculateMoveNumberAndSanWithSpace(this);
  }

}
