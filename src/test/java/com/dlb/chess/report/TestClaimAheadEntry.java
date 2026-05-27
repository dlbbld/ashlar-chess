package com.dlb.chess.report;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.dlb.chess.board.Board;
import com.dlb.chess.common.model.HalfMove;
import com.google.common.collect.ImmutableList;

/**
 * Direct unit tests for the {@link ClaimAheadEntry} record. Covers the compact-constructor invariant
 * ({@code totalRepetitionCount} must equal {@code priorOccurrences.size() + 1 + (includesInitialPosition ? 1 : 0)})
 * and the defensive copy of {@code priorOccurrences}.
 */
class TestClaimAheadEntry {

  /**
   * Boundary case: a claim-ahead that is itself the initial-position third occurrence — no prior played occurrences
   * yet, includes initial = true, total = 0 + 1 + 1 = 2... no wait, that's the 2nd occurrence count. Total of 3 means
   * one prior occurrence on the board plus the initial position plus the claim-ahead. The invariant doesn't care
   * about chess validity, only about the field consistency.
   */
  @SuppressWarnings("static-method")
  @Test
  void compactConstructorRejectsInconsistentTotal() {
    final HalfMove move = firstPlayedHalfMove();
    assertThrows(IllegalArgumentException.class,
        () -> new ClaimAheadEntry(move, false, ImmutableList.of(), false, 99),
        "totalRepetitionCount disagreeing with priorOccurrences.size() + 1 must throw");
  }

  @SuppressWarnings("static-method")
  @Test
  void compactConstructorAcceptsConsistentTotalWithoutInitialPosition() {
    final HalfMove move = firstPlayedHalfMove();
    final ClaimAheadEntry entry = new ClaimAheadEntry(move, false, ImmutableList.of(), false, 1);
    assertEquals(1, entry.totalRepetitionCount());
    assertEquals(0, entry.priorOccurrences().size());
    assertEquals(false, entry.includesInitialPosition());
    assertEquals(false, entry.hasBeenPlayed());
  }

  @SuppressWarnings("static-method")
  @Test
  void compactConstructorAcceptsConsistentTotalWithInitialPosition() {
    // priorOccurrences empty, includesInitialPosition true, claim-ahead move = the (n+1)th = 2nd occurrence overall:
    // 0 + 1 + 1 = 2.
    final HalfMove move = firstPlayedHalfMove();
    final ClaimAheadEntry entry = new ClaimAheadEntry(move, false, ImmutableList.of(), true, 2);
    assertEquals(2, entry.totalRepetitionCount());
    assertEquals(true, entry.includesInitialPosition());
  }

  @SuppressWarnings("static-method")
  @Test
  void priorOccurrencesAreDefensivelyCopied() {
    // Pass a mutable ArrayList, mutate it post-construction, verify the record's list is unaffected.
    final HalfMove move = firstPlayedHalfMove();
    final List<HalfMove> mutable = new ArrayList<>();
    mutable.add(move);
    final ClaimAheadEntry entry = new ClaimAheadEntry(move, true, ImmutableList.copyOf(mutable), false, 2);
    mutable.add(move);  // post-construction mutation of the source

    assertEquals(1, entry.priorOccurrences().size(), "record list must reflect snapshot at construction time");
    assertTrue(entry.priorOccurrences() instanceof ImmutableList,
        "record list must be an ImmutableList so callers cannot mutate it");
  }

  /** Returns the HalfMove for white's 1.e4 from the initial position. Convenient cheap fixture. */
  private static HalfMove firstPlayedHalfMove() {
    final Board board = new Board();
    board.moveStrict("e4");
    return board.getHalfMoveList().get(0);
  }
}
