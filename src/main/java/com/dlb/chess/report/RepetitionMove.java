package com.dlb.chess.report;

import com.dlb.chess.common.model.HalfMove;

record RepetitionMove(int totalRepetitionCount, HalfMove halfMove) implements Comparable<RepetitionMove> {

  @Override
  public int compareTo(RepetitionMove o) {
    return Integer.compare(this.halfMove.halfMoveCount(), o.halfMove.halfMoveCount());
  }

}
