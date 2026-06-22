// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.report;

import java.util.ArrayList;
import java.util.List;

import io.github.dlbbld.ashlarchess.board.Board;
import io.github.dlbbld.ashlarchess.common.Nulls;
import io.github.dlbbld.ashlarchess.common.model.MoveSpecification;
import io.github.dlbbld.ashlarchess.model.LegalMove;

/**
 * Builds {@link MoveRecord} rows for a played game: the report layer reconstructs the rows from {@code Board}'s public
 * per-move accessors. {@link #played(Board)} replays from the initial FEN, so it is
 * {@code O(moves * legal-move-generation)} - intended for report building, not hot paths.
 */
final class MoveRecords {

  private MoveRecords() {
  }

  static List<MoveRecord> played(Board board) {
    final List<MoveSpecification> moves = board.getPerformedMoveSpecifications();
    final Board replay = new Board(board.getInitialFen());
    final List<MoveRecord> result = new ArrayList<>(moves.size());
    for (final MoveSpecification moveSpecification : moves) {
      replay.move(moveSpecification);
      result.add(lastPlayed(replay));
    }
    return Nulls.copyOfList(result);
  }

  static MoveRecord lastPlayed(Board board) {
    if (board.getPerformedMoveCount() == 0) {
      throw new IllegalStateException("There is no last move");
    }
    final LegalMove last = board.getLastMove();
    return new MoveRecord(board.getPerformedMoveCount(), board.getLastPlayedFullMoveNumber(), board.getHalfMoveClock(),
        board.getDynamicPosition(), board.getRepetitionCount(), board.getSan(), last.movingPiece(),
        last.moveSpecification());
  }
}
