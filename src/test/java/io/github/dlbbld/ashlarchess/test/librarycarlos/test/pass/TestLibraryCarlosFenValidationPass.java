package io.github.dlbbld.ashlarchess.test.librarycarlos.test.pass;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.github.bhlangonijr.chesslib.Board;

import io.github.dlbbld.ashlarchess.fen.constants.FenConstants;

class TestLibraryCarlosFenValidationPass {

  @SuppressWarnings("static-method")
  @Test
  void testExpectedSuccessAndSuccess() throws Exception {
    // initial position
    assertTrue(testIsValid(FenConstants.FEN_INITIAL_STR));

  }

  @SuppressWarnings("static-method")
  @Test
  void testExpectedFailureAndFailure() throws Exception {

    // position field contains invalid letter
    assertFalse(testIsValid("xnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1"));

    // en passant field contains invalid letter
    assertFalse(testIsValid("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq x 0 1"));

    // halfmove clock contains invalid letter
    // board.loadFromFen();
    assertFalse(testIsValid("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - x 1"));

    // move counter contains invalid letter
    // board.loadFromFen();
    assertFalse(testIsValid("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 x"));

  }

  private static boolean testIsValid(String fen) {
    final Board board = new Board();
    try {
      board.loadFromFen(fen);
      return true;
    } catch (@SuppressWarnings("unused") final Exception e) {
      return false;
    }
  }
}
