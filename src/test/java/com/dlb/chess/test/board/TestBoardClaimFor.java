package com.dlb.chess.test.board;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
 * move satisfy the claim?"). FIDE 9.2 and 9.3 actually frame the claim as a per-move act - the player announces the
 * specific move they intend to play and claims the draw on that announcement. The per-move predicates are the
 * FIDE-faithful API; the existence predicates are convenience shorthand derived from them.
 */
class TestBoardClaimFor implements EnumConstants {

  // =============================================================================================
  // canClaimFiftyMoveRuleFor - FIDE 9.3 per-move
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
    final MoveSpecification nf7 = new MoveSpecification(H6, F7);
    assertTrue(board.canClaimFiftyMoveRuleFor(nf7),
        "FIDE 9.3: non-pawn non-capture knight move at clock 99 is a valid claim even though it delivers mate");

    // Fixture-correctness check (sister to the stalemate test below): after Nf7 the post-position
    // must actually be checkmate, not merely check or quiet. Pins the edge the predicate name
    // promises to exercise; a regression that quietly degraded the fixture would now fail here.
    board.move(nf7);
    assertTrue(board.isCheckmate(),
        "fixture must actually be checkmate after Nf7 - otherwise the 'candidate move is mate' edge is not covered");
  }

  @SuppressWarnings("static-method")
  @Test
  void fiftyMoveForReturnsTrueWhenCandidateMoveIsStalemate() {
    // Carefully constructed stalemate fixture. White: Ke7, Ph6. Black: Kh8, Ph7. After White plays
    // Kf7, Black's king has no legal moves (g8 and g7 are both attacked by the white king on f7;
    // g7 is additionally attacked by the white pawn on h6; h7 is occupied by Black's own pawn) and
    // Black's pawn on h7 has no legal moves (h6 is blocked by the white pawn; there is no piece
    // on g6 to capture). Black is not in check, so the resulting position is stalemate, not mate.
    //
    // The Kf7 move is non-pawn, non-capture and clock is 99 - a valid 50-move claim under FIDE 9.3
    // regardless of the post-position outcome. The fixture-correctness assertions below pin both
    // the per-move predicate result and the stalemate-ness of the post-position; a regression that
    // accidentally let Black move (e.g. omitting the white pawn on h6 - which is exactly what the
    // earlier "7k/6pp/5K2/..." fixture did, leaving Black with the legal pawn moves g5/g6/h5/h6)
    // would now fail the post-move stalemate assertion rather than silently degrading the test
    // to a quiet 50-move candidate.
    final Board board = new Board("7k/4K2p/7P/8/8/8/8/8 w - - 99 60");
    final MoveSpecification kf7 = new MoveSpecification(E7, F7);
    assertTrue(board.canClaimFiftyMoveRuleFor(kf7),
        "FIDE 9.3: non-pawn non-capture king move at clock 99 is a valid claim even though it delivers stalemate");

    // Fixture-correctness check: after Kf7 the post-position must actually be stalemate (not
    // merely a quiet position). Pins the edge that the predicate name promises to exercise.
    board.move(kf7);
    assertTrue(board.isStalemate(),
        "fixture must actually be stalemate after Kf7 - otherwise the 'candidate move is stalemate' edge is not covered");
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
    // White rook a1 has a quiet move (e.g. Ra2) - but the per-move predicate is asked about Ra8,
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
    // Clock 98 - one shy of the 99-required boundary. Even a non-pawn non-capture rook move
    // cannot satisfy the claim from here.
    final Board board = new Board("7k/8/8/8/8/8/4K3/R7 w - - 98 50");
    assertFalse(board.canClaimFiftyMoveRuleFor(new MoveSpecification(A1, A2)),
        "FIDE 9.3: clock must be at least 99 for the claim to be available");
  }

  @SuppressWarnings("static-method")
  @Test
  void fiftyMoveForThrowsForIllegalMove() {
    // Same boundary position as the WithOwnMove boundary fixture. Ra1-h8 is geometrically a rook
    // move but not legal (different rank and file from a1; rook moves only along one or the other).
    // The per-move predicate now treats "not in the legal-moves set" as a loud failure - throws
    // IllegalArgumentException rather than silently returning false.
    final Board board = new Board("7k/8/8/8/8/8/4K3/R7 w - - 99 51");
    assertThrows(IllegalArgumentException.class,
        () -> board.canClaimFiftyMoveRuleFor(new MoveSpecification(A1, H8)),
        "a move not in the legal-moves set must throw, not silently return false");
  }

  @SuppressWarnings("static-method")
  @Test
  void fiftyMoveForReturnsTrueForQuietRookMoveAtBoundary() {
    // Sanity baseline: the obvious "yes" case. Rook move at clock 99 -> claim valid.
    final Board board = new Board("7k/8/8/8/8/8/4K3/R7 w - - 99 51");
    assertTrue(board.canClaimFiftyMoveRuleFor(new MoveSpecification(A1, A2)),
        "FIDE 9.3: quiet non-zeroing legal move at clock 99 is a valid claim");
  }

  // =============================================================================================
  // canClaimThreefoldRepetitionRuleFor - FIDE 9.2 per-move
  // =============================================================================================

  @SuppressWarnings("static-method")
  @Test
  void threefoldForReturnsTrueWhenCandidateMoveCreatesThirdOccurrence() {
    // 7-ply knight shuffle stops one ply before the initial position's 3rd occurrence; Black to
    // move next. Black's knight is on f6; Ng8 returns to the initial position and triggers
    // threefold. canClaimThreefoldRepetitionRuleFor(Ng8) must return true.
    final Board board = new Board();
    board.movesStrict("Nf3", "Nf6", "Ng1", "Ng8", "Nf3", "Nf6", "Ng1");
    assertTrue(board.canClaimThreefoldRepetitionRuleFor(new MoveSpecification(F6, G8)),
        "FIDE 9.2: Black's Ng8 creates the initial position's 3rd occurrence");
  }

  @SuppressWarnings("static-method")
  @Test
  void threefoldForReturnsFalseForLegalMoveThatDoesNotCreateThreefold() {
    // Same position as above. Black's Nc6 (b8-c6) is a non-zeroing legal move but produces a
    // brand-new position; not a threefold. Distinguishes the per-move predicate from the
    // existence-shape predicate, which returns true here because Ng8 satisfies.
    final Board board = new Board();
    board.movesStrict("Nf3", "Nf6", "Ng1", "Ng8", "Nf3", "Nf6", "Ng1");
    assertTrue(board.canClaimThreefoldRepetitionRuleWithOwnMove(),
        "precondition: existence predicate is true (Ng8 satisfies)");
    assertFalse(board.canClaimThreefoldRepetitionRuleFor(new MoveSpecification(B8, C6)),
        "FIDE 9.2: Nc6 produces a new position, not a threefold");
  }

  @SuppressWarnings("static-method")
  @Test
  void threefoldForReturnsFalseForPawnMove() {
    // Same fixture. Black's e7-e5 pawn push resets the clock and creates a position that has never
    // occurred before in the game - cannot be a threefold.
    final Board board = new Board();
    board.movesStrict("Nf3", "Nf6", "Ng1", "Ng8", "Nf3", "Nf6", "Ng1");
    assertFalse(board.canClaimThreefoldRepetitionRuleFor(new MoveSpecification(E7, E5)),
        "FIDE 9.2: a pawn move creates a never-before-seen position; not a threefold");
  }

  @SuppressWarnings("static-method")
  @Test
  void threefoldForReturnsFalseForCapture() {
    // Tiny position with a capture available. The capture produces a position with different
    // material than any earlier position in the game; cannot satisfy threefold.
    final Board board = new Board("4k3/8/8/8/3p4/4P3/4K3/8 w - - 0 1");
    assertFalse(board.canClaimThreefoldRepetitionRuleFor(new MoveSpecification(E3, D4)),
        "FIDE 9.2: a capture changes material; the resulting position cannot have occurred before");
  }

  @SuppressWarnings("static-method")
  @Test
  void threefoldForThrowsForIllegalMove() {
    // Move not in the legal-moves set - predicate now throws rather than silently returning false.
    // F6-A1 is not a legal move from any piece on f6 (knight on f6 cannot reach a1).
    final Board board = new Board();
    board.movesStrict("Nf3", "Nf6", "Ng1", "Ng8", "Nf3", "Nf6", "Ng1");
    assertThrows(IllegalArgumentException.class,
        () -> board.canClaimThreefoldRepetitionRuleFor(new MoveSpecification(F6, A1)),
        "a move not in the legal-moves set must throw, not silently return false");
  }

  // =============================================================================================
  // canClaimDrawFor - composed convenience
  // =============================================================================================

  @SuppressWarnings("static-method")
  @Test
  void drawForReturnsTrueWhenOnlyFiftyMoveBranchTriggers() {
    // Quiet rook move at clock 99: 50-move branch true, threefold branch false (fresh position).
    final Board board = new Board("7k/8/8/8/8/8/4K3/R7 w - - 99 51");
    assertTrue(board.canClaimFiftyMoveRuleFor(new MoveSpecification(A1, A2)),
        "precondition: 50-move per-move predicate is true");
    assertFalse(board.canClaimThreefoldRepetitionRuleFor(new MoveSpecification(A1, A2)),
        "precondition: threefold per-move predicate is false");
    assertTrue(board.canClaimDrawFor(new MoveSpecification(A1, A2)),
        "composed convenience: true when either branch is true");
  }

  @SuppressWarnings("static-method")
  @Test
  void drawForReturnsTrueWhenOnlyThreefoldBranchTriggers() {
    // 7-ply knight shuffle; Black's Ng8 creates threefold. Clock is small (around 7) so the 50-move
    // branch is false. Composed convenience must still be true via the threefold branch.
    final Board board = new Board();
    board.movesStrict("Nf3", "Nf6", "Ng1", "Ng8", "Nf3", "Nf6", "Ng1");
    final MoveSpecification ng8 = new MoveSpecification(F6, G8);
    assertFalse(board.canClaimFiftyMoveRuleFor(ng8), "precondition: clock below 99");
    assertTrue(board.canClaimThreefoldRepetitionRuleFor(ng8), "precondition: Ng8 creates threefold");
    assertTrue(board.canClaimDrawFor(ng8), "composed convenience: true when either branch is true");
  }

  @SuppressWarnings("static-method")
  @Test
  void drawForReturnsFalseWhenNeitherBranchTriggers() {
    // Fresh game, low clock, no repetition history. No claim available via either branch.
    final Board board = new Board();
    board.movesStrict("e4", "e5");
    // 1.Nf3 from this position: legal, non-zeroing, but clock is 1 (after both pawn pushes the
    // clock is 0; the Nf3 push would bring it to 1) -- well below 99. And the post-position is
    // fresh, never seen before -- not threefold.
    assertFalse(board.canClaimDrawFor(new MoveSpecification(G1, F3)),
        "neither branch satisfies the claim");
  }
}
