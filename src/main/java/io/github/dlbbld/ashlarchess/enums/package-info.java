// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

/**
 * Pipeline-level domain enums shared across SAN and movement validation. Distinct from {@code board.enums} (which holds
 * basic chess primitives like {@link io.github.dlbbld.ashlarchess.board.enums.Side},
 * {@link io.github.dlbbld.ashlarchess.board.enums.Piece}, {@link io.github.dlbbld.ashlarchess.board.enums.Square});
 * this package holds enums that classify <em>diagnostic outcomes</em>:
 *
 * <ul>
 * <li>{@link io.github.dlbbld.ashlarchess.enums.MoveCheck} - outcome of a
 * {@link io.github.dlbbld.ashlarchess.common.model.MoveSpecification}-level legality check.</li>
 * <li>{@link io.github.dlbbld.ashlarchess.enums.MovementCheck} - outcome of a chess-rules movement check (the
 * move-shape question: does this piece move this way at all?), produced by
 * {@link io.github.dlbbld.ashlarchess.analyze.ChessRuleAnalyzer}.</li>
 * <li>{@link io.github.dlbbld.ashlarchess.enums.KingSafetyCheck} - outcome of a king-attack-after-move check.</li>
 * <li>{@link io.github.dlbbld.ashlarchess.enums.MoveSuffixAnnotation} - PGN suffix annotations ({@code !}, {@code ?},
 * {@code !!}, {@code ??}, {@code !?}, {@code ?!}).</li>
 * <li>{@link io.github.dlbbld.ashlarchess.enums.CastlingCheck} - outcome of a castling-precondition check.</li>
 * </ul>
 *
 * <p>
 * Heavy enum use is a project value: closed domains modeled as enums let the compiler enforce exhaustive {@code switch}
 * handling. See {@code specification.md} section 2.2.
 */
@NonNullByDefault
package io.github.dlbbld.ashlarchess.enums;

import org.eclipse.jdt.annotation.NonNullByDefault;
