/**
 * Board representation and the strict move-validation pipeline.
 *
 * <h2>The strict-game invariant</h2>
 *
 * <p>
 * A {@link com.dlb.chess.board.Board} represents a <em>game</em> — a position together with its move history — not
 * merely a position. Once any move-blocking termination has been reached, the game has ended permanently and no further
 * moves are accepted by either pipeline:
 *
 * <ul>
 * <li>{@link com.dlb.chess.board.ValidateNewMove#validateNewMove} (MoveSpecification pipeline)</li>
 * <li>{@code com.dlb.chess.san.StrictSanParser#parseText} (SAN pipeline)</li>
 * </ul>
 *
 * <p>
 * The three terminations enforced at the move pipeline (queryable via
 * {@link com.dlb.chess.common.utility.BasicChessUtility#calculateGameStatus}) are:
 *
 * <ul>
 * <li>{@link com.dlb.chess.common.enums.GameStatus#CHECKMATE} (FIDE 5.1.1)</li>
 * <li>{@link com.dlb.chess.common.enums.GameStatus#STALEMATE} (FIDE 5.2.1)</li>
 * <li>{@link com.dlb.chess.common.enums.GameStatus#DEAD_POSITION_INSUFFICIENT_MATERIAL} — FIDE 5.2.2 dead position by
 * mutual insufficient material (the single-side {@code INSUFFICIENT_MATERIAL_*_ONLY} variants are diagnostic states,
 * NOT terminations)</li>
 * </ul>
 *
 * <p>
 * An attempt to move on a terminal-state board surfaces as {@link com.dlb.chess.exceptions.InvalidMoveException} with
 * {@link com.dlb.chess.enums.MoveCheck#GAME_ALREADY_ENDED} and the originating
 * {@link com.dlb.chess.common.enums.GameStatus} as payload (or, on the SAN pipeline, the mirrored
 * {@code SanValidationException} with {@code SanValidationProblem.GAME_ALREADY_ENDED}).
 *
 * <p>
 * {@link com.dlb.chess.common.enums.GameStatus#FIVE_FOLD_REPETITION_RULE} (FIDE 9.6.1),
 * {@link com.dlb.chess.common.enums.GameStatus#SEVENTY_FIVE_MOVE_RULE} (FIDE 9.6.2), and
 * {@link com.dlb.chess.common.enums.GameStatus#DEAD_POSITION_UNWINNABLE_QUICK} (FIDE 5.2.2 via the quick analyzer) are
 * surfaced as <em>queryable predicates</em> rather than enforced at the move pipeline. The library is permissive here
 * for corpus and tooling compatibility; the caller decides whether to adjudicate. The
 * {@link com.dlb.chess.common.enums.GameStatus} values remain available via {@code calculateGameStatus} as diagnostic
 * answers, with move-blocking statuses taking precedence when both apply to the same position.
 *
 * <p>
 * The claimable draws — {@link com.dlb.chess.common.enums.GameStatus FIDE 9.2 (3-fold) and 9.3 (50-move)} — are
 * intentionally NOT enforced here: a player may decline to claim and continue playing. They remain queryable on the
 * board.
 */
@NonNullByDefault
package com.dlb.chess.board;

import org.eclipse.jdt.annotation.NonNullByDefault;
