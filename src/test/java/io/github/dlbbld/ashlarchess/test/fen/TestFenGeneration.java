package io.github.dlbbld.ashlarchess.test.fen;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import io.github.dlbbld.ashlarchess.board.Board;
import io.github.dlbbld.ashlarchess.fen.constants.FenConstants;

class TestFenGeneration {

  @SuppressWarnings("static-method")
  @Test
  void test() {
    final Board board = new Board();

    // initial position
    check(FenConstants.FEN_INITIAL_STR, board);

    // some position - white to move
    board.movesStrict("Nc3", "Nc6");
    check("r1bqkbnr/pppppppp/2n5/8/8/2N5/PPPPPPPP/R1BQKBNR w KQkq - 2 2", board);

    // some position - black to move
    board.moveStrict("Nf3");
    check("r1bqkbnr/pppppppp/2n5/8/8/2N2N2/PPPPPPPP/R1BQKB1R b KQkq - 3 2", board);

    board.moveStrict("Nf6");

    // two square advance white
    board.movesStrict("e4");
    check("r1bqkb1r/pppppppp/2n2n2/8/4P3/2N2N2/PPPP1PPP/R1BQKB1R b KQkq e3 0 3", board);

    // two square advance black
    board.movesStrict("e5");
    check("r1bqkb1r/pppp1ppp/2n2n2/4p3/4P3/2N2N2/PPPP1PPP/R1BQKB1R w KQkq e6 0 4", board);

    // en passant target square does not stay
    board.movesStrict("a3");
    check("r1bqkb1r/pppp1ppp/2n2n2/4p3/4P3/P1N2N2/1PPP1PPP/R1BQKB1R b KQkq - 0 4", board);

    // white lost both castling rights
    board.movesStrict("a6", "Ke2");
    check("r1bqkb1r/1ppp1ppp/p1n2n2/4p3/4P3/P1N2N2/1PPPKPPP/R1BQ1B1R b kq - 1 5", board);

    // black lost both king side castling rights
    board.movesStrict("Rg8");
    check("r1bqkbr1/1ppp1ppp/p1n2n2/4p3/4P3/P1N2N2/1PPPKPPP/R1BQ1B1R w q - 2 6", board);

    // black lost queen side castling rights - so no more player has castling rights
    board.movesStrict("a4", "Rb8");
    check("1rbqkbr1/1ppp1ppp/p1n2n2/4p3/P3P3/2N2N2/1PPPKPPP/R1BQ1B1R w - - 1 7", board);

    // increase half-move clock to 20
    board.movesStrict("Ra2", "Ra8", "Ra3", "Ra7", "Rb3", "Ra8", "Rb4", "Rb8", "Rc4", "Ra8", "Rd4", "Rb8", "Rd5", "Ra8",
        "Rd6", "Rb8", "Rg1", "Ra8", "Rd5");
    check("r1bqkbr1/1ppp1ppp/p1n2n2/3Rp3/P3P3/2N2N2/1PPPKPPP/2BQ1BR1 b - - 20 16", board);

  }

  private static void check(String expected, Board board) {
    final String actual = board.getFen();
    assertEquals(expected, actual);
  }
}