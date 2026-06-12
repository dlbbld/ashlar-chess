// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.test.board;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import io.github.dlbbld.ashlarchess.board.Board;
import io.github.dlbbld.ashlarchess.common.Nulls;
import io.github.dlbbld.ashlarchess.common.constants.EnumConstants;
import io.github.dlbbld.ashlarchess.common.model.ClaimRights;
import io.github.dlbbld.ashlarchess.common.model.ClaimableMove;
import io.github.dlbbld.ashlarchess.common.model.MoveSpecification;
import io.github.dlbbld.ashlarchess.fen.constants.FenConstants;
import io.github.dlbbld.ashlarchess.model.LegalMove;
import io.github.dlbbld.ashlarchess.san.LenientSanParserValidationException;

/**
 * Tests for {@link Board#calculateFiftyMoveRuleClaimRights()} and
 * {@link Board#calculateThreefoldRepetitionRuleClaimRights()}: the move-list variants of the FIDE 9.2 / 9.3 claim APIs.
 * Each {@link ClaimRights} pairs an existence boolean ({@code canClaim}) with the list of legal moves the side to move
 * could announce as a claim - defensively copied and ordered to match {@link Board#getLegalMoves()}.
 *
 * <p>
 * The per-move predicates {@code canClaimFiftyMoveRuleFor} / {@code canClaimThreefoldRepetitionRuleFor} are the single
 * source of truth for which candidates are admitted; these tests verify the list-shape wrapper around those predicates.
 */
class TestBoardClaimRights implements EnumConstants {

  // =============================================================================================
  // calculateFiftyMoveRuleClaimRights - FIDE 9.3
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
  // calculateThreefoldRepetitionRuleClaimRights - FIDE 9.2
  // =============================================================================================

  @SuppressWarnings("static-method")
  @Test
  void threefoldExactlyOneClaimAheadAfterSevenMoveKnightShuffle() {
    // 7-move knight shuffle: Nf3 Nf6 Ng1 Ng8 Nf3 Nf6 Ng1. Black to move. Only Ng8 returns to the
    // initial position for the 3rd time; all other Black moves either reset the clock (pawn moves)
    // or produce a fresh position.
    final Board board = new Board();
    board.movesStrict("Nf3", "Nf6", "Ng1", "Ng8", "Nf3", "Nf6", "Ng1");

    final ClaimRights rights = board.calculateThreefoldRepetitionRuleClaimRights();
    assertTrue(rights.canClaim());
    assertEquals(1, rights.claimableMoves().size(), "only Ng8 creates the initial position's 3rd occurrence");

    final ClaimableMove only = Nulls.get(rights.claimableMoves(), 0);
    assertEquals(new MoveSpecification(F6, G8), only.moveSpecification());
    assertEquals("Ng8", only.san());
  }

  @SuppressWarnings("static-method")
  @Test
  void threefoldNonRepeatingLegalMoveExcluded() {
    // Same fixture; the non-repeating move Nb8-c6 is legal but does not create threefold. Must
    // therefore not appear in the claimable list - and the list as a whole has exactly one entry.
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
  void threefoldSanOverloadMatchesMoveSpecificationOverload() throws LenientSanParserValidationException {
    final Board board = new Board();
    board.movesStrict("Nf3", "Nf6", "Ng1", "Ng8", "Nf3", "Nf6", "Ng1");

    final MoveSpecification ng8 = new MoveSpecification(F6, G8);
    assertEquals(board.canClaimThreefoldRepetitionRuleFor(ng8), board.canClaimThreefoldRepetitionRuleFor("Ng8"),
        "SAN overload must agree with MoveSpecification overload for the same move");
    assertTrue(board.canClaimThreefoldRepetitionRuleFor("Ng8"));

    // Invalid SAN inputs throw rather than silently returning false. Empty SAN and malformed SAN
    // throw SanValidationException from the lenient parser; a legal-shape SAN that doesn't match
    // any current legal move throws IllegalArgumentException from the MoveSpecification overload.
    assertThrows(LenientSanParserValidationException.class, () -> board.canClaimThreefoldRepetitionRuleFor(""),
        "empty SAN must throw LenientSanParserValidationException");
    assertThrows(LenientSanParserValidationException.class, () -> board.canClaimThreefoldRepetitionRuleFor("Ngarbage"),
        "malformed SAN must throw LenientSanParserValidationException");
    assertThrows(LenientSanParserValidationException.class, () -> board.canClaimThreefoldRepetitionRuleFor("Qh5"),
        "Qh5 is not a legal move from this position and must throw via the SAN pipeline");
  }

  @SuppressWarnings("static-method")
  @Test
  void fiftyMoveSanOverloadMatchesMoveSpecificationOverload() throws LenientSanParserValidationException {
    final Board board = new Board("7k/8/8/8/8/8/4K3/R7 w - - 99 51");

    assertEquals(board.canClaimFiftyMoveRuleFor(new MoveSpecification(A1, A2)), board.canClaimFiftyMoveRuleFor("Ra2"),
        "SAN overload must agree with MoveSpecification overload");
    assertTrue(board.canClaimFiftyMoveRuleFor("Ra2"));

    assertThrows(LenientSanParserValidationException.class, () -> board.canClaimFiftyMoveRuleFor(""),
        "empty SAN must throw LenientSanParserValidationException");
    assertThrows(LenientSanParserValidationException.class, () -> board.canClaimFiftyMoveRuleFor("not a san"),
        "malformed SAN must throw LenientSanParserValidationException");
  }

  // =============================================================================================
  // Object behavior - defensive copy, canClaim invariant, board immutability
  // =============================================================================================

  @SuppressWarnings("static-method")
  @Test
  void returnedListIsImmutable() {
    final Board board = new Board("7k/8/8/8/8/8/4K3/R7 w - - 99 51");
    final ClaimRights rights = board.calculateFiftyMoveRuleClaimRights();

    final List<ClaimableMove> moves = rights.claimableMoves();
    assertTrue(moves.size() > 0, "precondition: at least one claimable move");

    try {
      moves.add(Nulls.get(moves, 0));
      throw new AssertionError("expected UnsupportedOperationException - claimableMoves must be immutable");
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
    // The claimable list must follow getLegalMoves() iteration order - i.e., the indices of
    // claimable moves into the legal-moves list are strictly ascending.
    final Board board = new Board("7k/8/8/8/8/8/4K3/R7 w - - 99 51");
    final ClaimRights rights = board.calculateFiftyMoveRuleClaimRights();
    assertTrue(rights.claimableMoves().size() >= 2, "precondition: at least two candidates exist");

    final List<MoveSpecification> legalOrder = new ArrayList<>();
    for (final LegalMove legal : board.getLegalMoves()) {
      legalOrder.add(legal.moveSpecification());
    }

    int lastFoundIndex = -1;
    for (final ClaimableMove claim : rights.claimableMoves()) {
      final int idx = legalOrder.indexOf(claim.moveSpecification());
      assertTrue(idx > lastFoundIndex, "claimable move " + claim.san() + " (legal-move index " + idx
          + ") must appear after the previously seen claimable (index " + lastFoundIndex + ")");
      lastFoundIndex = idx;
    }
  }

  @SuppressWarnings("static-method")
  @Test
  void constructorDefensivelyCopiesSourceListNonEmptyPath() {
    // Sister to returnedListIsImmutable: that test proves the EXPOSED list rejects mutation.
    // This one proves the constructor decouples the record from the SOURCE list - i.e., the
    // compact constructor's ImmutableList.copyOf is actually copying, not aliasing. Build a
    // mutable source, construct ClaimRights, then mutate the source after construction and
    // assert the record's view is unchanged.
    final ClaimableMove original = new ClaimableMove(new MoveSpecification(A1, A2), "Ra2");
    final List<ClaimableMove> mutableSource = new ArrayList<>();
    mutableSource.add(original);

    final ClaimRights rights = new ClaimRights(true, mutableSource);
    assertEquals(1, rights.claimableMoves().size(), "precondition: one entry after construction");

    // Mutate the source AFTER construction - the record must not reflect these changes.
    mutableSource.clear();
    mutableSource.add(new ClaimableMove(new MoveSpecification(A1, B1), "Rb1"));
    mutableSource.add(new ClaimableMove(new MoveSpecification(A1, C1), "Rc1"));

    assertEquals(1, rights.claimableMoves().size(),
        "post-construction source mutations must not leak into the record's claimableMoves");
    assertEquals(original, rights.claimableMoves().get(0), "the originally-present entry must remain unchanged");
  }

  @SuppressWarnings("static-method")
  @Test
  void constructorDefensivelyCopiesSourceListEmptyPath() {
    // Same invariant on the canClaim=false branch: start with an empty mutable source, construct
    // a ClaimRights, then add to the source. The record must remain empty (and canClaim==false).
    final List<ClaimableMove> mutableSource = new ArrayList<>();
    final ClaimRights rights = new ClaimRights(false, mutableSource);
    assertFalse(rights.canClaim(), "precondition: empty source -> canClaim==false");
    assertEquals(0, rights.claimableMoves().size());

    mutableSource.add(new ClaimableMove(new MoveSpecification(A1, A2), "Ra2"));

    assertEquals(0, rights.claimableMoves().size(),
        "post-construction source addition must not leak into the record's claimableMoves");
    assertFalse(rights.canClaim(), "canClaim must remain false; the invariant is fixed at construction");
  }

  @SuppressWarnings("static-method")
  @Test
  void boardStateUnchangedAfterClaimRightsQuery() throws LenientSanParserValidationException {
    final Board board = new Board();
    board.movesStrict("Nf3", "Nf6", "Ng1", "Ng8", "Nf3", "Nf6", "Ng1");

    final int performedMoveCountBefore = board.getPerformedMoveCount();
    final String fenBefore = board.getFen();

    board.calculateFiftyMoveRuleClaimRights();
    board.calculateThreefoldRepetitionRuleClaimRights();
    board.canClaimFiftyMoveRuleFor("Ng8");
    board.canClaimThreefoldRepetitionRuleFor("Ng8");

    assertEquals(performedMoveCountBefore, board.getPerformedMoveCount(),
        "queries must not alter the played-move count");
    assertEquals(fenBefore, board.getFen(), "queries must not alter the position (FEN unchanged)");
  }
}
