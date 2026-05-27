package com.dlb.chess.report;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.dlb.chess.board.Board;
import com.dlb.chess.common.model.DynamicPosition;
import com.dlb.chess.common.model.HalfMove;
import com.dlb.chess.common.model.MoveSpecification;
import com.dlb.chess.fen.model.Fen;
import com.dlb.chess.model.LegalMove;
import com.google.common.collect.ImmutableList;

abstract class ThreefoldClaimAheadReportBuilder {

  /**
   * Builds the claim-ahead report by replaying the game on an internal fresh board. The {@code board} argument is
   * read-only — never receives {@code move} or {@code unmove} calls.
   *
   * <p>
   * Entries are ordered by {@link ReportLineOrder#CLAIM_AHEAD_COMPARATOR}: lexicographic on the displayed half-move-
   * count sequence ({@code priorOccurrences ++ claimAheadMove}, prefixed by a virtual {@code -1} when
   * {@code includesInitialPosition} is true). Sequences that share earlier plies stay adjacent and progress
   * length-3 → length-4 → length-5 (the shorter is a prefix of the longer in standard lex order). When the played
   * history reaches the same dynamic position multiple times, the earlier claim-ahead boundary surfaces first.
   */
  static ThreefoldClaimAheadReport build(Board board) {
    final List<HalfMove> rawClaimAheads = replayAndCollectClaimAheads(board.getPerformedLegalMoveList(),
        board.getInitialFen());
    final ImmutableList<HalfMove> halfMoveListPlayed = board.getHalfMoveList();
    final DynamicPosition initialDynamicPosition = board.getInitialDynamicPosition();

    final List<ClaimAheadEntry> entries = new ArrayList<>();
    for (final HalfMove claimAheadMove : rawClaimAheads) {
      entries.add(buildEntry(claimAheadMove, halfMoveListPlayed, initialDynamicPosition));
    }
    Collections.sort(entries, ReportLineOrder.CLAIM_AHEAD_COMPARATOR);
    return new ThreefoldClaimAheadReport(ImmutableList.copyOf(entries));
  }

  private static List<HalfMove> replayAndCollectClaimAheads(List<LegalMove> performedLegalMoveList, Fen initialFen) {
    final List<HalfMove> result = new ArrayList<>();
    final Board replayBoard = new Board(initialFen);
    for (final LegalMove legalMove : performedLegalMoveList) {
      collectClaimAheadsAtCurrentPly(result, replayBoard);
      replayBoard.move(legalMove.moveSpecification());
    }
    collectClaimAheadsAtCurrentPly(result, replayBoard);
    return result;
  }

  private static void collectClaimAheadsAtCurrentPly(List<HalfMove> result, Board replayBoard) {
    for (final LegalMove legalMoveCheckAhead : replayBoard.getLegalMoves()) {
      final MoveSpecification move = legalMoveCheckAhead.moveSpecification();
      // Single source of truth: the report's per-move claim-ahead entries are exactly the moves
      // for which Board.canClaimThreefoldRepetitionRuleFor returns true. Any future change to that
      // predicate (e.g. tighter FIDE 9.2 semantics) is automatically reflected here without a
      // parallel update.
      if (replayBoard.canClaimThreefoldRepetitionRuleFor(move)) {
        // The predicate did a transient push+unmove internally; re-push here to capture the
        // produced HalfMove that the entry needs to carry. The duplicated push is intentional
        // overhead — it keeps the predicate as the contract and the builder as a consumer of it.
        replayBoard.move(move);
        result.add(replayBoard.getLastHalfMove());
        replayBoard.unmove();
      }
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
