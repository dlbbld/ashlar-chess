package com.dlb.chess.unwinnability;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.util.List;

import org.eclipse.jdt.annotation.Nullable;
import org.junit.jupiter.api.Test;

import com.dlb.chess.board.Board;
import com.dlb.chess.common.Nulls;
import com.dlb.chess.common.model.DynamicPosition;
import com.dlb.chess.model.LegalMove;
import com.google.common.collect.ImmutableList;

/**
 * Differential test for {@link HelpmateSearchKey}: its equality semantics must match {@link DynamicPosition}'s exactly.
 * Phase D.1 of the 12.1.0 helpmate hot-path release.
 *
 * <p>
 * Two prongs:
 * <ol>
 * <li><strong>Lock-step parity with DynamicPosition</strong> across a recursive walk: at every node of every scenario
 * in {@link TestHelpmateSearchBoard}'s fixture set, build two independent {@link HelpmateSearchBoard}s from the same
 * {@code Board} state, walk the same move, and assert {@code key1.equals(key2)} iff {@code dp1.equals(dp2)}. The two
 * prongs (key equality, DynamicPosition equality) must agree at every node.
 * <li><strong>Positive controls</strong> for each distinguishing field: hand-constructed pairs that differ on exactly
 * one of {sideToMove, normalized EP, white castling rights, black castling rights, a piece bitboard} and assert the
 * keys are unequal. Catches accidental field omission in the equality / hashCode contract.
 * </ol>
 */
class TestHelpmateSearchKey {

  /**
   * Lock-step walker over {@link TestHelpmateSearchBoard}'s scenario fixtures. Tighter than the parity test in
   * {@link TestHelpmateSearchBoard} which compares search-board vs Board: here we compare two independent search boards
   * built from the same source, walking the same move sequence, so any deviation between key equality and
   * {@link DynamicPosition} equality is surfaced.
   */
  @SuppressWarnings("static-method")
  @Test
  void keyEqualityMirrorsDynamicPositionAcrossRecursiveWalk() {
    for (final Scenario scenario : SCENARIOS) {
      try {
        final var board1 = boardFrom(scenario.fen());
        final var board2 = boardFrom(scenario.fen());
        final HelpmateSearchBoard search1 = HelpmateSearchBoard.from(board1);
        final HelpmateSearchBoard search2 = HelpmateSearchBoard.from(board2);
        assertParityAtRoot(search1, search2);
        walkInLockStep(search1, search2, scenario.depth());
      } catch (final AssertionError | RuntimeException e) {
        throw new AssertionError(
            "scenario=" + scenario.label() + " fen=" + scenario.fen() + " depth=" + scenario.depth(), e);
      }
    }
  }

  private static Board boardFrom(@Nullable String fen) {
    if (fen == null) {
      return new Board();
    }
    return new Board(fen);
  }

  private static void walkInLockStep(HelpmateSearchBoard search1, HelpmateSearchBoard search2, int depth) {
    if (depth == 0 || search1.getLegalMoves().isEmpty()) {
      return;
    }
    final List<LegalMove> moves = List.copyOf(search1.getLegalMoves());
    for (final LegalMove legalMove : moves) {
      search1.move(legalMove.moveSpecification());
      search2.move(legalMove.moveSpecification());
      assertParityAtRoot(search1, search2);
      walkInLockStep(search1, search2, depth - 1);
      search2.unmove();
      search1.unmove();
    }
  }

  private static void assertParityAtRoot(HelpmateSearchBoard search1, HelpmateSearchBoard search2) {
    final DynamicPosition dp1 = search1.getDynamicPosition();
    final DynamicPosition dp2 = search2.getDynamicPosition();
    final HelpmateSearchKey key1 = search1.currentTranspositionKey();
    final HelpmateSearchKey key2 = search2.currentTranspositionKey();
    assertEquals(dp1.equals(dp2), key1.equals(key2),
        "key equality must mirror DynamicPosition equality (dp1=" + dp1 + " dp2=" + dp2 + ")");
    // Self-equality + hashCode contract.
    assertEquals(key1, key1, "key must equal itself");
    assertEquals(key1.hashCode(), key1.hashCode(), "key hashCode must be stable");
    if (key1.equals(key2)) {
      assertEquals(key1.hashCode(), key2.hashCode(), "equal keys must share hashCode");
    }
  }

  // ---------------------------- Positive controls ----------------------------

  /** Different sideToMove -> different keys. */
  @SuppressWarnings("static-method")
  @Test
  void differentSideToMoveProducesDifferentKey() {
    // Pawn-less position so the EP target is NONE for both sides; isolates the sideToMove field.
    final HelpmateSearchKey whiteToMove = HelpmateSearchBoard.from(new Board("4k3/8/8/8/8/8/8/4K3 w - - 0 1"))
        .currentTranspositionKey();
    final HelpmateSearchKey blackToMove = HelpmateSearchBoard.from(new Board("4k3/8/8/8/8/8/8/4K3 b - - 0 1"))
        .currentTranspositionKey();
    assertNotEquals(whiteToMove, blackToMove);
  }

  /** Different normalized EP target -> different keys. */
  @SuppressWarnings("static-method")
  @Test
  void differentNormalizedEpProducesDifferentKey() {
    // Same piece placement; one has a normalized-EP-capturable target, the other does not.
    final HelpmateSearchKey withEp = HelpmateSearchBoard.from(new Board("8/8/8/8/3pP3/8/8/K6k b - e3 0 1"))
        .currentTranspositionKey();
    final HelpmateSearchKey withoutEp = HelpmateSearchBoard.from(new Board("8/8/8/8/3pP3/8/8/K6k b - - 0 1"))
        .currentTranspositionKey();
    assertNotEquals(withEp, withoutEp);
  }

  /** Different white castling rights -> different keys. */
  @SuppressWarnings("static-method")
  @Test
  void differentWhiteCastlingRightsProducesDifferentKey() {
    final HelpmateSearchKey both = HelpmateSearchBoard.from(new Board("r3k2r/8/8/8/8/8/8/R3K2R w KQkq - 0 1"))
        .currentTranspositionKey();
    final HelpmateSearchKey whiteQueenSideOnly = HelpmateSearchBoard
        .from(new Board("r3k2r/8/8/8/8/8/8/R3K2R w Qkq - 0 1")).currentTranspositionKey();
    assertNotEquals(both, whiteQueenSideOnly);
  }

  /** Different black castling rights -> different keys. */
  @SuppressWarnings("static-method")
  @Test
  void differentBlackCastlingRightsProducesDifferentKey() {
    final HelpmateSearchKey both = HelpmateSearchBoard.from(new Board("r3k2r/8/8/8/8/8/8/R3K2R w KQkq - 0 1"))
        .currentTranspositionKey();
    final HelpmateSearchKey blackKingSideOnly = HelpmateSearchBoard
        .from(new Board("r3k2r/8/8/8/8/8/8/R3K2R w KQk - 0 1")).currentTranspositionKey();
    assertNotEquals(both, blackKingSideOnly);
  }

  /** Different piece bitboard (white pawn position) -> different keys. */
  @SuppressWarnings("static-method")
  @Test
  void differentPiecePlacementProducesDifferentKey() {
    final HelpmateSearchKey before = HelpmateSearchBoard.from(new Board("4k3/8/8/8/8/4P3/8/4K3 w - - 0 1"))
        .currentTranspositionKey();
    final HelpmateSearchKey after = HelpmateSearchBoard.from(new Board("4k3/8/8/8/4P3/8/8/4K3 w - - 0 1"))
        .currentTranspositionKey();
    assertNotEquals(before, after);
  }

  // ---------------------------- Scenario fixtures (mirror TestHelpmateSearchBoard) ----------------------------

  private static final ImmutableList<Scenario> SCENARIOS = Nulls.listOf(new Scenario("initial", null, 2),
      new Scenario("all-four-castling-rights", "r3k2r/8/8/8/8/8/8/R3K2R w KQkq - 0 1", 1),
      new Scenario("legal-en-passant", "8/8/8/8/3pP3/8/8/K6k b - e3 0 1", 2),
      new Scenario("illegal-en-passant-normalization", "8/8/8/8/k2pP2R/8/8/7K b - e3 0 1", 1),
      new Scenario("both-sides-promote", "7k/P7/8/8/8/8/7p/K7 w - - 0 1", 1),
      new Scenario("checkmate-terminal", "7k/6Q1/6K1/8/8/8/8/8 b - - 0 1", 0),
      new Scenario("stalemate-terminal", "7k/5Q2/6K1/8/8/8/8/8 b - - 0 1", 0),
      new Scenario("check-with-king-only-evasions", "4k3/8/8/8/8/8/4R3/4K3 b - - 0 1", 3),
      new Scenario("double-check-king-only", "4k3/2N5/8/8/8/2r5/8/4R2K b - - 0 1", 2),
      new Scenario("en-passant-resolves-check", "4k3/8/8/3pP3/2K5/8/8/8 w - - 0 1", 1));

  private record Scenario(String label, @Nullable String fen, int depth) {
  }
}
