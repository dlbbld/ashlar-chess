// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.report;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.ImmutableList;

import io.github.dlbbld.ashlarchess.board.Board;
import io.github.dlbbld.ashlarchess.common.Nulls;
import io.github.dlbbld.ashlarchess.common.model.MoveSpecification;
import io.github.dlbbld.ashlarchess.model.LegalMove;

/**
 * Builds {@link MoveRecord} rows for a played game. Replaces the play-history list bridge removed from {@link Board}
 * (the former per-game row surface): the report layer reconstructs the rows here from {@code Board}'s public per-move
 * accessors rather than Board pre-bundling them. {@link #played(Board)} replays from the initial FEN, so it is
 * {@code O(moves * legal-move-generation)} - intended for report building, not hot paths.
 */
abstract class MoveRecords {

  static ImmutableList<MoveRecord> played(Board board) {
    final ImmutableList<MoveSpecification> moves = board.getPerformedMoveSpecificationList();
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
    return new MoveRecord(board.getPerformedMoveCount(), board.getLastPlayedFullMoveNumber(),
        board.getHalfMoveClock(), board.getDynamicPosition(), board.getRepetitionCount(), board.getSan(),
        last.movingPiece(), last.moveSpecification());
  }
}
