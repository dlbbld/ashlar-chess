// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.test.pgn.export;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.eclipse.jdt.annotation.NonNull;
import org.junit.jupiter.api.Test;

import io.github.dlbbld.ashlarchess.board.Board;
import io.github.dlbbld.ashlarchess.pgn.PgnCreate;
import io.github.dlbbld.ashlarchess.pgn.PgnGame;
import io.github.dlbbld.ashlarchess.pgn.PgnUtility;
import io.github.dlbbld.ashlarchess.pgn.ResultTagValue;

/**
 * Verifies that {@link PgnCreate#createPgnGame(Board)} produces the minimal honest model - no STR fabrication - and
 * that the model round-trips through the parser back to the source board. STR fabrication is exercised separately in
 * {@code TestPgnExportBoardArchival} (the archival-mode path that does fill the Seven Tag Roster).
 */
class TestPgnExportBoard {

  @SuppressWarnings("static-method")
  @Test
  void test() {

    check(ResultTagValue.ONGOING);
    check(ResultTagValue.ONGOING, "e4");

    check(ResultTagValue.WHITE_WON, "e4", "e5", "Bc4", "Nc6", "Qh5", "h6", "Qxf7#");
    check(ResultTagValue.BLACK_WON, "f3", "e5", "g4", "Qh4#");

  }

  private static void check(ResultTagValue resultTagValue, String... sanArray) {

    final Board boardExpected = new Board();
    for (final String san : sanArray) {
      @SuppressWarnings("null") @NonNull final String sanIsNotNull = san;
      boardExpected.moveStrict(sanIsNotNull);
    }

    final PgnGame boardExpectedPgnGame = PgnCreate.createPgnGame(boardExpected);

    checkNoFabrication(resultTagValue, boardExpectedPgnGame);

    checkBoardReplay(boardExpected, boardExpectedPgnGame);
  }

  /**
   * The minimal honest model: no caller supplied tags and the board started from the initial position, so the tag list
   * is empty. The termination marker carries the board-state-derived result; the Result tag itself is not fabricated
   * (it would be added on the archival-write path, not into the parse-model).
   */
  private static void checkNoFabrication(ResultTagValue resultTagValue, PgnGame pgnGame) {
    assertTrue(pgnGame.tagList().isEmpty());
    assertEquals(resultTagValue, pgnGame.terminationMarker());
  }

  private static void checkBoardReplay(Board boardExpected, PgnGame boardExpectedPgnGame) {
    final Board boardActual = PgnUtility.calculateBoard(boardExpectedPgnGame);
    assertEquals(boardExpected, boardActual);
  }

}
