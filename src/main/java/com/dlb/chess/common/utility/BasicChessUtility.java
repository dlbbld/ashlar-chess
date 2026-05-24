package com.dlb.chess.common.utility;

import com.dlb.chess.board.Board;
import com.dlb.chess.board.enums.Piece;
import com.dlb.chess.board.enums.PieceType;
import com.dlb.chess.board.enums.Side;
import com.dlb.chess.common.enums.GameStatus;
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
   * Returns the single most-specific {@link GameStatus} for the given board, with move-blocking statuses checked before
   * cheap queryable draw predicates. Does <em>not</em> invoke any unwinnability analyzer:
   * {@link GameStatus#DEAD_POSITION_UNWINNABLE_QUICK} is never returned. Callers that want the analyzer-driven
   * dead-position verdict must call {@link Board#isDeadPositionQuick()} (or the full counterpart) directly.
   */
  public static GameStatus calculateGameStatus(Board board) {

    final GameStatus moveBlockingStatus = calculateMoveBlockingGameStatus(board);
    if (moveBlockingStatus != GameStatus.ONGOING) {
      return moveBlockingStatus;
    }
    if (board.isFivefoldRepetition()) {
      return GameStatus.FIVE_FOLD_REPETITION_RULE;
    }
    if (board.isSeventyFiveMove()) {
      return GameStatus.SEVENTY_FIVE_MOVE_RULE;
    }
    if (board.isInsufficientMaterial(Side.WHITE)) {
      return GameStatus.INSUFFICIENT_MATERIAL_WHITE_ONLY;
    }
    if (board.isInsufficientMaterial(Side.BLACK)) {
      return GameStatus.INSUFFICIENT_MATERIAL_BLACK_ONLY;
    }
    return GameStatus.ONGOING;
  }

  /**
   * Returns only statuses that may block further move execution. This method deliberately avoids analyzer-driven
   * dead-position queries, fivefold, and 75-move so the validation pipeline stays cheap and non-recursive.
   */
  public static GameStatus calculateMoveBlockingGameStatus(Board board) {

    if (board.isCheckmate()) {
      return GameStatus.CHECKMATE;
    }
    if (board.isStalemate()) {
      return GameStatus.STALEMATE;
    }
    if (board.isInsufficientMaterial()) {
      return GameStatus.DEAD_POSITION_INSUFFICIENT_MATERIAL;
    }
    return GameStatus.ONGOING;
  }

  public static boolean calculateIsResetHalfMoveClock(LegalMove legalMove) {
    return legalMove.movingPiece() != Piece.NONE && legalMove.movingPiece().getPieceType() == PieceType.PAWN
        || legalMove.pieceCaptured() != Piece.NONE;
  }
}
