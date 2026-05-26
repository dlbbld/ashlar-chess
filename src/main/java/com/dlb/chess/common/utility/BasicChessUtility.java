package com.dlb.chess.common.utility;

import org.eclipse.jdt.annotation.Nullable;

import com.dlb.chess.board.Board;
import com.dlb.chess.board.enums.Piece;
import com.dlb.chess.board.enums.PieceType;
import com.dlb.chess.board.enums.Side;
import com.dlb.chess.common.enums.Outcome;
import com.dlb.chess.common.enums.Termination;
import com.dlb.chess.model.LegalMove;

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
   * Current-position outcome query: returns the most-specific {@link Outcome} for the given board, or {@code null}
   * when the game is ongoing.
   *
   * <p>
   * Precedence (python-chess parity): {@link Termination#CHECKMATE} → {@link Termination#INSUFFICIENT_MATERIAL} →
   * {@link Termination#STALEMATE} → {@link Termination#SEVENTY_FIVE_MOVES} → {@link Termination#FIVEFOLD_REPETITION}.
   * Returns the first matching condition under that order. The library is permissive at the move pipeline — none of
   * these block further moves; callers poll this method to decide whether a game should be adjudicated as over.
   *
   * <p>
   * Does <em>not</em> invoke any unwinnability analyzer. Callers that want the analyzer-driven dead-position verdict
   * call {@link Board#isDeadPositionQuick()} (or the full counterpart) directly. Single-side insufficient-material
   * states are diagnostic, not terminations, and are not surfaced here; callers query
   * {@link Board#isInsufficientMaterial(Side)} directly.
   */
  public static @Nullable Outcome calculateOutcome(Board board) {
    if (board.isCheckmate()) {
      // Side to move is the loser; the other side delivered mate and is the winner.
      return new Outcome(Termination.CHECKMATE, board.getHavingMove().getOppositeSide());
    }
    if (board.isInsufficientMaterial()) {
      return new Outcome(Termination.INSUFFICIENT_MATERIAL, null);
    }
    if (board.isStalemate()) {
      return new Outcome(Termination.STALEMATE, null);
    }
    if (board.isSeventyFiveMove()) {
      return new Outcome(Termination.SEVENTY_FIVE_MOVES, null);
    }
    if (board.isFivefoldRepetition()) {
      return new Outcome(Termination.FIVEFOLD_REPETITION, null);
    }
    return null;
  }

  public static boolean calculateIsResetHalfMoveClock(LegalMove legalMove) {
    return legalMove.movingPiece() != Piece.NONE && legalMove.movingPiece().getPieceType() == PieceType.PAWN
        || legalMove.pieceCaptured() != Piece.NONE;
  }
}
