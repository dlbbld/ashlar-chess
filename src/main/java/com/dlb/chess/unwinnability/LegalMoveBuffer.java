package com.dlb.chess.unwinnability;

import java.util.AbstractList;

import org.eclipse.jdt.annotation.NonNull;

import com.dlb.chess.model.LegalMove;

/**
 * Per-ply reusable container for the legal-move generator's output, owned by {@link HelpmateSearchBoard}. Backed by a
 * {@code LegalMove[]} array that grows by doubling on overflow; {@link #reset()} resets the size counter so the array
 * slots are reused across moves. {@link HelpmateSearchBoard} keeps one buffer per search depth so the parent's buffer
 * survives child recursion untouched.
 *
 * <p>
 * Extends {@link AbstractList AbstractList&lt;LegalMove&gt;} directly - the buffer IS the read-only list view, so
 * {@code HelpmateSearchBoard.getLegalMoves()} hands out the buffer itself (no per-call view-object allocation). Use as
 * a {@code Consumer<LegalMove>} sink via the package-private {@link #append(LegalMove)} method-reference when calling
 * {@code BitboardLegalMoveFactory.calculateLegalMovesInto}. Iteration is in insertion order (the generator's natural
 * traversal order); deliberately not sorted - per the 12.1.0 move-order policy the search board's iteration order is an
 * internal performance choice. Read-only at callsites: the inherited {@link AbstractList} mutators ({@code add},
 * {@code clear}, {@code remove}, ...) throw {@link UnsupportedOperationException} by default; the package-private
 * {@link #append(LegalMove)} and {@link #reset()} below are the only mutators and are only callable from inside this
 * package. They are deliberately named differently from the {@link java.util.List} mutators ({@code add} /
 * {@code clear}) so the inherited unsupported-by-default contract is preserved unchanged.
 */
final class LegalMoveBuffer extends AbstractList<LegalMove> {

  /** Initial backing-array capacity. Doubles on overflow. */
  private static final int INITIAL_CAPACITY = 64;

  private LegalMove[] moves = new LegalMove[INITIAL_CAPACITY];
  private int size;

  /** Reset size to zero; backing array preserved for reuse. Call before the generator fills the buffer. */
  void reset() {
    size = 0;
  }

  /**
   * Appends {@code move} to the buffer. Grows the backing array by doubling if full. Intended as
   * {@code Consumer<LegalMove>} method reference passed to {@code calculateLegalMovesInto}.
   */
  void append(LegalMove move) {
    if (size == moves.length) {
      final var grown = new LegalMove[moves.length * 2];
      System.arraycopy(moves, 0, grown, 0, moves.length);
      moves = grown;
    }
    moves[size] = move;
    size++;
  }

  @Override
  public @NonNull LegalMove get(int index) {
    if (index < 0 || index >= size) {
      throw new IndexOutOfBoundsException(index);
    }
    final var move = moves[index];
    if (move == null) {
      throw new IllegalStateException("Buffer slot " + index + " is null");
    }
    return move;
  }

  @Override
  public int size() {
    return size;
  }
}
