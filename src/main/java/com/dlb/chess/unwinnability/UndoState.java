package com.dlb.chess.unwinnability;

import com.dlb.chess.board.enums.CastlingRight;
import com.dlb.chess.board.enums.Side;
import com.dlb.chess.board.enums.Square;

/**
 * Per-ply undo snapshot for {@link HelpmateSearchBoard}'s mutable make / unmake. Mutable by construction: instances
 * are pre-allocated into a growable stack and reused — fields are overwritten on every {@code move()}, read on the
 * matching {@code unmove()}. Not a record because records cannot be mutated in place; not exposed outside this
 * package because the field shape mirrors {@link HelpmateSearchBoard}'s private representation rather than any public
 * contract.
 *
 * <p>
 * Holds the full pre-move state of {@link HelpmateSearchBoard}: the twelve piece bitboards, side to move, raw and
 * normalized en-passant target squares, castling rights for both sides, and the cached derived flags
 * ({@code isCheck} / {@code isCheckmate} / {@code isStalemate}). The legal-move buffer is NOT saved here — Phase C's
 * per-depth {@link LegalMoveBuffer}s mean the buffer at depth N is preserved untouched across recursion into depth
 * N+1, so restoring it on unmove is a no-op (the buffer's contents at depth N are still the depth-N legal moves).
 */
final class UndoState {

  long whitePawns;
  long whiteRooks;
  long whiteKnights;
  long whiteBishops;
  long whiteQueens;
  long whiteKings;
  long blackPawns;
  long blackRooks;
  long blackKnights;
  long blackBishops;
  long blackQueens;
  long blackKings;

  Side havingMove = Side.WHITE;
  Square enPassantCaptureTargetSquare = Square.NONE;
  Square normalizedEnPassantCaptureTargetSquare = Square.NONE;
  CastlingRight castlingRightWhite = CastlingRight.NONE;
  CastlingRight castlingRightBlack = CastlingRight.NONE;

  boolean isCheck;
  boolean isCheckmate;
  boolean isStalemate;
}
