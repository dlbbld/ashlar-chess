package com.dlb.chess.report;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.dlb.chess.board.Board;
import com.dlb.chess.common.HalfMoveListListComparator;
import com.dlb.chess.common.Nulls;
import com.dlb.chess.common.model.DynamicPosition;
import com.dlb.chess.common.model.HalfMove;
import com.dlb.chess.fen.model.Fen;
import com.dlb.chess.model.LegalMove;
import com.google.common.collect.ImmutableList;

abstract class ThreefoldClaimAheadReportBuilder {

  /**
   * Builds the claim-ahead report by replaying the game on an internal fresh board. The {@code board} argument is
   * read-only — never receives {@code move} or {@code unmove} calls.
   *
   * <p>
   * Entries are ordered by {@code (claimAheadMove.halfMoveCount(), legal-move-iteration-order at that ply)}: the outer
   * order comes from {@link HalfMoveListListComparator} sorting the look-ahead groups by first-element half-move
   * count, the inner order from {@code Board.getLegalMoves()} iteration.
   */
  static ThreefoldClaimAheadReport build(Board board) {
    final List<List<HalfMove>> rawClaimAheadListList = replayAndCollectClaimAheads(board.getPerformedLegalMoveList(),
        board.getInitialFen());
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

  private static List<List<HalfMove>> replayAndCollectClaimAheads(List<LegalMove> performedLegalMoveList,
      Fen initialFen) {
    final List<List<HalfMove>> resultListList = new ArrayList<>();
    final Board replayBoard = new Board(initialFen);
    for (final LegalMove legalMove : performedLegalMoveList) {
      collectClaimAheadsAtCurrentPly(resultListList, replayBoard);
      replayBoard.move(legalMove.moveSpecification());
    }
    collectClaimAheadsAtCurrentPly(resultListList, replayBoard);
    Collections.sort(resultListList, HalfMoveListListComparator.COMPARATOR);
    return resultListList;
  }

  private static void collectClaimAheadsAtCurrentPly(List<List<HalfMove>> resultListList, Board replayBoard) {
    final List<HalfMove> claimAheadsAtThisPly = new ArrayList<>();
    for (final LegalMove legalMoveCheckAhead : replayBoard.getLegalMoves()) {
      replayBoard.move(legalMoveCheckAhead.moveSpecification());
      if (replayBoard.isThreefoldRepetition()) {
        claimAheadsAtThisPly.add(Nulls.getLast(replayBoard.getHalfMoveList()));
      }
      replayBoard.unmove();
    }
    if (!claimAheadsAtThisPly.isEmpty()) {
      resultListList.add(claimAheadsAtThisPly);
    }
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
