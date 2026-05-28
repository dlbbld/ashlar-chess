package com.dlb.chess.common.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import com.dlb.chess.board.enums.Side;
import com.dlb.chess.common.enums.Termination;

/**
 * Direct compact-constructor tests for {@link Outcome}. Pins the semantic invariants the record's compact constructor
 * enforces - the "winner is Side.NONE unless termination is CHECKMATE" rule. Non-nullness of the fields is enforced at
 * compile time by the package's {@code @NonNullByDefault}, so no runtime null-rejection tests are needed.
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
    checkDrawingTermination(new Outcome(Termination.INSUFFICIENT_MATERIAL, Side.NONE));
    checkDrawingTermination(new Outcome(Termination.STALEMATE, Side.NONE));
    checkDrawingTermination(new Outcome(Termination.SEVENTY_FIVE_MOVES, Side.NONE));
    checkDrawingTermination(new Outcome(Termination.FIVEFOLD_REPETITION, Side.NONE));
    // No exception thrown - all four drawing terminations accept Side.NONE as the winner.
  }

  private static void checkDrawingTermination(Outcome outcome) {
    assertEquals(Side.NONE, outcome.winner());
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
