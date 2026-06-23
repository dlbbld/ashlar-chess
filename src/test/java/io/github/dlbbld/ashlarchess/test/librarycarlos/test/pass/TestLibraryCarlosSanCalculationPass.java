// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.test.librarycarlos.test.pass;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.github.bhlangonijr.chesslib.Board;
import com.github.bhlangonijr.chesslib.MoveBackup;
import com.github.bhlangonijr.chesslib.Square;
import com.github.bhlangonijr.chesslib.move.Move;
import com.github.bhlangonijr.chesslib.move.MoveList;

import io.github.dlbbld.ashlarchess.internal.Nulls;
import io.github.dlbbld.ashlarchess.test.librarycarlos.NullsCarlos;

class TestLibraryCarlosSanCalculationPass {

  @SuppressWarnings("static-method")
  @Test
  void testWithoutFen() {
    final Board board = new Board();
    board.doMove(new Move(Square.E2, Square.E4));
    assertEquals("e4", calculateSan(board)); // works fine
  }

  private static String calculateSan(Board board) {
    final MoveList moves = new MoveList();
    moves.addAll(calculateMoves(board));
    final String[] sanArray = moves.toSanArray();
    @SuppressWarnings("null") final String last = Nulls.getLast(sanArray);
    return last;
  }

  private static List<Move> calculateMoves(Board board) {
    final List<Move> result = new ArrayList<>();
    for (final MoveBackup moveBackup : NullsCarlos.getBackup(board)) {
      result.add(NullsCarlos.getMove(moveBackup));
    }
    return result;
  }

}
