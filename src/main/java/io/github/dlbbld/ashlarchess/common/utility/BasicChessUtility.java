// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.common.utility;

import io.github.dlbbld.ashlarchess.board.Board;
import io.github.dlbbld.ashlarchess.board.enums.Piece;
import io.github.dlbbld.ashlarchess.board.enums.PieceType;
import io.github.dlbbld.ashlarchess.board.enums.Side;
import io.github.dlbbld.ashlarchess.common.enums.Termination;
import io.github.dlbbld.ashlarchess.common.model.Outcome;
import io.github.dlbbld.ashlarchess.model.LegalMove;

public abstract class BasicChessUtility {

  public static Side calculateSideMoved(Side havingMoveInitial, int halfMoveCount) {
    switch (havingMoveInitial) {
      case BLACK:
        if (halfMoveCount % 2 == 0) {
          return Side.WHITE;
        }
        return Side.BLACK;
      case WHITE:
        if (halfMoveCount % 2 == 0) {
          return Side.BLACK;
        }
        return Side.WHITE;
      case NONE:
      default:
        throw new IllegalArgumentException();
    }

  }

  public static int calculateFullMoveNumber(Side havingMoveInitial, int fullMoveNumberInitial,
      int performedHalfMoveCount) {
    return switch (havingMoveInitial) {
      case BLACK -> fullMoveNumberInitial + (int) StrictMath.floor(performedHalfMoveCount / 2.0);
      case WHITE -> fullMoveNumberInitial + (int) StrictMath.floor((performedHalfMoveCount - 1) / 2.0);
      case NONE -> throw new IllegalArgumentException();
      default -> throw new IllegalArgumentException();
    };
  }

  /**
   * Current-position outcome query: returns the most-specific {@link Outcome} for the given board. Returns
   * {@link Outcome#ONGOING} (the singleton with {@code termination == Termination.NONE}) when no termination condition
   * fires, so the return value is never {@code null}.
   *
   * <p>
   * Precedence (python-chess parity): {@link Termination#CHECKMATE} -> {@link Termination#INSUFFICIENT_MATERIAL} ->
   * {@link Termination#STALEMATE} -> {@link Termination#SEVENTY_FIVE_MOVES} -> {@link Termination#FIVEFOLD_REPETITION}.
   * Returns the first matching condition under that order. The library is permissive at the move pipeline - none of
   * these block further moves; callers poll this method to decide whether a game should be adjudicated as over.
   *
   * <p>
   * Does <em>not</em> invoke any unwinnability analyzer. Callers that want the analyzer-driven dead-position verdict
   * call the no-side overload of the quick or full unwinnability analyzer directly. Single-side insufficient-material
   * states are diagnostic, not terminations, and are not surfaced here; callers query
   * {@link Board#isInsufficientMaterial(Side)} directly.
   */
  public static Outcome calculateOutcome(Board board) {
    if (board.isCheckmate()) {
      // Side to move is the loser; the other side delivered mate and is the winner.
      return new Outcome(Termination.CHECKMATE, board.getHavingMove().getOppositeSide());
    }
    if (board.isInsufficientMaterial()) {
      return new Outcome(Termination.INSUFFICIENT_MATERIAL, Side.NONE);
    }
    if (board.isStalemate()) {
      return new Outcome(Termination.STALEMATE, Side.NONE);
    }
    if (board.isSeventyFiveMove()) {
      return new Outcome(Termination.SEVENTY_FIVE_MOVES, Side.NONE);
    }
    if (board.isFivefoldRepetition()) {
      return new Outcome(Termination.FIVEFOLD_REPETITION, Side.NONE);
    }
    return Outcome.ONGOING;
  }

  public static boolean calculateIsResetHalfMoveClock(LegalMove legalMove) {
    return legalMove.movingPiece() != Piece.NONE && legalMove.movingPiece().getPieceType() == PieceType.PAWN
        || legalMove.pieceCaptured() != Piece.NONE;
  }
}
