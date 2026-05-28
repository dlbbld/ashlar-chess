package io.github.dlbbld.ashlarchess.report;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import io.github.dlbbld.ashlarchess.board.Board;
import io.github.dlbbld.ashlarchess.common.Nulls;
import io.github.dlbbld.ashlarchess.common.model.HalfMove;
import io.github.dlbbld.ashlarchess.report.ClaimAheadEntry;

/**
 * Direct unit tests for the {@link ClaimAheadEntry} record. Covers the compact-constructor invariant
 * ({@code totalRepetitionCount} must equal {@code priorOccurrences.size() + 1 + (includesInitialPosition ? 1 : 0)}) and
 * the exposed-list immutability contract.
 */
class TestClaimAheadEntry {

  /**
   * Boundary case: a claim-ahead that is itself the initial-position third occurrence - no prior played occurrences
   * yet, includes initial = true, total = 0 + 1 + 1 = 2... no wait, that's the 2nd occurrence count. Total of 3 means
   * one prior occurrence on the board plus the initial position plus the claim-ahead. The invariant doesn't care about
   * chess validity, only about the field consistency.
   */
  @SuppressWarnings("static-method")
  @Test
  void compactConstructorRejectsInconsistentTotal() {
    final HalfMove move = firstPlayedHalfMove();
    assertThrows(IllegalArgumentException.class, () -> new ClaimAheadEntry(move, false, Nulls.listOf(), false, 99),
        "totalRepetitionCount disagreeing with priorOccurrences.size() + 1 must throw");
  }

  @SuppressWarnings("static-method")
  @Test
  void compactConstructorAcceptsConsistentTotalWithoutInitialPosition() {
    final HalfMove move = firstPlayedHalfMove();
    final ClaimAheadEntry entry = new ClaimAheadEntry(move, false, Nulls.listOf(), false, 1);
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
    final ClaimAheadEntry entry = new ClaimAheadEntry(move, false, Nulls.listOf(), true, 2);
    assertEquals(2, entry.totalRepetitionCount());
    assertEquals(true, entry.includesInitialPosition());
  }

  /** Returns the HalfMove for white's 1.e4 from the initial position. Convenient cheap fixture. */
  private static HalfMove firstPlayedHalfMove() {
    final Board board = new Board();
    board.moveStrict("e4");
    return Nulls.get(board.getHalfMoveList(), 0);
  }
}
