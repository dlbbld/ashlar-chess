package com.dlb.chess.report;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import com.dlb.chess.board.Board;
import com.dlb.chess.common.Nulls;
import com.dlb.chess.common.model.DynamicPosition;
import com.dlb.chess.common.model.HalfMove;

/**
 * Direct unit tests for the {@link RepetitionGroup} record. Covers the compact-constructor invariant
 * ({@code totalRepetitionCount} must equal {@code occurrences.size() + (includesInitialPosition ? 1 : 0)}) and the
 * exposed-list immutability contract.
 */
class TestRepetitionGroup {

  @SuppressWarnings("static-method")
  @Test
  void compactConstructorRejectsInconsistentTotal() {
    final HalfMove move = firstPlayedHalfMove();
    final DynamicPosition position = move.dynamicPosition();
    assertThrows(IllegalArgumentException.class,
        () -> new RepetitionGroup(position, Nulls.listOf(move, move, move), false, 99),
        "totalRepetitionCount disagreeing with occurrences.size() + (initial ? 1 : 0) must throw");
  }

  @SuppressWarnings("static-method")
  @Test
  void compactConstructorAcceptsConsistentTotalWithoutInitialPosition() {
    final HalfMove move = firstPlayedHalfMove();
    final DynamicPosition position = move.dynamicPosition();
    final RepetitionGroup group = new RepetitionGroup(position, Nulls.listOf(move, move, move), false, 3);
    assertEquals(3, group.totalRepetitionCount());
    assertEquals(3, group.occurrences().size());
    assertEquals(false, group.includesInitialPosition());
    assertEquals(position, group.repeatedPosition());
  }

  @SuppressWarnings("static-method")
  @Test
  void compactConstructorAcceptsConsistentTotalWithInitialPosition() {
    // Two played occurrences + 1 implicit initial-position occurrence = total 3 (the threefold).
    final HalfMove move = firstPlayedHalfMove();
    final DynamicPosition position = move.dynamicPosition();
    final RepetitionGroup group = new RepetitionGroup(position, Nulls.listOf(move, move), true, 3);
    assertEquals(3, group.totalRepetitionCount());
    assertEquals(2, group.occurrences().size());
    assertEquals(true, group.includesInitialPosition());
  }

  @SuppressWarnings("static-method")
  @Test
  void exposedOccurrencesIsUnmodifiable() {
    // The constructor parameter is already typed ImmutableList, so caller-side post-construction
    // mutation is impossible at the API level. The meaningful invariant is that the accessor
    // returns an unmodifiable list — calling add()/clear() throws.
    final HalfMove move = firstPlayedHalfMove();
    final DynamicPosition position = move.dynamicPosition();
    final RepetitionGroup group = new RepetitionGroup(position, Nulls.listOf(move, move, move), false, 3);
    assertEquals(3, group.occurrences().size());
    assertThrows(UnsupportedOperationException.class, () -> group.occurrences().add(move),
        "exposed occurrences must reject mutation");
    assertThrows(UnsupportedOperationException.class, () -> group.occurrences().clear(),
        "exposed occurrences must reject mutation");
  }

  private static HalfMove firstPlayedHalfMove() {
    final Board board = new Board();
    board.moveStrict("e4");
    return Nulls.get(board.getHalfMoveList(), 0);
  }
}
