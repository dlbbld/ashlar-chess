// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.test.fen.roundtrip;

import java.util.List;

import io.github.dlbbld.ashlarchess.board.Board;
import io.github.dlbbld.ashlarchess.board.CommonTestUtility;
import io.github.dlbbld.ashlarchess.common.Nulls;
import io.github.dlbbld.ashlarchess.board.MoveSpecification;

public final class TestFenRoundtripSupport {

  private TestFenRoundtripSupport() {
  }

  static void checkFenRoundtrip(String initialFen, List<MoveSpecification> moves) {

    final Board boardPlayMoves = Board.fromFenStrict(initialFen);

    Board previousBoardFromFen = null;
    for (int i = 0; i < moves.size(); i++) {
      final MoveSpecification move = Nulls.get(moves, i);
      boardPlayMoves.move(move);
      if (previousBoardFromFen != null) {
        // testing fen plus played move equals played move
        previousBoardFromFen.move(move);
        CommonTestUtility.checkBoardsAgainstEachOtherExcludeHistory(boardPlayMoves, previousBoardFromFen);
      }

      final String boardFen = boardPlayMoves.getFen();
      final Board boardFromFen = Board.fromFenStrict(boardFen);
      previousBoardFromFen = boardFromFen;

      // testing board plus played equals board after played move fen
      CommonTestUtility.checkBoardsAgainstEachOtherExcludeHistory(boardPlayMoves, boardFromFen);
    }
  }
}
