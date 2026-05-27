package com.dlb.chess.common.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import com.dlb.chess.board.enums.Side;
import com.dlb.chess.common.enums.Termination;

/**
 * Direct compact-constructor tests for {@link Outcome}. Pins the runtime invariants the record's compact constructor
 * enforces — non-null fields, plus the "winner == Side.NONE iff !CHECKMATE" invariant. {@code @NonNullByDefault} is not
 * a runtime check, so these tests defend against callers (including third-party consumers of the public record) that
 * pass nulls or otherwise violate the invariant.
 */
class TestOutcome {

  @SuppressWarnings("static-method")
  @Test
  void checkmateConstructionAcceptsWhiteWinner() {
    final Outcome outcome = new Outcome(Termination.CHECKMATE, Side.WHITE);
    assertEquals(Termination.CHECKMATE, outcome.termination());
    assertEquals(Side.WHITE, outcome.winner());
  }

  @SuppressWarnings("static-method")
  @Test
  void checkmateConstructionAcceptsBlackWinner() {
    final Outcome outcome = new Outcome(Termination.CHECKMATE, Side.BLACK);
    assertEquals(Side.BLACK, outcome.winner());
  }

  @SuppressWarnings("static-method")
  @Test
  void drawingTerminationsAcceptSideNone() {
    new Outcome(Termination.INSUFFICIENT_MATERIAL, Side.NONE);
    new Outcome(Termination.STALEMATE, Side.NONE);
    new Outcome(Termination.SEVENTY_FIVE_MOVES, Side.NONE);
    new Outcome(Termination.FIVEFOLD_REPETITION, Side.NONE);
    // No exception thrown — all four drawing terminations accept Side.NONE as the winner.
  }

  // === ONGOING / Termination.NONE ===

  @SuppressWarnings("static-method")
  @Test
  void ongoingTerminationAcceptsSideNone() {
    final Outcome ongoing = new Outcome(Termination.NONE, Side.NONE);
    assertEquals(Termination.NONE, ongoing.termination());
    assertEquals(Side.NONE, ongoing.winner());
  }

  @SuppressWarnings("static-method")
  @Test
  void ongoingSingletonHasExpectedShape() {
    assertEquals(Termination.NONE, Outcome.ONGOING.termination());
    assertEquals(Side.NONE, Outcome.ONGOING.winner());
  }

  @SuppressWarnings("static-method")
  @Test
  void ongoingTerminationWithWinnerSideRejected() {
    assertThrows(IllegalArgumentException.class, () -> new Outcome(Termination.NONE, Side.WHITE),
        "Termination.NONE outcome requires winner == Side.NONE (no one has won an ongoing game)");
  }

  // === null rejection ===

  @SuppressWarnings("static-method")
  @Test
  void nullTerminationRejected() {
    assertThrows(NullPointerException.class, () -> new Outcome(null, Side.NONE),
        "compact constructor must reject null termination");
  }

  @SuppressWarnings("static-method")
  @Test
  void nullWinnerRejectedForCheckmate() {
    assertThrows(NullPointerException.class, () -> new Outcome(Termination.CHECKMATE, null),
        "compact constructor must reject null winner (use Side.NONE for drawing outcomes)");
  }

  @SuppressWarnings("static-method")
  @Test
  void nullWinnerRejectedForDrawingTermination() {
    assertThrows(NullPointerException.class, () -> new Outcome(Termination.INSUFFICIENT_MATERIAL, null),
        "compact constructor must reject null winner even on drawing terminations (use Side.NONE explicitly)");
  }

  // === checkmate-vs-draw winner invariant ===

  @SuppressWarnings("static-method")
  @Test
  void checkmateWithSideNoneRejected() {
    assertThrows(IllegalArgumentException.class, () -> new Outcome(Termination.CHECKMATE, Side.NONE),
        "CHECKMATE outcome requires winner to be WHITE or BLACK");
  }

  @SuppressWarnings("static-method")
  @Test
  void drawingTerminationWithWhiteWinnerRejected() {
    assertThrows(IllegalArgumentException.class, () -> new Outcome(Termination.STALEMATE, Side.WHITE),
        "drawing outcome STALEMATE requires winner == Side.NONE");
  }

  @SuppressWarnings("static-method")
  @Test
  void drawingTerminationWithBlackWinnerRejected() {
    assertThrows(IllegalArgumentException.class, () -> new Outcome(Termination.INSUFFICIENT_MATERIAL, Side.BLACK),
        "drawing outcome INSUFFICIENT_MATERIAL requires winner == Side.NONE");
  }
}
