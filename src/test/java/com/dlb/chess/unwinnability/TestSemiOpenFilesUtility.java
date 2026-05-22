package com.dlb.chess.unwinnability;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.dlb.chess.board.Board;
import com.dlb.chess.board.enums.File;
import com.dlb.chess.board.enums.Side;
import com.dlb.chess.common.constants.EnumConstants;
import com.dlb.chess.fen.constants.FenConstants;

class TestSemiOpenFilesUtility implements EnumConstants {

  @SuppressWarnings("static-method")
  @Test
  void testFile() throws Exception {
    checkFile("4n1b1/k3P3/1bPP3r/2p4P/PP1p2P1/6p1/K2p1P2/8 w - - 0 50", FILE_A, WHITE, true);
    checkFile("4n1b1/k3P3/1bPP3r/2p4P/PP1p2P1/6p1/K2p1P2/8 w - - 0 50", FILE_B, WHITE, true);
    checkFile("4n1b1/k3P3/1bPP3r/2p4P/PP1p2P1/6p1/K2p1P2/8 w - - 0 50", FILE_C, WHITE, true);
    checkFile("4n1b1/k3P3/1bPP3r/2p4P/PP1p2P1/6p1/K2p1P2/8 w - - 0 50", FILE_D, WHITE, true);
    checkFile("4n1b1/k3P3/1bPP3r/2p4P/PP1p2P1/6p1/K2p1P2/8 w - - 0 50", FILE_E, WHITE, true);
    checkFile("4n1b1/k3P3/1bPP3r/2p4P/PP1p2P1/6p1/K2p1P2/8 w - - 0 50", FILE_F, WHITE, true);
    checkFile("4n1b1/k3P3/1bPP3r/2p4P/PP1p2P1/6p1/K2p1P2/8 w - - 0 50", FILE_G, WHITE, true);
    checkFile("4n1b1/k3P3/1bPP3r/2p4P/PP1p2P1/6p1/K2p1P2/8 w - - 0 50", FILE_H, WHITE, true);

    checkFile("8/7K/6Bp/p4Pp1/1pR2p2/1QpNp3/2Rp4/6k1 w - - 0 50", FILE_A, BLACK, true);
    checkFile("8/7K/6Bp/p4Pp1/1pR2p2/1QpNp3/2Rp4/6k1 w - - 0 50", FILE_B, BLACK, true);
    checkFile("8/7K/6Bp/p4Pp1/1pR2p2/1QpNp3/2Rp4/6k1 w - - 0 50", FILE_C, BLACK, true);
    checkFile("8/7K/6Bp/p4Pp1/1pR2p2/1QpNp3/2Rp4/6k1 w - - 0 50", FILE_D, BLACK, true);
    checkFile("8/7K/6Bp/p4Pp1/1pR2p2/1QpNp3/2Rp4/6k1 w - - 0 50", FILE_E, BLACK, true);
    checkFile("8/7K/6Bp/p4Pp1/1pR2p2/1QpNp3/2Rp4/6k1 w - - 0 50", FILE_F, BLACK, true);
    checkFile("8/7K/6Bp/p4Pp1/1pR2p2/1QpNp3/2Rp4/6k1 w - - 0 50", FILE_G, BLACK, true);
    checkFile("8/7K/6Bp/p4Pp1/1pR2p2/1QpNp3/2Rp4/6k1 w - - 0 50", FILE_H, BLACK, true);
  }

  private static void checkFile(String fen, File file, Side side, boolean expected) {
    final Board board = new Board(fen, false);

    assertEquals(expected, SemiOpenFilesUtility.calculateIsSemiOpenFile(board.getBitboardPosition(), file, side));

    if (expected) {
      assertEquals(true, SemiOpenFilesUtility.calculateHasSemiOpenFile(board.getBitboardPosition()));
    }
  }

  @SuppressWarnings("static-method")
  @Test
  void testPosition() throws Exception {
    checkPosition(FenConstants.FEN_INITIAL_STR, false);
    checkPosition("rnb1kbnr/p4p2/3pp1p1/q1p5/2QPP3/2P5/P4PP1/RNB1KBNR w KQkq - 0 50", false);
    checkPosition("8/1p1k4/1P1p1p2/p1pP1p1p/P1P2p1P/5P2/5P2/6K1 w - - 10 46", false);

    checkPosition("8/4k3/8/4P3/8/8/1K6/8 w - - 0 50", true);
    checkPosition("7n/4k2b/4p3/4P3/8/8/1K5P/8 w - - 0 50", true);
    checkPosition("1q5n/4k2b/4p2p/4P2p/1K6/1P6/7P/8 w - - 0 50", true);

    checkPosition(FenConstants.FEN_AFTER_E4_STR, false);
    checkPosition("1nbqkbn1/r1p4p/3p1ppr/pR2p3/8/P1N5/2PPPPPP/2BQKBNR b K - 0 50", false);
    checkPosition("1k6/8/8/5p2/8/5P2/1K6/8 b - - 0 50", false);

    checkPosition("8/1kpK4/8/8/8/8/8/7Q b - - 0 50", true);
    checkPosition("8/1k1K4/2P5/2p4p/2p5/8/8/7Q b - - 0 50", true);
    checkPosition("8/1k1K4/2P5/2p4p/2p4P/6p1/8/7Q b - - 0 50", true);
  }

  private static void checkPosition(String fen, boolean expected) {
    final Board board = new Board(fen, false);

    assertEquals(expected, SemiOpenFilesUtility.calculateHasSemiOpenFile(board.getBitboardPosition()));

  }
}
