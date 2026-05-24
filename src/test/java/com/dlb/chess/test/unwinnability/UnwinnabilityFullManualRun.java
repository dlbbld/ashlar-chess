package com.dlb.chess.test.unwinnability;

import com.dlb.chess.board.Board;
import com.dlb.chess.board.enums.Side;
import com.dlb.chess.pgn.LenientPgnParser;
import com.dlb.chess.pgn.PgnGame;
import com.dlb.chess.pgn.PgnUtility;
import com.dlb.chess.test.pgn.setup.PgnTestCaseCatalog;
import com.dlb.chess.test.pgntest.enums.PgnTest;
import com.dlb.chess.unwinnability.UnwinnableFullAnalyzer;

public class UnwinnabilityFullManualRun {

  public static void main(String[] args) {

    final var pgnName = "01_m1_white_to_move.pgn";

    final PgnTest pgnTest = PgnTestCaseCatalog.findPgnTestPgnNotListed(pgnName);
    final PgnGame pgnGame = LenientPgnParser.parse(pgnTest.getFolderPath(), pgnName);
    final Board board = PgnUtility.calculateBoard(pgnGame);

    System.out.println("White full: " + UnwinnableFullAnalyzer.unwinnableFull(board, Side.WHITE).verdict());

    System.out.println("Black full: " + UnwinnableFullAnalyzer.unwinnableFull(board, Side.BLACK).verdict());

  }

}
