/**
 * Bitboard piece-placement representation, built alongside the {@link com.dlb.chess.board.StaticPosition} mailbox
 * representation. This package is purely additive during the bitboard backend release: no production hot path is
 * switched to consume it here, and no {@code StaticPosition}-based code under {@code src/main/} is deleted or
 * relocated by this release. Every primitive added under this package is differential-tested bit-exact against the
 * corresponding {@code StaticPosition}-based code on the full PGN/FEN corpus.
 *
 * <p>
 * Bit layout: little-endian rank-file. Bit {@code i} of every {@code long} corresponds to
 * {@link com.dlb.chess.board.enums.Square#ordinal()} {@code i} — {@code A1 = 0, B1 = 1, …, H8 = 63}. This is also the
 * Stockfish layout.
 *
 * <p>
 * Castling rights, en-passant target, side-to-move, and the halfmove / fullmove counters live on
 * {@link com.dlb.chess.board.Board} / {@link com.dlb.chess.board.DynamicPosition} and intentionally do not appear on
 * {@link com.dlb.chess.bitboard.BitboardPosition}, which carries piece placement only.
 *
 * <p>
 * See {@code tasks.md} — <em>Project invariant — the {@code StaticPosition} reference implementation is never lost</em>
 * — for the governing rule across the bitboard transition.
 */
@NonNullByDefault
package com.dlb.chess.bitboard;

import org.eclipse.jdt.annotation.NonNullByDefault;
