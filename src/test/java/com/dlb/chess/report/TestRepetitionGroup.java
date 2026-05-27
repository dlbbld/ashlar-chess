package com.dlb.chess.report;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.dlb.chess.board.Board;
import com.dlb.chess.common.model.DynamicPosition;
import com.dlb.chess.common.model.HalfMove;
import com.google.common.collect.ImmutableList;

/**
 * Direct unit tests for the {@link RepetitionGroup} record. Covers the compact-constructor invariant
 * ({@code totalRepetitionCount} must equal {@code occurrences.size() + (includesInitialPosition ? 1 : 0)}) and the
 * defensive copy of {@code occurrences}.
 */
class TestRepetitionGroup {

  @SuppressWarnings("static-method")
  @Test
  void compactConstructorRejectsInconsistentTotal() {
    final HalfMove move = firstPlayedHalfMove();
    final DynamicPosition position = move.dynamicPosition();
    assertThrows(IllegalArgumentException.class,
        () -> new RepetitionGroup(position, ImmutableList.of(move, move, move), false, 99),
        "totalRepetitionCount disagreeing with occurrences.size() + (initial ? 1 : 0) must throw");
  }

  @SuppressWarnings("static-method")
  @Test
  void compactConstructorAcceptsConsistentTotalWithoutInitialPosition() {
    final HalfMove move = firstPlayedHalfMove();
    final DynamicPosition position = move.dynamicPosition();
    final RepetitionGroup group = new RepetitionGroup(position, ImmutableList.of(move, move, move), false, 3);
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
    final RepetitionGroup group = new RepetitionGroup(position, ImmutableList.of(move, move), true, 3);
    assertEquals(3, group.totalRepetitionCount());
    assertEquals(2, group.occurrences().size());
    assertEquals(true, group.includesInitialPosition());
  }

  @SuppressWarnings("static-method")
  @Test
  void occurrencesAreDefensivelyCopied() {
    final HalfMove move = firstPlayedHalfMove();
    final DynamicPosition position = move.dynamicPosition();
    final List<HalfMove> mutable = new ArrayList<>();
    mutable.add(move);
    mutable.add(move);
    mutable.add(move);
    final RepetitionGroup group = new RepetitionGroup(position, ImmutableList.copyOf(mutable), false, 3);
    mutable.add(move);  // post-construction mutation of the source

    assertEquals(3, group.occurrences().size(), "record list must reflect snapshot at construction time");
    assertTrue(group.occurrences() instanceof ImmutableList,
        "record list must be an ImmutableList so callers cannot mutate it");
  }

  private static HalfMove firstPlayedHalfMove() {
    final Board board = new Board();
    board.moveStrict("e4");
    return board.getHalfMoveList().get(0);
  }
}
