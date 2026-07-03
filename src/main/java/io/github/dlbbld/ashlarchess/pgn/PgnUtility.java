// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.pgn;

import java.nio.file.Path;

import io.github.dlbbld.ashlarchess.board.Board;

public final class PgnUtility {

  private PgnUtility() {
  }

  /** Replays the moves of {@code pgnGame} on a fresh board and returns the resulting state. */
  public static Board toBoard(PgnGame pgnGame) {

    final Board board = new Board(pgnGame.startFen());

    for (final PgnMove move : pgnGame.moves()) {
      final String san = move.san();
      board.moveStrict(san);
    }

    return board;
  }

  public static Board toBoard(Path folderPath, String pgnName) {
    final PgnGame pgnGame = LenientPgnParser.parsePath(folderPath, pgnName);
    return toBoard(pgnGame);
  }

}
