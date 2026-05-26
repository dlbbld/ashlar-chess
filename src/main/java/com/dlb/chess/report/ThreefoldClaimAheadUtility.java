package com.dlb.chess.report;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.dlb.chess.board.Board;
import com.dlb.chess.common.HalfMoveListListComparator;
import com.dlb.chess.common.Nulls;
import com.dlb.chess.common.model.HalfMove;
import com.dlb.chess.fen.model.Fen;
import com.dlb.chess.model.LegalMove;

abstract class ThreefoldClaimAheadUtility {
  public static List<List<HalfMove>> calculateClaimAheadListList(Board board) {
    return calculateThreefoldHalfMove(board.getPerformedLegalMoveList(), board.getInitialFen());
  }

  private static List<List<HalfMove>> calculateThreefoldHalfMove(List<LegalMove> legalMoveList, Fen initialFen) {

    final List<List<HalfMove>> resultListList = new ArrayList<>();
    final Board board = new Board(initialFen);

    for (final LegalMove legalMove : legalMoveList) {
      collectClaimAheadList(resultListList, board);
      board.move(legalMove.moveSpecification());
    }
    collectClaimAheadList(resultListList, board);
    Collections.sort(resultListList, HalfMoveListListComparator.COMPARATOR);
    return resultListList;
  }

  private static void collectClaimAheadList(List<List<HalfMove>> resultListList, Board board) {
    final List<HalfMove> resultList = new ArrayList<>();
    for (final LegalMove legalMoveCheckAhead : board.getLegalMoves()) {
      board.move(legalMoveCheckAhead.moveSpecification());
      if (board.isThreefoldRepetition()) {
        resultList.add(Nulls.getLast(board.getHalfMoveList()));
      }
      board.unmove();
    }
    if (!resultList.isEmpty()) {
      resultListList.add(resultList);
    }
  }
}
