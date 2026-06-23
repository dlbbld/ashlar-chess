// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.report;

import io.github.dlbbld.ashlarchess.board.DynamicPosition;
import io.github.dlbbld.ashlarchess.board.MoveSpecification;
import io.github.dlbbld.ashlarchess.board.enums.Piece;

// A recorded played move: the move plus the state it produced and its game numbering. Built by MoveRecords from a Board.
// only use in lists, added in order of play; not used in sets per this design, so no sorting needed.
// Pure data record: the side to move is movingPiece().getSide(); move-number-plus-SAN labels are formatted by
// MoveNumberFormat at the report print boundary (see SequenceStartFormat / PositionIdentifierUtility).
record MoveRecord(int performedMoveCount, int fullMoveNumber, int halfMoveClock, DynamicPosition dynamicPosition,
    int countRepetition, String san, Piece movingPiece, MoveSpecification moveSpecification) {
}
