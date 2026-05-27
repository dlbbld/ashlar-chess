package com.dlb.chess.report;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.jdt.annotation.Nullable;

import com.dlb.chess.board.Board;
import com.dlb.chess.common.Nulls;
import com.dlb.chess.common.model.HalfMove;
import com.dlb.chess.common.model.MoveSpecification;
import com.dlb.chess.model.LegalMove;
import com.google.common.collect.ImmutableList;

abstract class FiftyMoveClaimAheadReportBuilder {

  /**
   * Builds the 50-move claim-ahead report by replaying the game on an internal fresh board. The {@code board} argument
   * is read-only — never receives {@code move} or {@code unmove} calls.
   *
   * <p>
   * Single source of truth: each candidate move is admitted to the report iff
   * {@code Board.canClaimFiftyMoveRuleFor(move)} returns true on the replay board at that ply. Any future change to the
   * per-move predicate (tightening of FIDE 9.3 semantics, additional rejection cases) is automatically reflected in the
   * report.
   *
   * <p>
   * Each entry also carries the {@link SequenceStart} of the no-progress run the parent position belongs to, so the
   * print layer can render the line as {@code <sequence-start> - <claim-ahead-move> (<resulting-clock>[*])}. The
   * sequence-start tracker mirrors {@link FiftyMoveSequenceReportBuilder}: opened on first non-zeroing ply, closed on
   * any clock-resetting ply, initialised from the starting FEN's halfmove clock.
   */
  static FiftyMoveClaimAheadReport build(Board board) {
    final List<FiftyMoveClaimAheadEntry> entries = new ArrayList<>();
    final ImmutableList<HalfMove> halfMoveListPlayed = board.getHalfMoveList();
    final int initialFenClock = board.getInitialFen().halfMoveClock();

    final Board replayBoard = new Board(board.getInitialFen());
    @Nullable SequenceStart currentStart = initialFenClock > 0 ? new InitialFenStart(initialFenClock) : null;

    final List<LegalMove> performedLegalMoveList = board.getPerformedLegalMoveList();
    for (final LegalMove playedMove : performedLegalMoveList) {
      collectClaimAheadsAtCurrentPly(entries, replayBoard, currentStart, halfMoveListPlayed);
      replayBoard.move(playedMove.moveSpecification());
      currentStart = updatedSequenceStart(currentStart, Nulls.getLast(replayBoard.getHalfMoveList()));
    }
    collectClaimAheadsAtCurrentPly(entries, replayBoard, currentStart, halfMoveListPlayed);

    Collections.sort(entries, ReportLineOrder.FIFTY_MOVE_CLAIM_AHEAD_COMPARATOR);
    return new FiftyMoveClaimAheadReport(ImmutableList.copyOf(entries));
  }

  private static void collectClaimAheadsAtCurrentPly(List<FiftyMoveClaimAheadEntry> entries, Board replayBoard,
      @Nullable SequenceStart currentStart, ImmutableList<HalfMove> halfMoveListPlayed) {
    if (currentStart == null) {
      // No active sequence at this ply (game just started with a clock-0 FEN, or we just reset).
      // Either way the predicate cannot fire — by construction the clock is below 99 here. Skip.
      return;
    }
    for (final LegalMove legalMove : replayBoard.getLegalMoves()) {
      final MoveSpecification move = legalMove.moveSpecification();
      if (replayBoard.canClaimFiftyMoveRuleFor(move)) {
        // Capture the resulting HalfMove via a transient push; symmetric in shape with the
        // threefold claim-ahead builder.
        replayBoard.move(move);
        final HalfMove claimAheadMove = Nulls.getLast(replayBoard.getHalfMoveList());
        replayBoard.unmove();
        final boolean hasBeenPlayed = halfMoveListPlayed.contains(claimAheadMove);
        entries.add(new FiftyMoveClaimAheadEntry(currentStart, claimAheadMove, hasBeenPlayed));
      }
    }
  }

  private static @Nullable SequenceStart updatedSequenceStart(@Nullable SequenceStart currentStart,
      HalfMove playedHalfMove) {
    if (playedHalfMove.halfMoveClock() == 0) {
      // The played move zeroed the clock — sequence ends, no new one until the next non-zeroing ply.
      return null;
    }
    if (currentStart != null) {
      // Sequence continues unchanged.
      return currentStart;
    }
    // First non-zeroing ply after a reset (or after a clock-0 FEN start).
    return new AfterResetStart(playedHalfMove);
  }
}
