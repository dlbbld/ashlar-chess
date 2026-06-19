// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

/**
 * FEN (Forsyth-Edwards Notation) parsing, validation, and generation. The public API is split by strictness: strict
 * input goes through {@link io.github.dlbbld.ashlarchess.fen.StrictFenParser}; recovered/tolerant input goes through
 * {@link io.github.dlbbld.ashlarchess.fen.LenientFenParser}. The internal implementation still separates lexical
 * field parsing from structural and rule-consistency validation, but those stages are not public entry points.
 *
 * <ul>
 * <li>{@link io.github.dlbbld.ashlarchess.fen.StrictFenParser} - strict FEN parsing plus structural and
 * rule-consistency validation. It enforces:
 * <ul>
 * <li>exactly one king per side; pawn count &lt;= 8 per side; counts of non-pawn pieces beyond the starting set are
 * accounted for by missing pawns (promotion-consistency)</li>
 * <li>no pawns on rank 1 or rank 8</li>
 * <li>the side <em>not</em> to move is not in check (otherwise the last move would have been illegal)</li>
 * <li>castling rights consistent with king and rook static positions</li>
 * <li>en-passant target square consistent with the side to move and the adjacent pawn structure</li>
 * <li>halfmove clock consistent with the fullmove number - {@code halfMoveClock <= 2 * (fullMoveNumber - 1) +
 * (sideToMove == BLACK ? 1 : 0)}; a FEN like {@code ... 15 1} (15 halfmoves on move 1) is physically impossible. (The
 * halfmove clock itself is not capped: the 75-move rule is a queryable predicate, not enforced at FEN import, so
 * halfmove clock values at and above 150 are legitimate FEN.)</li>
 * <li>fullmove number in the supported range</li>
 * </ul>
 * This is the variant {@link io.github.dlbbld.ashlarchess.board.Board#fromFenStrict(String)} uses. It does not prove
 * full game reachability - a position passing these checks may still be unreachable from the initial position through
 * any legal sequence; structural and rule-consistency plausibility is the bar.</li>
 * <li>{@link io.github.dlbbld.ashlarchess.fen.LenientFenParser} - purely syntactic-tolerance pre-pass. Normalises
 * whitespace, casing, missing trailing counters, non-canonical castling order, non-ASCII dashes, and trailing garbage;
 * also recovers from the strict halfmove clock vs fullmove number inconsistency by auto-correcting the fullmove number
 * up to a round reserve value consistent with the halfmove clock. After normalisation, delegates to
 * {@code StrictFenParser} - strict semantic
 * invariants are unchanged. Every transform that fires surfaces as a typed
 * {@link io.github.dlbbld.ashlarchess.fen.ForgivenFenItem} on the
 * {@link io.github.dlbbld.ashlarchess.fen.LenientFenParserValidationResult}. Reached from
 * {@link io.github.dlbbld.ashlarchess.board.Board#fromFenLenient(String)}.</li>
 * </ul>
 *
 * <p>
 * The {@link io.github.dlbbld.ashlarchess.fen.model.Fen} record is the parsed result - a value object carrying the
 * static position, side to move, castling rights, en-passant capture target square, halfmove clock, and fullmove
 * number. FEN string generation is via {@link io.github.dlbbld.ashlarchess.fen.FenBoard} from a
 * {@link io.github.dlbbld.ashlarchess.board.Board}.
 */
@NonNullByDefault
package io.github.dlbbld.ashlarchess.fen;

import org.eclipse.jdt.annotation.NonNullByDefault;
