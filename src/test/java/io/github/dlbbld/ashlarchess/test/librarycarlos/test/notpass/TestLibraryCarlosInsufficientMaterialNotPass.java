package io.github.dlbbld.ashlarchess.test.librarycarlos.test.notpass;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.github.bhlangonijr.chesslib.Board;

class TestLibraryCarlosInsufficientMaterialNotPass {

  @SuppressWarnings("static-method")
  @Test
  void testExceptedFalseButTrue() throws Exception {
    // KNvKN
    assertTrue(isInsufficientMaterial("8/8/4K3/8/1n6/8/5k1N/8 w - - 0 50"));

    // KNvKB, bishop light squares
    assertTrue(isInsufficientMaterial("8/k2b4/8/8/2K5/8/8/1N6 w - - 0 50"));

    // KNvKB, bishop dark squares
    assertTrue(isInsufficientMaterial("5b2/8/3k4/8/8/4K3/8/1N6 w - - 0 50"));

    // KBvKN, bishop light squares
    assertTrue(isInsufficientMaterial("1n6/8/8/2k1K3/8/8/8/5B2 w - - 0 50"));

    // KBvKN, bishop dark squares
    assertTrue(isInsufficientMaterial("1n6/8/8/8/4K3/1k6/1B6/8 w - - 0 50"));

  }

  private static boolean isInsufficientMaterial(String fen) {
    final Board board = new Board();
    board.loadFromFen(fen);
    return board.isInsufficientMaterial();
  }
}
