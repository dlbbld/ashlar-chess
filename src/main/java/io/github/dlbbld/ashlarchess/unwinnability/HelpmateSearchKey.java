package io.github.dlbbld.ashlarchess.unwinnability;

import io.github.dlbbld.ashlarchess.board.enums.CastlingRight;
import io.github.dlbbld.ashlarchess.board.enums.Side;
import io.github.dlbbld.ashlarchess.board.enums.Square;
import io.github.dlbbld.ashlarchess.common.model.DynamicPosition;

/**
 * Exact structural transposition-cache key for {@link FindHelpmateExhaust}'s search, built directly from
 * {@link HelpmateSearchBoard}'s mutable piece bitboards and per-move auxiliary state. Equivalent in semantics to
 * {@link DynamicPosition} - same fields, same equality contract - but constructed without the nested
 * {@link io.github.dlbbld.ashlarchess.bitboard.BitboardPosition} record allocation that {@code DynamicPosition} carries: the twelve
 * piece bitboards are inlined as record components so that {@link HelpmateSearchBoard#currentTranspositionKey()} costs
 * exactly one record allocation per cache touch.
 *
 * <p>
 * <strong>Fields and semantics:</strong>
 * <ul>
 * <li>{@code havingMove} - side to move.</li>
 * <li>{@code whitePawns}...{@code blackKings} - the twelve piece bitboards (same field order as
 * {@link io.github.dlbbld.ashlarchess.bitboard.BitboardPosition}).</li>
 * <li>{@code normalizedEnPassantCaptureTargetSquare} - the FIDE-position-identity EP target (set only when an opposing
 * pawn can actually capture there with king-safety considered; otherwise {@link Square#NONE}). Matches the
 * {@code enPassantCaptureTargetSquare} component of {@link DynamicPosition}. The <em>raw</em> EP target square is
 * <strong>not</strong> part of the key - two positions that normalize to the same NONE collapse to one cache entry,
 * which matches the FIDE rule for position identity.</li>
 * <li>{@code castlingRightWhite} / {@code castlingRightBlack} - castling rights for each side.</li>
 * </ul>
 *
 * <p>
 * <strong>Explicitly NOT part of the key:</strong> raw EP target, halfmove / fullmove counters, cached check flags
 * ({@code isCheck} / {@code isCheckmate} / {@code isStalemate}), legal-move buffer contents, or any derived /
 * search-local state. The key is over the search node's <em>inputs</em>, not its outputs.
 *
 * <p>
 * Package-private and constructed in exactly one place ({@link HelpmateSearchBoard#currentTranspositionKey()}); no
 * compact-constructor validation - the search board owns the invariants that would be checked there.
 */
record HelpmateSearchKey(Side havingMove, long whitePawns, long whiteRooks, long whiteKnights, long whiteBishops,
    long whiteQueens, long whiteKings, long blackPawns, long blackRooks, long blackKnights, long blackBishops,
    long blackQueens, long blackKings, Square normalizedEnPassantCaptureTargetSquare, CastlingRight castlingRightWhite,
    CastlingRight castlingRightBlack) {
}
