// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.common.model;

import io.github.dlbbld.ashlarchess.bitboard.BitboardPosition;
import io.github.dlbbld.ashlarchess.board.enums.CastlingRight;
import io.github.dlbbld.ashlarchess.board.enums.Side;
import io.github.dlbbld.ashlarchess.board.enums.Square;

/**
 * The position-equivalence record used for in-game threefold-repetition equality and as the transposition key for the
 * helpmate search.
 *
 * <p>
 * The {@code enPassantCaptureTargetSquare} component is <strong>normalized</strong>: it holds the e.p. target square
 * only when an opposing pawn can <em>actually</em> capture there (king-safety considered); otherwise it is
 * {@link Square#NONE}. This matches the FIDE rule for position identity (a pawn that "could have been captured en
 * passant"). The raw FEN-spec e.p. target square (which is reported after any pawn double-step regardless of
 * capturability) lives on {@code Board} separately and is used for FEN export.
 */
public record DynamicPosition(Side havingMove, BitboardPosition bitboardPosition, Square enPassantCaptureTargetSquare,
    CastlingRight castlingRightWhite, CastlingRight castlingRightBlack) {
}
