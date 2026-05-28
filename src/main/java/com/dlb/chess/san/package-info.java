/**
 * SAN (Standard Algebraic Notation) parsing, validation, and generation. Validates a candidate SAN string against the
 * current legal-move set, with diagnostic-quality error messages classifying every failure mode (wrong file, wrong
 * rank, missing disambiguation, illegal capture target, exposed king, missing/wrong check-or-checkmate suffix, and so
 * on - see {@link com.dlb.chess.san.SanValidationProblem}).
 *
 * <p>
 * Two parser entry points sit on top of a shared validation core:
 * <ul>
 * <li>{@link com.dlb.chess.san.StrictSanParser#parseText(String, com.dlb.chess.board.Board)} - canonical SAN only.
 * Reached from {@link com.dlb.chess.board.Board#moveStrict(String)}.
 * <li>{@link com.dlb.chess.san.LenientSanParser#parseText(String, com.dlb.chess.board.Board)} - accepts a defined set
 * of forgivable deviations from canonical SAN. Reached from {@link com.dlb.chess.board.Board#moveLenient(String)}. See
 * {@code specification.md} section 3.3.1 for the taxonomy.
 * </ul>
 *
 * <p>
 * Generation goes the other direction: {@link com.dlb.chess.san.MoveToSan} produces canonical SAN for a played move
 * (with minimal disambiguation and the correct check/checkmate suffix); {@link com.dlb.chess.san.MoveToLan} produces
 * long algebraic notation.
 *
 * <p>
 * Format-level checks and movement-level (legal-move, king-safety) checks both live in this package.
 */
@NonNullByDefault
package com.dlb.chess.san;

import org.eclipse.jdt.annotation.NonNullByDefault;
