/**
 * Board representation and the strict move-validation pipeline.
 *
 * <h2>Termination is queryable, not enforced</h2>
 *
 * <p>
 * A {@link com.dlb.chess.board.Board} represents a <em>game</em> — a position together with its move history — not
 * merely a position. The move-validation pipeline does <em>not</em> gate on game-end states: neither
 * {@link com.dlb.chess.board.ValidateNewMove#validateNewMove} (MoveSpecification pipeline) nor
 * {@code com.dlb.chess.san.StrictSanParser#parseText} (SAN pipeline) consults any termination predicate. At checkmate
 * and stalemate the natural barrier is the empty legal-move set: any attempted move fails through ordinary legality
 * (own-piece occupation, king-into-check, etc.). At mutual insufficient material, fivefold repetition, the 75-move
 * rule, and analyzer-driven dead positions, legal moves still exist and the pipeline accepts them.
 *
 * <p>
 * Callers poll {@link com.dlb.chess.common.utility.BasicChessUtility#calculateOutcome} for the current-position
 * outcome — it returns the most-specific {@link com.dlb.chess.common.model.Outcome} for the board, or {@code null}
 * when the game is ongoing. The five surfaced terminations are checkmate, mutual insufficient material, stalemate,
 * 75-move, and fivefold (in that precedence order; python-chess parity). The library is permissive at the move
 * pipeline for corpus and tooling compatibility (historical PGN databases routinely contain games whose recorded
 * play continues a move or two past an automatic termination); the caller decides whether to adjudicate.
 *
 * <p>
 * Single-side insufficient material (one side lacks mating material but the other does not) is a diagnostic
 * position state and is not surfaced by {@code calculateOutcome}. Callers that need it query
 * {@link com.dlb.chess.board.Board#isInsufficientMaterial(com.dlb.chess.board.enums.Side)} directly.
 *
 * <p>
 * Analyzer-driven dead positions (FIDE 5.2.2 via the quick or full unwinnability analyzer) are <em>not</em> surfaced
 * via {@code calculateOutcome} — invoking the analyzer from that method would silently make every status query
 * expensive. Callers that want the analyzer-driven verdict invoke
 * {@link com.dlb.chess.board.Board#isDeadPositionQuick()} or {@link com.dlb.chess.board.Board#isDeadPositionFull()}
 * directly.
 *
 * <p>
 * The claimable draws (FIDE 9.2 3-fold, FIDE 9.3 50-move) are intentionally not surfaced by
 * {@code calculateOutcome}: a player may decline to claim and continue playing. They remain queryable on the board
 * via the dedicated {@code canClaim*} predicates.
 */
@NonNullByDefault
package com.dlb.chess.board;

import org.eclipse.jdt.annotation.NonNullByDefault;
