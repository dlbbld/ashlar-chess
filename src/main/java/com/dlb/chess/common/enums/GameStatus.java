package com.dlb.chess.common.enums;

/**
 * Current-position outcome of a game (board with history). All values are queryable — none block further moves at the
 * validation pipeline. Caller polls {@code BasicChessUtility.calculateGameStatus} or the specific {@code Board.is*}
 * predicates to learn whether the game has reached an automatic termination, and decides whether to adjudicate. The
 * library is permissive at the move pipeline for corpus and tooling compatibility (historical PGN databases routinely
 * contain games whose recorded play continues a move or two past an automatic termination).
 *
 * <p>
 * At {@link #CHECKMATE} and {@link #STALEMATE} the legal-move set is empty, so any further move attempt fails through
 * ordinary legality (own-piece occupation, king-into-check, etc.) — not via a dedicated game-end gate.
 *
 * <p>
 * {@link #DEAD_POSITION_INSUFFICIENT_MATERIAL} (FIDE 5.2.2 cheap detector — KK, KBK, KNK, KBKB-same-color) and
 * {@link #DEAD_POSITION_UNWINNABLE_QUICK} (analyzer-driven, Ambrona's quick check — pawn walls and similar) are both
 * queryable. {@link #DEAD_POSITION_INSUFFICIENT_MATERIAL} is the value {@code BasicChessUtility.calculateGameStatus}
 * surfaces; {@link #DEAD_POSITION_UNWINNABLE_QUICK} is <em>not</em> returned by {@code calculateGameStatus} because
 * that method intentionally avoids invoking the analyzer. Callers that want the analyzer-driven verdict invoke
 * {@link com.dlb.chess.board.Board#isDeadPositionQuick()} directly.
 *
 * <p>
 * {@link #FIVE_FOLD_REPETITION_RULE} (FIDE 9.6.1) and {@link #SEVENTY_FIVE_MOVE_RULE} (FIDE 9.6.2) are FIDE-automatic
 * draw rules. The current position itself is not necessarily drawn — mating material can still be present, pawn moves
 * and captures can still happen, and a later checkmate can still occur if play continues.
 *
 * <p>
 * The two single-side insufficient-material variants ({@link #INSUFFICIENT_MATERIAL_WHITE_ONLY},
 * {@link #INSUFFICIENT_MATERIAL_BLACK_ONLY}) are diagnostic states, not terminations: under FIDE 5.2.2 a dead position
 * requires that <em>neither</em> side can deliver checkmate. If one side still has mating material the game continues.
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
}
