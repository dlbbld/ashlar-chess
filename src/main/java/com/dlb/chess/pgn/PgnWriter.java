package com.dlb.chess.pgn;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import com.dlb.chess.board.Board;
import com.dlb.chess.common.Nulls;
import com.dlb.chess.common.exceptions.FileSystemAccessException;

/**
 * Serialises a {@link PgnGame} (or a {@link Board}) to a PGN file on disk. All overloads default to
 * {@link WriteMode#SEMANTIC} - honest preservation of what the parse model contains. Callers who need a PGN spec
 * section 8.1.1-conformant artifact pass {@link WriteMode#ARCHIVAL} explicitly.
 */
public class PgnWriter {

  // -------------------------------------------------------------------------------------------------
  // PgnGame entry points - semantic default, explicit-mode overloads
  // -------------------------------------------------------------------------------------------------

  public static void writePgn(PgnGame pgnGame, String pgnPath) {
    writePgn(pgnGame, Nulls.pathOf(pgnPath));
  }

  public static void writePgn(PgnGame pgnGame, String pgnPath, WriteMode writeMode) {
    writePgn(pgnGame, Nulls.pathOf(pgnPath), writeMode);
  }

  public static void writePgn(PgnGame pgnGame, Path folderPath, String pgnName) {
    writePgn(pgnGame, Nulls.pathResolve(folderPath, pgnName));
  }

  public static void writePgn(PgnGame pgnGame, Path folderPath, String pgnName, WriteMode writeMode) {
    writePgn(pgnGame, Nulls.pathResolve(folderPath, pgnName), writeMode);
  }

  public static void writePgn(PgnGame pgnGame, Path filePath) {
    writePgn(pgnGame, filePath, WriteMode.SEMANTIC);
  }

  public static void writePgn(PgnGame pgnGame, Path filePath, WriteMode writeMode) {
    final List<String> fileLines = PgnCreate.createPgnLines(pgnGame, writeMode);
    writeLinesReplacing(filePath, fileLines);
  }

  // -------------------------------------------------------------------------------------------------
  // Board entry points - semantic default, explicit-mode overloads
  // -------------------------------------------------------------------------------------------------

  public static void writePgn(Board board, List<Tag> tagList, Path folderPath, String pgnName) {
    writePgn(board, tagList, folderPath, pgnName, WriteMode.SEMANTIC);
  }

  public static void writePgn(Board board, List<Tag> tagList, Path folderPath, String pgnName, WriteMode writeMode) {
    final PgnGame pgnGame = PgnCreate.createPgnGame(board, tagList);
    writePgn(pgnGame, folderPath, pgnName, writeMode);
  }

  public static void writePgn(Board board, Path folderPath, String pgnName) {
    writePgn(board, folderPath, pgnName, WriteMode.SEMANTIC);
  }

  public static void writePgn(Board board, Path folderPath, String pgnName, WriteMode writeMode) {
    final PgnGame pgnGame = PgnCreate.createPgnGame(board);
    writePgn(pgnGame, folderPath, pgnName, writeMode);
  }

  // -------------------------------------------------------------------------------------------------
  // File I/O
  // -------------------------------------------------------------------------------------------------

  private static void writeLinesReplacing(Path filePath, List<String> lineList) {
    try {
      Files.deleteIfExists(filePath);
    } catch (final IOException ioe) {
      throw new FileSystemAccessException("Deleting existing file \"" + filePath + "\" failed.", ioe);
    }
    try (var writer = Files.newBufferedWriter(filePath, StandardCharsets.UTF_8)) {
      for (final String line : lineList) {
        writer.write(line);
        writer.write("\n");
      }
    } catch (final IOException ioe) {
      throw new FileSystemAccessException("Writing file \"" + filePath + "\" failed.", ioe);
    }
  }

}
