// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.test.librarycarlos.utility;

import java.nio.file.Path;

import com.github.bhlangonijr.chesslib.game.Game;
import com.github.bhlangonijr.chesslib.pgn.PgnHolder;

import io.github.dlbbld.ashlarchess.common.Nulls;
import io.github.dlbbld.ashlarchess.test.librarycarlos.NullsCarlos;

public class PgnParserUtilityLibraryCarlos {

  private PgnParserUtilityLibraryCarlos() {
  }

  public static Game parse(Path pgnFolderPath, String pgnName) {

    final Path pgnPath = Nulls.pathResolve(pgnFolderPath, pgnName);

    final PgnHolder pgnHolder = new PgnHolder(pgnPath.toAbsolutePath().toString());
    try {
      pgnHolder.loadPgn();
    } catch (final Exception e) {
      throw new RuntimeException("An error occurred loading the PGN with Carlos API", e);
    }

    if (pgnHolder.getGames().size() != 1) {
      throw new IllegalArgumentException("The PGN must contain exactly one game");
    }

    final Game game = NullsCarlos.getGame(pgnHolder, 0);
    try {
      game.loadMoveText();
    } catch (final Exception e) {
      throw new RuntimeException("An error occurred loading the PGN movetext with Carlos API", e);
    }

    return game;
  }

}
