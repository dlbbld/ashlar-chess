package com.dlb.chess.common.model;

import com.dlb.chess.bitboard.BitboardPosition;
import com.dlb.chess.board.StaticPosition;
import com.dlb.chess.board.enums.CastlingRight;
import com.dlb.chess.board.enums.Side;
import com.dlb.chess.board.enums.Square;

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
public record DynamicPosition(Side havingMove, StaticPosition staticPosition, BitboardPosition bitboardPosition,
    Square enPassantCaptureTargetSquare, CastlingRight castlingRightWhite, CastlingRight castlingRightBlack) {

  /**
   * Convenience predicate preserved for source compatibility with the previous shape of this record (which carried an
   * {@code isEnPassantCapturePossible} boolean instead of the target square). Returns {@code true} iff
   * {@link #enPassantCaptureTargetSquare()} is not {@link Square#NONE} — i.e. there is an opposing pawn that can
   * actually capture en passant in this position.
   */
  public boolean isEnPassantCapturePossible() {
    return enPassantCaptureTargetSquare != Square.NONE;
  }
}
