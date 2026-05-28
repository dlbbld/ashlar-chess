package io.github.dlbbld.ashlarchess.test.librarycarlos.test.notpass;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;

import com.github.bhlangonijr.chesslib.Board;
import com.github.bhlangonijr.chesslib.Square;
import com.github.bhlangonijr.chesslib.move.Move;

class TestLibraryCarlosPositionIdNotPass {

  @SuppressWarnings("static-method")
  @Test
  void test() throws Exception {
    final Board board = new Board();
    board.loadFromFen("6k1/8/8/8/6p1/8/5PR1/6K1 w - - 4 32");
    board.doMove(new Move(Square.F2, Square.F4));

    // ok
    assertEquals("6k1/8/8/8/5Pp1/8/6R1/6K1 b - f3 0 32", board.getFen());

    // expected "6k1/8/8/8/5Pp1/8/6R1/6K1 b - f3" but is "6k1/8/8/8/5Pp1/8/6R1/6K1 b --"
    assertNotEquals("6k1/8/8/8/5Pp1/8/6R1/6K1 b - f3", board.getPositionId());

  }

}
