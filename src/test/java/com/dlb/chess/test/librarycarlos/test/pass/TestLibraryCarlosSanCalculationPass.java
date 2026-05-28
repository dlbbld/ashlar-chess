package com.dlb.chess.test.librarycarlos.test.pass;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.dlb.chess.common.Nulls;
import com.dlb.chess.test.librarycarlos.NullsCarlos;
import com.github.bhlangonijr.chesslib.Board;
import com.github.bhlangonijr.chesslib.MoveBackup;
import com.github.bhlangonijr.chesslib.Square;
import com.github.bhlangonijr.chesslib.move.Move;
import com.github.bhlangonijr.chesslib.move.MoveList;

class TestLibraryCarlosSanCalculationPass {

  @SuppressWarnings("static-method")
  @Test
  void testWithoutFen() throws Exception {
    final Board board = new Board();
    board.doMove(new Move(Square.E2, Square.E4));
    assertEquals("e4", calculateSan(board)); // works fine
  }

  private static String calculateSan(Board board) {
    final MoveList moveList = new MoveList();
    moveList.addAll(calculateMoveList(board));
    final String[] sanArray = moveList.toSanArray();
    @SuppressWarnings("null") final String last = Nulls.getLast(sanArray);
    return last;
  }

  private static List<Move> calculateMoveList(Board board) {
    final List<Move> result = new ArrayList<>();
    for (final MoveBackup moveBackup : NullsCarlos.getBackup(board)) {
      result.add(NullsCarlos.getMove(moveBackup));
    }
    return result;
  }

}
