package com.dlb.chess.test.fen.roundtrip;

import java.util.List;

import com.dlb.chess.board.Board;
import com.dlb.chess.board.CommonTestUtility;
import com.dlb.chess.common.Nulls;
import com.dlb.chess.common.constants.EnumConstants;
import com.dlb.chess.common.model.MoveSpecification;

public abstract class AbstractTestFenRoundtrip implements EnumConstants {
  public static void checFenRoundtrip(String initialFen, List<MoveSpecification> moveList) {

    final Board boardPlayMoves = new Board(initialFen);

    Board previousBoardFromFen = null;
    for (var i = 0; i < moveList.size(); i++) {
      final MoveSpecification move = Nulls.get(moveList, i);
      boardPlayMoves.move(move);
      if (previousBoardFromFen != null) {
        // testing fen plus played move equals played move
        previousBoardFromFen.move(move);
        CommonTestUtility.checkBoardsAgainstEachOtherExcludeHistory(boardPlayMoves, previousBoardFromFen);
      }

      final String boardFen = boardPlayMoves.getFen();
      final Board boardFromFen = new Board(boardFen);
      previousBoardFromFen = boardFromFen;

      // testing board plus played equals board after played move fen
      CommonTestUtility.checkBoardsAgainstEachOtherExcludeHistory(boardPlayMoves, boardFromFen);
    }
  }
}
