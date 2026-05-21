/**
 * Bitboard piece-placement representation. The single piece-placement representation that production code consumes,
 * paired with the {@code StaticPosition} mailbox representation that lives test-side (under {@code src/test/}) as
 * the permanent differential-test oracle. Every primitive in this package is asserted bit-exact against the
 * corresponding {@code StaticPosition}-based code on the full PGN/FEN corpus, on every release going forward — see
 * {@code specification.md} §4.1 and §6.1.
 *
 * <p>
 * Bit layout: little-endian rank-file. Bit {@code i} of every {@code long} corresponds to
 * {@link com.dlb.chess.board.enums.Square#ordinal()} {@code i} — {@code A1 = 0, B1 = 1, …, H8 = 63}. This is also the
 * Stockfish layout.
 *
 * <p>
 * Castling rights, en-passant target, side-to-move, and the halfmove / fullmove counters live on
 * {@link com.dlb.chess.board.Board} / {@link com.dlb.chess.common.model.DynamicPosition} and intentionally do not
 * appear on {@link com.dlb.chess.bitboard.BitboardPosition}, which carries piece placement only.
 *
 * <p>
 * See {@code tasks.md} — <em>Project invariant — the {@code StaticPosition} reference implementation is never lost</em>
 * — for the governing rule across the bitboard transition.
 */
@NonNullByDefault
package com.dlb.chess.bitboard;

import org.eclipse.jdt.annotation.NonNullByDefault;
