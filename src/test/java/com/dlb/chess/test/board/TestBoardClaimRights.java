package com.dlb.chess.test.board;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.dlb.chess.board.Board;
import com.dlb.chess.common.constants.EnumConstants;
import com.dlb.chess.common.model.ClaimRights;
import com.dlb.chess.common.model.ClaimableMove;
import com.dlb.chess.common.model.MoveSpecification;
import com.dlb.chess.fen.constants.FenConstants;

/**
 * Tests for {@link Board#calculateFiftyMoveRuleClaimRights()} and
 * {@link Board#calculateThreefoldRepetitionRuleClaimRights()}: the move-list variants of the FIDE 9.2 / 9.3 claim
 * APIs. Each {@link ClaimRights} pairs an existence boolean ({@code canClaim}) with the list of legal moves the side
 * to move could announce as a claim — defensively copied and ordered to match {@link Board#getLegalMoves()}.
 *
 * <p>
 * The per-move predicates {@code canClaimFiftyMoveRuleFor} / {@code canClaimThreefoldRepetitionRuleFor} are the single
 * source of truth for which candidates are admitted; these tests verify the list-shape wrapper around those predicates.
 */
class TestBoardClaimRights implements EnumConstants {

  // =============================================================================================
  // calculateFiftyMoveRuleClaimRights — FIDE 9.3
  // =============================================================================================

  @SuppressWarnings("static-method")
  @Test
  void fiftyMoveEmptyBelowThreshold() {
    final Board board = new Board("7k/8/8/8/8/8/8/4K3 w - - 50 30");
    final ClaimRights rights = board.calculateFiftyMoveRuleClaimRights();
    assertFalse(rights.canClaim(), "clock 50 is below the 50-move-rule threshold of 99");
    assertEquals(0, rights.claimableMoves().size());
  }

  @SuppressWarnings("static-method")
  @Test
  void fiftyMoveQuietRookMoveAtClock99IsClaimable() {
    // White Ra1, K e1; clock 99. Any non-zeroing legal move qualifies (king or rook).
    final Board board = new Board("7k/8/8/8/8/8/4K3/R7 w - - 99 51");
    final ClaimRights rights = board.calculateFiftyMoveRuleClaimRights();
    assertTrue(rights.canClaim(), "at clock 99 every non-zeroing legal move is a 50-move claim candidate");

    boolean foundRa2 = false;
    for (final ClaimableMove claim : rights.claimableMoves()) {
      if (claim.moveSpecification().equals(new MoveSpecification(A1, A2))) {
        foundRa2 = true;
        assertEquals("Ra2", claim.san(), "canonical SAN for the quiet rook move");
        break;
      }
    }
    assertTrue(foundRa2, "the quiet rook move Ra1-a2 must be present as a claim candidate");
  }

  @SuppressWarnings("static-method")
  @Test
  void fiftyMovePawnMoveAtClock99NotClaimable() {
    // White P e4, K e1; clock 99. The pawn move e4-e5 would reset the clock, so it is NOT a 50-move
    // claim candidate even though clock is 99. Other non-zeroing legal moves still qualify.
    final Board board = new Board("7k/8/8/8/4P3/8/4K3/R7 w - - 99 51");
    final ClaimRights rights = board.calculateFiftyMoveRuleClaimRights();
    assertTrue(rights.canClaim(), "non-pawn non-capture moves remain claimable");

    for (final ClaimableMove claim : rights.claimableMoves()) {
      assertFalse(claim.moveSpecification().equals(new MoveSpecification(E4, E5)),
          "the pawn move e4-e5 is clock-resetting and must NOT appear among claimable moves");
    }
  }

  @SuppressWarnings("static-method")
  @Test
  void fiftyMoveCaptureAtClock99NotClaimable() {
    // White R a1, K e1; Black R a8 (a capture target on the same file). Clock 99. The capture Ra1xa8
    // would reset the clock and is not claimable; quiet rook moves on the a-file and king moves are.
    final Board board = new Board("r6k/8/8/8/8/8/4K3/R7 w - - 99 51");
    final ClaimRights rights = board.calculateFiftyMoveRuleClaimRights();
    assertTrue(rights.canClaim(), "non-capture moves remain claimable");

    for (final ClaimableMove claim : rights.claimableMoves()) {
      assertFalse(claim.moveSpecification().equals(new MoveSpecification(A1, A8)),
          "the capture Ra1xa8 is clock-resetting and must NOT appear among claimable moves");
    }
  }

  @SuppressWarnings("static-method")
  @Test
  void fiftyMoveMateInOneAtClock99StillClaimable() {
    // Same smothered-mate FEN as TestBoardClaimFor#fiftyMoveForReturnsTrueWhenCandidateMoveIsMate.
    // White's only non-zeroing legal move is Nh6-f7, which is mate. Strict FIDE 9.3: the move is a
    // valid 50-move claim regardless of the post-position outcome.
    final Board board = new Board("6rk/6pp/7N/5p2/6p1/8/2q5/K7 w - - 99 60");
    final ClaimRights rights = board.calculateFiftyMoveRuleClaimRights();
    assertTrue(rights.canClaim(), "mate-in-one at clock 99 remains a valid 50-move claim under strict FIDE 9.3");

    boolean foundNf7 = false;
    for (final ClaimableMove claim : rights.claimableMoves()) {
      if (claim.moveSpecification().equals(new MoveSpecification(H6, F7))) {
        foundNf7 = true;
        // Canonical SAN includes the mate marker '#'.
        assertEquals("Nf7#", claim.san(), "the SAN of the claim-ahead mate move carries the mate marker");
        break;
      }
    }
    assertTrue(foundNf7, "Nh6-f7 (mate) must surface as a claim candidate at clock 99");
  }

  // =============================================================================================
  // calculateThreefoldRepetitionRuleClaimRights — FIDE 9.2
  // =============================================================================================

  @SuppressWarnings("static-method")
  @Test
  void threefoldExactlyOneClaimAheadAfterSevenPlyKnightShuffle() {
    // 7-ply knight shuffle: Nf3 Nf6 Ng1 Ng8 Nf3 Nf6 Ng1. Black to move. Only Ng8 returns to the
    // initial position for the 3rd time; all other Black moves either reset the clock (pawn moves)
    // or produce a fresh position.
    final Board board = new Board();
    board.movesStrict("Nf3", "Nf6", "Ng1", "Ng8", "Nf3", "Nf6", "Ng1");

    final ClaimRights rights = board.calculateThreefoldRepetitionRuleClaimRights();
    assertTrue(rights.canClaim());
    assertEquals(1, rights.claimableMoves().size(), "only Ng8 creates the initial position's 3rd occurrence");

    final ClaimableMove only = rights.claimableMoves().get(0);
    assertEquals(new MoveSpecification(F6, G8), only.moveSpecification());
    assertEquals("Ng8", only.san());
  }

  @SuppressWarnings("static-method")
  @Test
  void threefoldNonRepeatingLegalMoveExcluded() {
    // Same fixture; the non-repeating move Nb8-c6 is legal but does not create threefold. Must
    // therefore not appear in the claimable list — and the list as a whole has exactly one entry.
    final Board board = new Board();
    board.movesStrict("Nf3", "Nf6", "Ng1", "Ng8", "Nf3", "Nf6", "Ng1");

    final ClaimRights rights = board.calculateThreefoldRepetitionRuleClaimRights();
    for (final ClaimableMove claim : rights.claimableMoves()) {
      assertFalse(claim.moveSpecification().equals(new MoveSpecification(B8, C6)),
          "Nb8-c6 produces a brand-new position and must not appear");
    }
  }

  @SuppressWarnings("static-method")
  @Test
  void threefoldEmptyWhenNoCandidateCreatesThreefold() {
    final Board board = new Board();
    board.movesStrict("e4", "e5", "Nf3", "Nf6");

    final ClaimRights rights = board.calculateThreefoldRepetitionRuleClaimRights();
    assertFalse(rights.canClaim());
    assertEquals(0, rights.claimableMoves().size());
  }

  @SuppressWarnings("static-method")
  @Test
  void threefoldSanOverloadMatchesMoveSpecificationOverload() {
    final Board board = new Board();
    board.movesStrict("Nf3", "Nf6", "Ng1", "Ng8", "Nf3", "Nf6", "Ng1");

    final MoveSpecification ng8 = new MoveSpecification(F6, G8);
    assertEquals(board.canClaimThreefoldRepetitionRuleFor(ng8), board.canClaimThreefoldRepetitionRuleFor("Ng8"),
        "SAN overload must agree with MoveSpecification overload for the same move");
    assertTrue(board.canClaimThreefoldRepetitionRuleFor("Ng8"));

    // Illegal / malformed SAN inputs all return false rather than throwing.
    assertFalse(board.canClaimThreefoldRepetitionRuleFor(""), "empty SAN returns false");
    assertFalse(board.canClaimThreefoldRepetitionRuleFor("Ngarbage"), "malformed SAN returns false");
    assertFalse(board.canClaimThreefoldRepetitionRuleFor("Qh5"),
        "Qh5 is not a legal move from this position and must return false");
  }

  @SuppressWarnings("static-method")
  @Test
  void fiftyMoveSanOverloadMatchesMoveSpecificationOverload() {
    final Board board = new Board("7k/8/8/8/8/8/4K3/R7 w - - 99 51");

    assertEquals(board.canClaimFiftyMoveRuleFor(new MoveSpecification(A1, A2)),
        board.canClaimFiftyMoveRuleFor("Ra2"), "SAN overload must agree with MoveSpecification overload");
    assertTrue(board.canClaimFiftyMoveRuleFor("Ra2"));

    assertFalse(board.canClaimFiftyMoveRuleFor(""), "empty SAN returns false");
    assertFalse(board.canClaimFiftyMoveRuleFor("not a san"), "malformed SAN returns false");
  }

  // =============================================================================================
  // Object behavior — defensive copy, canClaim invariant, board immutability
  // =============================================================================================

  @SuppressWarnings("static-method")
  @Test
  void returnedListIsImmutable() {
    final Board board = new Board("7k/8/8/8/8/8/4K3/R7 w - - 99 51");
    final ClaimRights rights = board.calculateFiftyMoveRuleClaimRights();

    final List<ClaimableMove> moves = rights.claimableMoves();
    assertTrue(moves.size() > 0, "precondition: at least one claimable move");

    try {
      moves.add(moves.get(0));
      throw new AssertionError("expected UnsupportedOperationException — claimableMoves must be immutable");
    } catch (@SuppressWarnings("unused") final UnsupportedOperationException expected) {
      // OK
    }
  }

  @SuppressWarnings("static-method")
  @Test
  void canClaimMirrorsListEmptiness() {
    final Board emptyBoard = new Board(FenConstants.FEN_INITIAL);
    final ClaimRights emptyRights = emptyBoard.calculateFiftyMoveRuleClaimRights();
    assertEquals(emptyRights.canClaim(), !emptyRights.claimableMoves().isEmpty(),
        "canClaim must equal !claimableMoves.isEmpty()");
    assertFalse(emptyRights.canClaim());

    final Board claimableBoard = new Board("7k/8/8/8/8/8/4K3/R7 w - - 99 51");
    final ClaimRights claimableRights = claimableBoard.calculateFiftyMoveRuleClaimRights();
    assertEquals(claimableRights.canClaim(), !claimableRights.claimableMoves().isEmpty());
    assertTrue(claimableRights.canClaim());
  }

  @SuppressWarnings("static-method")
  @Test
  void claimableMovesOrderFollowsLegalMovesOrder() {
    // At clock 99 with a quiet rook+king position, multiple non-zeroing legal moves all qualify.
    // The claimable list must follow getLegalMoves() iteration order — i.e., the indices of
    // claimable moves into the legal-moves list are strictly ascending.
    final Board board = new Board("7k/8/8/8/8/8/4K3/R7 w - - 99 51");
    final ClaimRights rights = board.calculateFiftyMoveRuleClaimRights();
    assertTrue(rights.claimableMoves().size() >= 2, "precondition: at least two candidates exist");

    final List<MoveSpecification> legalOrder = new java.util.ArrayList<>();
    for (final var legal : board.getLegalMoves()) {
      legalOrder.add(legal.moveSpecification());
    }

    var lastFoundIndex = -1;
    for (final ClaimableMove claim : rights.claimableMoves()) {
      final int idx = legalOrder.indexOf(claim.moveSpecification());
      assertTrue(idx > lastFoundIndex,
          "claimable move " + claim.san() + " (legal-move index " + idx
              + ") must appear after the previously seen claimable (index " + lastFoundIndex + ")");
      lastFoundIndex = idx;
    }
  }

  @SuppressWarnings("static-method")
  @Test
  void boardStateUnchangedAfterClaimRightsQuery() {
    final Board board = new Board();
    board.movesStrict("Nf3", "Nf6", "Ng1", "Ng8", "Nf3", "Nf6", "Ng1");

    final int halfMoveCountBefore = board.getPerformedHalfMoveCount();
    final String fenBefore = board.getFen();

    board.calculateFiftyMoveRuleClaimRights();
    board.calculateThreefoldRepetitionRuleClaimRights();
    board.canClaimFiftyMoveRuleFor("Ng8");
    board.canClaimThreefoldRepetitionRuleFor("Ng8");

    assertEquals(halfMoveCountBefore, board.getPerformedHalfMoveCount(),
        "queries must not alter the played-move count");
    assertEquals(fenBefore, board.getFen(), "queries must not alter the position (FEN unchanged)");
  }
}
