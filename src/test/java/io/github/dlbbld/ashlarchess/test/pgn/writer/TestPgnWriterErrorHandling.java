// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.test.pgn.writer;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import io.github.dlbbld.ashlarchess.board.Board;
import io.github.dlbbld.ashlarchess.common.Nulls;
import io.github.dlbbld.ashlarchess.common.exceptions.FileSystemAccessException;
import io.github.dlbbld.ashlarchess.pgn.PgnCreate;
import io.github.dlbbld.ashlarchess.pgn.PgnGame;
import io.github.dlbbld.ashlarchess.pgn.PgnWriter;

class TestPgnWriterErrorHandling {

  @SuppressWarnings("static-method")
  @Test
  void testWritePgnPropagatesFileSystemAccessException(@TempDir Path tempFolder) {
    final Path filePath = Nulls.pathResolve(tempFolder, "missing/game.pgn");
    final PgnGame pgnGame = PgnCreate.createPgnGame(new Board());

    assertThrows(FileSystemAccessException.class, () -> PgnWriter.writePgn(pgnGame, filePath));
  }
}
