/**
 * Pipeline-level domain enums shared across SAN and movement validation. Distinct from {@code board.enums} (which holds
 * basic chess primitives like {@link com.dlb.chess.board.enums.Side}, {@link com.dlb.chess.board.enums.Piece},
 * {@link com.dlb.chess.board.enums.Square}); this package holds enums that classify <em>diagnostic outcomes</em>:
 *
 * <ul>
 * <li>{@link com.dlb.chess.enums.MoveCheck} - outcome of a {@link com.dlb.chess.common.model.MoveSpecification}-level
 * legality check.</li>
 * <li>{@link com.dlb.chess.enums.MovementCheck} - outcome of a chess-rules movement check (the move-shape question:
 * does this piece move this way at all?), produced by {@link com.dlb.chess.analyze.ChessRuleAnalyzer}.</li>
 * <li>{@link com.dlb.chess.enums.KingSafetyCheck} - outcome of a king-attack-after-move check.</li>
 * <li>{@link com.dlb.chess.enums.MoveSuffixAnnotation} - PGN suffix annotations ({@code !}, {@code ?}, {@code !!},
 * {@code ??}, {@code !?}, {@code ?!}).</li>
 * <li>{@link com.dlb.chess.enums.SquareOccupation} - three-valued square occupancy (empty / own piece / opponent piece)
 * relative to a side.</li>
 * <li>{@link com.dlb.chess.enums.CastlingCheck} - outcome of a castling-precondition check.</li>
 * </ul>
 *
 * <p>
 * Heavy enum use is a project value: closed domains modeled as enums let the compiler enforce exhaustive {@code switch}
 * handling. See {@code specification.md} section 2.2.
 */
@NonNullByDefault
package com.dlb.chess.enums;

import org.eclipse.jdt.annotation.NonNullByDefault;
