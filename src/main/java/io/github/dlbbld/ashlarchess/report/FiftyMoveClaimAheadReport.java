package io.github.dlbbld.ashlarchess.report;

import com.google.common.collect.ImmutableList;

import io.github.dlbbld.ashlarchess.common.Nulls;

/**
 * Missed 50-move claim-ahead opportunities discovered during the played history's replay. Each entry represents one
 * clock-99 boundary where the player had at least one non-zeroing legal move available but the actually-played move
 * broke the sequence (a pawn move or capture, or the game ended at the boundary). Sequences that did reach the 50-
 * move-rule threshold in actual play do not produce entries here - they are surfaced by {@link FiftyMoveSequenceReport}
 * alone.
 *
 * <p>
 * One-entry-per-boundary collapse: multiple non-zeroing legal alternatives at the same boundary ply produce a single
 * entry, not one per alternative. The print layer renders the candidate position as a {@code [ahead claim possible]}
 * placeholder.
 *
 * <p>
 * Entries are ordered by {@code (sequenceStart-anchor, halfMoveCount)} via
 * {@link ReportLineOrder#FIFTY_MOVE_CLAIM_AHEAD_COMPARATOR}.
 */
record FiftyMoveClaimAheadReport(ImmutableList<FiftyMoveClaimAheadEntry> entries) {

  public FiftyMoveClaimAheadReport {
    entries = Nulls.copyOfList(entries);
  }
}
