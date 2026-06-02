// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.test.unwinnability;

import io.github.dlbbld.ashlarchess.board.Board;
import io.github.dlbbld.ashlarchess.board.enums.Side;
import io.github.dlbbld.ashlarchess.unwinnability.UnwinnableQuickAnalyzer;

public class UnwinnabilityQuickManualRun {

  public static void main(String[] args) {

    // final String pgnName = "05_helpmate2_white_to_move.pgn";
    //
    // final PgnTest pgnTest = PgnTestCaseCatalog.findPgnTestPgnNotListed(pgnName);
    // final PgnGame pgnGame = LenientPgnParser.parse(pgnTest.getFolderPath(), pgnName);
    // final Board board = PgnUtility.calculateBoard(pgnGame);

    final Board board = new Board("4k3/8/8/8/8/8/8/R3K3 b - - 0 100");

    System.out.println("White quick: " + UnwinnableQuickAnalyzer.unwinnableQuick(board, Side.WHITE));

    System.out.println("Black quick: " + UnwinnableQuickAnalyzer.unwinnableQuick(board, Side.BLACK));

  }

}
