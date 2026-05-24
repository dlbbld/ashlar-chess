package com.dlb.chess.common.enums;

/**
 * Status of a game (board with history). The three move-blocking terminations ({@link #CHECKMATE},
 * {@link #STALEMATE}, {@link #DEAD_POSITION_INSUFFICIENT_MATERIAL}) end the game permanently — no further moves are
 * accepted by the validation pipeline (see {@code ValidateNewMove} and {@code StrictSanParser}).
 *
 * <p>
 * {@link #FIVE_FOLD_REPETITION_RULE} (FIDE 9.6.1) and {@link #SEVENTY_FIVE_MOVE_RULE} (FIDE 9.6.2) are FIDE-automatic
 * draw rules in the rulebook, but in this library they are surfaced as <em>queryable predicates</em> rather than
 * enforced at the move pipeline. The position itself is not necessarily drawn — mating material can still be present,
 * pawn moves and captures can still happen, and a later checkmate can still occur if play continues. The library is
 * permissive here for corpus and tooling compatibility (historical PGN databases routinely contain games whose
 * recorded play continues a move or two past the threshold); the caller decides whether to adjudicate the draw.
 * Consumers that want to surface the rule call {@link com.dlb.chess.board.Board#isFivefoldRepetition()} /
 * {@link com.dlb.chess.board.Board#isSeventyFiveMove()} themselves. See {@link #isAutomaticTermination()}.
 *
 * <p>
 * The two single-side insufficient-material variants ({@link #INSUFFICIENT_MATERIAL_WHITE_ONLY},
 * {@link #INSUFFICIENT_MATERIAL_BLACK_ONLY}) are diagnostic states, not terminations: under FIDE 5.2.2 a dead position
 * requires that <em>neither</em> side can deliver checkmate. If one side still has mating material the game continues,
 * so these statuses do <em>not</em> block further moves — see {@link #isAutomaticTermination()}.
 *
 * <p>
 * The two dead-position values capture the FIDE 5.2.2 "no series of legal moves can lead to checkmate" rule via two
 * detection paths of different costs. {@link #DEAD_POSITION_INSUFFICIENT_MATERIAL} is the cheap detector that fires on
 * mechanical piece-count grounds (KK, KBK, KNK, KBKB-same-color) and still blocks moves. {@link
 * #DEAD_POSITION_UNWINNABLE_QUICK} is the more expensive analyzer-driven detector that catches the rest of the
 * dead-position class within Ambrona's quick unwinnability check (pawn walls and similar); it is surfaced as a
 * queryable status so callers can decide whether to adjudicate.
 *
 * <p>
 * Claimable draws (3-fold repetition, 50-move rule) are NOT represented here — they remain queryable on the board but
 * never end the game automatically, since a player may decline to claim and continue playing under FIDE rules.
 */
public enum GameStatus {

  CHECKMATE,
  STALEMATE,
  DEAD_POSITION_INSUFFICIENT_MATERIAL,
  DEAD_POSITION_UNWINNABLE_QUICK,
  INSUFFICIENT_MATERIAL_WHITE_ONLY,
  INSUFFICIENT_MATERIAL_BLACK_ONLY,
  FIVE_FOLD_REPETITION_RULE,
  SEVENTY_FIVE_MOVE_RULE,
  ONGOING;

  /**
   * Returns {@code true} iff this status is one of the three terminations that end the game permanently in this
   * library: {@link #CHECKMATE}, {@link #STALEMATE}, {@link #DEAD_POSITION_INSUFFICIENT_MATERIAL}. {@link
   * #DEAD_POSITION_UNWINNABLE_QUICK}, {@link #FIVE_FOLD_REPETITION_RULE}, and {@link #SEVENTY_FIVE_MOVE_RULE} are
   * queryable predicates rather than enforced terminations and return {@code false} here. The single-side
   * insufficient-material variants and {@link #ONGOING} also return {@code false}.
   */
  public boolean isAutomaticTermination() {
    return switch (this) {
      case CHECKMATE, STALEMATE, DEAD_POSITION_INSUFFICIENT_MATERIAL -> true;
      case DEAD_POSITION_UNWINNABLE_QUICK, FIVE_FOLD_REPETITION_RULE, SEVENTY_FIVE_MOVE_RULE, INSUFFICIENT_MATERIAL_WHITE_ONLY, INSUFFICIENT_MATERIAL_BLACK_ONLY, ONGOING -> false;
    };
  }
}
