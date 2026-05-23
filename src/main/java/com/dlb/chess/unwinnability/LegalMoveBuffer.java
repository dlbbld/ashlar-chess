package com.dlb.chess.unwinnability;

import java.util.AbstractList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import com.dlb.chess.model.LegalMove;

/**
 * Per-ply reusable container for the legal-move generator's output, owned by {@link HelpmateSearchBoard}. Backed by
 * an {@code LegalMove[]} array that grows by doubling on overflow; {@link #clear()} resets the size counter so the
 * array slots are reused across moves. {@link HelpmateSearchBoard} keeps one buffer per search depth so the parent's
 * buffer survives child recursion untouched.
 *
 * <p>
 * Implements the search-side {@code Consumer<LegalMove>} sink via {@link #add(LegalMove)} (used as a method reference
 * when calling {@code BitboardLegalMoveFactory.calculateLegalMovesInto}). Iteration is in insertion order (the
 * generator's natural traversal order); deliberately not sorted — per the 12.1.0 move-order policy the search board's
 * iteration order is an internal performance choice. Callers treat the buffer as read-only.
 */
final class LegalMoveBuffer implements Iterable<LegalMove> {

  /** Initial backing-array capacity. Doubles on overflow. */
  private static final int INITIAL_CAPACITY = 64;

  private LegalMove[] moves = new LegalMove[INITIAL_CAPACITY];
  private int size;

  /** Reset size to zero; backing array preserved for reuse. Call before the generator fills the buffer. */
  void clear() {
    size = 0;
  }

  /**
   * Appends {@code move} to the buffer. Grows the backing array by doubling if full. Intended as
   * {@code Consumer<LegalMove>} method reference passed to {@code calculateLegalMovesInto}.
   */
  void add(LegalMove move) {
    if (size == moves.length) {
      final var grown = new LegalMove[moves.length * 2];
      System.arraycopy(moves, 0, grown, 0, moves.length);
      moves = grown;
    }
    moves[size] = move;
    size++;
  }

  int size() {
    return size;
  }

  boolean isEmpty() {
    return size == 0;
  }

  /**
   * Read-only {@link List} view over the buffer's current contents. The view is backed by the buffer's array — if
   * the buffer is mutated (cleared, refilled, or grown), previously-returned views become stale. Per-depth buffer
   * ownership in {@link HelpmateSearchBoard} ensures the caller's view at depth N is not mutated by recursion into
   * depth N+1.
   */
  List<LegalMove> asList() {
    return new AbstractList<>() {

      @Override
      public LegalMove get(int index) {
        if (index < 0 || index >= size) {
          throw new IndexOutOfBoundsException(index);
        }
        final LegalMove move = moves[index];
        if (move == null) {
          throw new IllegalStateException("Buffer slot " + index + " is null");
        }
        return move;
      }

      @Override
      public int size() {
        return size;
      }
    };
  }

  @Override
  public Iterator<LegalMove> iterator() {
    return new Iterator<>() {
      private int cursor;

      @Override
      public boolean hasNext() {
        return cursor < size;
      }

      @Override
      public LegalMove next() {
        if (cursor >= size) {
          throw new NoSuchElementException();
        }
        final LegalMove move = moves[cursor];
        if (move == null) {
          throw new IllegalStateException("Buffer slot " + cursor + " is null");
        }
        cursor++;
        return move;
      }
    };
  }
}
