/**
 * Cross-cutting model types - small immutable value objects (records) used across the rule pipelines.
 *
 * <ul>
 * <li>{@link com.dlb.chess.model.LegalMove} - a {@link com.dlb.chess.common.model.MoveSpecification} plus the moving
 * piece, captured piece (if any), and the {@link com.dlb.chess.model.LegalMoveKind} category. Returned by the
 * legal-move generator.</li>
 * <li>{@link com.dlb.chess.model.LegalMoveKind} - categorises a legal move (normal, castling, en-passant capture, pawn
 * two-square advance, promotion).</li>
 * <li>{@link com.dlb.chess.model.PgnHalfMove} - a SAN string plus its move-suffix-annotation and
 * {@link com.dlb.chess.pgn.PgnCommentary}. The unit of PGN movetext.</li>
 * <li>{@link com.dlb.chess.model.UciMove} - a UCI move string with the convenience accessors.</li>
 * <li>{@link com.dlb.chess.model.CastlingRightBoth} - small record used in move execution.</li>
 * </ul>
 *
 * <p>
 * Records are constructed by parsers, generators, and the legal-move pipeline; once constructed, instances are
 * immutable. See {@code specification.md} section 2.2 for the records-as-value-objects design.
 */
@NonNullByDefault
package com.dlb.chess.model;

import org.eclipse.jdt.annotation.NonNullByDefault;
