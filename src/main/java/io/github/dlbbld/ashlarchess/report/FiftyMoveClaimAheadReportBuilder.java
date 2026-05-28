package io.github.dlbbld.ashlarchess.report;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.jdt.annotation.Nullable;

import io.github.dlbbld.ashlarchess.board.Board;
import io.github.dlbbld.ashlarchess.common.Nulls;
import io.github.dlbbld.ashlarchess.common.model.HalfMove;
import io.github.dlbbld.ashlarchess.common.model.MoveSpecification;
import io.github.dlbbld.ashlarchess.common.utility.BasicChessUtility;
import io.github.dlbbld.ashlarchess.model.LegalMove;

abstract class FiftyMoveClaimAheadReportBuilder {

  /**
   * Builds the 50-move claim-ahead report under the missed-opportunity filter: only those boundary plies are emitted
   * where the no-progress sequence containing the boundary did not, in the actual played history, reach the 50-move-
   * rule threshold (halfmove clock 100). Sequences that did reach the threshold are surfaced by
   * {@link FiftyMoveSequenceReportBuilder} alone; their would-be claim-aheads are informationally redundant here.
   *
   * <p>
   * Concretely, at any replay-ply where the position's halfmove clock equals 99, the builder asks: was the actually-
   * played move at this ply a clock-resetting move (pawn move or capture), or did the played history end here? If yes -
   * the sequence is about to break (or has ended) without ever reaching clock 100 - and at least one non-zeroing legal
   * move exists at this ply, ONE entry is emitted (regardless of how many non-zeroing alternatives were available). If
   * no - the actually-played move was non-zeroing, so clock will advance to 100 and the sequence reaches threshold - no
   * entry is emitted at this ply.
   *
   * <p>
   * One-entry-per-boundary collapse: at a clock-99 boundary with N >= 1 non-zeroing legal alternatives, listing all N
   * would be 30+ rows for one ply with no informational gain over a single row stating the opportunity existed. The
   * single entry carries only the boundary's chronological position; the print layer renders a {@code [ahead claim
   * possible]} placeholder where a SAN would normally go.
   *
   * <p>
   * The {@link io.github.dlbbld.ashlarchess.board.Board#canClaimFiftyMoveRuleFor} per-move predicate remains the single
   * source of truth for whether a candidate move qualifies as a 50-move claim.
   */
  static FiftyMoveClaimAheadReport build(Board board) {
    final List<FiftyMoveClaimAheadEntry> entries = new ArrayList<>();
    final int initialFenClock = board.getInitialFen().halfMoveClock();

    final Board replayBoard = new Board(board.getInitialFen());
    SequenceStart currentStart = initialSequenceStart(initialFenClock);

    final List<LegalMove> performedLegalMoveList = board.getPerformedLegalMoveList();
    for (final LegalMove nextPlayedMove : performedLegalMoveList) {
      final boolean nextPlayedMoveBreaksSequence = BasicChessUtility.calculateIsResetHalfMoveClock(nextPlayedMove);
      if (nextPlayedMoveBreaksSequence) {
        emitBoundaryIfMissedOpportunity(entries, replayBoard, currentStart);
      }
      replayBoard.move(nextPlayedMove.moveSpecification());
      currentStart = updatedSequenceStart(currentStart, replayBoard.getLastHalfMove());
    }
    // Played history exhausted; the open sequence (if any) ends here without a further played move.
    // If its clock is 99, the boundary ply is a missed opportunity.
    emitBoundaryIfMissedOpportunity(entries, replayBoard, currentStart);

    Collections.sort(entries, ReportLineOrder.FIFTY_MOVE_CLAIM_AHEAD_COMPARATOR);
    return new FiftyMoveClaimAheadReport(Nulls.copyOfList(entries));
  }

  private static @Nullable SequenceStart initialSequenceStart(int initialFenClock) {
    return initialFenClock > 0 ? SequenceStart.initialFen(initialFenClock) : null;
  }

  /**
   * If the replay board sits at exactly the clock-99 boundary and at least one non-zeroing legal move exists (predicate
   * accepts it), emits a single {@link FiftyMoveClaimAheadEntry} representing the boundary opportunity. Returns
   * silently when no active sequence is open ({@code currentStart == null}) or the clock is not 99 or no non-zeroing
   * legal alternative exists.
   */
  private static void emitBoundaryIfMissedOpportunity(List<FiftyMoveClaimAheadEntry> entries, Board replayBoard,
      @Nullable SequenceStart currentStart) {
    if (currentStart == null) {
      return;
    }
    if (replayBoard.getHalfMoveClock() != 99) {
      return;
    }
    if (!hasAnyNonZeroingClaimCandidate(replayBoard)) {
      return;
    }
    // Boundary metadata: the upcoming ply's chronological position. The candidate move itself is
    // not stored - the entry represents the boundary, not any single alternative move.
    final int boundaryHalfMoveCount = replayBoard.getPerformedHalfMoveCount() + 1;
    final int boundaryFullMoveNumber = replayBoard.getFullMoveNumber();
    entries.add(new FiftyMoveClaimAheadEntry(currentStart, boundaryHalfMoveCount, boundaryFullMoveNumber,
        replayBoard.getHavingMove()));
  }

  private static boolean hasAnyNonZeroingClaimCandidate(Board replayBoard) {
    for (final LegalMove legalMove : replayBoard.getLegalMoves()) {
      final MoveSpecification move = legalMove.moveSpecification();
      if (replayBoard.canClaimFiftyMoveRuleFor(move)) {
        return true;
      }
    }
    return false;
  }

  private static @Nullable SequenceStart updatedSequenceStart(@Nullable SequenceStart currentStart,
      HalfMove playedHalfMove) {
    if (playedHalfMove.halfMoveClock() == 0) {
      return null;
    }
    if (currentStart != null) {
      return currentStart;
    }
    return SequenceStart.afterReset(playedHalfMove);
  }
}
