package io.github.dlbbld.ashlarchess.test.librarycarlos.test.notpass;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;

import com.github.bhlangonijr.chesslib.Board;
import com.github.bhlangonijr.chesslib.Square;

class TestLibraryCarlosEnPassantCaptureNotPass {

  @SuppressWarnings("static-method")
  @Test
  void test() throws Exception {
    final Board board = new Board();
    board.doMove("e4");
    board.doMove("Nf6");
    board.doMove("e5");
    board.doMove("d5");

    // expected D6 but results is D5
    assertNotEquals(Square.D6, board.getEnPassantTarget());

    // expected D6 but is NONE
    assertNotEquals(Square.D6, board.getBackup().getLast().getEnPassantTarget());

    // expected true but is false
    board.doMove("exd6");
    assertFalse(board.getBackup().getLast().isEnPassantMove());
  }

}
