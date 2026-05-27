package com.dlb.chess.report;

import java.util.ArrayList;
import java.util.List;

import com.dlb.chess.board.Board;
import com.dlb.chess.common.model.DynamicPosition;
import com.dlb.chess.common.model.HalfMove;
import com.google.common.collect.ImmutableList;

abstract class ThreefoldClaimAheadReportBuilder {

  /**
   * Builds the claim-ahead report by replaying the game on an internal fresh board. The {@code board} argument is
   * read-only — never receives {@code move} or {@code unmove} calls.
   *
   * <p>
   * Entries are ordered by {@code (claimAheadMove.halfMoveCount(), legal-move-iteration-order at that ply)}: the outer
   * order comes from the sorted ply groups returned by the look-ahead replay, the inner order from
   * {@code Board.getLegalMoves()} iteration at each ply.
   */
  static ThreefoldClaimAheadReport build(Board board) {
    final List<List<HalfMove>> rawClaimAheadListList = ThreefoldClaimAheadUtility.calculateClaimAheadListList(board);
    final ImmutableList<HalfMove> halfMoveListPlayed = board.getHalfMoveList();
    final DynamicPosition initialDynamicPosition = board.getInitialDynamicPosition();

    final List<ClaimAheadEntry> entries = new ArrayList<>();
    for (final List<HalfMove> rawClaimAheadList : rawClaimAheadListList) {
      for (final HalfMove claimAheadMove : rawClaimAheadList) {
        entries.add(buildEntry(claimAheadMove, halfMoveListPlayed, initialDynamicPosition));
      }
    }
    return new ThreefoldClaimAheadReport(ImmutableList.copyOf(entries));
  }

  private static ClaimAheadEntry buildEntry(HalfMove claimAheadMove, ImmutableList<HalfMove> halfMoveListPlayed,
      DynamicPosition initialDynamicPosition) {

    final boolean hasBeenPlayed = halfMoveListPlayed.contains(claimAheadMove);
    final boolean includesInitialPosition = initialDynamicPosition.equals(claimAheadMove.dynamicPosition());

    final List<HalfMove> priorOccurrences = new ArrayList<>();
    for (final HalfMove played : halfMoveListPlayed) {
      if (played.halfMoveCount() >= claimAheadMove.halfMoveCount()) {
        break;
      }
      if (played.dynamicPosition().equals(claimAheadMove.dynamicPosition())) {
        priorOccurrences.add(played);
      }
    }

    final int totalRepetitionCount = priorOccurrences.size() + 1 + (includesInitialPosition ? 1 : 0);
    return new ClaimAheadEntry(claimAheadMove, hasBeenPlayed, ImmutableList.copyOf(priorOccurrences),
        includesInitialPosition, totalRepetitionCount);
  }
}
