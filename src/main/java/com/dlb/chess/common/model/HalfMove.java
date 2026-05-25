package com.dlb.chess.common.model;

import com.dlb.chess.board.HalfMoveUtility;
import com.dlb.chess.board.enums.Piece;
import com.dlb.chess.board.enums.Side;

// only use half moves in lists, and then add them in order of play.
// not used in sets per this design, so we don't need sorting.
public record HalfMove(int halfMoveCount, int fullMoveNumber, int halfMoveClock, DynamicPosition dynamicPosition,
    int countRepetition, String san, Piece movingPiece, MoveSpecification moveSpecification) {

  public Side havingMove() {
    return movingPiece.getSide();
  }

  @Override
  public String toString() {
    return HalfMoveUtility.calculateMoveNumberAndSanWithSpace(this);
  }

}
