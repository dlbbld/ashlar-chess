package com.dlb.chess.unwinnability;

import com.dlb.chess.board.enums.CastlingRight;
import com.dlb.chess.board.enums.Side;
import com.dlb.chess.board.enums.Square;
import com.dlb.chess.model.LegalMove;
import com.google.common.collect.ImmutableList;

/**
 * Per-ply undo snapshot for {@link HelpmateSearchBoard}'s mutable make / unmake. Mutable by construction: instances
 * are pre-allocated into a growable stack and reused — fields are overwritten on every {@code move()}, read on the
 * matching {@code unmove()}. Not a record because records cannot be mutated in place; not exposed outside this
 * package because the field shape mirrors {@link HelpmateSearchBoard}'s private representation rather than any public
 * contract.
 *
 * <p>
 * Holds the full pre-move state of {@link HelpmateSearchBoard}: the twelve piece bitboards, side to move, raw and
 * normalized en-passant target squares, castling rights for both sides, the cached legal-move list reference, and
 * the cached derived flags ({@code isCheck} / {@code isCheckmate} / {@code isStalemate}). The legal-move list is a
 * reference share — the {@link ImmutableList} from the prior {@code refreshDerivedState} call — so {@code unmove}
 * does not need to recompute it.
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

  @SuppressWarnings("null")
  ImmutableList<LegalMove> legalMoves = ImmutableList.of();
  boolean isCheck;
  boolean isCheckmate;
  boolean isStalemate;
}
