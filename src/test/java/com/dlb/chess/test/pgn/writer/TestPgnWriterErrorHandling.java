package com.dlb.chess.test.pgn.writer;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.nio.file.Path;

import com.dlb.chess.pgn.PgnGame;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.dlb.chess.board.Board;
import com.dlb.chess.common.Nulls;
import com.dlb.chess.common.exceptions.FileSystemAccessException;
import com.dlb.chess.pgn.PgnCreate;
import com.dlb.chess.pgn.PgnWriter;

class TestPgnWriterErrorHandling {

  @SuppressWarnings("static-method")
  @Test
  void testWritePgnPropagatesFileSystemAccessException(@TempDir Path tempFolder) {
    final Path filePath = Nulls.pathResolve(tempFolder, "missing/game.pgn");
    final PgnGame pgnGame = PgnCreate.createPgnGame(new Board());

    assertThrows(FileSystemAccessException.class, () -> PgnWriter.writePgn(pgnGame, filePath));
  }
}
