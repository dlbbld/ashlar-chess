/**
 * Differential tests asserting that {@link com.dlb.chess.bitboard.BitboardPosition} agrees bit-exact with
 * {@link com.dlb.chess.board.StaticPosition} across the full PGN/FEN corpus, primitive by primitive.
 *
 * <p>
 * Tests under this package iterate the corpus via {@link com.dlb.chess.test.pgn.setup.PgnTestCaseCatalog#getTestList}
 * and run both representations through the primitive under test, asserting agreement. Pure-unit-shaped bitboard tests
 * (no corpus walk) live alongside their subject under {@code com.dlb.chess.bitboard.*} instead.
 */
@NonNullByDefault
package com.dlb.chess.test.bitboard;

import org.eclipse.jdt.annotation.NonNullByDefault;
