package io.github.dlbbld.ashlarchess.test.unwinnability;

import io.github.dlbbld.ashlarchess.board.Board;
import io.github.dlbbld.ashlarchess.board.enums.Side;
import io.github.dlbbld.ashlarchess.pgn.LenientPgnParser;
import io.github.dlbbld.ashlarchess.pgn.PgnGame;
import io.github.dlbbld.ashlarchess.pgn.PgnUtility;
import io.github.dlbbld.ashlarchess.test.pgn.setup.PgnTestCaseCatalog;
import io.github.dlbbld.ashlarchess.test.pgntest.enums.PgnTest;
import io.github.dlbbld.ashlarchess.unwinnability.UnwinnableFullAnalyzer;

public class UnwinnabilityFullManualRun {

  public static void main(String[] args) {

    final String pgnName = "01_m1_white_to_move.pgn";

    final PgnTest pgnTest = PgnTestCaseCatalog.findPgnTestPgnNotListed(pgnName);
    final PgnGame pgnGame = LenientPgnParser.parse(pgnTest.getFolderPath(), pgnName);
    final Board board = PgnUtility.calculateBoard(pgnGame);

    System.out.println("White full: " + UnwinnableFullAnalyzer.unwinnableFull(board, Side.WHITE).verdict());

    System.out.println("Black full: " + UnwinnableFullAnalyzer.unwinnableFull(board, Side.BLACK).verdict());

  }

}
