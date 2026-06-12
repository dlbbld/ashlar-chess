// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.report;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.common.collect.ImmutableList;

import io.github.dlbbld.ashlarchess.board.Board;
import io.github.dlbbld.ashlarchess.common.Nulls;
import io.github.dlbbld.ashlarchess.common.model.DynamicPosition;
import io.github.dlbbld.ashlarchess.common.model.MoveSpecification;
import io.github.dlbbld.ashlarchess.fen.model.Fen;
import io.github.dlbbld.ashlarchess.model.LegalMove;

abstract class ThreefoldClaimAheadReportBuilder {

  /**
   * Builds the claim-ahead report by replaying the game on an internal fresh board. The {@code board} argument is
   * read-only - never receives {@code move} or {@code unmove} calls.
   *
   * <p>
   * Entries are ordered by {@link ReportLineOrder#CLAIM_AHEAD_COMPARATOR}: lexicographic on the displayed move-
   * count sequence ({@code priorOccurrences ++ claimAheadMove}, prefixed by a virtual {@code -1} when
   * {@code includesInitialPosition} is true). Sequences that share earlier moves stay adjacent and progress length-3 ->
   * length-4 -> length-5 (the shorter is a prefix of the longer in standard lex order). When the played history reaches
   * the same dynamic position multiple times, the earlier claim-ahead boundary surfaces first.
   */
  static ThreefoldClaimAheadReport build(Board board) {
    final List<MoveRecord> rawClaimAheads = replayAndCollectClaimAheads(board.getPerformedLegalMoveList(),
        board.getInitialFen());
    final ImmutableList<MoveRecord> moveRecordListPlayed = MoveRecords.played(board);
    final DynamicPosition initialDynamicPosition = board.getInitialDynamicPosition();

    final List<ClaimAheadEntry> entries = new ArrayList<>();
    for (final MoveRecord claimAheadMove : rawClaimAheads) {
      entries.add(buildEntry(claimAheadMove, moveRecordListPlayed, initialDynamicPosition));
    }
    Collections.sort(entries, ReportLineOrder.CLAIM_AHEAD_COMPARATOR);
    return new ThreefoldClaimAheadReport(Nulls.copyOfList(entries));
  }

  private static List<MoveRecord> replayAndCollectClaimAheads(List<LegalMove> performedLegalMoveList, Fen initialFen) {
    final List<MoveRecord> result = new ArrayList<>();
    final Board replayBoard = new Board(initialFen);
    for (final LegalMove legalMove : performedLegalMoveList) {
      collectClaimAheadsAtCurrentMove(result, replayBoard);
      replayBoard.move(legalMove.moveSpecification());
    }
    collectClaimAheadsAtCurrentMove(result, replayBoard);
    return result;
  }

  private static void collectClaimAheadsAtCurrentMove(List<MoveRecord> result, Board replayBoard) {
    for (final LegalMove legalMoveCheckAhead : replayBoard.getLegalMoves()) {
      final MoveSpecification move = legalMoveCheckAhead.moveSpecification();
      // Single source of truth: the report's per-move claim-ahead entries are exactly the moves
      // for which Board.canClaimThreefoldRepetitionRuleFor returns true. Any future change to that
      // predicate (e.g. tighter FIDE 9.2 semantics) is automatically reflected here without a
      // parallel update.
      if (replayBoard.canClaimThreefoldRepetitionRuleFor(move)) {
        // The predicate did a transient push+unmove internally; re-push here to capture the
        // produced MoveRecord that the entry needs to carry. The duplicated push is intentional
        // overhead - it keeps the predicate as the contract and the builder as a consumer of it.
        replayBoard.move(move);
        result.add(MoveRecords.lastPlayed(replayBoard));
        replayBoard.unmove();
      }
    }
  }

  private static ClaimAheadEntry buildEntry(MoveRecord claimAheadMove, ImmutableList<MoveRecord> moveRecordListPlayed,
      DynamicPosition initialDynamicPosition) {

    final boolean hasBeenPlayed = moveRecordListPlayed.contains(claimAheadMove);
    final boolean includesInitialPosition = initialDynamicPosition.equals(claimAheadMove.dynamicPosition());

    final List<MoveRecord> priorOccurrences = new ArrayList<>();
    for (final MoveRecord played : moveRecordListPlayed) {
      if (played.performedMoveCount() >= claimAheadMove.performedMoveCount()) {
        break;
      }
      if (played.dynamicPosition().equals(claimAheadMove.dynamicPosition())) {
        priorOccurrences.add(played);
      }
    }

    final int totalRepetitionCount = priorOccurrences.size() + 1 + (includesInitialPosition ? 1 : 0);
    return new ClaimAheadEntry(claimAheadMove, hasBeenPlayed, Nulls.copyOfList(priorOccurrences),
        includesInitialPosition, totalRepetitionCount);
  }
}
