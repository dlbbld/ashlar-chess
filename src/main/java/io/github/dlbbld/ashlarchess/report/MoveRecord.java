// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.report;

import io.github.dlbbld.ashlarchess.board.HalfMoveUtility;
import io.github.dlbbld.ashlarchess.board.enums.Piece;
import io.github.dlbbld.ashlarchess.board.enums.Side;
import io.github.dlbbld.ashlarchess.common.model.DynamicPosition;
import io.github.dlbbld.ashlarchess.common.model.MoveSpecification;

// A recorded played move: the move plus the state it produced and its game numbering. Built by MoveRecords from a Board.
// only use in lists, added in order of play; not used in sets per this design, so no sorting needed.
record MoveRecord(int performedMoveCount, int fullMoveNumber, int halfMoveClock, DynamicPosition dynamicPosition,
    int countRepetition, String san, Piece movingPiece, MoveSpecification moveSpecification) {

  public Side havingMove() {
    return movingPiece.getSide();
  }

  @Override
  public String toString() {
    return HalfMoveUtility.calculateMoveNumberAndSanWithSpace(fullMoveNumber, havingMove(), san);
  }
}
