package com.dlb.chess.test.unwinnability.lichess.pgn;

import com.dlb.chess.board.Board;
import com.dlb.chess.board.enums.Side;
import com.dlb.chess.common.exceptions.ProgrammingMistakeException;
import com.dlb.chess.pgn.PgnGame;
import com.dlb.chess.pgn.PgnUtility;
import com.dlb.chess.pgn.ResultTagValue;
import com.dlb.chess.pgn.TagUtility;
import com.dlb.chess.test.unwinnability.oracle.LimitedUnwinnabilityOracle;
import com.dlb.chess.test.unwinnability.oracle.enums.LimitedUnwinnabilityVerdict;

public abstract class AbstractLichessCheck {

  static boolean calculateIsTimeForfeitCandidate(PgnGame pgnGame) {
    final String terminationValue = TagUtility.calculateTagValue(pgnGame, "Termination");
    if (!"Time forfeit".equals(terminationValue)) {
      return false;
    }
    final ResultTagValue resultTagValue = TagUtility.readResultTagValue(pgnGame);
    return resultTagValue == ResultTagValue.WHITE_WON || resultTagValue == ResultTagValue.BLACK_WON;
  }

  static boolean calculateIsIncorrectResult(PgnGame pgnGame) {

    final Board boardPerLastMove = PgnUtility.calculateBoard(pgnGame);
    final ResultTagValue resultTagValue = TagUtility.readResultTagValue(pgnGame);

    if (boardPerLastMove.getHavingMove() == Side.WHITE && resultTagValue == ResultTagValue.WHITE_WON
        || boardPerLastMove.getHavingMove() == Side.BLACK && resultTagValue == ResultTagValue.BLACK_WON) {
      throw new ProgrammingMistakeException("Should not happen");
    }

    final LimitedUnwinnabilityVerdict verdict = LimitedUnwinnabilityOracle.calculateUnwinnability(boardPerLastMove,
        boardPerLastMove.getHavingMove().getOppositeSide());
    return verdict == LimitedUnwinnabilityVerdict.UNWINNABLE;
  }
}
