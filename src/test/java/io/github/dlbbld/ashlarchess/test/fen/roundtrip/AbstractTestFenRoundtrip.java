// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.test.fen.roundtrip;

import java.util.List;

import io.github.dlbbld.ashlarchess.board.Board;
import io.github.dlbbld.ashlarchess.board.CommonTestUtility;
import io.github.dlbbld.ashlarchess.common.Nulls;
import io.github.dlbbld.ashlarchess.common.constants.EnumConstants;
import io.github.dlbbld.ashlarchess.common.model.MoveSpecification;

public abstract class AbstractTestFenRoundtrip implements EnumConstants {
  public static void checFenRoundtrip(String initialFen, List<MoveSpecification> moveList) {

    final Board boardPlayMoves = new Board(initialFen);

    Board previousBoardFromFen = null;
    for (int i = 0; i < moveList.size(); i++) {
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
