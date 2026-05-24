package com.dlb.chess.report;

import java.util.ArrayList;
import java.util.List;

import com.dlb.chess.board.Board;
import com.dlb.chess.common.model.ClaimAhead;
import com.dlb.chess.fen.model.Fen;
import com.dlb.chess.model.LegalMove;

abstract class ThreefoldClaimAheadUtility {
  public static List<List<ClaimAhead>> calculateThreefoldClaimAhead(Board board) {
    return calculateThreefoldClaimAhead(board.getPerformedLegalMoveList(), board.getInitialFen());
  }

  private static List<List<ClaimAhead>> calculateThreefoldClaimAhead(List<LegalMove> legalMoveList, Fen initialFen) {

    final List<List<ClaimAhead>> resultListList = new ArrayList<>();
    final Board board = new Board(initialFen, false);

    for (final LegalMove legalMove : legalMoveList) {
      final List<ClaimAhead> resultList = new ArrayList<>();
      board.move(legalMove.moveSpecification());
      for (final LegalMove legalMoveCheckAhead : board.getLegalMoves()) {
        board.move(legalMoveCheckAhead.moveSpecification());
        if (board.isThreefoldRepetition()) {
          resultList.add(new ClaimAhead(legalMoveCheckAhead, board.getLastPlayedFullMoveNumber(), board.getSan()));
        }
        board.unmove();
      }
      if (!resultList.isEmpty()) {
        resultListList.add(resultList);
      }
    }

    return resultListList;

  }
}
