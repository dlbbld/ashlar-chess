package io.github.dlbbld.ashlarchess.common.utility;

import io.github.dlbbld.ashlarchess.board.Board;
import io.github.dlbbld.ashlarchess.report.CheckmateOrStalemate;

public abstract class BoardUtility {

  public static CheckmateOrStalemate calculateEvaluation(Board board) {
    // order is crucial
    if (board.isCheckmate()) {
      return CheckmateOrStalemate.CHECKMATE;
    }
    if (board.isStalemate()) {
      return CheckmateOrStalemate.STALEMATE;
    }
    return CheckmateOrStalemate.NA;
  }

}
