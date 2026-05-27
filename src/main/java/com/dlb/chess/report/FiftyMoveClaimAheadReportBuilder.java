package com.dlb.chess.report;

import java.util.ArrayList;
import java.util.List;

import com.dlb.chess.board.Board;
import com.dlb.chess.common.Nulls;
import com.dlb.chess.common.model.HalfMove;
import com.dlb.chess.common.model.MoveSpecification;
import com.dlb.chess.model.LegalMove;
import com.google.common.collect.ImmutableList;

abstract class FiftyMoveClaimAheadReportBuilder {

  /**
   * Builds the 50-move claim-ahead report by replaying the game on an internal fresh board. The {@code board}
   * argument is read-only — never receives {@code move} or {@code unmove} calls.
   *
   * <p>
   * Single source of truth: each candidate move is admitted to the report iff
   * {@code Board.canClaimFiftyMoveRuleFor(move)} returns true on the replay-board state at that ply. Any future
   * change to the per-move predicate (tightening of FIDE 9.3 semantics, additional rejection cases) is automatically
   * reflected in the report.
   */
  static FiftyMoveClaimAheadReport build(Board board) {
    final List<FiftyMoveClaimAheadEntry> entries = new ArrayList<>();
    final ImmutableList<HalfMove> halfMoveListPlayed = board.getHalfMoveList();

    final Board replayBoard = new Board(board.getInitialFen());
    final List<LegalMove> performedLegalMoveList = board.getPerformedLegalMoveList();
    for (final LegalMove playedMove : performedLegalMoveList) {
      collectClaimAheadsAtCurrentPly(entries, replayBoard, halfMoveListPlayed);
      replayBoard.move(playedMove.moveSpecification());
    }
    collectClaimAheadsAtCurrentPly(entries, replayBoard, halfMoveListPlayed);

    return new FiftyMoveClaimAheadReport(ImmutableList.copyOf(entries));
  }

  /**
   * For each legal move at the replay-board's current ply, if the per-move 50-move predicate accepts it, record an
   * entry. The entry's {@code hasBeenPlayed} flag is true when the same HalfMove appears in the played history.
   */
  private static void collectClaimAheadsAtCurrentPly(List<FiftyMoveClaimAheadEntry> entries, Board replayBoard,
      ImmutableList<HalfMove> halfMoveListPlayed) {
    for (final LegalMove legalMove : replayBoard.getLegalMoves()) {
      final MoveSpecification move = legalMove.moveSpecification();
      if (replayBoard.canClaimFiftyMoveRuleFor(move)) {
        // Capture the resulting HalfMove via a transient push; the predicate above does no push of
        // its own (50-move is a clock check, no resulting position needed), so this push is the
        // only one. Symmetric in shape with the threefold claim-ahead builder.
        replayBoard.move(move);
        final HalfMove claimAheadMove = Nulls.getLast(replayBoard.getHalfMoveList());
        replayBoard.unmove();
        final boolean hasBeenPlayed = halfMoveListPlayed.contains(claimAheadMove);
        entries.add(new FiftyMoveClaimAheadEntry(claimAheadMove, hasBeenPlayed));
      }
    }
  }

}
