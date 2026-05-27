package com.dlb.chess.common.model;

/**
 * One legal move that the side to move could announce as a FIDE 9.2 (threefold repetition) or 9.3 (50-move rule) draw
 * claim. Pairs the {@link MoveSpecification} the side would play with the canonical SAN string for that move in the
 * current position, so callers can render the move and play it without re-resolving SAN.
 *
 * <p>
 * Produced by {@code Board.calculateFiftyMoveRuleClaimRights()} and
 * {@code Board.calculateThreefoldRepetitionRuleClaimRights()}.
 */
public record ClaimableMove(MoveSpecification moveSpecification, String san) {

  public ClaimableMove {
    if (san.isEmpty()) {
      throw new IllegalArgumentException("san must be non-empty");
    }
  }
}
