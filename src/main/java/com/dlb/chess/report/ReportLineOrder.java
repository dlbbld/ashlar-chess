package com.dlb.chess.report;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.dlb.chess.common.model.HalfMove;

/**
 * Lexicographic ordering for printed report lines in the threefold sections. Each line corresponds to a sequence of
 * half-move counts (the displayed plies, optionally preceded by a virtual "[Initial position]" marker). The
 * comparator orders lines by that sequence in standard lex order, so:
 *
 * <ul>
 * <li>Lines whose displayed plies share an earlier-position prefix stay grouped together. For a single dynamic
 * position whose repetition crosses the threefold threshold, then continues to fourfold and fivefold, the
 * length-3 / length-4 / length-5 claim-ahead lines all share the same opening plies and therefore sit adjacent
 * — and in increasing-length order, because a shorter sequence is a prefix of the longer one.</li>
 * <li>Lines that include the initial position sort before lines that do not. The "[Initial position]" marker is
 * conceptually before any played ply; the sort key represents it as a virtual half-move count of {@code -1},
 * lower than any real ply.</li>
 * </ul>
 *
 * <p>
 * Used by {@link ThreefoldClaimAheadReportBuilder} (one entry per claim-ahead candidate move; displayed plies are
 * {@code priorOccurrences ++ claimAheadMove}) and by {@link ThreefoldExistingReportBuilder} (one group per
 * repeated position; displayed plies are {@code occurrences}).
 */
abstract class ReportLineOrder {

  static final Comparator<ClaimAheadEntry> CLAIM_AHEAD_COMPARATOR = (a, b) -> compareKeys(claimAheadSortKey(a),
      claimAheadSortKey(b));

  static final Comparator<RepetitionGroup> REPETITION_GROUP_COMPARATOR = (a, b) -> compareKeys(
      repetitionGroupSortKey(a), repetitionGroupSortKey(b));

  /**
   * Orders 50-move claim-ahead entries by (sequence-start-anchor, boundary half-move count). The sequence-start
   * anchor is {@code -1} when the start is {@link InitialFenStart} (sorts before any played ply) or the
   * {@code firstNonZeroingMove}'s half-move count when the start is {@link AfterResetStart}. This groups boundary
   * entries by the run they belong to and orders within a run chronologically.
   */
  static final Comparator<FiftyMoveClaimAheadEntry> FIFTY_MOVE_CLAIM_AHEAD_COMPARATOR = (a, b) -> {
    final int startCompare = Integer.compare(sequenceStartAnchor(a.sequenceStart()),
        sequenceStartAnchor(b.sequenceStart()));
    if (startCompare != 0) {
      return startCompare;
    }
    return Integer.compare(a.halfMoveCount(), b.halfMoveCount());
  };

  private static List<Integer> claimAheadSortKey(ClaimAheadEntry entry) {
    final List<Integer> key = new ArrayList<>();
    if (entry.includesInitialPosition()) {
      key.add(-1);
    }
    for (final HalfMove halfMove : entry.priorOccurrences()) {
      key.add(halfMove.halfMoveCount());
    }
    key.add(entry.claimAheadMove().halfMoveCount());
    return key;
  }

  private static List<Integer> repetitionGroupSortKey(RepetitionGroup group) {
    final List<Integer> key = new ArrayList<>();
    if (group.includesInitialPosition()) {
      key.add(-1);
    }
    for (final HalfMove halfMove : group.occurrences()) {
      key.add(halfMove.halfMoveCount());
    }
    return key;
  }

  private static int sequenceStartAnchor(SequenceStart start) {
    if (start instanceof InitialFenStart) {
      return -1;
    }
    final AfterResetStart afterResetStart = (AfterResetStart) start;
    return afterResetStart.firstNonZeroingMove().halfMoveCount();
  }

  private static int compareKeys(List<Integer> a, List<Integer> b) {
    final int common = Math.min(a.size(), b.size());
    for (var i = 0; i < common; i++) {
      final int cmp = Integer.compare(a.get(i), b.get(i));
      if (cmp != 0) {
        return cmp;
      }
    }
    // Lex order: when one sequence is a prefix of the other, the shorter one sorts first.
    return Integer.compare(a.size(), b.size());
  }
}
