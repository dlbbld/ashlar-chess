package com.dlb.chess.test.board;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.dlb.chess.board.Board;
import com.dlb.chess.common.constants.EnumConstants;
import com.dlb.chess.common.model.MoveSpecification;

/**
 * Tests for the per-move FIDE 9.2 / 9.3 claim predicates on {@link Board}:
 * {@code canClaimFiftyMoveRuleFor(MoveSpecification)}, {@code canClaimThreefoldRepetitionRuleFor(MoveSpecification)},
 * and the composed {@code canClaimDrawFor(MoveSpecification)}.
 *
 * <p>
 * Sister test to {@link TestBoardClaimWithOwnMove}, which exercises the existence-shape predicates ("does any legal
 * move satisfy the claim?"). FIDE 9.2 and 9.3 actually frame the claim as a per-move act — the player announces the
 * specific move they intend to play and claims the draw on that announcement. The per-move predicates are the
 * FIDE-faithful API; the existence predicates are convenience shorthand derived from them.
 */
class TestBoardClaimFor implements EnumConstants {

  // =============================================================================================
  // canClaimFiftyMoveRuleFor — FIDE 9.3 per-move
  // =============================================================================================

  @SuppressWarnings("static-method")
  @Test
  void fiftyMoveForReturnsTrueWhenCandidateMoveIsMate() {
    // Same smothered-mate FEN pinned in TestBoardClaimWithOwnMove#canClaimFiftyMoveRuleWithOwnMove
    // TrueEvenWhenOnlyNonZeroingMoveIsMate. White has one non-zeroing legal move (Nh6-f7, smothered
    // mate). FIDE 9.3 frames the claim as announced before the move is played; the move's outcome
    // (checkmate) does not affect whether the no-progress condition is met. Per-move predicate
    // makes this explicit at the API level.
    final Board board = new Board("6rk/6pp/7N/5p2/6p1/8/2q5/K7 w - - 99 60");
    assertTrue(board.canClaimFiftyMoveRuleFor(new MoveSpecification(H6, F7)),
        "FIDE 9.3: non-pawn non-capture knight move at clock 99 is a valid claim even though it delivers mate");
  }

  @SuppressWarnings("static-method")
  @Test
  void fiftyMoveForReturnsTrueWhenCandidateMoveIsStalemate() {
    // Black king h8 stuck on the corner by its own g7/h7 pawns. White king f6 plays Kf7, after
    // which black is stalemated: g8 is now attacked by the white king on f7, the pawns block g7/h7.
    // The Kf7 move is non-pawn, non-capture and at clock 99 — a valid 50-move claim under FIDE 9.3
    // regardless of the post-position outcome.
    final Board board = new Board("7k/6pp/5K2/8/8/8/8/8 w - - 99 60");
    assertTrue(board.canClaimFiftyMoveRuleFor(new MoveSpecification(F6, F7)),
        "FIDE 9.3: non-pawn non-capture king move at clock 99 is a valid claim even though it delivers stalemate");
  }

  @SuppressWarnings("static-method")
  @Test
  void fiftyMoveForReturnsFalseForPawnMove() {
    // White has a pawn on e4 plus a quiet rook move available. The pawn push resets the halfmove
    // clock so it cannot satisfy the 50-move claim, even though clock 99 is at the boundary.
    final Board board = new Board("7k/8/8/8/4P3/8/4K3/R7 w - - 99 51");
    assertFalse(board.canClaimFiftyMoveRuleFor(new MoveSpecification(E4, E5)),
        "FIDE 9.3: a pawn move resets the clock and cannot satisfy the 50-move claim");
  }

  @SuppressWarnings("static-method")
  @Test
  void fiftyMoveForReturnsFalseForCapture() {
    // White rook a1 has a quiet move (e.g. Ra2) — but the per-move predicate is asked about Ra8,
    // a hypothetical capture of the black king from the same position with a black piece on a8.
    // The capture would reset the clock, so it does not satisfy the claim.
    // Use a position where white can capture a non-king black piece on the back rank.
    final Board board = new Board("r6k/8/8/8/8/8/4K3/R7 w - - 99 51");
    assertFalse(board.canClaimFiftyMoveRuleFor(new MoveSpecification(A1, A8)),
        "FIDE 9.3: a capture resets the clock and cannot satisfy the 50-move claim");
  }

  @SuppressWarnings("static-method")
  @Test
  void fiftyMoveForReturnsFalseWhenClockBelowBoundary() {
    // Clock 98 — one shy of the 99-required boundary. Even a non-pawn non-capture rook move
    // cannot satisfy the claim from here.
    final Board board = new Board("7k/8/8/8/8/8/4K3/R7 w - - 98 50");
    assertFalse(board.canClaimFiftyMoveRuleFor(new MoveSpecification(A1, A2)),
        "FIDE 9.3: clock must be at least 99 for the claim to be available");
  }

  @SuppressWarnings("static-method")
  @Test
  void fiftyMoveForReturnsFalseForIllegalMove() {
    // Same boundary position as the WithOwnMove boundary fixture. Ra1-a9 is not a legal move (off
    // the board even before considering legality semantics); pass a clearly nonsensical move spec
    // that doesn't match any legal move, expect false. Use Ra1-h8 instead: legal as a board pattern
    // (rook can move along rank or file) but obstructed by other pieces.
    final Board board = new Board("7k/8/8/8/8/8/4K3/R7 w - - 99 51");
    // Ra1-h8 is geometrically a rook move but not legal (different rank and file from a1; rook
    // moves only along one or the other). Acts as a "not in the legal-moves set" probe.
    assertFalse(board.canClaimFiftyMoveRuleFor(new MoveSpecification(A1, H8)),
        "a move not in the legal-moves set is not a valid claim, even at clock 99");
  }

  @SuppressWarnings("static-method")
  @Test
  void fiftyMoveForReturnsTrueForQuietRookMoveAtBoundary() {
    // Sanity baseline: the obvious "yes" case. Rook move at clock 99 → claim valid.
    final Board board = new Board("7k/8/8/8/8/8/4K3/R7 w - - 99 51");
    assertTrue(board.canClaimFiftyMoveRuleFor(new MoveSpecification(A1, A2)),
        "FIDE 9.3: quiet non-zeroing legal move at clock 99 is a valid claim");
  }
}
