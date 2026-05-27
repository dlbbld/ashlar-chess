package com.dlb.chess.report;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.jdt.annotation.Nullable;

import com.dlb.chess.board.Board;
import com.dlb.chess.common.Nulls;
import com.dlb.chess.common.model.HalfMove;
import com.dlb.chess.common.model.MoveSpecification;
import com.dlb.chess.common.utility.BasicChessUtility;
import com.dlb.chess.model.LegalMove;
import com.google.common.collect.ImmutableList;

abstract class FiftyMoveClaimAheadReportBuilder {

  /**
   * Builds the 50-move claim-ahead report under the missed-opportunity filter: only those candidate moves are emitted
   * where the sequence containing the parent ply did not, in the actual played history, reach the 50-move-rule
   * threshold (halfmove clock 100). Sequences that did reach the threshold are surfaced by
   * {@link FiftyMoveSequenceReportBuilder} alone; their claim-ahead candidates would be informationally redundant here.
   *
   * <p>
   * Concretely, at any replay-ply where the position's halfmove clock equals 99, the builder asks: was the actually-
   * played move at this ply a clock-resetting move (pawn move or capture), or did the played history end here? If yes
   * — the sequence is about to break (or has ended) without ever reaching clock 100 — every non-zeroing legal move at
   * this ply is a missed claim-ahead, and one entry is emitted per such move. If no — the actually-played move was
   * non-zeroing, so clock will advance to 100 and the sequence reaches threshold — no entries are emitted at this ply.
   *
   * <p>
   * The {@link com.dlb.chess.board.Board#canClaimFiftyMoveRuleFor} per-move predicate remains the single source of
   * truth for whether a candidate move qualifies; the missed-opportunity filter is an orthogonal restriction layered on
   * top of it.
   */
  static FiftyMoveClaimAheadReport build(Board board) {
    final List<FiftyMoveClaimAheadEntry> entries = new ArrayList<>();
    final int initialFenClock = board.getInitialFen().halfMoveClock();

    final Board replayBoard = new Board(board.getInitialFen());
    @Nullable SequenceStart currentStart = initialFenClock > 0 ? new InitialFenStart(initialFenClock) : null;

    final List<LegalMove> performedLegalMoveList = board.getPerformedLegalMoveList();
    for (var i = 0; i < performedLegalMoveList.size(); i++) {
      final LegalMove nextPlayedMove = performedLegalMoveList.get(i);
      final boolean nextPlayedMoveBreaksSequence = BasicChessUtility.calculateIsResetHalfMoveClock(nextPlayedMove);
      if (nextPlayedMoveBreaksSequence) {
        collectMissedClaimAheads(entries, replayBoard, currentStart);
      }
      replayBoard.move(nextPlayedMove.moveSpecification());
      currentStart = updatedSequenceStart(currentStart, Nulls.getLast(replayBoard.getHalfMoveList()));
    }
    // Played history exhausted; the open sequence (if any) ends here without a further played move.
    // If its clock is 99, the boundary ply is a missed opportunity by the same filter logic.
    collectMissedClaimAheads(entries, replayBoard, currentStart);

    Collections.sort(entries, ReportLineOrder.FIFTY_MOVE_CLAIM_AHEAD_COMPARATOR);
    return new FiftyMoveClaimAheadReport(ImmutableList.copyOf(entries));
  }

  /**
   * If the replay board sits at exactly the clock-99 boundary and the sequence at this ply is destined not to reach
   * 100 (the caller guarantees this by only invoking immediately before a clock-resetting played move or at end-of-
   * history), enumerates the non-zeroing legal moves and emits one {@link FiftyMoveClaimAheadEntry} per. A {@code null}
   * {@code currentStart} indicates no active sequence (clock cannot be 99) and short-circuits.
   */
  private static void collectMissedClaimAheads(List<FiftyMoveClaimAheadEntry> entries, Board replayBoard,
      @Nullable SequenceStart currentStart) {
    if (currentStart == null) {
      return;
    }
    if (replayBoard.getHalfMoveClock() != 99) {
      return;
    }
    for (final LegalMove legalMove : replayBoard.getLegalMoves()) {
      final MoveSpecification move = legalMove.moveSpecification();
      if (!replayBoard.canClaimFiftyMoveRuleFor(move)) {
        continue;
      }
      // Capture the resulting HalfMove via a transient push; same shape as the threefold claim-ahead
      // builder. The predicate itself does no push (50-move is a clock check, no resulting position
      // needed), so this push is the only one.
      replayBoard.move(move);
      final HalfMove claimAheadMove = Nulls.getLast(replayBoard.getHalfMoveList());
      replayBoard.unmove();
      entries.add(new FiftyMoveClaimAheadEntry(currentStart, claimAheadMove));
    }
  }

  private static @Nullable SequenceStart updatedSequenceStart(@Nullable SequenceStart currentStart,
      HalfMove playedHalfMove) {
    if (playedHalfMove.halfMoveClock() == 0) {
      return null;
    }
    if (currentStart != null) {
      return currentStart;
    }
    return new AfterResetStart(playedHalfMove);
  }
}
