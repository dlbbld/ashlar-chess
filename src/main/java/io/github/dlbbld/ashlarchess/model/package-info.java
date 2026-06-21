// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

/**
 * Cross-cutting model types - small immutable value objects (records) used across the rule pipelines.
 *
 * <ul>
 * <li>{@link io.github.dlbbld.ashlarchess.model.LegalMove} - a
 * {@link io.github.dlbbld.ashlarchess.common.model.MoveSpecification} plus the moving piece, captured piece (if any),
 * and the {@link io.github.dlbbld.ashlarchess.model.LegalMoveKind} category. Returned by the legal-move generator.</li>
 * <li>{@link io.github.dlbbld.ashlarchess.model.LegalMoveKind} - categorises a legal move (normal, castling, en-passant
 * capture, pawn two-square advance, promotion).</li>
 * <li>{@link io.github.dlbbld.ashlarchess.model.PgnMove} - a SAN string plus its move-suffix-annotation and
 * {@link io.github.dlbbld.ashlarchess.pgn.PgnCommentary}. The unit of PGN movetext.</li>
 * <li>{@link io.github.dlbbld.ashlarchess.model.UciMove} - a UCI move string with the convenience accessors.</li>
 * <li>{@link io.github.dlbbld.ashlarchess.model.CastlingRightBoth} - small record used in move execution.</li>
 * </ul>
 *
 * <p>
 * Records are constructed by parsers, generators, and the legal-move pipeline; once constructed, instances are
 * immutable. See {@code specification.md} section 2.2 for the records-as-value-objects design.
 */
@NonNullByDefault
package io.github.dlbbld.ashlarchess.model;

import org.eclipse.jdt.annotation.NonNullByDefault;
