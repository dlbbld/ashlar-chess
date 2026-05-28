package io.github.dlbbld.ashlarchess.test.librarycarlos.test.notpass;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.github.bhlangonijr.chesslib.Board;
import com.github.bhlangonijr.chesslib.MoveBackup;
import com.github.bhlangonijr.chesslib.Square;
import com.github.bhlangonijr.chesslib.move.Move;
import com.github.bhlangonijr.chesslib.move.MoveList;

import io.github.dlbbld.ashlarchess.common.Nulls;
import io.github.dlbbld.ashlarchess.test.librarycarlos.NullsCarlos;

class TestLibraryCarlosSanCalculationNotPass {

  @SuppressWarnings("static-method")
  @Test
  void testWithFen() throws Exception {
    final Board board = new Board();
    final String fen = "8/8/3KP3/5P2/8/3p4/3kp3/8 w - - 0 100";
    board.loadFromFen(fen);

    board.doMove(new Move(Square.E6, Square.E5));

    boolean isException;
    try {
      // expected e5 but exception
      assertEquals("e5", calculateSan(board));
      isException = false;
    } catch (@SuppressWarnings("unused") final NullPointerException npe) {
      isException = true;
    }
    // expected no exception but is exception
    assertTrue(isException);
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
