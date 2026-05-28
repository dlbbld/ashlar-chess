package io.github.dlbbld.ashlarchess.pgn;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.eclipse.jdt.annotation.NonNull;

import io.github.dlbbld.ashlarchess.common.exceptions.FileSystemAccessException;

/**
 * Reads a PGN file as a UTF-8 string. Used internally by {@link StrictPgnParser} and {@link LenientPgnParser} to
 * support their {@code Path}-based parse / validate overloads. Not part of the public API.
 */
final class PgnReader {

  private PgnReader() {
  }

  static String readPgn(Path filePath) {
    final File file = filePath.toFile();
    if (!file.exists()) {
      throw new FileSystemAccessException("File \"" + filePath + "\" was not found.");
    }
    if (!file.isFile()) {
      throw new FileSystemAccessException("\"" + filePath + "\" is not a file.");
    }
    try {
      @SuppressWarnings("null") @NonNull final String content = Files.readString(filePath, StandardCharsets.UTF_8);
      return content;
    } catch (final IOException ioe) {
      throw new FileSystemAccessException("Reading file \"" + filePath + "\" failed.", ioe);
    }
  }
}
